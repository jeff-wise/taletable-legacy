
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.rules.programming.variable.Variable;
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
    private ModelValue<Variable>              value;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanWidget(UUID id, WidgetData widgetData, WidgetFormat.Size size, Variable value)
    {
        this.id = id;

        this.widgetData = new ModelValue<>(widgetData, this, WidgetData.class);
        this.size       = new PrimitiveValue<>(size, this, WidgetFormat.Size.class);
        this.value      = new ModelValue<>(value, this, Variable.class);
    }


    public static BooleanWidget fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID              id         = UUID.randomUUID();
        WidgetData        widgetData = WidgetData.fromYaml(yaml.atKey("data"));
        WidgetFormat.Size size       = WidgetFormat.Size.fromYaml(yaml.atKey("size"));
        Variable          value      = Variable.fromYaml(yaml.atKey("value"));

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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


    // > Widget
    // ------------------------------------------------------------------------------------------

    public String name() {
        return "text";
    }


    public void runAction(String actionName, Context context, Rules rules) { }


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

    public View getDisplayView(Context context, Rules rules)
    {
        TextView view = new TextView(context);

        view.setText(this.getValue().toString());

        return view;
    }

    public View getEditorView(Context context, Rules rules) {
        return new TextView(context);
    }

}
