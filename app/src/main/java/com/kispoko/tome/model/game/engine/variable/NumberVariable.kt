
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.program.Invocation
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.value.ValueNumber
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.game.engine.interpreter.Interpreter
import com.kispoko.tome.rts.sheet.*
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Number Variable
 */
sealed class NumberVariableValue : Serializable
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
                "summation"          -> NumberVariableSummationValue.fromDocument(doc)
                else                 -> effError<ValueError,NumberVariableValue>(
                                            UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    abstract fun value(sheetContext : SheetContext) : AppEff<Double>

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

    override fun value(sheetContext : SheetContext) : AppEff<Double> = effValue(this.value)

    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({ this.value })

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

    override fun dependencies() : Set<VariableReference> = setOf(variableId)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Double>
    {
        fun numberVariable(state : SheetState) : AppEff<NumberVariable> =
            state.numberVariableWithId(variableId)

        fun variableValue(numberVariable : NumberVariable) : AppEff<Double> =
            numberVariable.variableValue().value(sheetContext)

        return fromSheetEff(SheetManager.state(sheetContext.sheetId))
                .apply(::numberVariable)
                .apply(::variableValue)
    }


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


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

    override fun dependencies() : Set<VariableReference> = invocation.dependencies()


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Double> =
        Interpreter.evaluateNumber(this.invocation, sheetContext)


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


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

    override fun value(sheetContext : SheetContext) : AppEff<Double>
    {
        fun numberValue(engine : Engine) : AppEff<ValueNumber> =
            engine.numberValue(valueReference, sheetContext)

        fun doubleValue(numberValue : ValueNumber) : AppEff<Double> =
            effValue(numberValue.value())

        return GameManager.engine(sheetContext.gameId)
                          .apply(::numberValue)
                          .apply(::doubleValue)
    }


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
        GameManager.engine(sheetContext.gameId)
                .apply { it.value(this.valueReference, sheetContext) }
                .apply { effValue<AppError,Set<Variable>>(it.variables()) }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.valueReference.asSQLValue()

}


/**
 * Summation Value
 */
data class NumberVariableSummationValue(val summation : Summation)
            : NumberVariableValue(), Model
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariableValue> =
                effApply(::NumberVariableSummationValue, Summation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = summation.dependencies()


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Double>
            = summation.value(sheetContext)


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() = this.summation.onLoad()

    override val id = this.summation.id

    override val name = this.summation.name

    override val modelObject : Model = this.summation

}


fun liftNumberVariableValue(varValue : NumberVariableValue) : Func<NumberVariableValue>
    = when (varValue)
    {
        is NumberVariableLiteralValue   -> Prim(varValue, "literal")
        is NumberVariableProgramValue   -> Comp(varValue, "program")
        is NumberVariableVariableValue  -> Prim(varValue, "variable")
        is NumberVariableValueValue     -> Prim(varValue, "value")
        is NumberVariableSummationValue -> Comp(varValue, "summation")
    }


//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    // ** Initialize
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeNumberVariable()
//    {
//        // [1] Create reaction value (if program variable)
//        // --------------------------------------------------------------------------------------
//
//        if (this.kind.getValue() == Kind.PROGRAM) {
//            this.reactiveValue = new ReactiveValue<>(this.invocationValue.getValue(),
//                                                     VariableType.NUMBER);
//        }
//        else {
//            this.reactiveValue = null;
//        }
//    }
//
//
//    private void initializeFunctors()
//    {
//        // Name
//        this.name.setName("name");
//        this.name.setLabelId(R.string.variable_field_name_label);
//        this.name.setDescriptionId(R.string.variable_field_name_description);
//
//        // Label
//        this.label.setName("label");
//        this.label.setLabelId(R.string.variable_field_label_label);
//        this.label.setDescriptionId(R.string.variable_field_label_description);
//
//        // Description
//        this.description.setName("description");
//        this.description.setLabelId(R.string.variable_field_description_label);
//        this.description.setDescriptionId(R.string.variable_field_description_description);
//
//        // Is Namespaced
//        this.isNamespaced.setName("is_namespaced");
//        this.isNamespaced.setLabelId(R.string.variable_field_is_namespaced_label);
//        this.isNamespaced.setDescriptionId(R.string.variable_field_is_namespaced_description);
//
//        // Tags
//        this.tags.setName("tags");
//        this.tags.setLabelId(R.string.variable_field_tags_label);
//        this.tags.setDescriptionId(R.string.variable_field_tags_description);
//
//        // Kind
//        this.kind.setName("kind");
//        this.kind.setLabelId(R.string.variable_field_kind_label);
//        this.kind.setDescriptionId(R.string.variable_field_kind_description);
//
//        // Literal Value
//        this.literalValue.setName("kind_literal");
//        this.literalValue.setLabelId(R.string.number_variable_field_value_literal_label);
//        this.literalValue.setDescriptionId(R.string.number_variable_field_value_literal_description);
//        this.literalValue.caseOf("kind", "literal");
//
//        // Variable Value
//        this.variableReference.setName("kind_variable");
//        this.variableReference.setLabelId(R.string.number_variable_field_value_variable_label);
//        this.variableReference.setDescriptionId(R.string.number_variable_field_value_variable_description);
//        this.variableReference.caseOf("kind", "variable");
//
//        // Program Value
//        this.invocationValue.setName("kind_program");
//        this.invocationValue.setLabelId(R.string.number_variable_field_value_program_label);
//        this.invocationValue.setDescriptionId(R.string.number_variable_field_value_program_description);
//        this.invocationValue.caseOf("kind", "program");
//
//        // Value (From Value Set) Value
//        this.valueReference.setName("kind_value");
//        this.valueReference.setLabelId(R.string.number_variable_field_value_value_label);
//        this.valueReference.setDescriptionId(R.string.number_variable_field_value_value_description);
//        this.valueReference.caseOf("kind", "value");
//
//        // Summation Value
//        this.summation.setName("kind_summation");
//        this.summation.setLabelId(R.string.number_variable_field_value_summation_label);
//        this.summation.setDescriptionId(R.string.number_variable_field_value_summation_description);
//        this.summation.caseOf("kind", "summation");
//    }
//
//
//    // ** Variable State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Add any variables associated with the current value to the state.
//     */
//    private void addToState()
//    {
//        if (this.kind() != Kind.VALUE)
//            return;
//
//        Dictionary dictionary = SheetManagerOld.currentSheet().engine().dictionary();
//        dictionary.numberValue(this.valueReference()).addToState();
//    }
//
//
//    private void removeFromState()
//    {
//        if (this.kind() != Kind.VALUE)
//            return;
//
//        Dictionary dictionary = SheetManagerOld.currentSheet().engine().dictionary();
//        dictionary.numberValue(this.valueReference()).removeFromState();
//    }
//

