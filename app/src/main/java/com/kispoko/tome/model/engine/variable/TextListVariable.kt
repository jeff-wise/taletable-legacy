
package com.kispoko.tome.model.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.rts.entity.EntityId
import effect.apply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Text List Variable Value
 */
sealed class TextListVariableValue : ToDocument, SumType, Serializable
{

    companion object : Factory<TextListVariableValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextListVariableValue> =
            when (doc.case())
            {
                "text_list_literal" -> TextListVariableLiteralValue.fromDocument(doc)
                else                -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    abstract fun value(entityId : EntityId) : AppEff<List<String>>


    abstract fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class TextListVariableLiteralValue(val value : List<String>)
                : TextListVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextListVariableValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextListVariableValue> = when (doc)
        {
            is DocList -> apply(::TextListVariableLiteralValue, doc.stringList())
            // TODO imporve lulo to handle this
            else       -> effValue(TextListVariableLiteralValue(listOf()))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocList(this.value.map { DocText(it) })
                                    .withCase("text_list_literal")


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<List<String>> =
            effValue(this.value)


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
        effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "text_list_literal"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value.joinToString(",") })

}
