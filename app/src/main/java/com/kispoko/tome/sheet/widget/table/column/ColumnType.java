
package com.kispoko.tome.sheet.widget.table.column;


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
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }
}
