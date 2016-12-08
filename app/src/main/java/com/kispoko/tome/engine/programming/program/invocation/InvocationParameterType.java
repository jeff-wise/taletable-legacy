
package com.kispoko.tome.engine.programming.program.invocation;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Program Invocation Parameter ErrorType
 */
public enum InvocationParameterType
{

    REFERENCE;


    public static InvocationParameterType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(InvocationParameterType.class, typeString);
    }


    public static InvocationParameterType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return InvocationParameterType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static InvocationParameterType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            InvocationParameterType invocationParameterType =
                    InvocationParameterType.fromString(enumString);
            return invocationParameterType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }

}
