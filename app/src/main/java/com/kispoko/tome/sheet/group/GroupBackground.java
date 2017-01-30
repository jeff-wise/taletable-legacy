
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.widget.util.WidgetBackground;
import com.kispoko.tome.sheet.widget.util.WidgetCorners;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
 * Group Background
 */
public enum GroupBackground implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    LIGHT,
    DARK;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static GroupBackground fromString(String bgString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(GroupBackground.class, bgString);
    }


    public static GroupBackground fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return GroupBackground.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static GroupBackground fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            GroupBackground background = GroupBackground.fromString(enumString);
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
                return R.color.dark_blue_4;
            case DARK:
                return R.color.dark_blue_7;
        }

        return 0;
    }


}
