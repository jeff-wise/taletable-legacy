
package com.kispoko.tome.sheet.widget.action;


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
 * Action Size
 */
public enum ActionSize implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    SMALL,
    MEDIUM,
    LARGE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ActionSize fromString(String sizeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(ActionSize.class, sizeString);
    }


    public static ActionSize fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String sizeString = yaml.getString();

        try {
            return ActionSize.fromString(sizeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(sizeString));
        }
    }


    public static ActionSize fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            ActionSize size = ActionSize.fromString(enumString);
            return size;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Text Tint's yaml string representation.
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
            case SMALL:
                return R.dimen.widget_action_text_size_small;
            case MEDIUM:
                return R.dimen.widget_action_text_size_medium;
            case LARGE:
                return R.dimen.widget_action_text_size_large;
        }

        return 0;
    }


}
