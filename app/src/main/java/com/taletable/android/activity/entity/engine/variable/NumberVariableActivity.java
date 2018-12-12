
package com.taletable.android.activity.entity.engine.variable;


import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.taletable.android.R;
import com.taletable.android.lib.model.form.Field;
import com.taletable.android.lib.model.form.Form;
import com.taletable.android.lib.ui.LinearLayoutBuilder;
import com.taletable.android.model.engine.variable.NumberVariable;
import com.taletable.android.util.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * Number Variable Activity
 */
public class NumberVariableActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private NumberVariable numberVariable;


    // > Form
    // -----------------------------------------------------------------------------------------

    private Map<String,Field>           fieldByName;
    private Map<String,LinearLayout>    fieldViewByName;


    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        // -------------------------------------------------------------------------------------
        setContentView(R.layout.activity_form_basic);

        // [2] Read Parameters
        // -------------------------------------------------------------------------------------
        this.numberVariable = null;
//        if (getIntent().hasExtra("number_variable")) {
//            this.numberVariable =
//                    (NumberVariable) getIntent().getSerializableExtra("number_variable");
//        }

        // [3] Initialize data
        // -------------------------------------------------------------------------------------

        this.initializeData();

        // [4] Initialize views
        // -------------------------------------------------------------------------------------

        this.initializeToolbar();
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
        UI.initializeToolbar(this, getString(R.string.number_variable));
    }


    private void initializeView()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.content);
        scrollView.removeAllViews();
        scrollView.addView(this.view(this));
    }


    private void initializeData()
    {
        // [1] Initialize indexes
        // -------------------------------------------------------------------------------------

        this.fieldByName = new HashMap<>();
        this.fieldViewByName = new HashMap<>();

        // [2] Get & Index Fields
        // -------------------------------------------------------------------------------------

        if (this.numberVariable == null)
            return;

        Collection<Field> fields = new ArrayList<>();

        // GENERATE fields from Value Set
//        try {
//            fields = ProdType.fields(this.numberVariable, this);
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

        layout.addView(Form.headerView("Common Properties", context));

        this.addFieldView("name", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("label", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("kind", layout);

        layout.addView(Form.headerView("Other Properties", context));

        this.addFieldView("is_namespaced", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("tags", layout);


        // > Click Events
        // -------------------------------------------------------------------------------------

        this.setValueListeners();


        return layout;
    }


    private void setValueListeners()
    {
        Field kindField = this.fieldByName.get("kind");

        if (kindField == null)
            return;

        // > Summation Value
//        kindField.setCaseOnClickListener("summation", new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(NumberVariableActivity.this,
//                                           SummationActivity.class);
//                intent.putExtra("summation", numberVariable.summation());
//                startActivity(intent);
//            }
//        });
    }


    private void addFieldView(String fieldName, LinearLayout layout)
    {
        Field field = this.fieldByName.get(fieldName);

        if (field != null) {
            LinearLayout fieldView = field.view(this);
            layout.addView(fieldView);
            this.fieldViewByName.put(fieldName, fieldView);
        }
    }

}
