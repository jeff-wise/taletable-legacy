
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.R
import effect.effError
import effect.effValue
import lulo.document.DocText
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser



/**
 * Corners
 */
sealed class Corners
{

    class None : Corners()
    class Small : Corners()
    class Medium : Corners()
    class Large : Corners()


    fun resourceId() : Int = when(this)
    {
        is None -> R.drawable.bg_corners_none
        is Small -> R.drawable.bg_corners_small
        is Medium -> R.drawable.bg_corners_medium
        is Large -> R.drawable.bg_corners_large
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<Corners> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "none"   -> effValue<ValueError,Corners>(Corners.None())
                "small"  -> effValue<ValueError,Corners>(Corners.Small())
                "medium" -> effValue<ValueError,Corners>(Corners.Medium())
                "large"  -> effValue<ValueError,Corners>(Corners.Large())
                else     -> effError<ValueError,Corners>(
                                    UnexpectedValue("Corners", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}




