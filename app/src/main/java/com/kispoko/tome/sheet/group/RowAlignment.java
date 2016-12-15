
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.widget.util.WidgetContentAlignment;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


/**
 * Row Alignment
 */
public enum RowAlignment
{

    LEFT,
    CENTER,
    RIGHT;


    public static RowAlignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(RowAlignment.class, alignmentString);
    }


    public static RowAlignment fromYaml(Yaml yaml)
                  throws YamlException
    {
        if (yaml.isNull())
            return CENTER;

        String alignmentString = yaml.getString();
        try {
            return RowAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static RowAlignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            RowAlignment alignment = RowAlignment.fromString(enumString);
            return alignment;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


}
