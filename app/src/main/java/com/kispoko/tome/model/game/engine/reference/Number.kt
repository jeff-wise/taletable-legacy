
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType



/**
 * Number Reference
 */
sealed class NumberReference : SumModel
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberReference> =
            when (doc.case)
            {
                "literal"  -> NumberReferenceLiteral.fromDocument(doc)
                "value"    -> NumberReferenceValue.fromDocument(doc)
                "variable" -> NumberReferenceVariable.fromDocument(doc)
                else       -> effError<ValueError,NumberReference>(
                                        UnknownCase(doc.case, doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(): Set<VariableReference> = setOf()
}


/**
 * Literal Number Reference
 */
data class NumberReferenceLiteral(val value : Double) : NumberReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberReference> = when (doc)
        {
            is DocNumber -> effValue(NumberReferenceLiteral(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "literal")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLReal({ this.value })

}



/**
 * Value Number Reference
 */
data class NumberReferenceValue(val valueReference : ValueReference)
            : NumberReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberReference> =
                effApply(::NumberReferenceValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "value")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.valueReference.asSQLValue()

}


/**
 * Variable Number Reference
 */
data class NumberReferenceVariable(val variableReference : VariableReference)
            : NumberReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberReference> =
                effApply(::NumberReferenceVariable, VariableReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "variable")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = setOf(variableReference)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.variableReference.asSQLValue()

}

//
//fun liftNumberReference(reference : NumberReference) : Func<NumberReference>
//    = when (reference)
//    {
//        is NumberReferenceLiteral  -> Prim(reference, "literal")
//        is NumberReferenceValue    -> Prim(reference, "value")
//        is NumberReferenceVariable -> Prim(reference, "variable")
//    }



//
//
//    // ** Name
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The term value name (if a literal value). Variable's should have their own name.
//     * @return The literal value name.
//     */
//    public String name()
//    {
//        switch (this.type())
//        {
//            case LITERAL:
//                return this.name.getValue();
//            case VARIABLE:
//                return this.variableReference().variable().variable().label();
//        }
//
//        return "";
//    }
//
//
//    // > Value
//    // ------------------------------------------------------------------------------------------
//
//
//    /**
//     * Get the value of the integer term. It is either a literal integer, or the value of an
//     * integer variable.
//     * @return The integer value.
//     * @throws VariableException
//     */
//    public Integer value()
//           throws VariableException
//    {
//        switch (this.type.getValue())
//        {
//            case LITERAL:
//                return this.integerValue.getValue();
//            case VARIABLE:
//                return this.variableValue(this.variableReference());
//        }
//
//        return null;
//    }
//
//
//    /**
//     * Get the name of the integer variable of the term. If the term is not a variable, then
//     * null is returned.
//     * @return The variable name, or null.
//     */
//    public VariableReference variableDependency()
//    {
//        switch (this.type.getValue())
//        {
//            case LITERAL:
//                return null;
//            case VARIABLE:
//                return this.variableReference();
//        }
//
//        return null;
//    }
//
//
//    // > Components
//    // ------------------------------------------------------------------------------------------
//
//    public List<Tuple2<String,Integer>> components()
//    {
//        switch (this.type())
//        {
//            case LITERAL:
//                List<Tuple2<String, Integer>> components = new ArrayList<>();
//                String name = this.name() != null ? this.name() : "";
//                components.add(new Tuple2<>(name, this.literal()));
//                return components;
//            case VARIABLE:
//                return this.variableSummaries();
//        }
//
//        return new ArrayList<>();
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    private Integer variableValue(VariableReference variableReference)
//            throws VariableException
//    {
//        Integer total = 0;
//
//        for (VariableUnion variableUnion : variableReference.variables())
//        {
//            // [1] If variable is not a number, throw exception
//            // ----------------------------------------------------------------------------------
//            if (variableUnion.type() != VariableType.NUMBER) {
//                throw VariableException.unexpectedVariableType(
//                        new UnexpectedVariableTypeError(variableUnion.variable().name(),
//                                                        VariableType.NUMBER,
//                                                        variableUnion.type()));
//            }
//
//            // [2] Add the variable's value to the sum.
//            // ----------------------------------------------------------------------------------
//
//
//            Integer variableValue = 0;
//            try {
//                variableValue = variableUnion.numberVariable().value();
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//
//            total += variableValue;
//        }
//
//        return total;
//    }
//
//
//    private List<Tuple2<String,Integer>> variableSummaries()
//    {
//        List<Tuple2<String,Integer>> summaries = new ArrayList<>();
//
//        for (VariableUnion variableUnion : this.variableReference().variables())
//        {
//            // [1] If variable is not a number, throw exception
//            // ----------------------------------------------------------------------------------
//            if (variableUnion.type() != VariableType.NUMBER) {
//                ApplicationFailure.variable(
//                        VariableException.unexpectedVariableType(
//                                new UnexpectedVariableTypeError(variableUnion.variable().name(),
//                                        VariableType.NUMBER,
//                                        variableUnion.type())));
//                continue;
//            }
//
//            NumberVariable variable = variableUnion.numberVariable();
//
//            try {
//                Integer value = variable.value();
//                summaries.add(new Tuple2<>(variable.label(), value));
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//        }
//
//        // > Sort the summaries
//        Collections.sort(summaries, new Comparator<Tuple2<String,Integer>>()
//        {
//            @Override
//            public int compare(Tuple2<String,Integer> summary1, Tuple2<String,Integer> summary2)
//            {
//                int summary1Value = summary1.getItem2();
//                int summary2Value = summary2.getItem2();
//
//                if (summary1Value > summary2Value)
//                    return -1;
//                if (summary2Value < summary1Value)
//                    return 1;
//                return 0;
//            }
//        });
//
//
//        return summaries;
//    }

