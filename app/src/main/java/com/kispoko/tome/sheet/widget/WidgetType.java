
package com.kispoko.tome.sheet.widget;


import com.kispoko.tome.R;
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
 * Widget Type
 */
public enum WidgetType implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    TEXT,
    NUMBER,
    BOOLEAN,
    IMAGE,
    LIST,
    TABLE,
    ACTION,
    ADDER,
    MECHANIC,
    LOG;


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
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
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
            case TEXT:
                return R.string.widget_text;
            case NUMBER:
                return R.string.widget_number;
            case BOOLEAN:
                return R.string.widget_boolean;
            case LIST:
                return R.string.widget_list;
            case TABLE:
                return R.string.widget_table;
            case IMAGE:
                return R.string.widget_image;
            case ACTION:
                return R.string.widget_action;
            case ADDER:
                return R.string.widget_adder;
            case MECHANIC:
                return R.string.widget_mechanic;
            case LOG:
                return R.string.widget_log;
        }

        return 0;
    }


}
