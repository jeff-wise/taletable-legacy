
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Row Alignment
 */
public enum RowAlignment
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LEFT,
    CENTER,
    RIGHT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static RowAlignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(RowAlignment.class, alignmentString);
    }


    public static RowAlignment fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return CENTER;

        String alignmentString = yaml.getString();
        try {
            return RowAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static RowAlignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            RowAlignment alignment = RowAlignment.fromString(enumString);
            return alignment;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * The enum as a string for representation in a Yaml document.
     * @return The enum string.
     */
    public String yamlString()
    {
        return this.name().toLowerCase();
    }

}
