
package com.kispoko.tome.sheet;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



/**
 * Widget Background
 * TODO merge widget data into widget format yaml
 */
public enum BackgroundColor implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    EMPTY,
    LIGHT,
    MEDIUM_LIGHT,
    MEDIUM,
    MEDIUM_DARK,
    DARK,
    VERY_DARK,
    SUPER_DARK,
    UBER_DARK;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static BackgroundColor fromString(String bgString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(BackgroundColor.class, bgString);
    }


    public static BackgroundColor fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return BackgroundColor.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static BackgroundColor fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            BackgroundColor background = BackgroundColor.fromString(enumString);
            return background;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Content Alignment's yaml string representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }


    // RESOURCE ID
    // ------------------------------------------------------------------------------------------


    public Integer resourceId(Corners corners)
    {
        switch (this)
        {
            case NONE:
                return R.drawable.bg_widget_none;
            case EMPTY:
                return R.drawable.bg_widget_empty;
            case LIGHT:
                return R.drawable.bg_widget_light;
            case MEDIUM:
                return R.drawable.bg_widget_medium_small_corners_small;
            case MEDIUM_DARK:
                return R.drawable.bg_widget_medium_dark_small_corners_small;
            case DARK:
                if (corners != null)
                {
                    switch (corners)
                    {
                        case SMALL:
                            return R.drawable.bg_widget_dark_small_corners_small;
                        case MEDIUM:
                            return R.drawable.bg_widget_dark_medium_corners;
                        case LARGE:
                            return R.drawable.bg_widget_dark_large_corners;
                        case CIRCLE:
                            return R.drawable.bg_widget_dark_circle;
                    }
                }
                else
                {
                    return R.drawable.bg_widget_dark_small_corners_small;
                }
            default:
                return R.drawable.bg_widget_medium_small_corners_small;
        }
    }


    public Integer resourceId(Corners corners, TextSize size)
    {
        switch (this)
        {
            case NONE:
                return R.drawable.bg_widget_none;
            case EMPTY:
                return R.drawable.bg_widget_empty;
            case LIGHT:
                return R.drawable.bg_widget_light;
            case MEDIUM:
                switch (size)
                {
                    case VERY_SMALL:
                        return R.drawable.bg_widget_medium_small_corners_small;
                    case SMALL:
                        return R.drawable.bg_widget_medium_small_corners_small;
                    case MEDIUM_SMALL:
                        return R.drawable.bg_widget_medium_small_corners_small;
                    case MEDIUM:
                        return R.drawable.bg_widget_medium_small_corners_small;
                    case MEDIUM_LARGE:
                        return R.drawable.bg_widget_medium_small_corners_medium;
                    case LARGE:
                        return R.drawable.bg_widget_medium_small_corners_medium;
                    default:
                        return R.drawable.bg_widget_medium_small_corners_small;
                }
            case MEDIUM_DARK:
                return R.drawable.bg_widget_medium_dark_small_corners_small;
            case DARK:
                if (corners != null)
                {
                    switch (corners)
                    {
                        case SMALL:
                            switch (size)
                            {
                                case VERY_SMALL:
                                    return R.drawable.bg_widget_dark_small_corners_small;
                                case SMALL:
                                    return R.drawable.bg_widget_dark_small_corners_small;
                                case MEDIUM_SMALL:
                                    return R.drawable.bg_widget_dark_small_corners_small;
                                case MEDIUM:
                                    return R.drawable.bg_widget_dark_small_corners_small;
                                case MEDIUM_LARGE:
                                    return R.drawable.bg_widget_dark_small_corners_medium;
                                case LARGE:
                                    return R.drawable.bg_widget_dark_small_corners_large;
                            }
                        case MEDIUM:
                            return R.drawable.bg_widget_dark_medium_corners;
                        case LARGE:
                            return R.drawable.bg_widget_dark_large_corners;
                        case CIRCLE:
                            return R.drawable.bg_widget_dark_circle;
                    }
                }
                else
                {
                    return R.drawable.bg_widget_dark_small_corners_small;
                }
            default:
                return R.drawable.bg_widget_medium_small_corners_small;
        }
    }


    public Integer colorId()
    {
        switch (this)
        {
            case EMPTY:
                return null;
            case NONE:
                return R.color.transparent;
            case LIGHT:
                return R.color.dark_blue_5;
            case MEDIUM_LIGHT:
                return R.color.dark_blue_6;
            case MEDIUM:
                return R.color.dark_blue_7;
            case MEDIUM_DARK:
                return R.color.dark_blue_8;
            case DARK:
                return R.color.dark_blue_9;
            case VERY_DARK:
                return R.color.dark_blue_10;
            case SUPER_DARK:
                return R.color.dark_blue_11;
            default:
                return R.color.dark_blue_7;
        }
    }


}
