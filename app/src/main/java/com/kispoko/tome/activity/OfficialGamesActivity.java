
package com.kispoko.tome.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.engine.dictionary.ValueSetRowView;
import com.kispoko.tome.activity.engine.dictionary.ValueSetsRecyclerViewAdapter;
import com.kispoko.tome.activity.engine.valueset.BaseValueSetEditorActivity;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.engine.value.ValueSetUnion;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.game.GameIndex;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.util.List;


/**
 * Choose Template Game Activity
 */
public class OfficialGamesActivity extends AppCompatActivity
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private GameIndex gameIndex;


    // ACTIVITY LIFECYCLE
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_official_games);

        // [2] Read Games From Manifest File
        // -------------------------------------------------------------------------------------

        try {
            this.gameIndex = GameIndex.fromManifest(this);
        } catch (YamlParseException e) {
            Log.d("***CHOOSEGAME", e.errorMessage(), e);
        } catch (Exception e) {
            // TODO handle gracefully
            e.printStackTrace();
        }

        // [3] Initialize UI
        // -------------------------------------------------------------------------------------

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
    // -------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.which_game_do_you_play));
    }


    /**
     * Initialize the game list view.
     */
    private void initializeView()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.games_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        recyclerView.setAdapter(new GameSummaryRecyclerViewAdapter());

        SimpleDividerItemDecoration dividerItemDecoration =
                        new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    // GAME SUMMARY RECYCLER VIEW ADAPTER
    // -----------------------------------------------------------------------------------------

    public class GameSummaryRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>
    {

        // RECYCLER VIEW API
        // -------------------------------------------------------------------------------------

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new ViewHolder(gameSummaryView(parent.getContext()));
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder,
                                     int position)
        {
            Game game = gameIndex.games().get(position);

            viewHolder.setOnClick(game.name());

            viewHolder.setName(game.label());
            viewHolder.setDescription(game.description());

            viewHolder.setGenre(game.genre());
            viewHolder.setPlayers(game.players().toString());
            viewHolder.setCreated(game.created());
            viewHolder.setCreators(game.creators());
        }


        @Override
        public int getItemCount()
        {
            return gameIndex.games().size();
        }

    }


    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private LinearLayout    layout;

        private TextView        nameView;
        private TextView        descriptionView;

        private TextView        genreView;
        private TextView        playersView;
        private TextView        createdView;
        private TextView        creatorsView;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layout = (LinearLayout) itemView.findViewById(R.id.game_summary_layout);

            this.nameView = (TextView) itemView.findViewById(R.id.game_summary_name);
            this.descriptionView = (TextView) itemView.findViewById(R.id.game_summary_description);

            this.genreView = (TextView) itemView.findViewById(R.id.game_summary_genre);
            this.playersView = (TextView) itemView.findViewById(R.id.game_summary_players);
            this.createdView = (TextView) itemView.findViewById(R.id.game_summary_created);
            this.creatorsView = (TextView) itemView.findViewById(R.id.game_summary_creators);
        }


        // API
        // -------------------------------------------------------------------------------------

        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setDescription(String description)
        {
            this.descriptionView.setText(description);
        }


        public void setGenre(String genre)
        {
            this.genreView.setText(genre);
        }


        public void setPlayers(String players)
        {
            this.playersView.setText(players);
        }


        public void setCreated(String created)
        {
            this.createdView.setText(created);
        }


        public void setCreators(String creators)
        {
            this.creatorsView.setText(creators);
        }


        public void setOnClick(final String gameName)
        {
            this.layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(OfficialGamesActivity.this, TemplatesActivity.class);
                    intent.putExtra("game_name", gameName);
                    startActivity(intent);
                }
            });
        }

    }


    // GAME SUMMARY VIEW
    // -----------------------------------------------------------------------------------------

    private LinearLayout gameSummaryView(Context context)
    {
        LinearLayout layout = this.gameSummaryLayout(context);

        // > Title
        // -------------------------------------------------------------------------------------

        layout.addView(nameView(context));

        // > Description
        // -------------------------------------------------------------------------------------

        layout.addView(descriptionView(context));

        // > Add Facts
        // -------------------------------------------------------------------------------------

        LinearLayout factsRow1      = factsLayout(context);
        LinearLayout factsRow2      = factsLayout(context);

        LinearLayout genreView      = factView("GENRE", R.id.game_summary_genre, context);
        LinearLayout playersView    = factView("PLAYERS", R.id.game_summary_players, context);
        LinearLayout createdView    = factView("CREATED", R.id.game_summary_created, context);
        LinearLayout creatorsView   = factView("CREATORS", R.id.game_summary_creators, context);

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
    private LinearLayout gameSummaryLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.game_summary_layout;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 16f;
        layout.padding.bottomDp = 16f;

        return layout.linearLayout(context);
    }


    private TextView nameView(Context context)
    {
        TextViewBuilder title = new TextViewBuilder();

        title.id                = R.id.game_summary_name;

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        title.sizeSp            = 16f;
        title.color             = R.color.gold_light;
        title.font              = Font.serifFontRegular(context);
        title.margin.bottomDp   = 10f;

        return title.textView(context);
    }


    private TextView descriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.id              = R.id.game_summary_description;

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.font            = Font.serifFontRegular(context);
        description.color           = R.color.dark_theme_primary_60;
        description.sizeSp          = 14f;

        return description.textView(context);
    }


    private LinearLayout factsLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottom    = R.dimen.games_button_facts_layout_margin_bottom;

        return layout.linearLayout(context);
    }


    private LinearLayout factView(String factName, int viewId, Context context)
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

        layout.margin.rightDp       = 15f;

        layout.backgroundResource   = R.drawable.bg_game_fact;
        layout.backgroundColor      = R.color.dark_theme_primary_80;

        layout.child(nameView)
              .child(factView);

        // [3] Fact Name
        // --------------------------------------------------------------------------------------

        nameView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        nameView.text           = factName;
        nameView.font           = Font.serifFontRegular(this);
        nameView.color          = R.color.dark_blue_hl_3;
        nameView.size           = R.dimen.games_button_fact_text_size;
        nameView.padding.right  = R.dimen.games_button_facts_name_view_padding_right;

        // [4] Fact
        // --------------------------------------------------------------------------------------

        factView.id             = viewId;

        factView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        factView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        factView.color          = R.color.dark_blue_hlx_7;
        factView.size           = R.dimen.games_button_fact_text_size;
        factView.font           = Font.serifFontRegular(this);


        return layout.linearLayout(context);
    }


}
