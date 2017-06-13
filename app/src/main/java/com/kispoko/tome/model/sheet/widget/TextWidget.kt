
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Text Widget Format
 */
data class TextWidgetFormat(override val id : UUID,
                            val widgetFormat : Comp<WidgetFormat>,
                            val insideLabel : Maybe<Prim<TextWidgetLabel>>,
                            val insideLabelFormat : Comp<TextFormat>,
                            val outsideLabel : Maybe<Prim<TextWidgetLabel>>,
                            val outsideLabelFormat : Comp<TextFormat>,
                            val valueFormat : Comp<TextFormat>,
                            val descriptionStyle : Comp<TextStyle>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                insideLabel : Maybe<TextWidgetLabel>,
                insideLabelFormat : TextFormat,
                outsideLabel : Maybe<TextWidgetLabel>,
                outsideLabelFormat : TextFormat,
                valueFormat : TextFormat,
                descriptionStyle : TextStyle)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               maybeLiftPrim(insideLabel),
               Comp(insideLabelFormat),
               maybeLiftPrim(outsideLabel),
               Comp(outsideLabelFormat),
               Comp(valueFormat),
               Comp(descriptionStyle))


    companion object : Factory<TextWidgetFormat>
    {

        private val defaultWidgetFormat       = WidgetFormat.default()
        private val defaultInsideLabel        = Nothing<TextWidgetLabel>()
        private val defaultInsideLabelFormat  = TextFormat.default
        private val defaultOutsideLabel       = Nothing<TextWidgetLabel>()
        private val defaultOutsideLabelFormat = TextFormat.default
        private val defaultValueFormat        = TextFormat.default
        private val defaultDescriptionStyle   = TextStyle.default


        override fun fromDocument(doc : SpecDoc) : ValueParser<TextWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextWidgetFormat,
                         // Widget Format
                         split(doc.maybeAt("widget_format"),
                               effValue(defaultWidgetFormat),
                               { WidgetFormat.fromDocument(it) }),
                         // Inside Label
                         split(doc.maybeAt("inside_label"),
                               effValue<ValueError,Maybe<TextWidgetLabel>>(defaultInsideLabel),
                               { effApply(::Just, TextWidgetLabel.fromDocument(it)) }),
                         // Inside Label Format
                         split(doc.maybeAt("inside_label_format"),
                               effValue(defaultInsideLabelFormat),
                               { TextFormat.fromDocument(it) }),
                         // Outside Label
                         split(doc.maybeAt("outside_label"),
                               effValue<ValueError,Maybe<TextWidgetLabel>>(defaultOutsideLabel),
                               { effApply(::Just, TextWidgetLabel.fromDocument(it)) }),
                         // Outside Label Format
                         split(doc.maybeAt("outside_label_format"),
                               effValue(defaultOutsideLabelFormat),
                               { TextFormat.fromDocument(it) }),
                         // Value Format
                         split(doc.maybeAt("value_format"),
                               effValue(defaultValueFormat),
                               { TextFormat.fromDocument(it) }),
                         // Description Style
                         split(doc.maybeAt("description_style"),
                               effValue(defaultDescriptionStyle),
                               { TextStyle.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : TextWidgetFormat =
                TextWidgetFormat(defaultWidgetFormat,
                                 defaultInsideLabel,
                                 defaultInsideLabelFormat,
                                 defaultOutsideLabel,
                                 defaultOutsideLabelFormat,
                                 defaultValueFormat,
                                 defaultDescriptionStyle)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}


/**
 * Text Widget Description
 */
data class TextWidgetDescription(val value : String)
{

    companion object : Factory<TextWidgetDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TextWidgetDescription> = when (doc)
        {
            is DocText -> effValue(TextWidgetDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Text Widget Description
 */
data class TextWidgetLabel(val value : String)
{

    companion object : Factory<TextWidgetLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TextWidgetLabel> = when (doc)
        {
            is DocText -> effValue(TextWidgetLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
//        SheetActivityOld sheetActivity = (SheetActivityOld) context;
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
//                String         valueSetId   = this.valueVariable().valueSetId();
//                DataReference valueReference = this.valueVariable().valueReference();
//
//                ValueSetUnion valueSetUnion = dictionary.lookup(valueSetId);
//                ValueUnion valueUnion    = dictionary.valueUnion(valueReference);
//
//                if (valueSetUnion == null) {
//                    ApplicationFailure.sheet(
//                            SheetException.undefinedValueSet(
//                                    new UndefinedValueSetError("Text Widget", valueSetId)));
//                    break;
//                }
//
//                if (valueUnion == null) {
//                    ApplicationFailure.value(
//                            ValueException.undefinedValue(
//                                    new UndefinedValueError(valueSetId,
//                                                            valueReference.valueId())));
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

