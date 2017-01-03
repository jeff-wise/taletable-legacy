
package com.kispoko.tome.activity;


import android.app.Activity;
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
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.RelativeLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



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
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        // > If this is the first sheet, then we are not coming from another sheet, so no back
        //   button is provided
        if (!this.firstSheet)
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_toolbar_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // > Set the title
        String title = "New Character"; // + this.widgetData.getLabel();
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
        LinearLayout layout = newCharacterView();

        RelativeLayout fromTemplateView = buttonView(R.string.from_template,
                                                     R.drawable.ic_from_template,
                                                     R.string.from_template_description,
                                                     GamesActivity.class);
        RelativeLayout fromHubView      = buttonView(R.string.from_hub,
                                                     R.drawable.ic_from_hub,
                                                     R.string.from_hub_description,
                                                     null);
        RelativeLayout fromFileView     = buttonView(R.string.from_file,
                                                     R.drawable.ic_from_file,
                                                     R.string.from_file_description,
                                                     null);

        layout.addView(fromTemplateView);
        layout.addView(fromHubView);
        layout.addView(fromFileView);

        return layout;
    }


    private LinearLayout newCharacterView()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_blue_5;
        layout.padding.bottom   = R.dimen.new_character_layout_padding_bottom;
        layout.padding.left     = R.dimen.new_character_layout_padding_horz;
        layout.padding.right    = R.dimen.new_character_layout_padding_horz;

        return layout.linearLayout(this);
    }


    private RelativeLayout buttonView(int titleStringId,
                                      int iconId,
                                      int descriptionStringId,
                                      final Class<?> nextActivity)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        TextViewBuilder  title       = new TextViewBuilder();
        ImageViewBuilder icon        = new ImageViewBuilder();
        TextViewBuilder  description = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.LINEAR;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = 0;
        layout.weight           = 1.0f;

        layout.backgroundColor  = R.color.dark_blue_9;
        layout.margin.top       = R.dimen.new_character_button_margin_top;
        layout.padding.top      = R.dimen.new_character_button_padding_vert;
        layout.padding.bottom   = R.dimen.new_character_button_padding_vert;

        final Activity thisActivity = this;
        layout.onClick          = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, nextActivity);
                startActivity(intent);
            }
        };

        layout.child(title)
              .child(icon)
              .child(description);

        // [3] Title
        // --------------------------------------------------------------------------------------

        title.layoutType    = LayoutType.RELATIVE;
        title.width         = RelativeLayout.LayoutParams.WRAP_CONTENT;
        title.height        = RelativeLayout.LayoutParams.WRAP_CONTENT;

        title.textId        = titleStringId;
        title.font          = Font.sansSerifFontBold(this);
        title.color         = R.color.gold_6;
        title.margin.bottom = R.dimen.new_character_title_margin_bottom;

        title.addRule(RelativeLayout.CENTER_HORIZONTAL);

        // [4] Icon
        // --------------------------------------------------------------------------------------

        icon.layoutType     = LayoutType.RELATIVE;
        icon.width          = RelativeLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = RelativeLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = iconId;
        icon.margin.bottom  = R.dimen.new_character_icon_margin_bottom;

        icon.addRule(RelativeLayout.CENTER_IN_PARENT);

        // [5] Description
        // --------------------------------------------------------------------------------------

        description.layoutType      = LayoutType.RELATIVE;
        description.width           = RelativeLayout.LayoutParams.WRAP_CONTENT;
        description.height          = RelativeLayout.LayoutParams.WRAP_CONTENT;

        description.textId          = descriptionStringId;
        description.padding.left    = R.dimen.new_character_description_padding_horz;
        description.padding.right   = R.dimen.new_character_description_padding_horz;
        description.color           = R.color.dark_blue_hl_5;
        description.size            = R.dimen.new_character_description_text_size;
        description.font            = Font.sansSerifFontRegular(this);

        description.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        return layout.relativeLayout(this);
    }

}
