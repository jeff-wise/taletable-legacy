
package com.kispoko.tome.rules.programming.program.statement;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
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


}
