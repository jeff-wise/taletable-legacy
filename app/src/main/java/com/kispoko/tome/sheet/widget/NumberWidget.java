
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.sheet.dialog.SummationDialogFragment;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.number.NumberWidgetFormat;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.widget.util.WidgetBackground;
import com.kispoko.tome.sheet.widget.util.WidgetCorners;
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
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<WidgetData>            widgetData;
    private ModelFunctor<NumberWidgetFormat>    format;
    private ModelFunctor<NumberVariable>        valueVariable;
    private PrimitiveFunctor<String>            valuePrefix;
    private PrimitiveFunctor<String>            valuePostfix;
    private PrimitiveFunctor<String>            description;
    private CollectionFunctor<VariableUnion>    variables;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private Integer                             valueViewId;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberWidget()
    {
        this.id                 = null;

        this.widgetData         = ModelFunctor.empty(WidgetData.class);
        this.format             = ModelFunctor.empty(NumberWidgetFormat.class);
        this.valueVariable      = ModelFunctor.empty(NumberVariable.class);
        this.valuePrefix        = new PrimitiveFunctor<>(null, String.class);
        this.valuePostfix       = new PrimitiveFunctor<>(null, String.class);
        this.description        = new PrimitiveFunctor<>(null, String.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.empty(variableClasses);

        this.valueViewId    = null;
    }


    public NumberWidget(UUID id,
                        WidgetData widgetData,
                        NumberWidgetFormat format,
                        NumberVariable valueVariable,
                        String valuePrefix,
                        String valuePostfix,
                        String description,
                        List<VariableUnion> variables)
    {
        this.id                 = id;

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
        this.format             = ModelFunctor.full(format, NumberWidgetFormat.class);
        this.valueVariable      = ModelFunctor.full(valueVariable, NumberVariable.class);
        this.valuePrefix        = new PrimitiveFunctor<>(valuePrefix, String.class);
        this.valuePostfix       = new PrimitiveFunctor<>(valuePostfix, String.class);
        this.description        = new PrimitiveFunctor<>(description, String.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.full(variables, variableClasses);

        this.valueViewId    = null;

        this.initializeNumberWidget();
    }


    public static NumberWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID               id            = UUID.randomUUID();

        WidgetData         widgetData    = WidgetData.fromYaml(yaml.atMaybeKey("data"), false);
        NumberWidgetFormat format        = NumberWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        NumberVariable     value         = NumberVariable.fromYaml(yaml.atKey("value"));
        String             valuePrefix   = yaml.atMaybeKey("value_prefix").getString();
        String             valuePostfix  = yaml.atMaybeKey("value_postfix").getString();
        String             description   = yaml.atMaybeKey("description").getTrimmedString();

        List<VariableUnion> variables    = yaml.atMaybeKey("variables").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new NumberWidget(id, widgetData, format, value, valuePrefix, valuePostfix,
                                description, variables);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    // ** Id
    // -----------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // -----------------------------------------------------------------------------------------

    /**
     * This method is called when the Number Widget is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeNumberWidget();
    }


    // > To Yaml
    // -----------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data())
                .putYaml("format", this.format())
                .putYaml("value", this.valueVariable())
                .putString("value_prefix", this.valuePrefix())
                .putString("value_postfix", this.valuePostfix())
                .putString("description", this.description())
                .putList("variables", this.variables());
    }


    // > Widget
    // -----------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    @Override
    public void initialize(GroupParent groupParent)
    {
        // [1] Initialize variables with listeners to update the number widget views when the
        //     values of the variables change
        // -------------------------------------------------------------------------------------

        // ** Value
        // -------------------------------------------------------------------------------------

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


        // [2] Initialize the helper variables
        // -------------------------------------------------------------------------------------

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
    // -----------------------------------------------------------------------------------------

    // ** Format
    // -----------------------------------------------------------------------------------------

    /**
     * The Number Widget Format.
     * @return The Number Widget Format.
     */
    public NumberWidgetFormat format()
    {
        return this.format.getValue();
    }


    // ** Value
    // -----------------------------------------------------------------------------------------

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


    /**
     * The number description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    // ** Variables
    // -----------------------------------------------------------------------------------------

    /**
     * Get the text widget's helper variables.
     * @return The list of variables.
     */
    public List<VariableUnion> variables()
    {
        return this.variables.getValue();
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeNumberWidget()
    {
        // [1] Apply default format values
        // -------------------------------------------------------------------------------------

        // ** Width
        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        // ** Alignment
        if (this.data().format().alignment() == null)
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
        if (this.data().format().background() == null)
            this.data().format().setBackground(WidgetBackground.DARK);

        // ** Corners
        if (this.data().format().corners() == null)
            this.data().format().setCorners(WidgetCorners.SMALL);

    }


    // > Value Updates
    // -----------------------------------------------------------------------------------------

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


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = this.layout(rowHasLabel, context);

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
        label.color                 = this.format().labelStyle().color().resourceId();
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

        return layout;
    }


    private LinearLayout valueMainViewLayout(final Context context)
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

        layout.onClick              = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNumberWidgetShortClick(context);
            }
        };

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

        if (this.description() != null)
        {
            value.font          = Font.serifFontRegular(context);

            value.color     = this.format().descriptionStyle().color().resourceId();
            value.size      = this.format().descriptionStyle().size().resourceId();
            value.textSpan  = this.valueSpannableString(context);
        }
        else
        {
            value.text      = this.valueString();
            value.color     = this.format().valueStyle().color().resourceId();
            value.size      = this.format().valueStyle().size().resourceId();
            value.font      = this.format().valueStyle().typeface(context);
        }

        return value.textView(context);
    }


    private SpannableStringBuilder valueSpannableString(Context context)
    {
        String valueString = this.valueString();
        int valueStringLength = valueString.length();

        // > Get value index and remove placeholder
        int valueIndex = this.description().indexOf("<value>");

        StringBuilder stringBuilder = new StringBuilder(this.description().replace("<value>", ""));

        SpannableStringBuilder builder = new SpannableStringBuilder(stringBuilder.toString());

        if (valueIndex >= 0)
        {
            // > Format Value
            // (1) Insert number string
            builder.insert(valueIndex, valueString);
            stringBuilder.insert(valueIndex, valueString);

            // (2) Make the number bold
            if (this.format().valueStyle().isBold() && this.format().valueStyle().isItalic()) {
                StyleSpan valueBoldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                builder.setSpan(valueBoldItalicSpan, valueIndex, valueIndex + valueStringLength, 0);
            }
            else if (this.format().valueStyle().isBold()) {
                StyleSpan valueBoldSpan = new StyleSpan(Typeface.BOLD);
                builder.setSpan(valueBoldSpan, valueIndex, valueIndex + valueStringLength, 0);
            }
            else if (this.format().valueStyle().isItalic()) {
                StyleSpan valueItalicSpan = new StyleSpan(Typeface.ITALIC);
                builder.setSpan(valueItalicSpan, valueIndex, valueIndex + valueStringLength, 0);
            }

            // (3) Color the value
            builder.setSpan(this.format().valueStyle().color().foregroundColorSpan(context),
                            valueIndex, valueIndex + valueStringLength, 0);

            // (4) Size
            RelativeSizeSpan sizeSpan = this.format().valueStyle().size().relativeSizeSpan(
                                                this.format().descriptionStyle().size(), context);
            builder.setSpan(sizeSpan, valueIndex, valueIndex + valueStringLength, 0);
        }

        // > Format Label
        if (this.format().label() != null &&
            this.format().labelPosition() == InlineLabelPosition.TEXT)
        {
            int labelIndex = stringBuilder.indexOf(this.format().label());

            if (labelIndex >= 0)
            {
                int labelStringLength = this.format().label().length();

                // (1) Set Typeface
                if (this.format().labelStyle().isBold() &&
                    this.format().labelStyle().isItalic())
                {
                    StyleSpan labelBoldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                    builder.setSpan(labelBoldItalicSpan, labelIndex,
                                    labelIndex + labelStringLength, 0);
                }
                else if (this.format().labelStyle().isBold())
                {
                    StyleSpan labelBoldSpan = new StyleSpan(Typeface.BOLD);
                    builder.setSpan(labelBoldSpan, labelIndex, labelIndex + labelStringLength, 0);
                }
                else if (this.format().labelStyle().isItalic())
                {
                    StyleSpan labelItalicSpan = new StyleSpan(Typeface.ITALIC);
                    builder.setSpan(labelItalicSpan, labelIndex, labelIndex + labelStringLength, 0);
                }

                // (2) Set Color
                builder.setSpan(this.format().labelStyle().color().foregroundColorSpan(context),
                                labelIndex, labelIndex + labelStringLength, 0);

                // (3) Set Size
                RelativeSizeSpan labelSizeSpan =
                                this.format().labelStyle().size().relativeSizeSpan(
                                            this.format().descriptionStyle().size(), context);
                builder.setSpan(labelSizeSpan, labelIndex, labelIndex + labelStringLength, 0);
            }
        }

        return builder;
    }


    private TextView valueLeftLabelView(Context context)
    {
        TextViewBuilder label   = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = this.format().label();
        label.color             = this.format().labelStyle().color().resourceId();
        label.size              = this.format().labelStyle().size().resourceId();

        label.font              = this.format().labelStyle().typeface(context);

        label.margin.right      = R.dimen.widget_label_inline_margin_right;

        return label.textView(context);
    }


    private TextView labelView(Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.MATCH_PARENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.gravity           = this.data().format().labelStyle().alignment().gravityConstant();

        label.text              = this.data().format().label();
        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.dark_blue_hl_8;
        label.size              = R.dimen.widget_label_text_size;

        label.margin.bottom     = R.dimen.widget_label_margin_bottom;

        return label.textView(context);
    }


    // > Clicks
    // -----------------------------------------------------------------------------------------

    /**
     * When the number widget is clicked once, open a quick edit/view dialog.
     * @param context The context
     */
    private void onNumberWidgetShortClick(Context context)
    {
        SheetActivity sheetActivity = (SheetActivity) context;

        switch (this.valueVariable().kind())
        {

            // OPEN the summation preview dialog
            case SUMMATION:
                Summation summation      = this.valueVariable().summation();
                String    summationLabel = this.valueVariable().label();
                SummationDialogFragment summationDialog =
                                    SummationDialogFragment.newInstance(summation, summationLabel);
                summationDialog.show(sheetActivity.getSupportFragmentManager(), "");
                break;
        }
    }




}
