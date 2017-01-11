
package com.kispoko.tome.mechanic.dice;


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
 * Die Type
 */
public enum DiceType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    D3,
    D4,
    D6,
    D8,
    D10,
    D12,
    D20,
    D100;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DiceType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(DiceType.class, typeString);
    }


    public static DiceType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return DiceType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static DiceType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            DiceType dice = DiceType.fromString(enumString);
            return dice;
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
