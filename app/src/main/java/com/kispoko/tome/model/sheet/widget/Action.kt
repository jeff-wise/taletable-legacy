
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.engine.dice.DiceRollGroup
import com.kispoko.tome.model.engine.procedure.ProcedureId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Nothing
import maybe.Maybe
import java.io.Serializable
import java.util.*



/**
 * Action
 */
data class Action(override val id : UUID,
                  val actionName : ActionName,
                  val rollGroup : Maybe<DiceRollGroup>,
                  val autoRolls : AutoRolls,
                  val procedureId : Maybe<ProcedureId>)
                   : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : ActionName,
                rollGroup : Maybe<DiceRollGroup>,
                autoRolls : AutoRolls,
                procedureId : Maybe<ProcedureId>)
        : this(UUID.randomUUID(),
               name,
               rollGroup,
               autoRolls,
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
                      // Roll group
                      split(doc.maybeAt("roll_group"),
                            effValue<ValueError, Maybe<DiceRollGroup>>(Nothing()),
                            { effApply(::Just, DiceRollGroup.fromDocument(it)) }),
                      // Auto Rolls
                      split(doc.maybeAt("auto_rolls"),
                            effValue(AutoRolls(0)),
                            { AutoRolls.fromDocument(it) }),
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


    fun rollGroup() : Maybe<DiceRollGroup> = this.rollGroup


    fun autoRolls() : AutoRolls = this.autoRolls


    fun procedureId() : Maybe<ProcedureId> = this.procedureId


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "auto_rolls" to this.autoRolls().toDocument()
    ))
    .maybeMerge(this.rollGroup().apply {
        Just(Pair("roll_group", it.toDocument() as SchemaDoc)) })
    .maybeMerge(this.procedureId().apply {
        Just(Pair("procedure_id", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ActionValue =
        RowValue3(actionTable, PrimValue(this.actionName),
                               MaybeProdValue(this.rollGroup),
                               MaybePrimValue(this.procedureId))

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


/**
 * Auto Rolls
 */
data class AutoRolls(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<AutoRolls>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<AutoRolls> = when (doc)
        {
            is DocNumber -> effValue(AutoRolls(doc.number.toInt()))
            else         -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value.toLong()})

}

