
package com.kispoko.tome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.template.Template;
import com.kispoko.tome.template.TemplateIndex;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.util.ArrayList;
import java.util.List;



/**
 * Choose Template Activity
 */
public class TemplatesActivity extends AppCompatActivity
{

    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_templates);

        initializeToolbar();

        String gameId = null;
        if (getIntent().hasExtra("game_id")) {
            gameId = getIntent().getStringExtra("game_id");
        }

        TemplateIndex templateIndex = null;
        try {
            templateIndex = TemplateIndex.fromManifest(this);
        }
        catch (YamlParseException exception) {
            Log.d("***TEMPLATES", exception.errorMessage(), exception);
        }
        catch (Exception exception) {
            // TODO something better
            exception.printStackTrace();
        }

        List<Template> gameTemplates = templateIndex.templates(gameId);
        initializeView(gameTemplates);
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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        String title = "Who Will You Be?";

        UI.initializeToolbar(this, title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(List<Template> templates)
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.templates_content);
        contentLayout.addView(view(templates));
    }


    private ScrollView view(List<Template> templates)
    {
        ScrollView scrollView = templateChooserView();

        // [1] Create List Layout
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder listLayoutBuilder = new LinearLayoutBuilder();
        listLayoutBuilder.orientation  = LinearLayout.VERTICAL;
        listLayoutBuilder.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        listLayoutBuilder.height       = LinearLayout.LayoutParams.MATCH_PARENT;
        listLayoutBuilder.padding.top  = R.dimen.templates_padding_top;

        LinearLayout listLayout = listLayoutBuilder.linearLayout(this);

        scrollView.addView(listLayout);

        // [2] Add Game Buttons
        // --------------------------------------------------------------------------------------

        for (Template template : templates)
        {
            listLayout.addView(templateButton(template));
        }

        return scrollView;
    }


    /**
     * The template scroll view.
     * @return The template scroll view.
     */
    private ScrollView templateChooserView()
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        scrollView.backgroundColor  = R.color.dark_blue_5;

        return scrollView.scrollView(this);
    }


    /**
     * Template Button view.
     * @param template The template to render.
     * @return The game button Linear Layout.
     */
    private LinearLayout templateButton(Template template)
    {
        // [1] Create Layout
        // --------------------------------------------------------------------------------------

        LinearLayout layout = templateButtonLayout(template);

        // [2 A] Add Title
        // --------------------------------------------------------------------------------------

        layout.addView(titleView(template.label(), template.name()));

        // [2 B] Add Description
        // --------------------------------------------------------------------------------------

        layout.addView(descriptionView(template.description()));

        // [2 C] Add Variants
        // --------------------------------------------------------------------------------------

        if (!template.variants().isEmpty())
            layout.addView(variantsView(template, template.variants()));

        return layout;
    }


    /**
     * Template Button layout.
     * @return The Linear Layout.
     */
    private LinearLayout templateButtonLayout(final Template template)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left     = R.dimen.templates_button_layout_padding_horz;
        layout.padding.right    = R.dimen.templates_button_layout_padding_horz;
        layout.margin.bottom    = R.dimen.templates_button_layout_margin_bottom;

        layout.onClick          = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(TemplatesActivity.this, SheetActivity.class);
                intent.putExtra("TEMPLATE_ID", template.officialId());
                startActivity(intent);
            }
        };

        return layout.linearLayout(this);
    }


    /**
     * The template button title view.
     * @param titleString The title.
     * @return The title Text View.
     */
    private TextView titleView(String titleString, String name)
    {
        TextViewBuilder title = new TextViewBuilder();

        // > Highlight template name if it is in the title
        String[] titleParts = titleString.split("(?i)" + name);
        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
        String formattedName = "<font color='#CCC878'>" + capitalizedName + "</font>";

        titleString = titleParts[0] + formattedName;
        if (titleParts.length == 2)
            titleString += titleParts[1];

        title.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        title.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        title.textHtml      = Html.fromHtml(titleString);

        title.size          = R.dimen.templates_button_title_text_size;
        title.color         = R.color.dark_blue_hl_3;
        title.font          = Font.sansSerifFontBold(this);
        title.margin.bottom = R.dimen.templates_button_title_margin_bottom;

        return title.textView(this);
    }


    /**
     * The template button's description view.
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


    private LinearLayout variantsView(final Template template, List<String> variants)
    {
        LinearLayout layout = variantsLayout();

        final List<TextView> variantButtons = new ArrayList<>();

        for (final String variant : variants)
        {
            final TextView button = variantButton(variant);

            variantButtons.add(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    template.setSelectedVariant(variant);
                    highlightButton(button, variantButtons);
                }
            });

            layout.addView(button);
        }

        highlightButton(variantButtons.get(0), variantButtons);

        return layout;
    }


    private void highlightButton(TextView clickedButton, List<TextView> allButtons)
    {
        for (TextView button : allButtons) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_blue_4));
            button.setTextColor(ContextCompat.getColor(this, R.color.dark_blue_hl_1));
        }

        clickedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_blue_hl_6));
        clickedButton.setTextColor(ContextCompat.getColor(this, R.color.dark_blue_3));
    }


    private LinearLayout variantsLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation  = LinearLayout.HORIZONTAL;
        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(this);
    }


    private TextView variantButton(String variant)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;

        button.gravity              = Gravity.CENTER;
        button.margin.left          = R.dimen.templates_button_variant_button_margin_horz;
        button.margin.right         = R.dimen.templates_button_variant_button_margin_horz;

        button.backgroundResource   = R.drawable.bg_variant_button;
        button.text                 = variant;
        button.font                 = Font.sansSerifFontBold(this);
        button.size                 = R.dimen.templates_button_variant_button_text_size;

        return button.textView(this);
    }


}
