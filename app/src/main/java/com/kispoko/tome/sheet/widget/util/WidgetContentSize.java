
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Widget Content Size
 */
public enum WidgetContentSize implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    VERY_SMALL,
    SMALL,
    MEDIUM_SMALL,
    MEDIUM,
    MEDIUM_LARGE,
    LARGE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

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
     * @throws YamlParseException
     */
    public static WidgetContentSize fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull()) return MEDIUM;

        String sizeString = yaml.getString();
        try {
            return WidgetContentSize.fromString(sizeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(sizeString));
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


    // API
    // ------------------------------------------------------------------------------------------

    public int resourceId()
    {
        switch (this)
        {
            case VERY_SMALL:
                return R.dimen.text_size_very_small;
            case SMALL:
                return R.dimen.text_size_small;
            case MEDIUM_SMALL:
                return R.dimen.text_size_medium_small;
            case MEDIUM:
                return R.dimen.text_size_medium;
            case MEDIUM_LARGE:
                return R.dimen.text_size_medium_large;
            case LARGE:
                return R.dimen.text_size_large;
        }

        return 0;
    }


    public int labelResourceId()
    {
        switch (this)
        {
            case VERY_SMALL:
                return R.dimen.label_size_very_small;
            case SMALL:
                return R.dimen.label_size_small;
            case MEDIUM_SMALL:
                return R.dimen.label_size_medium_small;
            case MEDIUM:
                return R.dimen.label_size_medium;
            case MEDIUM_LARGE:
                return R.dimen.label_size_medium_large;
            case LARGE:
                return R.dimen.label_size_large;
        }

        return 0;
    }

}
