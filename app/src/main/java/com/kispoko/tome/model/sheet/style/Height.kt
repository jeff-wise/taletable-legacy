
package com.kispoko.tome.model.sheet.style


import android.util.Log
import com.kispoko.tome.lib.orm.sql.*
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



sealed class Height : ToDocument, SQLSerializable, Serializable
{


    override fun toString() : String = when(this)
    {
        is Wrap  -> "wrap"
        is Fixed -> this.value.toString()
    }


    /**
     * Height Wrap
     */
    object Wrap : Height()
    {
        override fun asSQLValue() : SQLValue = SQLReal({0.0})

        override fun toDocument() = DocNumber(0.0)
    }


    /**
     * Fixed
     */
    data class Fixed(val value : Float) : Height(), SQLSerializable, Serializable
    {

        // -----------------------------------------------------------------------------------------
        // SQL SERIALIZABLE
        // -----------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

        override fun toDocument() = DocNumber(this.value.toDouble())

    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Height> = when (doc)
        {
            is DocNumber ->
            {
                val num = doc.number.toFloat()
                Log.d("***HEIGHT", num.toString())
                if (num == 0.0f)
                    effValue<ValueError,Height>(Height.Wrap)
                else
                    effValue<ValueError,Height>(Height.Fixed(num))
            }
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    fun isWrap() : Boolean = when (this)
    {
        is Wrap -> true
        else    -> false

    }

}



//
//
//        public Integer resourceId(Corners corners)
//    {
//        switch (this)
//        {
//            case WRAP:
//                return R.drawable.bg_widget_wrap_corners_small;
//            case VERY_SMALL:
//                switch (corners)
//                {
//                    case SMALL:
//                        return R.drawable.bg_widget_very_small_corners_small;
//                    case MEDIUM:
//                        return R.drawable.bg_widget_very_small_corners_medium;
//                    default:
//                        return R.drawable.bg_widget_very_small_corners_small;
//                }
//            case SMALL:
//                switch (corners)
//                {
//                    case SMALL:
//                        return R.drawable.bg_widget_small_corners_small;
//                    case MEDIUM:
//                        return R.drawable.bg_widget_small_corners_medium;
//                    default:
//                        return R.drawable.bg_widget_small_corners_small;
//                }
//            default:
//                return R.drawable.bg_widget_very_small_corners_small;
//
//        }
//
//    }
//
//
//    public Integer cellBackgroundResourceId()
//    {
//        switch (this)
//        {
//            case MEDIUM_SMALL:
//                return R.drawable.bg_cell_medium_small;
//            case MEDIUM:
//                return R.drawable.bg_cell_medium;
//            default:
//                return R.drawable.bg_cell_medium;
//        }
//    }