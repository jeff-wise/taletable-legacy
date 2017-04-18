
package com.kispoko.tome.activity.sheet.widget.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.CompoundValueSet;
import com.kispoko.tome.engine.value.ValueSetUnion;
import com.kispoko.tome.engine.value.ValueUnion;
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RecyclerViewBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;

import java.util.List;



/**
 * Choose Value Dialog Fragment
 */
public class ChooseValueDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueSetUnion valueSetUnion;
    private ValueUnion    selectedValue;

    private String        title;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ChooseValueDialogFragment() { }


    public static ChooseValueDialogFragment newInstance(ValueSetUnion valueSetUnion,
                                                        ValueUnion selectedValue)
    {
        ChooseValueDialogFragment chooseValueDialogFragment = new ChooseValueDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("value_set_union", valueSetUnion);
        args.putSerializable("selected_value", selectedValue);
        chooseValueDialogFragment.setArguments(args);

        return chooseValueDialogFragment;
    }


    // DIALOG FRAGMENT
    // ------------------------------------------------------------------------------------------

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout dialogLayout = EditDialog.layout(getContext());

        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(dialogLayout);

        int width = (int) getContext().getResources().getDimension(R.dimen.action_dialog_width);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);

        // > Read State
        this.valueSetUnion = (ValueSetUnion) getArguments().getSerializable("value_set_union");
        this.selectedValue = (ValueUnion) getArguments().getSerializable("selected_value");

        this.title = "";
        if (this.valueSetUnion != null) {
            this.title = getContext().getString(R.string.choose) + " "  +
                            this.valueSetUnion.valueSet().labelSingular();
        }

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        return this.view(getContext());
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    // VIEWS
    // -----------------------------------------------------------------------------------------

    // > Views
    // -----------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // [1] Views
        // -------------------------------------------------------------------
        RecyclerView   chooserView     = this.chooserView(context);
        LinearLayout   optionsMenuView = this.optionsMenuView(context);
        RelativeLayout headerView      = this.headerView(chooserView, optionsMenuView, context);

        // [2] Initialize
        // -------------------------------------------------------------------

        // > Hide menu by default
        optionsMenuView.setVisibility(View.GONE);

        // [3] Add Views
        // -------------------------------------------------------------------

        // > Header
        layout.addView(headerView);

        // > Chooser
        layout.addView(chooserView);

        // > Options Menu
        layout.addView(optionsMenuView);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_blue_10;
        layout.backgroundResource   = R.drawable.bg_dialog;

        return layout.linearLayout(context);
    }


    // ** Header
    // -----------------------------------------------------------------------------------------

    private RelativeLayout headerView(final View chooserView,
                                      final View menuView,
                                      final Context context)
    {
        RelativeLayout layout = this.headerViewLayout(context);

        final TextView  titleView = this.headerTitleView(context);
        final ImageView iconView  = this.headerIconView(context);

        layout.addView(titleView);
        layout.addView(iconView);


        // > Toggle Menu Functionality
        // ----------------------------------------------------------------------------
        final Drawable closeIcon = ContextCompat.getDrawable(context,
                                                       R.drawable.ic_dialog_chooser_close_menu);

        final Drawable menuIcon = ContextCompat.getDrawable(context,
                                                        R.drawable.ic_dialog_chooser_menu);

        iconView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Show MENU
                if (chooserView.getVisibility() == View.VISIBLE)
                {
                    chooserView.setVisibility(View.GONE);
                    menuView.setVisibility(View.VISIBLE);

                    iconView.setImageDrawable(closeIcon);

                    titleView.setText(R.string.options);
                }
                // Show VALUES
                else
                {
                    chooserView.setVisibility(View.VISIBLE);
                    menuView.setVisibility(View.GONE);

                    iconView.setImageDrawable(menuIcon);

                    titleView.setText(title);
                }
            }
        });

        return layout;
    }


    private RelativeLayout headerViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_blue_7;
        layout.backgroundResource   = R.drawable.bg_dialog_header;

        layout.padding.leftDp       = 13.5f;
        layout.padding.rightDp      = 13.5f;
        layout.padding.topDp        = 16f;
        layout.padding.bottomDp     = 16f;

        return layout.relativeLayout(context);
    }


    private TextView headerTitleView(Context context)
    {
        TextViewBuilder title   = new TextViewBuilder();

        title.layoutType        = LayoutType.RELATIVE;
        title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT;
        title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT;

        if (this.valueSetUnion != null) {
            title.text          = context.getString(R.string.choose) + " "  +
                                    this.valueSetUnion.valueSet().labelSingular();
        }

        title.font              = Font.serifFontRegular(context);
        title.color             = R.color.dark_blue_hlx_4;
        title.sizeSp            = 19f;

        title.addRule(RelativeLayout.ALIGN_PARENT_START);
        title.addRule(RelativeLayout.CENTER_VERTICAL);

        return title.textView(context);
    }


    private ImageView headerIconView(Context context)
    {
        ImageViewBuilder icon = new ImageViewBuilder();

        icon.layoutType     = LayoutType.RELATIVE;
        icon.width          = RelativeLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = RelativeLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = R.drawable.ic_dialog_chooser_menu;

        icon.color          = R.color.dark_blue_hl_5;

        icon.addRule(RelativeLayout.ALIGN_PARENT_END);
        icon.addRule(RelativeLayout.CENTER_VERTICAL);

        return icon.imageView(context);
    }


    // ** List View
    // -----------------------------------------------------------------------------------------

    private RecyclerView chooserView(Context context)
    {
        RecyclerViewBuilder recyclerView = new RecyclerViewBuilder();

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        recyclerView.height             = R.dimen.dialog_choose_value_list_height;

        recyclerView.layoutManager      = new LinearLayoutManager(context);

        if (this.valueSetUnion != null)
        {
            switch (valueSetUnion.type())
            {
                case BASE:
                    List<ValueUnion> values = this.valueSetUnion.valueSet().values();
                    recyclerView.adapter = new BaseValueSetRecyclerViewAdapter(values);
                    break;
                case COMPOUND:
                    CompoundValueSet compoundValueSet = this.valueSetUnion.compound();
                    List<Object> items = compoundValueSet.valuesWithHeaders();
                    recyclerView.adapter = new CompoundValueSetRecyclerViewAdapter(items);
                    break;
            }
        }

        // recyclerView.divider            = new ValueDividerItemDecoration(context);

        recyclerView.margin.left        = R.dimen.five_dp;
        recyclerView.margin.right       = R.dimen.five_dp;

        recyclerView.padding.bottomDp   = 20f;

        return recyclerView.recyclerView(getContext());
    }


    // ** Value View
    // -----------------------------------------------------------------------------------------

    private LinearLayout valueView(Context context)
    {
        LinearLayout layout = valueViewLayout(context);

        layout.addView(this.valueHeaderView(context));
        layout.addView(this.valueSummaryView(context));
        // layout.addView(this.valueDividerView(context));

        return layout;
    }


    private LinearLayout valueViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.leftDp       = 9f;
        layout.padding.rightDp      = 9f;
        layout.padding.topDp        = 16f;

        return layout.linearLayout(context);
    }


    private LinearLayout valueHeaderView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     name   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.child(icon)
              .child(name);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.id                     = R.id.choose_value_dialog_item_icon;

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_dialog_chooser_selected;

        icon.color                  = R.color.green_light;

        icon.margin.rightDp         = 5f;

        icon.visibility             = View.GONE;

        // [3 B] Name
        // -------------------------------------------------------------------------------------

        name.id                     = R.id.choose_value_dialog_item_value;

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.font                   = Font.serifFontRegular(context);
        name.color                  = R.color.dark_blue_hlx_5;
        name.sizeSp                 = 17f;

        return layout.linearLayout(context);
    }


    private TextView valueSummaryView(Context context)
    {
        TextViewBuilder summary = new TextViewBuilder();

        summary.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        summary.id              = R.id.choose_value_dialog_item_summary;

        summary.font            = Font.serifFontRegular(context);
        summary.color           = R.color.dark_blue_hl_8;
        summary.size            = R.dimen.dialog_choose_value_summary_text_size;

        summary.margin.topDp    = 6f;

        return summary.textView(context);
    }


    // ** Set Name View
    // -----------------------------------------------------------------------------------------

    private TextView valueSetNameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.id             = R.id.value_list_header_text;

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.font           = Font.serifFontRegular(context);
        name.color          = R.color.gold_light;
        name.sizeSp         = 18f;

        name.margin.leftDp  = 10f;
        name.margin.rightDp = 10f;

        name.margin.topDp   = 20f;

        return name.textView(context);
    }


    // ** Menu View
    // -----------------------------------------------------------------------------------------

    private LinearLayout optionsMenuView(Context context)
    {
        LinearLayout layout = this.optionsMenuViewLayout(context);

        // Sort Asc Button
        LinearLayout sortAscButtonView =
                this.optionsMenuButtonView(R.string.sort_values_ascending,
                                           R.drawable.ic_dialog_chooser_sort_asc,
                                           context);
        layout.addView(sortAscButtonView);

        // Sort Desc Button
        LinearLayout sortDescButtonView =
                this.optionsMenuButtonView(R.string.sort_values_descending,
                                           R.drawable.ic_dialog_chooser_sort_desc,
                                           context);
        layout.addView(sortDescButtonView);

        // --- Divider
        layout.addView(this.optionsMenuDividerView(context));

        // Edit Values
        LinearLayout editValuesButton =
                this.optionsMenuButtonView(R.string.edit_values,
                                           R.drawable.ic_dialog_chooser_edit_values,
                                           context);
        layout.addView(editValuesButton);

        // --- Divider
        layout.addView(this.optionsMenuDividerView(context));

        // Edit Values
        LinearLayout styleWidgetButton =
                this.optionsMenuButtonView(R.string.style_widget,
                                           R.drawable.ic_dialog_chooser_style_widget,
                                           context);
        layout.addView(styleWidgetButton);

        // Edit Widget
        LinearLayout editWidgetButton =
                this.optionsMenuButtonView(R.string.edit_widget,
                                           R.drawable.ic_dialog_chooser_widget,
                                           context);
        layout.addView(editWidgetButton);

        return layout;
    }


    private LinearLayout optionsMenuViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = R.dimen.dialog_choose_value_list_height;

        layout.backgroundColor      = R.color.dark_blue_7;
        layout.backgroundResource   = R.drawable.bg_dialog_list_widget_chooser;

        layout.padding.leftDp       = 13f;
        layout.padding.rightDp      = 13f;
        layout.padding.topDp        = 10f;

        return layout.linearLayout(context);
    }


    private LinearLayout optionsMenuButtonView(int labelId, int iconId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL;

        layout.margin.topDp     = 14f;
        layout.margin.bottomDp  = 14f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = iconId;

        icon.color              = R.color.dark_blue_hl_2;

        icon.margin.rightDp     = 10f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId            = labelId;

        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.dark_blue_hlx_10;
        label.sizeSp            = 17f;


        return layout.linearLayout(context);
    }


    private LinearLayout optionsMenuDividerView(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.heightDp         = 1;

        layout.backgroundColor  = R.color.dark_blue_4;

        return layout.linearLayout(context);
    }


    // BASE VALUE SET ADPATER
    // -----------------------------------------------------------------------------------------

    /**
     * ValueSet RecyclerView Adapter
     */
    private class BaseValueSetRecyclerViewAdapter
           extends RecyclerView.Adapter<ValueViewHolder>
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private List<ValueUnion> values;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public BaseValueSetRecyclerViewAdapter(List<ValueUnion> values)
        {
            this.values  = values;
        }


        // RECYCLER VIEW ADAPTER API
        // -------------------------------------------------------------------------------------

        @Override
        public ValueViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = valueView(parent.getContext());
            return new ValueViewHolder(itemView, parent.getContext());
        }


        @Override
        public void onBindViewHolder(ValueViewHolder viewHolder,
                                     int position)
        {
            ValueUnion valueUnion = this.values.get(position);

            switch (valueUnion.type())
            {
                case TEXT:
                    if (valueUnion.equals(selectedValue))
                        viewHolder.setValueTextSelected(valueUnion.textValue().value());
                    else
                        viewHolder.setValueText(valueUnion.textValue().value());

                    viewHolder.setSummaryText(valueUnion.value().description());
                    break;
                case NUMBER:
                    viewHolder.setValueText(valueUnion.numberValue().value().toString());
                    break;
            }
        }


        @Override
        public int getItemCount()
        {
            return this.values.size();
        }


    }


    // COMPOUND VALUE SET ADPATER
    // -----------------------------------------------------------------------------------------

    private class CompoundValueSetRecyclerViewAdapter
                        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private List<Object>    items;

        private final int HEADER = 0, VALUE = 1;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public CompoundValueSetRecyclerViewAdapter(List<Object> items)
        {
            this.items = items;
        }


        // RECYCLER VIEW ADAPTER API
        // -------------------------------------------------------------------------------------

        @Override
        public int getItemViewType(int position)
        {
            Object itemAtPosition = this.items.get(position);

            if (itemAtPosition instanceof String)
                return HEADER;
            else if (itemAtPosition instanceof ValueUnion)
                return VALUE;
            else
                return -1;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case HEADER:
                    View headerView = valueSetNameView(parent.getContext());
                    return new HeaderViewHolder(headerView);
                case VALUE:
                    View valueView = valueView(parent.getContext());
                    return new ValueViewHolder(valueView, parent.getContext());
                default:
                    return null;
            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
        {
            Object item = this.items.get(position);

            if (item instanceof ValueUnion)
            {
                ValueUnion      valueUnion      = (ValueUnion) item;
                ValueViewHolder valueViewHolder = (ValueViewHolder) viewHolder;

                switch (valueUnion.type())
                {
                    case TEXT:
                        if (valueUnion.equals(selectedValue))
                            valueViewHolder.setValueTextSelected(valueUnion.textValue().value());
                        else
                            valueViewHolder.setValueText(valueUnion.textValue().value());

                        valueViewHolder.setSummaryText(valueUnion.value().description());
                        break;
                    case NUMBER:
                        valueViewHolder.setValueText(valueUnion.numberValue().value().toString());
                        break;
                }
            }
            else if (item instanceof String)
            {
                String           valueSetName     = (String) item;
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

                headerViewHolder.setHeaderText(valueSetName);
            }
        }


        @Override
        public int getItemCount()
        {
            return this.items.size();
        }

    }


    // HEADER VIEW HOLDER
    // -------------------------------------------------------------------------------------

    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {

        private TextView headerTextView;


        public HeaderViewHolder(View headerView)
        {
            super(headerView);

            this.headerTextView =
                    (TextView) headerView.findViewById(R.id.value_list_header_text);
        }


        public void setHeaderText(String text)
        {
            this.headerTextView.setText(text);
        }
    }


    // VALUE VIEW HOLDER
    // -------------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class ValueViewHolder extends RecyclerView.ViewHolder
    {

        private Context   context;

        private TextView  valueView;
        private TextView  summaryView;
        private ImageView iconView;


        public ValueViewHolder(final View itemView, Context context)
        {
            super(itemView);

            this.context = context;

            this.valueView =
                    (TextView) itemView.findViewById(R.id.choose_value_dialog_item_value);

            this.summaryView =
                    (TextView) itemView.findViewById(R.id.choose_value_dialog_item_summary);

            this.iconView =
                    (ImageView) itemView.findViewById(R.id.choose_value_dialog_item_icon);
        }


        public void setValueText(String valueText)
        {
            this.valueView.setText(valueText);
            this.valueView.setTextColor(
                    ContextCompat.getColor(this.context, R.color.dark_blue_hlx_5));
            this.iconView.setVisibility(View.GONE);
        }


        public void setValueTextSelected(String valueText)
        {
            this.valueView.setText(valueText);
            this.valueView.setTextColor(
                    ContextCompat.getColor(this.context, R.color.green_medium_light));
            this.iconView.setVisibility(View.VISIBLE);
        }


        public void setSummaryText(String summaryText)
        {
            this.summaryView.setText(summaryText);
        }

    }



    public class ValueDividerItemDecoration extends RecyclerView.ItemDecoration
    {
        private Drawable divider;

        public ValueDividerItemDecoration(Context context)
        {
            divider = ContextCompat.getDrawable(context, R.drawable.divider_choose_value);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
        {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

}
