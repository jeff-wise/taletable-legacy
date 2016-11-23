
package com.kispoko.tome.rules.refinement;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
* Refinement Type
*/
public enum RefinementType
{
    MEMBER_OF;


    public static RefinementType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(RefinementType.class, typeString);
    }


    public static RefinementType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String sizeString = yaml.getString();
        try {
            return RefinementType.fromString(sizeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(sizeString));
        }
    }

}


