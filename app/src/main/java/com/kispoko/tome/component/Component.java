
package com.kispoko.tome.component;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.Map;

import static android.R.attr.type;


/**
 * Component
 */
public class Component implements Serializable
{

    public static enum Type
    {
        TEXT,
        DOCUMENT,
        INTEGER,
        IMAGE
    }

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    ComponentI component;
    Type _type;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Component(ComponentI component)
    {
        this.component = component;

        if (component instanceof Text) {
           this._type = Type.TEXT;
        } else if (component instanceof Image) {
           this._type = Type.IMAGE;
        } else if (component instanceof NumberInteger) {
            this._type = Type.INTEGER;
        } else if (component instanceof Document) {
            this._type = Type.DOCUMENT;
        }
    }


    public static Component fromYaml(Map<String, Object> componentYaml)
    {
        String componentType = (String) componentYaml.get("type");
        ComponentI componentI = null;

        switch (componentType)
        {
            case "text":
               componentI = Text.fromYaml(componentYaml);
               break;
            case "image":
                componentI = Image.fromYaml(componentYaml);
                break;
            case "integer":
                componentI = NumberInteger.fromYaml(componentYaml);
                break;
            case "document":
                componentI = Document.fromYaml(componentYaml);
                break;
        }

        return new Component(componentI);
    }

    // > API
    // ------------------------------------------------------------------------------------------


    public Type getType()
    {
        return this._type;
    }


    public String getName()
    {
        return this.component.getName();
    }


    public View getView(Context context)
    {
        return this.component.getView(context);
    }





    public static LinearLayout linearLayout(Context context)
    {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(linearLayoutParams);
        return layout;
    }


    // > INTERNAL
    // ------------------------------------------------------------------------------------------



}
