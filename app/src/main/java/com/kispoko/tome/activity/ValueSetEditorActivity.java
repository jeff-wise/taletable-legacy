
package com.kispoko.tome.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.lib.functor.Functor;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.lib.functor.form.Field;
import com.kispoko.tome.lib.functor.form.Form;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.sheet.SheetManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Value Set Activity
 */
public class ValueSetEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueSet    valueSet;

    private boolean     isEditMode;


    private Map<String,Field> fieldByName;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_value_set);

        // > Read Parameters
        String valueSetName = null;
        if (getIntent().hasExtra("value_set_name")) {
            valueSetName = getIntent().getStringExtra("value_set_name");
        }

        // > Lookup ValueSet
        // -------------------------------------------------------------------------------------

        Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
        this.valueSet = dictionary.lookup(valueSetName);

        // If value set is NULL, assume we creating a new one
        if (this.valueSet == null)
        {
            this.valueSet = new ValueSet();
            this.valueSet.setId(UUID.randomUUID());

            this.isEditMode = false;
        }
        else
        {
            this.isEditMode = true;
        }


        // > Get & Index Fields
        // -------------------------------------------------------------------------------------

        this.fieldByName = new HashMap<>();
        List<Field> fields = new ArrayList<>();

        try
        {
            if (this.isEditMode)
                fields = Functor.fields(this.valueSet, true, this);
            else
                fields = Functor.fields(this.valueSet, false, this);
        }
        catch (FunctorException exception)
        {
            ApplicationFailure.functor(exception);
        }

        for (Field field : fields) {
            this.fieldByName.put(field.name(), field);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        try {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        catch (NullPointerException exception) {

        }

        // > Set the title
        // -------------------------------------------------------------------------------------
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setTypeface(Font.serifFontRegular(this));

        if (this.isEditMode)
            titleView.setText(R.string.value_set_editor);
        else
            titleView.setText(R.string.new_value_set);

        // > Configure Back Button
        // -------------------------------------------------------------------------------------
        ImageView backButtonView   = (ImageView) findViewById(R.id.toolbar_back_button);
        backButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // > Set the Value Set Name
        // -------------------------------------------------------------------------------------
        TextView nameView = (TextView) findViewById(R.id.value_set_name);
        nameView.setTypeface(Font.serifFontBold(this));
        nameView.setText(this.valueSet.label());

    }


    private void initializeView()
    {
        LinearLayout contentView = (LinearLayout) findViewById(R.id.value_set_content);
        contentView.addView(this.view(this));

        // > Initialize FAB
        // -------------------------------------------------------------------------------------
        FloatingActionButton newValueSetButton =
                                    (FloatingActionButton) findViewById(R.id.button_save_value_set);

        if (this.isEditMode)
            newValueSetButton.hide();
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
        //layout.addView(Form.dividerView(context));

        layout.addView(Form.headerView("Other Properties", context));

        this.addFieldView("description", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("label_singular", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("value_type", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("name", layout);
        //layout.addView(Form.dividerView(context));

        return layout;
    }


    private void addFieldView(String fieldName, LinearLayout layout)
    {
        Field field = this.fieldByName.get(fieldName);

        if (field != null)
            layout.addView(field.view());
    }

}
