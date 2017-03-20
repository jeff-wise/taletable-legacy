
package com.kispoko.tome.sheet.widget.button;


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
 * Button View Type
 */
public enum ButtonViewType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    CIRCLE_ICON_BUTTON,
    TEXT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ButtonViewType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ButtonViewType.class, typeString);
    }


    public static ButtonViewType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return ButtonViewType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ButtonViewType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ButtonViewType type = ButtonViewType.fromString(enumString);
            return type;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Content Alignment's yaml string representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }

}
