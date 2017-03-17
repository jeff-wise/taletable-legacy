
package com.kispoko.tome.engine.program;


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
 * Program Value ErrorType
 */
public enum ProgramValueType implements ToYaml
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

    public static ProgramValueType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ProgramValueType.class, typeString);
    }


    public static ProgramValueType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return ProgramValueType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ProgramValueType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ProgramValueType programValueType = ProgramValueType.fromString(enumString);
            return programValueType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
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
