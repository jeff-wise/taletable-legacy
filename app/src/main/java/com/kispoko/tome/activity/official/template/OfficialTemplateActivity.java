
package com.kispoko.tome.activity.official.template;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.official.template.Template;
import com.kispoko.tome.official.template.Variant;
import com.kispoko.tome.util.UI;



/**
 * Official Template Activity
 */
public class OfficialTemplateActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Template template;


    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // ------------------------------------------------------------------------------------

        setContentView(R.layout.activity_official_template);

        // [2] Get Parameters
        // ------------------------------------------------------------------------------------

        this.template = null;
        if (getIntent().hasExtra("official")) {
            this.template = (Template) getIntent().getSerializableExtra("official");
        }

        // [3] Initialize UI
        // -----------------------------------------------------------------------------------

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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.template));
    }


    private void initializeView()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.content);
        layout.addView(this.view(this));
    }


    // VIEW
    // ------------------------------------------------------------------------------------------

    private LinearLayout view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > General View
        layout.addView(this.generalView(context));

        // > Variants View
        layout.addView(this.variantsView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation          = LinearLayout.VERTICAL;

        return layout.linearLayout(context);
    }


    private RelativeLayout generalView(Context context)
    {
        RelativeLayout layout = this.generalViewLayout(context);

        // > Name
        layout.addView(this.nameView(context));

        // > Description
        layout.addView(this.descriptionView(context));

        return layout;
    }


    private RelativeLayout generalViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = 0;
        layout.weight           = 3f;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 12f;
        layout.padding.bottomDp = 12f;

        return layout.relativeLayout(context);
    }


    private TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.layoutType         = LayoutType.RELATIVE;
        name.width              = RelativeLayout.LayoutParams.WRAP_CONTENT;
        name.height             = RelativeLayout.LayoutParams.WRAP_CONTENT;

        name.text               = this.template.label();

//        name.font               = Font.serifFontRegular(context);
        name.color              = R.color.dark_theme_primary_15;
        name.sizeSp             = 18f;

        name.margin.leftDp      = 1f;

        name.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        return name.textView(context);
    }


    private TextView descriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.layoutType      = LayoutType.RELATIVE;
        description.width           = RelativeLayout.LayoutParams.WRAP_CONTENT;
        description.height          = RelativeLayout.LayoutParams.WRAP_CONTENT;

        description.text            = this.template.fullDescription();

//        description.font            = Font.serifFontItalic(context);
        description.color           = R.color.dark_theme_primary_45;
        description.sizeSp          = 13f;

        description.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        return description.textView(context);
    }


    private LinearLayout variantsView(Context context)
    {
        LinearLayout layout = this.variantsViewLayout(context);

        if (this.template != null)
        {
            for (Variant variant : this.template.variants()) {
                layout.addView(this.variantButtonView(variant, context));
            }
        }

        return layout;
    }


    private LinearLayout variantsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = 0;
        layout.weight           = 2f;

        layout.orientation      = LinearLayout.VERTICAL;

        return layout.linearLayout(context);
    }


    private RelativeLayout variantButtonView(Variant variant, Context context)
    {
        RelativeLayout layout = this.variantButtonViewLayout(variant.name(), context);

        layout.addView(this.variantDividerView(context));

        // > Info
        layout.addView(this.variantButtonInfoView(variant.label(),
                                                  variant.description(),
                                                  context));

        // > Chevron
        layout.addView(this.variantButtonChevronView(context));

        return layout;
    }


    private LinearLayout variantDividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.layoutType  = LayoutType.RELATIVE;
        divider.width       = RelativeLayout.LayoutParams.MATCH_PARENT;
        divider.heightDp    = 1;

        divider.backgroundColor = R.color.dark_theme_primary_86;

        divider.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        return divider.linearLayout(context);
    }


    private RelativeLayout variantButtonViewLayout(final String variantName, Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = 0;
        layout.weight           = 1f;

//        layout.onClick          = new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(OfficialTemplateActivity.this, SheetActivityOld.class);
//                intent.putExtra("official_template_id", template.id(variantName));
//                startActivity(intent);
//            }
//        };

        return layout.relativeLayout(context);
    }


    private LinearLayout variantButtonInfoView(String nameString,
                                               String descriptionString,
                                               Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
        TextViewBuilder     name        = new TextViewBuilder();
        TextViewBuilder     description = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.RELATIVE;
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.VERTICAL;

        layout.addRule(RelativeLayout.ALIGN_PARENT_START);
        layout.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.margin.leftDp        = 12f;

        layout.child(name)
              .child(description);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text                   = nameString;

//        name.font                   = Font.serifFontRegular(context);
        name.color                  = R.color.dark_theme_primary_25;
        name.sizeSp                 = 16f;

        // [3 B] Description
        // -------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.text            = descriptionString;

//        description.font            = Font.serifFontRegular(context);
        description.color           = R.color.dark_theme_primary_55;
        description.sizeSp          = 14f;

        return layout.linearLayout(context);
    }


    private ImageView variantButtonChevronView(Context context)
    {
        ImageViewBuilder chevron = new ImageViewBuilder();

        chevron.layoutType          = LayoutType.RELATIVE;
        chevron.width               = RelativeLayout.LayoutParams.WRAP_CONTENT;
        chevron.height              = RelativeLayout.LayoutParams.WRAP_CONTENT;

        chevron.image               = R.drawable.ic_official_template_variant;
        chevron.color               = R.color.dark_theme_primary_15;

        chevron.margin.rightDp      = 12f;

        chevron.addRule(RelativeLayout.ALIGN_PARENT_END);
        chevron.addRule(RelativeLayout.CENTER_VERTICAL);

        return chevron.imageView(context);
    }

}
