
package com.taletable.android.model.engine.variable


import android.util.Log
import com.taletable.android.app.AppEff
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.SumType
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.model.entity.ContentReference
import com.taletable.android.rts.entity.EntityId
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Content Reference Variable Value
 */
sealed class ContentReferenceVariableValue : ToDocument, SumType, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ContentReferenceVariableValue>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<ContentReferenceVariableValue> =
            when (doc.case())
            {
                "content_reference" -> ContentReferenceVariableLiteralValue.fromDocument(doc.nextCase())
                else                -> {
                    Log.d("***CONTENT REF VAR", "doc is: $doc")
                    effError(UnknownCase(doc.case(), doc.path))
                }
            }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    open fun dependencies(entityId : EntityId) : Set<VariableReference> = setOf()


    abstract fun value() : AppEff<ContentReference>


    abstract fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>>



}


/**
 * Literal Value
 */
data class ContentReferenceVariableLiteralValue(var value : ContentReference)
                : ContentReferenceVariableValue()
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ContentReferenceVariableValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ContentReferenceVariableValue> =
            effect.apply(::ContentReferenceVariableLiteralValue, ContentReference.fromDocument(doc))
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.value.toDocument()


    // | Value
    // -----------------------------------------------------------------------------------------

    override fun value() : AppEff<ContentReference> = effValue(this.value)


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())



    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(VariableNamespace(""))


    override fun case() = "literal"


    override val sumModelObject = this

}
