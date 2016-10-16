
package com.kispoko.tome.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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



/**
 * Choose Template Game Activity
 */
public class ChooseTemplateGameActivity extends AppCompatActivity
{


    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private ArrayList<Sheet.Game> templateGames;


    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_template_game);

        initializeToolbar();

        this.templateGames = Sheet.templateGames(this);

        renderGameButtons();
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

        actionBar.setTitle("Choose Template Game");
        actionBar.setElevation(0);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_toolbar_back_arrow_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Generate the buttons for selecting the template game.
     */
    private void renderGameButtons()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.game_list);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(Util.linearLayoutParamsMatch());
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.bluegrey_900));

        scrollView.addView(layout);

        for (Sheet.Game game : this.templateGames)
        {
            // Game Layout
            LinearLayout gameLayout = new LinearLayout(this);
            gameLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams gameLayoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                  LinearLayout.LayoutParams.WRAP_CONTENT);
            gameLayout.setLayoutParams(gameLayoutParams);

            int gameLayoutPaddingLeft =
                    (int) Util.getDim(this, R.dimen.choose_template_game_game_layout_padding_left);
            int gameLayoutPaddingTop =
                    (int) Util.getDim(this, R.dimen.choose_template_game_game_layout_padding_top);
            int gameLayoutPaddingBottom =
                    (int) Util.getDim(this, R.dimen.choose_template_game_game_layout_padding_bottom);
            gameLayout.setPadding(gameLayoutPaddingLeft, gameLayoutPaddingTop,
                                  0, gameLayoutPaddingBottom);

            final ChooseTemplateGameActivity thisActivity = this;
            final Sheet.Game thisGame = game;
            gameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChooseTemplateGameActivity.this, ChooseTemplateActivity.class);

                    Bundle bundle = new Bundle();
                    String gameId = thisGame.getId();
                    bundle.putString("game_id", gameId);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            });


            // Info Layout
            LinearLayout infoLayout = new LinearLayout(this);
            infoLayout.setGravity(Gravity.CENTER_VERTICAL);
            infoLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoLayoutParams =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            infoLayoutParams.weight = 4;
            infoLayout.setLayoutParams(infoLayoutParams);


            // Game Name
            TextView gameNameView = new TextView(this);

            LinearLayout.LayoutParams gameNameViewLayoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                  LinearLayout.LayoutParams.WRAP_CONTENT);
            gameNameView.setLayoutParams(gameNameViewLayoutParams);


            gameNameView.setTextColor(ContextCompat.getColor(this, R.color.amber_300));

            float nameTextSize = Util.getDim(this, R.dimen.choose_template_game_name_text_size);
            gameNameView.setTextSize(nameTextSize);

            gameNameView.setTypeface(Util.serifFontBold(this));

            gameNameView.setText(game.getLabel());


            // Game Players Layout
            LinearLayout gamePlayersLayout = new LinearLayout(this);
            LinearLayout.LayoutParams gamePlayersLayoutParams
                    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
            gamePlayersLayout.setGravity(Gravity.CENTER_VERTICAL);
            int gamePlayersLayoutPaddingVert =
                    (int) Util.getDim(this, R.dimen.choose_template_game_players_padding_vert);
            gamePlayersLayout.setPadding(0, gamePlayersLayoutPaddingVert,
                                         0, gamePlayersLayoutPaddingVert);
            gamePlayersLayout.setLayoutParams(gamePlayersLayoutParams);


            // >> Icon
            ImageView gamePlayersIcon = new ImageView(this);

            gamePlayersIcon.setLayoutParams(Util.linearLayoutParamsWrap());

            gamePlayersIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_game_players));
            int gamePlayersIconPaddingRight =
                    (int) Util.getDim(this,
                                      R.dimen.choose_template_game_players_icon_padding_right);
            gamePlayersIcon.setPadding(0, 0, gamePlayersIconPaddingRight, 0);


            // >> Number
            TextView gamePlayersNumberView = new TextView(this);

            gamePlayersNumberView.setLayoutParams(Util.linearLayoutParamsWrap());

            int numberOfPlayers = Statistics.gamePlayers(game.getId());
            gamePlayersNumberView.setText(Integer.toString(numberOfPlayers));
            gamePlayersNumberView.setTextColor(ContextCompat.getColor(this, R.color.bluegrey_100));

            int gamePlayersNumberPaddingRight =
                    (int) Util.getDim(this,
                            R.dimen.choose_template_game_players_number_padding_right);
            gamePlayersNumberView.setPadding(0, 0, gamePlayersNumberPaddingRight, 0);

            float gamePlayersNumberTextSize =
                    Util.getDim(this, R.dimen.choose_template_game_players_number_text_size);
            gamePlayersNumberView.setTextSize(gamePlayersNumberTextSize);
            //gamePlayersNumberView.setTypeface(null, Typeface.BOLD);


            // >> Label
            TextView gamePlayersLabelView = new TextView(this);

            gamePlayersLabelView.setLayoutParams(Util.linearLayoutParamsWrap());

            gamePlayersLabelView.setText("Players");
            gamePlayersLabelView.setTextColor(ContextCompat.getColor(this, R.color.bluegrey_100));
            float gamePlayersLabelTextSize =
                    Util.getDim(this, R.dimen.choose_template_game_players_label_text_size);
            gamePlayersLabelView.setTextSize(gamePlayersLabelTextSize);
            //gamePlayersLabelView.setTypeface(null, Typeface.BOLD);


            // Game Description Layout
            LinearLayout gameDescLayout = new LinearLayout(this);
            LinearLayout.LayoutParams gameDescLayoutParams
                    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
            gameDescLayout.setLayoutParams(gameDescLayoutParams);
            int gameDescLayoutPaddingVert =
                    (int) Util.getDim(this, R.dimen.choose_template_game_desc_padding_vert);
            gameDescLayout.setPadding(0, gameDescLayoutPaddingVert, 0, 0);

            // >> Description View
            TextView gameDescView = new TextView(this);
            gameDescView.setText(game.getDescription());
            gameDescView.setTextColor(ContextCompat.getColor(this, R.color.bluegrey_200));


            // Icon
            ImageView nextIcon = new ImageView(this);
            nextIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_choose_template_game_next));
            LinearLayout.LayoutParams iconLayoutParams =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            iconLayoutParams.weight = 1;
            iconLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            nextIcon.setLayoutParams(iconLayoutParams);



            // Configure Layouts

            gamePlayersLayout.addView(gamePlayersIcon);
            gamePlayersLayout.addView(gamePlayersNumberView);
            gamePlayersLayout.addView(gamePlayersLabelView);

            gameDescLayout.addView(gameDescView);

            infoLayout.addView(gameNameView);
            infoLayout.addView(gamePlayersLayout);
            infoLayout.addView(gameDescLayout);


            gameLayout.addView(infoLayout);
            gameLayout.addView(nextIcon);

            layout.addView(divider());
            layout.addView(gameLayout);
        }
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
