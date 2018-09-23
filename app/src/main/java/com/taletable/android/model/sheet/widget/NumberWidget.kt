
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import java.io.Serializable
import java.util.*



/**
 * Number Widget Format
 */
data class NumberWidgetFormat(val widgetFormat : WidgetFormat,
                              val insideLabelFormat : TextFormat,
                              val outsideLabelFormat : TextFormat,
                              val valueFormat : TextFormat,
                              val numberFormat : NumberFormat)
                               : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberWidgetFormat>
    {

        private fun defaultWidgetFormat()         = WidgetFormat.default()
        private fun defaultInsideLabelFormat()    = TextFormat.default()
        private fun defaultOutsideLabelFormat()   = TextFormat.default()
        private fun defaultValueFormat()          = TextFormat.default()
        private fun defaultNumberFormat()         = NumberFormat.Normal


        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Inside Label Format
                      split(doc.maybeAt("inside_label_format"),
                            effValue(defaultInsideLabelFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Outside Label Format
                      split(doc.maybeAt("outside_label_format"),
                            effValue(defaultOutsideLabelFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Value Format
                      split(doc.maybeAt("value_format"),
                            effValue(defaultValueFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Number Format
                      split(doc.maybeAt("number_format"),
                            effValue<ValueError,NumberFormat>(defaultNumberFormat()),
                            { NumberFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = NumberWidgetFormat(defaultWidgetFormat(),
                                           defaultInsideLabelFormat(),
                                           defaultOutsideLabelFormat(),
                                           defaultValueFormat(),
                                           defaultNumberFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "inside_label_format" to this.insideLabelFormat.toDocument(),
        "outside_label_format" to this.outsideLabelFormat.toDocument(),
        "value_format" to this.valueFormat.toDocument(),
        "number_format" to this.numberFormat.toDocument()))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun insideLabelFormat() : TextFormat = this.insideLabelFormat


    fun outsideLabelFormat() : TextFormat = this.outsideLabelFormat


    fun valueFormat() : TextFormat = this.valueFormat


    fun numberFormat() : NumberFormat = this.numberFormat


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
             entityId : EntityId,
             context : Context) : View
    {
        val layout = WidgetView.layout(numberWidget.format().widgetFormat(), entityId, context)

        val layoutId = Util.generateViewId()
        numberWidget.layoutId = layoutId
        layout.id  = layoutId


        layout.setOnClickListener {
            numberWidget.primaryAction(entityId, context)
        }


        layout.setOnLongClickListener {
            numberWidget.secondaryAction(entityId, context)
            true
        }

        this.updateView(numberWidget, entityId, layout, context)

        return layout
    }


    fun updateView(numberWidget : NumberWidget,
                   entityId : EntityId,
                   layout : LinearLayout,
                   context : Context)
    {

        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
        contentLayout.removeAllViews()
        contentLayout.addView(this.mainView(numberWidget, entityId, context))
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
                         entityId : EntityId,
                         context : Context) : LinearLayout
    {

        val format = numberWidget.format()

        val layout = this.mainLayout(format, context)

        // > Outside Top/Left Label View
//        if (format.outsideLabelString() != null) {
//            if (format.outsideLabelFormat().position().isTop() ||
//                format.outsideLabelFormat().position().isLeft()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

        // > Value
        layout.addView(this.valueMainView(numberWidget, format, entityId, context))

        // > Outside Bottom/Right Label View
//        if (format.outsideLabelString() != null) {
//            if (format.outsideLabelFormat().position().isBottom() ||
//                format.outsideLabelFormat().position().isRight()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

        return layout
    }


    private fun mainLayout(format : NumberWidgetFormat, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = format.outsideLabelFormat().elementFormat().position()
                                            .linearLayoutOrientation()

//        layout.gravity              = format.widgetFormat().elementFormat().alignment().gravityConstant() or
//                                        Gravity.CENTER_VERTICAL

//        layout.padding.leftDp       = format.widgetFormat().padding().leftDp()
//        layout.padding.rightDp      = format.widgetFormat().padding().rightDp()

        return layout.linearLayout(context)
    }


    /**
     * The view that holds the value as well as the inside labels around the value.
     */
    private fun valueMainView(numberWidget : NumberWidget,
                              format : NumberWidgetFormat,
                              entityId : EntityId,
                              context : Context) : LinearLayout
    {
        val layout = this.valueMainViewLayout(format, entityId, context)

        val insideLabel = numberWidget.insideLabel()

        // > Inside Top/Left Label View
        when (insideLabel) {
            is Just -> {
                val position = format.insideLabelFormat().elementFormat().position()
                if (position.isTop() || position.isLeft()) {
                    layout.addView(this.insideLabelView(format, insideLabel.value.value, entityId, context))
                }
            }
        }

        layout.addView(this.valueView(numberWidget, format, entityId, context))

        // > Inside Bottom/Right Label View
        when (insideLabel) {
            is Just -> {
                val position = format.insideLabelFormat().elementFormat().position()
                if (position.isBottom() || position.isRight()) {
                    layout.addView(this.insideLabelView(format, insideLabel.value.value, entityId, context))
                }
            }
        }

        return layout
    }


    private fun valueMainViewLayout(format : NumberWidgetFormat,
                                    entityId : EntityId,
                                    context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = format.insideLabelFormat().elementFormat().position()
                                            .linearLayoutOrientation()

        // > Width
        //   If no padding is specified, the value (and its background) stretches to fill the
        //   space. Otherwise it only stretches as far as the padding allows
        // -------------------------------------------------------------------------------------
//        if (this.format().valuePaddingHorizontal() != null ||
//            this.data().format().background() == BackgroundColor.EMPTY) {

        //layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        val height = format.widgetFormat().elementFormat().height()
//        when (height)
//        {
//            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
//            is Height.Fixed -> layout.heightDp = height.value.toInt()
//        }



//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }
//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {

        layout.backgroundColor      = colorOrBlack(
                                          format.valueFormat().elementFormat().backgroundColorTheme(), entityId)

//        layout.backgroundResource   = format.valueFormat().height()
//                                            .resourceId(format.widgetFormat().corners())

        layout.corners              = format.valueFormat().elementFormat().corners()

        layout.paddingSpacing       = format.valueFormat().elementFormat().padding()


//        if (format.valueFormat().elementFormat().height().isWrap())
//        {
//            layout.padding.topDp    = format.valueFormat().elementFormat().padding().topDp()
//            layout.padding.bottomDp = format.valueFormat().elementFormat().padding().bottomDp()
//        }



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

        return layout.linearLayout(context)
    }



    private fun valueView(numberWidget : NumberWidget,
                          format : NumberWidgetFormat,
                          entityId : EntityId,
                          context : Context) : LinearLayout
    {
        val layout = this.valueViewLayout(format, context)

        // > Prefix
//        val prefixString = format.valuePrefixString()
//        if (prefixString != null)
//            layout.addView(this.valueFixView(prefixString,
//                                             format.valuePrefixStyle(),
//                                             format.valueFormat(),
//                    sheetUIContext))

        // > Value
        layout.addView(this.valueTextView(numberWidget, format, entityId, context))

        // > Base Value
//        if (this.baseValueVariableName() != null)
//            layout.addView(baseValueView(context));

        // > Postfix
//        val postfixString = format.valuePostfixString()
//        if (postfixString != null)
//            layout.addView(this.valueFixView(postfixString,
//                                             format.valuePostfixStyle(),
//                                             format.valueFormat(),
//                    sheetUIContext))

        return layout
    }


    private fun valueViewLayout(format : NumberWidgetFormat, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT

        if (format.widgetFormat().elementFormat().height().isWrap())
            layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
        else
            layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.paddingSpacing   = format.valueFormat().elementFormat().padding()

        layout.gravity = format.valueFormat().elementFormat().alignment().gravityConstant() or
                                    format.valueFormat().elementFormat().verticalAlignment().gravityConstant()

        layout.layoutGravity = format.valueFormat().elementFormat().alignment().gravityConstant() or
                format.valueFormat().elementFormat().verticalAlignment().gravityConstant()

        return layout.linearLayout(context)
    }


    private fun valueTextView(numberWidget : NumberWidget,
                              format : NumberWidgetFormat,
                              entityId : EntityId,
                              context : Context) : TextView
    {
        val value = TextViewBuilder()

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        value.gravity       = format.valueFormat().elementFormat().alignment().gravityConstant() or
                                format.valueFormat().elementFormat().verticalAlignment().gravityConstant()

        value.layoutGravity       = format.valueFormat().elementFormat().alignment().gravityConstant() or
                format.valueFormat().elementFormat().verticalAlignment().gravityConstant()

//        if (numberWidget.description() != null)
//        {
//            value.layoutGravity = format.descriptionStyle().alignment().gravityConstant() or
//                                    Gravity.CENTER_VERTICAL
//            value.gravity       = format.descriptionStyle().alignment().gravityConstant()
//
//            format.descriptionStyle().styleTextViewBuilder(value, sheetUIContext)
//
//            val spans = mutableListOf<FormattedString.Span>()
//
//            val labelSpan =
//                FormattedString.Span(
//                        format.insideLabelString(),
//                        SheetManager.color(sheetUIContext.sheetId,
//                                           format.insideLabelFormat().style().colorTheme()),
//                        format.insideLabelFormat().style().sizeSp(),
//                        format.insideLabelFormat().style().font())
//
//            val valueSpan =
//                FormattedString.Span(numberWidget.valueString(sheetContext),
//                                     sheetUIContext.context.getString(R.string.placeholder_value),
//                                     SheetManager.color(sheetUIContext.sheetId,
//                                                        format.valueFormat().style().colorTheme()),
//                                     format.valueFormat().style().sizeSp(),
//                                     format.valueFormat().style().font())
//
//
//            if (format.insideLabelString() != null)
//                spans.add(labelSpan)
//
//            spans.add(valueSpan)
//
//            value.textSpan  = FormattedString.spannableStringBuilder(numberWidget.description(),
//                                                                     spans)
//        }

//        var valueString = ""
//        valueString = numberWidget.valueString(sheetContext)

        val valueDouble = numberWidget.value(entityId)
        val valueString = format.valueFormat().numberFormat().formattedString(valueDouble)

        value.text = valueString

        format.valueFormat().styleTextViewBuilder(value, entityId, context)

        return value.textView(context)
    }

//
//    private fun valueFixView(fixString : String,
//                             style : TextFormat,
//                             valueFormat : TextFormat,
//                             sheetUIContext: SheetUIContext) : TextView
//    {
//        val prefix              = TextViewBuilder()
//
//        prefix.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        prefix.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        prefix.layoutGravity    = valueFormat.elementFormat().alignment().gravityConstant() or
//                                        Gravity.CENTER_VERTICAL
//        prefix.gravity          = valueFormat.elementFormat().alignment().gravityConstant()
//
//        prefix.text             = fixString
//
//        style.styleTextViewBuilder(prefix, sheetUIContext)
//
//        return prefix.textView(sheetUIContext.context)
//    }
//
//
//    private fun baseValueView(format : NumberWidgetFormat, sheetUIContext: SheetUIContext) : LinearLayout
//    {
//        val layout = this.baseValueViewLayout(sheetUIContext.context)
//
//        // > Separator
//        layout.addView(baseValueSeparatorView(format, sheetUIContext))
//
//        // > Value
//        layout.addView(baseValueTextView(sheetUIContext.context))
//
//        return layout;
//    }


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


//    private fun baseValueSeparatorView(format : NumberWidgetFormat,
//                                       sheetUIContext: SheetUIContext) : TextView
//    {
//        val separator = TextViewBuilder()
//
//        separator.width         = LinearLayout.LayoutParams.WRAP_CONTENT
//        separator.height        = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    //    separator.text          = format.valueSeparatorString()
//
////        format.valueSeparatorFormat()?.style()?.styleTextViewBuilder(separator, sheetUIContext)
////
////        separator.marginSpacing = format.valueSeparatorFormat()?.margins()
//
//        return separator.textView(sheetUIContext.context)
//    }
//
//
//    private fun baseValueTextView(context : Context) : TextView
//    {
//        val value = TextViewBuilder()
//
//        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
//        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        // value.text          = this.baseValue().toString()
//
//        // this.format().baseValueStyle().styleTextViewBuilder(value, context)
//
//        // value.marginSpacing = format.baseValueMargins()
//
//        return value.textView(context)
//    }
//
//
//    private fun outsideLabelView(format : NumberWidgetFormat,
//                                 sheetUIContext: SheetUIContext) : TextView
//    {
//        val label = TextViewBuilder()
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.layoutGravity     = format.outsideLabelFormat().elementFormat().alignment().gravityConstant() or
//                                    Gravity.CENTER_VERTICAL
//
//        //label.text              = format.outsideLabelString()
//
//        format.outsideLabelFormat().styleTextViewBuilder(label, sheetUIContext)
//
//        label.marginSpacing     = format.outsideLabelFormat().elementFormat().margins()
//
//        return label.textView(sheetUIContext.context)
//    }


    private fun insideLabelView(format : NumberWidgetFormat,
                                labelString : String,
                                entityId : EntityId,
                                context : Context) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = labelString

        label.layoutGravity     = format.insideLabelFormat().elementFormat().alignment().gravityConstant() or
                                      Gravity.CENTER_VERTICAL;

        format.insideLabelFormat().styleTextViewBuilder(label, entityId, context)

        label.marginSpacing     = format.insideLabelFormat().elementFormat().margins()

        return label.textView(context)
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
//                SummationDialog summationDialog =
//                                    SummationDialog.newInstance(summation, summationLabel);
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
