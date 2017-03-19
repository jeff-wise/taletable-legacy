
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.EditTextBuilder;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Number Widget Dialog Fragment
 */
public class CalculatorDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private NumberVariable              numberVariable;
    private List<DialogOptionButton>    buttons;

    private EditText                    valueEditText;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public CalculatorDialogFragment() { }


    public static CalculatorDialogFragment newInstance(NumberVariable numberVariable,
                                                       List<DialogOptionButton> buttons)
    {
        CalculatorDialogFragment numberWidgetDialogFragment = new CalculatorDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("number_variable", numberVariable);
        args.putSerializable("buttons", (Serializable) buttons);
        numberWidgetDialogFragment.setArguments(args);

        return numberWidgetDialogFragment;
    }


    // DIALOG FRAGMENT
    // ------------------------------------------------------------------------------------------

    @Override @NonNull @SuppressWarnings("unchecked")
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
        this.numberVariable = (NumberVariable) getArguments().getSerializable("number_variable");
        this.buttons        = (List<DialogOptionButton>) getArguments().getSerializable("buttons");

        if (this.buttons == null)
            this.buttons    = new ArrayList<>();

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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Update
    // ------------------------------------------------------------------------------------------

//    private void sendTextWidgetUpdate(String newValue)
//    {
//        TextWidget.UpdateLiteralEvent event =
//                new TextWidget.UpdateLiteralEvent(this.textWidget.getId(), newValue);
//
//        EventBus.getDefault().post(event);
//    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        layout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        layout.setFocusableInTouchMode(true);

        // > Header
        layout.addView(this.headerView(context));

        layout.addView(this.dividerView(context));

        // > Calculator
        layout.addView(this.calculatorView(context));

        // > Footer View
        layout.addView(this.footerView(context));

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


        return layout.linearLayout(context);
    }


    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = headerViewLayout(context);

        for (DialogOptionButton button : this.buttons)
        {
            layout.addView(headerButtonView(button.labelId(),
                                            button.iconId(),
                                            button.onClickListener(),
                                            context));
        }
        return layout;
    }


    private LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.topDp        = 6f;
        layout.padding.bottomDp     = 4f;

        layout.padding.leftDp       = 12f;
        layout.padding.rightDp      = 12f;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.heightDp        = 1;

        divider.backgroundColor = R.color.dark_blue_6;

        divider.margin.leftDp   = 12f;
        divider.margin.rightDp  = 12f;

        return divider.linearLayout(context);
    }


    private LinearLayout headerButtonView(int labelId,
                                          int iconId,
                                          View.OnClickListener onClickListener,
                                          Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder icon   = new ImageViewBuilder();
        TextViewBuilder label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.HORIZONTAL;

        if (this.buttons.size() == 2)
            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        else
            layout.width            = 0;

        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        if (this.buttons.size() == 3)
            layout.weight           = 1f;

        layout.gravity          = Gravity.CENTER_VERTICAL;

        if (this.buttons.size() == 2)
            layout.margin.rightDp   = 25f;
        if (this.buttons.size() == 3)
            layout.margin.rightDp   = 10f;

        layout.onClick          = onClickListener;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = iconId;

        icon.color          = R.color.dark_blue_2;

        if (this.buttons.size() == 2)
            icon.margin.rightDp = 4f;
        else if (this.buttons.size() == 3)
            icon.margin.rightDp = 2f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.gravity              = Gravity.CENTER_HORIZONTAL;

        label.textId               = labelId;

        label.color                = R.color.dark_blue_1;
        label.font                 = Font.serifFontRegular(context);

        if (this.buttons.size() > 2)
            label.sizeSp               = 13.0f;
        else
            label.sizeSp               = 16.0f;

        label.padding.topDp        = 12f;
        label.padding.bottomDp     = 12f;


        return layout.linearLayout(context);
    }


    private LinearLayout calculatorView(Context context)
    {
        LinearLayout layout = calculatorViewLayout(context);

        // > Expression
        layout.addView(calculatorExpressionView(context));

        // > Buttons
        layout.addView(calculatorButtonsView(context));

        return layout;
    }


    private LinearLayout calculatorViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private RelativeLayout calculatorExpressionView(Context context)
    {
        RelativeLayout layout = calculatorExpressionViewLayout(context);

        // > Clear Button
        layout.addView(calculatorExpressionClearButtonView(context));

        // > Value
        this.valueEditText = calculatorValueView(context);
        layout.addView(this.valueEditText);

        // > Delete Button
        layout.addView(calculatorExpressionBackspaceButtonView(context));

        return layout;
    }


    private RelativeLayout calculatorExpressionViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.topDp    = 5f;
        layout.padding.bottomDp = 5f;

        layout.padding.leftDp   = 17f;
        layout.padding.rightDp  = 20f;

        return layout.relativeLayout(context);
    }


    private ImageView calculatorExpressionClearButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType       = LayoutType.RELATIVE;

        button.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        button.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        button.addRule(RelativeLayout.CENTER_VERTICAL);

        button.image            = R.drawable.ic_dialog_number_widget_clear;

        button.color            = R.color.dark_blue_1;

        button.onClick          = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                valueEditText.setText("");
            }
        };

        return button.imageView(context);
    }


    private ImageView calculatorExpressionBackspaceButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType       = LayoutType.RELATIVE;

        button.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        button.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        button.addRule(RelativeLayout.CENTER_VERTICAL);

        button.image            = R.drawable.ic_dialog_number_widget_backspace;

        button.color            = R.color.dark_blue_3;

        button.onClick          = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String currentValue = valueEditText.getText().toString();
                valueEditText.setText(currentValue.substring(0, currentValue.length() - 1));
            }
        };

        return button.imageView(context);
    }


    private EditText calculatorValueView(Context context)
    {
        EditTextBuilder value = new EditTextBuilder();

        value.layoutType            = LayoutType.RELATIVE;

        value.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT;
        value.height                = RelativeLayout.LayoutParams.WRAP_CONTENT;

        value.addRule(RelativeLayout.CENTER_IN_PARENT);

        try {
            if (this.numberVariable != null)
                value.text          = this.numberVariable.valueString();
        }
        catch (NullVariableException exception) {
            value.text              = "N/A";
        }

        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.gold_medium_light;
        value.sizeSp                = 25f;

        value.backgroundColor       = R.color.dark_blue_9;
        value.backgroundResource    = R.drawable.bg_edit_text_no_style;

        value.underlineColor        = R.color.dark_blue_hl_1;

        value.margin.topDp          = 12f;
        value.margin.bottomDp       = 12f;

        value.margin.rightDp        = 12f;
        value.padding.leftDp        = 3f;

        return value.editText(context);
    }


    private LinearLayout calculatorButtonsView(Context context)
    {
        LinearLayout layout = calculatorButtonsViewLayout(context);

        // > Row 1: [1 2 3]
        layout.addView(calculatorButtonsRowOneView(context));

        // > Row 2: [4 5 6]
        layout.addView(calculatorButtonsRowTwoView(context));

        // > Row 3: [7 8 9]
        layout.addView(calculatorButtonsRowThreeView(context));

        // > Row 4: [- 0 +]
        layout.addView(calculatorButtonsRowFourView(context));

        return layout;
    }


    private LinearLayout calculatorButtonsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.leftDp       = 10f;
        layout.padding.rightDp      = 10f;

        return layout.linearLayout(context);
    }


    private LinearLayout calculatorButtonsRowOneView(Context context)
    {
        LinearLayout layout = calculatorButtonsRowViewLayout(context);

        // > One Button
        layout.addView(calculatorNumberButtonView("1", context));

        // > Two Button
        layout.addView(calculatorNumberButtonView("2", context));

        // > Three Button
        layout.addView(calculatorNumberButtonView("3", context));

        return layout;
    }


    private LinearLayout calculatorButtonsRowTwoView(Context context)
    {
        LinearLayout layout = calculatorButtonsRowViewLayout(context);

        // > Four Button
        layout.addView(calculatorNumberButtonView("4", context));

        // > Five Button
        layout.addView(calculatorNumberButtonView("5", context));

        // > Six Button
        layout.addView(calculatorNumberButtonView("6", context));

        return layout;
    }


    private LinearLayout calculatorButtonsRowThreeView(Context context)
    {
        LinearLayout layout = calculatorButtonsRowViewLayout(context);

        // > Seven Button
        layout.addView(calculatorNumberButtonView("7", context));

        // > Eight Button
        layout.addView(calculatorNumberButtonView("8", context));

        // > Nine Button
        layout.addView(calculatorNumberButtonView("9", context));

        return layout;
    }


    private LinearLayout calculatorButtonsRowFourView(Context context)
    {
        LinearLayout layout = calculatorButtonsRowViewLayout(context);

        // > Minus Button
        layout.addView(calculatorOperationButtonView("-", context));

        // > Zero Button
        layout.addView(calculatorNumberButtonView("0", context));

        // > Plus Button
        layout.addView(calculatorOperationButtonView("+", context));

        return layout;
    }


    private TextView calculatorNumberButtonView(final String number, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.heightDp             = 55;
        button.weight               = 1f;

        button.gravity              = Gravity.CENTER;

        button.backgroundResource   = R.drawable.bg_dialog_number_widget_calculator_button;
        button.backgroundColor      = R.color.dark_blue_7;

        button.margin.leftDp        = 3f;
        button.margin.rightDp       = 3f;

        button.text                 = number;

        button.font                 = Font.serifFontRegular(context);
        button.color                = R.color.dark_blue_hlx_3;
        button.sizeSp               = 20f;

        button.onClick              = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String currentValue = valueEditText.getText().toString();
                String currentValuePlusNumber = currentValue + number;
                valueEditText.setText(currentValuePlusNumber);
            }
        };

        return button.textView(context);
    }


    private TextView calculatorOperationButtonView(String label, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.heightDp             = 55;
        button.weight               = 1f;

        button.gravity              = Gravity.CENTER;

        button.backgroundResource   = R.drawable.bg_dialog_number_widget_calculator_button;
        button.backgroundColor      = R.color.dark_blue_5;

        button.margin.leftDp        = 3f;
        button.margin.rightDp       = 3f;

        button.text                 = label;

        button.font                 = Font.serifFontRegular(context);
        button.color                = R.color.dark_blue_hlx_3;
        button.sizeSp               = 28f;

        return button.textView(context);
    }


    private LinearLayout calculatorButtonsRowViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottomDp      = 10f;

        return layout.linearLayout(context);
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

        layout.margin.topDp     = 10f;
        layout.margin.bottomDp  = 15f;

        layout.margin.leftDp    = 12f;
        layout.margin.rightDp   = 12f;

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

        button.text             = context.getString(R.string.full_editor);
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

        layout.onClick              = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //sendTextWidgetUpdate(editValueView.getText().toString());
                dismiss();
            }
        };

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_dialog_done;

        icon.color                  = R.color.green_medium_dark;

        icon.margin.rightDp         = 3f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = context.getString(R.string.done).toUpperCase();
        label.font                  = Font.serifFontBold(context);
        label.color                 = R.color.green_medium_dark;
        label.sizeSp                = 14f;


        return layout.linearLayout(context);
    }

}
