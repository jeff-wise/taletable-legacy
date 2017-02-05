
package com.kispoko.tome.sheet.widget.util;


import android.view.Gravity;

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
 * Widget Label Alignment
 */
public enum WidgetLabelAlignment implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LEFT,
    CENTER,
    RIGHT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetLabelAlignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetLabelAlignment.class, alignmentString);
    }


    public static WidgetLabelAlignment fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String alignmentString = yaml.getString();
        try {
            return WidgetLabelAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static WidgetLabelAlignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetLabelAlignment alignment = WidgetLabelAlignment.fromString(enumString);
            return alignment;
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


    // Gravity Constant
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
        }

        return 0;
    }

}
