
package com.kispoko.tome.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.TextValue;
import com.kispoko.tome.engine.value.ValueReference;
import com.kispoko.tome.lib.ui.ActivityCommon;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.functor.form.FieldOptions;
import com.kispoko.tome.lib.ui.form.Form;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.sheet.SheetManager;


/**
 * Value Editor Activity
 */
public class TextValueEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueReference  valueReference;

    private TextValue       textValue;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_value_editor);

        // > Read Parameters
        this.valueReference = null;
        if (getIntent().hasExtra("value_reference")) {
            this.valueReference =
                    (ValueReference) getIntent().getSerializableExtra("value_reference");
        }

        Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();

        this.textValue = null;
        if (this.valueReference != null)
            this.textValue = dictionary.textValue(this.valueReference);

        initializeToolbar();

        initializeView();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        // > Initialize Toolbar
        // -------------------------------------------------------------------------------------
        ActivityCommon.initializeToolbar(this);

        // > Set the title
        // -------------------------------------------------------------------------------------
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setTypeface(Font.serifFontRegular(this));
        titleView.setText(R.string.value_editor);

        // > Configure Back Button
        // -------------------------------------------------------------------------------------
        ImageView backButtonView   = (ImageView) findViewById(R.id.toolbar_back_button);
        backButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void initializeView()
    {
        LinearLayout contentView = (LinearLayout) findViewById(R.id.value_editor_content);
        contentView.addView(this.view(this));
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private ScrollView view(Context context)
    {
        ScrollView scrollView = this.scrollView(context);

        LinearLayout layout = this.viewLayout(context);

        // > Help
        layout.addView(this.helpView(context));

        // > Form
        // layout.addView(this.formView(context));


        scrollView.addView(layout);

        return scrollView;
    }


    private ScrollView scrollView(Context context)
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width    = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height   = LinearLayout.LayoutParams.MATCH_PARENT;

        return scrollView.scrollView(context);
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_blue_9;

        return layout.linearLayout(context);
    }


    // ** Help View
    // -----------------------------------------------------------------------------------------

    private LinearLayout helpView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder icon   = new ImageViewBuilder();
        TextViewBuilder label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER;
        layout.layoutGravity        = Gravity.CENTER;

        layout.margin.topDp         = 25f;
        layout.margin.bottomDp      = 25f;

        layout.padding.leftDp       = 7f;
        layout.padding.rightDp      = 7f;
        layout.padding.topDp        = 7f;
        layout.padding.bottomDp     = 7f;

        layout.backgroundResource   = R.drawable.bg_form_help_button;
        layout.backgroundColor      = R.color.dark_blue_6;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = R.drawable.ic_form_help;
        icon.color          = R.color.dark_blue_hl_2;

        icon.margin.rightDp = 5f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId        = R.string.learn_about_value_sets;

        label.font          = Font.serifFontRegular(context);
        label.color         = R.color.dark_blue_hlx_9;
        label.sizeSp        = 18f;


        return layout.linearLayout(context);
    }


    // ** Form Views
    // -----------------------------------------------------------------------------------------

    /*
    private LinearLayout formView(final Context context)
    {

        LinearLayout layout = this.formLayout(context);

        // > Value Field
        // -------------------------------------------------------------------------------------

        FieldOptions valueFieldOptions = new FieldOptions();
        if (this.textValue == null) {
            String stepDescription = context.getString(R.string.value_editor_field_step_value);
            valueFieldOptions = FieldOptions.newField(1, stepDescription);
        }

        String valueValue = null;
        if (this.textValue != null)
            valueValue = this.textValue.value();

        LinearLayout valueField = Form.textFieldView(
                context.getString(R.string.value_editor_field_value_label),
                context.getString(R.string.value_editor_field_value_description),
                valueValue,
                valueFieldOptions,
                context);

        // > Summary Field
        // -------------------------------------------------------------------------------------

        FieldOptions summaryFieldOptions = new FieldOptions();
        if (this.textValue == null) {
            String stepDescription = context.getString(R.string.value_editor_field_step_summary);
            summaryFieldOptions = FieldOptions.newField(2, stepDescription);
        }

        String summaryValue = null;
        if (this.textValue != null)
            summaryValue = this.textValue.summary();

        LinearLayout summaryField = Form.textFieldView(
                context.getString(R.string.value_editor_field_summary_label),
                context.getString(R.string.value_editor_field_summary_description),
                summaryValue,
                summaryFieldOptions,
                context);

        // > Variables Field
        // -------------------------------------------------------------------------------------

        FieldOptions variablesFieldOptions = new FieldOptions();
        if (this.textValue == null) {
            String stepDescription = context.getString(R.string.value_editor_field_step_variables);
            variablesFieldOptions = FieldOptions.newField(3, stepDescription);
        }

        int variablesSize = 0;
        if (this.textValue != null)
            variablesSize = this.textValue.variables().size();

        LinearLayout variablesField = Form.listFieldView(
                context.getString(R.string.value_editor_field_variables_label),
                context.getString(R.string.value_editor_field_variables_description),
                variablesSize,
                variablesFieldOptions,
                context);

        //layout.addView(Form.fieldDividerView(context));
        layout.addView(valueField);
        //layout.addView(Form.fieldDividerView(context));
        layout.addView(summaryField);
        //layout.addView(Form.fieldDividerView(context));
        layout.addView(variablesField);
        //layout.addView(Form.fieldDividerView(context));

        return layout;
    }*/


    private LinearLayout formLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


}
