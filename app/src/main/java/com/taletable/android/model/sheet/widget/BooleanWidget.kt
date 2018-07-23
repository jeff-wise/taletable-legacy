
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.entity.BooleanWidgetUpdateSetValue
import com.taletable.android.model.entity.BooleanWidgetUpdateToggle
import com.taletable.android.model.sheet.style.Icon
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.MessageSheetUpdate
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Boolean Widget Format
 */
data class BooleanWidgetFormat(val widgetFormat : WidgetFormat,
                               val viewType : BooleanWidgetViewType,
                               val trueFormat : TextFormat,
                               val falseFormat : TextFormat,
                               val leftToggleFormat : TextFormat,
                               val rightToggleFormat : TextFormat,
                               val trueText : TrueText,
                               val falseText : FalseText,
                               val trueIcon : Maybe<Icon>,
                               val falseIcon : Maybe<Icon>)
                                : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanWidgetFormat>
    {

        private fun defaultWidgetFormat()           = WidgetFormat.default()
        private fun defaultWidgetViewType()         = BooleanWidgetViewType.SimpleText
        private fun defaultTrueFormat()             = TextFormat.default()
        private fun defaultFalseFormat()            = TextFormat.default()
        private fun defaultLeftToggleFormat()       = TextFormat.default()
        private fun defaultRightToggleFormat()      = TextFormat.default()
        private fun defaultTrueText()               = TrueText("")
        private fun defaultFalseText()              = FalseText("")


        override fun fromDocument(doc : SchemaDoc) : ValueParser<BooleanWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::BooleanWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Boolean Widget View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,BooleanWidgetViewType>(defaultWidgetViewType()),
                            { BooleanWidgetViewType.fromDocument(it) }),
                      // True Format
                      split(doc.maybeAt("true_format"),
                            effValue(defaultTrueFormat()),
                            { TextFormat.fromDocument(it) }),
                      // False Format
                      split(doc.maybeAt("false_format"),
                            effValue(defaultFalseFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Left Toggle Format
                      split(doc.maybeAt("left_toggle_format"),
                            effValue(defaultRightToggleFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Right Toggle Format
                      split(doc.maybeAt("right_toggle_format"),
                            effValue(defaultRightToggleFormat()),
                            { TextFormat.fromDocument(it) }),
                      // True Text
                      split(doc.maybeAt("true_text"),
                            effValue(defaultTrueText()),
                            { TrueText.fromDocument(it) }),
                      // False Text
                      split(doc.maybeAt("false_text"),
                            effValue(defaultFalseText()),
                            { FalseText.fromDocument(it) }),
                      // True Icon
                      split(doc.maybeAt("true_icon"),
                            effValue<ValueError,Maybe<Icon>>(Nothing()),
                            { apply(::Just, Icon.fromDocument(it)) }),
                      // False Icon
                      split(doc.maybeAt("false_icon"),
                            effValue<ValueError,Maybe<Icon>>(Nothing()),
                            { apply(::Just, Icon.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = BooleanWidgetFormat(defaultWidgetFormat(),
                                            defaultWidgetViewType(),
                                            defaultTrueFormat(),
                                            defaultFalseFormat(),
                                            defaultLeftToggleFormat(),
                                            defaultRightToggleFormat(),
                                            defaultTrueText(),
                                            defaultFalseText(),
                                            Nothing(),
                                            Nothing())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "true_format" to this.trueFormat().toDocument(),
        "false_format" to this.falseFormat().toDocument(),
        "true_text" to this.trueText().toDocument(),
        "false_text" to this.falseText().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : BooleanWidgetViewType = this.viewType


    fun trueFormat() : TextFormat = this.trueFormat


    fun falseFormat() : TextFormat = this.falseFormat


    fun leftToggleFormat() : TextFormat = this.leftToggleFormat


    fun rightToggleFormat() : TextFormat = this.rightToggleFormat


    fun trueText() : TrueText = this.trueText


    fun falseText() : FalseText = this.falseText


    fun trueIcon() : Maybe<Icon> = this.trueIcon


    fun falseIcon() : Maybe<Icon> = this.falseIcon


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetBooleanFormatValue =
//        RowValue8(widgetBooleanFormatTable,
//                  ProdValue(this.widgetFormat),
//                  PrimValue(this.viewType),
//                  ProdValue(this.trueFormat),
//                  ProdValue(this.falseFormat),
//                  PrimValue(this.trueText),
//                  PrimValue(this.falseText),
//                  MaybeProdValue(this.trueIcon),
//                  MaybeProdValue(this.falseIcon))

}


/**
 * True Text
 */
data class TrueText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TrueText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TrueText> = when (doc)
        {
            is DocText -> effValue(TrueText(doc.text))
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

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * False Text
 */
data class FalseText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FalseText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<FalseText> = when (doc)
        {
            is DocText -> effValue(FalseText(doc.text))
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

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Boolean Widget View Type
 */
sealed class BooleanWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object SimpleText : BooleanWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "simple_text" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("simple_text")

    }


    object SimpleToggle : BooleanWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "simple_toggle" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("simple_toggle")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<BooleanWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "simple_text"   -> effValue<ValueError,BooleanWidgetViewType>(
                                        BooleanWidgetViewType.SimpleText)
                "simple_toggle" -> effValue<ValueError,BooleanWidgetViewType>(
                                        BooleanWidgetViewType.SimpleToggle)
                else          -> effError<ValueError,BooleanWidgetViewType>(
                                        UnexpectedValue("BooleanWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}



class BooleanWidgetViewBuilder(val booleanWidget : BooleanWidget,
                               val entityId : EntityId,
                               val context : Context)
{


    fun view() : View
    {
        val layout = WidgetView.layout(booleanWidget.widgetFormat(), entityId, context)

        val layoutId = Util.generateViewId()
        layout.id = layoutId
        booleanWidget.layoutId = layoutId

        this.updateView(layout)

        return layout
    }


    fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
        contentLayout.removeAllViews()

        val bool = booleanWidget.variableValue(entityId)
        val contentView = this.contentView(bool)
        contentLayout.addView(contentView)
    }


    private fun contentView(currentValue : Boolean) : View =
        when(booleanWidget.format().viewType())
        {
            is BooleanWidgetViewType.SimpleText -> simpleTextView(currentValue)
            is BooleanWidgetViewType.SimpleToggle -> simpleToggleView(currentValue)
        }


    fun simpleTextView(currentValue : Boolean) : LinearLayout
    {
        val format  = if (currentValue)
                          booleanWidget.format().trueFormat()
                      else
                          booleanWidget.format().falseFormat()

        val layout      = this.simpleTextViewLayout(format)

        layout.setOnClickListener {
            Router.send(MessageSheetUpdate(BooleanWidgetUpdateToggle(booleanWidget.widgetId())))
        }

        if (currentValue)
        {
            val trueIcon = booleanWidget.format().trueIcon()
            when (trueIcon) {
                is Just -> layout.addView(this.iconView(trueIcon.value))
            }
        }
        else
        {
            val falseIcon = booleanWidget.format().falseIcon()
            when (falseIcon) {
                is Just -> layout.addView(this.iconView(falseIcon.value))
            }
        }

        layout.addView(this.simpleTextTextView(currentValue))

        return layout
    }


    fun simpleTextViewLayout(format : TextFormat) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity      = Gravity.CENTER_VERTICAL

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        layout.corners          = format.elementFormat().corners()

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        return layout.linearLayout(context)
    }


    fun simpleTextTextView(currentValue : Boolean) : TextView
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val text            = TextViewBuilder()

        text.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        text.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        var valueString : String
        var format : TextFormat

        if (currentValue)
        {
            valueString = booleanWidget.format().trueText().value
            format = booleanWidget.format().trueFormat()
        }
        else
        {
            valueString = booleanWidget.format().falseText().value
            format = booleanWidget.format().falseFormat()
        }


        text.text           = valueString

        text.font           = Font.typeface(format.font(),
                                            format.fontStyle(),
                                            context)

        text.sizeSp         = format.sizeSp()

        text.color          = colorOrBlack(format.colorTheme(), entityId)

        return text.textView(context)
    }


    private fun iconView(_icon : Icon) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        val iconFormat      = _icon.iconFormat()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.paddingSpacing   = _icon.elementFormat().padding()
        layout.marginSpacing    = _icon.elementFormat().margins()

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = iconFormat.size().width
        icon.heightDp           = iconFormat.size().height

        icon.image              = _icon.iconType().drawableResId()

        icon.color              = colorOrBlack(iconFormat.colorTheme(), entityId)

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Simple Toggle View
    // -----------------------------------------------------------------------------------------

    private fun simpleToggleView(currentValue : Boolean) : LinearLayout
    {
        val layout = this.simpleToggleViewLayout()

        layout.addView(this.simpleToggleFalseOptionView(currentValue))
        layout.addView(this.simpleToggleTrueOptionView(currentValue))

        return layout
    }


    private fun simpleToggleViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }


    private fun simpleToggleFalseOptionView(currentValue : Boolean) : TextView
    {
        val text            = TextViewBuilder()

        text.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        text.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        val format = if (!currentValue) {
            booleanWidget.format().falseFormat()
        }
        else {
            booleanWidget.format().leftToggleFormat()
        }

        val bgColorTheme = format.elementFormat().backgroundColorTheme()
        text.backgroundColor    = colorOrBlack(bgColorTheme, entityId)

        text.corners            = format.elementFormat().corners()

        format.styleTextViewBuilder(text, entityId, context)

        text.text               = booleanWidget.format().falseText().value

        text.paddingSpacing     = format.elementFormat().padding()
        text.marginSpacing      = format.elementFormat().margins()

        text.onClick            = View.OnClickListener {
            if (currentValue) {
                Router.send(MessageSheetUpdate(BooleanWidgetUpdateSetValue(booleanWidget.widgetId(), false)))
            }
        }

        return text.textView(context)
    }


    private fun simpleToggleTrueOptionView(currentValue : Boolean) : TextView
    {
        val text            = TextViewBuilder()

        text.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        text.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        var format : TextFormat

        if (currentValue)
            format = booleanWidget.format().trueFormat()
        else
            format = booleanWidget.format().rightToggleFormat()

        text.text           = booleanWidget.format().trueText().value

        val bgColorTheme = format.elementFormat().backgroundColorTheme()
        text.backgroundColor    = colorOrBlack(bgColorTheme, entityId)

        text.corners            = format.elementFormat().corners()

        text.paddingSpacing     = format.elementFormat().padding()
        text.marginSpacing      = format.elementFormat().margins()

        format.styleTextViewBuilder(text, entityId, context)

        text.onClick            = View.OnClickListener {
            if (!currentValue) {
                Router.send(MessageSheetUpdate(BooleanWidgetUpdateSetValue(booleanWidget.widgetId(), true)))
            }
        }

        return text.textView(context)
    }

}
