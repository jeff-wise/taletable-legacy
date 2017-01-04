
package com.kispoko.tome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.game.GameIndex;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.ScrollViewBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.yaml.YamlException;


/**
 * Choose Template Game Activity
 */
public class GamesActivity extends AppCompatActivity
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private GameIndex gameIndex;


    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_games);

        initializeToolbar();

        try {
            this.gameIndex = GameIndex.fromManifest(this);
        } catch (YamlException e) {
            Log.d("***CHOOSEGAME", e.errorMessage(), e);
        } catch (Exception e) {
            // TODO handle gracefully
            e.printStackTrace();
        }

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
        }
        return super.onOptionsItemSelected(item);
    }


    // > INTERNAL
    // -------------------------------------------------------------------------------------------


    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_toolbar_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // > Set the title
        String title = "Which Game Do You Play?";
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    /**
     * Initialize the game list view.
     */
    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.new_character_content);
        contentLayout.addView(view());
    }


    private ScrollView view()
    {
        ScrollView scrollView = gameChooserView();

        // [1] Create List Layout
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder listLayoutBuilder = new LinearLayoutBuilder();
        listLayoutBuilder.orientation  = LinearLayout.VERTICAL;
        listLayoutBuilder.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        listLayoutBuilder.height       = LinearLayout.LayoutParams.MATCH_PARENT;
        listLayoutBuilder.padding.top  = R.dimen.games_padding_top;

        LinearLayout listLayout = listLayoutBuilder.linearLayout(this);

        scrollView.addView(listLayout);

        // [2] Add Game Buttons
        // --------------------------------------------------------------------------------------

        for (Game game : this.gameIndex.games())
        {
            listLayout.addView(gameButton(game));
        }

        return scrollView;
    }


    private ScrollView gameChooserView()
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT;

        return scrollView.scrollView(this);
    }


    /**
     * The game button view.
     * @param game The game.
     * @return The game button Linear Layout.
     */
    private LinearLayout gameButton(Game game)
    {
        // [1] Create Layout
        // --------------------------------------------------------------------------------------

        LinearLayout layout = gameButtonLayout(game);

        // [2 A] Add Title
        // --------------------------------------------------------------------------------------

        layout.addView(titleView(game.label()));

        // [2 B] Add Description
        // --------------------------------------------------------------------------------------

        layout.addView(descriptionView(game.description()));

        // [2 C] Add Facts
        // --------------------------------------------------------------------------------------

        LinearLayout factsRow1  = factsLayout();
        LinearLayout factsRow2  = factsLayout();

        LinearLayout genreView    = factView("GENRE", game.genre());
        LinearLayout playersView = factView("PLAYERS", game.players().toString());
        LinearLayout createdView  = factView("CREATED", game.created());
        LinearLayout creatorsView = factView("CREATORS", game.creators());

        factsRow1.addView(genreView);
        factsRow1.addView(playersView);
        factsRow2.addView(createdView);
        factsRow2.addView(creatorsView);

        layout.addView(factsRow1);
        layout.addView(factsRow2);


        return layout;
    }


    /**
     * The game button Linear Layout.
     * @return The Linear Layout.
     */
    private LinearLayout gameButtonLayout(final Game game)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left     = R.dimen.games_button_layout_padding_horz;
        layout.padding.right    = R.dimen.games_button_layout_padding_horz;
        layout.margin.bottom    = R.dimen.games_button_layout_margin_bottom;

        layout.onClick          = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(GamesActivity.this, TemplatesActivity.class);

                Bundle bundle = new Bundle();
                String gameId = game.name();
                bundle.putString("game_id", gameId);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        };

        return layout.linearLayout(this);
    }


    /**
     * The game button title view.
     * @param titleString The title.
     * @return The title Text View.
     */
    private TextView titleView(String titleString)
    {
        TextViewBuilder title = new TextViewBuilder();

        title.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        title.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        title.text          = titleString;
        title.size          = R.dimen.games_button_title_text_size;
        title.color         = R.color.gold_6;
        title.font          = Font.sansSerifFontBold(this);
        title.margin.bottom = R.dimen.games_button_title_margin_bottom;

        return title.textView(this);
    }


    /**
     * The game button's description view.
     * @param descriptionString The description.
     * @return The description Text View.
     */
    private TextView descriptionView(String descriptionString)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.text          = descriptionString;
        description.font          = Font.sansSerifFontRegular(this);
        description.color         = R.color.dark_blue_hl_8;

        return description.textView(this);
    }


    private LinearLayout factsLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottom    = R.dimen.games_button_facts_layout_margin_bottom;

        return layout.linearLayout(this);
    }


    private LinearLayout factView(String factName, String factValue)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout   = new LinearLayoutBuilder();
        TextViewBuilder     nameView = new TextViewBuilder();
        TextViewBuilder     factView = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.right         = R.dimen.games_button_fact_layout_margin_right;

        layout.backgroundResource   = R.drawable.bg_game_fact;

        layout.child(nameView)
              .child(factView);

        // [3] Fact Name
        // --------------------------------------------------------------------------------------

        nameView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        nameView.text           = factName;
        nameView.font           = Font.sansSerifFontRegular(this);
        nameView.color          = R.color.dark_blue_hl_3;
        nameView.size           = R.dimen.games_button_fact_text_size;
        nameView.padding.right  = R.dimen.games_button_facts_name_view_padding_right;

        // [4] Fact
        // --------------------------------------------------------------------------------------

        factView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        factView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        factView.text           = factValue;
        factView.color          = R.color.dark_blue_hlx_7;
        factView.size           = R.dimen.games_button_fact_text_size;
        factView.font           = Font.sansSerifFontRegular(this);


        return layout.linearLayout(this);
    }



    /**
     * Generate the buttons for selecting the template game.
     */
    /*
    private void renderGameButtons()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.game_list);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(Util.linearLayoutParamsMatch());
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.bluegrey_900));

        scrollView.addView(layout);

        for (Game game : this.gameIndex.getGames())
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

            gameNameView.setText(game.label());


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


            // >> NumberWidget
            TextView gamePlayersNumberView = new TextView(this);

            gamePlayersNumberView.setLayoutParams(Util.linearLayoutParamsWrap());

            int numberOfPlayers = Statistics.gamePlayers(game.name());
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

            layout.addView(UI.divider(this));
            layout.addView(gameLayout);
        }
    }
    */


}
