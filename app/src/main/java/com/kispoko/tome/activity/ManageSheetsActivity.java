
package com.kispoko.tome.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.DatabaseManager;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



/**
 * Switch Character Screen
 */
public class ManageSheetsActivity extends AppCompatActivity
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------


    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DatabaseManager databaseManager = new DatabaseManager(this);
        SQLiteDatabase database = databaseManager.getWritableDatabase();

        Sheet.summaryInfo(database, this);

        setContentView(R.layout.activity_manage_sheets);

        initializeToolbar();
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


    // > API
    // -------------------------------------------------------------------------------------------

    public void renderSheetSummaries(List<Sheet.SummaryInfo> summaryInfos)
    {
        ScrollView sheetsMainView = (ScrollView) findViewById(R.id.sheet_list);

        // Configure Floating Action Button
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.button_new_sheet);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageSheetsActivity.this, NewCharacterActivity.class);
                startActivity(intent);
            }
        });

        // Summaries Layout
        LinearLayout summariesLayout = new LinearLayout(this);
        summariesLayout.setOrientation(LinearLayout.VERTICAL);
        summariesLayout.setLayoutParams(Util.linearLayoutParamsMatch());

        int index = 0;
        for (Sheet.SummaryInfo summaryInfo : summaryInfos)
        {
            LinearLayout summaryLayout = summaryLayout();

            LinearLayout sheetInformationLayout = sheetInformationLayout();
            sheetInformationLayout.addView( characterNameView(summaryInfo.getName()) );
            sheetInformationLayout.addView( lastUsedView(summaryInfo.getLastUsed()) );
            sheetInformationLayout.addView( campaignNameView(index) );

            // Create view structure
            summaryLayout.addView(sheetInformationLayout);
            summaryLayout.addView(nextIcon());

            summariesLayout.addView(summaryLayout);
            summariesLayout.addView(UI.divider(this));

            index += 1;
        }

        // Create view structure
        sheetsMainView.addView(summariesLayout);
    }


    // > INTERNAL
    // -------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar ComponentUtil components.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_toolbar_back);
    }


    // >> Views
    // -------------------------------------------------------------------------------------------

    private LinearLayout summaryLayout()
    {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(Util.linearLayoutParamsMatchWrap());

        int layoutPaddingVert = (int) Util.getDim(this,
                                            R.dimen.manage_sheets_summary_layout_padding_vert);
        int layoutPaddingHorz = (int) Util.getDim(this,
                                            R.dimen.manage_sheets_summary_layout_padding_horz);
        layout.setPadding(layoutPaddingHorz, layoutPaddingVert,
                          layoutPaddingHorz, layoutPaddingVert);
        return layout;
    }

    private LinearLayout sheetInformationLayout()
    {
        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 8;
        layout.setLayoutParams(layoutParams);

        return layout;
    }

    private TextView characterNameView(String characterName)
    {
        TextView view = new TextView(this);
        view.setText(characterName);

        float textSize = Util.getDim(this, R.dimen.manage_sheets_char_name_text_size);
        view.setTextSize(textSize);

        view.setTextColor(ContextCompat.getColor(this, R.color.amber_200));
        view.setTypeface(Util.serifFontBold(this));

        int paddingBottom = (int) Util.getDim(this, R.dimen.manage_sheets_char_name_padding_bottom);
        int paddingLeft = (int) Util.getDim(this, R.dimen.manage_sheets_char_name_padding_left);
        view.setPadding(paddingLeft, 0, 0, paddingBottom);

        return view;
    }

    private ImageView nextIcon()
    {
        ImageView nextIcon = new ImageView(this);

        nextIcon.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_manage_sheets_next));

        LinearLayout.LayoutParams iconLayoutParams =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        iconLayoutParams.weight = 1;
        iconLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        nextIcon.setLayoutParams(iconLayoutParams);

        return nextIcon;
    }


    private LinearLayout lastUsedView(Calendar lastUsed)
    {
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(Util.linearLayoutParamsMatchWrap());
        layout.setGravity(Gravity.CENTER_VERTICAL);

        int layoutPaddingVert =
                (int) Util.getDim(this, R.dimen.manage_sheets_info_item_layout_padding_vert);
        layout.setPadding(0, layoutPaddingVert, 0, layoutPaddingVert);

        // >> Icon
        ImageView dateIcon = new ImageView(this);

        dateIcon.setLayoutParams(Util.linearLayoutParamsWrap());

        dateIcon.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_sheet_last_used));
        int dateIconPaddingRight =
                (int) Util.getDim(this, R.dimen.manage_sheets_info_item_icon_padding_right);
        dateIcon.setPadding(0, 0, dateIconPaddingRight, 0);

        // >> Last Used View
        TextView lastUsedView = new TextView(this);

        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        lastUsedView.setText(dateFormat.format(lastUsed.getTime()));

        float lastUsedTextSize = Util.getDim(this, R.dimen.manage_sheets_info_item_text_size);
        lastUsedView.setTextSize(lastUsedTextSize);

        lastUsedView.setTextColor(ContextCompat.getColor(this, R.color.bluegrey_200));
        lastUsedView.setTypeface(Util.sansSerifFontRegular(this));

        // Create layout structure
        layout.addView(dateIcon);
        layout.addView(lastUsedView);

        return layout;
    }


    private LinearLayout campaignNameView(int index)
    {
        String[] mockCampaignNames = {
                "Restless",
                "Keep on the Borderlands",
                "Subterranean Myth",
                "Trouble in the City of Towers",
                "The Endless War",
                "Waves of Eden",
                "Forgotten Star",
                "Orc King",
                "Darkness Alight",
                "Distant Shores",
        };


        // Layout
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(Util.linearLayoutParamsMatchWrap());
        layout.setGravity(Gravity.CENTER_VERTICAL);

        int layoutPaddingVert =
                (int) Util.getDim(this, R.dimen.manage_sheets_info_item_layout_padding_vert);
        layout.setPadding(0, layoutPaddingVert, 0, layoutPaddingVert);


        // > Campaign Icon
        ImageView campaignIcon = new ImageView(this);

        campaignIcon.setLayoutParams(Util.linearLayoutParamsWrap());

        campaignIcon.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_campaign));
        int dateIconPaddingRight =
                (int) Util.getDim(this, R.dimen.manage_sheets_info_item_icon_padding_right);
        campaignIcon.setPadding(0, 0, dateIconPaddingRight, 0);


        // > Campaign Name
        TextView campaignNameView = new TextView(this);

        campaignNameView.setText(mockCampaignNames[index]);

        float textSize = Util.getDim(this, R.dimen.manage_sheets_info_item_text_size);
        campaignNameView.setTextSize(textSize);

        campaignNameView.setTextColor(ContextCompat.getColor(this, R.color.bluegrey_200));

        // Create layout structure
        layout.addView(campaignIcon);
        layout.addView(campaignNameView);

        return layout;
    }




}
