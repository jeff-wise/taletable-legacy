
package com.kispoko.tome.model.sheet.widget;


import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;



/**
 * Widget Type
 */
public enum WidgetType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    ACTION,
    BOOLEAN,
    BUTTON,
    EXPANDER,
    IMAGE,
    LIST,
    LOG,
    MECHANIC,
    NUMBER,
    OPTION,
    QUOTE,
    TABLE,
    TAB,
    TEXT;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(WidgetType.class, typeString);
    }


    public static WidgetType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return WidgetType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static WidgetType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            WidgetType widgetType = WidgetType.fromString(enumString);
            return widgetType;
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


    // LABEL
    // ------------------------------------------------------------------------------------------

    public int stringLabelResourceId()
    {
        switch (this)
        {
            case ACTION:
                return R.string.widget_action;
            case BOOLEAN:
                return R.string.widget_boolean;
            case EXPANDER:
                return R.string.widget_expander;
            case IMAGE:
                return R.string.widget_image;
            case LIST:
                return R.string.widget_list;
            case LOG:
                return R.string.widget_log;
            case MECHANIC:
                return R.string.widget_mechanic;
            case NUMBER:
                return R.string.widget_number;
            case OPTION:
                return R.string.widget_option;
            case QUOTE:
                return R.string.widget_quote;
            case TAB:
                return R.string.widget_tab;
            case TABLE:
                return R.string.widget_table;
            case TEXT:
                return R.string.widget_text;
        }

        return 0;
    }


}
