
package com.kispoko.tome.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.official.OfficialGamesActivity;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.model.sheet.Sheet;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.query.CountQuery;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;



/**
 * New Character Activity
 */
public class NewCharacterActivity extends AppCompatActivity
                                  implements CountQuery.OnCountListener
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private boolean firstSheet;


    // ACTIVITY LIFECYCLE EVENTS
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_new_character);

        // [2] Query Models
        // -------------------------------------------------------------------------------------

        //CountQuery.fromModel(Sheet.class).run(this);

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
        UI.initializeToolbar(this, getString(R.string.create_a_new_character));
    }


    /**
     * Initialize the new character view.
     */
    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.content);
        contentLayout.addView(view(this));
    }


    /**
     * The activity view.
     * @return The linear layout.
     */
    private LinearLayout view(Context context)
    {
        LinearLayout layout = viewLayout();

        // > Option Buttons
        layout.addView(this.optionsView(context));

        // > Quote
        layout.addView(this.quoteView(context));

        return layout;
    }


    private LinearLayout viewLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation      = LinearLayout.VERTICAL;

        return layout.linearLayout(this);
    }


    private LinearLayout optionsView(Context context)
    {
        LinearLayout layout = this.optionsViewLayout(context);

        LinearLayout buttonsLayout = this.optionsButtonsLayout(context);

        // > FROM TEMPLATE
        // -------------------------------------------------------------------------------------

        RelativeLayout fromTemplateButtonView =
                                this.optionButtonView(getString(R.string.from_template),
                                                      R.drawable.ic_new_character_template,
                                        R.color.purple_medium_dark,
                                        R.color.purple_medium_light,
                                                      context);

        fromTemplateButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NewCharacterActivity.this, OfficialGamesActivity.class);
                startActivity(intent);
            }
        });

        buttonsLayout.addView(fromTemplateButtonView);

        // > FROM HUB
        // -------------------------------------------------------------------------------------

        RelativeLayout fromHubButtonView =
                                this.optionButtonView(getString(R.string.from_hub),
                                                      R.drawable.ic_new_character_hub,
                                        R.color.gold_medium,
                                        R.color.gold_medium_light,
                                                      context);

        buttonsLayout.addView(fromHubButtonView);

        // > FROM FILE
        // -------------------------------------------------------------------------------------

        RelativeLayout fromFileButtonView =
                                this.optionButtonView(getString(R.string.from_file),
                                                      R.drawable.ic_new_character_file,
                                                      R.color.green_medium_dark,
                                                      R.color.green_medium_light,
                                                      context);

        buttonsLayout.addView(fromFileButtonView);

        layout.addView(buttonsLayout);

        return layout;
    }


    private LinearLayout optionsButtonsLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.gravity          = Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private LinearLayout optionsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = 0;
        layout.weight           = 4f;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_theme_primary_88;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;

        return layout.linearLayout(context);
    }


    private RelativeLayout optionButtonView(String labelString,
                                            int iconId,
                                            int iconColor,
                                            int labelColor,
                                            Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout     = new RelativeLayoutBuilder();

        ImageViewBuilder      chevron    = new ImageViewBuilder();
        LinearLayoutBuilder   leftLayout = new LinearLayoutBuilder();

        ImageViewBuilder      icon       = new ImageViewBuilder();
        TextViewBuilder       label      = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_new_character_option;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.leftDp       = 15f;
        layout.padding.rightDp      = 15f;
        layout.padding.topDp        = 15f;
        layout.padding.bottomDp     = 15f;

        layout.margin.bottomDp      = 18f;

        layout.child(leftLayout)
              .child(chevron);

        // [3] Left Layout
        // -------------------------------------------------------------------------------------

        leftLayout.layoutType           = LayoutType.RELATIVE;
        leftLayout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        leftLayout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;

        leftLayout.orientation          = LinearLayout.HORIZONTAL;

        leftLayout.gravity              = Gravity.CENTER_VERTICAL;

        leftLayout.addRule(RelativeLayout.ALIGN_PARENT_START);
        leftLayout.addRule(RelativeLayout.CENTER_VERTICAL);

        leftLayout.child(icon)
                  .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = iconId;
        icon.color              = iconColor;

        icon.margin.rightDp     = 12f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = labelString;

        label.font              = Font.serifFontRegular(context);
        label.color             = labelColor;
        label.sizeSp            = 17.5f;

        // [4] Chevron
        // -------------------------------------------------------------------------------------

        chevron.layoutType         = LayoutType.RELATIVE;
        chevron.width              = RelativeLayout.LayoutParams.WRAP_CONTENT;
        chevron.height             = RelativeLayout.LayoutParams.WRAP_CONTENT;

        chevron.image              = R.drawable.ic_new_character_option_chevron;
        chevron.color              = iconColor;

        chevron.addRule(RelativeLayout.ALIGN_PARENT_END);
        chevron.addRule(RelativeLayout.CENTER_VERTICAL);

        return layout.relativeLayout(context);
    }


    private LinearLayout quoteView(Context context)
    {
        LinearLayout layout = this.quoteViewLayout(context);

        // > Header
        layout.addView(this.quoteHeaderView(context));

        // > Quote
        layout.addView(this.quoteTextView(context));

        // > Source
        layout.addView(this.quoteSourceView(context));

        return layout;
    }


    private LinearLayout quoteViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = 0;
        layout.weight           = 5f;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_theme_primary_86;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 15f;

        return layout.linearLayout(context);
    }


    private TextView quoteHeaderView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.text             = context.getString(R.string.random_user_quote).toUpperCase();

        header.font             = Font.serifFontBold(context);
        header.color            = R.color.dark_theme_primary_70;
        header.sizeSp           = 14f;

        return header.textView(context);
    }


    private TextView quoteTextView(Context context)
    {
        TextViewBuilder quote = new TextViewBuilder();

        quote.width             = LinearLayout.LayoutParams.MATCH_PARENT;
        quote.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        quote.text              = "I know what you are thinking. Because the square moon is in " +
                                  "the sky back in my homeland, you may use \"he\".";

        quote.font              = Font.serifFontItalic(context);
        quote.color             = R.color.dark_theme_primary_18;
        quote.sizeSp            = 16.5f;

        quote.margin.topDp      = 20f;

        quote.lineSpacingAdd    = 2f;
        quote.lineSpacingMult   = 1.3f;

        quote.gravity           = Gravity.CENTER;

        return quote.textView(context);
    }


    private RelativeLayout quoteSourceView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();
        TextViewBuilder       source = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.topDp         = 12f;

        layout.child(source);

        // [3] Source
        // -------------------------------------------------------------------------------------

        source.layoutType           = LayoutType.RELATIVE;
        source.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        source.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;

        source.text                 = "\u2014 Francis, Extra-Dimensional Tentacle Creature";

        source.font                 = Font.serifFontRegular(context);
        source.color                = R.color.dark_theme_primary_45;
        source.sizeSp               = 15f;

        source.gravity              = Gravity.RIGHT;

        source.margin.rightDp       = 10f;

        source.addRule(RelativeLayout.ALIGN_PARENT_END);

        return layout.relativeLayout(context);
    }

}
