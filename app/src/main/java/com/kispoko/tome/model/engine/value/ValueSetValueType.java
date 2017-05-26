
package com.kispoko.tome.model.engine.value;


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
 * Value Set Value Type
 *
 * The type of values inside a value set.
 */
public enum ValueSetValueType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    TEXT,
    NUMBER,
    ANY;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ValueSetValueType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ValueSetValueType.class, typeString);
    }


    public static ValueSetValueType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return ValueSetValueType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ValueSetValueType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ValueSetValueType valueType = ValueSetValueType.fromString(enumString);
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
