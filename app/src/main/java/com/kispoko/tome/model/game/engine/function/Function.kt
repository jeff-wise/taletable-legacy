
package com.kispoko.tome.model.game.engine.function


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueType
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Function
 */
data class Function(override val id : UUID,
                    val functionId : Func<FunctionId>,
                    val label : Func<FunctionLabel>,
                    val description : Func<FunctionDescription>,
                    val parameterTypes : Func<List<EngineValueType>>,
                    val resultType : Func<EngineValueType>,
                    val variables : Coll<Tuple>) : Model
{

    companion object : Factory<Function>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Function>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::Function,
                         // Model Id
                         valueResult(UUID.randomUUID()),
                         // Function Id
                         doc.at("function_id") ap {
                             effApply(::Prim, FunctionId.fromDocument(it))
                         },
                         // Label
                         split(doc.maybeAt("label"),
                               valueResult<Func<FunctionLabel>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<FunctionLabel>> =
                                       effApply(::Prim, FunctionLabel.fromDocument(d))),
                         // Description
                         split(doc.maybeAt("description"),
                               valueResult<Func<FunctionDescription>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<FunctionDescription>> =
                                       effApply(::Prim, FunctionDescription.fromDocument(d))),
                         // Parameter Types
                         doc.list("parameter_types") ap { docList ->
                             effApply(::Prim, docList.enumList<EngineValueType>())
                         },
                         // Result Type
                         effApply(::Prim, doc.enum<EngineValueType>("result_type")),
                         // Tuples
                         doc.list("tuples") ap { docList ->
                             effApply(::Coll, docList.map { Tuple.fromDocument(it) })
                         })
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Function Id
 */
data class FunctionId(val value : String)
{

    companion object : Factory<FunctionId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<FunctionId> = when (doc)
        {
            is DocText -> valueResult(FunctionId(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Function Label
 */
data class FunctionLabel(val value : String)
{

    companion object : Factory<FunctionLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<FunctionLabel> = when (doc)
        {
            is DocText -> valueResult(FunctionLabel(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Function Description
 */
data class FunctionDescription(val value : String)
{

    companion object : Factory<FunctionDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<FunctionDescription> = when (doc)
        {
            is DocText -> valueResult(FunctionDescription(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Tuple
 */
data class Tuple(override val id : UUID,
                 val parameters : Func<List<EngineValue>>,
                 val result : Func<EngineValue>) : Model
{

    companion object : Factory<Tuple>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Tuple> = when (doc)
        {
            is DocDict -> effApply(::Tuple,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Parameters
                                   doc.list("parameters") ap { docList ->
                                       effApply(::Prim,
                                           docList.map { EngineValue.fromDocument(it) })
                                   },
                                   // Result
                                   doc.at("result") ap {
                                       effApply(::Prim, EngineValue.fromDocument(it))
                                   })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}



//
//
//    // > Execute
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Execute the function. Returns a EngineValueUnion based on the provided parameters. If the
//     * function does not have a case for the given parameters, it returns null.
//     * @param parameters
//     * @return
//     */
//    public EngineValueUnion execute(List<EngineValueUnion> parameters)
//    {
//        EngineValueUnion result = this.functionMap.get(new Parameters(parameters));
//        return result;
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeFunctors()
//    {
//        // Name
//        this.name.setName("name");
//        this.name.setLabelId(R.string.function_field_name_label);
//        this.name.setDescriptionId(R.string.function_field_name_description);
//
//        // Label
//        this.label.setName("label");
//        this.label.setLabelId(R.string.function_field_label_label);
//        this.label.setDescriptionId(R.string.function_field_label_description);
//
//        // Description
//        this.description.setName("description");
//        this.description.setLabelId(R.string.function_field_description_label);
//        this.description.setDescriptionId(R.string.function_field_description_description);
//
//        // Parameter Types
//        this.parameterTypes.setName("parameter_types");
//        this.parameterTypes.setLabelId(R.string.function_field_parameter_types_label);
//        this.parameterTypes.setDescriptionId(R.string.function_field_parameter_types_description);
//
//        // Result Type
//        this.resultType.setName("result_type");
//        this.resultType.setLabelId(R.string.function_field_result_type_label);
//        this.resultType.setDescriptionId(R.string.function_field_result_type_description);
//
//        // Tuples
//        this.tuples.setName("tuples");
//        this.tuples.setLabelId(R.string.function_field_tuples_label);
//        this.tuples.setDescriptionId(R.string.function_field_tuples_description);
//    }
//
//
//    // > Validate
//    // ------------------------------------------------------------------------------------------
//
//    private void validate()
//            throws InvalidFunctionException
//    {
//        // [1] Make sure each tuple has the same number of parameters as specified by the function
//
//        int numberOfParameters = this.parameterTypes.getValue().length;
//        List<Tuple> tuples = this.tuples.getValue();
//
//        for (int i = 0; i < tuples.size(); i++)
//        {
//            int tupleSize = tuples.get(i).parameters().size();
//            if (tupleSize != numberOfParameters)
//                throw new InvalidFunctionException(
//                        new InvalidTupleLengthError(i, numberOfParameters, tupleSize),
//                        InvalidFunctionException.ErrorType.INVALID_TUPLE_LENGTH);
//        }
//    }
//
//
//    // > Index tuples
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Index the function's tuples for quick lookup when execute is called.
//     */
//    private void indexTuples()
//    {
//        this.functionMap = new HashMap<>();
//        for (Tuple tuple : this.tuples.getValue()) {
//            this.functionMap.put(new Parameters(tuple.parameters()), tuple.result());
//        }
//    }
//
//
//    // > Log Function
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Print the function to the debug log.
//     */
//    public void logFunction()
//    {
//        for (Map.Entry<Parameters,EngineValueUnion> e : this.functionMap.entrySet())
//        {
//            Parameters params = e.getKey();
//            EngineValueUnion res = e.getValue();
//
//            StringBuilder row = new StringBuilder();
//            for (EngineValueUnion param : params.getValues()) {
//                row.append(param.type().toString());
//                row.append("  ");
//                row.append(param.toString());
//                row.append("    ");
//            }
//
//            row.append("result: ");
//            row.append(res.toString());
//
//            Log.d("***FUNCTION", row.toString());
//        }
//    }
//
//
//    // PARAMETERS CLASS
//    // ------------------------------------------------------------------------------------------
//
//    private static class Parameters implements Serializable
//    {
//
//        // PROPERTIES
//        // ------------------------------------------------------------------------------------------
//
//        private List<EngineValueUnion> values;
//
//
//        // CONSTRUCTORS
//        // ------------------------------------------------------------------------------------------
//
//        public Parameters(List<EngineValueUnion> parameterValues)
//        {
//            values = parameterValues;
//        }
//
//
//        // API
//        // ------------------------------------------------------------------------------------------
//
//        // > Values
//        // ------------------------------------------------------------------------------------------
//
//        public List<EngineValueUnion> getValues()
//        {
//            return this.values;
//        }
//
//
//        // > Size
//        // ------------------------------------------------------------------------------------------
//
//        public int size()
//        {
//            return this.values.size();
//        }
//
//
//        // > HashCode / Equals
//        // ------------------------------------------------------------------------------------------
//
//        @Override
//        public boolean equals(Object o)
//        {
//
//            if (o == this) return true;
//
//            if (!(o instanceof Function.Parameters)) {
//                return false;
//            }
//
//            Parameters otherParameters = (Parameters) o;
//
//            if (otherParameters.size() != this.size())
//                return false;
//
//            for (int i = 0; i < this.size(); i++)
//            {
//                if (!this.values.get(i).equals(otherParameters.values.get(i)))
//                    return false;
//            }
//
//            return true;
//        }
//
//
//        @Override
//        public int hashCode()
//        {
//            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);
//
//            for (EngineValueUnion value : this.values)
//            {
//                switch (value.type())
//                {
//                    case STRING:
//                        hashCodeBuilder.append(value.stringValue());
//                        break;
//                    case INTEGER:
//                        hashCodeBuilder.append(value.integerValue());
//                        break;
//                    case BOOLEAN:
//                        hashCodeBuilder.append(value.booleanValue());
//                        break;
//                }
//            }
//
//            return hashCodeBuilder.toHashCode();
//        }
//
//    }


