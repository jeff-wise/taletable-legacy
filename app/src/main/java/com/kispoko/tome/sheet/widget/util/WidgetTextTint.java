
package com.kispoko.tome.sheet.widget.util;


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
 * Widget Text Tint
 */
public enum WidgetTextTint implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LIGHT,
    MEDIUM_LIGHT,
    MEDIUM,
    MEDIUM_DARK,
    DARK,
    VERY_DARK,
    PURPLE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetTextTint fromString(String bgString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetTextTint.class, bgString);
    }


    public static WidgetTextTint fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return WidgetTextTint.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static WidgetTextTint fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetTextTint tint = WidgetTextTint.fromString(enumString);
            return tint;
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
            case LIGHT:
                return R.color.dark_blue_hlx_7;
            case MEDIUM_LIGHT:
                return R.color.dark_blue_hlx_9;
            case MEDIUM:
                return R.color.dark_blue_hl_1;
            case MEDIUM_DARK:
                return R.color.dark_blue_hl_3;
            case DARK:
                return R.color.dark_blue_hl_5;
            case VERY_DARK:
                return R.color.dark_blue_1;
            case PURPLE:
                return R.color.purple_medium;
        }

        return 0;
    }


    public Integer labelResourceId()
    {
        switch (this)
        {
            case LIGHT:
                return R.color.dark_blue_hl_2;
            case MEDIUM_LIGHT:
                return R.color.dark_blue_hl_4;
            case MEDIUM:
                return R.color.dark_blue_hl_6;
            case MEDIUM_DARK:
                return R.color.dark_blue_hl_8;
            case DARK:
                return R.color.dark_blue_1;
            case VERY_DARK:
                return R.color.dark_blue_3;
            case PURPLE:
                return R.color.dark_blue_hl_5;
        }

        return 0;
    }


}
