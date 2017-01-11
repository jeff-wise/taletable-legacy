
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Row Width
 */
public enum RowWidth
{

    FULL,
    THREE_QUARTERS,
    HALF;


    public static RowWidth fromString(String widthString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(RowWidth.class, widthString);
    }


    public static RowWidth fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return FULL;

        String widthString = yaml.getString();
        try {
            return RowWidth.fromString(widthString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(widthString));
        }
    }


    public static RowWidth fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            RowWidth width = RowWidth.fromString(enumString);
            return width;
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
