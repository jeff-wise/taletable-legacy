
package com.kispoko.tome.sheet.widget.table.cell;


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
 * CellUnion alignment. Determines the alignment of the cell data inside its cell boundary.
 */
public enum CellAlignment implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    LEFT,
    CENTER,
    RIGHT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static CellAlignment fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(CellAlignment.class, alignmentString);
    }


    /**
     * Create a cell Alignment object from its Yaml representation. If the representation does
     * not exist (is null), then a default alignment of CENTER is returned.
     * @param yaml The Yaml parser.
     * @return The parsed Alignment, or CENTER as default.
     * @throws YamlParseException
     */
    public static CellAlignment fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String alignmentString = yaml.getString();
        try {
            return CellAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static CellAlignment fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            CellAlignment cellAlignment = CellAlignment.fromString(enumString);
            return cellAlignment;
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
