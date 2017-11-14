
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.db.DB_Action
import com.kispoko.tome.db.dbAction
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.summation.SummationId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Action
 */
data class Action(override val id : UUID,
                  val actionName : ActionName,
                  val rollSummationId : Maybe<SummationId>,
                  val procedureId : Maybe<ProcedureId>)
                   : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : ActionName,
                rollSummationId : Maybe<SummationId>,
                procedureId : Maybe<ProcedureId>)
        : this(UUID.randomUUID(),
               name,
               rollSummationId,
               procedureId)


    companion object : Factory<Action>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Action> = when (doc)
        {
            is DocDict ->
            {
                apply(::Action,
                      // Action Name
                      doc.at("name") ap { ActionName.fromDocument(it) },
                      // Roll Summation Id
                      split(doc.maybeAt("roll_summation_id"),
                            effValue<ValueError,Maybe<SummationId>>(Nothing()),
                            { effApply(::Just, SummationId.fromDocument(it)) }),
                      // Procedure Id
                      split(doc.maybeAt("procedure_id"),
                            effValue<ValueError,Maybe<ProcedureId>>(Nothing()),
                            { effApply(::Just, ProcedureId.fromDocument(it)) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : ActionName = this.actionName

    fun rollSummationId() : Maybe<SummationId> = this.rollSummationId

    fun procedureId() : Maybe<ProcedureId> = this.procedureId


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument()
    ))
    .maybeMerge(this.rollSummationId().apply {
        Just(Pair("roll_summation_id", it.toDocument() as SchemaDoc)) })
    .maybeMerge(this.procedureId().apply {
        Just(Pair("procedure_id", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_Action =
            dbAction(this.actionName, this.rollSummationId, this.procedureId)

}



/**
 * Action Name
 */
data class ActionName(val value : String) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ActionName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ActionName> = when (doc)
        {
            is DocText -> effValue(ActionName(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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

