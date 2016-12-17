
package com.kispoko.tome.mechanic.dice;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

/**
 * Die Type
 */
public enum DiceType
{

    D3,
    D4,
    D6,
    D8,
    D10,
    D12,
    D20,
    D100;


    public static DiceType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(DiceType.class, typeString);
    }


    public static DiceType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return DiceType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(typeString));
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

}
