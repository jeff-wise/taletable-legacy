
package com.kispoko.tome.model.game.engine.summation.term


import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.SumValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.reference.*
import com.kispoko.tome.model.game.engine.variable.VariableNamespace
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetData
import effect.*
import effect.Err
import effect.Val
import maybe.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Summation Term
 */
sealed class SummationTerm(open val termName : Maybe<TermName>)
                    : ToDocument, ProdType, Serializable
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
                else                         -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun termName() : Maybe<TermName> = this.termName


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(sheetContext : SheetContext): Set<VariableReference>


    abstract fun value(sheetContext : SheetContext,
                       context : Maybe<VariableNamespace> = Nothing()) : Maybe<Double>


    abstract fun summary(sheetContext : SheetContext) : TermSummary?

}


data class SummationTermNumber(override val id : UUID,
                               override val termName : Maybe<TermName>,
                               val numberReference : NumberReference)
                                : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(termName : Maybe<TermName>,
                numberReference : NumberReference)
        : this(UUID.randomUUID(),
               termName,
               numberReference)


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> apply(::SummationTermNumber,
                                // Term Name
                                split(doc.maybeAt("term_name"),
                                      effValue<ValueError,Maybe<TermName>>(Nothing()),
                                      { apply(::Just, TermName.fromDocument(it)) }),
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
    .maybeMerge(this.termName.apply {
        Just(Pair("term_name", it.toDocument() as SchemaDoc)) })
    .withCase("summation_term_number")


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun numberReference() = this.numberReference


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext): Set<VariableReference> = this.numberReference().dependencies()


    override fun value(sheetContext : SheetContext,
                       context : Maybe<VariableNamespace>) : Maybe<Double>
    {
        val numbers = SheetData.numbers(sheetContext, this.numberReference(), context)

        when (numbers)
        {
            is effect.Val -> return Just(numbers.value.filterJust().sum())
            is Err -> ApplicationLog.error(numbers.error)
        }

        return Nothing()
    }


    override fun summary(sheetContext : SheetContext) : TermSummary?
    {
        val components = this.numberReference().components(sheetContext)

        if (components.isNotEmpty())
        {
            return TermSummary(this.termName().toNullable()?.value, components, this)
        }
        else
        {
            val termName = this.termName()
            when (termName)
            {
                is Just ->
                {
                    val value = SheetData.number(sheetContext, this.numberReference())
                    when (value)
                    {
                        is effect.Val -> {
                            return TermSummary(termName().toNullable()?.value,
                                               listOf(TermComponent(termName.value.value, value.value.toString())),
                                               this)
                        }
                        is Err -> ApplicationLog.error(value.error)
                    }
                }
            }
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SummationTermNumberValue =
        RowValue2(summationTermNumberTable,
                  MaybePrimValue(this.termName),
                  SumValue(this.numberReference))

}


data class SummationTermDiceRoll(override val id : UUID,
                                 override val termName : Maybe<TermName>,
                                 val diceRollReference : DiceRollReference)
                                  : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(termName : Maybe<TermName>,
                diceRollReference : DiceRollReference)
        : this(UUID.randomUUID(),
               termName,
               diceRollReference)


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> apply(::SummationTermDiceRoll,
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
    .maybeMerge(this.termName.apply {
        Just(Pair("term_name", it.toDocument() as SchemaDoc)) })
    .withCase("summation_term_dice_roll")


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun diceRollReference() = this.diceRollReference


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext): Set<VariableReference> =
            this.diceRollReference().dependencies(sheetContext)


    override fun value(sheetContext : SheetContext, context : Maybe<VariableNamespace>) : Maybe<Double>
    {
        val diceRoll = SheetData.diceRoll(sheetContext, diceRollReference())

        when (diceRoll)
        {
            is effect.Val -> return Just(diceRoll.value.roll().toDouble())
            is Err -> ApplicationLog.error(diceRoll.error)
        }

        return Nothing()
    }


    override fun summary(sheetContext : SheetContext) : TermSummary?
    {
        val components = this.diceRollReference().components(sheetContext)

        if (components.isNotEmpty())
        {
            return TermSummary(this.termName.toNullable()?.value, components, this)
        }
        else
        {
            val termName = this.termName()
            when (termName)
            {
                is Just ->
                {
                    val value = SheetData.diceRoll(sheetContext, this.diceRollReference())
                    when (value)
                    {
                        is effect.Val -> {
                            return TermSummary(termName().toNullable()?.value,
                                               listOf(TermComponent(termName.value.value, value.value.toString())),
                                               this)
                        }
                        is Err -> ApplicationLog.error(value.error)
                    }
                }

            }
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SummationTermDiceRollValue =
        RowValue2(summationTermDiceRollTable,
                  MaybePrimValue(this.termName),
                  SumValue(this.diceRollReference))

}


data class SummationTermConditional(override val id : UUID,
                                    override val termName : Maybe<TermName>,
                                    val conditionalValueReference : BooleanReference,
                                    val trueValueReference : NumberReference,
                                    val falseValueReference: NumberReference)
                                      : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(termName : Maybe<TermName>,
                conditionalValueReference : BooleanReference,
                trueValueReference : NumberReference,
                falseValueReference : NumberReference)
        : this(UUID.randomUUID(),
               termName,
               conditionalValueReference,
               trueValueReference,
               falseValueReference)


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationTerm> = when (doc)
        {
            is DocDict ->
            {
                apply(::SummationTermConditional,
                      // Term Name
                      split(doc.maybeAt("term_name"),
                            effValue<ValueError,Maybe<TermName>>(Nothing()),
                            { effApply(::Just, TermName.fromDocument(it)) }),
                      // Condition
                      doc.at("condition") ap {
                          BooleanReference.fromDocument(it) },
                      // When True
                      doc.at("when_true") ap { NumberReference.fromDocument(it) },
                      // When False
                      doc.at("when_false") ap { NumberReference.fromDocument(it) })
            }

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
    .maybeMerge(this.termName.apply {
        Just(Pair("term_name", it.toDocument() as SchemaDoc)) })
    .withCase("summation_term_conditional")


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun conditionalValueReference() : BooleanReference = this.conditionalValueReference


    fun trueValueReference() : NumberReference = this.trueValueReference


    fun falseValueReference() : NumberReference = this.falseValueReference


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SummationTermConditionalValue =
        RowValue4(summationTermConditionalTable,
                  MaybePrimValue(this.termName),
                  SumValue(this.conditionalValueReference),
                  SumValue(this.trueValueReference),
                  SumValue(this.falseValueReference))


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext): Set<VariableReference> =
        conditionalValueReference.dependencies()
            .plus(trueValueReference.dependencies())
            .plus(falseValueReference.dependencies())


    override fun value(sheetContext : SheetContext,
                       context : Maybe<VariableNamespace>) : Maybe<Double>
    {
        val number = SheetData.boolean(sheetContext, conditionalValueReference())
                        .apply { condition ->
                            if (condition) {
                                SheetData.number(sheetContext, trueValueReference())
                            }
                            else {
                                SheetData.number(sheetContext, falseValueReference())
                            }
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
            is effect.Val ->
            {
                val valueRef = if (isTrueEff.value) this.trueValueReference()
                                    else this.falseValueReference()

                val components = valueRef.components(sheetContext)

                if (components.isNotEmpty())
                {
                    return TermSummary(this.termName().toNullable()?.value, components, this)
                }
                else
                {
                    val termName = this.termName()
                    when (termName)
                    {
                        is Just ->
                        {
                            val value = SheetData.number(sheetContext, valueRef)
                            when (value)
                            {
                                is effect.Val -> {
                                    return TermSummary(termName().toNullable()?.value,
                                                       listOf(TermComponent(termName.value.value,
                                                                            value.value.toString())),
                                                       this)
                                }
                                is Err -> ApplicationLog.error(value.error)
                            }
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

