
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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
import com.kispoko.tome.engine.programming.variable.TextVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.engine.refinement.MemberOf;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.FileObserver.ACCESS;


/**
 * TextWidget
 */
public class TextWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public static final long serialVersionUID = 88L;


    // > Functors
    // ------------------------------------------------------------------------------------------
    private UUID                              id;
    private ModelValue<WidgetData>            widgetData;
    private PrimitiveValue<WidgetFormat.Size> size;
    private ModelValue<TextVariable>          value;


    // > Internal
    // ------------------------------------------------------------------------------------------
    private Integer                           displayTextViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextWidget()
    {
        this.id         = null;

        this.widgetData = ModelValue.empty(WidgetData.class);
        this.value      = ModelValue.empty(TextVariable.class);
        this.size       = new PrimitiveValue<>(null, WidgetFormat.Size.class);
    }


    public TextWidget(UUID id, WidgetData widgetData, WidgetFormat.Size size, TextVariable value)
    {
        this.id         = id;

        this.widgetData = ModelValue.full(widgetData, WidgetData.class);
        this.value      = ModelValue.full(value, TextVariable.class);
        this.size       = new PrimitiveValue<>(size, WidgetFormat.Size.class);

        initialize();
    }


    /**
     * Create a text component from a Yaml representation.
     * @param yaml The yaml parsing object at the text component node.
     * @return A new TextWidget.
     * @throws YamlException
     */
    public static TextWidget fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID              id         = UUID.randomUUID();
        WidgetData        widgetData = WidgetData.fromYaml(yaml.atKey("data"));
        WidgetFormat.Size size       = WidgetFormat.Size.fromYaml(yaml.atMaybeKey("size"));
        TextVariable      value      = TextVariable.fromYaml(yaml.atKey("value"));

        return new TextWidget(id, widgetData, size, value);
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
        initialize();
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    public String name() {
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
        LinearLayout textLayout = this.linearLayout();
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
        textView.color   = R.color.light_grey_5;
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
            return this.getFreeEditorView(context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the TextWidget's value variable.
     * @return The Variable for the TextWidget value.
     */
    public TextVariable valueVariable()
    {
        return this.value.getValue();
    }


    /**
     * Get the text widget's value (from its value variable).
     * @return The value.
     */
    public String value()
    {
        return this.valueVariable().value();
    }


    public void setValue(String stringValue, Context context)
    {
        this.valueVariable().setValue(stringValue);

        if (context != null) {
            TextView textView = (TextView) ((Activity) context)
                                    .findViewById(this.displayTextViewId);
            textView.setText(this.valueVariable().value());
        }

        this.value.save();
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    private void initialize()
    {
        this.displayTextViewId = null;

        if (!this.valueVariable().isNull())
        {
            this.valueVariable().addOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onValueUpdate();
                }
            });
        }
    }


    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        if (this.displayTextViewId != null && !this.value.isNull())
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
        RulesEngine rulesEngine = SheetManager.currentSheet().getRulesEngine();

        // Lookup the recyclerview in activity layout
        RecyclerView textEditorView = new RecyclerView(context);
        textEditorView.setLayoutParams(Util.linearLayoutParamsMatch());
        textEditorView.addItemDecoration(new SimpleDividerItemDecoration(context));

        // Create adapter passing in the sample user data
        MemberOf memberOf = rulesEngine.getRefinementIndex()
                                 .memberOfWithName(this.valueVariable().getRefinementId().getName());
        TextEditRecyclerViewAdapter adapter = new TextEditRecyclerViewAdapter(this, memberOf);
        textEditorView.setAdapter(adapter);
        // Set layout manager to position the items
        textEditorView.setLayoutManager(new LinearLayoutManager(context));

        return textEditorView;
    }


    public View getFreeEditorView(final Context context)
    {
        // Layout
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = Util.linearLayoutParamsMatch();

        int layoutParamsMargins = (int) Util.getDim(context,
                                                R.dimen.comp_text_editor_free_layout_margins);
//        layoutParams.setMargins(layoutParamsMargins, layoutParamsMargins,
//                                layoutParamsMargins, layoutParamsMargins);
        layout.setLayoutParams(layoutParams);
        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_7));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);

        int layoutPaddingHorz = (int) Util.getDim(context,
                                              R.dimen.comp_text_editor_free_layout_padding_horz);
        int layoutPaddingVert = (int) Util.getDim(context,
                                              R.dimen.comp_text_editor_free_layout_padding_vert);
        layout.setPadding(layoutPaddingHorz, layoutPaddingVert,
                          layoutPaddingHorz, layoutPaddingVert);


        // Edit TextWidget
        EditText editView = new EditText(context);
        editView.setId(R.id.comp_text_editor_value);
        editView.setGravity(Gravity.TOP);

        editView.setTextSize(this.size.getValue().toSP(context));

        editView.setTypeface(Util.serifFontBold(context));
        editView.setTextColor(ContextCompat.getColor(context, R.color.light_grey_5));
        //editView.setGravity(Gravity.CENTER_HORIZONTAL);
        editView.setMinHeight((int) Util.getDim(context, R.dimen.comp_text_editor_value_min_height));


        LinearLayout.LayoutParams editViewLayoutParams = Util.linearLayoutParamsMatchWrap();
        //editViewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        editView.setLayoutParams(editViewLayoutParams);
        editView.setBackgroundResource(R.drawable.bg_text_component_editor);


        // Save Button
        TextView saveButton = new TextView(context);
        LinearLayout.LayoutParams saveButtonLayoutParams = Util.linearLayoutParamsMatchWrap();
        saveButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
//        saveButtonLayoutParams.topMargin = (int) Util.getDim(context, R.dimen.two_dp);
        saveButton.setGravity(Gravity.CENTER_HORIZONTAL);
        saveButton.setLayoutParams(saveButtonLayoutParams);
        saveButton.setBackgroundResource(R.drawable.bg_text_component_editor_save_button);
        saveButton.setText("DONE");
        saveButton.setTypeface(Util.sansSerifFontBold(context));
        saveButton.setTextColor(ContextCompat.getColor(context, R.color.green_5));
        float saveButtonTextSize = Util.getDim(context,
                                           R.dimen.comp_text_editor_free_save_button_text_size);
        saveButton.setTextSize(saveButtonTextSize);

        final TextWidget thisTextWidget = this;
        final EditText thisEditView = editView;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("***TEXTWIDGET", "on click");
                Activity editActivity = (Activity) context;
                String newValue = thisEditView.getText().toString();
                EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
                                                       thisTextWidget.getId(), newValue);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT", editResult);
                editActivity.setResult(Activity.RESULT_OK, resultIntent);
                editActivity.finish();
            }
        });


        float valueTextSize = Util.getDim(context, R.dimen.comp_text_editor_value_text_size);
        editView.setTextSize(valueTextSize);

        editView.setText(this.value());

        // Define layout structure
        layout.addView(editView);
        layout.addView(saveButton);

        return layout;
    }


}
