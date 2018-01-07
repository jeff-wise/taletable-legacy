
package com.kispoko.tome.model.game.engine.function


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEvalError
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.*
import com.kispoko.tome.rts.game.engine.interpreter.FunctionNotDefinedForParameters
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

    val tupleByParameters : MutableMap<FunctionParameters,Tuple> =
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


    override fun rowValue() : DB_FunctionValue =
        RowValue5(functionTable, PrimValue(this.functionId),
                                 PrimValue(this.label),
                                 PrimValue(this.description),
                                 ProdValue(this.typeSignature),
                                 CollValue(this.tuples))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun tupleWithParameters(parameters : FunctionParameters) : Tuple? =
            this.tupleByParameters[parameters]


    fun isPlatformFunction() : Boolean =
        platformFunctionNames.contains(this.functionId)


    fun value(parameters : FunctionParameters) : AppEff<EngineValue>
    {
        val tuple = this.tupleWithParameters(parameters)

        return if (tuple != null)
            effValue(tuple.result())
        else
            effError(AppEvalError(
                     FunctionNotDefinedForParameters(this.functionId(), parameters)))
    }


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


    override fun rowValue() : DB_FunctionTypeSignatureValue =
        RowValue6(functionTypeSignatureTable,
                  PrimValue(this.parameter1Type),
                  MaybePrimValue(this.parameter2Type),
                  MaybePrimValue(this.parameter3Type),
                  MaybePrimValue(this.parameter4Type),
                  MaybePrimValue(this.parameter5Type),
                  PrimValue(this.resultType))

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


    override fun rowValue() : DB_TupleValue =
        RowValue6(tupleTable,
                  SumValue(this.parameter1),
                  MaybeSumValue(this.parameter2),
                  MaybeSumValue(this.parameter3),
                  MaybeSumValue(this.parameter4),
                  MaybeSumValue(this.parameter5),
                  SumValue(this.result))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun parameters() : FunctionParameters =
            FunctionParameters(this.parameter1(), this.parameter2(), this.parameter3(),
                       this.parameter4(), this.parameter5())

}


data class FunctionParameters(val parameter1 : EngineValue,
                              val parameter2 : Maybe<EngineValue>,
                              val parameter3 : Maybe<EngineValue>,
                              val parameter4 : Maybe<EngineValue>,
                              val parameter5 : Maybe<EngineValue>) : Serializable
{

    fun atIndex(index : Int) : Maybe<EngineValue> =
        when (index)
        {
            1    -> Just(parameter1)
            2    -> parameter2
            3    -> parameter3
            4    -> parameter4
            5    -> parameter5
            else -> Nothing()
        }

}


