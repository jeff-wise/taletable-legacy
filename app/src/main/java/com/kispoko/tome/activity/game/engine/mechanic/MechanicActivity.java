
package com.kispoko.tome.activity.game.engine.mechanic;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.lib.model.form.Form;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.model.game.engine.mechanic.Mechanic;
import com.kispoko.tome.util.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * Mechanic Activity
 */
public class MechanicActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Mechanic mechanic;

    // > Form
    // -----------------------------------------------------------------------------------------

    private Map<String,Field>   fieldByName;


    // ACTIVITY LIFECYCLE EVENTS
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_form_basic);

        // [2] Get Parameters
        // -------------------------------------------------------------------------------------

        String mechanicName = null;
        if (getIntent().hasExtra("mechanic_name")) {
            mechanicName = getIntent().getStringExtra("mechanic_name");
        }

        // > Lookup Mechanic
//        MechanicIndex mechanicIndex = SheetManagerOld.mechanicIndex();
//        if (mechanicIndex != null && mechanicName != null)
//            this.mechanic = mechanicIndex.mechanicWithName(mechanicName);
//        else
//            this.mechanic = null;

        // [3] Initialize UI
        // -------------------------------------------------------------------------------------

        initializeToolbar();
        initializeData();
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
        UI.initializeToolbar(this, getString(R.string.mechanic_editor));
    }


    private void initializeView()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.content);
        scrollView.addView(this.view(this));
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private void initializeData()
    {
        // [1] Initialize indexes
        // -------------------------------------------------------------------------------------

        this.fieldByName = new HashMap<>();

        // [2] Get & Index Fields
        // -------------------------------------------------------------------------------------

        if (this.mechanic == null)
            return;

        Collection<Field> fields = new ArrayList<>();

        // GENERATE fields from Value Set
//        try {
//            //fields.addAll(Model.fields(this.mechanic, this));
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

        layout.addView(Form.headerView("Descriptive Properties", context));

        this.addFieldView("label", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("category", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("summary", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("description", layout);

        layout.addView(Form.headerView("Functional Properties", context));

        this.addFieldView("requirements", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("variables", layout);

        layout.addView(Form.headerView("Other Properties", context));

        this.addFieldView("name", layout);

        // > Click Events
        // -------------------------------------------------------------------------------------

        this.setOnVariableListClickListener(context);


        return layout;
    }


    private void setOnVariableListClickListener(final Context context)
    {
        Field field = this.fieldByName.get("variables");

        if (field != null)
        {
//            field.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    Intent intent = new Intent(MechanicActivity.this, VariableListActivity.class);
//                    intent.putExtra("variables", (Serializable) mechanic.variables());
//                    context.startActivity(intent);
//                }
//            });
        }
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
