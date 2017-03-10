
package com.kispoko.tome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.query.CountQuery;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.SectionCard;



/**
 * New Character Activity
 */
public class NewCharacterActivity extends AppCompatActivity
                                  implements CountQuery.OnCountListener
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private boolean firstSheet;


    // ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        CountQuery.fromModel(Sheet.class).run(this);
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
        getMenuInflater().inflate(R.menu.toolbar_new_character, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ON COUNT RESULT
    // -------------------------------------------------------------------------------------------


    public void onCountResult(String modelName, Integer count)
    {

        if (count == 0)
            this.firstSheet = true;
        else
            this.firstSheet = false;

        setContentView(R.layout.activity_new_character);

        initializeToolbar();

        initializeView();
    }


    public void onCountError(DatabaseException exception)
    {

    }


    // INTERNAL
    // -------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar ComponentUtil components.
     */
    private void initializeToolbar()
    {
        // > If this is the first sheet, then we are not coming from another sheet, so no back
        //   button is provided
        if (!this.firstSheet) {
            UI.initializeToolbar(this, true, true);
        }
        else {
            UI.initializeToolbar(this, true, false);
        }

        // > Set the title
        String title = "Create a New Character"; // + this.widgetData.label();
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    /**
     * Initialize the new character view.
     */
    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.new_character_content);
        contentLayout.addView(view());
    }


    /**
     * The activity view.
     * @return The linear layout.
     */
    private LinearLayout view()
    {
        LinearLayout layout = viewLayout();

        // > From Template Button
        // -------------------------------------------------------------------------------------

        RelativeLayout fromTemplateButton =
                SectionCard.view(R.string.from_template,
                                 R.drawable.ic_new_character_template,
                                 R.string.from_template_description,
                                 SectionCard.Color.GOLD,
                                 this);

        fromTemplateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewCharacterActivity.this, GamesActivity.class);
                startActivity(intent);
            }
        });

        // > From Hub Button
        // -------------------------------------------------------------------------------------

        RelativeLayout fromHubButton =
                SectionCard.view(R.string.from_hub,
                                 R.drawable.ic_new_character_hub,
                                 R.string.from_hub_description,
                                 SectionCard.Color.RED_ORANGE,
                                 this);

        // > From File Button
        // -------------------------------------------------------------------------------------

        RelativeLayout fromFileButton =
                SectionCard.view(R.string.from_file,
                                 R.drawable.ic_new_character_file,
                                 R.string.from_file_description,
                                 SectionCard.Color.RED,
                                 this);

        layout.addView(fromTemplateButton);
        layout.addView(fromHubButton);
        layout.addView(fromFileButton);

        return layout;
    }


    private LinearLayout viewLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_blue_11;
        //layout.padding.bottom   = R.dimen.new_character_layout_padding_bottom;
        layout.padding.left     = R.dimen.new_character_layout_padding_horz;
        layout.padding.right    = R.dimen.new_character_layout_padding_horz;
        layout.padding.top      = R.dimen.five_dp;

        return layout.linearLayout(this);
    }

}
