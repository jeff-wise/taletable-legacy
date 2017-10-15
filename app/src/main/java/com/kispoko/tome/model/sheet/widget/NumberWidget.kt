
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.FormattedString
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.util.Util
import effect.*
import effect.Nothing
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Number Widget Format
 */
data class NumberWidgetFormat(override val id : UUID,
                              val widgetFormat : Comp<WidgetFormat>,
                              val height : Prim<Height>,
                              val insideLabel : Maybe<Prim<NumberWidgetLabel>>,
                              val insideLabelFormat : Comp<TextFormat>,
                              val outsideLabel : Maybe<Prim<NumberWidgetLabel>>,
                              val outsideLabelFormat : Comp<TextFormat>,
                              val valueFormat : Comp<TextFormat>,
                              val descriptionStyle : Comp<TextStyle>,
                              val valuePrefix : Maybe<Prim<NumberWidgetValuePrefix>>,
                              val valuePrefixStyle : Comp<TextStyle>,
                              val valuePostfix : Maybe<Prim<NumberWidgetValuePostfix>>,
                              val valuePostfixStyle : Comp<TextStyle>,
                              val valueSeparator : Maybe<Prim<ValueSeparator>>,
                              val valueSeparatorFormat : Comp<TextFormat>)
                               : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name                          = "widget_format"

        this.height.name                                = "height"

        when (this.insideLabel) {
            is Just -> this.insideLabel.value.name      = "inside_label"
        }

        this.insideLabelFormat.name                     = "inside_label_format"

        when (this.outsideLabel) {
            is Just -> this.outsideLabel.value.name     = "outside_label"
        }

        this.outsideLabelFormat.name                    = "outside_label_format"

        this.valueFormat.name                           = "value_format"

        this.descriptionStyle.name                      = "description_style"

        when (this.valuePrefix) {
            is Just -> this.valuePrefix.value.name      = "value_prefix"
        }

        this.valuePrefixStyle.name                      = "value_prefix_style"

        when (this.valuePostfix) {
            is Just -> this.valuePostfix.value.name     = "value_postfix"
        }

        this.valuePostfixStyle.name                     = "value_postfix_style"

        when (this.valueSeparator) {
            is Just -> this.valueSeparator.value.name   = "value_separator"
        }

        this.valueSeparatorFormat.name                  = "value_separator_format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                height : Height,
                insideLabel : Maybe<NumberWidgetLabel>,
                insideLabelFormat : TextFormat,
                outsideLabel : Maybe<NumberWidgetLabel>,
                outsideLabelFormat : TextFormat,
                valueFormat : TextFormat,
                descriptionStyle : TextStyle,
                valuePrefix : Maybe<NumberWidgetValuePrefix>,
                valuePrefixStyle : TextStyle,
                valuePostfix : Maybe<NumberWidgetValuePostfix>,
                valuePostfixStyle : TextStyle,
                valueSeparator : Maybe<ValueSeparator>,
                valueSeparatorFormat : TextFormat)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Prim(height),
               maybeLiftPrim(insideLabel),
               Comp(insideLabelFormat),
               maybeLiftPrim(outsideLabel),
               Comp(outsideLabelFormat),
               Comp(valueFormat),
               Comp(descriptionStyle),
               maybeLiftPrim(valuePrefix),
               Comp(valuePrefixStyle),
               maybeLiftPrim(valuePostfix),
               Comp(valuePostfixStyle),
               maybeLiftPrim(valueSeparator),
               Comp(valueSeparatorFormat))


    companion object : Factory<NumberWidgetFormat>
    {

        private val defaultHeight               = Height.Wrap
        private val defaultInsideLabel          = Nothing<NumberWidgetLabel>()
        private val defaultInsideLabelFormat    = TextFormat.default()
        private val defaultOutsideLabel         = Nothing<NumberWidgetLabel>()
        private val defaultOutsideLabelFormat   = TextFormat.default()
        private val defaultValueFormat          = TextFormat.default()
        private val defaultDescriptionStyle     = TextStyle.default()
        private val defaultValuePrefixStyle     = TextStyle.default()
        private val defaultValuePostfixStyle    = TextStyle.default()
        private val defaultValueSeparator       = Nothing<ValueSeparator>()
        private val defaultValueSeparatorFormat = TextFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::NumberWidgetFormat,
                         // Widget Format
                         split(doc.maybeAt("widget_format"),
                               effValue(WidgetFormat.default()),
                               { WidgetFormat.fromDocument(it) }),
                         // Height
                         split(doc.maybeAt("height"),
                               effValue<ValueError,Height>(defaultHeight),
                               { Height.fromDocument(it) }),
                         // Inside Label
                         split(doc.maybeAt("inside_label"),
                               effValue<ValueError,Maybe<NumberWidgetLabel>>(defaultInsideLabel),
                               { effApply(::Just, NumberWidgetLabel.fromDocument(it)) }),
                         // Inside Label Format
                         split(doc.maybeAt("inside_label_format"),
                               effValue(defaultInsideLabelFormat),
                               { TextFormat.fromDocument(it) }),
                         // Outside Label
                         split(doc.maybeAt("outside_label"),
                               effValue<ValueError,Maybe<NumberWidgetLabel>>(defaultOutsideLabel),
                               { effApply(::Just, NumberWidgetLabel.fromDocument(it)) }),
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
                               { TextStyle.fromDocument(it) }),
                         // Value Prefix
                         split(doc.maybeAt("value_prefix"),
                               effValue<ValueError,Maybe<NumberWidgetValuePrefix>>(Nothing()),
                               { effApply(::Just, NumberWidgetValuePrefix.fromDocument(it)) }),
                         // Value Prefix Style
                         split(doc.maybeAt("value_prefix_format"),
                               effValue(defaultValuePrefixStyle),
                               { TextStyle.fromDocument(it) }),
                        // Value Postfix
                        split(doc.maybeAt("value_postfix"),
                                effValue<ValueError,Maybe<NumberWidgetValuePostfix>>(Nothing()),
                                { effApply(::Just, NumberWidgetValuePostfix.fromDocument(it)) }),
                         // Value Postfix Style
                         split(doc.maybeAt("value_postfix_format"),
                               effValue(defaultValuePostfixStyle),
                               { TextStyle.fromDocument(it) }),
                         // Value Separator
                         split(doc.maybeAt("value_separator"),
                               effValue<ValueError,Maybe<ValueSeparator>>(defaultValueSeparator),
                               { effApply(::Just, ValueSeparator.fromDocument(it)) }),
                         // Value Separator Format
                         split(doc.maybeAt("value_separator_format"),
                               effValue(defaultValueSeparatorFormat),
                               { TextFormat.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : NumberWidgetFormat =
                NumberWidgetFormat(WidgetFormat.default(),
                                   defaultHeight,
                                   defaultInsideLabel,
                                   defaultInsideLabelFormat,
                                   defaultOutsideLabel,
                                   defaultOutsideLabelFormat,
                                   defaultValueFormat,
                                   defaultDescriptionStyle,
                                   Nothing(),
                                   defaultValuePrefixStyle,
                                   Nothing(),
                                   defaultValuePostfixStyle,
                                   defaultValueSeparator,
                                   defaultValueSeparatorFormat)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "height" to this.height().toDocument(),
        "inside_label_format" to this.insideLabelFormat().toDocument(),
        "outside_label_format" to this.outsideLabelFormat().toDocument(),
        "value_format" to this.valueFormat().toDocument(),
        "description_style" to this.descriptionStyle().toDocument(),
        "value_prefix_style" to this.valuePrefixStyle().toDocument(),
        "value_postfix_style" to this.valuePostfixStyle().toDocument(),
        "value_separator_format" to this.valueSeparatorFormat().toDocument()))
        .maybeMerge(this.insideLabel().apply {
            Just(Pair("inside_label", DocText(it.value) as SchemaDoc)) })
        .maybeMerge(this.outsideLabel().apply {
            Just(Pair("outside_label", DocText(it.value) as SchemaDoc)) })
        .maybeMerge(this.valuePrefix().apply {
            Just(Pair("value_prefix", DocText(it.value) as SchemaDoc)) })
        .maybeMerge(this.valuePostfix().apply {
            Just(Pair("value_postfix", DocText(it.value) as SchemaDoc)) })
        .maybeMerge(this.valueSeparator().apply {
            Just(Pair("value_separator", DocText(it.value) as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun height() : Height = this.height.value

    fun insideLabel() : Maybe<NumberWidgetLabel> = _getMaybePrim(this.insideLabel)

    fun insideLabelString() : String? = getMaybePrim(this.insideLabel)?.value

    fun insideLabelFormat() : TextFormat = this.insideLabelFormat.value

    fun outsideLabel() : Maybe<NumberWidgetLabel> = _getMaybePrim(this.outsideLabel)

    fun outsideLabelString() : String? = getMaybePrim(this.outsideLabel)?.value

    fun outsideLabelFormat() : TextFormat = this.outsideLabelFormat.value

    fun valueFormat() : TextFormat = this.valueFormat.value

    fun descriptionStyle() : TextStyle = this.descriptionStyle.value

    fun valuePrefix() : Maybe<NumberWidgetValuePrefix> = _getMaybePrim(this.valuePrefix)

    fun valuePrefixString() : String? = getMaybePrim(this.valuePrefix)?.value

    fun valuePrefixStyle() : TextStyle = this.valuePrefixStyle.value

    fun valuePostfix() : Maybe<NumberWidgetValuePostfix> = _getMaybePrim(this.valuePostfix)

    fun valuePostfixString() : String? = getMaybePrim(this.valuePostfix)?.value

    fun valuePostfixStyle() : TextStyle = this.valuePostfixStyle.value

    fun valueSeparator() : Maybe<ValueSeparator> = _getMaybePrim(this.valueSeparator)

    fun valueSeparatorString() : String? = getMaybePrim(this.valueSeparator)?.value

    fun valueSeparatorFormat() : TextFormat = this.valueSeparatorFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "number_widget_format"

    override val modelObject = this

}


/**
 * Label
 */
data class NumberWidgetLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberWidgetLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberWidgetLabel> = when (doc)
        {
            is DocText -> effValue(NumberWidgetLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Value Separator
 */
data class ValueSeparator(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSeparator>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSeparator> = when (doc)
        {
            is DocText -> effValue(ValueSeparator(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Description
 */
data class NumberWidgetDescription(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberWidgetDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberWidgetDescription> = when (doc)
        {
            is DocText -> effValue(NumberWidgetDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Value Prefix
 */
data class NumberWidgetValuePrefix(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberWidgetValuePrefix>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberWidgetValuePrefix> = when (doc)
        {
            is DocText -> effValue(NumberWidgetValuePrefix(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Value Postfix
 */
data class NumberWidgetValuePostfix(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberWidgetValuePostfix>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberWidgetValuePostfix> = when (doc)
        {
            is DocText -> effValue(NumberWidgetValuePostfix(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



object NumberWidgetView
{


    fun view(numberWidget : NumberWidget,
             format : NumberWidgetFormat,
             sheetUIContext: SheetUIContext) : View
    {
        val layout = WidgetView.layout(format.widgetFormat(), sheetUIContext)

        layout.addView(this.mainView(numberWidget, format, sheetUIContext))

        return layout
    }


    /**
     * The outermost view that holds the outside labels and the value view.
     *
     *                      top label
     *             --------------------------
     *             |                         |
     *  left label |        Value View       | right label
     *             |                         |
     *             ---------------------------
     *                    bottom label
     *
     */
    private fun mainView(numberWidget : NumberWidget,
                         format : NumberWidgetFormat,
                         sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.mainLayout(format, sheetUIContext.context)

        // > Outside Top/Left Label View
        if (format.outsideLabelString() != null) {
            if (format.outsideLabelFormat().position().isTop() ||
                format.outsideLabelFormat().position().isLeft()) {
                layout.addView(this.outsideLabelView(format, sheetUIContext))
            }
        }

        // > Value
        layout.addView(this.valueMainView(numberWidget, format, sheetUIContext))

        // > Outside Bottom/Right Label View
        if (format.outsideLabelString() != null) {
            if (format.outsideLabelFormat().position().isBottom() ||
                format.outsideLabelFormat().position().isRight()) {
                layout.addView(this.outsideLabelView(format, sheetUIContext))
            }
        }

        return layout
    }


    private fun mainLayout(format : NumberWidgetFormat, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = format.outsideLabelFormat().position()
                                            .linearLayoutOrientation()

        layout.gravity              = format.widgetFormat().alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL

//        layout.padding.leftDp       = format.widgetFormat().padding().leftDp()
//        layout.padding.rightDp      = format.widgetFormat().padding().rightDp()

        return layout.linearLayout(context)
    }


    /**
     * The view that holds the value as well as the inside labels around the value.
     */
    private fun valueMainView(numberWidget : NumberWidget,
                              format : NumberWidgetFormat,
                              sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.valueMainViewLayout(format, sheetUIContext)

        // > Inside Top/Left Label View
        if (format.insideLabelString() != null && numberWidget.description() == null) {
            if (format.insideLabelFormat().position().isTop() ||
                format.insideLabelFormat().position().isLeft()) {
                layout.addView(this.insideLabelView(format, sheetUIContext))
            }
        }

        layout.addView(this.valueView(numberWidget, format, sheetUIContext))

        // > Inside Bottom/Right Label View
        if (format.insideLabelString() != null && numberWidget.description() == null) {
            if (format.insideLabelFormat().position().isBottom() ||
                format.insideLabelFormat().position().isRight()) {
                layout.addView(this.insideLabelView(format, sheetUIContext))
            }
        }

        return layout
    }


    private fun valueMainViewLayout(format : NumberWidgetFormat,
                                    sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = format.insideLabelFormat().position()
                                            .linearLayoutOrientation()

        // > Width
        //   If no padding is specified, the value (and its background) stretches to fill the
        //   space. Otherwise it only stretches as far as the padding allows
        // -------------------------------------------------------------------------------------
//        if (this.format().valuePaddingHorizontal() != null ||
//            this.data().format().background() == BackgroundColor.EMPTY) {

        //layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT

        val height = format.height()
        when (height)
        {
            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
            is Height.Fixed -> layout.heightDp = height.value.toInt()
        }



//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }
//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {

//        layout.backgroundColor      = SheetManager.color(
//                                                sheetUIContext.sheetId,
//                                                format.widgetFormat().backgroundColorTheme())

//        layout.backgroundResource   = format.valueFormat().height()
//                                            .resourceId(format.widgetFormat().corners())

//        layout.corners              = format.widgetFormat().corners()


        if (format.valueFormat().height().isWrap())
        {
            layout.padding.topDp    = format.valueFormat().padding().topDp()
            layout.padding.bottomDp = format.valueFormat().padding().bottomDp()
        }



//        if (format.widgetFormat.background() == BackgroundColor.EMPTY)
//            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT

//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }

//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {

        return layout.linearLayout(sheetUIContext.context)
    }



    private fun valueView(numberWidget : NumberWidget,
                          format : NumberWidgetFormat,
                          sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.valueViewLayout(format, sheetUIContext.context)

        // > Prefix
        val prefixString = format.valuePrefixString()
        if (prefixString != null)
            layout.addView(this.valueFixView(prefixString,
                                             format.valuePrefixStyle(),
                                             format.valueFormat(),
                    sheetUIContext))

        // > Value
        layout.addView(this.valueTextView(numberWidget, format, sheetUIContext))

        // > Base Value
//        if (this.baseValueVariableName() != null)
//            layout.addView(baseValueView(context));

        // > Postfix
        val postfixString = format.valuePostfixString()
        if (postfixString != null)
            layout.addView(this.valueFixView(postfixString,
                                             format.valuePostfixStyle(),
                                             format.valueFormat(),
                    sheetUIContext))

        return layout
    }


    private fun valueViewLayout(format : NumberWidgetFormat, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT

        if (format.height().isWrap())
            layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
        else
            layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.paddingSpacing   = format.valueFormat().padding()

        layout.gravity = format.valueFormat().alignment().gravityConstant() or
                                    format.valueFormat().verticalAlignment().gravityConstant()

        return layout.linearLayout(context)
    }


    private fun valueTextView(numberWidget : NumberWidget,
                              format : NumberWidgetFormat,
                              sheetUIContext: SheetUIContext) : TextView
    {
        val value = TextViewBuilder()

        numberWidget.viewId = Util.generateViewId()
        value.id            = numberWidget.viewId

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        value.gravity       = format.valueFormat().alignment().gravityConstant() or
                                format.valueFormat().verticalAlignment().gravityConstant()

        if (numberWidget.description() != null)
        {
            value.layoutGravity = format.descriptionStyle().alignment().gravityConstant() or
                                    Gravity.CENTER_VERTICAL
            value.gravity       = format.descriptionStyle().alignment().gravityConstant()

            format.descriptionStyle().styleTextViewBuilder(value, sheetUIContext)

            val spans = mutableListOf<FormattedString.Span>()

            val labelSpan =
                FormattedString.Span(
                        format.insideLabelString(),
                        SheetManager.color(sheetUIContext.sheetId,
                                           format.insideLabelFormat().style().colorTheme()),
                        format.insideLabelFormat().style().sizeSp(),
                        format.insideLabelFormat().style().font())

            val valueSpan =
                FormattedString.Span(numberWidget.valueString(SheetContext(sheetUIContext)),
                                     sheetUIContext.context.getString(R.string.placeholder_value),
                                     SheetManager.color(sheetUIContext.sheetId,
                                                        format.valueFormat().style().colorTheme()),
                                     format.valueFormat().style().sizeSp(),
                                     format.valueFormat().style().font())


            if (format.insideLabelString() != null)
                spans.add(labelSpan)

            spans.add(valueSpan)

            value.textSpan  = FormattedString.spannableStringBuilder(numberWidget.description(),
                                                                     spans)
        }
        else
        {
            value.text  = numberWidget.valueString(SheetContext(sheetUIContext))
            format.valueFormat().style().styleTextViewBuilder(value, sheetUIContext)
        }

        return value.textView(sheetUIContext.context)
    }


    private fun valueFixView(fixString : String,
                             style : TextStyle,
                             valueFormat : TextFormat,
                             sheetUIContext: SheetUIContext) : TextView
    {
        val prefix              = TextViewBuilder()

        prefix.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        prefix.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        prefix.layoutGravity    = valueFormat.alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL
        prefix.gravity          = valueFormat.alignment().gravityConstant()

        prefix.text             = fixString

        style.styleTextViewBuilder(prefix, sheetUIContext)

        return prefix.textView(sheetUIContext.context)
    }


    private fun baseValueView(format : NumberWidgetFormat, sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.baseValueViewLayout(sheetUIContext.context)

        // > Separator
        layout.addView(baseValueSeparatorView(format, sheetUIContext))

        // > Value
        layout.addView(baseValueTextView(sheetUIContext.context))

        return layout;
    }


    private fun baseValueViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        // layout.layoutGravity        = format.baseValueVerticalAlignment().gravityConstant()
        layout.gravity              = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    private fun baseValueSeparatorView(format : NumberWidgetFormat,
                                       sheetUIContext: SheetUIContext) : TextView
    {
        val separator = TextViewBuilder()

        separator.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        separator.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        separator.text          = format.valueSeparatorString()

        format.valueSeparatorFormat()?.style()?.styleTextViewBuilder(separator, sheetUIContext)

        separator.marginSpacing = format.valueSeparatorFormat()?.margins()

        return separator.textView(sheetUIContext.context)
    }


    private fun baseValueTextView(context : Context) : TextView
    {
        val value = TextViewBuilder()

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        // value.text          = this.baseValue().toString()

        // this.format().baseValueStyle().styleTextViewBuilder(value, context)

        // value.marginSpacing = format.baseValueMargins()

        return value.textView(context)
    }


    private fun outsideLabelView(format : NumberWidgetFormat,
                                 sheetUIContext: SheetUIContext) : TextView
    {
        val label = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.layoutGravity     = format.outsideLabelFormat().alignment().gravityConstant() or
                                    Gravity.CENTER_VERTICAL

        label.text              = format.outsideLabelString()

        format.outsideLabelFormat().style().styleTextViewBuilder(label, sheetUIContext)

        label.marginSpacing     = format.outsideLabelFormat().margins()

        return label.textView(sheetUIContext.context)
    }


    private fun insideLabelView(format : NumberWidgetFormat,
                                sheetUIContext: SheetUIContext) : TextView
    {
        val label   = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = format.insideLabelString()

        label.layoutGravity     = format.insideLabelFormat().alignment().gravityConstant() or
                                      Gravity.CENTER_VERTICAL;

        format.insideLabelFormat().style().styleTextViewBuilder(label, sheetUIContext)

        label.marginSpacing     = format.insideLabelFormat().margins()

        return label.textView(sheetUIContext.context)
    }


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

