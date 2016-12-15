
package com.kispoko.tome.sheet.widget.util;


import android.content.Context;

import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Widget Content Size
 */
public enum WidgetContentSize
{

    SMALL,
    MEDIUM,
    LARGE;


    public static WidgetContentSize fromString(String sizeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetContentSize.class, sizeString);
    }


    /**
     * Creates a Size enum from its Yaml representation. If there is no Yaml representation
     * (it is null), then use MEDIUM as a default size.
     * @param yaml The Yaml parser.
     * @return A new Size enum, with MEDIUM as the default.
     * @throws YamlException
     */
    public static WidgetContentSize fromYaml(Yaml yaml)
                  throws YamlException
    {
        if (yaml.isNull()) return MEDIUM;

        String sizeString = yaml.getString();
        try {
            return WidgetContentSize.fromString(sizeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(sizeString));
        }
    }


    public static WidgetContentSize fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetContentSize size = WidgetContentSize.fromString(enumString);
            return size;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    public float toSP(Context context)
    {
        switch (this)
        {
            case SMALL:
                return context.getResources().getDimension(R.dimen.text_size_small);
            case MEDIUM:
                return context.getResources().getDimension(R.dimen.text_size_medium);
            case LARGE:
                return context.getResources().getDimension(R.dimen.text_size_large);
        }
        return 0;
    }


    public int resourceId()
    {
        switch (this)
        {
            case SMALL:
                return R.dimen.text_size_small;
            case MEDIUM:
                return R.dimen.text_size_medium;
            case LARGE:
                return R.dimen.text_size_large;
        }

        return 0;
    }


}
