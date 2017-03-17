
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;

import java.util.ArrayList;
import java.util.List;



/**
 * Row Width
 */
public enum RowWidth implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NO_PADDING,
    FULL,
    INDENTED,
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
                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
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
            case NO_PADDING:
                return "No Padding";
            case FULL:
                return "100%";
            case INDENTED:
                return "90%";
            case THREE_QUARTERS:
                return "75%";
            case HALF:
                return "50%";
        }

        return "";
    }


    // RESOURCE ID
    // ------------------------------------------------------------------------------------------

    public int resourceId()
    {
        switch (this)
        {
            case NO_PADDING:
                return R.dimen.zero_dp;
            case FULL:
                return R.dimen.group_row_full_padding;
            case INDENTED:
                return R.dimen.group_row_indented_padding;
            case THREE_QUARTERS:
                return R.dimen.group_row_three_quarters_padding;
            case HALF:
                return R.dimen.group_row_half_padding;
        }

        return 0;
    }

}
