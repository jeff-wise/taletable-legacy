
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Text Widget Format
 */
data class TextWidgetFormat(override val id : UUID,
                            val widgetFormat : Func<WidgetFormat>,
                            val insideLabel : Func<String>,
                            val insideLabelFormat : Func<TextFormat>,
                            val outsideLabel : Func<String>,
                            val outsideLabelFormat : Func<TextFormat>,
                            val valueFormat : Func<TextFormat>,
                            val descriptionStyle : Func<TextStyle>) : Model
{

    companion object : Factory<TextWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextWidgetFormat> = when (doc)
        {
            is DocDict -> effApply8(::TextWidgetFormat,
                                     // Model Id
                                     valueResult(UUID.randomUUID()),
                                     // Widget Format
                                     split(doc.maybeAt("widget_format"),
                                           valueResult<Func<WidgetFormat>>(Null()),
                                           fun(d : SpecDoc) : ValueParser<Func<WidgetFormat>> =
                                              effApply(::Comp, WidgetFormat.fromDocument(d))),
                                     // Inside Label
                                     split(doc.maybeText("inside_label"),
                                           valueResult<Func<String>>(Null()),
                                           { valueResult(Prim(it))  }),
                                     // Inside Label Format
                                     split(doc.maybeAt("inside_label_format"),
                                           valueResult<Func<TextFormat>>(Null()),
                                            fun(d : SpecDoc) : ValueParser<Func<TextFormat>> =
                                                effApply(::Comp, TextFormat.fromDocument(d))),
                                     // Outside Label
                                     split(doc.maybeText("outside_label"),
                                           valueResult<Func<String>>(Null()),
                                           { valueResult(Prim(it))  }),
                                     // Outside Label Format
                                     split(doc.maybeAt("outside_label_format"),
                                           valueResult<Func<TextFormat>>(Null()),
                                            fun(d : SpecDoc) : ValueParser<Func<TextFormat>> =
                                                effApply(::Comp, TextFormat.fromDocument(d))),
                                     // Value Format
                                     split(doc.maybeAt("value_format"),
                                           valueResult<Func<TextFormat>>(Null()),
                                           fun(d : SpecDoc) : ValueParser<Func<TextFormat>> =
                                               effApply(::Comp, TextFormat.fromDocument(d))),
                                     // Description Style
                                     split(doc.maybeAt("description_style"),
                                           valueResult<Func<TextStyle>>(Null()),
                                           fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                               effApply(::Comp, TextStyle.fromDocument(d)))
                                     )
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



/**
 * Text Description
 */
data class TextDescription(val value : String)
{

    companion object : Factory<TextDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TextDescription> = when (doc)
        {
            is DocText -> valueResult(TextDescription(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}



//
//
/// ** Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the text widget.
//     */
//    @Override
//    public void initialize(GroupParent groupParent, Context context)
//    {
//        // [1] Initialize the value variable
//        // --------------------------------------------------------------------------------------
//
//        // > If the variable is non-null
//        if (!this.valueVariable.isNull())
//        {
//            this.valueVariable().initialize();
//
//            this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
//                @Override
//                public void onUpdate() {
//                    onValueUpdate();
//                }
//            });
//
//            // > Add to the state
//            State.addVariable(this.valueVariable());
//        }
//
//        // [2] Initialize the helper variables
//        // -------------------------------------------------------------------------------------
//
//        for (VariableUnion variableUnion : this.variables()) {
//            State.addVariable(variableUnion);
//        }
//
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeTextWidget()
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
//
//        this.valueViewId = null;
//    }
//
//
//    // > Value Update
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * When the text widget's valueVariable is updated.
//     */
//    private void onValueUpdate()
//    {
//        if (this.valueViewId != null && !this.valueVariable.isNull())
//        {
//            Activity activity = (Activity) SheetManagerOld.currentSheetContext();
//            TextView textView = (TextView) activity.findViewById(this.valueViewId);
//
//            String value = this.value();
//
//            if (value != null)
//                textView.setText(value);
//        }
//    }
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    private View widgetView(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = this.layout(rowHasLabel, context);
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
//        layout.gravity              = this.data().format().alignment().gravityConstant();
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
//        layout.addView(valueTextView(context));
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
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        if (this.data().format().background() == BackgroundColor.EMPTY)
//            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//
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
//        if (this.format().valueHeight() == Height.WRAP)
//        {
//            layout.padding.topDp    = this.format().valuePaddingVertical().floatValue();
//            layout.padding.bottomDp = this.format().valuePaddingVertical().floatValue();
//        }
//
//        layout.gravity              = this.format().valueStyle().alignment().gravityConstant()
//                | Gravity.CENTER_VERTICAL;
//
//        layout.onClick              = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onTextWidgetShortClick(context);
//            }
//        };
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
//            value.font          = Font.serifFontRegular(context);
//
//            value.color     = this.format().descriptionStyle().color().resourceId();
//            value.size      = this.format().descriptionStyle().size().resourceId();
//
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
//                    new FormattedString.Span(this.value(),
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
//            value.text      = this.value();
//            value.color     = this.format().valueStyle().color().resourceId();
//            value.size      = this.format().valueStyle().size().resourceId();
//            value.font      = this.format().valueStyle().typeface(context);
//        }
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
//        label.layoutGravity     = this.format().outsideLabelStyle().alignment().gravityConstant();
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
//     * On a short click, open the value editor.
//     */
//    private void onTextWidgetShortClick(Context context)
//    {
//        SheetActivity sheetActivity = (SheetActivity) context;
//
//        switch (this.valueVariable().kind())
//        {
//
//            // OPEN the Quick Text Edit Dialog
//            case LITERAL:
//                // If the string is short, edit in DIALOG
//                if (this.value().length() < 145)
//                {
//                    TextEditorDialogFragment textDialog =
//                            TextEditorDialogFragment.forTextWidget(this);
//                    textDialog.show(sheetActivity.getSupportFragmentManager(), "");
//                }
//                // ...otherwise, edit in ACTIVITY
//                else
//                {
//                    Intent intent = new Intent(context, TextEditorActivity.class);
//                    intent.putExtra("text_widget", this);
//                    context.startActivity(intent);
//                }
//                break;
//
//            // OPEN the Choose Value Set Dialog
//            case VALUE:
//
//                Dictionary dictionary         = SheetManagerOld.dictionary();
//
//                if (this.valueVariable() == null || dictionary == null)
//                    break;
//
//                String         valueSetName   = this.valueVariable().valueSetName();
//                ValueReference valueReference = this.valueVariable().valueReference();
//
//                ValueSetUnion valueSetUnion = dictionary.lookup(valueSetName);
//                ValueUnion valueUnion    = dictionary.valueUnion(valueReference);
//
//                if (valueSetUnion == null) {
//                    ApplicationFailure.sheet(
//                            SheetException.undefinedValueSet(
//                                    new UndefinedValueSetError("Text Widget", valueSetName)));
//                    break;
//                }
//
//                if (valueUnion == null) {
//                    ApplicationFailure.value(
//                            ValueException.undefinedValue(
//                                    new UndefinedValueError(valueSetName,
//                                                            valueReference.valueName())));
//                    break;
//                }
//
//                ChooseValueDialogFragment chooseDialog =
//                        ChooseValueDialogFragment.newInstance(valueSetUnion, valueUnion);
//                chooseDialog.show(sheetActivity.getSupportFragmentManager(), "");
//                break;
//
//            case PROGRAM:
//                break;
//        }
//    }
//
//
//    // UPDATE EVENT
//    // -----------------------------------------------------------------------------------------
//
//    public static class UpdateLiteralEvent
//    {
//
//        // PROPERTIES
//        // -------------------------------------------------------------------------------------
//
//        private UUID   widgetId;
//        private String newValue;
//
//
//        // CONSTRUCTORS
//        // -------------------------------------------------------------------------------------
//
//        public UpdateLiteralEvent(UUID widgetId, String newValue)
//        {
//            this.widgetId   = widgetId;
//            this.newValue   = newValue;
//        }
//
//        // API
//        // -------------------------------------------------------------------------------------
//
//        public UUID widgetId()
//        {
//            return this.widgetId;
//        }

//        public String newValue()
//        {
//            return this.newValue;
//        }
//
//    }

