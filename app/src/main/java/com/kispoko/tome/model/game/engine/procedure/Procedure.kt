
package com.kispoko.tome.model.game.engine.procedure


import com.kispoko.tome.db.DB_ProcedureValue
import com.kispoko.tome.db.procedureTable
import com.kispoko.tome.db.programTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.game.engine.variable.VariableTagSet
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
                     val procedureUpdates : ProcedureUpdates)
                      : ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(procedureId : ProcedureId,
                procedureName : ProcedureName,
                procedureUpdates : ProcedureUpdates)
        : this(UUID.randomUUID(),
               procedureId,
               procedureName,
               procedureUpdates)


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
                      doc.at("updates") ap { ProcedureUpdates.fromDocument(it) }
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


    // -----------------------------------------------------------------------------------------
    // PROD MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ProcedureValue =
        RowValue3(procedureTable,
                  PrimValue(this.procedureId),
                  PrimValue(this.procedureName),
                  PrimValue(this.procedureUpdates))

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
data class ProcedureName(val value : String) : SQLSerializable, Serializable
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
