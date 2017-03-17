
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.BooleanVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.bool.BooleanWidgetFormat;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

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
    private ModelFunctor<BooleanWidgetFormat>   format;
    private ModelFunctor<BooleanVariable>       valueVariable;

    private PrimitiveFunctor<String>            onText;
    private PrimitiveFunctor<String>            offText;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                             valueViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanWidget()
    {
        this.id             = null;

        this.widgetData     = ModelFunctor.empty(WidgetData.class);
        this.format         = ModelFunctor.empty(BooleanWidgetFormat.class);
        this.valueVariable  = ModelFunctor.empty(BooleanVariable.class);

        this.onText         = new PrimitiveFunctor<>(null, String.class);
        this.offText        = new PrimitiveFunctor<>(null, String.class);

        this.valueViewId    = null;
    }


    public BooleanWidget(UUID id,
                         WidgetData widgetData,
                         BooleanWidgetFormat format,
                         BooleanVariable valueVariable,
                         String onText,
                         String offText)
    {
        this.id             = id;

        this.widgetData     = ModelFunctor.full(widgetData, WidgetData.class);
        this.format         = ModelFunctor.full(format, BooleanWidgetFormat.class);
        this.valueVariable  = ModelFunctor.full(valueVariable, BooleanVariable.class);

        this.onText         = new PrimitiveFunctor<>(onText, String.class);
        this.offText        = new PrimitiveFunctor<>(offText, String.class);

        this.valueViewId    = null;

        this.initializeBooleanWidget();
    }


    public static BooleanWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                id         = UUID.randomUUID();

        WidgetData          widgetData = WidgetData.fromYaml(yaml.atKey("data"));
        BooleanWidgetFormat format     = BooleanWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        BooleanVariable     value      = BooleanVariable.fromYaml(yaml.atKey("value"));

        String            onText     = yaml.atMaybeKey("on_text").getString();
        String            offText    = yaml.atMaybeKey("off_text").getString();

        return new BooleanWidget(id, widgetData, format, value, onText, offText);
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
        this.initializeBooleanWidget();
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    @Override
    public void initialize(GroupParent groupParent)
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
    public View view(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = viewLayout(rowHasLabel, context);

        // > Label View
//        if (this.data().format().label() != null) {
//            layout.addView(this.labelView(context));
//        }

        // > Value
        final TextView valueView = valueView(context);

        valueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (value()) {
                    setValue(false);
                    valueView.setText(offText());
                }
                else {
                    setValue(true);
                    valueView.setText(onText());
                }
            }
        });

        layout.addView(valueView);

        return layout;
    }


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
                .putYaml("value", this.valueVariable());
    }


    // > State
    // ------------------------------------------------------------------------------------------

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


    public void setValue(Boolean value)
    {
        this.valueVariable().setValue(value);
    }


    /**
     * The text displayed when the widget's value is true.
     * @return The on text.
     */
    public String onText()
    {
        return this.onText.getValue();
    }


    /**
     * The text displayed when the widget's value is false.
     * @return The off text.
     */
    public String offText()
    {
        return this.offText.getValue();
    }


    /**
     * The Boolean Widget format settings.
     * @return The boolean widget format.
     */
    public BooleanWidgetFormat format()
    {
        return this.format.getValue();
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeBooleanWidget()
    {
        // [1] Apply default format values
        // -------------------------------------------------------------------------------------

        // ** Content Alignment
        if (this.data().format().alignmentIsDefault())
            this.data().format().setAlignment(Alignment.CENTER);

        // ** Label Style
        if (this.data().format().labelStyle() == null) {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_DARK,
                                                        TextSize.SMALL,
                                                        Alignment.CENTER);
            this.data().format().setLabelStyle(defaultLabelStyle);
        }

        // ** Background
        if (this.data().format().backgroundIsDefault())
            this.data().format().setBackground(BackgroundColor.DARK);

        // ** Corners
        if (this.data().format().cornersIsDefault())
            this.data().format().setCorners(Corners.SMALL);
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout viewLayout(boolean rowHasLabel, Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.width                = 0;
        layout.weight               = this.data().format().width().floatValue();
        layout.gravity              = Gravity.CENTER;

        layout.margin.left          = R.dimen.widget_margin_horz;
        layout.margin.right         = R.dimen.widget_margin_horz;

        if (this.data().format().label() == null && rowHasLabel) {
            layout.padding.top      = R.dimen.widget_label_fill_padding;
        }


        return layout.linearLayout(context);
    }


    private TextView valueView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.font                  = Font.serifFontRegular(context);
        value.size                  = this.format().size().resourceId();

        value.backgroundResource    = this.data().format().background()
                                          .resourceId(this.data().format().corners());

        value.gravity               = Gravity.CENTER;
        value.layoutGravity         = Gravity.CENTER;

        if (this.value())
        {
            value.text  = this.onText();
            value.color =  R.color.dark_blue_hl_1;
        }
        else
        {
            value.text  = this.offText();
            value.color =  R.color.dark_blue_hl_6;
        }

        return value.textView(context);
    }


    private TextView labelView(Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = this.data().format().label();
        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.dark_blue_1;
        label.size              = R.dimen.widget_label_text_size;

        label.margin.bottom     = R.dimen.widget_label_margin_bottom;

        return label.textView(context);
    }


    // > Value Updates
    // -----------------------------------------------------------------------------------------

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
