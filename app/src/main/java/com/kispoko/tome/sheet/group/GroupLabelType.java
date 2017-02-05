
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.util.ArrayList;
import java.util.List;



/**
 * Group Label Type
 */
public enum GroupLabelType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    PRIMARY,
    SECONDARY,
    TERTIARY;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static GroupLabelType fromString(String alignmentString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(GroupLabelType.class, alignmentString);
    }


    public static GroupLabelType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String alignmentString = yaml.getString();
        try {
            return GroupLabelType.fromString(alignmentString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
        }
    }


    public static GroupLabelType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            GroupLabelType groupLabelType = GroupLabelType.fromString(enumString);
            return groupLabelType;
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
