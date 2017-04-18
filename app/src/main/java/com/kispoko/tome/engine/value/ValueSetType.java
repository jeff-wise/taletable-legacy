
package com.kispoko.tome.engine.value;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;
import com.kispoko.tome.util.EnumUtils;



/**
 * Value Set Type
 */
public enum ValueSetType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    BASE,
    COMPOUND;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ValueSetType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ValueSetType.class, typeString);
    }


    // TODO generalize this and account for when yaml == null
    public static ValueSetType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return ValueSetType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }

}
