
package com.kispoko.tome.sheet;


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
 * Group Background
 */
public enum ElementBackground implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LIGHT,
    MEDIUM_LIGHT,
    MEDIUM,
    MEDIUM_DARK,
    DARK;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ElementBackground fromString(String bgString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ElementBackground.class, bgString);
    }


    public static ElementBackground fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return ElementBackground.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static ElementBackground fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ElementBackground background = ElementBackground.fromString(enumString);
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
            case LIGHT:
                return R.color.dark_blue_3;
            case MEDIUM_LIGHT:
                return R.color.dark_blue_4;
            case MEDIUM:
                return R.color.dark_blue_5;
            case MEDIUM_DARK:
                return R.color.dark_blue_6;
            case DARK:
                return R.color.dark_blue_7;
        }

        return 0;
    }


}