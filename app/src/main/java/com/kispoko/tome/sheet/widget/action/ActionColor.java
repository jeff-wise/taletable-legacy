
package com.kispoko.tome.sheet.widget.action;


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
 * Action Color
 */
public enum ActionColor implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    BLUE,
    GREEN,
    PURPLE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ActionColor fromString(String colorString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ActionColor.class, colorString);
    }


    public static ActionColor fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String colorString = yaml.getString();
        try {
            return ActionColor.fromString(colorString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(colorString));
        }
    }


    public static ActionColor fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ActionColor color = ActionColor.fromString(enumString);
            return color;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
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


    // RESOURCE ID
    // ------------------------------------------------------------------------------------------

    public Integer resourceId()
    {
        switch (this)
        {
            case BLUE:
                return R.color.dark_blue_hlx_7;
            case PURPLE:
                return R.color.purple_light;
            case GREEN:
                return R.color.green_light;
            default:
                return R.color.dark_blue_hlx_7;
        }
    }
}
