
package com.kispoko.tome.model.game.engine.procedure


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Conj
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.model.game.engine.program.ProgramId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Procedure
 */
data class Procedure(override val id : UUID,
                     val procedureId : Prim<ProcedureId>,
                     val procedureName : Prim<ProcedureName>,
                     val programIds : Conj<ProgramId>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(procedureId : ProcedureId,
                procedureName : ProcedureName,
                programIds : MutableSet<ProgramId>)
        : this(UUID.randomUUID(),
               Prim(procedureId),
               Prim(procedureName),
               Conj(programIds))


    companion object : Factory<Procedure>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Procedure> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Procedure,
                         // Procedure Id
                         doc.at("procedure_id") ap { ProcedureId.fromDocument(it) },
                         // Procedure Name
                         doc.at("procedure_name") ap { ProcedureName.fromDocument(it) },
                         // Program Ids
                         doc.list("program_ids") ap { it.mapSetMut { ProgramId.fromDocument(it) }}
                         )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun procedureId() : ProcedureId = this.procedureId.value

    fun procedureName() : String = this.procedureName.value.value

    fun programIds() : Set<ProgramId> = this.programIds.set


    // -----------------------------------------------------------------------------------------
    // PROD MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "procedure"

    override val modelObject = this

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
