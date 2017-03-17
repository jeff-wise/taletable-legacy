
package com.kispoko.tome.sheet.widget.util;


import android.content.Context;
import android.text.style.RelativeSizeSpan;

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


    public RelativeSizeSpan relativeSizeSpan(TextSize baseSize, Context context)
    {
        float baseSizePx = context.getResources().getDimension(baseSize.resourceId());
        float thisSizePx = context.getResources().getDimension(this.resourceId());

        float relativeSize = thisSizePx / baseSizePx;

        return new RelativeSizeSpan(relativeSize);
    }

}
