
package com.taletable.android.model.engine.program


import com.taletable.android.app.AppEff
import com.taletable.android.app.AppError
import com.taletable.android.app.AppEvalError
import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue2
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.SQLBlob
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.engine.EngineValue
import com.taletable.android.model.engine.EngineValueNumber
import com.taletable.android.model.engine.EngineValueText
import com.taletable.android.model.engine.EngineValueType
import com.taletable.android.model.engine.reference.DataReference
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.engine.interpreter.UnexpectedProgramResultType
import com.taletable.android.rts.entity.program
import com.taletable.android.rts.entity.sheet.SheetData
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
    fun dependencies(entityId : EntityId) : Set<VariableReference>
    {
        val deps = mutableSetOf<VariableReference>()

        this.parameters().parameterMap.values.forEach {
            deps.addAll(it.dependencies(entityId))
        }

        program(this.programId, entityId) apDo {
            deps.addAll(it.dependencies(entityId))
        }

//        Log.d("****INVOCATION", "deps: $deps")

        return deps
    }


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

    private fun programParameterValues(entityId : EntityId) : ProgramParameterValues
    {
        val parameterValueMap : MutableMap<String,EngineValue> = mutableMapOf()

        this.parameters().parameterMap.entries.forEach {
            val name = it.key
            val dataReference = it.value

            SheetData.referenceEngineValue(dataReference,entityId) apDo { mEngineValue ->
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

    fun value(entityId : EntityId) : AppEff<EngineValue> =
        program(this.programId, entityId)          ap { it ->
            it.value(this.programParameterValues(entityId), entityId)
        }


    fun numberValue(entityId : EntityId) : AppEff<Double> =
        this.value(entityId) ap { engineValue ->
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


    fun textValue(entityId : EntityId) : AppEff<String> =
        this.value(entityId) ap { engineValue ->
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

