
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
 * Height
 */
sealed class Height
{

    class Wrap        : Height()
    class VerySmall   : Height()
    class Small       : Height()
    class MediumSmall : Height()
    class Medium      : Height()
    class MediumLarge : Height()
    class Large       : Height()
    class VeryLarge   : Height()


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<Height> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "wrap"         -> effValue<ValueError,Height>(Height.Wrap())
                "very_small"   -> effValue<ValueError,Height>(Height.VerySmall())
                "small"        -> effValue<ValueError,Height>(Height.Small())
                "medium_small" -> effValue<ValueError,Height>(Height.MediumSmall())
                "medium"       -> effValue<ValueError,Height>(Height.Medium())
                "medium_large" -> effValue<ValueError,Height>(Height.MediumLarge())
                "large"        -> effValue<ValueError,Height>(Height.Large())
                "very_large"   -> effValue<ValueError,Height>(Height.VeryLarge())
                else           -> effError<ValueError,Height>(
                                        UnexpectedValue("Height", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
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