
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.EditActivity;
import com.kispoko.tome.activity.EditResult;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.engine.refinement.MemberOf;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.EditTextBuilder;
import com.kispoko.tome.util.ui.Font;
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

    private ModelFunctor<WidgetData>            widgetData;
    private PrimitiveFunctor<WidgetContentSize> size;
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
        this.valueVariable      = ModelFunctor.empty(TextVariable.class);
        this.size               = new PrimitiveFunctor<>(null, WidgetContentSize.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables          = CollectionFunctor.empty(variableClasses);

        this.displayTextViewId  = null;
    }


    public TextWidget(UUID id,
                      WidgetData widgetData,
                      WidgetContentSize size,
                      TextVariable valueVariable,
                      List<VariableUnion> variables)
    {
        this.id                 = id;

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
        this.valueVariable      = ModelFunctor.full(valueVariable, TextVariable.class);
        this.size               = new PrimitiveFunctor<>(size, WidgetContentSize.class);

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
        UUID                id         = UUID.randomUUID();

        WidgetData          widgetData = WidgetData.fromYaml(yaml.atKey("data"));
        WidgetContentSize   size       = WidgetContentSize.fromYaml(yaml.atMaybeKey("size"));
        TextVariable        value      = TextVariable.fromYaml(yaml.atKey("value"));

        List<VariableUnion> variables  = yaml.atMaybeKey("variables").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new TextWidget(id, widgetData, size, value, variables);
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
                .putYaml("size", this.size())
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
            case EDIT:
                Context context = SheetManager.currentSheetContext();
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("WIDGET", this);
                ((Activity) context).startActivityForResult(intent, SheetActivity.COMPONENT_EDIT);
                break;
        }
    }



    /**
     * The text widget's tile view.
     * @return The tile view.
     */
    public View tileView()
    {
        // [1] Setup / Declarations
        // --------------------------------------------------------------------------------------

        final Context context = SheetManager.currentSheetContext();
        LinearLayout textLayout = this.widgetLayout(true);
        LinearLayout contentLayout =
                (LinearLayout) textLayout.findViewById(R.id.widget_content_layout);

        this.displayTextViewId = Util.generateViewId();

        // [2] Text View
        // --------------------------------------------------------------------------------------

        TextViewBuilder textView = new TextViewBuilder();

        textView.gravity = Gravity.CENTER_HORIZONTAL;
        textView.id      = this.displayTextViewId;
        textView.size    = this.size.getValue().resourceId();
        textView.font    = Font.serifFontBold(context);
        textView.color   = R.color.dark_blue_hlx_6;
        //textView.color   = R.color.light_grey_6;
        textView.text    = this.value();

        contentLayout.addView(textView.textView(context));

        return textLayout;
    }


    /**
     * The text widget's editor view.
     * @return The editor view.
     */
    public View editorView(Context context)
    {
        if (this.valueVariable().hasRefinement())
            return this.getTypeEditorView(context);
        // No type is set, so allow free form edit
        else
            return this.freeEditorView(context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Widget's content size.
     * @return The Widget Content Size.
     */
    public WidgetContentSize size()
    {
        return this.size.getValue();
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


    public View getTypeEditorView(Context context)
    {
        RulesEngine rulesEngine = SheetManager.currentSheet().engine();

        // Lookup the recyclerview in activity layout
        RecyclerView textEditorView = new RecyclerView(context);
        textEditorView.setLayoutParams(Util.linearLayoutParamsMatch());
        //textEditorView.addItemDecoration(new SimpleDividerItemDecoration(context));

        // Create adapter passing in the sample user data
        MemberOf memberOf = rulesEngine.refinementIndex()
                                 .memberOfWithName(this.valueVariable().refinementId().name());
        TextEditRecyclerViewAdapter adapter = new TextEditRecyclerViewAdapter(this, memberOf);
        textEditorView.setAdapter(adapter);
        // Set layout manager to position the items
        textEditorView.setLayoutManager(new LinearLayoutManager(context));

        return textEditorView;
    }


    public View freeEditorView(final Context context)
    {
        LinearLayout layout = this.freeEditorLayout(context);

        EditText editView   = this.freeEditView(context);
        TextView saveButton = this.saveButtonView(context, editView);

        layout.addView(editView);
        layout.addView(saveButton);

        return layout;
    }


    private LinearLayout freeEditorLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.backgroundColor = R.color.dark_grey_7;
        layout.gravity         = Gravity.CENTER_HORIZONTAL;
        layout.orientation     = LinearLayout.VERTICAL;
        layout.padding.left    = R.dimen.widget_text_editor_free_layout_padding_horz;
        layout.padding.right   = R.dimen.widget_text_editor_free_layout_padding_horz;
        layout.padding.top     = R.dimen.widget_text_editor_free_layout_padding_vert;
        layout.padding.bottom  = R.dimen.widget_text_editor_free_layout_padding_vert;

        return layout.linearLayout(context);
    }


    private EditText freeEditView(Context context)
    {
        EditTextBuilder editText    = new EditTextBuilder();

        editText.id                 = R.id.comp_text_editor_value;
        editText.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
        editText.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        editText.gravity            = Gravity.TOP;
        editText.size               = R.dimen.widget_text_editor_free_value_text_size;
        editText.font               = Font.serifFontBold(context);
        editText.color              = R.color.light_grey_5;
        editText.minHeight          = R.dimen.widget_text_editor_free_value_min_height;
        editText.backgroundResource = R.drawable.bg_text_component_editor;
        editText.text               = this.value();

        return (EditText) editText.editText(context);
    }


    private TextView saveButtonView(final Context context, final EditText editText)
    {
        TextViewBuilder saveButton = new TextViewBuilder();

        final TextWidget thisTextWidget = this;

        saveButton.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        saveButton.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        saveButton.layoutGravity        = Gravity.CENTER_HORIZONTAL;
        saveButton.gravity              = Gravity.CENTER_HORIZONTAL;
        saveButton.backgroundResource   = R.drawable.bg_text_component_editor_save_button;
        saveButton.text                 = "DONE";
        saveButton.font                 = Font.sansSerifFontBold(context);
        saveButton.color                = R.color.green_5;
        saveButton.size                 = R.dimen.widget_text_editor_free_button_text_size;

        saveButton.onClick              = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("***TEXTWIDGET", "on click");
                Activity editActivity = (Activity) context;
                String newValue = editText.getText().toString();
                EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
                                                       thisTextWidget.getId(), newValue);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT", editResult);
                editActivity.setResult(Activity.RESULT_OK, resultIntent);
                editActivity.finish();
            }
        };

        return saveButton.textView(context);
    }



}
