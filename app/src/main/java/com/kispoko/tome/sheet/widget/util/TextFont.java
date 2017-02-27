
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
 * Text Font
 */
public enum TextFont implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NORMAL,
    BOLD,
    ITALIC,
    BOLD_ITALIC;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static TextFont fromString(String fontString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(TextFont.class, fontString);
    }


    public static TextFont fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String fontString = yaml.getString();
        try {
            return TextFont.fromString(fontString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(fontString));
        }
    }


    public static TextFont fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            TextFont font = TextFont.fromString(enumString);
            return font;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Content Size's yaml string representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }

}
