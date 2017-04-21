
package com.kispoko.tome.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.engine.summation.SummationException;
import com.kispoko.tome.engine.summation.term.DiceRollTerm;
import com.kispoko.tome.engine.summation.term.DiceRollTermValue;
import com.kispoko.tome.engine.summation.term.IntegerTerm;
import com.kispoko.tome.engine.summation.term.IntegerTermValue;
import com.kispoko.tome.engine.summation.term.TermUnion;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.value;


/**
 * Summation Activity
 */
public class SummationActivity extends AppCompatActivity
{

    // PROPERTEIS
    // ------------------------------------------------------------------------------------------

    private Summation summation;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        // --------------------------------------------------------------------------------------
        setContentView(R.layout.activity_summation);

        // [2] Read parameters
        // --------------------------------------------------------------------------------------
        this.summation = null;
        if (getIntent().hasExtra("summation")) {
            this.summation = (Summation) getIntent().getSerializableExtra("summation");
        }

        // [3] Initialize UI components
        // -------------------------------------------------------------------------------------
        this.initializeToolbar();
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


    // UI
    // -----------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, false, false);

        // > Set the title
        // -------------------------------------------------------------------------------------
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(R.string.summation_editor);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.content);
        scrollView.addView(this.view(this));
    }


    // STATE
    // -----------------------------------------------------------------------------------------

    public List<TermUnion> summationTerms()
    {
        List<TermUnion> terms = new ArrayList<>();

        if (this.summation != null)
            terms = this.summation.terms();

        return terms;
    }


    // VIEWS
    // -----------------------------------------------------------------------------------------


    private LinearLayout view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Header
        layout.addView(this.headerView(context));

        // > Components View
        layout.addView(this.termListView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_theme_primary_84;

        layout.padding.topDp    = 10f;

        return layout.linearLayout(context);
    }


    // > Header View
    // -----------------------------------------------------------------------------------------

    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = this.headerViewLayout(context);

        return layout;
    }


    private LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        return layout.linearLayout(context);
    }


    // > Term List View
    // -----------------------------------------------------------------------------------------

    private LinearLayout termListView(Context context)
    {
        LinearLayout layout = this.termListViewLayout(context);

        for (TermUnion termUnion : this.summationTerms())
        {
            switch (termUnion.type())
            {
                case CONDITIONAL:
                    break;
                case DICE_ROLL:
                    layout.addView(this.diceRollTermView(termUnion.diceRollTerm(), context));
                    break;
                case INTEGER:
                    layout.addView(this.integerTermView(termUnion.integerTerm(), context));
                    break;
            }
        }

        return layout;
    }


    private LinearLayout termListViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        return layout.linearLayout(context);
    }


    // > Term Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout termViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.VERTICAL;

        layout.backgroundResource   = R.drawable.bg_term_view;
        layout.backgroundColor      = R.color.dark_theme_primary_80;

        layout.margin.bottomDp      = 12f;
        layout.margin.leftDp        = 8f;
        layout.margin.rightDp       = 8f;

        //layout.padding.leftDp       = 8f;
        // layout.padding.rightDp      = 9f;
        layout.padding.topDp        = 10f;
        //layout.padding.bottomDp     = 9f;

        return layout.linearLayout(context);
    }


    private TextView termHeaderView(String label, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.text             = label.toUpperCase();

        header.font             = Font.serifFontBoldItalic(context);
        header.color            = R.color.dark_theme_primary_7;
        header.sizeSp           = 14f;

        header.padding.leftDp   = 10f;
        header.padding.rightDp  = 10f;

        return header.textView(context);
    }


    private TextView termValueTypeView(String valueTypeString, Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = valueTypeString;

        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.dark_theme_primary_45;
        label.sizeSp            = 15.5f;

        label.margin.topDp      = 10f;

        label.padding.leftDp    = 10f;
        label.padding.rightDp   = 10f;

        return label.textView(context);
    }


    private LinearLayout termCurrentValueView(String valueString, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.backgroundResource   = R.drawable.bg_dialog_footer;
        layout.backgroundColor      = R.color.dark_theme_primary_81;

        layout.padding.topDp        = 7f;
        layout.padding.bottomDp     = 7f;
        layout.padding.leftDp       = 2.5f;
        layout.padding.rightDp      = 10f;

        layout.margin.topDp         = 10f;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.child(icon)
              .child(value);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_term_current_value;
        icon.color              = R.color.dark_theme_primary_50;

        icon.margin.rightDp     = 2f;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.width             = LinearLayout.LayoutParams.MATCH_PARENT;
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text              = valueString;

        value.font              = Font.serifFontNumeric(context);
        value.color             = R.color.purple_medium;
        value.sizeSp            = 19f;

        return layout.linearLayout(context);
    }


    // ** Integer Term
    // -----------------------------------------------------------------------------------------

    private LinearLayout integerTermView(IntegerTerm integerTerm, Context context)
    {
        LinearLayout layout = this.termViewLayout(context);

        // > Header
        // -------------------------------------------------------------------------------------

        String headerLabel = context.getString(R.string.number);
        layout.addView(this.termHeaderView(headerLabel, context));

        // > Value Type
        // -------------------------------------------------------------------------------------

        IntegerTermValue.Type termValueType = integerTerm.termValueType();
        StringBuilder typeStringBuilder = new StringBuilder();

        if (termValueType != null)
        {
            switch (termValueType)
            {
                case LITERAL:
                    typeStringBuilder.append("Literal");
                    break;
                case VARIABLE:
                    typeStringBuilder.append("Variable");
                    break;
            }

            typeStringBuilder.append(" Value");
        }

        layout.addView(this.termValueTypeView(typeStringBuilder.toString(), context));

        // > Current Value
        // -------------------------------------------------------------------------------------

        String valueString = "";
        try {
            valueString = integerTerm.value().toString();
        }
        catch (SummationException exception) {
            ApplicationFailure.summation(exception);
        }
        layout.addView(this.termCurrentValueView(valueString, context));

        return layout;
    }


    // ** Dice Roll Term
    // -----------------------------------------------------------------------------------------

    private LinearLayout diceRollTermView(DiceRollTerm diceRollTerm, Context context)
    {
        LinearLayout layout = this.termViewLayout(context);

        // > Header
        // -------------------------------------------------------------------------------------

        String headerLabel = context.getString(R.string.dice_roll);
        layout.addView(this.termHeaderView(headerLabel, context));

        // > Value Type
        // -------------------------------------------------------------------------------------

        DiceRollTermValue.Type termValueType = diceRollTerm.termValueType();
        StringBuilder typeStringBuilder = new StringBuilder();

        if (termValueType != null)
        {
            switch (termValueType)
            {
                case LITERAL:
                    typeStringBuilder.append("Literal");
                    break;
                case VARIABLE:
                    typeStringBuilder.append("Variable");
                    break;
            }

            typeStringBuilder.append(" Value");
        }

        layout.addView(this.termValueTypeView(typeStringBuilder.toString(), context));

        // > Current Value
        // -------------------------------------------------------------------------------------

        String valueString = "";
        try {
            valueString = diceRollTerm.diceRoll().toString(true);
        }
        catch (SummationException exception) {
            ApplicationFailure.summation(exception);
        }

        layout.addView(this.termCurrentValueView(valueString, context));

        return layout;
    }
}
