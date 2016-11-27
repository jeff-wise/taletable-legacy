
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.EditActivity;
import com.kispoko.tome.activity.EditResult;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.rules.programming.variable.TextVariable;
import com.kispoko.tome.sheet.widget.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetUI;
import com.kispoko.tome.rules.refinement.MemberOf;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * TextWidget
 */
public class TextWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // ** Model Values
    private UUID                              id;
    private ModelValue<WidgetData>            widgetData;
    private PrimitiveValue<WidgetFormat.Size> size;
    private ModelValue<TextVariable>          value;

    // ** Internal
    private int                               displayTextViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextWidget()
    {
        this.id         = null;

        this.widgetData = new ModelValue<>(null, WidgetData.class);
        this.value      = new ModelValue<>(null, TextVariable.class);
        this.size       = new PrimitiveValue<>(null, WidgetFormat.Size.class);
    }


    public TextWidget(UUID id, WidgetData widgetData, WidgetFormat.Size size, TextVariable value)
    {
        this.id         = id;

        this.widgetData = new ModelValue<>(widgetData, WidgetData.class);
        this.value      = new ModelValue<>(value, TextVariable.class);
        this.size       = new PrimitiveValue<>(size, WidgetFormat.Size.class);
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onValueUpdate(String valueName) { }


    // > Widget
    // ------------------------------------------------------------------------------------------

    public String name() {
        return "text";
    }


    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    public void runAction(String actionName, Context context, Rules rules)
    {
        switch (actionName)
        {
            case "edit":
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("COMPONENT", this);
                intent.putExtra("RULES", rules);
                ((Activity) context).startActivityForResult(intent, SheetActivity.COMPONENT_EDIT);
                break;
        }
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the TextWidget's value variable.
     * @return The Variable for the TextWidget value.
     */
    public TextVariable getValue()
    {
        return this.value.getValue();
    }


    public void setValue(String stringValue, Context context)
    {
        this.getValue().setString(stringValue);

        if (context != null) {
            TextView textView = (TextView) ((Activity) context)
                                    .findViewById(this.displayTextViewId);
            textView.setText(this.getValue().getString());
        }

        this.value.save();
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(final Context context, Rules rules)
    {
        LinearLayout textLayout = WidgetUI.linearLayout(this, context, rules);


        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        this.displayTextViewId = Util.generateViewId();
        textView.setId(this.displayTextViewId);

        textView.setTextSize(this.size.getValue().toSP(context));

        textView.setTypeface(Util.serifFontBold(context));
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        textView.setText(this.getValue().getString());

        textLayout.addView(textView);

        return textLayout;
    }


    public View getEditorView(Context context, Rules rules)
    {
        if (this.getValue().hasRefinement())
            return this.getTypeEditorView(context, rules);
        // No type is set, so allow free form edit
        else
            return this.getFreeEditorView(context);
    }


    public View getTypeEditorView(Context context, Rules rules)
    {
        // Lookup the recyclerview in activity layout
        RecyclerView textEditorView = new RecyclerView(context);
        textEditorView.setLayoutParams(Util.linearLayoutParamsMatch());
        textEditorView.addItemDecoration(new SimpleDividerItemDecoration(context));

        // Create adapter passing in the sample user data
        MemberOf memberOf = rules.getRefinementIndex()
                                 .memberOfWithName(this.getValue().getRefinementId().getName());
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
        layoutParams.setMargins(layoutParamsMargins, layoutParamsMargins,
                                layoutParamsMargins, layoutParamsMargins);
        layout.setLayoutParams(layoutParams);
        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.sheet_medium));
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

        editView.setTextSize(this.size.getValue().toSP(context));

        editView.setTypeface(Util.serifFontBold(context));
        editView.setTextColor(ContextCompat.getColor(context, R.color.text_medium_dark));
        editView.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams editViewLayoutParams = Util.linearLayoutParamsMatchWrap();
        editViewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        editView.setLayoutParams(editViewLayoutParams);
        editView.setBackgroundResource(R.drawable.bg_text_component_editor);


        // Save Button
        TextView saveButton = new TextView(context);
        LinearLayout.LayoutParams saveButtonLayoutParams = Util.linearLayoutParamsMatchWrap();
        saveButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        saveButtonLayoutParams.topMargin = (int) Util.getDim(context, R.dimen.two_dp);
        saveButton.setGravity(Gravity.CENTER_HORIZONTAL);
        saveButton.setLayoutParams(saveButtonLayoutParams);
        saveButton.setBackgroundResource(R.drawable.bg_text_component_editor_save_button);
        saveButton.setText("SAVE");
        saveButton.setTypeface(Util.sansSerifFontBold(context));
        saveButton.setTextColor(ContextCompat.getColor(context, R.color.green_medium));
        float saveButtonTextSize = Util.getDim(context,
                                           R.dimen.comp_text_editor_free_save_button_text_size);
        saveButton.setTextSize(saveButtonTextSize);

        final TextWidget thisTextWidget = this;
        final EditText thisEditView = editView;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        editView.setText(this.getValue().getString());

        // Define layout structure
        layout.addView(editView);
        layout.addView(saveButton);

        return layout;
    }



}
