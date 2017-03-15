
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
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
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.number.NumberWidgetFormat;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.FormattedString;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.functor.CollectionFunctor;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.functor.PrimitiveFunctor;
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

        this.variables          = CollectionFunctor.empty(VariableUnion.class);

        this.valueViewId        = null;
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

        this.variables  = CollectionFunctor.full(variables, VariableUnion.class);

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

        // ** Alignment
        if (this.data().format().alignmentIsDefault())
            this.data().format().setAlignment(Alignment.CENTER);

        // ** Background
        if (this.data().format().backgroundIsDefault())
            this.data().format().setBackground(BackgroundColor.NONE);

        // ** Corners
        if (this.data().format().cornersIsDefault())
            this.data().format().setCorners(Corners.SMALL);

        // ** Underline Thickness
        if (this.data().format().underlineThicknessIsDefault())
            this.data().format().setUnderlineThickness(0);

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

        layout.addView(mainView(context));

        return layout;
    }


    /**
     * The outer-most view that holds the outside labels and the value view.
     * @param context The context.
     * @return The main view Linear Layout.
     */
    private LinearLayout mainView(Context context)
    {
        LinearLayout layout = mainLayout(context);

        // > Outside Top/Left Label View
        if (this.format().outsideLabel() != null) {
            if (this.format().outsideLabelPosition() == Position.TOP ||
                this.format().outsideLabelPosition() == Position.LEFT) {
                layout.addView(this.outsideLabelView(context));
            }
        }

        // > Value
        layout.addView(this.valueMainView(context));

        // > Outside Bottom/Right Label View
        if (this.format().outsideLabel() != null) {
            if (this.format().outsideLabelPosition() == Position.BOTTOM ||
                this.format().outsideLabelPosition() == Position.RIGHT) {
                layout.addView(this.outsideLabelView(context));
            }
        }

        return layout;
    }


    private LinearLayout mainLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation          = this.format().outsideLabelPosition()
                                          .linearLayoutOrientation();

        layout.gravity              = this.data().format().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;

        layout.marginSpacing        = this.data().format().margins();

        return layout.linearLayout(context);
    }


    /**
     * The view that holds the value as well as the inside labels around the value.
     * @param context The context.
     * @return The value main view Linear Layout.
     */
    private LinearLayout valueMainView(Context context)
    {
        LinearLayout layout = valueMainViewLayout(context);

        // > Inside Top/Left Label View
        if (this.format().insideLabel() != null && this.description() == null) {
            if (this.format().insideLabelPosition() == Position.TOP ||
                this.format().insideLabelPosition() == Position.LEFT) {
                layout.addView(this.insideLabelView(context));
            }
        }

        layout.addView(valueTextView(context));

        // > Inside Bottom/Right Label View
        if (this.format().insideLabel() != null && this.description() == null) {
            if (this.format().insideLabelPosition() == Position.BOTTOM ||
                this.format().insideLabelPosition() == Position.RIGHT) {
                layout.addView(this.insideLabelView(context));
            }
        }

        return layout;
    }


    private LinearLayout valueMainViewLayout(final Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = this.format().insideLabelPosition().linearLayoutOrientation();

        // > Width
        //   If no padding is specified, the value (and its background) stretches to fill the
        //   space. Otherwise it only stretches as far as the padding allows
        // -------------------------------------------------------------------------------------
        if (this.format().valuePaddingHorizontal() != null ||
            this.data().format().background() == BackgroundColor.EMPTY) {
            layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        else {
            layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        }

        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;


        if (this.data().format().underlineThickness() > 0)
        {
            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
        }
        else if (this.data().format().background() != BackgroundColor.EMPTY &&
                 this.data().format().background() != BackgroundColor.NONE)
        {
            layout.backgroundColor      = this.data().format().background().colorId();

            if (this.format().valueHeight() != Height.WRAP)
            {
                layout.backgroundResource   = this.format().valueHeight()
                                                  .resourceId(this.data().format().corners());
            }
            else
            {
                layout.backgroundResource = this.data().format().corners().widgetResourceId();
            }
        }


        if (this.format().valueHeight() == Height.WRAP)
        {
            layout.padding.topDp    = this.format().valuePaddingVertical();
            layout.padding.bottomDp = this.format().valuePaddingVertical();
        }


        layout.gravity              = this.format().valueStyle().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;

        // > Padding
        // -------------------------------------------------------------------------------------
        if (this.format().valuePaddingHorizontal() != null)
        {
            layout.padding.leftDp   = this.format().valuePaddingHorizontal();
            layout.padding.rightDp  = this.format().valuePaddingHorizontal();
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

        value.layoutGravity = this.format().valueStyle().alignment().gravityConstant()
                                | Gravity.CENTER_VERTICAL;
        value.gravity       = this.format().valueStyle().alignment().gravityConstant();

        if (this.description() != null)
        {
            value.layoutGravity = this.format().descriptionStyle().alignment().gravityConstant()
                                    | Gravity.CENTER_VERTICAL;
            value.gravity       = this.format().descriptionStyle().alignment().gravityConstant();

            this.format().descriptionStyle().styleTextViewBuilder(value, context);

            List<FormattedString.Span> spans = new ArrayList<>();

            FormattedString.Span labelSpan =
                    new FormattedString.Span(null,
                                             this.format().insideLabel(),
                                             this.format().insideLabelStyle(),
                                             this.format().descriptionStyle().size());

            FormattedString.Span valueSpan =
                    new FormattedString.Span(context.getString(R.string.placeholder_value),
                                             this.valueString(),
                                             this.format().valueStyle(),
                                             this.format().descriptionStyle().size());

            if (this.format().insideLabel() != null)
                spans.add(labelSpan);

            spans.add(valueSpan);

            value.textSpan  = FormattedString.spannableStringBuilder(this.description(),
                                                                     spans,
                                                                     context);
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


    private TextView outsideLabelView(Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.layoutGravity     = this.format().outsideLabelStyle().alignment().gravityConstant()
                                    | Gravity.CENTER_VERTICAL;

        label.text              = this.format().outsideLabel();

        this.format().outsideLabelStyle().styleTextViewBuilder(label, context);

        label.marginSpacing     = this.format().outsideLabelMargins();

        return label.textView(context);
    }


    private TextView insideLabelView(Context context)
    {
        TextViewBuilder label   = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = this.format().insideLabel();

        label.layoutGravity     = this.format().insideLabelStyle().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;

        this.format().insideLabelStyle().styleTextViewBuilder(label, context);

        label.marginSpacing     = this.format().insideLabelMargins();

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
