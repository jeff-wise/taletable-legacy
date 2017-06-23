
package com.kispoko.tome.model.game.engine.function


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Function
 */
data class Function(override val id : UUID,
                    val functionId : Prim<FunctionId>,
                    val label : Prim<FunctionLabel>,
                    val description : Prim<FunctionDescription>,
                    val typeSignature : Comp<FunctionTypeSignature>,
                    val variables : Coll<Tuple>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.functionId.name        = "function_id"
        this.label.name             = "function_id"
        this.description.name       = "description"
        this.typeSignature.name     = "type_signature"
        this.variables.name         = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(functionId : FunctionId,
                label : FunctionLabel,
                description : FunctionDescription,
                typeSignature : FunctionTypeSignature,
                variables : MutableList<Tuple>)
        : this(UUID.randomUUID(),
               Prim(functionId),
               Prim(label),
               Prim(description),
               Comp(typeSignature),
               Coll(variables))


    companion object : Factory<Function>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Function>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::Function,
                         // Function Id
                         doc.at("function_id") ap { FunctionId.fromDocument(it) },
                         // Label
                         doc.at("label") ap { FunctionLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { FunctionDescription.fromDocument(it) },
                         // Type Signature
                         doc.at("type_signature") ap { FunctionTypeSignature.fromDocument(it) },
                         // Tuples
                         doc.list("tuples") ap { docList ->
                             docList.mapMut { Tuple.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "function"

    override val modelObject = this

}


/**
 * Function Type Signature
 */
data class FunctionTypeSignature(override val id : UUID,
                                 val parameter1Type : Prim<EngineValueType>,
                                 val parameter2Type : Maybe<Prim<EngineValueType>>,
                                 val parameter3Type : Maybe<Prim<EngineValueType>>,
                                 val parameter4Type : Maybe<Prim<EngineValueType>>,
                                 val parameter5Type : Maybe<Prim<EngineValueType>>,
                                 val resultType : Prim<EngineValueType>,
                                 val arity : Prim<FunctionArity>)
                                 : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.parameter1Type.name = "parameter_1_type"

        when (this.parameter2Type) {
            is Just -> this.parameter2Type.value.name = "parameter_2_type"
        }

        when (this.parameter3Type) {
            is Just -> this.parameter3Type.value.name = "parameter_3_type"
        }

        when (this.parameter4Type) {
            is Just -> this.parameter4Type.value.name = "parameter_4_type"
        }

        when (this.parameter5Type) {
            is Just -> this.parameter5Type.value.name = "parameter_5_type"
        }

        this.resultType.name     = "result_type"
        this.arity.name          = "arity"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(parameter1Type : EngineValueType,
                parameter2Type : Maybe<EngineValueType>,
                parameter3Type : Maybe<EngineValueType>,
                parameter4Type : Maybe<EngineValueType>,
                parameter5Type : Maybe<EngineValueType>,
                resultType : EngineValueType,
                arity : FunctionArity)
        : this(UUID.randomUUID(),
               Prim(parameter1Type),
               maybeLiftPrim(parameter2Type),
               maybeLiftPrim(parameter3Type),
               maybeLiftPrim(parameter4Type),
               maybeLiftPrim(parameter5Type),
               Prim(resultType),
               Prim(arity))


    companion object : Factory<FunctionTypeSignature>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<FunctionTypeSignature> = when (doc)
        {
            is DocDict ->
            {
                effApply(::FunctionTypeSignature,
                         // Parameter 1 Type
                         doc.at("parameter_1_type") ap { EngineValueType.fromDocument(it) },
                         // Parameter 2 Type
                         split(doc.maybeAt("parameter_2_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 3 Type
                         split(doc.maybeAt("parameter_3_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 4 Type
                         split(doc.maybeAt("parameter_4_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 5 Type
                         split(doc.maybeAt("parameter_5_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Result Type
                         doc.at("result_type") ap { EngineValueType.fromDocument(it) },
                         // Arity
                         doc.at("arity") ap { FunctionArity.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "program_type_signature"

    override val modelObject = this

}


/**
 * Function Arity
 */
data class FunctionArity(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FunctionArity>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<FunctionArity> = when (doc)
        {
            is DocNumber -> effValue(FunctionArity(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Function Id
 */
data class FunctionId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FunctionId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<FunctionId> = when (doc)
        {
            is DocText -> effValue(FunctionId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Function Label
 */
data class FunctionLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FunctionLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<FunctionLabel> = when (doc)
        {
            is DocText -> effValue(FunctionLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Function Description
 */
data class FunctionDescription(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FunctionDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<FunctionDescription> = when (doc)
        {
            is DocText -> effValue(FunctionDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Tuple
 */
data class Tuple(override val id : UUID,
                 val parameter1 : Func<EngineValue>,
                 val parameter2 : Maybe<Func<EngineValue>>,
                 val parameter3 : Maybe<Func<EngineValue>>,
                 val parameter4 : Maybe<Func<EngineValue>>,
                 val parameter5 : Maybe<Func<EngineValue>>,
                 val result : Func<EngineValue>,
                 val arity : Prim<FunctionArity>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.parameter1.name                        = "parameter1"

        when (this.parameter2) {
            is Just -> this.parameter2.value.name   = "parameter2"
        }

        when (this.parameter3) {
            is Just -> this.parameter3.value.name   = "parameter3"
        }

        when (this.parameter4) {
            is Just -> this.parameter4.value.name   = "parameter4"
        }

        when (this.parameter5) {
            is Just -> this.parameter5.value.name   = "parameter5"
        }

        this.result.name                            = "result"

        this.arity.name                             = "arity"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(parameter1 : EngineValue,
                parameter2 : Maybe<EngineValue>,
                parameter3 : Maybe<EngineValue>,
                parameter4 : Maybe<EngineValue>,
                parameter5 : Maybe<EngineValue>,
                result : EngineValue,
                arity : FunctionArity)
        : this(UUID.randomUUID(),
               liftEngineValue(parameter1),
               liftMaybeEngineValue(parameter2),
               liftMaybeEngineValue(parameter3),
               liftMaybeEngineValue(parameter4),
               liftMaybeEngineValue(parameter5),
               liftEngineValue(result),
               Prim(arity))


    companion object : Factory<Tuple>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Tuple> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Tuple,
                         // Parameter 1
                         doc.at("parameter1") ap { EngineValue.fromDocument(it) },
                         // Parameter 2 Type
                         split(doc.maybeAt("parameter_2"),
                               effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                               { effApply(::Just, EngineValue.fromDocument(it)) }),
                         // Parameter 3 Type
                         split(doc.maybeAt("parameter_3"),
                               effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                               { effApply(::Just, EngineValue.fromDocument(it)) }),
                         // Parameter 4 Type
                         split(doc.maybeAt("parameter_4"),
                               effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                               { effApply(::Just, EngineValue.fromDocument(it)) }),
                         // Parameter 5 Type
                         split(doc.maybeAt("parameter_5"),
                               effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                               { effApply(::Just, EngineValue.fromDocument(it)) }),
                         // Result
                         doc.at("result") ap { EngineValue.fromDocument(it) },
                         // Arity
                         doc.at("arity") ap { FunctionArity.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "tupel"

    override val modelObject = this

}



fun liftEngineValue(engineValue : EngineValue) : Func<EngineValue> = when (engineValue)
    {
        is EngineNumberValue    -> Prim(engineValue, "number")
        is EngineTextValue      -> Prim(engineValue, "text")
        is EngineBooleanValue   -> Prim(engineValue, "boolean")
        is EngineDiceRollValue  -> Comp(engineValue, "dice_roll")
        is EngineTextListValue  -> Prim(engineValue, "text_list")
    }


@Suppress("UNCHECKED_CAST")
fun liftMaybeEngineValue(mEngineValue : Maybe<EngineValue>) : Maybe<Func<EngineValue>>
    = when (mEngineValue)
    {
        is Just ->
        {
            val engineValue = mEngineValue.value
            when (engineValue)
            {
                is EngineNumberValue    -> Just(Prim(engineValue, "number"))
                                            as Maybe<Func<EngineValue>>
                is EngineTextValue      -> Just(Prim(engineValue, "text"))
                                            as Maybe<Func<EngineValue>>
                is EngineBooleanValue   -> Just(Prim(engineValue, "boolean"))
                                            as Maybe<Func<EngineValue>>
                is EngineDiceRollValue  -> Just(Comp(engineValue, "dice_roll"))
                                            as Maybe<Func<EngineValue>>
                is EngineTextListValue  -> Just(Prim(engineValue, "text_list"))
                                            as Maybe<Func<EngineValue>>
            }
        }
        else  -> Nothing()
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


