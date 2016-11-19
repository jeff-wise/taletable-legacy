
package com.kispoko.tome.sheet.widget;


import android.content.Context;

import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Widget
 */
public abstract class Widget implements Model
{


   // abstract public View getDisplayView(Context context, Rules rules);
    //abstract public View getEditorView(Context context, Rules rules);

    abstract public void runAction(String actionName, Context context, Rules rules);
    abstract public String name();

    abstract public WidgetData data();

//    abstract public void save(UUID callerTrackerId);
//    abstract public void load(UUID callerTrackerId);



    public static Widget fromYaml(Yaml yaml)
                  throws YamlException
    {
        Type widgetType = Type.fromYaml(yaml.atKey("type"));

        switch (widgetType) {
            case TEXT:
                return TextWidget.fromYaml(yaml);
            case NUMBER:
                return NumberWidget.fromYaml(yaml);
            case BOOLEAN:
                return BooleanWidget.fromYaml(yaml);
            case IMAGE:
                return ImageWidget.fromYaml(yaml);
            case TABLE:
                return TableWidget.fromYaml(yaml);
        }

        // SHOULD NOT REACH HERE. If Widget.Type is null, then an InvalidDataException
        // should be thrown.
        return null;
    }


    public enum Type
    {
        TEXT,
        NUMBER,
        BOOLEAN,
        IMAGE,
        TABLE;


        public static Type fromString(String typeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Type.class, typeString);
        }


        public static Type fromYaml(Yaml yaml)
                      throws YamlException
        {
            String typeString = yaml.getString();
            try {
                return Type.fromString(typeString);
            } catch (InvalidDataException e) {
                throw new YamlException(new InvalidEnumError(typeString),
                                        YamlException.Type.INVALID_ENUM);
            }
        }

    }


}
