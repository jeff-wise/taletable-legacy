
package com.kispoko.tome.sheet.widget.util;


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
 * Widget Corners
 *
 * The corner radius of the widget background.
 */
public enum WidgetCorners implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    SMALL,
    MEDIUM,
    LARGE,
    CIRCLE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetCorners fromString(String radius)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetCorners.class, radius);
    }


    public static WidgetCorners fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return WidgetCorners.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static WidgetCorners fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetCorners radius = WidgetCorners.fromString(enumString);
            return radius;
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

}
