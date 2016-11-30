
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.rules.programming.variable.VariableType;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
 * Program Value Type
 */
public enum ProgramValueType
{
    INTEGER,
    STRING;


    public static ProgramValueType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ProgramValueType.class, typeString);
    }


    public static ProgramValueType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return ProgramValueType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ProgramValueType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ProgramValueType programValueType = ProgramValueType.fromString(enumString);
            return programValueType;
        } catch (InvalidDataException e) {
            throw new DatabaseException(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString),
                    DatabaseException.ErrorType.INVALID_ENUM);
        }
    }

}
