
package com.taletable.android.activity.entity.engine.variable;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.taletable.android.R;
import com.taletable.android.model.engine.variable.TextVariable;
import com.taletable.android.lib.ui.LinearLayoutBuilder;


/**
 * Variable Activity
 */
public class TextVariableActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TextVariable variable;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_variable);

        // > Read Parameters
//        if (getIntent().hasExtra("text_variable")) {
//            this.variable = (TextVariable) getIntent().getSerializableExtra("text_variable");
//        }

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
//        String title = this.variable.label();

 //       UI.initializeToolbar(this, title);
    }


    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.variable_content);

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
//        LinearLayout nameField = Form.field(
//                    R.string.variable_field_name_label,
//                    R.string.variable_field_name_description,
//                    Form.textInput(this.variable.name(), this),
//                    this);
//
//        // > Label Field
//        // -------------------------------------------------------------------------------------
//        LinearLayout labelField = Form.field(
//                    R.string.variable_field_label_label,
//                    R.string.variable_field_label_description,
//                    Form.textInput(this.variable.label(), this),
//                    this);

        // > Value Field
        // -------------------------------------------------------------------------------------

        // ** Literal Tab View
//        String literalValue = "";
//        if (this.variable.kind() == TextVariable.Kind.LITERAL)
//            literalValue = this.variable.stringLiteral();
//
//        LinearLayout literalValueInput = Form.textInput(literalValue, this);
//
//        // ** Value Tab View
//        String valueString = "";
//        DataReference valueReference = this.variable.valueReference();
//        if (valueReference != null) {
//            TextValue textValue = SheetManagerOld.currentSheet().engine().dictionary()
//                                              .textValue(valueReference);
//            valueString = textValue.value();
//        }
//        else {
//            valueString = "Select Value";
//        }

//        TextView valueInput = Form.buttonInput(valueString, this);
//
//        // ** Program Tab View
//        String programString = "";
//        if (this.variable.kind() == TextVariable.Kind.PROGRAM)
//            programString = "VIEW";
//        else
//            programString = "CREATE";

//        TextView programInvocationInput = Form.buttonInput(programString, this);
//
//
//        List<String> tabNames = Arrays.asList("Text", "Value", "Computation");
//        List<View> tabViews = Arrays.asList(literalValueInput, valueInput, programInvocationInput);

//        LinearLayout valueField =
//                Form.field(R.string.text_variable_field_value_label,
//                           R.string.text_variable_field_value_description,
//                           Form.optionInput(tabNames, tabViews, this),
//                           this);
//
//        // > Is Namespaced Field
//        // -------------------------------------------------------------------------------------
//        LinearLayout isNamespacedField =
//                Form.field(R.string.variable_field_is_namespaced_label,
//                           R.string.variable_field_is_namespaced_description,
//                           Form.booleanInput(this.variable.isNamespaced(), this),
//                           this);
//
//        // > Defines Namespace Field
//        // -------------------------------------------------------------------------------------
//        LinearLayout definesNamespaceField =
//                Form.field(R.string.variable_field_defines_namespace_label,
//                        R.string.variable_field_defines_namespace_description,
//                        Form.booleanInput(this.variable.definesNamespace(), this),
//                        this);
//
//
//        // [2] Add Fields
//        // -------------------------------------------------------------------------------------
//
//        layout.addView(nameField);
//        layout.addView(labelField);
//        layout.addView(valueField);
//        layout.addView(isNamespacedField);
//        layout.addView(definesNamespaceField);
//
        return layout;
    }


    private LinearLayout formLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.variable_form_padding_horz;
        layout.padding.right        = R.dimen.variable_form_padding_horz;
        layout.padding.top          = R.dimen.variable_form_padding_vert;
        layout.padding.bottom       = R.dimen.variable_form_padding_vert;

        return layout.linearLayout(this);
    }




}
