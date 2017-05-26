
package com.kispoko.tome.model.engine.program.statement;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



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
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }


    // TO STRING
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        switch (this)
        {
            case PARAMETER:
                return "Parameter";
            case VARIABLE:
                return "Variable";
            case LITERAL_STRING:
                return "String Literal";
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ParameterType.class.getName())));
        }

        return "";
    }

}
