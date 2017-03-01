
package com.kispoko.tome.sheet.widget.quote;


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
 * Quote View Type
 */
public enum ViewType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    SOURCE;


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
            ViewType type = ViewType.fromString(enumString);
            return type;
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
