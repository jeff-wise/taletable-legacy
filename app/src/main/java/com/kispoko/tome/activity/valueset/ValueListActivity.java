
package com.kispoko.tome.activity.valueset;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.NumberValueEditorActivity;
import com.kispoko.tome.activity.TextValueEditorActivity;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.lib.ui.ActivityCommon;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.util.SimpleDividerItemDecoration;



/**
 * Value List Activity
 */
public class ValueListActivity extends AppCompatActivity
{


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueSet             valueSet;

    private FloatingActionButton addNewValueButton;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_value_list);

        // > Read Parameters
        // --------------------------------------------------------
        this.valueSet = null;
        if (getIntent().hasExtra("value_set")) {
            this.valueSet = (ValueSet) getIntent().getSerializableExtra("value_set");
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
        // > Initalize the toolbar
        // -------------------------------------------------------------------------------------
        ActivityCommon.initializeToolbar(this);

        // > Set the title
        // -------------------------------------------------------------------------------------
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setTypeface(Font.serifFontRegular(this));
        titleView.setText(R.string.values_editor);

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

        if (this.valueSet != null)
            nameView.setText(this.valueSet.label());
        else
            nameView.setText(R.string.not_available);

    }


    private void initializeView()
    {
        // [1] Initalize RecyclerView
        // -------------------------------------------------------------------------------------

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.value_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));


        if (this.valueSet != null) {
            this.valueSet.sortAscByLabel();
            recyclerView.setAdapter(new ValuesRecyclerViewAdapter(this.valueSet));
        }


        // [2] Initalize Floating Action Button
        // -------------------------------------------------------------------------------------
        this.addNewValueButton =
                (FloatingActionButton) findViewById(R.id.button_new_value);

        final ValueListActivity valueListActivity = this;
        this.addNewValueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (valueSet.valueType())
                {
                    case TEXT:
                        Intent textIntent = new Intent(ValueListActivity.this,
                                                       TextValueEditorActivity.class);
                        valueListActivity.startActivity(textIntent);
                        break;
                    case NUMBER:
                        Intent numberIntent = new Intent(ValueListActivity.this,
                                                         NumberValueEditorActivity.class);
                        valueListActivity.startActivity(numberIntent);
                        break;
                }
            }
        });
    }

}
