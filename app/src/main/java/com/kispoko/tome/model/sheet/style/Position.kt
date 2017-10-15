
package com.kispoko.tome.model.sheet.style


import android.widget.LinearLayout
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Position
 */
sealed class Position : ToDocument, SQLSerializable, Serializable
{

    object Left : Position()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"left"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("left")

    }


    object Top : Position()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"top"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("top")

    }


    object Right : Position()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"right"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("right")

    }


    object Bottom : Position()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"bottom"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("bottom")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Position> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "left"   -> effValue<ValueError,Position>(Position.Left)
                "top"    -> effValue<ValueError,Position>(Position.Top)
                "right"  -> effValue<ValueError,Position>(Position.Right)
                "bottom" -> effValue<ValueError,Position>(Position.Bottom)
                else     -> effError<ValueError,Position>(
                                    UnexpectedValue("Corners", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun isLeft() : Boolean = when (this)
    {
        is Left -> true
        else    -> false
    }


    fun isTop() : Boolean = when (this)
    {
        is Top -> true
        else   -> false
    }


    fun isRight() : Boolean = when (this)
    {
        is Right -> true
        else     -> false
    }


    fun isBottom() : Boolean = when (this)
    {
        is Bottom -> true
        else      -> false
    }


    fun linearLayoutOrientation() : Int = when (this)
    {
        is Top    -> LinearLayout.VERTICAL
        is Right  -> LinearLayout.HORIZONTAL
        is Bottom -> LinearLayout.VERTICAL
        is Left   -> LinearLayout.HORIZONTAL
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

