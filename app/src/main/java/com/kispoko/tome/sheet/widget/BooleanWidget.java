
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.BooleanVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Boolean WidgetData
 */
public class BooleanWidget extends Widget
                           implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<WidgetData>            widgetData;
    private PrimitiveFunctor<WidgetContentSize> size;
    private ModelFunctor<BooleanVariable>       valueVariable;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                             valueViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanWidget()
    {
        this.id             = null;

        this.widgetData     = ModelFunctor.empty(WidgetData.class);
        this.size           = new PrimitiveFunctor<>(null, WidgetContentSize.class);
        this.valueVariable  = ModelFunctor.empty(BooleanVariable.class);

        this.valueViewId    = null;
    }


    public BooleanWidget(UUID id,
                         WidgetData widgetData,
                         WidgetContentSize size,
                         BooleanVariable valueVariable)
    {
        this.id             = id;

        this.widgetData     = ModelFunctor.full(widgetData, WidgetData.class);
        this.size           = new PrimitiveFunctor<>(size, WidgetContentSize.class);
        this.valueVariable  = ModelFunctor.full(valueVariable, BooleanVariable.class);

        this.valueViewId    = null;
    }


    public static BooleanWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
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
    public void onLoad() { }


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    @Override
    public void initialize()
    {
        // > If the variable is non-null
        if (!this.valueVariable.isNull()) {
            this.valueVariable().initialize();
        }

        // > If the variable has a non-null value
        if (!this.valueVariable.isNull())
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


    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    @Override
    public void runAction(Action action) { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Boolean Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data())
                .putYaml("size", this.size())
                .putYaml("value", this.valueVariable());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The widget content size.
     * @return The Widget Content Size.
     */
    public WidgetContentSize size()
    {
        return this.size.getValue();
    }

    /**
     * Get the BooleanWidget's value variable (of type boolean).
     * @return The Variable for the BoolenWidget value.
     */
    public BooleanVariable valueVariable()
    {
        return this.valueVariable.getValue();
    }


    public Boolean value()
    {
        if (!this.valueVariable.isNull())
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
