
package com.kispoko.tome.sheet.widget.util;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

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
public enum TextColor implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    VERY_LIGHT,
    LIGHT,
    MEDIUM_LIGHT,
    MEDIUM,
    MEDIUM_DARK,
    DARK,
    VERY_DARK,
    SUPER_DARK,
    GOLD_VERY_LIGHT,
    PURPLE,
    PURPLE_LIGHT,
    PURPLE_VERY_LIGHT,
    GREEN_VERY_LIGHT,
    GREEN_LIGHT,
    GREEN_MEDIUM_LIGHT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static TextColor fromString(String bgString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(TextColor.class, bgString);
    }


    public static TextColor fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String bgString = yaml.getString();
        try {
            return TextColor.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static TextColor fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            TextColor tint = TextColor.fromString(enumString);
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
            case VERY_LIGHT:
                return R.color.dark_blue_hlx_5;
            case LIGHT:
                return R.color.dark_blue_hlx_7;
            case MEDIUM_LIGHT:
                return R.color.dark_blue_hlx_9;
            case MEDIUM:
                return R.color.dark_blue_hl_2;
            case MEDIUM_DARK:
                return R.color.dark_blue_hl_4;
            case DARK:
                return R.color.dark_blue_hl_6;
            case VERY_DARK:
                return R.color.dark_blue_hl_8;
            case SUPER_DARK:
                return R.color.dark_blue_1;
            case GOLD_VERY_LIGHT:
                return R.color.gold_very_light;
            case PURPLE:
                return R.color.purple_light;
            case PURPLE_LIGHT:
                return R.color.purple_light;
            case PURPLE_VERY_LIGHT:
                return R.color.purple_very_light;
            case GREEN_VERY_LIGHT:
                return R.color.green_very_light;
            case GREEN_LIGHT:
                return R.color.green_light;
            case GREEN_MEDIUM_LIGHT:
                return R.color.green_medium_light;
        }

        return 0;
    }


    public ForegroundColorSpan foregroundColorSpan(Context context)
    {
        int colorId = this.resourceId();
        return new ForegroundColorSpan(ContextCompat.getColor(context, colorId));
    }


}
