
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.R
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
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
import java.io.Serializable



/**
 * Height
 */
sealed class Height : SQLSerializable, Serializable
{

    object Wrap : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"wrap"})
    }


    object VerySmall   : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"very_small"})
    }


    object Small       : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"small"})
    }


    object MediumSmall : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"medium_small"})
    }


    object Medium      : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"medium"})
    }


    object MediumLarge : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"medium_large"})
    }


    object Large       : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"large"})
    }


    object VeryLarge   : Height()
    {
        override fun asSQLValue() : SQLValue = SQLText({"very_large"})
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<Height> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "wrap"         -> effValue<ValueError,Height>(Height.Wrap)
                "very_small"   -> effValue<ValueError,Height>(Height.VerySmall)
                "small"        -> effValue<ValueError,Height>(Height.Small)
                "medium_small" -> effValue<ValueError,Height>(Height.MediumSmall)
                "medium"       -> effValue<ValueError,Height>(Height.Medium)
                "medium_large" -> effValue<ValueError,Height>(Height.MediumLarge)
                "large"        -> effValue<ValueError,Height>(Height.Large)
                "very_large"   -> effValue<ValueError,Height>(Height.VeryLarge)
                else           -> effError<ValueError,Height>(
                                        UnexpectedValue("Height", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun isWrap() : Boolean = when (this)
    {
        is Wrap -> true
        else    -> false

    }


    fun resourceId(corners : Corners) = when (this)
    {
        is Wrap ->
        {
            when (corners)
            {
                is Corners.None   -> R.drawable.bg_sheet_corners_none
                is Corners.Small  -> R.drawable.bg_sheet_corners_small
                is Corners.Medium -> R.drawable.bg_sheet_corners_medium
                is Corners.Large  -> R.drawable.bg_sheet_corners_large
            }
        }
        is VerySmall ->
        {
            when (corners)
            {
                is Corners.None   -> R.drawable.bg_widget_very_small_none
                is Corners.Small  -> R.drawable.bg_widget_very_small_small
                is Corners.Medium -> R.drawable.bg_widget_very_small_medium
                is Corners.Large  -> R.drawable.bg_widget_very_small_large
            }
        }
        is Small ->
        {
            when (corners)
            {
                is Corners.None   -> R.drawable.bg_widget_small_none
                is Corners.Small  -> R.drawable.bg_widget_small_small
                is Corners.Medium -> R.drawable.bg_widget_small_medium
                is Corners.Large  -> R.drawable.bg_widget_small_large
            }
        }
        else -> R.drawable.bg_widget_very_small_small
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