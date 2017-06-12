
package com.kispoko.tome.model.sheet.style


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
 * Position
 */
sealed class Position
{

    class Left : Position()
    class Top : Position()
    class Right : Position()
    class Bottom : Position()


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<Position> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "left"   -> effValue<ValueError,Position>(Position.Left())
                "top"    -> effValue<ValueError,Position>(Position.Top())
                "right"  -> effValue<ValueError,Position>(Position.Right())
                "bottom" -> effValue<ValueError,Position>(Position.Bottom())
                else     -> effError<ValueError,Position>(
                                    UnexpectedValue("Corners", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}



//
//
//    // LAYOUT ORIENTATION
//    // ------------------------------------------------------------------------------------------
//
//    public int linearLayoutOrientation()
//    {
//        switch (this)
//        {
//            case LEFT:
//                return LinearLayout.HORIZONTAL;
//            case TOP:
//                return LinearLayout.VERTICAL;
//            case RIGHT:
//                return LinearLayout.HORIZONTAL;
//            case BOTTOM:
//                return LinearLayout.VERTICAL;
//            default:
//                return LinearLayout.HORIZONTAL;
//        }
//    }

