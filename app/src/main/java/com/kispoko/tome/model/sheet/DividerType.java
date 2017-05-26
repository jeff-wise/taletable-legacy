
package com.kispoko.tome.model.sheet;


import com.kispoko.tome.R;
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
 * Group Divider Type
 */
public enum DividerType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    VERY_LIGHT,
    LIGHT,
    DARK,
    VERY_DARK;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DividerType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(DividerType.class, typeString);
    }


    public static DividerType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return DividerType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static DividerType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            DividerType type = DividerType.fromString(enumString);
            return type;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }


    // RESOURCES
    // ------------------------------------------------------------------------------------------

    // Color Id With Background
    // ------------------------------------------------------------------------------------------

    public int colorIdWithBackground(BackgroundColor background)
    {
        switch (background)
        {
            case LIGHT:
                switch (this)
                {
                    case LIGHT:
                        return R.color.dark_blue_3;
                    case VERY_LIGHT:
                        return R.color.dark_blue_4;
                    case DARK:
                        return R.color.dark_blue_6;
                    case VERY_DARK:
                        return R.color.dark_blue_7;
                }
                break;
            case MEDIUM_LIGHT:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_4;
                    case LIGHT:
                        return R.color.dark_blue_5;
                    case DARK:
                        return R.color.dark_blue_7;
                    case VERY_DARK:
                        return R.color.dark_blue_8;
                }
                break;
            case MEDIUM:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_5;
                    case LIGHT:
                        return R.color.dark_blue_6;
                    case DARK:
                        return R.color.dark_blue_8;
                    case VERY_DARK:
                        return R.color.dark_blue_9;
                }
                break;
            case MEDIUM_DARK:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_6;
                    case LIGHT:
                        return R.color.dark_blue_7;
                    case DARK:
                        return R.color.dark_blue_9;
                    case VERY_DARK:
                        return R.color.dark_blue_10;
                }
                break;
            case DARK:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_7;
                    case LIGHT:
                        return R.color.dark_blue_8;
                    case DARK:
                        return R.color.dark_blue_10;
                    case VERY_DARK:
                        return R.color.dark_blue_11;
                }
                break;
        }

        return R.color.dark_blue_5;
    }


    public int tableRowResourceIdWithBackground(BackgroundColor background)
    {
        switch (background)
        {
            case LIGHT:
                switch (this)
                {
                    case LIGHT:
                        return R.color.dark_blue_3;
                    case VERY_LIGHT:
                        return R.color.dark_blue_4;
                    case DARK:
                        return R.color.dark_blue_6;
                    case VERY_DARK:
                        return R.color.dark_blue_7;
                }
                break;
            case MEDIUM_LIGHT:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_4;
                    case LIGHT:
                        return R.color.dark_blue_5;
                    case DARK:
                        return R.color.dark_blue_7;
                    case VERY_DARK:
                        return R.color.dark_blue_8;
                }
                break;
            case MEDIUM:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_5;
                    case LIGHT:
                        return R.color.dark_blue_6;
                    case DARK:
                        return R.color.dark_blue_8;
                    case VERY_DARK:
                        return R.color.dark_blue_9;
                }
                break;
            case MEDIUM_DARK:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_6;
                    case LIGHT:
                        return R.color.dark_blue_7;
                    case DARK:
                        return R.color.dark_blue_9;
                    case VERY_DARK:
                        return R.color.dark_blue_10;
                }
                break;
            case DARK:
                switch (this)
                {
                    case VERY_LIGHT:
                        return R.color.dark_blue_7;
                    case LIGHT:
                        return R.color.dark_blue_8;
                    case DARK:
                        return R.color.dark_blue_10;
                    case VERY_DARK:
                        return R.color.dark_blue_11;
                }
                break;
        }

        return R.color.dark_blue_5;
    }


}
