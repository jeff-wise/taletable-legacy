
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Divider Margin
 */
data class DividerMargin(val value : Float) : Serializable
{

    companion object : Factory<DividerMargin>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<DividerMargin> = when (doc)
        {
            is DocNumber -> effValue(DividerMargin(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : DividerMargin = DividerMargin(0f)
    }
}


/**
 * Divider Thickness
 */
data class DividerThickness(val value : Int) : Serializable
{

    companion object : Factory<DividerThickness>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<DividerThickness> = when (doc)
        {
            is DocNumber -> effValue(DividerThickness(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : DividerThickness = DividerThickness(0)
    }
}




