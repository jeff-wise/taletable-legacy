
package com.kispoko.tome.sheet.widget.option;


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
 * View Mode
 */
public enum ViewType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NO_ARROWS,
    ARROWS_VERTICAL,
    ARROWS_HORZIONTAL,
    EXPANDED_SLASHES;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ViewType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ViewType.class, typeString);
    }


    public static ViewType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return ViewType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static ViewType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ViewType viewType = ViewType.fromString(enumString);
            return viewType;
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


}
