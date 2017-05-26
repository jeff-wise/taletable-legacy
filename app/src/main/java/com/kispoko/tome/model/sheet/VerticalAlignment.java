
package com.kispoko.tome.model.sheet;

import android.view.Gravity;

import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;
import com.kispoko.tome.util.EnumUtils;



/**
 * Vertical Alignment
 */
public enum VerticalAlignment implements ToYaml
{


    // VALUES
    // ------------------------------------------------------------------------------------------

    TOP,
    MIDDLE,
    BOTTOM;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static VerticalAlignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(VerticalAlignment.class, alignmentString);
    }


    public static VerticalAlignment fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String alignmentString = yaml.getString();
        try {
            return VerticalAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static VerticalAlignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            VerticalAlignment alignment = VerticalAlignment.fromString(enumString);
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
            case TOP:
                return "Top";
            case MIDDLE:
                return "Middle";
            case BOTTOM:
                return "Bottom";
        }

        return "";
    }


    // GRAVITY CONSTANT
    // ------------------------------------------------------------------------------------------

    public int gravityConstant()
    {
        switch (this)
        {
            case TOP:
                return Gravity.TOP;
            case MIDDLE:
                return Gravity.CENTER_VERTICAL;
            case BOTTOM:
                return Gravity.BOTTOM;
            default:
                return Gravity.CENTER_VERTICAL;
        }
    }

}
