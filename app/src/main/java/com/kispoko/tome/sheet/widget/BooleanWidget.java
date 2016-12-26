
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.programming.variable.BooleanVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
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

    private ModelFunctor<WidgetData> widgetData;
    private PrimitiveFunctor<WidgetContentSize> size;
    private ModelFunctor<BooleanVariable> value;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                           valueViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanWidget()
    {
        this.id         = null;

        this.widgetData = ModelFunctor.empty(WidgetData.class);
        this.size       = new PrimitiveFunctor<>(null, WidgetContentSize.class);
        this.value      = ModelFunctor.empty(BooleanVariable.class);
    }


    public BooleanWidget(UUID id,
                         WidgetData widgetData,
                         WidgetContentSize size,
                         BooleanVariable value)
    {
        this.id = id;

        this.widgetData = ModelFunctor.full(widgetData, WidgetData.class);
        this.size       = new PrimitiveFunctor<>(size, WidgetContentSize.class);
        this.value      = ModelFunctor.full(value, BooleanVariable.class);

        initialize();
    }


    public static BooleanWidget fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID              id         = UUID.randomUUID();
        WidgetData        widgetData = WidgetData.fromYaml(yaml.atKey("data"));
        WidgetContentSize size       = WidgetContentSize.fromYaml(yaml.atKey("size"));
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
    public BooleanVariable valueVariable()
    {
        return this.value.getValue();
    }


    public Boolean value()
    {
        if (!this.value.isNull())
            return this.valueVariable().value();
        return null;
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View tileView()
    {
        Context context = SheetManager.currentSheetContext();

        TextView view = new TextView(context);

        this.valueViewId = Util.generateViewId();
        view.setId(this.valueViewId);

        view.setText(this.value().toString());

        return view;
    }


    public View editorView(Context context)
    {
        return new TextView(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    private void initialize()
    {
        // [1] The text widget's value view ID. It is null until the view is created.
        // --------------------------------------------------------------------------------------

        this.valueViewId   = null;

        // [2] Initialize the value variable
        // --------------------------------------------------------------------------------------

        if (!this.value.isNull())
        {
            this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onValueUpdate();
                }
            });

            State.addVariable(this.valueVariable());
        }
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

            Boolean value = this.value();

            // TODO can value be null
            if (value != null)
                textView.setText(Boolean.toString(value));
        }
    }


}
