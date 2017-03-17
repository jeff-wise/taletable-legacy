
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



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
                return R.drawable.bg_widget_wrap_corners_small;
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


    public Integer cellBackgroundResourceId()
    {
        switch (this)
        {
            case MEDIUM_SMALL:
                return R.drawable.bg_cell_medium_small;
            case MEDIUM:
                return R.drawable.bg_cell_medium;
            default:
                return R.drawable.bg_cell_medium;
        }
    }

}
