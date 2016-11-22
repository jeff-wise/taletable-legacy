
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Variable Type
 */
public enum VariableType
{

    LITERAL_INTEGER,
    LITERAL_STRING,
    LITERAL_BOOLEAN,
    PROGRAM;


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
            throw new YamlException(new InvalidEnumError(typeString),
                                    YamlException.Type.INVALID_ENUM);
        }
    }


}
