
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.Form;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;



/**
 * Boolean Column Activity
 */
public class BooleanColumnActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private BooleanColumn booleanColumn;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_column);

        // > Read Parameters
        if (getIntent().hasExtra("column")) {
            this.booleanColumn = (BooleanColumn) getIntent().getSerializableExtra("column");
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
        String title = this.booleanColumn.name() + " " + getString(R.string.column);
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.column_content);
        contentLayout.addView(formView());
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout formView()
    {
        LinearLayout layout = formLayout();

        // [1] Define Fields
        // -------------------------------------------------------------------------------------

        // > Name Field
        // -------------------------------------------------------------------------------------

        String name = this.booleanColumn.name();

        LinearLayout nameField =
                Form.field(
                    R.string.boolean_column_field_name_label,
                    R.string.boolean_column_field_name_description,
                    Form.textInput(name, this),
                    this);

        // > Default Value
        // -------------------------------------------------------------------------------------

        LinearLayout defaultValueField =
                Form.field(
                    R.string.boolean_column_field_default_value_label,
                    R.string.boolean_column_field_default_value_description,
                    Form.booleanInput(this.booleanColumn.defaultValue(), this),
                    this);

        // > Alignment Field
        // -------------------------------------------------------------------------------------

        LinearLayout alignmentField =
                Form.field(
                        R.string.boolean_column_field_alignment_label,
                        R.string.boolean_column_field_alignment_description,
                        Form.variantInput(Alignment.class,
                                          this.booleanColumn.alignment(),
                                          this),
                        this);

        // > Width Field
        // -------------------------------------------------------------------------------------

        LinearLayout widthField =
                Form.field(
                        R.string.boolean_column_field_width_label,
                        R.string.boolean_column_field_width_description,
                        Form.textInput(this.booleanColumn.width().toString(), this),
                        this);


        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        layout.addView(nameField);
        layout.addView(defaultValueField);
        layout.addView(alignmentField);
        layout.addView(widthField);


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
