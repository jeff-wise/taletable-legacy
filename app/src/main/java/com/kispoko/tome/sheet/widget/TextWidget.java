
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
import com.kispoko.tome.activity.sheet.dialog.ChooseValueDialogFragment;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.NavigationDialogFragment;
import com.kispoko.tome.sheet.SheetException;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.error.UndefinedValueSetError;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.text.TextWidgetDialogFragment;
import com.kispoko.tome.sheet.widget.text.TextWidgetFormat;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.Background;
import com.kispoko.tome.sheet.widget.util.WidgetCorners;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.FormattedString;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * TextWidget
 */
public class TextWidget extends Widget
                        implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<TextVariable>          valueVariable;
    private ModelFunctor<TextWidgetFormat>      format;
    private PrimitiveFunctor<String>            description;

    private ModelFunctor<WidgetData>            widgetData;
    private CollectionFunctor<VariableUnion>    variables;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                             valueViewId;


    // > Misc
    // ------------------------------------------------------------------------------------------

    public static final long serialVersionUID = 88L;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextWidget()
    {
        this.id                 = null;

        this.valueVariable      = ModelFunctor.empty(TextVariable.class);
        this.format             = ModelFunctor.empty(TextWidgetFormat.class);
        this.description        = new PrimitiveFunctor<>(null, String.class);

        this.widgetData         = ModelFunctor.empty(WidgetData.class);

        this.variables          = CollectionFunctor.empty(VariableUnion.class);
    }


    public TextWidget(UUID id,
                      TextVariable valueVariable,
                      TextWidgetFormat format,
                      String description,
                      WidgetData widgetData,
                      List<VariableUnion> variables)
    {
        this.id                 = id;

        this.valueVariable      = ModelFunctor.full(valueVariable, TextVariable.class);
        this.format             = ModelFunctor.full(format, TextWidgetFormat.class);
        this.description        = new PrimitiveFunctor<>(description, String.class);

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);

        this.variables          = CollectionFunctor.full(variables, VariableUnion.class);

        this.initializeTextWidget();
    }


    /**
     * Create a text component from a Yaml representation.
     * @param yaml The yaml parsing object at the text component node.
     * @return A new TextWidget.
     * @throws YamlParseException
     */
    public static TextWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID             id          = UUID.randomUUID();

        TextVariable     value       = TextVariable.fromYaml(yaml.atKey("value"));
        TextWidgetFormat format      = TextWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        String           description = yaml.atMaybeKey("description").getTrimmedString();

        WidgetData       widgetData  = WidgetData.fromYaml(yaml.atKey("data"), false);

        List<VariableUnion> variables  = yaml.atMaybeKey("variables").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new TextWidget(id, value, format, description, widgetData, variables);
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
     * This method is called when the Text Widget is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeTextWidget();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data())
                .putYaml("value", this.valueVariable())
                .putList("variables", this.variables());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    public String name()
    {
        return "text";
    }


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
     * The text widget's format object.
     * @return The Text Widget Format.
     */
    public TextWidgetFormat format()
    {
        return this.format.getValue();
    }


    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the TextWidget's valueVariable variable.
     * @return The Variable for the TextWidget valueVariable.
     */
    public TextVariable valueVariable()
    {
        return this.valueVariable.getValue();
    }


    /**
     * Get the text widget's valueVariable (from its valueVariable variable).
     * @return The valueVariable.
     */
    public String value()
    {
        try {
            return this.valueVariable().value();
        }
        catch (NullVariableException exception) {
            ApplicationFailure.nullVariable(exception);
            return "N/A";
        }
    }


    public void setValue(String stringValue, Context context)
    {
        this.valueVariable().setLiteralValue(stringValue);

        // Update the text view, if it exists
        if (context != null) {
            TextView textView = (TextView) ((Activity) context)
                                    .findViewById(this.valueViewId);
            try {
                textView.setText(this.valueVariable().value());
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
            }
        }

        this.valueVariable.save();
    }


    // ** Description
    // ------------------------------------------------------------------------------------------

    /**
     * The description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
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


    // ** Initialize
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget.
     */
    @Override
    public void initialize(GroupParent groupParent)
    {
        // [1] Initialize the value variable
        // --------------------------------------------------------------------------------------

        // > If the variable is non-null
        if (!this.valueVariable.isNull())
        {
            this.valueVariable().initialize();

            this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onValueUpdate();
                }
            });

            // > Add to the state
            State.addVariable(this.valueVariable());
        }

        // [2] Initialize the helper variables
        // -------------------------------------------------------------------------------------

        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }

    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeTextWidget()
    {
        // [1] Apply default format values
        // -------------------------------------------------------------------------------------

        // ** Width
        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        // ** Alignment
        if (this.data().format().alignment() == null)
            this.data().format().setAlignment(Alignment.CENTER);

        // ** Background
        if (this.data().format().background() == null)
            this.data().format().setBackground(Background.DARK);

        // ** Corners
        if (this.data().format().corners() == null)
            this.data().format().setCorners(WidgetCorners.SMALL);


        this.valueViewId = null;

    }


    // > Value Update
    // -----------------------------------------------------------------------------------------

    /**
     * When the text widget's valueVariable is updated.
     */
    private void onValueUpdate()
    {
        if (this.valueViewId != null && !this.valueVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            String value = this.value();

            if (value != null)
                textView.setText(value);
        }
    }


    // > Views
    // ------------------------------------------------------------------------------------------


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

        layout.gravity              = this.data().format().alignment().gravityConstant();

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
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundResource   = this.data().format().background()
                                          .resourceId(this.data().format().corners(),
                                                      this.format().valueStyle().size());

        layout.gravity              = this.format().valueStyle().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;

        layout.onClick              = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTextWidgetShortClick(context);
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
            value.font          = Font.serifFontRegular(context);

            value.color     = this.format().descriptionStyle().color().resourceId();
            value.size      = this.format().descriptionStyle().size().resourceId();


            List<FormattedString.Span> spans = new ArrayList<>();

            FormattedString.Span labelSpan =
                    new FormattedString.Span(null,
                                             this.format().insideLabel(),
                                             this.format().insideLabelStyle(),
                                             this.format().descriptionStyle().size());

            FormattedString.Span valueSpan =
                    new FormattedString.Span(context.getString(R.string.placeholder_value),
                                             this.value(),
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
            value.text      = this.value();
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

        label.layoutGravity     = this.format().outsideLabelStyle().alignment().gravityConstant();

        label.text              = this.format().outsideLabel();

        this.format().outsideLabelStyle().styleTextViewBuilder(label, context);

        // > Format the label depending on its properties

        // > Alignment: LEFT
        if (this.format().outsideLabelStyle().alignment() == Alignment.LEFT)
            label.margin.left   = R.dimen.one_dp;

        // > Position: TOP
        if (this.format().outsideLabelPosition() == Position.TOP)
            label.margin.bottom = R.dimen.two_dp;

        return label.textView(context);
    }


    private TextView insideLabelView(Context context)
    {
        TextViewBuilder label   = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = this.format().insideLabel();

        this.format().insideLabelStyle().styleTextViewBuilder(label, context);

        label.margin.right      = R.dimen.widget_label_inline_margin_right;

        return label.textView(context);
    }




    /**
     * The text widget label view.
     * @param context The context.
     * @return The Text View.
     */
    private TextView labelView(Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.layoutGravity     = this.data().format().labelStyle().alignment().gravityConstant();

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
     * On a short click, open the value editor.
     */
    private void onTextWidgetShortClick(Context context)
    {
        SheetActivity sheetActivity = (SheetActivity) context;

        switch (this.valueVariable().kind())
        {

            // OPEN the Quick Text Edit Dialog
            case LITERAL:
                TextWidgetDialogFragment textDialog = TextWidgetDialogFragment.newInstance(this);
                textDialog.show(sheetActivity.getSupportFragmentManager(), "");
                break;

            // OPEN the Choose Value Set Dialog
            case VALUE:
                String valueSetName = this.valueVariable().valueSetName();
                Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
                ValueSet valueSet = dictionary.lookup(valueSetName);

                if (valueSet == null) {
                    ApplicationFailure.sheet(
                            SheetException.undefinedValueSet(
                                    new UndefinedValueSetError("Text Widget", valueSetName)));
                    break;
                }

                ChooseValueDialogFragment chooseDialog =
                                            ChooseValueDialogFragment.newInstance(valueSet);
                chooseDialog.show(sheetActivity.getSupportFragmentManager(), "");

                break;

            case PROGRAM:
                break;
        }
    }


    /**
     * On a long click, open the text widget action dialog.
     */
    private void onTextWidgetLongClick(Context context)
    {
        SheetActivity sheetActivity = (SheetActivity) context;
        String widgetName = this.data().format().name();

        NavigationDialogFragment actionDialogFragment =
                NavigationDialogFragment.newInstance(widgetName, WidgetType.TEXT);
        actionDialogFragment.show(sheetActivity.getSupportFragmentManager(), "actions");
    }


//                Activity editActivity = (Activity) context;
//                String newValue = editText.getText().toString();
//                EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
//                                                       thisTextWidget.getId(), newValue);
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("RESULT", editResult);
//                editActivity.setResult(Activity.RESULT_OK, resultIntent);
//                editActivity.finish();

}
