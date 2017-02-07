
package com.kispoko.tome.sheet.widget.number;


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
 * Number Widget Style
 */
public enum NumberWidgetStyle implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    NONE,
    PURPLE_CIRCLE,
    GREEN_CIRCLE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static NumberWidgetStyle fromString(String styleString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(NumberWidgetStyle.class, styleString);
    }


    public static NumberWidgetStyle fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String styleString = yaml.getString();
        try {
            return NumberWidgetStyle.fromString(styleString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(styleString));
        }
    }


    public static NumberWidgetStyle fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            NumberWidgetStyle style = NumberWidgetStyle.fromString(enumString);
            return style;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Content Alignment's yaml string representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }


    // RESOURCE ID
    // ------------------------------------------------------------------------------------------

    public Integer resourceId()
    {
        switch (this)
        {
            case PURPLE_CIRCLE:
                return R.drawable.bg_widget_purple_circle;
        }

        return 0;
    }

}
