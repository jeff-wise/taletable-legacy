
package com.kispoko.tome.activity.game.engine.program;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.lib.model.form.Form;
import com.kispoko.tome.model.game.engine.program.Program;
import com.kispoko.tome.util.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * Program Activity
 */
public class ProgramEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Program program;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private Map<String,Field>   fieldByName;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_form_basic);

        // [2] Read parameters
        // -------------------------------------------------------------------------------------

        String programName = null;
        if (getIntent().hasExtra("program_name")) {
            programName = getIntent().getStringExtra("program_name");
        }

        // > Lookup Program
//        ProgramIndex programIndex = SheetManagerOld.currentSheet().engine().programIndex();
//        this.program = programIndex.programWithName(programName);

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
        UI.initializeToolbar(this, getString(R.string.program_editor));
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

        if (this.program == null)
            return;

        Collection<Field> fields = new ArrayList<>();

        // GENERATE fields from Value Set
//        try {
//            fields.addAll(ProdType.fields(this.program, this));
//        }
//        catch (FunctorException exception) {
//            ApplicationFailure.functor(exception);
//        }

        // Add custom field for statements
        fields.add(Field.model("statements",
                        getString(R.string.program_field_statements_label),
                        getString(R.string.program_field_statements_description)));

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

        layout.addView(Form.headerView("Modify the Program", context));

        this.addFieldView("statements", layout);

        layout.addView(Form.headerView("General Properties", context));

        this.addFieldView("name", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("label", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("description", layout);

        layout.addView(Form.headerView("Program Types", context));

        this.addFieldView("parameter_types", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("result_type", layout);

        // > Click Events
        // -------------------------------------------------------------------------------------

        this.setStatementsOnClickListener();


        return layout;
    }


    private void setStatementsOnClickListener()
    {
        Field field = this.fieldByName.get("statements");

        if (field == null)
            return;

//        field.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(ProgramEditorActivity.this,
//                                           StatementsEditorActivity.class);
//                if (program != null)
//                    intent.putExtra("program_name", program.name());
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
        }
    }


}
