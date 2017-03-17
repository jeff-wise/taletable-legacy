
package com.kispoko.tome.sheet;


import android.view.Gravity;

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
 * Row Alignment
 */
public enum Alignment implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LEFT,
    CENTER,
    RIGHT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static Alignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(Alignment.class, alignmentString);
    }


    public static Alignment fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String alignmentString = yaml.getString();
        try {
            return Alignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static Alignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            Alignment alignment = Alignment.fromString(enumString);
            return alignment;
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


    // TO STRING
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        switch (this)
        {
            case LEFT:
                return "Left";
            case CENTER:
                return "Center";
            case RIGHT:
                return "Right";
        }

        return "";
    }


    // GRAVITY CONSTANT
    // ------------------------------------------------------------------------------------------

    public int gravityConstant()
    {
        switch (this)
        {
            case LEFT:
                return Gravity.START;
            case CENTER:
                return Gravity.CENTER_HORIZONTAL;
            case RIGHT:
                return Gravity.END;
            default:
                return Gravity.START;
        }
    }


}
