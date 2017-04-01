
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;

import java.util.ArrayList;
import java.util.List;



/**
 * Number Widget Activity
 */
public class NumberWidgetActivity extends AppCompatActivity
{


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private NumberWidget numberWidget;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_widget);

        // > Read Parameters
        if (getIntent().hasExtra("widget")) {
            this.numberWidget = (NumberWidget) getIntent().getSerializableExtra("widget");
        }

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
        getMenuInflater().inflate(R.menu.toolbar_choose_template, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        // > Set the title
        String title = this.numberWidget.data().format().label();
        if (title == null)
            title = "Number Widget";
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.widget_content);
        contentLayout.addView(formView());
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout formView()
    {
        LinearLayout layout = formLayout();

        // [1] Define Fields
        // -------------------------------------------------------------------------------------

        // prefix variable
        // postfix variable

        // > Name Field
        // -------------------------------------------------------------------------------------

        String name = this.numberWidget.data().format().label();

//        LinearLayout nameField =
//                Form.field(
//                    R.string.number_widget_field_name_label,
//                    R.string.number_widget_field_name_description,
//                    Form.textInput(name, this),
//                    this);


        // > Text Size Field
        // -------------------------------------------------------------------------------------

//        LinearLayout textSizeField =
//                Form.field(
//                        R.string.number_widget_field_size_label,
//                        R.string.number_widget_field_size_description,
//                        Form.variantInput(TextSize.class,
//                                          this.numberWidget.format().valueStyle().size(),
//                                          this),
//                        this);

        // > Alignment Field
        // -------------------------------------------------------------------------------------

//        LinearLayout alignmentField =
//                Form.field(
//                        R.string.number_widget_field_alignment_label,
//                        R.string.number_widget_field_alignment_description,
//                        Form.variantInput(Alignment.class,
//                                          this.numberWidget.data().format().alignment(),
//                                          this),
//                        this);

        // > Width
        // -------------------------------------------------------------------------------------

        String width = this.numberWidget.data().format().width().toString();

//        LinearLayout widthField =
//                Form.field(
//                        R.string.number_widget_field_width_label,
//                        R.string.number_widget_field_width_description,
//                        Form.textInput(width, this),
//                        this);
//
//        // > Value Variable Field
//        // -------------------------------------------------------------------------------------
//
//        LinearLayout valueVariableField =
//                Form.field(
//                        R.string.number_widget_field_value_label,
//                        R.string.number_widget_field_value_description,
//                        Form.buttonInput(this.numberWidget.value().toString(), this),
//                        this);
//
//        // > Value Prefix Field
//        // -------------------------------------------------------------------------------------
//
//        LinearLayout valuePrefixField =
//                Form.field(
//                        R.string.number_widget_field_value_prefix_label,
//                        R.string.number_widget_field_value_prefix_description,
//                        Form.buttonInput(this.numberWidget.valuePrefix(), this),
//                        this);
//
//        // > Value Postfix Field
//        // -------------------------------------------------------------------------------------
//
//        LinearLayout valuePostfixField =
//                Form.field(
//                        R.string.number_widget_field_value_postfix_label,
//                        R.string.number_widget_field_value_postfix_description,
//                        Form.buttonInput(this.numberWidget.valuePostfix(), this),
//                        this);

        // > Variables Field
        // -------------------------------------------------------------------------------------

        List<String> variableNames = new ArrayList<>();
        for (VariableUnion variableUnion : this.numberWidget.variables()) {
            variableNames.add(variableUnion.variable().label());
        }

//        LinearLayout variablesField =
//                Form.field(R.string.number_widget_field_variables_label,
//                           R.string.number_widget_field_variables_description,
//                           Form.listInput("VARIABLE",
//                                          variableNames,
//                                          this),
//                           this);


        // [2] Add Fields
        // -------------------------------------------------------------------------------------

//        layout.addView(nameField);
//
//        layout.addView(Form.divider(this));
//
//        layout.addView(textSizeField);
//        layout.addView(alignmentField);
//        layout.addView(widthField);
//
//        layout.addView(Form.divider(this));
//
//        layout.addView(valueVariableField);
//
//        layout.addView(Form.divider(this));
//
//        layout.addView(valuePrefixField);
//        layout.addView(valuePostfixField);
//
//        layout.addView(Form.divider(this));
//
//        layout.addView(variablesField);


        return layout;
    }



    private LinearLayout formLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.form_padding_horz;
        layout.padding.right        = R.dimen.form_padding_horz;
        layout.padding.top          = R.dimen.form_padding_vert;
        layout.padding.bottom       = R.dimen.form_padding_vert;

        return layout.linearLayout(this);
    }


}
