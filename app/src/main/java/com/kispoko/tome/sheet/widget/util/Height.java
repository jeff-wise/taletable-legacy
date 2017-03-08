
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Widget Height
 */
public enum Height implements ToYaml
{

    // VALUES
    // -----------------------------------------------------------------------------------------

    WRAP,
    VERY_SMALL,
    SMALL,
    MEDIUM_SMALL,
    MEDIUM,
    MEDIUM_LARGE,
    LARGE,
    VERY_LARGE;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public static Height fromString(String heightString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(Height.class, heightString);
    }


    public static Height fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String heightString = yaml.getString();
        try {
            return Height.fromString(heightString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(heightString));
        }
    }


    public static Height fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            Height height = Height.fromString(enumString);
            return height;
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


    // HEIGHT
    // ------------------------------------------------------------------------------------------

    /**
     * Get the drawable resource id of the background with this height and the specified corners.
     * @param corners The corners measurement.
     * @return The drawable resource id i.e. bg_xxxx_xxx.xml
     */
    public Integer resourceId(Corners corners)
    {
        switch (this)
        {
            case WRAP:
                return null;
            case VERY_SMALL:
                switch (corners)
                {
                    case SMALL:
                        return R.drawable.bg_widget_very_small_corners_small;
                    case MEDIUM:
                        return R.drawable.bg_widget_very_small_corners_medium;
                    default:
                        return R.drawable.bg_widget_very_small_corners_small;
                }
            case SMALL:
                switch (corners)
                {
                    case SMALL:
                        return R.drawable.bg_widget_small_corners_small;
                    case MEDIUM:
                        return R.drawable.bg_widget_small_corners_medium;
                    default:
                        return R.drawable.bg_widget_small_corners_small;
                }
            default:
                return R.drawable.bg_widget_very_small_corners_small;

        }

    }

}
