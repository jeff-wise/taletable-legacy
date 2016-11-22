
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Program Invocation Parameter Type
 */
public enum ProgramInvocationParameterType
{

    REFERENCE;


    public static ProgramInvocationParameterType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ProgramInvocationParameterType.class, typeString);
    }


    public static ProgramInvocationParameterType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return ProgramInvocationParameterType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw new YamlException(new InvalidEnumError(typeString),
                                    YamlException.Type.INVALID_ENUM);
        }
    }

}
