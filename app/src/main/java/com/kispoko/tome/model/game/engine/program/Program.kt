
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEvalError
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.reference.DataReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.rts.game.engine.interpreter.ResultBindingDoesNotExist
import com.kispoko.tome.rts.sheet.SheetContext
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Nothing
import maybe.Maybe
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
                                ProdValue(this.typeSignature),
                                CollValue(this.statements),
                                PrimValue(this.resultBindingName))


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    fun dependencies(sheetContext : SheetContext) : Set<VariableReference>
    {
        val deps = mutableSetOf<VariableReference>()

        this.statements().forEach {
            deps.addAll(it.dependencies(sheetContext))
        }

        return deps
    }


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
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramDescription> = when (doc)
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

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })

}


/**
 * Program Parameters
 */
data class ProgramTypeSignature(override val id : UUID,
                                val parameters : List<ProgramParameter>,
                                val result : EngineValueType)
                                 : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(parameters : List<ProgramParameter>,
                resultType : EngineValueType)
        : this(UUID.randomUUID(),
               parameters,
               resultType)


    companion object : Factory<ProgramTypeSignature>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramTypeSignature> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProgramTypeSignature,
                      // Parameters
                      split(doc.maybeList("parameters"),
                            effValue(listOf()),
                            { it.map { ProgramParameter.fromDocument(it) } }),
                      // Result
                      doc.at("result") ap { EngineValueType.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "parameters" to DocList(this.parameters().map { it.toDocument() }),
        "result" to this.result().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun parameters() : List<ProgramParameter> = this.parameters


    fun result() : EngineValueType = this.result


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ProgramTypeSignatureValue =
        RowValue2(programTypeSignatureTable,
                  CollValue(this.parameters),
                  PrimValue(this.result))

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


