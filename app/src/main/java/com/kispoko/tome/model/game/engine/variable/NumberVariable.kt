
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.program.Invocation
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.value.ValueNumber
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.game.engine.interpreter.Interpreter
import com.kispoko.tome.rts.sheet.*
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable



/**
 * Number Variable
 */
sealed class NumberVariableValue : SumModel, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<NumberVariableValue> =
            when (doc.case())
            {
                "number_literal"     -> NumberVariableLiteralValue.fromDocument(doc)
                "variable_id"        -> NumberVariableVariableValue.fromDocument(doc)
                "program_invocation" -> NumberVariableProgramValue.fromDocument(doc)
                "value_reference"    -> NumberVariableValueValue.fromDocument(doc)
                "summation_id"       -> NumberVariableSummationValue.fromDocument(doc)
                else                 -> effError<ValueError,NumberVariableValue>(
                                            UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(sheetContext : SheetContext) : Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    abstract fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>>

    abstract fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class NumberVariableLiteralValue(val value : Double)
            : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariableValue> = when (doc)
        {
            is DocNumber -> effValue(NumberVariableLiteralValue(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>> =
            effValue(Just(this.value))


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "literal")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({ this.value })

}


/**
 * Unknown Literal Value
 */
class NumberVariableUnknownLiteralValue() : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariableValue> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "unknown_literal_value" -> effValue<ValueError,NumberVariableValue>(
                                                NumberVariableUnknownLiteralValue())
                else                    -> effError<ValueError,NumberVariableValue>(
                                                UnexpectedValue("NumberVariableUnknownLiteralValue",
                                                                doc.text,
                                                                doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>> = effValue(Nothing())


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "literal")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ "unknown_literal_value" })

}


/**
 * Variable Value
 */
data class NumberVariableVariableValue(val variableId : VariableId)
            : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariableValue> =
                effApply(::NumberVariableVariableValue, VariableId.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) = setOf(variableId)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>> =
        SheetManager.sheetState(sheetContext.sheetId)
                .apply { it.numberVariableWithId(variableId) }
                .apply { it.variableValue().value(sheetContext) }



    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "variable")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.variableId.asSQLValue()


}


/**
 * Program Value
 */
data class NumberVariableProgramValue(val invocation : Invocation)
            : NumberVariableValue(), Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariableValue> =
            effApply(::NumberVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) = invocation.dependencies()


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>> =
        effApply(::Just, Interpreter.evaluateNumber(this.invocation, sheetContext))


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Comp(this, "program")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() = this.invocation.onLoad()

    override val id = this.invocation.id

    override val name = this.invocation.name

    override val modelObject : Model = this.invocation

}


/**
 * Program Value
 */
data class NumberVariableValueValue(val valueReference : ValueReference)
            : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariableValue> =
                effApply(::NumberVariableValueValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>>
    {
        fun numberValue(engine : Engine) : AppEff<ValueNumber> =
            engine.numberValue(valueReference, sheetContext)

        fun doubleValue(numberValue : ValueNumber) : AppEff<Maybe<Double>> =
            effValue(Just(numberValue.value()))

        return GameManager.engine(sheetContext.gameId)
                          .apply(::numberValue)
                          .apply(::doubleValue)
    }


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
        GameManager.engine(sheetContext.gameId)
                .apply { it.value(this.valueReference, sheetContext) }
                .apply { effValue<AppError,Set<Variable>>(it.variables()) }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "value")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.valueReference.asSQLValue()

}


/**
 * Summation Value
 */
data class NumberVariableSummationValue(val summationId : SummationId)
            : NumberVariableValue(), SQLSerializable
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariableValue> =
                effApply(::NumberVariableSummationValue, SummationId.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) : Set<VariableReference>
    {
        val deps = GameManager.engine(sheetContext.gameId)
                         .apply { it.summation(summationId) }
                         .apply { effValue<AppError,Set<VariableReference>>(it.dependencies()) }

        when (deps) {
            is Val -> return deps.value
            is Err -> ApplicationLog.error(deps.error)
        }

        return setOf()
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>>
            = GameManager.engine(sheetContext.gameId)
                    .apply { it.summation(summationId) }
                    .apply { effValue<AppError,Double>(it.value(sheetContext)) }
                    .apply { effValue<AppError,Maybe<Double>>(Just(it)) }


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "summation")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.summationId.asSQLValue()

}


