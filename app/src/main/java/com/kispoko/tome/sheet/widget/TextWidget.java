
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.text.TextWidgetFormat;
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

        WidgetData       widgetData = WidgetData.fromYaml(yaml.atKey("data"));
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
    public void onLoad() { }


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


    public void runAction(Action action)
    {
        switch (action)
        {
//            case EDIT:
//                Context context = SheetManager.currentSheetContext();
//                Intent intent = new Intent(context, EditActivity.class);
//                intent.putExtra("WIDGET", this);
//                ((Activity) context).startActivityForResult(intent, SheetActivity.COMPONENT_EDIT);
//                break;
        }
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
    public void initialize()
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
        // --------------------------------------------------------------------------------------

        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }

    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

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

        // > Value View
        layout.addView(valueView(context));

        // > Quote View
        if (this.format().isQuote())
            layout.addView(quoteView(context));

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
                onTextWidgetShortClick();
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

        layout.backgroundResource   = this.data().format().background()
                                          .resourceId(this.data().format().corners());

        if (this.format().inlineLabel() != null)
            layout.child(label);

        layout.child(value);

        // [3] Value
        // -------------------------------------------------------------------------------------

        value.id                    = this.displayTextViewId;

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = this.value();
        value.size                  = this.format().size().resourceId();
        value.color                 = this.format().tint().resourceId();


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

        if (this.data().format().isBold())
            value.font   = Font.serifFontBold(context);
        else
            value.font   = Font.serifFontRegular(context);

        // [4] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = this.format().inlineLabel();
        label.color                 = R.color.dark_blue_hl_6;
        label.font                  = Font.serifFontRegular(context);
        label.size                  = this.format().size().labelResourceId();

        label.margin.right          = R.dimen.widget_label_inline_margin_right;


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
    private void onTextWidgetShortClick()
    {
        switch (this.valueVariable().kind())
        {
            case LITERAL:
                break;
            case VALUE:
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

        ActionDialogFragment actionDialogFragment =
                ActionDialogFragment.newInstance(widgetName, Type.TEXT);
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
