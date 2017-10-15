
package com.kispoko.tome.model.game.engine.summation.term


import android.util.Log
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.reference.*
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetData
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Summation Term
 */
sealed class SummationTerm(open val termName : Maybe<Prim<TermName>>)
                    : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationTerm> =
            when (doc.case())
            {
                "summation_term_number"      -> SummationTermNumber.fromDocument(doc)
                "summation_term_dice_roll"   -> SummationTermDiceRoll.fromDocument(doc)
                "summation_term_conditional" -> SummationTermConditional.fromDocument(doc)
                else                         -> {
                    Log.d("***TERM", "case: " + doc.case())
                    effError<ValueError,SummationTerm>(
                            UnknownCase(doc.case(), doc.path))
                }
            }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun maybeTermName() : Maybe<TermName> = _getMaybePrim(this.termName)


    fun termName() : String? = getMaybePrim(this.termName)?.value


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(): Set<VariableReference>


    abstract fun value(sheetContext : SheetContext) : Maybe<Double>


    abstract fun summary(sheetContext : SheetContext) : TermSummary?

}


data class SummationTermNumber(override val id : UUID,
                               override val termName : Maybe<Prim<TermName>>,
                               val numberReference : Sum<NumberReference>)
                                : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(termName : Maybe<TermName>,
                numberReference : NumberReference)
        : this(UUID.randomUUID(),
               maybeLiftPrim(termName),
               Sum(numberReference))


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply(::SummationTermNumber,
                                   // Term Name
                                   split(doc.maybeAt("term_name"),
                                         effValue<ValueError,Maybe<TermName>>(Nothing()),
                                         { effApply(::Just, TermName.fromDocument(it)) }),
                                   // Value
                                   doc.at("value") ap { NumberReference.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value" to this.numberReference().toDocument()
    ))
    .maybeMerge(this.maybeTermName().apply {
        Just(Pair("term_name", it.toDocument() as SchemaDoc)) })
    .withCase("summation_term_number")


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun numberReference() = this.numberReference.value

    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.numberReference().dependencies()


    override fun value(sheetContext : SheetContext) : Maybe<Double>
    {
        val numbers = SheetData.numbers(sheetContext, this.numberReference())

        when (numbers)
        {
            is Val -> return Just(numbers.value.filterJust().sum())
            is Err -> ApplicationLog.error(numbers.error)
        }

        return Nothing()
    }


    override fun summary(sheetContext : SheetContext) : TermSummary?
    {
        val components = this.numberReference().components(sheetContext)

        if (components.isNotEmpty())
        {
            return TermSummary(this.termName(), components, this)
        }
        else
        {
            val termName = this.termName()
            if (termName != null)
            {
                val value = SheetData.number(sheetContext, this.numberReference())
                when (value)
                {
                    is Val -> {
                        return TermSummary(termName(),
                                           listOf(TermComponent(termName, value.value.toString())),
                                           this)
                    }
                    is Err -> ApplicationLog.error(value.error)
                }
            }
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "summation_term_number"

    override val modelObject = this

}


data class SummationTermDiceRoll(override val id : UUID,
                                 override val termName : Maybe<Prim<TermName>>,
                                 val diceRollReference : Sum<DiceRollReference>)
                                  : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(termName : Maybe<TermName>,
                diceRollReference : DiceRollReference)
        : this(UUID.randomUUID(),
               maybeLiftPrim(termName),
               Sum(diceRollReference))


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply(::SummationTermDiceRoll,
                                   // Term Name
                                   split(doc.maybeAt("term_name"),
                                         effValue<ValueError,Maybe<TermName>>(Nothing()),
                                         { effApply(::Just, TermName.fromDocument(it)) }),
                                   // Value
                                   doc.at("value") ap { DiceRollReference.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value" to this.diceRollReference().toDocument()
    ))
    .maybeMerge(this.maybeTermName().apply {
        Just(Pair("term_name", it.toDocument() as SchemaDoc)) })
    .withCase("summation_term_dice_roll")


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun diceRollReference() = this.diceRollReference.value


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> =
            this.diceRollReference().dependencies()


    override fun value(sheetContext : SheetContext) : Maybe<Double>
    {
        val diceRoll = SheetData.diceRoll(sheetContext, diceRollReference())

        when (diceRoll)
        {
            is Val -> return Just(diceRoll.value.roll().toDouble())
            is Err -> ApplicationLog.error(diceRoll.error)
        }

        return Nothing()
    }


    override fun summary(sheetContext : SheetContext) : TermSummary?
    {
        val components = this.diceRollReference().components(sheetContext)

        if (components.isNotEmpty())
        {
            return TermSummary(this.termName(), components, this)
        }
        else
        {
            val termName = this.termName()
            if (termName != null)
            {
                val value = SheetData.diceRoll(sheetContext, this.diceRollReference())
                when (value)
                {
                    is Val -> {
                        return TermSummary(termName(),
                                           listOf(TermComponent(termName, value.value.toString())),
                                           this)
                    }
                    is Err -> ApplicationLog.error(value.error)
                }
            }
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "summation_term_dice_roll"

    override val modelObject = this

}


data class SummationTermConditional(override val id : UUID,
                                    override val termName : Maybe<Prim<TermName>>,
                                    val conditionalValueReference : Sum<BooleanReference>,
                                    val trueValueReference : Sum<NumberReference>,
                                    val falseValueReference: Sum<NumberReference>)
                                      : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(termName : Maybe<TermName>,
                conditionalValueReference: BooleanReference,
                trueValueReference: NumberReference,
                falseValueReference: NumberReference)
        : this(UUID.randomUUID(),
               maybeLiftPrim(termName),
               Sum(conditionalValueReference),
               Sum(trueValueReference),
               Sum(falseValueReference))


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply(::SummationTermConditional,
                                   // Term Name
                                   split(doc.maybeAt("term_name"),
                                         effValue<ValueError,Maybe<TermName>>(Nothing()),
                                         { effApply(::Just, TermName.fromDocument(it)) }),
                                   // Condition
                                   doc.at("condition") ap { BooleanReference.fromDocument(it) },
                                   // When True
                                   doc.at("when_true") ap { NumberReference.fromDocument(it) },
                                   // When False
                                   doc.at("when_false") ap { NumberReference.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "condition" to this.conditionalValueReference().toDocument(),
        "when_true" to this.trueValueReference().toDocument(),
        "when_false" to this.falseValueReference().toDocument()
    ))
    .maybeMerge(this.maybeTermName().apply {
        Just(Pair("term_name", it.toDocument() as SchemaDoc)) })
    .withCase("summation_term_conditional")


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun conditionalValueReference() : BooleanReference = this.conditionalValueReference.value

    fun trueValueReference() : NumberReference = this.trueValueReference.value

    fun falseValueReference() : NumberReference = this.falseValueReference.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "summation_term_conditional"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> =
        conditionalValueReference.value.dependencies()
            .plus(trueValueReference.value.dependencies())
            .plus(falseValueReference.value.dependencies())


    override fun value(sheetContext : SheetContext) : Maybe<Double>
    {
        val number = SheetData.boolean(sheetContext, conditionalValueReference())
                        .apply { condition ->
                            if (condition)
                                SheetData.number(sheetContext, trueValueReference())
                            else
                                SheetData.number(sheetContext, falseValueReference())
                        }

        when (number)
        {
            is Val -> return number.value
            is Err -> ApplicationLog.error(number.error)
        }

        return Nothing()
    }


    override fun summary(sheetContext : SheetContext) : TermSummary?
    {
        val isTrueEff = SheetData.boolean(sheetContext, this.conditionalValueReference())

        when (isTrueEff)
        {
            is Val ->
            {
                val valueRef = if (isTrueEff.value) this.trueValueReference()
                                    else this.falseValueReference()

                val components = valueRef.components(sheetContext)

                if (components.isNotEmpty())
                {
                    return TermSummary(this.termName(), components, this)
                }
                else
                {
                    val termName = this.termName()
                    if (termName != null)
                    {
                        val value = SheetData.number(sheetContext, valueRef)
                        when (value)
                        {
                            is Val -> {
                                return TermSummary(termName(),
                                                   listOf(TermComponent(termName,
                                                                        value.value.toString())),
                                                   this)
                            }
                            is Err -> ApplicationLog.error(value.error)
                        }
                    }
                }


            }
            is Err -> ApplicationLog.error(isTrueEff.error)
        }

        return null
    }

}


/**
 * Term Name
 */
data class TermName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TermName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TermName> = when (doc)
        {
            is DocText -> effValue(TermName(doc.text))
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



data class TermSummary(val name : String?,
                       val components : List<TermComponent>,
                       val term : SummationTerm)


data class TermComponent(val name : String, val value : String)

