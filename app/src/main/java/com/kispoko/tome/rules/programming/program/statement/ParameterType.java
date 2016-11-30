
package com.kispoko.tome.rules.programming.program.statement;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.rules.programming.variable.VariableType;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Parameter Type
 */
public enum ParameterType
{
    PARAMETER,
    VARIABLE,
    LITERAL_STRING;


    public static ParameterType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ParameterType.class, typeString);
    }


    public static ParameterType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return ParameterType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ParameterType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ParameterType parameterType = ParameterType.fromString(enumString);
            return parameterType;
        } catch (InvalidDataException e) {
            throw new DatabaseException(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString),
                    DatabaseException.ErrorType.INVALID_ENUM);
        }
    }

}
