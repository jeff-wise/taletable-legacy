
package com.kispoko.tome.model.theme


import com.kispoko.tome.lib.Factory
import effect.Err
import effect.effError
import effect.effValue
import lulo.document.DocText
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser



/**
 * Color Id
 */
data class ColorId(val id : String)
{

    companion object : Factory<ColorId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ColorId> = when (doc)
        {
            is DocText -> effValue(ColorId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}
