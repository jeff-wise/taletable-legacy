
package com.kispoko.tome.activity.sheet.dialog;


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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.ValueSet;
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

    private ValueSet valueSet;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ChooseValueDialogFragment() { }


    public static ChooseValueDialogFragment newInstance(ValueSet valueSet)
    {
        ChooseValueDialogFragment chooseValueDialogFragment = new ChooseValueDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("valueset", valueSet);
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
        this.valueSet = (ValueSet) getArguments().getSerializable("valueset");

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
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Header
        layout.addView(this.headerView(context));

        // > Chooser
        layout.addView(chooserView(context));

        // > Footer
        //layout.addView(EditDialog.footerView(context));

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


    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = headerViewLayout(context);

        // > Buttons
        layout.addView(optionsView(context));

        // > Divider
        // layout.addView(dividerView(context));

        // > Name
        layout.addView(nameView(context));

        return layout;
    }


    private LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.leftDp       = 10f;

        layout.padding.bottomDp     = 8f;

        layout.backgroundColor      = R.color.dark_blue_7;
        layout.backgroundResource   = R.drawable.bg_dialog_header;

        return layout.linearLayout(context);
    }


    private TextView nameView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.text                 = context.getString(R.string.choose) + " "  +
                                            this.valueSet.labelSingular();

        header.font                 = Font.serifFontRegular(context);
        header.color                = R.color.gold_medium_light;
        header.sizeSp               = 19f;

        header.padding.leftDp       = 3.5f;
        header.padding.topDp        = 7f;
        header.padding.bottomDp     = 5f;

        return header.textView(context);
    }


    private RecyclerView chooserView(Context context)
    {
        RecyclerViewBuilder recyclerView = new RecyclerViewBuilder();

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        recyclerView.height             = R.dimen.dialog_choose_value_list_height;

        recyclerView.layoutManager      = new LinearLayoutManager(context);
        recyclerView.adapter            = new ValueSetRecyclerViewAdapter(this.valueSet.values());
        recyclerView.divider            = new ValueDividerItemDecoration(context);

        recyclerView.margin.left        = R.dimen.five_dp;
        recyclerView.margin.right       = R.dimen.five_dp;

        return recyclerView.recyclerView(getContext());
    }


    private LinearLayout optionsView(Context context)
    {
        LinearLayout layout = optionsViewLayout(context);

        // > Style Button
        String styleString = context.getString(R.string.style);
        layout.addView(optionButtonView(styleString, R.drawable.ic_dialog_style, context));

        // > Widget Button
        String configureWidgetString = context.getString(R.string.widget);
        layout.addView(optionButtonView(configureWidgetString,
                                        R.drawable.ic_dialog_widget, context));

        return layout;
    }


    private LinearLayout optionButtonView(String labelText, int iconId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.HORIZONTAL;

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL;

        layout.margin.rightDp   = 25f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = iconId;

        icon.color          = R.color.dark_blue_2;

        icon.margin.rightDp = 4f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.gravity              = Gravity.CENTER_HORIZONTAL;

        label.text                 = labelText;
        label.sizeSp               = 16.0f;
        label.color                = R.color.dark_blue_1;
        label.font                 = Font.serifFontRegular(context);


        return layout.linearLayout(context);
    }


    private LinearLayout optionsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.bottomDp = 5f;
        layout.padding.topDp    = 12f;

        layout.padding.leftDp   = 0.5f;

        return layout.linearLayout(context);
    }


    private LinearLayout valueView(Context context)
    {
        LinearLayout layout = valueViewLayout(context);

        layout.addView(valueHeaderView(context));
        layout.addView(valueSummaryView(context));

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
        layout.padding.top          = R.dimen.dialog_choose_value_item_padding_vert;
        layout.padding.bottom       = R.dimen.dialog_choose_value_item_padding_vert;

        return layout.linearLayout(context);
    }


    private RelativeLayout valueHeaderView(Context context)
    {
        RelativeLayout layout = valueHeaderViewLayout(context);

        layout.addView(valueLeftView(context));
        layout.addView(valueRightView(context));

        return layout;
    }


    private RelativeLayout valueHeaderViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.relativeLayout(context);
    }


    private LinearLayout valueLeftView(Context context)
    {
        LinearLayout layout = valueLeftViewLayout(context);

        layout.addView(valueTextView(context));

        return layout;
    }


    private LinearLayout valueRightView(Context context)
    {
        LinearLayout layout = valueRightViewLayout(context);

        return layout;
    }


    private LinearLayout valueLeftViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.layoutType           = LayoutType.RELATIVE;
        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private LinearLayout valueRightViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.layoutType           = LayoutType.RELATIVE;
        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private TextView valueTextView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.id                = R.id.choose_value_dialog_item_value;

        value.font              = Font.serifFontRegular(context);
        value.color             = R.color.dark_blue_hlx_9;
        value.sizeSp            = 17f;

        return value.textView(context);
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

        summary.margin.top      = R.dimen.dialog_choose_value_summary_margin_top;

        return summary.textView(context);
    }


    /**
     * ValueSet RecyclerView Adapter
     */
    private class ValueSetRecyclerViewAdapter
           extends RecyclerView.Adapter<ValueSetRecyclerViewAdapter.ViewHolder>
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------------

        private List<ValueUnion> values;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------------

        public ValueSetRecyclerViewAdapter(List<ValueUnion> values)
        {
            this.values  = values;
        }


        // RECYCLER VIEW ADAPTER API
        // -------------------------------------------------------------------------------------------

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = valueView(parent.getContext());
            return new ViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(ValueSetRecyclerViewAdapter.ViewHolder viewHolder,
                                     int position)
        {
            ValueUnion valueUnion = this.values.get(position);

            switch (valueUnion.type())
            {
                case TEXT:
                    viewHolder.setValueText(valueUnion.textValue().value());
                    viewHolder.setSummaryText(valueUnion.value().summary());
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


        // VIEW HOLDER
        // -------------------------------------------------------------------------------------------

        /**
         * The View Holder caches a view for each item.
         */
        public class ViewHolder extends RecyclerView.ViewHolder
        {

            private TextView valueView;
            private TextView summaryView;


            public ViewHolder(final View itemView)
            {
                super(itemView);

                this.valueView =
                        (TextView) itemView.findViewById(R.id.choose_value_dialog_item_value);

                this.summaryView =
                        (TextView) itemView.findViewById(R.id.choose_value_dialog_item_summary);
            }


            public void setValueText(String valueText)
            {
                this.valueView.setText(valueText);
            }


            public void setSummaryText(String summaryText)
            {
                this.summaryView.setText(summaryText);
            }

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
