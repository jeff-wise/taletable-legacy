
package com.kispoko.tome.sheet.widget.table.cell;

import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * CellUnion alignment. Determines the alignment of the cell data inside its cell boundary.
 */
public enum CellAlignment
{
    LEFT,
    CENTER,
    RIGHT;

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
     * @throws YamlException
     */
    public static CellAlignment fromYaml(Yaml yaml)
                  throws YamlException
    {
        if (yaml.isNull())
            return CENTER;

        String alignmentString = yaml.getString();
        try {
            return CellAlignment.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }

}
