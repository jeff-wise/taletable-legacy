
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
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.number.NumberWidgetFormat;
import com.kispoko.tome.sheet.widget.number.NumberWidgetStyle;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.InlineLabelPosition;
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
        return this.widgetView(rowHasLabel, context);
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
        try {
            return this.valueVariable().value();
        }
        catch (NullVariableException exception) {
            ApplicationFailure.nullVariable(exception);
            return 0;
        }
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

    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = widgetViewLayout(rowHasLabel, context);

        // > Label View
        if (this.data().format().label() != null) {
            layout.addView(this.labelView(context));
        }

        // > Value
        LinearLayout valueLayout = valueLayout(context);
        layout.addView(valueLayout);

        if (this.format().labelPosition() == InlineLabelPosition.TOP)
            valueLayout.addView(this.valueTopLabelView(context));

        valueLayout.addView(this.valueMainView(context));

        return layout;
    }


    private LinearLayout widgetViewLayout(boolean rowHasLabel, Context context)
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


    private LinearLayout valueLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.backgroundResource   = this.data().format().background()
                                          .resourceId(this.data().format().corners(),
                                                      this.format().size());

        return layout.linearLayout(context);
    }


    private LinearLayout valueTopLabelView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.child(label);

        // [3] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = this.format().label();
        label.color                 = this.format().tint().labelResourceId();
        label.size                  = R.dimen.widget_label_text_size;
        label.font                  = Font.serifFontRegular(context);

        switch (this.format().size())
        {
            case VERY_SMALL:
                label.margin.bottom = R.dimen.widget_label_inline_top_margin_bottom_small;
            case SMALL:
                label.margin.bottom = R.dimen.widget_label_inline_top_margin_bottom_small;
            case MEDIUM_SMALL:
                label.margin.bottom = R.dimen.widget_label_inline_top_margin_bottom_medium;
            case MEDIUM:
                label.margin.bottom = R.dimen.widget_label_inline_top_margin_bottom_medium;
            case MEDIUM_LARGE:
                label.margin.bottom = R.dimen.widget_label_inline_top_margin_bottom_large;
            case LARGE:
                label.margin.bottom = R.dimen.widget_label_inline_top_margin_bottom_large;
        }

        return layout.linearLayout(context);
    }


    private LinearLayout valueMainView(Context context)
    {
        LinearLayout layout = valueMainViewLayout(context);

        if (this.format().label() != null &&
            this.format().labelPosition() == InlineLabelPosition.LEFT) {
            layout.addView(valueLeftLabelView(context));
        }

        layout.addView(valueTextView(context));

        if (!this.postfixVariable.isNull())
            layout.addView(valuePostfixview(context));

        return layout;
    }


    private LinearLayout valueMainViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        // > Gravity
        switch (this.data().format().alignment())
        {
            case LEFT:
                layout.layoutGravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case CENTER:
                layout.layoutGravity = Gravity.CENTER;
                layout.gravity = Gravity.CENTER;
                break;
            case RIGHT:
                layout.layoutGravity = Gravity.END  | Gravity.CENTER_VERTICAL;
                break;
        }

        return layout.linearLayout(context);
    }


    private TextView valueTextView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        this.valueViewId   = Util.generateViewId();

        value.id            = this.valueViewId;

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        // > Gravity
        switch (this.data().format().alignment())
        {
            case LEFT:
                value.gravity = Gravity.START;
                break;
            case CENTER:
                value.gravity = Gravity.CENTER;
                break;
            case RIGHT:
                value.gravity = Gravity.END;
                break;
        }

        if (this.format().style() == NumberWidgetStyle.PURPLE_CIRCLE)
            value.backgroundResource = this.format().style().resourceId();

        value.size          = this.format().size().resourceId();
        value.font          = Font.serifFontRegular(context);
        value.color         = this.format().tint().resourceId();
        value.text          = this.valueString();

        return value.textView(context);
    }


    private TextView valueLeftLabelView(Context context)
    {
        TextViewBuilder label   = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = this.format().label();
        label.font              = Font.serifFontRegular(context);
        label.color             = this.format().tint().labelResourceId();
        label.size              = this.format().size().labelResourceId();

        label.margin.right      = R.dimen.widget_label_inline_margin_right;

        return label.textView(context);
    }


    private TextView valuePostfixview(Context context)
    {
        TextViewBuilder postfix = new TextViewBuilder();

        this.postfixViewId = Util.generateViewId();

        postfix.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        postfix.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        postfix.id              = this.postfixViewId;

        postfix.text            = this.postfix();
        postfix.size            = this.format().size().resourceId();
        postfix.font            = Font.serifFontRegular(context);
        postfix.color           = R.color.dark_blue_hl_8;

        return postfix.textView(context);
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

        if (this.format().style() == NumberWidgetStyle.PURPLE_CIRCLE)
            label.margin.bottom     = R.dimen.widget_label_margin_bottom_circle;
        else
            label.margin.bottom     = R.dimen.widget_label_margin_bottom;

        return label.textView(context);
    }

}
