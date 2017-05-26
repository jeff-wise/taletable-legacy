
package com.kispoko.tome.activity.sheet.widget;


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
import com.kispoko.tome.SheetManagerOld;
import com.kispoko.tome.activity.engine.variable.NumberVariableActivity;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.lib.model.form.Form;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.model.sheet.widget.ActionWidget;
import com.kispoko.tome.util.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Action Widget Activity
 */
public class ActionWidgetActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private ActionWidget                actionWidget;


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
        UUID widgetId = null;
        if (getIntent().hasExtra("widget_id")) {
            widgetId = (UUID) getIntent().getSerializableExtra("widget_id");
        }

        if (widgetId != null)
        {
            WidgetUnion widgetUnion = SheetManagerOld.currentSheet().widgetWithId(widgetId);

            if (widgetUnion != null && widgetUnion.type() == WidgetType.ACTION)
                actionWidget = widgetUnion.actionWidget();
        }

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
        UI.initializeToolbar(this, getString(R.string.widget_action));
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

        if (this.actionWidget == null)
            return;

        Collection<Field> fields = new ArrayList<>();

        // GENERATE fields from Value Set
        try {
            fields = Model.fields(this.actionWidget, this);
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

        layout.backgroundColor      = R.color.dark_blue_9;

        return layout.linearLayout(context);
    }


    private LinearLayout formView(Context context)
    {
        LinearLayout layout = Form.layout(context);

        // > Form Structure
        // -------------------------------------------------------------------------------------

        layout.addView(Form.headerView("General Properties", context));

        this.addFieldView("action_name", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("action_result", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("modifier", layout);

        layout.addView(Form.headerView("UI Properties", context));

        this.addFieldView("description", layout);
        layout.addView(Form.dividerView(context));
        this.addFieldView("action_highlight", layout);

        layout.addView(Form.headerView("Widget Properties", context));
        this.addFieldView("format", layout);

        // > Click Events
        // -------------------------------------------------------------------------------------

        this.setModifierFieldClickListener();

        return layout;
    }


    private void setModifierFieldClickListener()
    {
        LinearLayout modifierFieldView = this.fieldViewByName.get("modifier");

        if (modifierFieldView == null)
            return;

        modifierFieldView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ActionWidgetActivity.this,
                                           NumberVariableActivity.class);
                intent.putExtra("number_variable", actionWidget.modifierVariable());
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
            this.fieldViewByName.put(fieldName, fieldView);
        }
    }
}
