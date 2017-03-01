
package com.kispoko.tome.sheet.widget.util;


import android.widget.LinearLayout;

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
 * Padding
 */
public enum Padding implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    VERY_SMALL,
    SMALL,
    MEDIUM_SMALL,
    MEDIUM,
    MEDIUM_LARGE,
    LARGE,
    VERY_LARGE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static Padding fromString(String paddingString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(Padding.class, paddingString);
    }


    public static Padding fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String paddingString = yaml.getString();
        try {
            return Padding.fromString(paddingString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(paddingString));
        }
    }


    public static Padding fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            Padding padding = Padding.fromString(enumString);
            return padding;
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


    // RESOURCE ID (FOR THE PADDING DIMENSION)
    // ------------------------------------------------------------------------------------------

    /**
     * The resource id of the dimension for the standard padding size.
     * @return The dimension resource id.
     */
    public int resourceId()
    {
        switch (this)
        {
            case VERY_SMALL:
                return R.dimen.padding_very_small;
            case SMALL:
                return R.dimen.padding_small;
            case MEDIUM_SMALL:
                return R.dimen.padding_medium_small;
            case MEDIUM:
                return R.dimen.padding_medium;
            case MEDIUM_LARGE:
                return R.dimen.padding_medium_large;
            case LARGE:
                return R.dimen.padding_large;
            case VERY_LARGE:
                return R.dimen.padding_very_large;
            default:
                return R.dimen.padding_small;
        }
    }



}
