
package com.kispoko.tome.sheet.group;


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
 * Row Separation
 *
 * The vertical margins of the group row.
 */
public enum SpaceAbove implements ToYaml
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

    public static SpaceAbove fromString(String sepString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(SpaceAbove.class, sepString);
    }


    public static SpaceAbove fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String sepString = yaml.getString();
        try {
            return SpaceAbove.fromString(sepString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(sepString));
        }
    }


    public static SpaceAbove fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            SpaceAbove separation = SpaceAbove.fromString(enumString);
            return separation;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

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
            case VERY_SMALL:
                return R.dimen.space_abvoe_very_small;
            case SMALL:
                return R.dimen.space_abvoe_small;
            case MEDIUM_SMALL:
                return R.dimen.space_above_medium_small;
            case MEDIUM:
                return R.dimen.space_above_medium;
            case MEDIUM_LARGE:
                return R.dimen.space_above_medium_large;
            case LARGE:
                return R.dimen.space_above_large;
            case VERY_LARGE:
                return R.dimen.space_above_very_large;
        }

        return 0;
    }


}
