
package com.kispoko.tome.sheet.widget.util;


import android.widget.LinearLayout;

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
 * Position
 */
public enum Position implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LEFT,
    TOP,
    RIGHT,
    BOTTOM;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static Position fromString(String positionString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(Position.class, positionString);
    }


    public static Position fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String positionString = yaml.getString();
        try {
            return Position.fromString(positionString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(positionString));
        }
    }


    public static Position fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            Position position = Position.fromString(enumString);
            return position;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
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


    // LAYOUT ORIENTATION
    // ------------------------------------------------------------------------------------------

    public int linearLayoutOrientation()
    {
        switch (this)
        {
            case LEFT:
                return LinearLayout.HORIZONTAL;
            case TOP:
                return LinearLayout.VERTICAL;
            case RIGHT:
                return LinearLayout.HORIZONTAL;
            case BOTTOM:
                return LinearLayout.VERTICAL;
            default:
                return LinearLayout.HORIZONTAL;
        }
    }


}
