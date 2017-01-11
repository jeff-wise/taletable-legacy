
package com.kispoko.tome.engine.programming.program.statement;


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
 * Parameter ErrorType
 */
public enum ParameterType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    PARAMETER,
    VARIABLE,
    LITERAL_STRING;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ParameterType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ParameterType.class, typeString);
    }


    public static ParameterType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return ParameterType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
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
