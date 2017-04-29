
package com.kispoko.tome.activity.sheet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.widget.ActionWidgetActivity;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.lib.ui.ActivityCommon;
import com.kispoko.tome.lib.ui.FlexboxLayoutBuilder;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.mechanic.dice.DieRollResult;
import com.kispoko.tome.mechanic.dice.RollModifier;
import com.kispoko.tome.mechanic.dice.RollSummary;
import com.kispoko.tome.util.UI;

import java.util.UUID;



/**
 * Dice Roll Activity
 */
public class DiceRollerActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String       rollName;
    private String       rollDescription;
    private Summation    summation;

    private UUID         actionWidgetId;


    private LinearLayout rollsView;


    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        // -------------------------------------------------------------------------------------
        setContentView(R.layout.activity_dice_roll);

        // [2] Read Parameters
        // -------------------------------------------------------------------------------------

        this.summation = null;
        if (getIntent().hasExtra("summation")) {
            this.summation = (Summation) getIntent().getSerializableExtra("summation");
        }

        this.rollName = "";
        if (getIntent().hasExtra("roll_name")) {
            this.rollName = getIntent().getStringExtra("roll_name");
        }

        this.rollDescription = "";
        if (getIntent().hasExtra("roll_description")) {
            this.rollDescription = getIntent().getStringExtra("roll_description");
        }

        this.actionWidgetId = null;
        if (getIntent().hasExtra("action_widget_id")) {
            this.actionWidgetId = (UUID) getIntent().getSerializableExtra("action_widget_id");
        }

        // [3] Initialize UI components
        // -------------------------------------------------------------------------------------
        this.initializeToolbar();
        this.initializeNavigationView();
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


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        String title;
        if (this.rollName != null)
            title = this.rollName;
        else
            title = getString(R.string.dice_roller);

        UI.initializeToolbar(this, title);
    }


    private void initializeNavigationView()
    {

        // Sheet Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.right_nav_view);
        navigationView.addView(this.optionsNavigationView(this));

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ImageButton optionsButton = (ImageButton) findViewById(R.id.toolbar_options_button);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

    }


    private void initializeView()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.dice_rolls);

        LinearLayout rollsListView = this.rollsListView(this);
        scrollView.addView(rollsListView);
        this.rollsView = rollsListView;

        LinearLayout footerLayoutView = (LinearLayout) findViewById(R.id.dice_roll_footer);
        footerLayoutView.addView(this.footerView(this));
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    // ** Rolls List View
    // -----------------------------------------------------------------------------------------

    private LinearLayout rollsListView(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_theme_primary_84;

        return layout.linearLayout(context);
    }


    // ** Footer
    // -----------------------------------------------------------------------------------------

    private LinearLayout footerView(final Context context)
    {
        LinearLayout layout = this.footerViewLayout(context);

        // > Roll Name
        // -------------------------------------------------------------------------------------

        // layout.addView(this.rollNameView(context));

        // > Roll Button
        // -------------------------------------------------------------------------------------

        LinearLayout rollButton =  this.rollButtonView(context);
        layout.addView(rollButton);

        rollButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (summation != null && summation.diceRoll() != null)
                {
                    DiceRoll diceRoll = summation.diceRoll();
                    RollSummary rollSummary = diceRoll.rollAsSummary();
                    LinearLayout rollView = rollView(rollSummary, context);
                    rollsView.addView(rollView);
                }
            }
        });

        return layout;
    }


    private LinearLayout footerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation  = LinearLayout.VERTICAL;

        return layout.linearLayout(context);
    }


    private TextView rollNameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.margin.topDp       = 20f;

        name.layoutGravity      = Gravity.CENTER_HORIZONTAL;

        name.text               = this.rollName;

        name.font               = Font.serifFontRegular(context);
        name.color              = R.color.dark_theme_primary_27;
        name.sizeSp             = 15f;

        return name.textView(context);
    }


    private LinearLayout rollButtonView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.HORIZONTAL;

        // layout.backgroundResource   = R.drawable.bg_roll_button;

        layout.layoutGravity        = Gravity.CENTER;
        layout.gravity              = Gravity.CENTER;

        layout.margin.topDp         = 20f;
        layout.margin.bottomDp      = 20f;

//        layout.padding.topDp        = 20f;
//        layout.padding.bottomDp     = 20f;
//        layout.padding.leftDp       = 24f;
//        layout.padding.rightDp      = 28f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_roll_button;
        //icon.color                  = R.color.gold_medium_light;
        icon.color                  = R.color.dark_theme_primary_15;

        icon.margin.rightDp         = 7f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        if (this.summation != null && this.summation.diceRoll() != null)
            label.text              = this.summation.diceRoll().toString(true);

        label.font                  = Font.serifFontRegular(context);
        //label.color                 = R.color.gold_light;
        label.color                 = R.color.dark_theme_primary_10;
        label.sizeSp                = 22f;


        return layout.linearLayout(context);
    }


    // ** Roll
    // -----------------------------------------------------------------------------------------

    private LinearLayout rollView(RollSummary rollSummary, Context context)
    {
        LinearLayout layout = this.rollViewLayout(context);

        // > Header
        layout.addView(this.rollHeaderView(rollSummary.rollValue(), context));

        // > Summary
        layout.addView(this.rollSummaryView(rollSummary, context));

        return layout;
    }


    private LinearLayout rollViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.heightDp             = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.VERTICAL;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.backgroundResource   = R.drawable.bg_roll_view;
        layout.backgroundColor      = R.color.dark_theme_primary_81;

        layout.margin.rightDp       = 7f;
        layout.margin.leftDp        = 7f;
        layout.margin.topDp         = 10f;

        layout.padding.topDp        = 5f;
        layout.padding.bottomDp     = 5f;

        return layout.linearLayout(context);
    }


    private RelativeLayout rollHeaderView(int rollValue, Context context)
    {
        RelativeLayout layout = this.rollHeaderViewLayout(context);

        // > Result
        layout.addView(this.rollHeaderSummaryView(rollValue, context));

        // > Description
        layout.addView(this.rollOptionsButtonView(context));

        return layout;
    }


    private RelativeLayout rollHeaderViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.relativeLayout(context);
    }


    private LinearLayout rollHeaderSummaryView(int rollValue, Context context)
    {
        LinearLayout layout = this.rollHeaderSummaryViewLayout(context);

        layout.addView(this.rollResultView(rollValue, context));

        layout.addView(this.rollDescriptionView(context));

        return layout;
    }


    private LinearLayout rollHeaderSummaryViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.layoutType       = LayoutType.RELATIVE;
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.HORIZONTAL;

        return layout.linearLayout(context);
    }


    private LinearLayout rollResultView(int rollValue, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        // layout.layoutType           = LayoutType.RELATIVE;
        layout.widthDp              = 44;
        layout.heightDp             = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.addRule(RelativeLayout.ALIGN_PARENT_START);

        // layout.gravity              = Gravity.CENTER_HORIZONTAL;
        layout.layoutGravity        = Gravity.BOTTOM;

//        layout.backgroundResource    = R.drawable.bg_dice_roll_result;
//        layout.backgroundColor       = R.color.dark_theme_primary_78;

        layout.margin.leftDp        = 15f;

        layout.child(value);

        // [3 A] Value
        // -------------------------------------------------------------------------------------

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

//        value.padding.topDp         = 8f;
//        value.padding.bottomDp      = 8f;
//        value.padding.leftDp        = 15f;
//        value.padding.rightDp       = 15f;

        value.text                  = Integer.toString(rollValue);

        value.shadowColor           = R.color.dark_theme_primary_88;
        value.shadowRadius          = 8f;
        value.shadowDx              = 4f;
        value.shadowDy              = 4f;

        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_theme_primary_5;
        value.sizeSp                = 28f;

        return layout.linearLayout(context);
    }


    private TextView rollDescriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        //description.layoutType      = LayoutType.RELATIVE;
        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.layoutGravity   = Gravity.BOTTOM;

        description.margin.bottomDp = 1.5f;

        description.shadowColor     = R.color.dark_theme_primary_88;
        description.shadowRadius    = 8f;
        description.shadowDx        = 4f;
        description.shadowDy        = 4f;

        description.text            = this.rollDescription;

        description.font            = Font.serifFontRegular(context);
        description.color           = R.color.dark_theme_primary_20;
        description.sizeSp          = 18f;

        return description.textView(context);
    }


    private ImageView rollOptionsButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType       = LayoutType.RELATIVE;
        button.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        button.image            = R.drawable.ic_roll_options;
        button.color            = R.color.dark_theme_primary_25;

        button.margin.rightDp   = 12f;

        button.padding.bottomDp = 3f;

        button.addRule(RelativeLayout.ALIGN_PARENT_END);
        button.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        return button.imageView(context);
    }


    private FlexboxLayout rollSummaryView(RollSummary rollSummary, Context context)
    {
        FlexboxLayout layout = this.rollSummaryViewLayout(context);

        for (DieRollResult rollResult : rollSummary.rollResults()) {
            layout.addView(this.dieRollResultView(rollResult, context));
        }

        for (RollModifier rollModifier : rollSummary.modifiers()) {
            layout.addView(this.rollModifierView(rollModifier, context));
        }

        return layout;
    }


    private FlexboxLayout rollSummaryViewLayout(Context context)
    {
        FlexboxLayoutBuilder layout = new FlexboxLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.wrap             = FlexboxLayout.FLEX_WRAP_WRAP;

        layout.margin.leftDp    = 14f;
        layout.margin.rightDp   = 14f;
        layout.margin.topDp     = 12f;
        layout.margin.bottomDp  = 4f;

        return layout.flexboxLayout(context);
    }


    private LinearLayout dieRollResultView(DieRollResult rollResult, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     dice   = new TextViewBuilder();
        TextViewBuilder     value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.FLEXBOX;
        layout.width                = FlexboxLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = FlexboxLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_die_roll_result;
        layout.backgroundColor      = R.color.dark_theme_primary_81;

        layout.padding.topDp        = 3f;
        layout.padding.bottomDp     = 3f;
        layout.padding.leftDp       = 7f;
        layout.padding.rightDp      = 7f;

        layout.margin.rightDp       = 10f;
        layout.margin.bottomDp      = 8f;

        layout.child(dice)
              .child(value);

        // [3 A] Dice Label
        // -------------------------------------------------------------------------------------

        dice.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        dice.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        String diceString = "d" + Integer.toString(rollResult.diceSides());
        dice.text                   = diceString;

        dice.font                   = Font.serifFontRegular(context);
        dice.color                  = R.color.dark_theme_primary_60;
        dice.sizeSp                 = 14f;

        dice.margin.rightDp         = 5f;

        // [3 B] Value Label
        // -------------------------------------------------------------------------------------

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = Integer.toString(rollResult.value());

        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_theme_primary_35;
        value.sizeSp                = 14f;


        return layout.linearLayout(context);
    }


    private LinearLayout rollModifierView(RollModifier rollModifier, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     name   = new TextViewBuilder();
        TextViewBuilder     value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.FLEXBOX;
        layout.width                = FlexboxLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = FlexboxLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_die_roll_result;
        layout.backgroundColor      = R.color.dark_theme_primary_81;

        layout.margin.rightDp       = 10f;
        layout.margin.bottomDp      = 8f;

        layout.padding.topDp        = 3f;
        layout.padding.bottomDp     = 3f;
        layout.padding.leftDp       = 7f;
        layout.padding.rightDp      = 7f;

        layout.child(name)
              .child(value);

        // [3 A] Dice Label
        // -------------------------------------------------------------------------------------

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text                   = rollModifier.name();

        name.font                   = Font.serifFontRegular(context);
        name.color                  = R.color.dark_theme_primary_60;
        name.sizeSp                 = 14f;

        name.margin.rightDp         = 5f;

        // [3 B] Value Label
        // -------------------------------------------------------------------------------------

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = Integer.toString(rollModifier.value());

        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_theme_primary_35;
        value.sizeSp                = 14f;


        return layout.linearLayout(context);
    }


    // ** Options Navigation View
    // -----------------------------------------------------------------------------------------

    private LinearLayout optionsNavigationView(final Context context)
    {
        LinearLayout layout = this.optionsNavigationViewLayout(context);

        final LinearLayout actionWidgetButton =
                ActivityCommon.navigationButtonView(R.string.widget_action,
                                                    R.drawable.ic_nav_button_widget,
                                                    context);
        actionWidgetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, ActionWidgetActivity.class);

                if (actionWidgetId != null)
                    intent.putExtra("widget_id", actionWidgetId);

                context.startActivity(intent);
            }
        });

        layout.addView(actionWidgetButton);

        return layout;
    }


    private LinearLayout optionsNavigationViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.topDp     = 35f;
        layout.padding.leftDp    = 15f;

        return layout.linearLayout(context);
    }


}
