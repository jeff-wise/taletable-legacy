
package com.kispoko.tome.activity.sheet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.engine.summation.term.TermSummary;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.RelativeLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Roll Dialog Fragment
 */
public class RollDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String         actionName;
    private NumberVariable numberVariable;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RollDialogFragment() { }


    public static RollDialogFragment newInstance(String actionName, NumberVariable numberVariable)
    {
        RollDialogFragment rollDialogFragment = new RollDialogFragment();

        Bundle args = new Bundle();
        args.putString("action_name", actionName);
        args.putSerializable("number_variable", numberVariable);
        rollDialogFragment.setArguments(args);

        return rollDialogFragment;
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
        this.actionName     = getArguments().getString("action_name");
        this.numberVariable = (NumberVariable) getArguments().getSerializable("number_variable");

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
    // -----------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Rolls View
        layout.addView(rollsView(context));

        // > Components View
        if (this.numberVariable.kind() == NumberVariable.Kind.SUMMATION) {
            layout.addView(componentsView(this.numberVariable.summation(), context));
        }

        // > Button View
        layout.addView(rollButtonView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_dialog;

        return layout.linearLayout(context);
    }


    private LinearLayout rollsView(Context context)
    {
        LinearLayout layout = rollsViewLayout(context);

        layout.addView(topRowView(context));


        return layout;
    }


    private LinearLayout rollsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = R.dimen.dialog_roll_rolls_height;

        layout.backgroundResource  = R.drawable.bg_dialog_header_dark;

        layout.padding.left     = R.dimen.sheet_dialog_header_padding_horz;
        layout.padding.right    = R.dimen.sheet_dialog_header_padding_horz;
        layout.padding.top      = R.dimen.sheet_dialog_header_padding_top;

        return layout.linearLayout(context);
    }




    private LinearLayout rollButtonView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        LinearLayoutBuilder rollLayout = new LinearLayoutBuilder();
        ImageViewBuilder    rollIcon   = new ImageViewBuilder();
        TextViewBuilder     roll       = new TextViewBuilder();

        TextViewBuilder     action = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.padding.top          = R.dimen.dialog_roll_roll_button_padding_vert;
        layout.padding.bottom       = R.dimen.dialog_roll_roll_button_padding_vert;

        layout.margin.left          = R.dimen.dialog_roll_roll_button_margin_horz;
        layout.margin.right         = R.dimen.dialog_roll_roll_button_margin_horz;

        layout.child(rollLayout)
              .child(action);

        // [3] Roll
        // -------------------------------------------------------------------------------------

        // [3 A] Layout
        // -------------------------------------------------------------------------------------

        rollLayout.orientation      = LinearLayout.HORIZONTAL;
        rollLayout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        rollLayout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        rollLayout.gravity          = Gravity.CENTER_VERTICAL;

        rollLayout.child(rollIcon)
                  .child(roll);

        // [3 B] Icon
        // -------------------------------------------------------------------------------------

        rollIcon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        rollIcon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        rollIcon.image              = R.drawable.ic_roll_dialog;

        rollIcon.margin.right       = R.dimen.dialog_roll_roll_button_icon_margin_right;

        // [3 C] Text
        // -------------------------------------------------------------------------------------

        roll.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        roll.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        roll.textId                 = R.string.roll;
        roll.font                   = Font.serifFontBold(context);
        roll.color                  = R.color.dark_blue_hlx_7;
        roll.size                   = R.dimen.dialog_roll_roll_button_roll_text_size;

        // [4] Action
        // -------------------------------------------------------------------------------------

        action.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        action.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        action.text                 = this.actionName;
        action.font                 = Font.serifFontRegular(context);
        action.color                = R.color.dark_blue_hl_5;
        action.size                 = R.dimen.dialog_roll_roll_button_action_text_size;


        return layout.linearLayout(context);
    }


    private LinearLayout componentsView(Summation summation, Context context)
    {
        LinearLayout layout = componentsViewLayout(context);

        for (TermSummary summary: summation.summary()) {
            layout.addView(componentView(summary, context));
        }

        return layout;
    }


    private LinearLayout componentView(TermSummary summary, Context context)
    {
        LinearLayout layout = componentViewLayout(context);

        if (summary.name() != null)
            layout.addView(componentHeaderView(summary.name(), context));

        for (Tuple2<String,String> component : summary.components())
        {
            String name  = component.getItem1();
            String value = component.getItem2();
            layout.addView(componentItemView(name, value, context));
        }

        layout.addView(componentDividerView(context));

        return layout;
    }


    private LinearLayout componentDividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.height          = R.dimen.one_dp;

        divider.backgroundColor = R.color.dark_blue_7;

        return divider.linearLayout(context);
    }


    private LinearLayout componentViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private RelativeLayout componentItemView(String nameText, String valueText, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();
        TextViewBuilder     name   = new TextViewBuilder();
        TextViewBuilder     value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left             = R.dimen.dialog_summ_component_padding_left;
        layout.padding.right            = R.dimen.dialog_summ_component_padding_right;

        layout.padding.top              = R.dimen.dialog_summ_component_padding_vert;
        layout.padding.bottom           = R.dimen.dialog_summ_component_padding_vert;

        layout.child(name)
              .child(value);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.layoutType                 = LayoutType.RELATIVE;
        name.width                      = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height                     = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        name.text                       = nameText;
        name.font                       = Font.serifFontRegular(context);
        name.size                       = R.dimen.dialog_summ_component_name_text_size;
        name.color                      = R.color.dark_blue_hl_5;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE;
        value.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        value.text                      = valueText;
        value.font                      = Font.serifFontBold(context);
        value.size                      = R.dimen.dialog_summ_component_value_text_size;
        value.color                     = R.color.dark_blue_hl_5;


        return layout.relativeLayout(context);
    }


    private TextView componentHeaderView(String headerText, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.text                 = headerText;
        header.font                 = Font.serifFontRegular(context);
        header.color                = R.color.dark_blue_hl_5;
        header.size                 = R.dimen.dialog_summ_component_header_text_size;

        return header.textView(context);
    }


    private LinearLayout componentsViewLayout(Context context)
    {
        LinearLayoutBuilder layout =  new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private RelativeLayout topRowView(Context context)
    {
        RelativeLayout layout = topRowViewLayout(context);

        // > Label
        layout.addView(rollsTitleView(context));

        // > Close Button
        layout.addView(closeButtonView(context));

        return layout;
    }


    private RelativeLayout topRowViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        return layout.relativeLayout(context);
    }


    private TextView rollsTitleView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.layoutType       = LayoutType.RELATIVE;
        header.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        header.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        header.textId           = R.string.rolls;
        header.font             = Font.sansSerifFontRegular(context);
        header.color            = R.color.dark_blue_hl_9;
        header.size             = R.dimen.sheet_dialog_heading_text_size;

        header.margin.top       = R.dimen.one_dp;

        header.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return header.textView(context);
    }


    private ImageView closeButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType           = LayoutType.RELATIVE;
        button.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.layoutGravity        = Gravity.CENTER;

        button.image                = R.drawable.ic_dialog_close;

        button.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        return button.imageView(context);
    }


}
