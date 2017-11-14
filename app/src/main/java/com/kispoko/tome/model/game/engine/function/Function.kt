
package com.kispoko.tome.model.game.engine.function


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.functor.Val
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.*
import com.kispoko.tome.rts.game.engine.interpreter.Parameters
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
                    val functionId : FunctionId,
                    val label : FunctionLabel,
                    val description : FunctionDescription,
                    val typeSignature : FunctionTypeSignature,
                    val tuples : MutableList<Tuple>)
                     : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val tupleByParameters : MutableMap<Parameters,Tuple> =
                                        this.tuples().associateBy { it.parameters() }
                                                     .toMutableMap()


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(functionId : FunctionId,
                label : FunctionLabel,
                description : FunctionDescription,
                typeSignature : FunctionTypeSignature,
                tuples : List<Tuple>)
        : this(UUID.randomUUID(),
               functionId,
               label,
               description,
               typeSignature,
               tuples.toMutableList())


    companion object : Factory<Function>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Function> = when (doc)
        {
            is DocDict ->
            {
                apply(::Function,
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
                          docList.map { Tuple.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "function_id" to this.functionId().toDocument(),
        "label" to this.label().toDocument(),
        "description" to this.description().toDocument(),
        "type_signature" to this.typeSignature().toDocument(),
        "tuples" to DocList(this.tuples().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun functionId() : FunctionId = this.functionId


    fun label() : FunctionLabel = this.label


    fun labelString() : String = this.label.value


    fun description() : FunctionDescription = this.description


    fun descriptionString() : String = this.description.value


    fun typeSignature() : FunctionTypeSignature = this.typeSignature


    fun tuples() : List<Tuple> = this.tuples


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_Function = dbFunction(this.functionId,
                                                  this.label,
                                                  this.description,
                                                  this.typeSignature,
                                                  this.tuples)

    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun tupleWithParameters(parameters : Parameters) : Tuple? =
            this.tupleByParameters[parameters]

}


/**
 * Function Type Signature
 */
data class FunctionTypeSignature(override val id : UUID,
                                 val parameter1Type : EngineValueType,
                                 val parameter2Type : Maybe<EngineValueType>,
                                 val parameter3Type : Maybe<EngineValueType>,
                                 val parameter4Type : Maybe<EngineValueType>,
                                 val parameter5Type : Maybe<EngineValueType>,
                                 val resultType : EngineValueType)
                                  : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(parameter1Type : EngineValueType,
                parameter2Type : Maybe<EngineValueType>,
                parameter3Type : Maybe<EngineValueType>,
                parameter4Type : Maybe<EngineValueType>,
                parameter5Type : Maybe<EngineValueType>,
                resultType : EngineValueType)
        : this(UUID.randomUUID(),
               parameter1Type,
               parameter2Type,
               parameter3Type,
               parameter4Type,
               parameter5Type,
               resultType)


    companion object : Factory<FunctionTypeSignature>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<FunctionTypeSignature> = when (doc)
        {
            is DocDict ->
            {
                apply(::FunctionTypeSignature,
                      // Parameter 1 Type
                      doc.at("parameter1_type") ap { EngineValueType.fromDocument(it) },
                      // Parameter 2 Type
                      split(doc.maybeAt("parameter2_type"),
                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                            { apply(::Just, EngineValueType.fromDocument(it)) }),
                      // Parameter 3 Type
                      split(doc.maybeAt("parameter3_type"),
                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                            { apply(::Just, EngineValueType.fromDocument(it)) }),
                      // Parameter 4 Type
                      split(doc.maybeAt("parameter4_type"),
                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                            { apply(::Just, EngineValueType.fromDocument(it)) }),
                      // Parameter 5 Type
                      split(doc.maybeAt("parameter5_type"),
                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                            { apply(::Just, EngineValueType.fromDocument(it)) }),
                      // Result Type
                      doc.at("result_type") ap { EngineValueType.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "parameter1_type" to this.parameter1Type().toDocument(),
        "result_type" to this.resultType().toDocument()
        ))
        .maybeMerge(this.parameter2Type.apply {
            Just(Pair("parameter2_type", it.toDocument())) })
        .maybeMerge(this.parameter3Type.apply {
            Just(Pair("parameter3_type", it.toDocument())) })
        .maybeMerge(this.parameter4Type.apply {
            Just(Pair("parameter4_type", it.toDocument())) })
        .maybeMerge(this.parameter5Type.apply {
            Just(Pair("parameter5_type", it.toDocument())) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun parameter1Type() : EngineValueType = this.parameter1Type


    fun parameter2Type() : Maybe<EngineValueType> = this.parameter2Type


    fun parameter3Type() : Maybe<EngineValueType> = this.parameter3Type


    fun parameter4Type() : Maybe<EngineValueType> = this.parameter4Type


    fun parameter5Type() : Maybe<EngineValueType> = this.parameter5Type


    fun resultType() : EngineValueType = this.resultType


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_FunctionTypeSignature =
            dbFunctionTypeSignature(this.parameter1Type,
                                    this.parameter2Type,
                                    this.parameter3Type,
                                    this.parameter4Type,
                                    this.parameter5Type,
                                    this.resultType)

}


/**
 * Function Id
 */
data class FunctionId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FunctionId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<FunctionId> = when (doc)
        {
            is DocText -> effValue(FunctionId(doc.text))
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


/**
 * Function Label
 */
data class FunctionLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FunctionLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<FunctionLabel> = when (doc)
        {
            is DocText -> effValue(FunctionLabel(doc.text))
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


/**
 * Function Description
 */
data class FunctionDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FunctionDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<FunctionDescription> = when (doc)
        {
            is DocText -> effValue(FunctionDescription(doc.text))
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


/**
 * Tuple
 */
data class Tuple(override val id : UUID,
                 val parameter1 : EngineValue,
                 val parameter2 : Maybe<EngineValue>,
                 val parameter3 : Maybe<EngineValue>,
                 val parameter4 : Maybe<EngineValue>,
                 val parameter5 : Maybe<EngineValue>,
                 val result : EngineValue)
                  : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(parameter1 : EngineValue,
                parameter2 : Maybe<EngineValue>,
                parameter3 : Maybe<EngineValue>,
                parameter4 : Maybe<EngineValue>,
                parameter5 : Maybe<EngineValue>,
                result : EngineValue)
        : this(UUID.randomUUID(),
               parameter1,
               parameter2,
               parameter3,
               parameter4,
               parameter5,
               result)


    companion object : Factory<Tuple>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Tuple> = when (doc)
        {
            is DocDict ->
            {
                apply(::Tuple,
                      // Parameter 1
                      doc.at("parameter1") ap { EngineValue.fromDocument(it) },
                      // Parameter 2
                      split(doc.maybeAt("parameter2"),
                            effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                            { effApply(::Just, EngineValue.fromDocument(it)) }),
                      // Parameter 3
                      split(doc.maybeAt("parameter3"),
                            effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                            { effApply(::Just, EngineValue.fromDocument(it)) }),
                      // Parameter 4
                      split(doc.maybeAt("parameter4"),
                            effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                            { effApply(::Just, EngineValue.fromDocument(it)) }),
                      // Parameter 5
                      split(doc.maybeAt("parameter5"),
                            effValue<ValueError,Maybe<EngineValue>>(Nothing()),
                            { effApply(::Just, EngineValue.fromDocument(it)) }),
                      // Result
                      doc.at("result") ap { EngineValue.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "parameter1" to this.parameter1().toDocument(),
        "result" to this.result().toDocument()
        ))
        .maybeMerge(this.parameter2().apply {
            Just(Pair("parameter2", it.toDocument())) })
        .maybeMerge(this.parameter3().apply {
            Just(Pair("parameter3", it.toDocument())) })
        .maybeMerge(this.parameter4().apply {
            Just(Pair("parameter4", it.toDocument())) })
        .maybeMerge(this.parameter5().apply {
            Just(Pair("parameter5", it.toDocument())) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun parameter1() : EngineValue = this.parameter1


    fun parameter2() : Maybe<EngineValue> = this.parameter2


    fun parameter3() : Maybe<EngineValue> = this.parameter3


    fun parameter4() : Maybe<EngineValue> = this.parameter4


    fun parameter5() : Maybe<EngineValue> = this.parameter5


    fun result() : EngineValue = this.result


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_Tuple = dbTuple(this.parameter1,
                                            this.parameter2,
                                            this.parameter3,
                                            this.parameter4,
                                            this.parameter5,
                                            this.result)

    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun parameters() : Parameters =
            Parameters(this.parameter1(), this.parameter2(), this.parameter3(),
                       this.parameter4(), this.parameter5())

}

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


