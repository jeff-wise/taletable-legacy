
package com.taletable.android.model.engine.summation


import com.taletable.android.app.AppEff
import com.taletable.android.app.AppEngineError
import com.taletable.android.app.ApplicationLog
import com.taletable.android.db.DB_SummationValue
import com.taletable.android.db.summationTable
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue2
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.engine.FormulaModifier
import com.taletable.android.model.engine.dice.*
import com.taletable.android.model.engine.summation.term.*
import com.taletable.android.model.engine.variable.VariableNamespace
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.engine.SummationIsNotDiceRoll
import com.taletable.android.rts.entity.sheet.SheetData
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.filterJust
import java.io.Serializable
import java.util.*



/**
 * Summation
 */
data class Summation(override val id : UUID,
                     val summationId : SummationId,
                     val summationName : SummationName,
                     val terms : MutableList<SummationTerm>)
                      : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(summationId : SummationId,
                summationName : SummationName,
                terms : List<SummationTerm>)
        : this(UUID.randomUUID(),
               summationId,
               summationName,
               terms.toMutableList())


    companion object : Factory<Summation>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Summation> = when (doc)
        {
            is DocDict ->
            {
                apply(::Summation,
                      // Summation Id
                      doc.at("summation_id") ap { SummationId.fromDocument(it) },
                      // Summation Name
                      doc.at("summation_name") ap { SummationName.fromDocument(it) },
                      // Terms
                      doc.list("terms") ap { docList ->
                          docList.map { SummationTerm.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "summation_id" to this.summationId().toDocument(),
        "summation_name" to this.summationName().toDocument(),
        "terms" to DocList(this.terms().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun summationId() : SummationId = this.summationId


    fun summationName() : SummationName = this.summationName


    fun summationNameString() : String = this.summationName.value


    fun terms() : List<SummationTerm> = this.terms


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SummationValue =
        RowValue2(summationTable,
                  PrimValue(this.summationId),
                  PrimValue(this.summationName))


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    fun dependencies(entityId : EntityId) : Set<VariableReference> =
        this.terms.fold(setOf(), {
            accSet, term -> accSet.plus(term.dependencies(entityId))
        })


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(entityId : EntityId) : Double =
            this.terms().map({it.value(entityId)}).filterJust().sum()


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun summary(entityId : EntityId) : List<TermSummary> =
            this.terms().mapNotNull { it.summary(entityId) }


    fun diceRoll(entityId : EntityId,
                 context : Maybe<VariableNamespace> = Nothing()) : AppEff<DiceRoll>
    {
        val quantities : MutableList<DiceQuantity> = mutableListOf()
        val modifiers  : MutableList<RollModifier> = mutableListOf()
        val formulaModifiers : MutableList<FormulaModifier> = mutableListOf()

        for (term in this.terms())
        {
            when (term)
            {
                // Add dice quantity
                is SummationTermDiceRoll ->
                {
                    val diceRoll = SheetData.diceRoll(term.diceRollReference(), entityId)
                    when (diceRoll) {
                        is effect.Val -> {
                            quantities.addAll(diceRoll.value.quantities())
                            modifiers.addAll(diceRoll.value.modifiers())
                        }
                        is Err -> ApplicationLog.error(diceRoll.error)
                    }
                }
                // Add dice modifier
                is SummationTermNumber ->
                {
                    val maybeModValue = SheetData.number(term.numberReference(), entityId, context)
                    when (maybeModValue)
                    {
                        is effect.Val -> {
                            val maybeTermName = term.termName() ap { Just(RollModifierName(it.value)) }
//                            val maybeTermName = if (termName != null)
//                                                    Just(RollModifierName(termName))
//                                                    else Nothing<RollModifierName>()
                            val modValue = maybeModValue.value
                            when (modValue) {
                                is Just -> modifiers.add(RollModifier(RollModifierValue(modValue.value),
                                                         maybeTermName))
                            }
                        }
                    }
                }
                is SummationTermConditional ->
                {
                    val maybeModValue = term.value(entityId, context)
                    when (maybeModValue)
                    {
                        is Just -> {
                            val maybeTermName = term.termName() ap { Just(RollModifierName(it.value)) }
                            modifiers.add(RollModifier(RollModifierValue(maybeModValue.value),
                                                     maybeTermName))
                        }
                    }
                }
                is SummationTermConditionalFunction ->
                {
                    val isTrueEff = SheetData.boolean(term.conditionalValueReference(), entityId)
                    when (isTrueEff)
                    {
                        is Val -> {
                            if (isTrueEff.value)
                            {
                                formulaModifiers.add(term.function())
                            }
                        }
                    }
                }
            }
        }

        return if (quantities.isEmpty()) {
            effError(AppEngineError(SummationIsNotDiceRoll(summationId())))
        }
        else {
            effValue(DiceRoll(quantities,
                              modifiers,
                              formulaModifiers,
                              Just(DiceRollName(this.summationNameString()))))
        }
    }


//    fun termWithId(termId : UUID) : SummationTerm?
//    {
//        this.terms().forEach {
//            if (it.id == termId)
//                return it
//        }
//
//        return null
//    }

}


/**
 * Summation Id
 */
data class SummationId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SummationId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationId> = when (doc)
        {
            is DocText -> effValue(SummationId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Summation Name
 */
data class SummationName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SummationName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationName> = when (doc)
        {
            is DocText -> effValue(SummationName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


