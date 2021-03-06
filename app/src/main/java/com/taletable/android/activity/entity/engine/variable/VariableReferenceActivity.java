
package com.taletable.android.activity.entity.engine.variable;


import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.taletable.android.R;
import com.taletable.android.model.engine.variable.VariableReference;
import com.taletable.android.lib.model.form.Field;
import com.taletable.android.lib.model.form.Form;
import com.taletable.android.lib.ui.LinearLayoutBuilder;
import com.taletable.android.util.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * Variable Reference Activity
 */
public class VariableReferenceActivity extends AppCompatActivity
{

    // PROPERTEIS
    // -----------------------------------------------------------------------------------------

    private VariableReference   variableReference;


    // > Form
    // -----------------------------------------------------------------------------------------

    private Map<String,Field>   fieldByName;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        // --------------------------------------------------------------------------------------
        setContentView(R.layout.activity_form_basic);

        // [2] Read parameters
        // --------------------------------------------------------------------------------------
        this.variableReference = null;
        if (getIntent().hasExtra("variable_reference")) {
            this.variableReference =
                    (VariableReference) getIntent().getSerializableExtra("variable_reference");
        }

        // [3] Initialize UI components
        // -------------------------------------------------------------------------------------
        this.initializeToolbar();
        this.initializeData();
        this.initializeView();
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


    // UI
    // -----------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.variable_reference_editor));
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.content);
        scrollView.addView(this.view(this));
    }


    private void initializeData()
    {
        // [1] Initialize indexes
        // -------------------------------------------------------------------------------------

        this.fieldByName = new HashMap<>();

        // [2] Get & Index Fields
        // -------------------------------------------------------------------------------------

        if (this.variableReference == null)
            return;

        Collection<Field> fields = new ArrayList<>();

        // GENERATE fields from Value Set
//        try {
//            fields.addAll(ProdType.fields(this.variableReference, this));
//        }
//        catch (FunctorException exception) {
//            ApplicationFailure.functor(exception);
//        }

        // INDEX fields by name
        for (Field field : fields) {
            this.fieldByName.put(field.name(), field);
        }
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Toolbar
        layout.addView(Form.toolbarView(context));

        // > Form
        layout.addView(this.formView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_theme_primary_84;

        return layout.linearLayout(context);
    }


    private LinearLayout formView(Context context)
    {
        LinearLayout layout = Form.layout(context);

        // > Form Structure
        // -------------------------------------------------------------------------------------

        this.addFieldView("type", layout);

        // > Click Events
        // -------------------------------------------------------------------------------------

        // this.setReferenceListeners();


        return layout;
    }


    private void addFieldView(String fieldName, LinearLayout layout)
    {
        Field field = this.fieldByName.get(fieldName);

        if (field != null) {
            LinearLayout fieldView = field.view(this);
            layout.addView(fieldView);
        }
    }

}
