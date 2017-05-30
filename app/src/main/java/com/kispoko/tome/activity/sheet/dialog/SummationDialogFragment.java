
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.model.game.engine.summation.Summation;


/**
 * Summation Dialog Fragment
 */
public class SummationDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Summation summation;
    private String    summationLabel;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public SummationDialogFragment() { }


    public static SummationDialogFragment newInstance(Summation summation, String summationLabel)
    {
        SummationDialogFragment summationDialogFragment = new SummationDialogFragment();

        Bundle args = new Bundle();
//        args.putSerializable("summation", summation);
        args.putString("summation_label", summationLabel);
        summationDialogFragment.setArguments(args);

        return summationDialogFragment;
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
//        this.summation      = (Summation) getArguments().getSerializable("summation");
        this.summationLabel = getArguments().getString("summation_label");

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
        LinearLayout layout = viewLayout(context);

        // > Header
        layout.addView(headerView(context));

        layout.addView(dividerView(context));

        layout.addView(this.nameView(context));
        layout.addView(this.totalView(context));

        // > Summmation
        layout.addView(this.summationView(context));

        // > Footer View
        layout.addView(footerView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_blue_9;
        layout.backgroundResource   = R.drawable.bg_dialog;

        layout.padding.leftDp       = 12f;
        layout.padding.rightDp      = 12f;

        return layout.linearLayout(context);
    }


    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = headerViewLayout(context);

        // > Style Button
        String styleString = context.getString(R.string.style);
        layout.addView(headerButtonView(styleString, R.drawable.ic_dialog_style, context));

        // > Widget Button
        String configureWidgetString = context.getString(R.string.widget);
        layout.addView(headerButtonView(configureWidgetString,
                                        R.drawable.ic_dialog_widget,
                                        context));

        return layout;
    }


    private LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

//        layout.padding.topDp        = 5f;
//        layout.padding.bottomDp     = 5f;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private LinearLayout headerButtonView(String labelText, int iconId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder icon   = new ImageViewBuilder();
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

        label.padding.topDp        = 12f;
        label.padding.bottomDp     = 12f;


        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.heightDp         = 1;

        layout.backgroundColor  = R.color.dark_blue_5;

        return layout.linearLayout(context);
    }


    // ** Summation
    // -----------------------------------------------------------------------------------------

    private LinearLayout summationView(Context context)
    {
        LinearLayout layout = this.summationViewLayout(context);

        // > Components
        layout.addView(this.componentsView(context));


        return layout;
    }


    private LinearLayout summationViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottomDp      = 15f;

        return layout.linearLayout(context);
    }


    private LinearLayout componentsView(Context context)
    {
        LinearLayout layout = this.componentsViewLayout(context);

//        // > Components
//        for (com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary summary: this.summation.summary()) {
//            layout.addView(componentView(summary, context));
//        }

        return layout;
    }


    private LinearLayout componentsViewLayout(Context context)
    {
        LinearLayoutBuilder layout =  new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


//    private LinearLayout componentView(com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary summary, Context context)
//    {
//        LinearLayout layout = this.componentViewLayout(context);
//
//        if (summary.name() != null)
//            layout.addView(this.componentHeaderView(summary.name(), context));
//
//
//        for (Tuple2<String,String> component : summary.components())
//        {
//            String name  = component.getItem1();
//            String value = component.getItem2();
//
//            layout.addView(componentItemView(name, value, context));
//        }
//
//        return layout;
//    }


    private LinearLayout componentViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_widget_wrap_corners_small;
        layout.backgroundColor      = R.color.dark_blue_6;

        layout.margin.topDp         = 3f;
        layout.margin.bottomDp      = 3f;

        return layout.linearLayout(context);
    }


    private RelativeLayout componentItemView(String nameText,
                                             String valueText,
                                             Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout  = new RelativeLayoutBuilder();
        TextViewBuilder       name    = new TextViewBuilder();
        TextViewBuilder       value   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.leftDp           = 10f;
        layout.padding.rightDp          = 10f;

        layout.padding.topDp            = 8f;
        layout.padding.bottomDp         = 8f;

        layout.child(name)
              .child(value);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.layoutType                 = LayoutType.RELATIVE;
        name.width                      = RelativeLayout.LayoutParams.WRAP_CONTENT;
        name.height                     = RelativeLayout.LayoutParams.WRAP_CONTENT;

        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        name.text                       = nameText;
        name.font                       = Font.serifFontRegular(context);
        name.sizeSp                     = 16f;
        name.color                      = R.color.dark_blue_hlx_9;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE;
        value.width                     = RelativeLayout.LayoutParams.WRAP_CONTENT;
        value.height                    = RelativeLayout.LayoutParams.WRAP_CONTENT;

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        value.text                      = valueText;
        value.font                      = Font.serifFontBold(context);
        value.sizeSp                    = 16f;
        value.color                     = R.color.dark_blue_hlx_9;


        return layout.relativeLayout(context);
    }


    private TextView componentHeaderView(String headerText, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.padding.topDp        = 10f;
        header.padding.leftDp       = 10f;

        header.text                 = headerText;
        header.font                 = Font.serifFontRegular(context);
        header.color                = R.color.dark_blue_1;
        header.sizeSp               = 12f;

        return header.textView(context);
    }


    private TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.layoutGravity  = Gravity.CENTER_HORIZONTAL;

        if (this.summationLabel != null)
            name.text           = this.summationLabel.toUpperCase();

        name.font           = Font.serifFontRegular(context);
        name.color          = R.color.dark_blue_hl_8;
        name.sizeSp         = 11f;

        name.margin.topDp    = 15f;

        return name.textView(context);
    }


    private TextView totalView(Context context)
    {
        TextViewBuilder total = new TextViewBuilder();

        total.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        total.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        total.layoutGravity     = Gravity.CENTER_HORIZONTAL;

        //total.text              = this.summation.valueString();

        total.font              = Font.serifFontRegular(context);
        total.color             = R.color.gold_medium_light;
        total.sizeSp            = 34f;

        total.margin.topDp      = 8f;
        total.margin.bottomDp   = 8f;

        return total.textView(context);
    }


    private LinearLayout footerView(Context context)
    {
        LinearLayout layout = footerViewLayout(context);

        // Full Editor Button
        layout.addView(fullEditorButton(context));

        // Done Button
        layout.addView(doneButton(context));

        return layout;
    }


    private LinearLayout footerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL | Gravity.END;

        layout.margin.topDp     = 5f;
        layout.margin.bottomDp  = 15f;

        return layout.linearLayout(context);
    }


    private LinearLayout fullEditorButton(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     button = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.rightDp   = 15f;
        layout.margin.topDp     = 2f;

//        layout.onClick          = new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(getContext(), TextEditorActivity.class);
//                intent.putExtra("text_widget", textWidget);
//                dismiss();
//                startActivity(intent);
//            }
//        };


        layout.child(button);

        // [3] Button
        // -------------------------------------------------------------------------------------

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.text             = context.getString(R.string.calculator);
        button.font             = Font.serifFontRegular(context);
        button.color            = R.color.dark_blue_1;
        button.sizeSp           = 16f;

        return layout.linearLayout(context);
    }


    private LinearLayout doneButton(Context context)
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

        layout.backgroundColor      = R.color.dark_blue_7;
        layout.backgroundResource   = R.drawable.bg_widget_wrap_corners_small;

        layout.padding.topDp        = 6f;
        layout.padding.bottomDp     = 6f;
        layout.padding.leftDp       = 6f;
        layout.padding.rightDp      = 10f;

//        layout.onClick              = new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                sendTextWidgetUpdate(editValueView.getText().toString());
//                dismiss();
//            }
//        };

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_dialog_summation_edit;

        icon.color                  = R.color.green_medium_dark;

        icon.margin.rightDp         = 3f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = context.getString(R.string.edit).toUpperCase();
        label.font                  = Font.serifFontBold(context);
        label.color                 = R.color.green_medium_dark;
        label.sizeSp                = 14f;


        return layout.linearLayout(context);
    }



}
