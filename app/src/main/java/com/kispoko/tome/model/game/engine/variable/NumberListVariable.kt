
package com.kispoko.tome.model.game.engine.variable


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
 * Number List Variable Value
 */
sealed class NumberListVariableValue : ToDocument, SumType, Serializable
{

    companion object : Factory<NumberListVariableValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberListVariableValue> =
            when (doc.case())
            {
                "number_list_literal" -> NumberListVariableLiteralValue.fromDocument(doc)
                else                  -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    abstract fun value(entityId : EntityId) : AppEff<List<Double>>


    abstract fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class NumberListVariableLiteralValue(val value : List<Double>)
                : NumberListVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberListVariableValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberListVariableValue> = when (doc)
        {
            is DocList -> apply(::NumberListVariableLiteralValue, doc.doubleList())
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocList(this.value.map { DocNumber(it) })
                                    .withCase("number_list_literal")


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<List<Double>> =
            effValue(this.value)


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
        effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "number_list_literal"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value.joinToString(",") })

}