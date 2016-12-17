
package com.kispoko.tome.engine.programming.variable;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.widget.table.column.ColumnType;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
 * Variable ErrorType
 */
public enum VariableType
{

    TEXT,
    NUMBER,
    BOOLEAN,
    DICE;


    public static VariableType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(VariableType.class, typeString);
    }


    public static VariableType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return VariableType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static VariableType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            VariableType variableType = VariableType.fromString(enumString);
            return variableType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


}
