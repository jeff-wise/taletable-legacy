
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



/**
 * ColumnUnion ErrorType
 */
public enum ColumnType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    TEXT,
    NUMBER,
    BOOLEAN;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ColumnType fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ColumnType.class, alignmentString);
    }


    /**
     * Create a ColumnType from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Alignment, or CENTER as default.
     * @throws YamlParseException
     */
    public static ColumnType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String alignmentString = yaml.getString();
        try {
            return ColumnType.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static ColumnType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ColumnType columnType = ColumnType.fromString(enumString);
            return columnType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }
}
