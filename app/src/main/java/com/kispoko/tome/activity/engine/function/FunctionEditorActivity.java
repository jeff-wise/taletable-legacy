
package com.kispoko.tome.activity.engine.function;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.function.Function;
import com.kispoko.tome.engine.function.FunctionIndex;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.lib.model.form.Form;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * Function Activity
 */
public class FunctionEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Function            function;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private Map<String,Field>   fieldByName;


    // ACTIVITY LIFECYCLE EVENTS
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_form_basic);

        // [2] Read parameters
        // -------------------------------------------------------------------------------------

        String functionName = null;
        if (getIntent().hasExtra("function_name")) {
            functionName = getIntent().getStringExtra("function_name");
        }

        // > Lookup Function
        FunctionIndex functionIndex = SheetManager.currentSheet().engine().functionIndex();
        this.function = functionIndex.functionWithName(functionName);

        // [3] Initialize UI
        // --------------------------------------------------------------------------------------

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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.function_editor));
    }


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

        if (this.function == null)
            return;

        Collection<Field> fields = new ArrayList<>();

        // GENERATE fields from Value Set
        try {
            fields.addAll(Model.fields(this.function, this));
        }
        catch (FunctorException exception) {
            ApplicationFailure.functor(exception);
        }

        // INDEX fields by name
        for (Field field : fields) {
            this.fieldByName.put(field.name(), field);
        }
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout view(Context context)
    {
        LinearLayout layout = Form.layout(context);

        // > Toolbar
        layout.addView(Form.toolbarView(context));

        // > Form
        layout.addView(this.formView(context));

        return layout;
    }


    private LinearLayout formView(Context context)
    {
        LinearLayout layout = Form.layout(context);

        // > Form Structure
        // -------------------------------------------------------------------------------------

        layout.addView(Form.headerView("General Properties", context));

        this.addFieldView("name", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("label", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("description", layout);

        layout.addView(Form.headerView("Definition Properties", context));

        this.addFieldView("parameter_types", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("result_type", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("tuples", layout);


        // > Click Events
        // -------------------------------------------------------------------------------------

        this.setTuplesOnClickListener();


        return layout;
    }


    private void setTuplesOnClickListener()
    {
        Field field = this.fieldByName.get("tuples");

        if (field == null)
            return;

        // > Variable Reference
        field.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(FunctionEditorActivity.this,
                                           TuplesEditorActivity.class);
                if (function != null)
                    intent.putExtra("function_name", function.name());
                startActivity(intent);
            }
        });
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
