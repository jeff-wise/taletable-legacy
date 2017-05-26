
package com.kispoko.tome.model.sheet;


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
 * Sheet Theme
 */
public enum SheetThemeType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    DARK,
    LIGHT,
    CUSTOM;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static SheetThemeType fromString(String themeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(SheetThemeType.class, themeString);
    }


    public static SheetThemeType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String themeString = yaml.getString();
        try {
            return SheetThemeType.fromString(themeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(themeString));
        }
    }


    public static SheetThemeType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            SheetThemeType theme = SheetThemeType.fromString(enumString);
            return theme;
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


}
