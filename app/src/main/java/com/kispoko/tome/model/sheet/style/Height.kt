
package com.kispoko.tome.model.sheet.style



/**
 * Height
 */
enum class Height
{
    WRAP,
    VERY_SMALL,
    SMALL,
    MEDIUM_SMALL,
    MEDIUM,
    MEDIUM_LARGE,
    LARGE,
    VERY_LARGE;
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