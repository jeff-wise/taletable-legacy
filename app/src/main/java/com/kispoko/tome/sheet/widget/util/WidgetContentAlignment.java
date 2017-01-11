
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Widget Content Alignment
 */
public enum WidgetContentAlignment implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LEFT,
    CENTER,
    RIGHT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetContentAlignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetContentAlignment.class, alignmentString);
    }


    public static WidgetContentAlignment fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String alignmentString = yaml.getString();
        try {
            return WidgetContentAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static WidgetContentAlignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetContentAlignment alignment = WidgetContentAlignment.fromString(enumString);
            return alignment;
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
