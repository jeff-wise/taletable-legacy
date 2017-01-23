
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.util.ArrayList;
import java.util.List;



/**
 * Row Width
 */
public enum RowWidth implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    FULL,
    THREE_QUARTERS,
    HALF;


    public static List<String> valueStringList()
    {
        List<String> stringList = new ArrayList<>();

        for (RowWidth rowWidth : RowWidth.values()) {
            stringList.add(rowWidth.name());
        }

        return stringList;
    }


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static RowWidth fromString(String widthString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(RowWidth.class, widthString);
    }


    public static RowWidth fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return FULL;

        String widthString = yaml.getString();
        try {
            return RowWidth.fromString(widthString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(widthString));
        }
    }


    public static RowWidth fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            RowWidth width = RowWidth.fromString(enumString);
            return width;
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


    // TO STRING
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        switch (this)
        {
            case FULL:
                return "100%";
            case THREE_QUARTERS:
                return "75%";
            case HALF:
                return "50%";
        }

        return "";
    }

}
