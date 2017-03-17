
package com.kispoko.tome.engine.program.invocation;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



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


    public static InvocationParameterType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return InvocationParameterType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
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
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }

}
