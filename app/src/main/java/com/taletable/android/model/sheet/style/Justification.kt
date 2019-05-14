
package com.taletable.android.model.sheet.style


import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Justification
 */
sealed class Justification : ToDocument, Serializable
{

    // | CASES
    // -----------------------------------------------------------------------------------------

    object ParentStart : Justification()
    {
        // | TO DOCUMENT
        override fun toDocument() = DocText("parent_start")
    }


    object SpaceBetween : Justification()
    {
        // | TO DOCUMENT
        override fun toDocument() = DocText("space_between")
    }

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Justification> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "parent_start"  -> effValue<ValueError,Justification>(Justification.ParentStart)
                "space_between" -> effValue<ValueError,Justification>(Justification.SpaceBetween)
                else            -> effError<ValueError,Justification>(
                                       UnexpectedValue("Justification", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}