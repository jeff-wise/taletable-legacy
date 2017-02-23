
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
import com.kispoko.tome.sheet.widget.util.InlineLabelPosition;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.widget.util.WidgetBackground;
import com.kispoko.tome.sheet.widget.util.WidgetCorners;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
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

    private ModelFunctor<WidgetData>            widgetData;
    private ModelFunctor<TextWidgetFormat>      format;
    private ModelFunctor<TextVariable>          valueVariable;
    private CollectionFunctor<VariableUnion>    variables;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                             displayTextViewId;


    // > Misc
    // ------------------------------------------------------------------------------------------

    public static final long serialVersionUID = 88L;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextWidget()
    {
        this.id                 = null;

        this.widgetData         = ModelFunctor.empty(WidgetData.class);
        this.format             = ModelFunctor.empty(TextWidgetFormat.class);
        this.valueVariable      = ModelFunctor.empty(TextVariable.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables          = CollectionFunctor.empty(variableClasses);

        this.displayTextViewId  = null;
    }


    public TextWidget(UUID id,
                      WidgetData widgetData,
                      TextWidgetFormat format,
                      TextVariable valueVariable,
                      List<VariableUnion> variables)
    {
        this.id                 = id;

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
        this.format             = ModelFunctor.full(format, TextWidgetFormat.class);
        this.valueVariable      = ModelFunctor.full(valueVariable, TextVariable.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables          = CollectionFunctor.full(variables, variableClasses);

        this.displayTextViewId  = null;

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
        UUID             id         = UUID.randomUUID();

        WidgetData       widgetData = WidgetData.fromYaml(yaml.atKey("data"), false);
        TextWidgetFormat format     = TextWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        TextVariable     value      = TextVariable.fromYaml(yaml.atKey("value"));

        List<VariableUnion> variables  = yaml.atMaybeKey("variables").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new TextWidget(id, widgetData, format, value, variables);
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
        return this.valueVariable().value();
    }


    public void setValue(String stringValue, Context context)
    {
        this.valueVariable().setLiteralValue(stringValue);

        // Update the text view, if it exists
        if (context != null) {
            TextView textView = (TextView) ((Activity) context)
                                    .findViewById(this.displayTextViewId);
            textView.setText(this.valueVariable().value());
        }

        this.valueVariable.save();
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


    // > Value Update
    // -----------------------------------------------------------------------------------------

    /**
     * When the text widget's valueVariable is updated.
     */
    private void onValueUpdate()
    {
        if (this.displayTextViewId != null && !this.valueVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.displayTextViewId);

            String value = this.value();

            if (value != null)
                textView.setText(value);
        }
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * The text widget's tile view.
     * @return The tile view.
     */
    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = viewLayout(rowHasLabel, context);

        // > Label View
        if (this.data().format().label() != null)
            layout.addView(this.labelView(context));

        // > Content
        LinearLayout contentView = contentView(context);
        layout.addView(contentView);

        // ** Top Label View
        if (this.format().label() != null &&
            this.format().labelPosition() == InlineLabelPosition.TOP) {
            contentView.addView(this.valueTopLabelView(context));
        }

        // ** Value View
        contentView.addView(valueView(context));

        // ** Quote View
        if (this.format().isQuote())
            contentView.addView(quoteView(context));

        return layout;
    }


    private LinearLayout viewLayout(boolean rowHasLabel, final Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = 0;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight               = this.data().format().width().floatValue();

        if (this.data().format().label() == null && rowHasLabel) {
            layout.padding.top      = R.dimen.widget_label_fill_padding;
        }

        layout.margin.left          = R.dimen.widget_margin_horz;
        layout.margin.right         = R.dimen.widget_margin_horz;

        layout.onClick          = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTextWidgetShortClick(context);
            }
        };

        layout.onLongClick      = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onTextWidgetLongClick(context);
                return false;
            }
        };

        layout.hapticFeedback   = true;

        if (this.format().isQuote()) {
            layout.padding.left  = R.dimen.widget_text_layout_quote_padding_horz;
            layout.padding.right = R.dimen.widget_text_layout_quote_padding_horz;
        }

        return layout.linearLayout(context);
    }


    private LinearLayout contentView(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.backgroundResource   = this.data().format().background()
                                          .resourceId(this.data().format().corners());

        return layout.linearLayout(context);
    }


    private LinearLayout valueView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     value  = new TextViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        this.displayTextViewId = Util.generateViewId();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        if (this.format().label() != null &&
                this.format().labelPosition() == InlineLabelPosition.LEFT) {
            layout.child(label);
        }

        layout.child(value);

        // [3] Value
        // -------------------------------------------------------------------------------------

        value.id                    = this.displayTextViewId;

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = this.value();
        value.size                  = this.format().valueStyle().size().resourceId();
        value.color                 = this.format().valueStyle().color().resourceId();

        // > Alignment
        // -------------------------------------------------------------------------------------

        switch (this.data().format().alignment())
        {
            case LEFT:
                layout.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                layout.layoutGravity = Gravity.START;
                break;
            case CENTER:
                layout.gravity = Gravity.CENTER;
                value.gravity = Gravity.CENTER_HORIZONTAL;
                break;
            case RIGHT:
                layout.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                layout.layoutGravity = Gravity.END;
                break;
        }

        // ** Font
        // -------------------------------------------------------------------------------------

        if (this.format().valueStyle().isBold() && this.format().valueStyle().isItalic()) {
            value.font  = Font.serifFontBoldItalic(context);
        }
        else if (this.format().valueStyle().isBold()) {
            value.font  = Font.serifFontBold(context);
        }
        else if (this.format().valueStyle().isItalic()) {
            value.font  = Font.serifFontItalic(context);
        }
        else {
            value.font  = Font.serifFontRegular(context);
        }

        // [4] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = this.format().label();
        label.color                 = R.color.dark_blue_hl_6;
        label.font                  = Font.serifFontRegular(context);
        label.size                  = this.format().labelStyle().size().resourceId();

        label.margin.right          = R.dimen.widget_label_inline_margin_right;


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


    private LinearLayout quoteView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();

        ImageViewBuilder    icon        = new ImageViewBuilder();
        TextViewBuilder     source      = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation     = LinearLayout.HORIZONTAL;
        layout.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.layoutGravity   = Gravity.CENTER_HORIZONTAL;
        layout.gravity         = Gravity.CENTER_VERTICAL;

        layout.margin.top      = R.dimen.widget_text_quote_margin_top;

        layout.child(icon)
              .child(source);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_quote;

        // [3 B] Source
        // -------------------------------------------------------------------------------------

        source.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        source.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        source.text                 = this.format().quoteSource();
        source.font                 = Font.sansSerifFontRegular(context);
        source.color                = R.color.dark_blue_hl_8;
        source.size                 = R.dimen.widget_text_quote_source_text_size;

        return layout.linearLayout(context);
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
