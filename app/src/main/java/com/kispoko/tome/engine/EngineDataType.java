
package com.kispoko.tome.engine;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



/**
 * Program Value ErrorType
 */
public enum EngineDataType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    INTEGER,
    STRING,
    BOOLEAN,
    DICE,
    LIST;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static EngineDataType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(EngineDataType.class, typeString);
    }


    public static EngineDataType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return EngineDataType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    // TO STRING
    // ------------------------------------------------------------------------------------------

    public String toString()
    {
        switch (this)
        {
            case INTEGER:
                return "Number";
            case STRING:
                return "Text";
            case BOOLEAN:
                return "True/False";
            case DICE:
                return "Dice";
            case LIST:
                return "List";
        }

        return "";
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }

}
