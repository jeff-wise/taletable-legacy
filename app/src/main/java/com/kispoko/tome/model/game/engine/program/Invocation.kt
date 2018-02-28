
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppEvalError
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueNumber
import com.kispoko.tome.model.game.engine.EngineValueText
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.reference.DataReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.game.engine.interpreter.UnexpectedProgramResultType
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetData
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Program Invocation
 */
data class Invocation(override val id : UUID,
                      val programId : ProgramId,
                      val parameters : InvocationParameters)
                       : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(programId : ProgramId,
                parameters : InvocationParameters)
        : this(UUID.randomUUID(),
               programId,
               parameters)


    companion object : Factory<Invocation>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Invocation> = when (doc)
        {
            is DocDict ->
            {
                apply(::Invocation,
                      // Program Name
                      doc.at("program_id") ap { ProgramId.fromDocument(it) },
                      // Parameters
                      split(doc.maybeAt("parameters"),
                            effValue(InvocationParameters(mapOf())),
                            { InvocationParameters.fromDocument(it) })
                      )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "program_id" to this.programId().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun programId() : ProgramId = this.programId


    fun parameters() : InvocationParameters = this.parameters


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject = this


    override fun rowValue() : DB_InvocationValue =
        RowValue2(invocationTable,
                  PrimValue(this.programId),
                  PrimValue(this.parameters))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The set of variables that the program depends on.
     */
    fun dependencies(sheetContext : SheetContext) : Set<VariableReference> = setOf()
//    {
//        val deps = mutableSetOf<VariableReference>()
//
//        this.parameters().forEach {
//            deps.addAll(it.dependencies(sheetContext))
//        }
//
//        val programDeps = SheetManager.program(this.programId, sheetContext).apply {
//                effValue<AppError,Set<VariableReference>>(it.dependencies(sheetContext)) }
//        when (programDeps) {
//            is Val -> deps.addAll(programDeps.value)
//            is Err -> ApplicationLog.error(programDeps.error)
//        }
//
//        return deps
//    }


//    private fun programParameters(sheetContext : SheetContext) : AppEff<ProgramParameterValues> =
//        if (this.parameters.isEmpty())
//        {
//            effValue(ProgramParameterValues(listOf()))
//        }
//        else
//        {
//            this.parameters().mapM {
//                SheetData.referenceEngineValue(it, sheetContext)
//            }
//            .apply { effValue<AppError,ProgramParameterValues>(ProgramParameterValues(it.filterJust()))
//            }
//        }


    // -----------------------------------------------------------------------------------------
    // PROGRAM PARAMETER VALUES
    // -----------------------------------------------------------------------------------------

    private fun programParameterValues(sheetContext : SheetContext) : ProgramParameterValues
    {
        val parameterValueMap : MutableMap<String,EngineValue> = mutableMapOf()

        this.parameters().parameterMap.entries.forEach {
            val name = it.key
            val dataReference = it.value

            SheetData.referenceEngineValue(dataReference,sheetContext) apDo { mEngineValue ->
                when (mEngineValue) {
                    is Just -> parameterValueMap.put(name, mEngineValue.value)
                }
            }

        }

        return ProgramParameterValues(parameterValueMap)
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(sheetContext : SheetContext) : AppEff<EngineValue> =
            GameManager.engine(sheetContext.gameId) ap { engine  ->
            engine.program(this.programId)          ap { program ->
            program.value(this.programParameterValues(sheetContext), sheetContext)
            } }


    fun numberValue(sheetContext : SheetContext) : AppEff<Double> =
        this.value(sheetContext) ap { engineValue ->
            when (engineValue)
            {
                is EngineValueNumber -> effValue(engineValue.value)
                else                 ->
                    effError<AppError,Double>(
                            AppEvalError(UnexpectedProgramResultType(this.programId(),
                                                                      engineValue.type(),
                                                                      EngineValueType.Number)))
            }
        }


    fun textValue(sheetContext : SheetContext) : AppEff<String> =
        this.value(sheetContext) ap { engineValue ->
            when (engineValue)
            {
                is EngineValueText -> effValue(engineValue.value)
                else                 ->
                    effError<AppError,String>(
                            AppEvalError(UnexpectedProgramResultType(this.programId(),
                                                                      engineValue.type(),
                                                                      EngineValueType.Number)))
            }
        }

}


/**
 * Invocation Parameters
 */
data class InvocationParameters(val parameterMap : Map<String,DataReference>)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<InvocationParameters>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<InvocationParameters> = when (doc)
        {
            is DocList ->
            {
                doc.map { InvocationParameter.fromDocument(it) } ap {
                    effValue<ValueError, InvocationParameters>(InvocationParameters(it.map { it.toPair() }.toMap()))
                }
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
 * Invocation Parameter
 */
data class InvocationParameter(val name : String, val value : DataReference) : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<InvocationParameter>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<InvocationParameter> = when (doc)
        {
            is DocDict ->
            {
                apply(::InvocationParameter,
                      // Name
                      doc.text("name"),
                      // Value
                      doc.at("value") ap { DataReference.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    fun toPair() : Pair<String,DataReference> = Pair(this.name, this.value)

}

