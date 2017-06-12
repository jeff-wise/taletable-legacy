
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

    class NONE : Corners()
    class SMALL : Corners()
    class MEDIUM : Corners()
    class LARGE : Corners()


    fun resourceId() : Int = when(this)
    {
        is NONE   -> R.drawable.bg_corners_none
        is SMALL  -> R.drawable.bg_corners_small
        is MEDIUM -> R.drawable.bg_corners_medium
        is LARGE  -> R.drawable.bg_corners_large
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<Corners> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "none"   -> effValue<ValueError,Corners>(Corners.NONE())
                "small"  -> effValue<ValueError,Corners>(Corners.SMALL())
                "medium" -> effValue<ValueError,Corners>(Corners.MEDIUM())
                "large"  -> effValue<ValueError,Corners>(Corners.LARGE())
                else     -> effError<ValueError,Corners>(
                                    UnexpectedValue("Corners", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}




