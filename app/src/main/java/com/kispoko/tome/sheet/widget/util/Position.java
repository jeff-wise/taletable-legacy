
package com.kispoko.tome.sheet.widget.util;


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


}
