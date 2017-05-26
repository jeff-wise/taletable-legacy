
package com.kispoko.tome.activity.engine.valueset;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.engine.value.ValueListActivity;
import com.kispoko.tome.model.engine.value.Dictionary;
import com.kispoko.tome.model.engine.value.BaseValueSet;
import com.kispoko.tome.model.engine.value.ValueSetType;
import com.kispoko.tome.model.engine.value.ValueSetUnion;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.lib.model.form.Form;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.SheetManagerOld;
import com.kispoko.tome.util.UI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Value Set Activity
 */
public class BaseValueSetEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String                      valueSetName;
    private BaseValueSet                valueSet;

    private Map<String,Field>           fieldByName;
    private Map<String,LinearLayout>    fieldViewByName;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        setContentView(R.layout.activity_value_set);

        // [2] Read Parameters
        this.valueSetName = null;
        if (getIntent().hasExtra("value_set_name")) {
            this.valueSetName = getIntent().getStringExtra("value_set_name");
        }

        // [3] Initialize Data e.g. Value Set, Fields, ..
        this.initializeData();

        // [4] Initialize views
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


    @Override
    public void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        this.initializeData();
        this.initializeView();
    }


    // > Events
    //   (Event Bus Subscriptions)
    // -------------------------------------------------------------------------------------------

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextFieldUpdate(Field.TextUpdateEvent event)
    {
        if (this.valueSet != null && event.modelId().equals(this.valueSet.getId()))
        {
            try
            {
                Model.updateProperty(this.valueSet, event.fieldName(), event.text());

                Field updatedfield = this.fieldByName.get(event.fieldName());
                LinearLayout updatedFieldView = this.fieldViewByName.get(event.fieldName());

                if (updatedfield != null && updatedFieldView !=  null)
                {
                    updatedfield.setValue(event.text(), updatedFieldView);

                    if (event.fieldName().equals("label")) {
                        TextView titleView = (TextView) findViewById(R.id.value_set_name);
                        titleView.setText(this.valueSet.label());
                    }
                }

            }
            catch (FunctorException exception)
            {
                ApplicationFailure.functor(exception);
            }
        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.value_set_editor));

        // > Set the Value Set Name
        // -------------------------------------------------------------------------------------
        TextView nameView = (TextView) findViewById(R.id.value_set_name);
        nameView.setTypeface(Font.serifFontBold(this));
        if (this.valueSet != null)
            nameView.setText(this.valueSet.label());

    }


    private void initializeView()
    {
        LinearLayout contentView = (LinearLayout) findViewById(R.id.value_set_content);
        contentView.removeAllViews();
        contentView.addView(this.view(this));
    }


    // INITIALIZE
    // ------------------------------------------------------------------------------------------

    private void initializeData()
    {
        // [1] Initialize indexes
        // -------------------------------------------------------------------------------------

        this.fieldByName = new HashMap<>();
        this.fieldViewByName = new HashMap<>();

        // [2] Lookup ValueSet
        // -------------------------------------------------------------------------------------

        Dictionary dictionary = SheetManagerOld.dictionary();
        if (this.valueSetName != null && dictionary != null)
        {
            ValueSetUnion valueSetUnion = dictionary.lookup(this.valueSetName);
            if (valueSetUnion.type() == ValueSetType.BASE)
                this.valueSet = valueSetUnion.base();
        }

        // If value set is NULL, assume we creating a new one
        if (this.valueSet == null) {
            this.valueSet = new BaseValueSet();
            this.valueSet.setId(UUID.randomUUID());
        }

        // [3] Get & Index Fields
        // -------------------------------------------------------------------------------------

        Collection<Field> fields = new ArrayList<>();

        // GENERATE fields from Value Set
        try {
            fields = Model.fields(this.valueSet, this);
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

    private ScrollView view(Context context)
    {
        ScrollView scrollView = this.scrollView(context);

        LinearLayout layout = this.viewLayout(context);

        // > Toolbar
        layout.addView(Form.toolbarView(context));

        // > Form
        layout.addView(this.formView(context));


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

        layout.backgroundColor      = R.color.dark_blue_9;

        return layout.linearLayout(context);
    }


    private LinearLayout formView(Context context)
    {
        LinearLayout layout = Form.layout(context);

        layout.addView(Form.headerView("Common Properties", context));

        this.addFieldView("label", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("values", layout);

        layout.addView(Form.headerView("Other Properties", context));

        this.addFieldView("description", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("label_singular", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("value_type", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("name", layout);


        // > Link to deeper editors
        LinearLayout valuesFieldView = this.fieldViewByName.get("values");
        if (valuesFieldView != null)
        {
            valuesFieldView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(BaseValueSetEditorActivity.this,
                                               ValueListActivity.class);
                    intent.putExtra("value_set_name", valueSet.name());
                    startActivity(intent);
                }
            });
        }

        return layout;
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
