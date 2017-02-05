
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.number.NumberWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetBackground;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Widget: Number
 */
public class NumberWidget extends Widget
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
    private ModelFunctor<NumberWidgetFormat>    format;
    private ModelFunctor<NumberVariable>        valueVariable;
    private PrimitiveFunctor<String>            valuePrefix;
    private PrimitiveFunctor<String>            valuePostfix;
    private ModelFunctor<TextVariable>          prefixVariable;
    private ModelFunctor<TextVariable>          postfixVariable;
    private CollectionFunctor<VariableUnion>    variables;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                             valueViewId;
    private Integer                             prefixViewId;
    private Integer                             postfixViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberWidget()
    {
        this.id                 = null;

        this.widgetData         = ModelFunctor.empty(WidgetData.class);
        this.format             = ModelFunctor.empty(NumberWidgetFormat.class);
        this.valueVariable      = ModelFunctor.empty(NumberVariable.class);
        this.valuePrefix        = new PrimitiveFunctor<>(null, String.class);
        this.valuePostfix       = new PrimitiveFunctor<>(null, String.class);
        this.prefixVariable     = ModelFunctor.empty(TextVariable.class);
        this.postfixVariable    = ModelFunctor.empty(TextVariable.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.empty(variableClasses);

        this.valueViewId    = null;
        this.prefixViewId   = null;
        this.postfixViewId  = null;
    }


    public NumberWidget(UUID id,
                        WidgetData widgetData,
                        NumberWidgetFormat format,
                        NumberVariable valueVariable,
                        String valuePrefix,
                        String valuePostfix,
                        TextVariable prefixVariable,
                        TextVariable postfixVariable,
                        List<VariableUnion> variables)
    {
        this.id                 = id;

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
        this.format             = ModelFunctor.full(format, NumberWidgetFormat.class);
        this.valueVariable      = ModelFunctor.full(valueVariable, NumberVariable.class);
        this.valuePrefix        = new PrimitiveFunctor<>(valuePrefix, String.class);
        this.valuePostfix       = new PrimitiveFunctor<>(valuePostfix, String.class);
        this.prefixVariable     = ModelFunctor.full(prefixVariable, TextVariable.class);
        this.postfixVariable    = ModelFunctor.full(postfixVariable, TextVariable.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.full(variables, variableClasses);

        this.valueViewId    = null;
        this.prefixViewId   = null;
        this.postfixViewId  = null;
    }


    public static NumberWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID               id            = UUID.randomUUID();

        WidgetData         widgetData    = WidgetData.fromYaml(yaml.atKey("data"));
        NumberWidgetFormat format        = NumberWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        NumberVariable     value         = NumberVariable.fromYaml(yaml.atKey("value"));
        String             valuePrefix   = yaml.atMaybeKey("value_prefix").getString();
        String             valuePostfix  = yaml.atMaybeKey("value_postfix").getString();
        TextVariable       prefix        = TextVariable.fromYaml(yaml.atMaybeKey("prefix"));
        TextVariable       postfix       = TextVariable.fromYaml(yaml.atMaybeKey("postfix"));

        List<VariableUnion> variables   = yaml.atMaybeKey("variables").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new NumberWidget(id, widgetData, format, value, valuePrefix, valuePostfix,
                                prefix, postfix, variables);
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
     * This method is called when the Number Widget is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data())
                .putYaml("format", this.format())
                .putYaml("value", this.valueVariable())
                .putString("value_prefix", this.valuePrefix())
                .putString("value_postfix", this.valuePostfix())
                .putYaml("prefix", this.prefixVariable())
                .putYaml("postfix", this.postfixVariable())
                .putList("variables", this.variables());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    @Override
    public void initialize()
    {
        // [1] Initialize variables with listeners to update the number widget views when the
        //     values of the variables change
        // --------------------------------------------------------------------------------------

        // ** Value
        // --------------------------------------------------------------------------------------

        if (!this.valueVariable.isNull()) {
            this.valueVariable().initialize();
        }

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

        // ** Prefix
        // --------------------------------------------------------------------------------------

        if (!this.prefixVariable.isNull()) {
            this.prefixVariable().initialize();
        }

        if (!this.prefixVariable.isNull())
        {
            this.prefixVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onPrefixUpdate();
                }
            });

            State.addVariable(this.prefixVariable());
        }

        // ** Postfix
        // --------------------------------------------------------------------------------------

        if (!this.postfixVariable.isNull()) {
            this.postfixVariable().initialize();
        }

        if (!this.postfixVariable.isNull())
        {
            this.postfixVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onPostfixUpdate();
                }
            });

            State.addVariable(this.postfixVariable());
        }

        // [2] Initialize the helper variables
        // --------------------------------------------------------------------------------------

        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }

    }


    /**
     * Get the widget's common data values.
     * @return The widget's WidgetData.
     */
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
        if (this.data().format().label() != null) {
            layout.addView(this.labelView(context));
        }

        // > Value
        layout.addView(this.valueView(context));

        return layout;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The Number Widget Format.
     * @return The Number Widget Format.
     */
    public NumberWidgetFormat format()
    {
        return this.format.getValue();
    }


    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the number widget's value variable.
     * @return The number variable.
     */
    public NumberVariable valueVariable()
    {
        return this.valueVariable.getValue();
    }


    /**
     * Get the number widget's value (from its value variable).
     * @return The integer value.
     */
    public Integer value()
    {
        Integer integerValue = null;

        try {
            integerValue = this.valueVariable().value();
        }
        catch (VariableException exception) {
            ApplicationFailure.variable(exception);
        }

        return integerValue;
    }


    /**
     * Get the number widget's value as a string.
     * @return The value string.
     */
    public String valueString()
    {
        Integer value = this.value();
        if (value != null)
        {
            StringBuilder valueString = new StringBuilder();

            if (!this.valuePrefix.isNull())
                valueString.append(this.valuePrefix());

            valueString.append(Integer.toString(value));

            if (!this.valuePostfix.isNull())
                valueString.append(this.valuePostfix());

            return valueString.toString();
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the value prefix (may be null).
     * @return The value prefix string.
     */
    public String valuePrefix()
    {
        return this.valuePrefix.getValue();
    }


    /**
     * Get the value postfix (may be null).
     * @return The value postfix string.
     */
    public String valuePostfix()
    {
        return this.valuePostfix.getValue();
    }


    // ** Variables
    // ------------------------------------------------------------------------------------------

    /**
     * Get the text widget's helper variables.
     * @return The list of variables.
     */
    public List<VariableUnion> variables()
    {
        return this.variables.getValue();
    }


    // ** Prefix
    // ------------------------------------------------------------------------------------------

    /**
     * Get the number widget's prefix variable.
     * @return The text variable.
     */
    public TextVariable prefixVariable()
    {
        return this.prefixVariable.getValue();
    }


    /**
     * Get the number widget's prefix value (from its prefix variable).
     * @return The prefix (may be null).
     */
    public String prefix()
    {
        if (this.prefixVariable() != null)
            return this.prefixVariable().value();
        else
            return null;
    }


    // ** Postfix
    // ------------------------------------------------------------------------------------------

    /**
     * Get the postfixVariable Text Variable. The postfixVariable contains any text that comes after
     * the main value.
     * @return The postfixVariable Text Variable.
     */
    public TextVariable postfixVariable()
    {
        return this.postfixVariable.getValue();
    }


    /**
     * Get the number widget's postfixVariable value (from its postfixVariable variablbe).
     * @return The postfixVariable (may be null).
     */
    public String postfix()
    {
        if (this.postfixVariable() != null)
            return this.postfixVariable().value();
        else
            return null;
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Value Updates
    // ------------------------------------------------------------------------------------------

    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        if (this.valueViewId != null && !this.valueVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            if (textView == null)
                return;

            Integer value = this.value();

            // TODO can value be null
            if (value != null)
                textView.setText(this.valueString());
        }
    }


    /**
     * When the text widget's prefix is updated.
     */
    private void onPrefixUpdate()
    {
        if (this.prefixViewId != null && !this.prefixVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.prefixViewId);

            String prefixValue = this.prefix();

            if (prefixValue != null)
                textView.setText(prefixValue);
        }
    }


    /**
     * When the text widget's postfixVariable is updated.
     */
    private void onPostfixUpdate()
    {
        if (this.postfixViewId != null && !this.postfixVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.postfixViewId);

            String postfixValue = this.postfix();

            // TODO why is textView null on hotswapping??
            if (postfixValue != null && textView != null)
                textView.setText(postfixValue);
        }
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout viewLayout(boolean rowHasLabel, Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = 0;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight           = this.data().format().width().floatValue();

        if (this.data().format().label() == null && rowHasLabel) {
            layout.padding.top      = R.dimen.widget_label_fill_padding;
        }


        layout.margin.left      = R.dimen.widget_margin_horz;
        layout.margin.right     = R.dimen.widget_margin_horz;

        return layout.linearLayout(context);
    }


    private LinearLayout valueView(Context context)
    {
        // [1] Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
        TextViewBuilder     value   = new TextViewBuilder();
        TextViewBuilder     label   = new TextViewBuilder();
        TextViewBuilder     postfix = new TextViewBuilder();

        this.valueViewId   = Util.generateViewId();
        this.postfixViewId = Util.generateViewId();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        if (this.data().format().background() == WidgetBackground.PURPLE_CIRCLE)
            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        else
            layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = this.data().format().background()
                                          .resourceId(this.data().format().corners());

        // > Content Alignment
        switch (this.data().format().alignment())
        {
            case LEFT:
                layout.gravity  = Gravity.START | Gravity.CENTER_VERTICAL;
                value.gravity   = Gravity.START;
                break;
            case CENTER:
                layout.gravity  = Gravity.CENTER;
                layout.layoutGravity = Gravity.CENTER;
                value.gravity  = Gravity.CENTER_HORIZONTAL;
                break;
            case RIGHT:
                layout.gravity  = Gravity.END | Gravity.CENTER_VERTICAL;
                value.gravity   = Gravity.END;
                break;
        }

        if (this.format().inlineLabel() != null)
            layout.child(label);

        layout.child(value);

        if (!this.postfixVariable.isNull())
            layout.child(postfix);

        // [3 A] Value
        // --------------------------------------------------------------------------------------

        value.id            = this.valueViewId;

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.size          = this.format().size().resourceId();
        value.font          = Font.serifFontRegular(context);
        value.color         = this.format().tint().resourceId();
        value.text          = this.valueString();

        // [3 B] Label
        // --------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = this.format().inlineLabel();
        label.font              = Font.serifFontRegular(context);
        label.color             = this.format().tint().labelResourceId();
        label.size              = this.format().size().labelResourceId();

        label.margin.right      = R.dimen.widget_label_inline_margin_right;

        // [3 C] Postfix
        // --------------------------------------------------------------------------------------

        postfix.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        postfix.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        postfix.id    = this.postfixViewId;

        postfix.text  = this.postfix();
        postfix.size  = this.format().size().resourceId();
        postfix.font  = Font.serifFontRegular(context);
        postfix.color = R.color.dark_blue_hl_8;


        return layout.linearLayout(context);
    }


    private TextView labelView(Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.MATCH_PARENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.gravity           = this.data().format().labelAlignment().gravityConstant();

        label.text              = this.data().format().label();
        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.dark_blue_hl_8;
        label.size              = R.dimen.widget_label_text_size;

        if (this.data().format().background() == WidgetBackground.PURPLE_CIRCLE)
            label.margin.bottom     = R.dimen.widget_label_margin_bottom_circle;
        else
            label.margin.bottom     = R.dimen.widget_label_margin_bottom;

        return label.textView(context);
    }

}
