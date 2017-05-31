
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import effect.Err
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser



/**
 * Divider Margin
 */
data class DividerMargin(val value : Long)
{

    companion object : Factory<DividerMargin>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<DividerMargin> = when (doc)
        {
            is DocInteger -> effValue(DividerMargin(doc.integer))
            else          -> effError(UnexpectedType(DocType.INTEGER, docType(doc), doc.path))
        }
    }
}


/**
 * Divider Thickness
 */
data class DividerThickness(val value : Long)
{

    companion object : Factory<DividerThickness>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<DividerThickness> = when (doc)
        {
            is DocInteger -> effValue(DividerThickness(doc.integer))
            else          -> effError(UnexpectedType(DocType.INTEGER, docType(doc), doc.path))
        }
    }
}




