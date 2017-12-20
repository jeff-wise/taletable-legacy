
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable


/**
 * Option View Type
 */
sealed class OptionViewType : ToDocument, SQLSerializable, Serializable
{

    object NoArrows : OptionViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"no_arrows"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("no_arrows")

    }


    object VerticalArrows : OptionViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"vertical_arrows"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("vertical_arrows")

    }


    object HorizontalArrows : OptionViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"horizontal_arrows"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("horizontal_arrows")

    }


    object ExpandedSlashes : OptionViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"expanded_slashes"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("expanded_slashes")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<OptionViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "no_arrows"         -> effValue<ValueError,OptionViewType>(
                                            OptionViewType.NoArrows)
                "arrows_vertical"   -> effValue<ValueError,OptionViewType>(
                                            OptionViewType.VerticalArrows)
                "arrows_horizontal" -> effValue<ValueError,OptionViewType>(
                                            OptionViewType.HorizontalArrows)
                "expanded_slashes"  -> effValue<ValueError,OptionViewType>(
                                            OptionViewType.ExpandedSlashes)
                else                -> effError<ValueError,OptionViewType>(
                                            UnexpectedValue("OptionViewType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Option Widget Format
 */
//data class OptionWidgetFormat(override val id : UUID,
//                              val widgetFormat : Prod<WidgetFormat>,
//                              val descriptionStyle : Prod<TextFormat>,
//                              val valueStyle : Prod<TextFormat>,
//                              val valueItemStyle : Prod<TextFormat>,
//                              val height : Prim<Height>) : ToDocument, ProdType
//{
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.widgetFormat.name      = "widget_format"
//        this.descriptionStyle.name  = "description_style"
//        this.valueStyle.name        = "value_style"
//        this.valueItemStyle.name    = "value_item_style"
//        this.height.name            = "height"
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(widgetFormat : WidgetFormat,
//                descriptionStyle : TextFormat,
//                valueStyle : TextFormat,
//                valueItemStyle : TextFormat,
//                height : Height)
//        : this(UUID.randomUUID(),
//               Prod(widgetFormat),
//               Prod(descriptionStyle),
//               Prod(valueStyle),
//               Prod(valueItemStyle),
//               Prim(height))
//
//
//    companion object : Factory<OptionWidgetFormat>
//    {
//
//        private fun defaultWidgetFormat()     = WidgetFormat.default()
//        private fun defaultDescriptionStyle() = TextFormat.default()
//        private fun defaultValueStyle()       = TextFormat.default()
//        private fun defaultValueItemStyle()   = TextFormat.default()
//        private fun defaultHeight()           = Height.Wrap
//
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<OptionWidgetFormat> = when (doc)
//        {
//            is DocDict ->
//            {
//                apply(::OptionWidgetFormat,
//                      // Widget Format
//                      split(doc.maybeAt("widget_format"),
//                            effValue(defaultWidgetFormat()),
//                            { WidgetFormat.fromDocument(it) }),
//                      // Description Style
//                      split(doc.maybeAt("description_style"),
//                            effValue(defaultDescriptionStyle()),
//                            { TextFormat.fromDocument(it) }),
//                      // Value Style
//                      split(doc.maybeAt("value_style"),
//                            effValue(defaultValueStyle()),
//                            { TextFormat.fromDocument(it) }),
//                      // Value Item Style
//                      split(doc.maybeAt("value_item_style"),
//                            effValue(defaultValueItemStyle()),
//                            { TextFormat.fromDocument(it) }),
//                      // Height
//                      split(doc.maybeAt("height"),
//                            effValue<ValueError,Height>(defaultHeight()),
//                            { Height.fromDocument(it) })
//                      )
//            }
//            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
//        }
//
//
//        fun default() = OptionWidgetFormat(defaultWidgetFormat(),
//                                           defaultDescriptionStyle(),
//                                           defaultValueStyle(),
//                                           defaultValueItemStyle(),
//                                           defaultHeight())
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocDict(mapOf(
//        "widget_format" to this.widgetFormat().toDocument(),
//        "description_style" to this.descriptionStyle().toDocument(),
//        "value_style" to this.valueStyle().toDocument(),
//        "value_item_style" to this.valueItemStyle().toDocument(),
//        "height" to this.height().toDocument()
//    ))
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun widgetFormat() : WidgetFormat = this.widgetFormat.value
//
//    fun descriptionStyle() : TextFormat = this.descriptionStyle.value
//
//    fun valueStyle() : TextFormat = this.valueStyle.value
//
//    fun valueItemStyle() : TextFormat = this.valueItemStyle.value
//
//    fun height() : Height = this.height.value
//
//
//    // -----------------------------------------------------------------------------------------
//    // MODEL
//    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//    override val name : String = "option_widget_format"
//
//    override val prodTypeObject = this
//
//    override fun persistentFunctors() : List<Val<*>> =
//            listOf(this.widgetFormat,
//                   this.descriptionStyle,
//                   this.valueStyle,
//                   this.valueItemStyle,
//                   this.height)
//
//}


/**
 * Option Description
 */
data class OptionDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<OptionDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<OptionDescription> = when (doc)
        {
            is DocText -> effValue(OptionDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


//
//
//
//    // > Value String
//    // -----------------------------------------------------------------------------------------
//
//    private String valueString()
//    {
//        switch (this.valueType())
//        {
//            case TEXT:
//                try {
//                    return this.textVariable().value();
//                }
//                catch (NullVariableException exception) {
//                    ApplicationFailure.nullVariable(exception);
//                }
//            case NUMBER:
//                try {
//                    return this.numberVariable().valueString();
//                }
//                catch (NullVariableException exception) {
//                    ApplicationFailure.nullVariable(exception);
//                }
//            default:
//                return "N/A";
//        }
//    }
//
//
//    // > Value Reference
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The value reference of the option widget.
//     * @return The value reference.
//     */
//    private DataReference valueReference()
//    {
//        switch (this.valueType())
//        {
//            case TEXT:
//                return this.textVariable().valueReference();
//            case NUMBER:
//                return this.numberVariable().valueReference();
//            default:
//                ApplicationFailure.union(
//                        UnionException.unknownVariant(
//                                new UnknownVariantError(ValueType.class.getName())));
//                return null;
//        }
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Value Set
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * Get the value set that the option widget represents.
//     * @return The Value Set.
//     */
//    public ValueSet valueSet()
//    {
//        if (this.valueSet != null)
//            return this.valueSet;
//
//        Dictionary dictionary = SheetManagerOld.dictionary();
//
//        if (this.valueReference() != null && dictionary != null)
//        {
//            ValueSetUnion valueSetUnion = dictionary.lookup(this.valueReference().valueSetId());
//            if (valueSetUnion != null)
//            {
//                this.valueSet = valueSetUnion.valueSet();
//                return this.valueSet;
//            }
//        }
//
//        return null;
//    }
//
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeOptionWidget()
//    {
//        // [1] Configure default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Alignment
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.DARK);
//
//        // ** Corners
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.SMALL);
//    }
//
//
//    // > Values
//    // -----------------------------------------------------------------------------------------
//
//    // TODO add null value exception
//    private List<String> valueStrings()
//    {
//        List<String> valueStrings = new ArrayList<>();
//
//        ValueSet valueSet = this.valueSet();
//
//        if (valueSet != null)
//        {
//            for (ValueUnion valueUnion : valueSet().values()) {
//                valueStrings.add(valueUnion.value().valueString());
//            }
//        }
//
//        return valueStrings;
//    }
//
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private View widgetView(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = this.layout(rowHasLabel, context);
//
//        switch (this.viewType())
//        {
//            case ARROWS_VERTICAL:
//                layout.addView(verticalArrowsView(context));
//                break;
//            case EXPANDED_SLASHES:
//                layout.addView(expandedSlashesView(context));
//                break;
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout verticalArrowsView(Context context)
//    {
//        LinearLayout layout = verticalArrowsViewLayout(context);
//
//        // > Description
//        layout.addView(verticalArrowsDescriptionView(context));
//
//        // > Value
//        layout.addView(verticalArrowsValueView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout verticalArrowsViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = this.data().format().alignment().gravityConstant()
//                                        | Gravity.CENTER_VERTICAL;
//
//        if (this.data().format().background() != BackgroundColor.EMPTY) {
//            layout.backgroundResource   = this.data().format().corners().resourceId();
//            layout.backgroundColor      = this.data().format().background().colorId();
//        }
//
//        return layout.linearLayout(context);
//    }
//
//
//    private TextView verticalArrowsDescriptionView(Context context)
//    {
//        TextViewBuilder description = new TextViewBuilder();
//
//        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
//        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        description.text            = this.description();
//        description.color           = this.format().descriptionStyle().color().resourceId();
//        description.size            = this.format().descriptionStyle().size().resourceId();
//        description.font            = this.format().descriptionStyle().typeface(context);
//
//        return description.textView(context);
//    }
//
//
//    private LinearLayout verticalArrowsValueView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
//        ImageViewBuilder    chevronUp   = new ImageViewBuilder();
//        ImageViewBuilder    chevronDown = new ImageViewBuilder();
//        TextViewBuilder     value       = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation              = LinearLayout.VERTICAL;
//        layout.width                    = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity                  = Gravity.CENTER_HORIZONTAL;
//
//        layout.margin.top               = R.dimen.half_dp;
//
//        layout.child(chevronUp)
//              .child(value)
//              .child(chevronDown);
//
//        // [3 A] Chevron Up
//        // -------------------------------------------------------------------------------------
//
//        chevronUp.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
//        chevronUp.height        = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        chevronUp.image         = R.drawable.ic_option_chevron_up;
//
//        chevronUp.margin.bottom = R.dimen.negative_four_dp;
//
//        // [3 A] Chevron Down
//        // -------------------------------------------------------------------------------------
//
//        chevronDown.width       = LinearLayout.LayoutParams.WRAP_CONTENT;
//        chevronDown.height      = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        chevronDown.image       = R.drawable.ic_option_chevron_down;
//
//        chevronDown.margin.top    = R.dimen.negative_four_dp;
//
//        // [4] Value
//        // -------------------------------------------------------------------------------------
//
//        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        value.text                  = this.valueString();
//        value.font                  = this.format().valueStyle().typeface(context);
//        value.color                 = this.format().valueStyle().color().resourceId();
//        value.size                  = this.format().valueStyle().size().resourceId();
//
//        value.gravity               = Gravity.CENTER_HORIZONTAL;
//
//        value.backgroundColor       = this.format().valueStyle().backgroundColor().colorId();
//        value.backgroundResource    = R.drawable.bg_option_value;
//
//        value.margin.left           = R.dimen.six_dp;
//
//        // > Set value width by longest value string
//        value.maxEms                = this.valueSet().lengthOfLongestValueString() - 2;
//        value.minEms                = this.valueSet().lengthOfLongestValueString() - 2;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private TextView expandedSlashesView(Context context)
//    {
//        TextViewBuilder value = new TextViewBuilder();
//
//        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        value.gravity       = this.data().format().alignment().gravityConstant()
//                                | Gravity.CENTER_VERTICAL;
//
//        value.layoutGravity = this.data().format().alignment().gravityConstant()
//                                | Gravity.CENTER_VERTICAL;
//
//
//        value.textSpan      = expandedSlashesSpannable(context);
//
//        this.format().descriptionStyle().styleTextViewBuilder(value, context);
//
//        if (this.data().format().background() != BackgroundColor.EMPTY) {
//            value.backgroundResource   = this.data().format().corners().resourceId();
//            value.backgroundColor      = this.data().format().background().colorId();
//        }
//
//        if (this.format().height() == Height.WRAP) {
//            value.padding.topDp    = this.format().verticalPadding().floatValue();
//            value.padding.bottomDp = this.format().verticalPadding().floatValue();
//        }
//
//        return value.textView(context);
//    }
//
//
//    private SpannableStringBuilder expandedSlashesSpannable(Context context)
//    {
//        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(this.description());
//
//        StringBuilder stringBuilder = new StringBuilder(this.description());
//
//        String placeholderString = context.getString(R.string.placeholder_value);
//        int placeholderIndex = this.description().indexOf(placeholderString);
//        int placeholderLength = placeholderString.length();
//
//        // IF the placeholder EXISTS in the text...
//        if (placeholderIndex >= 0)
//        {
//            String valueListString = valueListBySlashes();
//
//            // (1 A) Remove the placeholder string
//            spanBuilder.delete(placeholderIndex, placeholderIndex + placeholderLength);
//            stringBuilder.replace(placeholderIndex, placeholderIndex + placeholderLength, "");
//
//            // (1 B) Insert the text
//            spanBuilder.insert(placeholderIndex, valueListString);
//            stringBuilder.insert(placeholderIndex, valueListString);
//
//            // > Format value list item strings
//            for (String valueItemString : this.valueStrings())
//            {
//                int valueItemIndex = placeholderIndex +
//                                     this.valueStringListIndexMap.get(valueItemString);
//
//                if (valueItemIndex >= 0)
//                {
//                    FormattedString.formatSpan(
//                                        spanBuilder,
//                                        valueItemIndex,
//                                        valueItemString.length(),
//                                        this.format().valueItemStyle().color().color(context),
//                                        this.format().valueItemStyle().size().size(),
//                                        this.format().valueItemStyle().font());
//                }
//
//            }
//
//            // > Format value
//            String valueString = this.valueString();
//            int valueIndex = placeholderIndex + this.valueStringListIndexMap.get(valueString);
//
//            if (valueIndex >= 0)
//            {
//                FormattedString.formatSpan(spanBuilder,
//                                           valueIndex,
//                                           valueIndex + valueString.length() - 4,
//                                           this.format().valueStyle().color().color(context),
//                                           this.format().valueItemStyle().size().size(),
//                                           this.format().valueItemStyle().font());
//            }
//
//        }
//
//        return spanBuilder;
//    }
//
//
//    private String valueListBySlashes()
//    {
//        StringBuilder valuesSB = new StringBuilder();
//
//        if (this.valueSet() == null)
//            return "";
//
//        Collection<ValueUnion> values = valueSet().values();
//
//        this.valueStringListIndexMap = new HashMap<>();
//
//        int currentIndex = 0;
//        String sep = "";
//        for (ValueUnion valueUnion : values)
//        {
//            String valueString = valueUnion.value().valueString();
//
//            valuesSB.append(sep);
//
//            valuesSB.append(valueString);
//
//            this.valueStringListIndexMap.put(valueString, currentIndex);
//
//            sep = " / ";
//
//            currentIndex += valueString.length();
//            currentIndex += 3;
//        }
//
//        return valuesSB.toString().trim();
//    }


