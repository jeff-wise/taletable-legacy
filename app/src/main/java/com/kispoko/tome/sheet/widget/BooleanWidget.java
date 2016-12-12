
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.engine.programming.variable.BooleanVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.util.Util;
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

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                              id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<WidgetData>            widgetData;
    private PrimitiveValue<WidgetFormat.Size> size;
    private ModelValue<BooleanVariable>       value;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                           valueViewId;


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

        initialize();
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
    public void onLoad()
    {
        initialize();
    }


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

    public void runAction(Action action) { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the BooleanWidget's value variable (of type boolean).
     * @return The Variable for the BoolenWidget value.
     */
    public BooleanVariable value()
    {
        return this.value.getValue();
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View view()
    {
        Context context = SheetManager.currentSheetContext();

        TextView view = new TextView(context);

        this.valueViewId = Util.generateViewId();
        view.setId(this.valueViewId);

        view.setText(this.value().value().toString());

        return view;
    }


    public View getEditorView(Context context, RulesEngine rulesEngine) {
        return new TextView(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    private void initialize()
    {
        // [1] Initialize variables with listeners to update the number widget views when the
        //     values of the variables change
        // --------------------------------------------------------------------------------------

        this.valueViewId   = null;

        this.value().addOnUpdateListener(new Variable.OnUpdateListener() {
            @Override
            public void onUpdate() {
                onValueUpdate();
            }
        });
    }


    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        if (this.valueViewId != null)
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            Boolean value = this.value().value();

            // TODO can value be null
            if (value != null)
                textView.setText(Boolean.toString(value));
        }
    }


}
