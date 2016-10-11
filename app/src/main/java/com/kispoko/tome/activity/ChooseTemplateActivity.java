
package com.kispoko.tome.activity;


import android.os.Bundle;
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
import com.kispoko.tome.Statistics;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.Map;


/**
 * Choose Template Activity
 */
public class ChooseTemplateActivity extends AppCompatActivity
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private String gameId;
    private Map<String, ArrayList<Sheet.Name>> namesByGame;



    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_template);

        initializeToolbar();

        // Get game id passed from previous activity
        Bundle extras = getIntent().getExtras();
        this.gameId = extras.getString("GAME_ID");

        this.namesByGame = Sheet.templateNamesByGame(this);

        renderTemplates();

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
        getMenuInflater().inflate(R.menu.toolbar_choose_template, menu);
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


    // > INTERNAL
    // -------------------------------------------------------------------------------------------


    /**
     * Initialize the toolbar ComponentUtil components.
     */
    private void initializeToolbar()
    {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Choose Template");
        actionBar.setElevation(0);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_toolbar_back_arrow_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Render the list of templates.
     */
    private void renderTemplates()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.template_list);

        LinearLayout templateListLayout = new LinearLayout(this);
        templateListLayout.setOrientation(LinearLayout.VERTICAL);


        ArrayList<Sheet.Name> templateNames = this.namesByGame.get(this.gameId);
        for (Sheet.Name name : templateNames)
        {
            // Template Layout
            LinearLayout templateLayout = new LinearLayout(this);

            templateLayout.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams templateLayoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                  LinearLayout.LayoutParams.WRAP_CONTENT);
            templateLayout.setLayoutParams(templateLayoutParams);
            templateLayout.setOrientation(LinearLayout.HORIZONTAL);

            int templateLayoutPaddingTop =
                    (int) Util.getDim(this, R.dimen.choose_template_layout_padding_top);
            int templateLayoutPaddingBottom =
                    (int) Util.getDim(this, R.dimen.choose_template_layout_padding_bottom);
            templateLayout.setPadding(0, templateLayoutPaddingTop, 0, templateLayoutPaddingBottom);


            // >> Add Icon
            ImageView iconView = new ImageView(this);

            iconView.setLayoutParams(Util.linearLayoutParamsWrap());

            iconView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_choose_template));

            int iconViewPaddingLeft =
                    (int) Util.getDim(this, R.dimen.choose_template_add_icon_padding_left);
            int iconViewPaddingRight =
                    (int) Util.getDim(this, R.dimen.choose_template_add_icon_padding_right);
            iconView.setPadding(iconViewPaddingLeft, 0, iconViewPaddingRight, 0);


            // Info Layout
            LinearLayout infoLayout = new LinearLayout(this);

            infoLayout.setGravity(Gravity.CENTER_VERTICAL);
            infoLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams infoLayoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                  LinearLayout.LayoutParams.WRAP_CONTENT);
            infoLayout.setLayoutParams(infoLayoutParams);


            // >> Name View
            TextView nameView = new TextView(this);

            nameView.setLayoutParams(Util.linearLayoutParamsWrap());

            nameView.setText(name.getLabel());

            float nameViewTextSize = Util.getDim(this, R.dimen.choose_template_name_text_size);
            nameView.setTextSize(nameViewTextSize);

            nameView.setTextColor(ContextCompat.getColor(this, R.color.amber_300));

            nameView.setTypeface(Util.serifFontBold(this));


            // Templates Created Layout
            LinearLayout templatesCreatedLayout = new LinearLayout(this);
            LinearLayout.LayoutParams templatesCreatedLayoutParams
                    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
            templatesCreatedLayout.setGravity(Gravity.CENTER_VERTICAL);
            int templatesCreatedLayoutPaddingVert =
                    (int) Util.getDim(this, R.dimen.choose_template_created_padding_vert);
            templatesCreatedLayout.setPadding(0, templatesCreatedLayoutPaddingVert,
                                         0, templatesCreatedLayoutPaddingVert);
            templatesCreatedLayout.setLayoutParams(templatesCreatedLayoutParams);


            // >> Icon
            ImageView templatesCreatedIcon = new ImageView(this);

            templatesCreatedIcon.setLayoutParams(Util.linearLayoutParamsWrap());

            templatesCreatedIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_templates_created));
            int gamePlayersIconPaddingRight =
                    (int) Util.getDim(this,
                                      R.dimen.choose_template_created_icon_padding_right);
            templatesCreatedIcon.setPadding(0, 0, gamePlayersIconPaddingRight, 0);


            // >> Number
            TextView templatesCreatedNumberView = new TextView(this);

            templatesCreatedNumberView.setLayoutParams(Util.linearLayoutParamsWrap());

            int numberOfPlayers = Statistics.templatesCreated(
                    Sheet.officialTemplateId(this.gameId, name.getName()));
            templatesCreatedNumberView.setText(Integer.toString(numberOfPlayers));
            templatesCreatedNumberView.setTextColor(
                    ContextCompat.getColor(this, R.color.bluegrey_100));

            int templatesCreatedNumberPaddingRight =
                    (int) Util.getDim(this,
                            R.dimen.choose_template_created_number_padding_right);
            templatesCreatedNumberView.setPadding(0, 0, templatesCreatedNumberPaddingRight, 0);

            float templatesCreatedNumberTextSize =
                    Util.getDim(this, R.dimen.choose_template_created_number_text_size);
            templatesCreatedNumberView.setTextSize(templatesCreatedNumberTextSize);
            //gamePlayersNumberView.setTypeface(null, Typeface.BOLD);


            // >> Label
            TextView templatesCreatedLabelView = new TextView(this);

            templatesCreatedLabelView.setLayoutParams(Util.linearLayoutParamsWrap());

            templatesCreatedLabelView.setText("Created");
            templatesCreatedLabelView.setTextColor(
                    ContextCompat.getColor(this, R.color.bluegrey_100));

            float gamePlayersLabelTextSize =
                    Util.getDim(this, R.dimen.choose_template_game_players_label_text_size);
            templatesCreatedLabelView.setTextSize(gamePlayersLabelTextSize);


            // Game Description Layout
            LinearLayout templateDescLayout = new LinearLayout(this);
            LinearLayout.LayoutParams gameDescLayoutParams
                    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
            templateDescLayout.setLayoutParams(gameDescLayoutParams);
            int gameDescLayoutPaddingVert =
                    (int) Util.getDim(this, R.dimen.choose_template_desc_padding_vert);
            templateDescLayout.setPadding(0, gameDescLayoutPaddingVert, 0, 0);

            // >> Description View
            TextView templateDescView = new TextView(this);
            templateDescView.setText(name.getDescription());
            templateDescView.setTextColor(ContextCompat.getColor(this, R.color.bluegrey_200));


            // Construct Views
            templateDescLayout.addView(templateDescView);

            templatesCreatedLayout.addView(templatesCreatedIcon);
            templatesCreatedLayout.addView(templatesCreatedNumberView);
            templatesCreatedLayout.addView(templatesCreatedLabelView);

            infoLayout.addView(nameView);
            infoLayout.addView(templatesCreatedLayout);
            infoLayout.addView(templateDescLayout);

            templateLayout.addView(iconView);
            templateLayout.addView(infoLayout);

            templateListLayout.addView(divider());
            templateListLayout.addView(templateLayout);
        }

        scrollView.addView(templateListLayout);

    }


    private View divider()
    {
        View dividerView = new View(this);

        int one_dp = (int) Util.getDim(this, R.dimen.one_dp);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, one_dp);
        dividerView.setLayoutParams(layoutParams);

        dividerView.setBackgroundColor(ContextCompat.getColor(this, R.color.bluegrey_800));

        return dividerView;
    }


}
