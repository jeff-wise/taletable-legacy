
package com.kispoko.tome.official.template;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;
import com.kispoko.tome.util.EnumUtils;

import java.io.Serializable;



/**
 * Games
 */
public enum Game implements Serializable
{

    // VALUES
    // -----------------------------------------------------------------------------------------

    SRD_5;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public static Game fromString(String gameString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(Game.class, gameString);
    }


    public static Game fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String gameString = yaml.getString();
        try {
            return Game.fromString(gameString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(gameString));
        }
    }

}
