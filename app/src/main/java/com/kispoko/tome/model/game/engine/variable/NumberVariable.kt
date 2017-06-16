
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.program.Invocation
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.value.ValueNumber
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.game.fromEngineEff
import com.kispoko.tome.rts.game.fromGameEff
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetState
import com.kispoko.tome.rts.sheet.fromSheetEff
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
            when (doc.case)
            {
                "literal"   -> NumberVariableLiteralValue.fromDocument(doc)
                "variable"  -> NumberVariableVariableValue.fromDocument(doc)
                "program"   -> NumberVariableProgramValue.fromDocument(doc)
                "value"     -> NumberVariableValueValue.fromDocument(doc)
                "summation" -> NumberVariableSummationValue.fromDocument(doc)
                else        -> effError<ValueError,NumberVariableValue>(
                                    UnknownCase(doc.case, doc.path))
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

}


/**
 * Literal Value
 */
data class NumberVariableLiteralValue(val value : Double) : NumberVariableValue()
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

}


/**
 * Variable Value
 */
data class NumberVariableVariableValue(val variableId : VariableId) : NumberVariableValue()
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

}


/**
 * Program Value
 */
data class NumberVariableProgramValue(val invocation : Invocation) : NumberVariableValue()
{

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

    override fun value(sheetContext: SheetContext): AppEff<Double> {
        TODO("not implemented")
    }

}


/**
 * Program Value
 */
data class NumberVariableValueValue(val valueReference : ValueReference) : NumberVariableValue()
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

    override fun value(sheetContext: SheetContext): AppEff<Double>
    {
        fun numberValue(engine : Engine) : AppEff<ValueNumber> =
            engine.numberValue(valueReference)

        fun doubleValue(numberValue : ValueNumber) : AppEff<Double> =
            effValue(numberValue.value())

        return GameManager.engine(sheetContext.gameId)
                          .apply(::numberValue)
                          .apply(::doubleValue)
    }

}


/**
 * Summation Value
 */
data class NumberVariableSummationValue(val summation : Summation) : NumberVariableValue()
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

    override fun value(sheetContext : SheetContext): AppEff<Double>
            = summation.value(sheetContext)

}


//
//
//    @Override
//    public List<VariableReference> dependencies()
//    {
//        List<VariableReference> variableDependencies = new ArrayList<>();
//
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                break;
//            case VARIABLE:
//                break;
//            case PROGRAM:
//                variableDependencies = this.invocation().variableDependencies();
//                break;
//            case VALUE:
//                break;
//            case SUMMATION:
//                variableDependencies = this.summation().variableDependencies();
//                break;
//            default:
//                ApplicationFailure.union(
//                        UnionException.unknownVariant(
//                                new UnknownVariantError(Kind.class.getName())));
//        }
//
//        return variableDependencies;
//    }
//
//
//    /**
//     * The variable's tags.
//     * @return The tag list.
//     */
//    @Override
//    public List<String> tags()
//    {
//        return Arrays.asList(this.tags.getValue());
//    }
//
//
//    /**
//     * Get the value string representation. If the value contains any dice rolls, then it appears
//     * as a formula, otherwise it is just an integer string.
//     * @return The value string.
//     * @throws NullVariableException
//     */
//    public String valueString()
//           throws NullVariableException
//    {
//        switch (this.kind())
//        {
//            case LITERAL:
//                return this.value().toString();
//            case VARIABLE:
//                return this.value().toString();
//            case PROGRAM:
//                return this.value().toString();
//            case VALUE:
//                return this.value().toString();
//            case SUMMATION:
//                return this.summation().valueString();
//            default:
//                ApplicationFailure.union(
//                        UnionException.unknownVariant(
//                                new UnknownVariantError(Kind.class.getName())));
//        }
//
//        return "";
//    }
//
//
//    @Override
//    public void initialize()
//    {
//        // [1] Add to state
//        // --------------------------------------------------------------------------------------
//        this.addToState();
//
//        // [2] Save original name and label values in case namespaces changes multiple times
//        // --------------------------------------------------------------------------------------
//        this.originalName  = name();
//        this.originalLabel = label();
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    // ** Kind
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the kind of number variable.
//     * @return The number variable kind.
//     */
//    public Kind kind()
//    {
//        return this.kind.getValue();
//    }
//
//
//    // ** Cases
//    // ------------------------------------------------------------------------------------------
//
//
//
//    // ** Value
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Set the number variable integer. value
//     * @param newValue The integer value.
//     */
//    public void setValue(Integer newValue)
//    {
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                this.literalValue.setValue(newValue);
//                this.onUpdate();
//                break;
//            case VARIABLE:
//                break;
//            case PROGRAM:
//                // Do Nothing?
//                //this.reactiveValue.setLiteralValue(newValue);
//                break;
//            case VALUE:
//                break;
//            case SUMMATION:
//                // Do Nothing?
//                break;
//        }
//    }
//
//
//    /**
//     * Get the number variable's integer value.
//     * @return The integer value.
//     */
//    public Integer value()
//           throws NullVariableException
//    {
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                return this.literalValue.getValue();
//            case VARIABLE:
//                return referencedVariableValue();
//            case PROGRAM:
//                return this.reactiveValue.value();
//            case VALUE:
//                Dictionary dictionary = SheetManagerOld.currentSheet().engine().dictionary();
//                NumberValue numberValue = dictionary.numberValue(this.valueReference());
//                return numberValue.value();
//            case SUMMATION:
//                return this.summation().value();
//        }
//
//        throw new NullVariableException();
//    }
//
//
//    private Integer referencedVariableValue()
//            throws NullVariableException
//    {
//        if (!State.hasVariable(this.variableReference())) {
//            ApplicationFailure.variable(
//                    VariableException.undefinedVariable(
//                            new UndefinedVariableError(this.variableReference())));
//            throw new NullVariableException();
//        }
//
//        VariableUnion variableUnion = State.variableWithName(this.variableReference());
//
//        // Variable is wrong type, log error, and return as null variable exception
//        if (variableUnion.type() != VariableType.NUMBER) {
//            ApplicationFailure.variable(
//                    VariableException.unexpectedVariableType(
//                            new UnexpectedVariableTypeError(this.variableReference(),
//                                                            VariableType.NUMBER,
//                                                            variableUnion.type())));
//            throw new NullVariableException();
//        }
//
//        return variableUnion.numberVariable().value();
//    }
//
//
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

