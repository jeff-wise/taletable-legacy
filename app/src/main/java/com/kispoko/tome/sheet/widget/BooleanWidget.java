
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.rules.programming.variable.BooleanVariable;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Boolean WidgetData
 */
public class BooleanWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                              id;

    private ModelValue<WidgetData>            widgetData;
    private PrimitiveValue<WidgetFormat.Size> size;
    private ModelValue<BooleanVariable>       value;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanWidget()
    {
        this.id         = null;

        this.widgetData = ModelValue.empty(WidgetData.class);
        this.size       = new PrimitiveValue<>(null, WidgetFormat.Size.class);
        this.value      = ModelValue.empty(BooleanVariable.class);
    }


    public BooleanWidget(UUID id,
                         WidgetData widgetData,
                         WidgetFormat.Size size,
                         BooleanVariable value)
    {
        this.id = id;

        this.widgetData = ModelValue.full(widgetData, WidgetData.class);
        this.size       = new PrimitiveValue<>(size, WidgetFormat.Size.class);
        this.value      = ModelValue.full(value, BooleanVariable.class);
    }


    public static BooleanWidget fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID              id         = UUID.randomUUID();
        WidgetData        widgetData = WidgetData.fromYaml(yaml.atKey("data"));
        WidgetFormat.Size size       = WidgetFormat.Size.fromYaml(yaml.atKey("size"));
        BooleanVariable   value      = BooleanVariable.fromYaml(yaml.atKey("value"));

        return new BooleanWidget(id, widgetData, size, value);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Boolean Widget is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Widget
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    public String name()
    {
        return "text";
    }


    // ** Data
    // ------------------------------------------------------------------------------------------

    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    // ** Run Action
    // ------------------------------------------------------------------------------------------

    public void runAction(String actionName, Context context, RulesEngine rulesEngine) { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the BooleanWidget's value variable (of type boolean).
     * @return The Variable for the BoolenWidget value.
     */
    public BooleanVariable getValue()
    {
        return this.value.getValue();
    }


//    public void setValue(Boolean value, Context context)
//    {
//        this.value = value;

//        if (context != null) {
//            TextView textView = (TextView) ((Activity) context)
//                                    .findViewById(this.displayTextViewId);
//            textView.setText(this.value);
//        }
//        this.save(null);
//    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context, RulesEngine rulesEngine)
    {
        TextView view = new TextView(context);

        view.setText(this.value.getValue().getBoolean().toString());

        return view;
    }

    public View getEditorView(Context context, RulesEngine rulesEngine) {
        return new TextView(context);
    }

}
