
package com.kispoko.tome.activity.sheet.dialog;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



/**
 * Arithmetic Dialog Type
 */
public enum ArithmeticDialogType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    INCREMENTAL;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ArithmeticDialogType fromString(String type)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ArithmeticDialogType.class, type);
    }


    public static ArithmeticDialogType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String typeString = yaml.getString();
        try {
            return ArithmeticDialogType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ArithmeticDialogType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ArithmeticDialogType type = ArithmeticDialogType.fromString(enumString);
            return type;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Text Tint's yaml string representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }

}
