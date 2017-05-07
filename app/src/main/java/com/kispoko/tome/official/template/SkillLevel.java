
package com.kispoko.tome.official.template;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;
import com.kispoko.tome.util.EnumUtils;

import java.io.Serializable;



/**
 * Skill Level
 */
public enum SkillLevel implements Serializable
{

    // VALUES
    // -----------------------------------------------------------------------------------------

    NOVICE,
    EXPERIENCED,
    ADVANCED;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public static SkillLevel fromString(String skillLevelString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(SkillLevel.class, skillLevelString);
    }


    public static SkillLevel fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String skillLevelString = yaml.getString();
        try {
            return SkillLevel.fromString(skillLevelString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(skillLevelString));
        }
    }

}
