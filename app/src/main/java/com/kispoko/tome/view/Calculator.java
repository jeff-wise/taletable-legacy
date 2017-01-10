
package com.kispoko.tome.view;


import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Calculator View
 */
public class Calculator
{


    public static LinearLayout view(int startValue, Context context)
    {
        LinearLayout layout = calculatorLayout(context);

        // [1] Result View
        // --------------------------------------------------------------------------------------

        layout.addView(resultView(startValue, context));

        // [2] Top Row
        // --------------------------------------------------------------------------------------

        LinearLayout topRow = topRowLayout(context);
        topRow.addView(decrementButton(context));
        topRow.addView(clearButton(context));
        topRow.addView(equalsButton(context));
        topRow.addView(incrementButton(context));

        layout.addView(topRow);

        // [3] Operators
        // --------------------------------------------------------------------------------------

        layout.addView(operatorsRowView(context));

        // [4] Number Pad
        // --------------------------------------------------------------------------------------

        layout.addView(numberPadView(context));


        layout.addView(zeroRowView(context));

        layout.addView(diceRowView(context));


        return layout;
    }


    private static LinearLayout calculatorLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    /**
     * The result view.
     * @return The Linear Layout.
     */
    private static LinearLayout resultView(int startValue, Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     result = new TextViewBuilder();

        // [2 A] Layout
        // --------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.backgroundColor      = R.color.dark_blue_10;
        layout.margin.bottom        = R.dimen.calculator_result_layout_margin_bottom;

        layout.child(result);

        // [2 B] Value
        // --------------------------------------------------------------------------------------

        result.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        result.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        result.padding.top          = R.dimen.calculator_result_layout_padding_vert;
        result.padding.bottom       = R.dimen.calculator_result_layout_padding_vert;

        result.text                 = Integer.toString(startValue);
        result.font                 = Font.serifFontRegular(context);
        result.color                = R.color.gold_4;
        result.size                 = R.dimen.calculator_result_text_size;

        return layout.linearLayout(context);
    }


    /**
     * The layout for the top row of buttons
     * @param context The context.
     * @return The Linear Layout.
     */
    private static LinearLayout topRowLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left     = R.dimen.calculator_row_padding_horz;
        layout.padding.right    = R.dimen.calculator_row_padding_horz;
        layout.margin.bottom    = R.dimen.calculator_top_row_layout_margin_bottom;

        return layout.linearLayout(context);
    }


    /**
     * The +1 button.
     * @param context The context.
     * @return The Text View.
     */
    private static TextView incrementButton(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.0f;
        button.gravity          = Gravity.CENTER_HORIZONTAL;

        button.backgroundColor  = R.color.dark_blue_5;
        button.text             = "+ 1";
        button.font             = Font.serifFontBold(context);
        button.size             = R.dimen.calculator_button_inc_text_size;
        button.color            = R.color.dark_blue_hlx_7;

        button.padding.top      = R.dimen.calculator_button_inc_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_inc_padding_vert;
        button.margin.left      = R.dimen.calculator_button_inc_margin_left;

        return button.textView(context);
    }


    /**
     * The -1 button.
     * @param context The context.
     * @return The Text View.
     */
    private static TextView decrementButton(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.0f;
        button.gravity          = Gravity.CENTER_HORIZONTAL;

        button.backgroundColor  = R.color.dark_blue_5;
        button.text             = "- 1";
        button.font             = Font.serifFontBold(context);
        button.size             = R.dimen.calculator_button_inc_text_size;
        button.color            = R.color.dark_blue_hlx_7;

        button.padding.top      = R.dimen.calculator_button_inc_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_inc_padding_vert;
        button.margin.right     = R.dimen.calculator_button_dec_margin_right;

        return button.textView(context);
    }


    /**
     * The clear button.
     * @param context The context.
     * @return The Text View.
     */
    private static TextView clearButton(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.5f;
        button.gravity          = Gravity.CENTER_HORIZONTAL;

        button.backgroundColor  = R.color.dark_blue_5;
        button.text             = "CLEAR";
        button.color            = R.color.dark_blue_hlx_7;
        button.font             = Font.serifFontBold(context);
        button.size             = R.dimen.calculator_button_clear_text_size;

        button.padding.top      = R.dimen.calculator_button_clear_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_clear_padding_vert;
        button.margin.left      = R.dimen.calculator_button_clear_margin_horz;
        button.margin.right     = R.dimen.calculator_button_clear_margin_horz;

        return button.textView(context);
    }


    /**
     * Equals button view.
     * @param context The context.
     * @return The Text View.
     */
    private static TextView equalsButton(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.5f;
        button.gravity          = Gravity.CENTER_HORIZONTAL;

        button.text             = "=";
        button.font             = Font.serifFontBold(context);
        button.backgroundColor  = R.color.dark_blue_5;
        button.size             = R.dimen.calculator_button_equals_text_size;
        button.color            = R.color.dark_blue_hlx_7;

        button.padding.top      = R.dimen.calculator_button_equals_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_equals_padding_vert;
        button.margin.left      = R.dimen.calculator_button_clear_margin_horz;
        button.margin.right     = R.dimen.calculator_button_clear_margin_horz;

        return button.textView(context);
    }


    /**
     * The Number Pad View.
     * @param context The context.
     * @return The Linear Layout.
     */
    private static LinearLayout numberPadView(Context context)
    {
        LinearLayout layout = numberPadLayout(context);

        // > Row 1
        // --------------------------------------------------------------------------------------

        LinearLayout row1 = numberPadRowView(context);

        TextView oneButton = numberButton("1", context);
        TextView twoButton = numberButton("2", context);
        TextView threeButton = numberButton("3", context);

        row1.addView(oneButton);
        row1.addView(twoButton);
        row1.addView(threeButton);

        layout.addView(row1);

        // > Row 2
        // --------------------------------------------------------------------------------------

        LinearLayout row2 = numberPadRowView(context);

        TextView fourButton = numberButton("4", context);
        TextView fiveButton = numberButton("5", context);
        TextView sixButton = numberButton("6", context);

        row2.addView(fourButton);
        row2.addView(fiveButton);
        row2.addView(sixButton);

        layout.addView(row2);

        // > Row 3
        // --------------------------------------------------------------------------------------

        LinearLayout row3 = numberPadRowView(context);

        TextView sevenButton = numberButton("7", context);
        TextView eightButton = numberButton("8", context);
        TextView nineButton = numberButton("9", context);

        row3.addView(sevenButton);
        row3.addView(eightButton);
        row3.addView(nineButton);

        layout.addView(row3);

        return layout;
    }


    /**
     * The number pad view layout.
     * @param context The context.
     * @return The Linear Layout.
     */
    private static LinearLayout numberPadLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.top       = R.dimen.calculator_number_pad_margin_top;
        layout.margin.left      = R.dimen.calculator_number_pad_margins_horz;
        layout.margin.right     = R.dimen.calculator_number_pad_margins_horz;

        return layout.linearLayout(context);
    }


    /**
     * Number pad row view.
     * @param context The context.
     * @return The Linear Layout.
     */
    private static LinearLayout numberPadRowView(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    /**
     * A number button on the number pad.
     * @param label The button's text.
     * @param context The context.
     * @return The Text View.
     */
    private static TextView numberButton(String label, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.0f;
        button.gravity          = Gravity.CENTER;

        button.text             = label;
        button.color            = R.color.dark_blue_hlx_7;
        button.size             = R.dimen.calculator_number_pad_text_size;
        button.font             = Font.serifFontBold(context);
        button.backgroundColor  = R.color.dark_blue_5;

        button.padding.top      = R.dimen.calculator_number_pad_button_padding_vert;
        button.padding.bottom   = R.dimen.calculator_number_pad_button_padding_vert;
        button.margin.left      = R.dimen.calculator_number_pad_button_margins_horz;
        button.margin.right     = R.dimen.calculator_number_pad_button_margins_horz;
        button.margin.top       = R.dimen.calculator_number_pad_button_margins_vert;
        button.margin.bottom    = R.dimen.calculator_number_pad_button_margins_vert;

        return button.textView(context);
    }


    /**
     * The row view for the operators (*, /, etc)
     * @param context The context.
     * @return The Linear Layout.
     */
    private static LinearLayout operatorsRowView(Context context)
    {
        LinearLayout layout = operatorsRowLayout(context);

        layout.addView(addButton(context));
        layout.addView(subtractButton(context));
        layout.addView(multiplyButton(context));
        layout.addView(divideButton(context));

        return layout;
    }


    /**
     * The operator row layout.
     * @param context The context.
     * @return The Linear Layout.
     */
    private static LinearLayout operatorsRowLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static ImageView subtractButton(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.0f;

        button.backgroundColor  = R.color.dark_blue_5;
        button.image            = R.drawable.ic_calculator_subtract;

        button.margin.left      = R.dimen.calculator_button_operator_margin_horz;
        button.margin.right     = R.dimen.calculator_button_operator_margin_horz;
        button.padding.top      = R.dimen.calculator_button_operator_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_operator_padding_vert;

        return button.imageView(context);
    }


    private static ImageView addButton(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.0f;

        button.backgroundColor  = R.color.dark_blue_5;
        button.image            = R.drawable.ic_calculator_add;

        button.margin.left      = R.dimen.calculator_button_operator_margin_horz;
        button.margin.right     = R.dimen.calculator_button_operator_margin_horz;
        button.padding.top      = R.dimen.calculator_button_operator_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_operator_padding_vert;

        return button.imageView(context);
    }


    private static ImageView divideButton(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.0f;

        button.backgroundColor  = R.color.dark_blue_5;
        button.image            = R.drawable.ic_calculator_divide;

        button.margin.left      = R.dimen.calculator_button_operator_margin_horz;
        button.margin.right     = R.dimen.calculator_button_operator_margin_horz;
        button.padding.top      = R.dimen.calculator_button_operator_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_operator_padding_vert;

        return button.imageView(context);
    }


    private static ImageView multiplyButton(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.width            = 0;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight           = 1.0f;

        button.backgroundColor  = R.color.dark_blue_5;
        button.image            = R.drawable.ic_calculator_multiply;

        button.margin.left      = R.dimen.calculator_button_operator_margin_horz;
        button.margin.right     = R.dimen.calculator_button_operator_margin_horz;
        button.padding.top      = R.dimen.calculator_button_operator_padding_vert;
        button.padding.bottom   = R.dimen.calculator_button_operator_padding_vert;

        return button.imageView(context);
    }


    /**
     * The row with the 0 button.
     * @param context The context.
     * @return The Linear Layout.
     */
    private static LinearLayout zeroRowView(Context context)
    {
        LinearLayout layout = zeroRowLayout(context);

        layout.addView(d6Button(context));
        layout.addView(zeroButton(context));
        layout.addView(d20Button(context));

        return layout;
    }


    private static LinearLayout zeroRowLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static TextView d6Button(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;
        button.gravity              = Gravity.CENTER_HORIZONTAL;

        button.text                 = "d6";
        button.color                = R.color.dark_blue_hlx_7;
        button.font                 = Font.serifFontBold(context);

        return button.textView(context);
    }


    private static TextView zeroButton(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;
        button.gravity              = Gravity.CENTER_HORIZONTAL;

        button.text                 = "0";
        button.color                = R.color.dark_blue_hlx_7;
        button.font                 = Font.serifFontBold(context);

        return button.textView(context);
    }


    private static TextView d20Button(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;
        button.gravity              = Gravity.CENTER_HORIZONTAL;

        button.text                 = "d20";
        button.color                = R.color.dark_blue_hlx_7;
        button.font                 = Font.serifFontBold(context);

        return button.textView(context);
    }


    private static LinearLayout diceRowView(Context context)
    {
        LinearLayout layout = diceRowLayout(context);

        layout.addView(d4Button(context));
        layout.addView(d8Button(context));
        layout.addView(d100Button(context));
        layout.addView(dXXButton(context));

        return layout;
    }


    private static LinearLayout diceRowLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static TextView d4Button(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;
        button.gravity              = Gravity.CENTER_HORIZONTAL;

        button.text                 = "d4";
        button.color                = R.color.dark_blue_hlx_7;
        button.font                 = Font.serifFontBold(context);

        return button.textView(context);
    }


    private static TextView d8Button(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;
        button.gravity              = Gravity.CENTER_HORIZONTAL;

        button.text                 = "d8";
        button.color                = R.color.dark_blue_hlx_7;
        button.font                 = Font.serifFontBold(context);

        return button.textView(context);
    }


    private static TextView d100Button(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;
        button.gravity              = Gravity.CENTER_HORIZONTAL;

        button.text                 = "d100";
        button.color                = R.color.dark_blue_hlx_7;
        button.font                 = Font.serifFontBold(context);

        return button.textView(context);
    }


    private static TextView dXXButton(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = 0;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.weight               = 1.0f;
        button.gravity              = Gravity.CENTER_HORIZONTAL;

        button.text                 = "dXX";
        button.color                = R.color.dark_blue_hlx_7;
        button.font                 = Font.serifFontBold(context);

        return button.textView(context);
    }

}
