
package com.kispoko.tome.model.engine.variable


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.engine.program.Invocation
import com.kispoko.tome.model.engine.summation.Summation
import com.kispoko.tome.model.engine.value.ValueReference
import effect.Err
import effect.effApply
import effect.effApply2
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Number Variable
 */
sealed class NumberVariableValue : Model
{

     companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<NumberVariableValue> = when (doc)
        {
            is DocDict -> when (doc.case())
            {
                "literal"   -> NumberVariableLiteralIntegerValue.fromDocument(doc)
                "variable"  -> NumberVariableVariableValue.fromDocument(doc)
                "program"   -> NumberVariableProgramValue.fromDocument(doc)
                "value"     -> NumberVariableValueValue.fromDocument(doc)
                "summation" -> NumberVariableSummationValue.fromDocument(doc)
                else        -> Err<ValueError, DocPath,NumberVariableValue>(
                                    UnknownCase(doc.case()), doc.path)
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

}


/**
 * Literal Value
 */
data class NumberVariableLiteralIntegerValue(
                            override val id : UUID,
                            val value : Func<Int>) : NumberVariableValue()
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<NumberVariableValue> = when (doc)
        {
            is DocDict -> effApply2(::NumberVariableLiteralIntegerValue,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Value
                                    effApply(::Prim, doc.int("value")))
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Variable Value
 */
data class NumberVariableVariableValue(
                            override val id : UUID,
                            val variableReference : Func<VariableReference>) : NumberVariableValue()
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<NumberVariableValue> = when (doc)
        {
            is DocDict -> effApply2(::NumberVariableVariableValue,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Variable Reference
                                    doc.at("reference") ap {
                                        effApply(::Comp, VariableReference.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Program Value
 */
data class NumberVariableProgramValue(
                            override val id : UUID,
                            val inovcation : Func<Invocation>) : NumberVariableValue()
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<NumberVariableValue> = when (doc)
        {
            is DocDict -> effApply2(::NumberVariableProgramValue,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Variable Reference
                                    doc.at("invocation") ap {
                                        effApply(::Comp, Invocation.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Program Value
 */
data class NumberVariableValueValue(
                            override val id : UUID,
                            val valueReference : Func<ValueReference>) : NumberVariableValue()
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<NumberVariableValue> = when (doc)
        {
            is DocDict -> effApply2(::NumberVariableValueValue,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Variable Reference
                                    doc.at("reference") ap {
                                        effApply(::Comp, ValueReference.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Summation Value
 */
data class NumberVariableSummationValue(
                            override val id : UUID,
                            val summation : Func<Summation>) : NumberVariableValue()
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<NumberVariableValue> = when (doc)
        {
            is DocDict -> effApply2(::NumberVariableSummationValue,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Variable Reference
                                    doc.at("summation") ap {
                                        effApply(::Comp, Summation.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

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

