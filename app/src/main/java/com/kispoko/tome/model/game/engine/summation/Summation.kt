
package com.kispoko.tome.model.game.engine.summation


import android.util.Log
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.DB_Summation
import com.kispoko.tome.db.dbSummation
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.dice.*
import com.kispoko.tome.model.game.engine.summation.term.SummationTerm
import com.kispoko.tome.model.game.engine.summation.term.SummationTermDiceRoll
import com.kispoko.tome.model.game.engine.summation.term.SummationTermNumber
import com.kispoko.tome.model.game.engine.summation.term.TermSummary
import com.kispoko.tome.model.game.engine.variable.VariableNamespace
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.rts.game.engine.SummationIsNotDiceRoll
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetData
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
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


    override fun row() : DB_Summation =
            dbSummation(this.summationId, this.summationName, this.terms)


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    fun dependencies() : Set<VariableReference> =
        this.terms.fold(setOf(), {
            accSet, term -> accSet.plus(term.dependencies())
        })


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(sheetContext : SheetContext) : Double =
            this.terms().map({it.value(sheetContext)}).filterJust().sum()


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun summary(sheetContext : SheetContext) : List<TermSummary> =
            this.terms().mapNotNull { it.summary(sheetContext) }


    fun diceRoll(sheetContext : SheetContext,
                 context : Maybe<VariableNamespace> = Nothing()) : AppEff<DiceRoll>
    {
        val quantities : MutableList<DiceQuantity> = mutableListOf()
        val modifiers  : MutableList<RollModifier> = mutableListOf()

        for (term in this.terms())
        {
            when (term)
            {
                // Add dice quantity
                is SummationTermDiceRoll ->
                {
                    val diceRoll = SheetData.diceRoll(sheetContext, term.diceRollReference())
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
                    val maybeModValue = SheetData.number(sheetContext, term.numberReference(), context)
                    Log.d("***SUMMATION", "number term reference: ${term.numberReference()}")
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
            }
        }

        return if (quantities.isEmpty())
            effError(AppEngineError(SummationIsNotDiceRoll(summationId())))
        else
            effValue(DiceRoll(quantities, modifiers, Just(DiceRollName(this.summationNameString()))))
    }


    fun termWithId(termId : UUID) : SummationTerm?
    {
        this.terms().forEach {
            if (it.id == termId)
                return it
        }

        return null
    }

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


