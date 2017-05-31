
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Number Widget Format
 */
data class NumberWidgetFormat(override val id : UUID,
                              val widgetFormat : Func<WidgetFormat>,
                              val insideLabel : Func<String>,
                              val insideLabelFormat : Func<TextFormat>,
                              val outsideLabel : Func<String>,
                              val outsideLabelFormat : Func<TextFormat>,
                              val valueFormat : Func<TextFormat>,
                              val descriptionStyle : Func<TextStyle>,
                              val valuePrefixStyle : Func<TextStyle>,
                              val valuePostfixStyle : Func<TextStyle>,
                              val valueSeparator : Func<String>,
                              val valueSeparatorFormat : Func<TextFormat>) : Model
{

    companion object : Factory<NumberWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::NumberWidgetFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Widget Format
                                   doc.at("widget_format") ap {
                                       effApply(::Comp, WidgetFormat.fromDocument(it))
                                   },
                                   // Inside Label
                                   split(doc.maybeText("inside_label"),
                                         nullEff<String>(),
                                         { effValue(Prim(it))  }),
                                   // Inside Label Format
                                   split(doc.maybeAt("inside_label_format"),
                                         nullEff<TextFormat>(),
                                         { effApply(::Comp, TextFormat.fromDocument(it)) }),
                                   // Outside Label
                                   split(doc.maybeText("outside_label"),
                                         nullEff<String>(),
                                         { effValue(Prim(it))  }),
                                   // Outside Label Format
                                   split(doc.maybeAt("outside_label_format"),
                                         nullEff<TextFormat>(),
                                         { effApply(::Comp, TextFormat.fromDocument(it)) }),
                                   // Value Format
                                   split(doc.maybeAt("value_format"),
                                         nullEff<TextFormat>(),
                                         { effApply(::Comp, TextFormat.fromDocument(it)) }),
                                   // Description Style
                                   split(doc.maybeAt("description_style"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // Value Prefix Style
                                   split(doc.maybeAt("value_prefix_format"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // Value Postfix Style
                                   split(doc.maybeAt("value_postfix_format"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // Value Separator
                                   split(doc.maybeText("value_separator"),
                                         nullEff<String>(),
                                         { effValue(Prim(it)) }),
                                   // Outside Label Format
                                   split(doc.maybeAt("outside_label_format"),
                                         nullEff<TextFormat>(),
                                         { effApply(::Comp, TextFormat.fromDocument(it))})
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}

//
//
//
//
//    // ** Base Value Variable Name
//    // -----------------------------------------------------------------------------------------
//
//    @Nullable
//    private String baseValueVariableName()
//    {
//        return this.baseValueVariableName.getValue();
//    }
//
//
//    private Integer baseValue()
//    {
//        if (this.baseValueVariableName.isNull())
//            return 0;
//
//        try
//        {
//            NumberVariable numberVariable =
//                                State.numberVariableWithName(this.baseValueVariableName());
//            return numberVariable.value();
//        }
//        catch (VariableException exception)
//        {
//            ApplicationFailure.variable(exception);
//            return 0;
//        }
//        catch (NullVariableException exception)
//        {
//            ApplicationFailure.nullVariable(exception);
//            return 0;
//        }
//    }
//
//
//    // ** Description
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The number description.
//     * @return The description.
//     */
//    public String description()
//    {
//        return this.description.getValue();
//    }
//
//
//    // ** Variables
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * Get the text widget's helper variables.
//     * @return The list of variables.
//     */
//    public List<VariableUnion> variables()
//    {
//        return this.variables.getValue();
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeNumberWidget()
//    {
//        // [1] Apply default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Alignment
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//        // ** Corners
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.SMALL);
//
//        // ** Underline Thickness
//        if (this.data().format().underlineThicknessIsDefault())
//            this.data().format().setUnderlineThickness(0);
//
//    }
//
//    // -----------------------------------------------------------------------------------------
//
//    private View widgetView(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = this.layout(rowHasLabel, context);
//
//        this.widgetViewId   = Util.generateViewId();
//        layout.setId(this.widgetViewId);
//
//        layout.addView(mainView(context));
//
//        return layout;
//    }
//
//
//    /**
//     * The outer-most view that holds the outside labels and the value view.
//     * @param context The context.
//     * @return The main view Linear Layout.
//     */
//    private LinearLayout mainView(Context context)
//    {
//        LinearLayout layout = mainLayout(context);
//
//        // > Outside Top/Left Label View
//        if (this.format().outsideLabel() != null) {
//            if (this.format().outsideLabelPosition() == Position.TOP ||
//                this.format().outsideLabelPosition() == Position.LEFT) {
//                layout.addView(this.outsideLabelView(context));
//            }
//        }
//
//        // > Value
//        layout.addView(this.valueMainView(context));
//
//        // > Outside Bottom/Right Label View
//        if (this.format().outsideLabel() != null) {
//            if (this.format().outsideLabelPosition() == Position.BOTTOM ||
//                this.format().outsideLabelPosition() == Position.RIGHT) {
//                layout.addView(this.outsideLabelView(context));
//            }
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout mainLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        layout.orientation          = this.format().outsideLabelPosition()
//                                          .linearLayoutOrientation();
//
//        layout.gravity              = this.data().format().alignment().gravityConstant()
//                                        | Gravity.CENTER_VERTICAL;
//
//        layout.marginSpacing        = this.data().format().margins();
//
//        return layout.linearLayout(context);
//    }
//
//
//    /**
//     * The view that holds the value as well as the inside labels around the value.
//     * @param context The context.
//     * @return The value main view Linear Layout.
//     */
//    private LinearLayout valueMainView(Context context)
//    {
//        LinearLayout layout = valueMainViewLayout(context);
//
//        // > Inside Top/Left Label View
//        if (this.format().insideLabel() != null && this.description() == null) {
//            if (this.format().insideLabelPosition() == Position.TOP ||
//                this.format().insideLabelPosition() == Position.LEFT) {
//                layout.addView(this.insideLabelView(context));
//            }
//        }
//
//        layout.addView(valueView(context));
//
//        // > Inside Bottom/Right Label View
//        if (this.format().insideLabel() != null && this.description() == null) {
//            if (this.format().insideLabelPosition() == Position.BOTTOM ||
//                this.format().insideLabelPosition() == Position.RIGHT) {
//                layout.addView(this.insideLabelView(context));
//            }
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout valueMainViewLayout(final Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = this.format().insideLabelPosition().linearLayoutOrientation();
//
//        // > Width
//        //   If no padding is specified, the value (and its background) stretches to fill the
//        //   space. Otherwise it only stretches as far as the padding allows
//        // -------------------------------------------------------------------------------------
//        if (this.format().valuePaddingHorizontal() != null ||
//            this.data().format().background() == BackgroundColor.EMPTY) {
//            layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        }
//        else {
//            layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        }
//
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//
//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }
//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {
//            layout.backgroundColor      = this.data().format().background().colorId();
//
//            if (this.format().valueHeight() != Height.WRAP)
//            {
//                layout.backgroundResource   = this.format().valueHeight()
//                                                  .resourceId(this.data().format().corners());
//            }
//            else
//            {
//                layout.backgroundResource = this.data().format().corners().widgetResourceId();
//            }
//        }
//
//
//        if (this.format().valueHeight() == Height.WRAP)
//        {
//            layout.padding.topDp    = this.format().valuePaddingVertical().floatValue();
//            layout.padding.bottomDp = this.format().valuePaddingVertical().floatValue();
//        }
//
//
//        layout.gravity              = this.format().valueStyle().alignment().gravityConstant()
//                                        | Gravity.CENTER_VERTICAL;
//
//        // > Padding
//        // -------------------------------------------------------------------------------------
//        if (this.format().valuePaddingHorizontal() != null)
//        {
//            layout.padding.leftDp   = this.format().valuePaddingHorizontal().floatValue();
//            layout.padding.rightDp  = this.format().valuePaddingHorizontal().floatValue();
//        }
//
//        layout.onClick              = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onNumberWidgetShortClick(context);
//            }
//        };
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout valueView(Context context)
//    {
//        LinearLayout layout = valueViewLayout(context);
//
//        // > Value
//        layout.addView(valueTextView(context));
//
//        // > Base Value
//        if (this.baseValueVariableName() != null)
//            layout.addView(baseValueView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout valueViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.HORIZONTAL;
//
//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private TextView valueTextView(Context context)
//    {
//        TextViewBuilder value = new TextViewBuilder();
//
//        this.valueViewId   = Util.generateViewId();
//
//        value.id            = this.valueViewId;
//
//        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        value.layoutGravity = this.format().valueStyle().alignment().gravityConstant()
//                                | Gravity.CENTER_VERTICAL;
//        value.gravity       = this.format().valueStyle().alignment().gravityConstant();
//
//        if (this.description() != null)
//        {
//            value.layoutGravity = this.format().descriptionStyle().alignment().gravityConstant()
//                                    | Gravity.CENTER_VERTICAL;
//            value.gravity       = this.format().descriptionStyle().alignment().gravityConstant();
//
//            this.format().descriptionStyle().styleTextViewBuilder(value, context);
//
//            List<FormattedString.Span> spans = new ArrayList<>();
//
//            FormattedString.Span labelSpan =
//                new FormattedString.Span(this.format().insideLabel(),
//                                         this.format().insideLabelStyle().color().color(context),
//                                         this.format().descriptionStyle().size().size(),
//                                         this.format().insideLabelStyle().font());
//
//            FormattedString.Span valueSpan =
//                    new FormattedString.Span(this.valueString(),
//                                             context.getString(R.string.placeholder_value),
//                                             this.format().valueStyle().color().color(context),
//                                             this.format().descriptionStyle().size().size(),
//                                             this.format().valueStyle().font());
//
//            if (this.format().insideLabel() != null)
//                spans.add(labelSpan);
//
//            spans.add(valueSpan);
//
//            value.textSpan  = FormattedString.spannableStringBuilder(this.description(),
//                                                                     spans);
//        }
//        else
//        {
//            value.text      = this.valueString();
//            value.color     = this.format().valueStyle().color().resourceId();
//            value.size      = this.format().valueStyle().size().resourceId();
//            value.font      = this.format().valueStyle().typeface(context);
//        }
//
//        return value.textView(context);
//    }
//
//
//    private LinearLayout baseValueView(Context context)
//    {
//        LinearLayout layout = this.baseValueViewLayout(context);
//
//        // > Separator
//        layout.addView(baseValueSeparatorView(context));
//
//        // > Value
//        layout.addView(baseValueTextView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout baseValueViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.layoutGravity        = this.format().baseValueVerticalAlignment().gravityConstant();
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private TextView baseValueSeparatorView(Context context)
//    {
//        TextViewBuilder separator = new TextViewBuilder();
//
//        separator.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
//        separator.height        = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        separator.text          = this.format().baseValueSeparator();
//
//        this.format().baseValueSeparatorStyle().styleTextViewBuilder(separator, context);
//
//        separator.marginSpacing = this.format().baseValueSeparatorMargins();
//
//        return separator.textView(context);
//    }
//
//
//    private TextView baseValueTextView(Context context)
//    {
//        TextViewBuilder value = new TextViewBuilder();
//
//        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        value.text          = this.baseValue().toString();
//
//        this.format().baseValueStyle().styleTextViewBuilder(value, context);
//
//        value.marginSpacing = this.format().baseValueMargins();
//
//        return value.textView(context);
//    }
//
//
//    private TextView outsideLabelView(Context context)
//    {
//        TextViewBuilder label = new TextViewBuilder();
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.layoutGravity     = this.format().outsideLabelStyle().alignment().gravityConstant()
//                                    | Gravity.CENTER_VERTICAL;
//
//        label.text              = this.format().outsideLabel();
//
//        this.format().outsideLabelStyle().styleTextViewBuilder(label, context);
//
//        label.marginSpacing     = this.format().outsideLabelMargins();
//
//        return label.textView(context);
//    }
//
//
//    private TextView insideLabelView(Context context)
//    {
//        TextViewBuilder label   = new TextViewBuilder();
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.text              = this.format().insideLabel();
//
//        label.layoutGravity     = this.format().insideLabelStyle().alignment().gravityConstant()
//                                        | Gravity.CENTER_VERTICAL;
//
//        this.format().insideLabelStyle().styleTextViewBuilder(label, context);
//
//        label.marginSpacing     = this.format().insideLabelMargins();
//
//        return label.textView(context);
//    }
//
//
//    // > Clicks
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * When the number widget is clicked once, open a quick edit/view dialog.
//     * @param context The context
//     */
//    private void onNumberWidgetShortClick(Context context)
//    {
//        SheetActivityOld sheetActivity = (SheetActivityOld) context;
//
//        switch (this.valueVariable().kind())
//        {
//            case LITERAL:
//                ArrayList<DialogOptionButton> dialogButtons = new ArrayList<>();
//
//                DialogOptionButton styleButton =
//                        new DialogOptionButton(R.string.style, R.drawable.ic_dialog_style, null);
//
//                DialogOptionButton widgetButton =
//                        new DialogOptionButton(R.string.widget, R.drawable.ic_dialog_widget, null);
//
//                dialogButtons.add(styleButton);
//                dialogButtons.add(widgetButton);
//
//                CalculatorDialogFragment dialog =
//                            CalculatorDialogFragment.newInstance(valueVariable(), dialogButtons);
//                dialog.show(sheetActivity.getSupportFragmentManager(), "");
//                break;
//
//            // OPEN the summation preview dialog
//            case SUMMATION:
//                Summation summation      = this.valueVariable().summation();
//
//                String    summationLabel = "";
//                if (this.data().name() != null)
//                    summationLabel = this.data().name();
//                else if (this.valueVariable().label() != null)
//                    summationLabel = this.valueVariable().label();
//
//                SummationDialogFragment summationDialog =
//                                    SummationDialogFragment.newInstance(summation, summationLabel);
//                summationDialog.show(sheetActivity.getSupportFragmentManager(), "");
//                break;
//        }
//    }
//
//
//
//    // > Value Updates
//    // -----------------------------------------------------------------------------------------
//
//
//    /**
//     * Update the value view section of the widget view to reflect a new value.
//     * @param context The context
//     */
//    private void updateValueView(Context context)
//    {
//        if (this.widgetViewId != null)
//        {
//            Activity activity = (Activity) SheetManagerOld.currentSheetContext();
//            LinearLayout widgetView = (LinearLayout) activity.findViewById(this.widgetViewId);
//
//            if (widgetView != null) {
//                widgetView.removeAllViews();
//                widgetView.addView(this.mainView(context));
//            }
//        }
//    }
//

