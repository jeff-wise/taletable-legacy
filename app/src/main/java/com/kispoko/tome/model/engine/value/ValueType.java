
package com.kispoko.tome.model.engine.value;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



/**
 * Value Type
 */
public enum ValueType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    TEXT,
    NUMBER;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ValueType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ValueType.class, typeString);
    }


    public static ValueType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return ValueType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ValueType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ValueType valueType = ValueType.fromString(enumString);
            return valueType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }

}
