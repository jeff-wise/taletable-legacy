
package com.kispoko.tome.model.game.engine.procedure


import android.util.Log
import com.kispoko.tome.db.DB_ProcedureValue
import com.kispoko.tome.db.procedureTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.program.ProgramParameterValues
import com.kispoko.tome.model.game.engine.variable.Message
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
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
                     val procedureUpdates : ProcedureUpdates,
                     val description : Maybe<Message>,
                     val actionLabel : Maybe<ProcedureActionLabel>)
                      : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(procedureId : ProcedureId,
                procedureName : ProcedureName,
                procedureUpdates : ProcedureUpdates,
                description : Maybe<Message>,
                actionLabel : Maybe<ProcedureActionLabel>)
        : this(UUID.randomUUID(),
               procedureId,
               procedureName,
               procedureUpdates,
               description,
               actionLabel)


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
                      doc.at("updates") ap { ProcedureUpdates.fromDocument(it) },
                      // Description
                      split(doc.maybeAt("description"),
                            effValue<ValueError,Maybe<Message>>(Nothing()),
                            { apply(::Just, Message.fromDocument(it))}),
                      // Action Label
                      split(doc.maybeAt("action_label"),
                            effValue<ValueError,Maybe<ProcedureActionLabel>>(Nothing()),
                            { apply(::Just, ProcedureActionLabel.fromDocument(it))})
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


    fun procedureUpdates() : ProcedureUpdates = this.procedureUpdates


    fun description() : Maybe<Message> = this.description


    fun actionLabel() : Maybe<ProcedureActionLabel> = this.actionLabel


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "procedure_id" to this.procedureId().toDocument(),
        "procedure_name" to this.procedureName().toDocument()
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
                  PrimValue(this.procedureUpdates),
                  MaybeProdValue(this.description),
                  MaybePrimValue(this.actionLabel))


    // -----------------------------------------------------------------------------------------
    // RUN
    // -----------------------------------------------------------------------------------------

    fun run(sheetContext : SheetContext) =
        this.procedureUpdates().updates.forEach { (variableId, programId) ->
            GameManager.engine(sheetContext.gameId)                       apDo { engine ->
            engine.program(programId)                                     apDo { program ->
            program.value(ProgramParameterValues(listOf()), sheetContext) apDo { engineValue ->
            SheetManager.sheetState(sheetContext.sheetId)                 apDo { state ->
            state.updateVariable(variableId, engineValue, sheetContext)
            } } } }
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
 * Procedure Action Lable
 */
data class ProcedureActionLabel(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({this.value})

}



/**
 * Procedure Update
 */
data class ProcedureUpdates(val updates : List<Pair<VariableId,ProgramId>>)
            : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProcedureUpdates>
    {

        // TODO do culebra example
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProcedureUpdates> = when (doc)
        {
            is DocList -> {
                apply(::ProcedureUpdates,
                doc.map { itemDoc ->
                   when (itemDoc) {
                       is DocDict -> {
                           apply(::Pair,
                                 // Variable Id
                                 itemDoc.at("variable_id") ap { VariableId.fromDocument(it) },
                                 // Program Id
                                 itemDoc.at("program_id") ap { ProgramId.fromDocument(it) }
                                 )
                       }
                       else       -> effError<ValueError,Pair<VariableId,ProgramId>>(UnexpectedType(DocType.DICT, docType(doc), doc.path))
                   }
                })
            }
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}
