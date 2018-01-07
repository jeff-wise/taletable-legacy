
package com.kispoko.tome.model.game.engine.program


import android.util.Log
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEvalError
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.reference.DataReference
import com.kispoko.tome.rts.game.engine.interpreter.ResultBindingDoesNotExist
import com.kispoko.tome.rts.sheet.SheetContext
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Program
 */
data class Program(override val id : UUID,
                   val programId : ProgramId,
                   val label : ProgramLabel,
                   val description : ProgramDescription,
                   val typeSignature : ProgramTypeSignature,
                   val statements : MutableList<Statement>,
                   val resultBindingName : StatementBindingName)
                    : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(programId : ProgramId,
                label : ProgramLabel,
                description : ProgramDescription,
                typeSignature : ProgramTypeSignature,
                statements : List<Statement>,
                resultBindingName : StatementBindingName)
        : this(UUID.randomUUID(),
               programId,
               label,
               description,
               typeSignature,
               statements.toMutableList(),
               resultBindingName)


    companion object : Factory<Program>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Program> = when (doc)
        {
            is DocDict ->
            {
                apply(::Program,
                      // Program Id
                      doc.at("program_id") ap { ProgramId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { ProgramLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { ProgramDescription.fromDocument(it) },
                      // Type Signature
                      doc.at("type_signature") ap { ProgramTypeSignature.fromDocument(it) },
                      // Statements
                      split(doc.maybeList("statements"),
                            effValue(listOf()),
                            { it.map { Statement.fromDocument(it) } }),
                      doc.at("result_binding_name") ap { StatementBindingName.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "program_id" to this.programId().toDocument(),
        "label" to this.label().toDocument(),
        "description" to this.description().toDocument(),
        "statements" to DocList(this.statements().map { it.toDocument() }),
        "result_binding_name" to this.resultBindingName().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun programId() : ProgramId = this.programId


    fun label() : ProgramLabel = this.label


    fun labelString() : String = this.label.value


    fun description() : ProgramDescription = this.description


    fun descriptionString() : String = this.description.value


    fun typeSignature() : ProgramTypeSignature = this.typeSignature


    fun statements() : List<Statement> = this.statements


    fun resultBindingName() : StatementBindingName = this.resultBindingName


    fun resultBindingNameString() : String = this.resultBindingName.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ProgramValue =
        RowValue6(programTable, PrimValue(this.programId),
                                PrimValue(this.label),
                                PrimValue(this.description),
                                PrimValue(this.typeSignature),
                                CollValue(this.statements),
                                PrimValue(this.resultBindingName))


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(parameters : ProgramParameterValues,
              sheetContext : SheetContext) : AppEff<EngineValue>
    {
        val bindings : MutableMap<String,EngineValue> = mutableMapOf()

        for (statement in this.statements())
        {
            val value = statement.value(parameters,
                                        bindings,
                                        this.programId(),
                                        sheetContext)
            when (value)
            {
                is Val ->
                {
                    val binding = value.value
                    bindings.put(statement.bindingNameString(), binding)
                }
                is Err -> {
                    return value
                }
            }
        }

        return note(bindings[this.resultBindingNameString()],
                    AppEvalError(ResultBindingDoesNotExist(this.resultBindingNameString(),
                                                           this.programId())))
    }


}


/**
 * Program Type Signature
 */
//data class ProgramTypeSignature(override val id : UUID,
//                                val parameterTypes : List<EngineValueType>)
//                                : ToDocument, SQLSerializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(parameterTypes : List<EngineValueType>)
//        : this(UUID.randomUUID(),
//               parameterTypes)
//
//
//    companion object : Factory<ProgramTypeSignature>
//    {
//        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramTypeSignature> = when (doc)
//        {
//            is DocDict ->
//            {
//                apply(::ProgramTypeSignature,
//                      // Parameter 1 Type
//                      doc.at("parameter1_type") ap { EngineValueType.fromDocument(it) },
//                      // Parameter 2 Type
//                      split(doc.maybeAt("parameter2_type"),
//                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
//                            { effApply(::Just, EngineValueType.fromDocument(it)) }),
//                      // Parameter 3 Type
//                      split(doc.maybeAt("parameter3_type"),
//                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
//                            { effApply(::Just, EngineValueType.fromDocument(it)) }),
//                      // Parameter 4 Type
//                      split(doc.maybeAt("parameter4_type"),
//                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
//                            { effApply(::Just, EngineValueType.fromDocument(it)) }),
//                      // Parameter 5 Type
//                      split(doc.maybeAt("parameter5_type"),
//                            effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
//                            { effApply(::Just, EngineValueType.fromDocument(it)) }),
//                      // Result Type
//                      doc.at("result_type") ap { EngineValueType.fromDocument(it) }
//                      )
//            }
//            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
//        }
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocDict(mapOf(
//            "parameter1_type" to this.parameter1Type().toDocument(),
//            "result_type" to this.resultType().toDocument()
//    ))
//    .maybeMerge(this.parameter2Type().apply {
//        Just(Pair("parameter2_type", it.toDocument())) })
//    .maybeMerge(this.parameter3Type().apply {
//        Just(Pair("parameter3_type", it.toDocument())) })
//    .maybeMerge(this.parameter4Type().apply {
//        Just(Pair("parameter4_type", it.toDocument())) })
//    .maybeMerge(this.parameter5Type().apply {
//        Just(Pair("parameter5_type", it.toDocument())) })
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun parameter1Type() : EngineValueType = this.parameter1Type
//
//
//    fun parameter2Type() : Maybe<EngineValueType> = this.parameter2Type
//
//
//    fun parameter3Type() : Maybe<EngineValueType> = this.parameter3Type
//
//
//    fun parameter4Type() : Maybe<EngineValueType> = this.parameter4Type
//
//
//    fun parameter5Type() : Maybe<EngineValueType> = this.parameter5Type
//
//
//    fun resultType() : EngineValueType = this.resultType
//
//
//    // -----------------------------------------------------------------------------------------
//    // MODEL
//    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_ProgramTypeSignatureValue =
//        RowValue6(programTypeSignatureTable,
//                  PrimValue(this.parameter1Type),
//                  MaybePrimValue(this.parameter2Type),
//                  MaybePrimValue(this.parameter3Type),
//                  MaybePrimValue(this.parameter4Type),
//                  MaybePrimValue(this.parameter5Type),
//                  PrimValue(this.resultType))
//
//}


/**
 * Program Id
 */
data class ProgramId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramId> = when (doc)

        {
            is DocText -> effValue(ProgramId(doc.text))
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
 * Program Label
 */
data class ProgramLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramLabel> = when (doc)
        {
            is DocText -> effValue(ProgramLabel(doc.text))
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
 * Program Description
 */
data class ProgramDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramDescription> = when (doc)
        {
            is DocText -> effValue(ProgramDescription(doc.text))
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
 * Program Parameter
 */
data class ProgramParameterIndex(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramParameterIndex>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramParameterIndex> = when (doc)
        {
            is DocNumber -> effValue(ProgramParameterIndex(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}


/**
 * Program Parameters
 */
data class ProgramParameters(val parameters : List<DataReference>)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------
//
//    fun atIndex(index : Int) : Maybe<EngineValue> =
//        if (index < parameters.size)
//            Just(parameters[index])
//        else
//            Nothing()
//

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


/**
 * Program Parameters
 */
data class ProgramTypeSignature(val parameterTypes : List<EngineValueType>,
                                val resultType : EngineValueType)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramTypeSignature>
    {

        // TODO do culebra example
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramTypeSignature> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProgramTypeSignature,
                      // Parameter Types
                      split(doc.maybeList("parameter_types"),
                            effValue(listOf()),
                            { it.map { EngineValueType.fromDocument(it) } }),
                              // Result Type
                      doc.at("result_type") ap { EngineValueType.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}



/**
 * Program Parameter Values
 */
data class ProgramParameterValues(val values : List<EngineValue>)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun atIndex(index : Int) : Maybe<EngineValue> =
        if (index > 0 && index <= values.size)
            Just(values[index - 1])
        else
            Nothing()


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


