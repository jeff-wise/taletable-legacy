
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
 * Widget Background
 */
public enum WidgetBackground implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    EMPTY,
    LIGHT,
    MEDIUM,
    MEDIUM_DARK,
    DARK;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetBackground fromString(String bgString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetBackground.class, bgString);
    }


    public static WidgetBackground fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return WidgetBackground.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static WidgetBackground fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetBackground background = WidgetBackground.fromString(enumString);
            return background;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
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

    public Integer resourceId(WidgetCorners corners)
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


    public Integer resourceId(WidgetCorners corners, TextSize size)
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

}
