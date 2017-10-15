
package com.kispoko.tome.model.game.engine.summation


import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Conj
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.dice.*
import com.kispoko.tome.model.game.engine.summation.term.SummationTerm
import com.kispoko.tome.model.game.engine.summation.term.SummationTermDiceRoll
import com.kispoko.tome.model.game.engine.summation.term.SummationTermNumber
import com.kispoko.tome.model.game.engine.summation.term.TermSummary
import com.kispoko.tome.model.game.engine.variable.VariableReference
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
                     val summationId : Prim<SummationId>,
                     val summationName : Prim<SummationName>,
                     val terms : Conj<SummationTerm>)
                      : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.summationId.name   = "summation_id"
        this.summationName.name = "summation_name"
        this.terms.name         = "terms"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(summationId : SummationId,
                summationName : SummationName,
                terms : MutableSet<SummationTerm>)
        : this(UUID.randomUUID(),
               Prim(summationId),
               Prim(summationName),
               Conj(terms))


    companion object : Factory<Summation>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Summation> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Summation,
                         // Summation Id
                         doc.at("summation_id") ap { SummationId.fromDocument(it) },
                         // Summation Name
                         doc.at("summation_name") ap { SummationName.fromDocument(it) },
                         // Terms
                         doc.list("terms") ap { docList ->
                             docList.mapSetMut { SummationTerm.fromDocument(it) }
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

    fun summationId() : SummationId = this.summationId.value

    fun summationName() : SummationName = this.summationName.value

    fun summationNameString() : String = this.summationName.value.value

    fun terms() : Set<SummationTerm> = this.terms.set


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "summation"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    fun dependencies() : Set<VariableReference> =
        this.terms.set.fold(setOf(), {
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


    fun diceRoll(sheetContext : SheetContext) : DiceRoll?
    {
        val quantities : MutableSet<DiceQuantity> = mutableSetOf()
        val modifiers  : MutableSet<RollModifier> = mutableSetOf()

        for (term in this.terms())
        {
            when (term)
            {
                // Add dice quantity
                is SummationTermDiceRoll ->
                {
                    val diceRoll = SheetData.diceRoll(sheetContext, term.diceRollReference())
                    when (diceRoll) {
                        is Val -> {
                            quantities.addAll(diceRoll.value.quantities())
                            modifiers.addAll(diceRoll.value.modifiers())
                        }
                        is Err -> ApplicationLog.error(diceRoll.error)
                    }
                }
                // Add dice modifier
                is SummationTermNumber ->
                {
                    val maybeModValue = SheetData.number(sheetContext, term.numberReference())
                    when (maybeModValue)
                    {
                        is Val -> {
                            val termName = term.termName()
                            val maybeTermName = if (termName != null)
                                                    Just(RollModifierName(termName))
                                                    else Nothing<RollModifierName>()
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
            null
        else
            DiceRoll(quantities, modifiers, Just(DiceRollName(this.summationNameString())))
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


