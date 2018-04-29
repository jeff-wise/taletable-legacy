
package com.kispoko.tome.model.game.engine.summation.term


import android.util.Log
import com.kispoko.tome.app.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.reference.*
import com.kispoko.tome.model.game.engine.FormulaModifier
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.booleanVariable
import com.kispoko.tome.rts.entity.numberVariable
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.rts.entity.variablesWithTag
import com.kispoko.tome.util.Util
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
                    : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SummationTerm> =
            when (doc.case())
            {
                "summation_term_number"             -> SummationTermNumber.fromDocument(doc)
                "summation_term_linear_combination" -> SummationTermLinearCombination.fromDocument(doc)
                "summation_term_dice_roll"          -> SummationTermDiceRoll.fromDocument(doc)
                "summation_term_conditional"        -> SummationTermConditional.fromDocument(doc)
                "summation_term_conditional_function" -> SummationTermConditionalFunction.fromDocument(doc)
                else                                -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun termName() : Maybe<TermName> = this.termName


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(entityId : EntityId): Set<VariableReference>


    abstract fun value(entityId : EntityId,
                       context : Maybe<VariableNamespace> = Nothing()) : Maybe<Double>


    abstract fun summary(entityId : EntityId) : TermSummary?

}


data class SummationTermNumber(val id : UUID,
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

    override fun dependencies(entityId : EntityId): Set<VariableReference> =
            this.numberReference().dependencies()


    override fun value(entityId : EntityId,
                       context : Maybe<VariableNamespace>) : Maybe<Double>
    {
        val numbers = SheetData.numbers(this.numberReference(), entityId, context)

        when (numbers)
        {
            is effect.Val -> return Just(numbers.value.filterJust().sum())
            is Err -> ApplicationLog.error(numbers.error)
        }

        return Nothing()
    }


    override fun summary(entityId : EntityId) : TermSummary?
    {
        val components = this.numberReference().components(entityId)

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
                    val value = SheetData.number(this.numberReference(), entityId)
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

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_SummationTermNumberValue =
//        RowValue2(summationTermNumberTable,
//                  MaybePrimValue(this.termName),
//                  SumValue(this.numberReference))

}


data class SummationTermLinearCombination(
                            val id : UUID,
                            override val termName : Maybe<TermName>,
                            val variableTag : VariableTag,
                            val valueRelation : Maybe<VariableRelation>,
                            val weightRelation : Maybe<VariableRelation>,
                            val filterRelation : Maybe<VariableRelation>,
                            val defaultValue : Maybe<NumberReference>)
                             : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(termName : Maybe<TermName>,
                variableTag : VariableTag,
                valueRelation : Maybe<VariableRelation>,
                weightRelation : Maybe<VariableRelation>,
                filterRelation : Maybe<VariableRelation>,
                defaultValue : Maybe<NumberReference>)
        : this(UUID.randomUUID(),
               termName,
               variableTag,
               valueRelation,
               weightRelation,
               filterRelation,
               defaultValue)


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict ->
            {
                apply(::SummationTermLinearCombination,
                      // Term Name
                      split(doc.maybeAt("term_name"),
                            effValue<ValueError,Maybe<TermName>>(Nothing()),
                            { apply(::Just, TermName.fromDocument(it)) }),
                      // Variable Tag
                      doc.at("variable_tag") ap { VariableTag.fromDocument(it) },
                      // Value Relation
                      split(doc.maybeAt("value_relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Weight Relation
                      split(doc.maybeAt("weight_relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Filter Relation
                      split(doc.maybeAt("filter_relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Default Value
                      split(doc.maybeAt("default_value"),
                            effValue<ValueError,Maybe<NumberReference>>(Nothing()),
                            { apply(::Just, NumberReference.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "variable_tag" to this.variableTag().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableTag() = this.variableTag


    fun valueRelation() = this.valueRelation


    fun weightRelation() = this.weightRelation


    fun filterRelation() : Maybe<VariableRelation> = this.filterRelation


    fun defaultValueReference() : Maybe<NumberReference> = this.defaultValue


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    fun relatedValueVariable(variable : Variable, entityId : EntityId) : AppEff<NumberVariable>
    {
        return when (this.valueRelation)
        {
            is Just ->
            {
                val valueVariableId = variable.relatedVariableId(this.valueRelation.value)
                when (valueVariableId) {
                    is Just -> numberVariable(valueVariableId.value, entityId)
                    else    -> effError<AppError,NumberVariable>(AppStateError(
                                    VariableDoesNotHaveRelation(variable.variableId(), this.valueRelation.value)))
                }
            }
            else ->
            {
                variable.numberVariable(entityId)
            }
        }
    }


    fun relatedValue(variable : Variable, entityId : EntityId) : AppEff<Double>
    {
        return when (this.valueRelation)
        {
            is Just ->
            {
                val valueVariableId = variable.relatedVariableId(this.valueRelation.value)
                when (valueVariableId)
                {
                    is Just ->
                    {
                        numberVariable(valueVariableId.value, entityId) ap { numberVar ->
                        numberVar.value(entityId)                       ap { mValue ->
                            when (mValue) {
                                is Just -> effValue(mValue.value)
                                else -> effError<AppError,Double>(AppStateError(
                                            VariableDoesNotHaveValue(variable.variableId())))
                            }
                        } }
                    }
                    else -> effError<AppError,Double>(AppStateError(
                                VariableDoesNotHaveRelation(variable.variableId(), this.valueRelation.value)))
                }
            }
            else ->
            {
                variable.numberVariable(entityId).apply { it.valueOrError(entityId) }
            }
        }

    }


    fun relatedWeight(variable : Variable, entityId : EntityId) : AppEff<Double>
    {
        val weightVariableId = this.weightRelation.apply { variable.relatedVariableId(it) }
        return when (weightVariableId) {
            is Just -> {
                numberVariable(weightVariableId.value, entityId) ap { numberVar ->
                numberVar.value(entityId)                        ap { mValue ->
                    when (mValue) {
                        is Just -> effValue<AppError,Double>(mValue.value)
                        else    -> effValue(1.0)
                    }
                } }
            }
            else -> effValue(1.0)
        }
    }


    fun relatedFilter(variable : Variable, entityId : EntityId) : AppEff<Boolean>
    {
        val filterVariableId = this.filterRelation.apply { variable.relatedVariableId(it) }
        return when (filterVariableId) {
            is Just -> booleanVariable(filterVariableId.value, entityId) ap { it.value()}
            else    -> effValue(true)
        }
    }


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference>
    {
        val referenceSet : MutableSet<VariableReference> = mutableSetOf()

        when (this.valueRelation) {
            is Just -> referenceSet.add(RelatedVariableSet(this.variableTag, this.valueRelation.value))
        }

        when (this.weightRelation) {
            is Just -> referenceSet.add(RelatedVariableSet(this.variableTag, this.weightRelation.value))
        }

        when (this.filterRelation) {
            is Just -> referenceSet.add(RelatedVariableSet(this.variableTag, this.filterRelation.value))
        }

        return referenceSet
    }



    override fun value(entityId : EntityId,
                       context : Maybe<VariableNamespace>) : Maybe<Double>
    {
        val variablesEff = variablesWithTag(this.variableTag(), entityId)

        when (variablesEff)
        {
            is Val ->
            {
                var sum = 0.0
                val variables = variablesEff.value

                variables.forEach { variable ->
                    this.relatedValue(variable, entityId)  apDo { value ->
                    this.relatedWeight(variable, entityId) apDo { weight ->
                    this.relatedFilter(variable, entityId) apDo { filter ->
                        if (filter) { sum += (value * weight) }
                    } } }
                }

                // Add default variable if not variables currently match tag
                if (sum == 0.0) {
                    when (this.defaultValue) {
                        is Just -> {
                            SheetData.number(this.defaultValue.value, entityId) apDo {
                                when (it) {
                                    is Just -> {
                                        sum += it.value
                                    }
                                }
                            }
                        }
                    }
                }

                return Just(sum)

            }
            is Err -> ApplicationLog.error(variablesEff.error)
        }

        return Nothing()
    }


    override fun summary(entityId : EntityId) : TermSummary?
    {
        val variablesEff = variablesWithTag(this.variableTag(), entityId)

        when (variablesEff)
        {
            is Val ->
            {
                val variables = variablesEff.value
                var components : MutableList<TermComponent> = mutableListOf()

                variables.forEach { variable ->

                    val x = this.relatedValueVariable(variable, entityId) ap { valueVariable ->
                    valueVariable.valueOrError(entityId)                  ap { value ->
                    this.relatedWeight(variable, entityId)                ap { weight ->
                    this.relatedFilter(variable, entityId)                ap { filter ->
                        if (filter)
                        {
                            val total = value * weight
                            val component = TermComponent(valueVariable.label().value,
                                                          Util.doubleString(total),
                                                          Util.doubleString(value),
                                                          Util.doubleString(weight))
                            effValue(component)
                        }
                        else
                        {
                            effError<AppError,TermComponent>(AppVoidError())

                        }
                    } } } }

                    when (x) {
                        is Val -> components.add(x.value)
                        is Err -> ApplicationLog.error(x.error)
                    }
                }

                // Add default variable if not variables currently match tag
                if (components.isEmpty()) {
                    when (this.defaultValue) {
                        is Just -> {
                            components.addAll(this.defaultValue.value.components(entityId))
                        }
                    }
                }

                return TermSummary(this.termName().toNullable()?.value, components, this)
            }
            is Err -> ApplicationLog.error(variablesEff.error)
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_SummationTermLinearCombinationValue =
//        RowValue5(summationTermLinearCombinationTable,
//                  MaybePrimValue(this.termName),
//                  PrimValue(this.variableTag),
//                  MaybePrimValue(this.valueRelation),
//                  MaybePrimValue(this.weightRelation),
//                  MaybePrimValue(this.filterRelation))

}


data class SummationTermDiceRoll(val id : UUID,
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

    override fun dependencies(entityId : EntityId): Set<VariableReference> =
            this.diceRollReference().dependencies(entityId)


    override fun value(entityId : EntityId, context : Maybe<VariableNamespace>) : Maybe<Double>
    {
        val diceRoll = SheetData.diceRoll(diceRollReference(), entityId)

        return when (diceRoll)
        {
            is effect.Val -> Just(diceRoll.value.roll().toDouble())
            is Err        -> {
                ApplicationLog.error(diceRoll.error)
                Nothing()
            }
        }
    }


    override fun summary(entityId : EntityId) : TermSummary?
    {
        val components = this.diceRollReference().components(entityId)

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
                    val value = SheetData.diceRoll(this.diceRollReference(), entityId)
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
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_SummationTermDiceRollValue =
//        RowValue2(summationTermDiceRollTable,
//                  MaybePrimValue(this.termName),
//                  SumValue(this.diceRollReference))

}


// ---------------------------------------------------------------------------------------------
// TERM > Conditional
// ---------------------------------------------------------------------------------------------

data class SummationTermConditional(val id : UUID,
                                    override val termName : Maybe<TermName>,
                                    val conditionalValueReference : BooleanReference,
                                    val trueValueReference : NumberReference,
                                    val falseValueReference : NumberReference)
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
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_SummationTermConditionalValue =
//        RowValue4(summationTermConditionalTable,
//                  MaybePrimValue(this.termName),
//                  SumValue(this.conditionalValueReference),
//                  SumValue(this.trueValueReference),
//                  SumValue(this.falseValueReference))
//

    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId): Set<VariableReference> =
        conditionalValueReference.dependencies()
            .plus(trueValueReference.dependencies())
            .plus(falseValueReference.dependencies())


    override fun value(entityId : EntityId,
                       context : Maybe<VariableNamespace>) : Maybe<Double>
    {
        val number = SheetData.boolean(conditionalValueReference(), entityId)
                        .apply { condition ->
                            if (condition) {
                                SheetData.number(trueValueReference(), entityId)
                            }
                            else {
                                SheetData.number(falseValueReference(), entityId)
                            }
                        }

        return when (number)
        {
            is Val -> number.value
            is Err -> {
                ApplicationLog.error(number.error)
                Nothing()
            }
        }
    }


    override fun summary(entityId : EntityId) : TermSummary?
    {
        val isTrueEff = SheetData.boolean(this.conditionalValueReference(), entityId)

        when (isTrueEff)
        {
            is effect.Val ->
            {
                val valueRef = if (isTrueEff.value) this.trueValueReference()
                                    else this.falseValueReference()

                val components = valueRef.components(entityId)

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
                            val value = SheetData.number(valueRef, entityId)
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


// ---------------------------------------------------------------------------------------------
// TERM > Conditional Function
// ---------------------------------------------------------------------------------------------

data class SummationTermConditionalFunction(
                        override val termName : Maybe<TermName>,
                        val conditionalValueReference : BooleanReference,
                        val function : FormulaModifier)
                          : SummationTerm(termName)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SummationTerm> = when (doc)
        {
            is DocDict ->
            {
                apply(::SummationTermConditionalFunction,
                      // Term Name
                      split(doc.maybeAt("term_name"),
                            effValue<ValueError,Maybe<TermName>>(Nothing()),
                            { effApply(::Just, TermName.fromDocument(it)) }),
                      // Condition
                      doc.at("condition") ap {
                          BooleanReference.fromDocument(it) },
                      // Formula Modifier
                      doc.at("formula_modifier") ap { FormulaModifier.fromDocument(it) })
            }

            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "condition" to this.conditionalValueReference().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun conditionalValueReference() : BooleanReference = this.conditionalValueReference


    fun function() : FormulaModifier = this.function


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference>
    {
        val deps = conditionalValueReference.dependencies()

        Log.d("****TERM", "deps: $deps")

        return deps
    }


    override fun value(entityId : EntityId,
                       context : Maybe<VariableNamespace>) = Nothing<Double>()


    override fun summary(entityId : EntityId) = null


    // -----------------------------------------------------------------------------------------
    // FUNCTION
    // -----------------------------------------------------------------------------------------
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


data class TermComponent(val name : String,
                         val value : String,
                         val baseValue : String? = null,
                         val multiplier : String? = null)

