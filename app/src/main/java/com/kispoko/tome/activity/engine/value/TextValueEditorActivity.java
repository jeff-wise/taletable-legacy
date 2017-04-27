
package com.kispoko.tome.activity.engine.value;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.TextValue;
import com.kispoko.tome.engine.value.ValueReference;
import com.kispoko.tome.engine.value.BaseValueSet;
import com.kispoko.tome.engine.value.ValueSetType;
import com.kispoko.tome.engine.value.ValueSetUnion;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.lib.model.form.Form;
import com.kispoko.tome.lib.ui.ActivityCommon;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Value Editor Activity
 */
public class TextValueEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String                      valueSetName;
    private String                      valueName;

    private BaseValueSet                valueSet;
    private TextValue                   textValue;


    // > Fields
    // ------------------------------------------------------------------------------------------

    private Map<String,Field>           fieldByName;
    private Map<String,LinearLayout>    fieldViewByName;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_value);


        // [1] Read Parameters
        // -------------------------------------------------------------------------------------

        this.valueSetName = null;
        if (getIntent().hasExtra("value_set_name")) {
            this.valueSetName = getIntent().getStringExtra("value_set_name");
        }

        this.valueName = null;
        if (getIntent().hasExtra("value_name")) {
            this.valueName = getIntent().getStringExtra("value_name");
        }

        // [2] Lookup Value/Set from Dictionary
        // -------------------------------------------------------------------------------------

        Dictionary dictionary = SheetManager.dictionary();

        // [2] Find ValueSet
        if (this.valueSetName != null && dictionary != null)
        {
            ValueSetUnion valueSetUnion = dictionary.lookup(this.valueSetName);
            if (valueSetUnion.type() == ValueSetType.BASE)
                this.valueSet = valueSetUnion.base();
        }

        // [1] Find Value
        this.textValue = null;
        if (this.valueSetName != null && this.valueName != null && dictionary != null)
        {
            ValueReference valueReference =
                                    ValueReference.create(this.valueSetName, this.valueName);
            this.textValue = dictionary.textValue(valueReference);
        }

        // If value is NULL, assume we are creating a new one
        if (this.textValue == null)
            this.textValue = this.createAndSaveNewTextValue();


        // > Get & Index Fields
        // -------------------------------------------------------------------------------------

        this.fieldByName = new HashMap<>();
        Collection<Field> fields = new ArrayList<>();

        try
        {
            fields = Model.fields(this.textValue, this);
        }
        catch (FunctorException exception)
        {
            ApplicationFailure.functor(exception);
        }

        for (Field field : fields) {
            this.fieldByName.put(field.name(), field);
        }

        this.fieldViewByName = new HashMap<>();


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
        UI.initializeToolbar(this, getString(R.string.value_editor));

        // > Set the Value Set Name
        // -------------------------------------------------------------------------------------
        TextView nameView = (TextView) findViewById(R.id.value_name);
        nameView.setTypeface(Font.serifFontBold(this));
        if (this.textValue != null)
            nameView.setText(this.textValue.value());
    }


    private void initializeView()
    {
        // > Content View
        // -------------------------------------------------------------------------------------
        LinearLayout contentView = (LinearLayout) findViewById(R.id.value_content);
        contentView.addView(this.view(this));
    }


    // > New Text Value
    // ------------------------------------------------------------------------------------------

    private TextValue createAndSaveNewTextValue()
    {
        // [1] Set Fields
        // --------------------------------------------------------------------------------------

        Tuple2<String,String> defaultValues = null;
        if (this.valueSet != null)
            defaultValues = this.valueSet.nextDefaultTextValue();

        // ** ID
        UUID   id           = UUID.randomUUID();

        // ** NAME
        String name         = "";
        if (defaultValues != null)
            name = defaultValues.getItem1();

        // ** VALUE
        String value        = "";
        if (defaultValues != null)
            value = defaultValues.getItem2();

        // ** DESCRIPTION
        String description  = "Add a description";

        // [2] Create Text Value
        // --------------------------------------------------------------------------------------

        TextValue textValue = new TextValue(id, name, value, description,
                                            new ArrayList<VariableUnion>());

        // [3] Save Value
        // --------------------------------------------------------------------------------------

        if (this.valueSet != null)
            this.valueSet.addValue(textValue);

        return textValue;
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private ScrollView view(Context context)
    {
        ScrollView scrollView = this.scrollView(context);

        LinearLayout layout = this.viewLayout(context);

        // > Toolbar
        layout.addView(Form.toolbarView(context));

        // > Form
        layout.addView(this.editFormView(context));


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

        //layout.backgroundColor      = R.color.dark_blue_9;

        return layout.linearLayout(context);
    }


    // ** Forms
    // ------------------------------------------------------------------------------------------

    private LinearLayout newFormView(Context context)
    {
        LinearLayout layout = Form.layout(context);

        for (Field field : this.fieldByName.values()) {
            layout.addView(field.view(this));
        }

        return layout;
    }


    private LinearLayout editFormView(Context context)
    {
        LinearLayout layout = Form.layout(context);

        layout.addView(Form.headerView("Common Properties", context));

        this.addEditFieldView("value", layout);
        layout.addView(Form.dividerView(context));
        this.addEditFieldView("description", layout);

        layout.addView(Form.headerView("Other Properties", context));

        this.addEditFieldView("name", layout);
        layout.addView(Form.dividerView(context));
        this.addEditFieldView("variables", layout);

        // > Link to deeper editors
        LinearLayout variablesFieldView = this.fieldViewByName.get("variables");
        if (variablesFieldView != null)
        {
            variablesFieldView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
//                    Intent intent = new Intent(BaseValueSetEditorActivity.this,
//                                               ValueListActivity.class);
//                    intent.putExtra("value_set_name", valueSet.name());
//                    startActivity(intent);
                }
            });
        }

        return layout;
    }


    private void addEditFieldView(String fieldName, LinearLayout layout)
    {
        Field field = this.fieldByName.get(fieldName);

        if (field != null) {
            LinearLayout fieldView = field.view(this);
            layout.addView(fieldView);
            this.fieldViewByName.put(fieldName, fieldView);
        }
    }


    private void addNewEditFieldView(String fieldName, LinearLayout layout)
    {
        Field field = this.fieldByName.get(fieldName);

        if (field != null) {
            LinearLayout fieldView = field.view(this);
            layout.addView(fieldView);
            this.fieldViewByName.put(fieldName, fieldView);
        }
    }

}
