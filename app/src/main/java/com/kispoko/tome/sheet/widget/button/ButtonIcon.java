
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
 * Button Icon
 */
public enum ButtonIcon implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    HEART_PLUS,
    HEART_MINUS;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ButtonIcon fromString(String iconString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ButtonIcon.class, iconString);
    }


    public static ButtonIcon fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String iconString = yaml.getString();
        try {
            return ButtonIcon.fromString(iconString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(iconString));
        }
    }


    public static ButtonIcon fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ButtonIcon icon = ButtonIcon.fromString(enumString);
            return icon;
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
            case HEART_PLUS:
                return R.drawable.ic_button_heart_plus;
            case HEART_MINUS:
                return R.drawable.ic_button_heart_minus;
            default:
                return R.drawable.ic_button_heart_plus;
        }

    }


}
