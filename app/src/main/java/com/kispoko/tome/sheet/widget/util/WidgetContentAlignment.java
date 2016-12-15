
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
 * Widget Content Alignment
 */
public enum WidgetContentAlignment
{

    LEFT,
    CENTER,
    RIGHT;


    public static WidgetContentAlignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetContentAlignment.class, alignmentString);
    }


    public static WidgetContentAlignment fromYaml(Yaml yaml)
                  throws YamlException
    {
        String alignmentString = yaml.getString();
        try {
            return WidgetContentAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static WidgetContentAlignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetContentAlignment alignment = WidgetContentAlignment.fromString(enumString);
            return alignment;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


}
