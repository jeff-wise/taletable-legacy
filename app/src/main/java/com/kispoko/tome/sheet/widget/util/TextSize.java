
package com.kispoko.tome.sheet.widget.util;


import android.content.Context;

import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



/**
 * Widget Content Size
 */
public enum TextSize implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    SUPER_SMALL,
    VERY_SMALL,
    SMALL,
    MEDIUM_SMALL,
    MEDIUM,
    MEDIUM_LARGE,
    LARGE,
    VERY_LARGE,
    HUGE,
    GARGANTUAN,
    COLOSSAL;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static TextSize fromString(String sizeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(TextSize.class, sizeString);
    }


    /**
     * Creates a Size enum from its Yaml representation. If there is no Yaml representation
     * (it is null), then use MEDIUM as a default size.
     * @param yaml The Yaml parser.
     * @return A new Size enum, with MEDIUM as the default.
     * @throws YamlParseException
     */
    public static TextSize fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String sizeString = yaml.getString();
        try {
            return TextSize.fromString(sizeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(sizeString));
        }
    }


    public static TextSize fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            TextSize size = TextSize.fromString(enumString);
            return size;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Content Size's yaml string representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }


    // API
    // ------------------------------------------------------------------------------------------

    public int resourceId()
    {
        switch (this)
        {
            case SUPER_SMALL:
                return R.dimen.text_size_super_small;
            case VERY_SMALL:
                return R.dimen.text_size_very_small;
            case SMALL:
                return R.dimen.text_size_small;
            case MEDIUM_SMALL:
                return R.dimen.text_size_medium_small;
            case MEDIUM:
                return R.dimen.text_size_medium;
            case MEDIUM_LARGE:
                return R.dimen.text_size_medium_large;
            case LARGE:
                return R.dimen.text_size_large;
            case VERY_LARGE:
                return R.dimen.text_size_very_large;
            case HUGE:
                return R.dimen.text_size_huge;
            case GARGANTUAN:
                return R.dimen.text_size_gargantuan;
            case COLOSSAL:
                return R.dimen.text_size_colossal;
        }

        return 0;
    }


    public float size()
    {
        switch (this)
        {
            case SUPER_SMALL:
                return 3f;
            case VERY_SMALL:
                return 3.3f;
            case SMALL:
                return 3.7f;
            case MEDIUM_SMALL:
                return 4.2f;
            case MEDIUM:
                return 4.6f;
            case MEDIUM_LARGE:
                return 5f;
            case LARGE:
                return 6.2f;
            case VERY_LARGE:
                return 7.5f;
            case HUGE:
                return 9f;
            case GARGANTUAN:
                return 11;
            case COLOSSAL:
                return 13f;
            default:
                return 4.2f;
        }
    }


}
