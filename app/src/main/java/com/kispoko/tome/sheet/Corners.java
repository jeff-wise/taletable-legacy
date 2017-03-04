
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
 * Widget Corners
 *
 * The corner radius of the widget background.
 */
public enum Corners implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    SMALL,
    MEDIUM,
    LARGE,
    CIRCLE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static Corners fromString(String radius)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(Corners.class, radius);
    }


    public static Corners fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String bgString = yaml.getString();
        try {
            return Corners.fromString(bgString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(bgString));
        }
    }


    public static Corners fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            Corners radius = Corners.fromString(enumString);
            return radius;
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

    public int resourceId()
    {
        switch (this)
        {
            case SMALL:
                return R.drawable.bg_group_corners_small;
            case MEDIUM:
                return R.drawable.bg_group_corners_medium;
            default:
                return R.drawable.bg_group_corners_small;
        }
    }

}
