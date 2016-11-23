
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * CellUnion Type
 */
public enum CellType
{

    TEXT,
    NUMBER,
    BOOLEAN;


    public static CellType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(CellType.class, typeString);
    }


    public static CellType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return CellType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


}
