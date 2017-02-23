
package com.kispoko.tome.sheet.widget.button;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Button Color
 */
public enum ButtonColor implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    THEME_MEDIUM;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ButtonColor fromString(String colorString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ButtonColor.class, colorString);
    }


    public static ButtonColor fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String colorString = yaml.getString();
        try {
            return ButtonColor.fromString(colorString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(colorString));
        }
    }


    public static ButtonColor fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ButtonColor color = ButtonColor.fromString(enumString);
            return color;
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


    // RESOURCE ID
    // ------------------------------------------------------------------------------------------

    public int resouceId()
    {
        switch (this)
        {
            case THEME_MEDIUM:
                return R.color.dark_blue_1;
            default:
                return R.color.dark_blue_1;
        }

    }



}
