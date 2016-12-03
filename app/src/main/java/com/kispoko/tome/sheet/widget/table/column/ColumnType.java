
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.rules.programming.program.ProgramValueType;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
 * ColumnUnion ErrorType
 */
public enum ColumnType
{

    TEXT,
    NUMBER,
    BOOLEAN;


    public static ColumnType fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ColumnType.class, alignmentString);
    }


    /**
     * Create a ColumnType from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Alignment, or CENTER as default.
     * @throws YamlException
     */
    public static ColumnType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String alignmentString = yaml.getString();
        try {
            return ColumnType.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(alignmentString));
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
            throw new DatabaseException(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString),
                    DatabaseException.ErrorType.INVALID_ENUM);
        }
    }


}
