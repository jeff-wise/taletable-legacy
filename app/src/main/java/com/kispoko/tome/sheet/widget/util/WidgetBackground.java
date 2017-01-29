
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import static com.kispoko.tome.sheet.widget.util.WidgetContentSize.MEDIUM_LARGE;
import static com.kispoko.tome.sheet.widget.util.WidgetContentSize.MEDIUM_SMALL;


/**
 * Widget Background
 */
public enum WidgetBackground
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    LIGHT,
    MEDIUM,
    DARK;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetBackground fromString(String bgString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetBackground.class, bgString);
    }


    public static WidgetBackground fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return WidgetBackground.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static WidgetBackground fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetBackground background = WidgetBackground.fromString(enumString);
            return background;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
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


    // RESOURCE ID
    // ------------------------------------------------------------------------------------------

    public Integer resourceId()
    {
        switch (this)
        {
            case NONE:
                return null;
            case LIGHT:
                return R.drawable.bg_widget_light;
            case MEDIUM:
                return R.drawable.bg_widget_medium;
            case DARK:
                return R.drawable.bg_widget_dark;
        }

        return 0;
    }

}
