
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.group.RowWidth;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.util.WidgetContentAlignment;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.ui.Form;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * Text Widget Activity
 */
public class TextWidgetActivity extends AppCompatActivity
{


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TextWidget textWidget;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_widget);

        // > Read Parameters
        if (getIntent().hasExtra("widget")) {
            this.textWidget = (TextWidget) getIntent().getSerializableExtra("widget");
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
        String title = this.textWidget.data().format().label();
        if (title == null)
            title = "Text Widget";
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

        // > Name Field
        // -------------------------------------------------------------------------------------

        String name = this.textWidget.data().format().label();

        LinearLayout nameField =
                Form.field(
                    R.string.text_widget_field_name_label,
                    R.string.text_widget_field_name_description,
                    Form.textInput(name, this),
                    this);


        // > Text Size Field
        // -------------------------------------------------------------------------------------

        LinearLayout textSizeField =
                Form.field(
                        R.string.text_widget_field_size_label,
                        R.string.text_widget_field_size_description,
                        Form.variantInput(WidgetContentSize.class,
                                          this.textWidget.size(),
                                          this),
                        this);

        // > Alignment Field
        // -------------------------------------------------------------------------------------

        LinearLayout alignmentField =
                Form.field(
                        R.string.text_widget_field_alignment_label,
                        R.string.text_widget_field_alignment_description,
                        Form.variantInput(WidgetContentAlignment.class,
                                          this.textWidget.data().format().alignment(),
                                          this),
                        this);

        // > Width
        // -------------------------------------------------------------------------------------

        String width = this.textWidget.data().format().width().toString();

        LinearLayout widthField =
                Form.field(
                        R.string.text_widget_field_width_label,
                        R.string.text_widget_field_width_description,
                        Form.textInput(width, this),
                        this);


        // > Value Variable Field
        // -------------------------------------------------------------------------------------

        LinearLayout valueVariableField =
                Form.field(
                        R.string.text_widget_field_value_label,
                        R.string.text_widget_field_value_description,
                        Form.buttonInput(this.textWidget.value(), this),
                        this);

        // > Variables Field
        // -------------------------------------------------------------------------------------

        List<String> variableNames = new ArrayList<>();
        for (VariableUnion variableUnion : this.textWidget.variables()) {
            variableNames.add(variableUnion.variable().label());
        }

        LinearLayout variablesField =
                Form.field(R.string.text_widget_field_variables_label,
                           R.string.text_widget_field_variables_description,
                           Form.listInput("VARIABLE",
                                          variableNames,
                                          this),
                           this);


        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        layout.addView(nameField);
        layout.addView(textSizeField);
        layout.addView(alignmentField);
        layout.addView(widthField);
        layout.addView(valueVariableField);
        layout.addView(variablesField);


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
