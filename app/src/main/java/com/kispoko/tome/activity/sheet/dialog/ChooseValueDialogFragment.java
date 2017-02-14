
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.valueset.ValueListItemView;
import com.kispoko.tome.activity.valueset.ValuesRecyclerViewAdapter;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.engine.value.ValueUnion;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.text.TextWidgetDialogFragment;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.ui.EditDialog;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.RecyclerViewBuilder;
import com.kispoko.tome.util.ui.RelativeLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;

import java.util.List;

import static android.R.attr.value;


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
        LinearLayout dialogLayout = this.dialogLayout(getContext());

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

    // > SheetDialog Layout
    // ------------------------------------------------------------------------------------------

    private LinearLayout dialogLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundColor      = R.color.dark_blue_5;

        return layout.linearLayout(context);
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Header
        String headerString = context.getString(R.string.choose) + " " +
                                this.valueSet.labelSingular();
        layout.addView(this.headerView(headerString, context));

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

        layout.backgroundResource   = R.drawable.bg_dialog_dark;

        return layout.linearLayout(context);
    }


    private TextView headerView(String headerText, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.padding.left         = R.dimen.dialog_edit_padding_horz;
        header.padding.right        = R.dimen.dialog_edit_padding_horz;

        header.padding.top          = R.dimen.dialog_choose_value_header_padding_vert;
        header.padding.bottom       = R.dimen.dialog_choose_value_header_padding_vert;

        header.backgroundResource   = R.drawable.bg_dialog_header;

        header.text                 = headerText;

        header.font                 = Font.serifFontRegular(context);
        header.color                = R.color.gold_medium_light;
        header.size                 = R.dimen.dialog_edit_header_text_size;

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

        recyclerView.backgroundResource = R.drawable.bg_choose_value_list;

        recyclerView.margin.left        = R.dimen.five_dp;
        recyclerView.margin.right       = R.dimen.five_dp;

        //recyclerView.padding.top        = R.dimen.two_dp;

        return recyclerView.recyclerView(getContext());
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

        layout.padding.left         = R.dimen.dialog_choose_value_item_padding_horz;
        layout.padding.right        = R.dimen.dialog_choose_value_item_padding_horz;
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
        value.size              = R.dimen.dialog_choose_value_value_text_size;

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
