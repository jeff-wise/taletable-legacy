
package com.taletable.android.model.engine.procedure


import android.content.Context
import com.taletable.android.app.AppEff
import com.taletable.android.db.DB_ProcedureValue
import com.taletable.android.db.procedureTable
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue5
import com.taletable.android.lib.orm.schema.MaybeProdValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.SQLBlob
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.engine.program.*
import com.taletable.android.model.engine.program.ProgramParameterValues
import com.taletable.android.model.engine.message.Message
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.program
import com.taletable.android.rts.entity.updateVariable
import effect.*
import maybe.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Procedure
 */
data class Procedure(override val id : UUID,
                     val procedureId : ProcedureId,
                     val procedureName : ProcedureName,
                     val procedureUpdates : List<ProcedureUpdate>,
                     val description : Maybe<Message>,
                     val actionLabel : ProcedureActionLabel,
                     val statistics : ProcedureStatistics)
                      : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(procedureId : ProcedureId,
                procedureName : ProcedureName,
                procedureUpdates : List<ProcedureUpdate>,
                description : Maybe<Message>,
                actionLabel : ProcedureActionLabel)
        : this(UUID.randomUUID(),
               procedureId,
               procedureName,
               procedureUpdates,
               description,
               actionLabel,
               ProcedureStatistics.default())

    constructor(procedureId : ProcedureId,
                procedureName : ProcedureName,
                procedureUpdates : List<ProcedureUpdate>,
                description : Maybe<Message>,
                actionLabel : ProcedureActionLabel,
                statistics : ProcedureStatistics)
        : this(UUID.randomUUID(),
               procedureId,
               procedureName,
               procedureUpdates,
               description,
               actionLabel,
               statistics)


    companion object : Factory<Procedure>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Procedure> = when (doc)
        {
            is DocDict ->
            {
                apply(::Procedure,
                      // Procedure Id
                      doc.at("procedure_id") ap { ProcedureId.fromDocument(it) },
                      // Procedure Name
                      doc.at("procedure_name") ap { ProcedureName.fromDocument(it) },
                      // Procedure Updates
                      doc.list("updates") ap { it.map { ProcedureUpdate.fromDocument(it) } },
                      // Description
                      split(doc.maybeAt("description"),
                            effValue<ValueError,Maybe<Message>>(Nothing()),
                            { apply(::Just, Message.fromDocument(it))}),
                      // Action Label
                      doc.at("action_label") ap { ProcedureActionLabel.fromDocument(it) },
                      // Statistics
                      split(doc.maybeAt("statistics"),
                            effValue(ProcedureStatistics.default()),
                            { ProcedureStatistics.fromDocument(it) })
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun procedureId() : ProcedureId = this.procedureId


    fun procedureName() : ProcedureName = this.procedureName


    fun procedureUpdates() : List<ProcedureUpdate> = this.procedureUpdates


    fun description() : Maybe<Message> = this.description


    fun actionLabel() : ProcedureActionLabel = this.actionLabel


    fun statistics() : ProcedureStatistics = this.statistics


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "procedure_id" to this.procedureId().toDocument(),
        "procedure_name" to this.procedureName().toDocument(),
        "action_label" to this.actionLabel().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // PROD MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ProcedureValue =
        RowValue5(procedureTable,
                  PrimValue(this.procedureId),
                  PrimValue(this.procedureName),
                  PrimValue(ProcedureUpdates(this.procedureUpdates)),
                  MaybeProdValue(this.description),
                  PrimValue(this.actionLabel))


    // -----------------------------------------------------------------------------------------
    // DESCRIPTION
    // -----------------------------------------------------------------------------------------

    fun descriptionLength() : Int = when (this.description)
    {
        is Just -> {
            this.description.value.template.value.length
        }
        else -> {
            0
        }
    }


    // -----------------------------------------------------------------------------------------
    // PROGRAMS
    // -----------------------------------------------------------------------------------------

//    fun program(sheetContext : SheetContext) : AppEff<Program> =
//        if (this.procedureUpdates.isNotEmpty()) {
//            val programId = this.procedureUpdates.first().programId()
//            SheetManager.program(programId, sheetContext)
//        }
//        else {
//            effError(AppEngineError(ProcedureDoesNotHaveUpdates(this.procedureId)))
//        }


    private fun programs(entityId : EntityId) : AppEff<List<Program>> =
        if (this.procedureUpdates.isNotEmpty())
            this.procedureUpdates.mapM { program(it.programId(), entityId) }
        else
            effValue(listOf())

    // -----------------------------------------------------------------------------------------
    // RUN
    // -----------------------------------------------------------------------------------------

    fun run(entityId : EntityId) =
        this.procedureUpdates().forEach { (variableIds, programId) ->
            program(programId, entityId)                             apDo { program ->
            program.value(ProgramParameterValues(mapOf()), entityId) apDo { engineValue ->
                variableIds.forEach {
                    updateVariable(it, engineValue, entityId)
            } } }
      }


    fun run(results : List<ProcedureUpdateResult>, entityId : EntityId)
    {
        results.forEach { result ->
            result.variableIds.forEach { variableId ->
                updateVariable(variableId, result.programResult.value, entityId)
            }
        }


//        this.procedureUpdates().forEach { (variableIds, programId) ->
//            program(programId, entityId)                             apDo { program ->
//            program.value(ProgramParameterValues(mapOf()), entityId) apDo { engineValue ->
//                variableIds.forEach {
//                    updateVariable(it, engineValue, entityId)
//            } } }
//      }
    }


    fun results(invocation : ProcedureInvocation,
                entityId : EntityId,
                context : Context) : List<ProcedureUpdateResult>
    {
        val results : MutableList<ProcedureUpdateResult> = mutableListOf()

        this.procedureUpdates().forEach { (variableIds, programId) ->
            val paramValues = programParameterValues(programId, invocation.parametersByProgram)

            program(programId, entityId)                   apDo { program ->
            program.result(paramValues, entityId, context) apDo { result ->
                results.add(ProcedureUpdateResult(variableIds, result))
            } }
        }

        return results
    }


    private fun programParameterValues(programId : ProgramId,
                                       parameterValueMap : Map<ProgramId,ProgramParameterValues>)
                                        : ProgramParameterValues =
        if (parameterValueMap.containsKey(programId))
            parameterValueMap[programId]!!
        else
            ProgramParameterValues(mapOf())





    // -----------------------------------------------------------------------------------------
    // PARAMETERS
    // -----------------------------------------------------------------------------------------

    fun parameters(entityId : EntityId) : Map<ProgramId,List<ProgramParameter>>
    {
        val parameters : MutableMap<ProgramId,List<ProgramParameter>> = mutableMapOf()

        val programs = this.programs(entityId)
        when (programs) {
            is Val -> {
                programs.value.forEach {
                    parameters.put(it.programId(), it.typeSignature().parameters())
                }
            }
        }

        return parameters
    }


    fun hasParameters(entityId : EntityId) : Boolean
    {
        val programs = this.programs(entityId)
        when (programs) {
            is Val -> {
                programs.value.forEach {
                    if (it.typeSignature().parameters().isNotEmpty())
                        return true
                }
            }
        }

        return false
    }
}


/**
 * Procedure Id
 */
data class ProcedureId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProcedureId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProcedureId> = when (doc)
        {
            is DocText -> effValue(ProcedureId(doc.text))
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

    override fun asSQLValue() = SQLText({this.value})

}


/**
 * Procedure Name
 */
data class ProcedureName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProcedureName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProcedureName> = when (doc)
        {
            is DocText -> effValue(ProcedureName(doc.text))
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

    override fun asSQLValue() = SQLText({this.value})

}


/**
 * Procedure Action Label
 */
data class ProcedureActionLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProcedureActionLabel>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProcedureActionLabel> = when (doc)
        {
            is DocText -> effValue(ProcedureActionLabel(doc.text))
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

    override fun asSQLValue() = SQLText({this.value})

}



/**
 * Procedure Update
 */
data class ProcedureUpdates(val updates : List<ProcedureUpdate>)
            : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<ProcedureUpdates>
//    {
//
//        // TODO do culebra example
//        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProcedureUpdates> = when (doc)
//        {
//            is DocList -> {
//                apply(::ProcedureUpdates,
//                doc.map { itemDoc ->
//                   when (itemDoc) {
//                       is DocDict -> {
//                           apply(::Pair,
//                                 // Variable Id
//                                 itemDoc.at("variable_id") ap { VariableId.fromDocument(it) },
//                                 // Program Id
//                                 itemDoc.at("program_id") ap { ProgramId.fromDocument(it) }
//                                 )
//                       }
//                       else       -> effError<ValueError,Pair<VariableId,ProgramId>>(UnexpectedType(DocType.DICT, docType(doc), doc.path))
//                   }
//                })
//            }
//            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
//        }
//
//    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


/**
 * Procedure Update
 */
data class ProcedureUpdate(val variableIds : List<VariableId>,
                           val programId : ProgramId,
                           val resultMessage : Maybe<Message>)
                            : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProcedureUpdate>
    {

        // TODO do culebra example
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProcedureUpdate> = when (doc)
        {
            is DocDict -> {
                apply(::ProcedureUpdate,
                     // Variable Id
                     doc.list("variable_ids") ap { it.map { VariableId.fromDocument(it) } },
                     // Program Id
                     doc.at("program_id") ap { ProgramId.fromDocument(it) },
                     // Result Message
                     split(doc.maybeAt("result_message"),
                           effValue<ValueError,Maybe<Message>>(Nothing()),
                           { apply(::Just, Message.fromDocument(it)) })
                     )
           }
           else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableIds() : List<VariableId> = this.variableIds


    fun programId() : ProgramId = this.programId


    fun resultMessage() : Maybe<Message> = this.resultMessage


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


/**
 * Procedure Statistics
 */
data class ProcedureStatistics(val usedCount : Int,
                               val usedCountMessage : Maybe<Message>)
                                : SQLSerializable, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProcedureStatistics>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProcedureStatistics> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProcedureStatistics,
                      // Times Used
                      doc.int("used_count"),
                      // Times Used Message
                      split(doc.maybeAt("used_count_message"),
                            effValue<ValueError,Maybe<Message>>(Nothing()),
                            { apply(::Just, Message.fromDocument(it)) })
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ProcedureStatistics(0, Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun usedCount() : Int = this.usedCount


    fun usedCountMessage() : Maybe<Message> = this.usedCountMessage


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "used_count" to DocNumber(this.usedCount().toDouble())
    ))


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


/**
 * Procedure Invocation
 */
data class ProcedureInvocation(val procedureId : ProcedureId,
                               val parametersByProgram : Map<ProgramId,ProgramParameterValues>)
                                : Serializable


data class ProcedureUpdateResult(val variableIds : List<VariableId>,
                                 val programResult : ProgramResultSpannable)
                                  : Serializable


