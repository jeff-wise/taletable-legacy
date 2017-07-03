
package com.kispoko.tome.activity.official.template;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RecyclerViewBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.official.template.Template;
import com.kispoko.tome.util.SimpleDividerItemDecoration;

import java.io.Serializable;
import java.util.List;


/**
 * Official Template List Fragment
 *
 * Used in the Official Templates Activity to show a list of templates.
 */
public class TemplateListFragment extends Fragment
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private List<Template> templateList;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public static TemplateListFragment newInstance(List<Template> templateList)
    {
        TemplateListFragment templateListFragment = new TemplateListFragment();

        Bundle args = new Bundle();
        args.putSerializable("template_list", (Serializable) templateList);
        templateListFragment.setArguments(args);

        return templateListFragment;
    }


    // FRAGMENT API
    // -----------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.templateList = (List<Template>) getArguments().getSerializable("template_list");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        return this.templateRecyclerView(getContext());
    }


    // RECYCLER VIEW
    // -----------------------------------------------------------------------------------------

    private RecyclerView templateRecyclerView(Context context)
    {
        RecyclerViewBuilder recyclerView = new RecyclerViewBuilder();

        recyclerView.layoutManager  = new LinearLayoutManager(context);
        recyclerView.adapter        = new TemplateSummaryRecyclerViewAdapter();

        recyclerView.divider        =
                        new SimpleDividerItemDecoration(context, R.color.dark_theme_primary_86);

        return recyclerView.recyclerView(context);
    }


    // RECYCLER VIEW ADAPTER
    // -----------------------------------------------------------------------------------------

    public class TemplateSummaryRecyclerViewAdapter
                    extends RecyclerView.Adapter<TemplateSummaryViewHolder>
    {

        // RECYCLER VIEW API
        // -------------------------------------------------------------------------------------

        @Override
        public TemplateSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new TemplateSummaryViewHolder(templateSummaryView(parent.getContext()),
                                                 getContext());
        }


        @Override
        public void onBindViewHolder(TemplateSummaryViewHolder viewHolder, int position)
        {
            Template template = templateList.get(position);

            viewHolder.setName(template.name(), template.label());
            viewHolder.setDescription(template.shortDescription());

            viewHolder.setOnClick(template);
        }


        @Override
        public int getItemCount()
        {
            return templateList.size();
        }

    }


    // TEMPLATE SUMMARY VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    public class TemplateSummaryViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private Context         context;

        private LinearLayout    layout;

        private TextView        nameView;
        private TextView        descriptionView;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public TemplateSummaryViewHolder(final View itemView, Context context)
        {
            super(itemView);

            this.context = context;

            this.layout =
                    (LinearLayout) itemView.findViewById(R.id.official_templates_summary_layout);

            this.nameView = (TextView) itemView.findViewById(R.id.official_templates_summary_name);
            this.descriptionView =
                    (TextView) itemView.findViewById(R.id.official_templates_summary_description);
        }


        // API
        // -------------------------------------------------------------------------------------

        public void setName(String name, String label)
        {
            int spanColor = ContextCompat.getColor(this.context, R.color.gold_light);
            //FormattedString.Span span = new FormattedString.Span(name, spanColor, TextFont.BOLD);
//            SpannableStringBuilder spannableStringBuilder =
//                                    FormattedString.spannableStringBuilder(label, span);
            //this.nameView.setText(spannableStringBuilder);
        }


        public void setDescription(String description)
        {
            this.descriptionView.setText(description);
        }


        public void setOnClick(final Template template)
        {
            this.layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, OfficialTemplateActivity.class);
                    intent.putExtra("official", template);
                    startActivity(intent);
                }
            });
        }

    }


    // TEMPLATE SUMMARY VIEW
    // -----------------------------------------------------------------------------------------

    private LinearLayout templateSummaryView(Context context)
    {
        LinearLayout layout = this.templateSummaryViewLayout(context);

        // > Left View
        layout.addView(this.templateSummaryLeftView(context));

        // > Right View
        layout.addView(this.templateSummaryRightView(context));

        return layout;
    }


    private LinearLayout templateSummaryViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id                   = R.id.official_templates_summary_layout;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.padding.topDp        = 18f;
        layout.padding.bottomDp     = 18f;

        layout.padding.leftDp       = 12f;
        layout.padding.rightDp      = 12f;

        return layout.linearLayout(context);
    }


    private LinearLayout templateSummaryLeftView(Context context)
    {
        LinearLayout layout = this.templateSummaryLeftViewLayout(context);

        layout.addView(this.templateSummaryImageView(context));

        return layout;
    }


    private LinearLayout templateSummaryLeftViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private LinearLayout templateSummaryImageView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_mock_template_image;

        layout.gravity              = Gravity.CENTER;

        layout.padding.topDp        = 10f;
        layout.padding.bottomDp     = 10f;
        layout.padding.rightDp      = 10f;
        layout.padding.leftDp       = 10f;

        layout.margin.rightDp       = 12f;

        layout.child(icon);

        // [3] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_mock_template_image;
        icon.color                  = R.color.dark_theme_primary_30;

        return layout.linearLayout(context);
    }


    private LinearLayout templateSummaryRightView(Context context)
    {
        LinearLayout layout = this.templateSummaryRightViewLayout(context);

        // > Name
        layout.addView(this.templateSummaryNameView(context));

        // > Description
        layout.addView(this.templateSummaryDescriptionView(context));

        return layout;
    }


    private LinearLayout templateSummaryRightViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation  = LinearLayout.VERTICAL;

        return layout.linearLayout(context);
    }


    private TextView templateSummaryNameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.id                 = R.id.official_templates_summary_name;

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

//        name.font               = Font.serifFontRegular(context);
        name.color              = R.color.dark_theme_primary_18;
        name.sizeSp             = 16f;

        return name.textView(context);
    }


    private TextView templateSummaryDescriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.id              = R.id.official_templates_summary_description;

        description.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

//        description.font            = Font.serifFontRegular(context);
        description.color           = R.color.dark_theme_primary_55;
        description.sizeSp          = 14f;

        description.margin.topDp    = 8f;

        return description.textView(context);
    }

}
