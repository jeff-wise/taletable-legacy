
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.db.DB_WidgetBooleanFormatValue
import com.kispoko.tome.db.widgetBooleanFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue8
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Icon
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.BooleanWidgetUpdateToggle
import com.kispoko.tome.rts.entity.sheet.MessageSheetUpdate
import com.kispoko.tome.rts.entity.sheet.SheetUpdateWidget
import com.kispoko.tome.rts.entity.sheetOrError
import com.kispoko.tome.util.Util
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
import java.util.*



/**
 * Boolean Widget Format
 */
data class BooleanWidgetFormat(override val id : UUID,
                               val widgetFormat : WidgetFormat,
                               val viewType: BooleanWidgetViewType,
                               val trueFormat : TextFormat,
                               val falseFormat : TextFormat,
                               val trueText : TrueText,
                               val falseText : FalseText,
                               val trueIcon : Maybe<Icon>,
                               val falseIcon : Maybe<Icon>)
                                : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                viewType : BooleanWidgetViewType,
                trueFormat : TextFormat,
                falseFormat : TextFormat,
                trueText : TrueText,
                falseText : FalseText,
                trueIcon : Maybe<Icon>,
                falseIcon : Maybe<Icon>)
        : this(UUID.randomUUID(),
               widgetFormat,
               viewType,
               trueFormat,
               falseFormat,
               trueText,
               falseText,
               trueIcon,
               falseIcon)


    companion object : Factory<BooleanWidgetFormat>
    {

        private fun defaultWidgetFormat()           = WidgetFormat.default()
        private fun defaultWidgetViewType()         = BooleanWidgetViewType.SimpleText
        private fun defaultTrueFormat()             = TextFormat.default()
        private fun defaultFalseFormat()            = TextFormat.default()
        private fun defaultTrueText()               = TrueText("true")
        private fun defaultFalseText()              = FalseText("false")


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


    fun trueFormat() : TextFormat = this.trueFormat


    fun falseFormat() : TextFormat = this.falseFormat


    fun trueText() : TrueText = this.trueText


    fun falseText() : FalseText = this.falseText


    fun trueIcon() : Maybe<Icon> = this.trueIcon


    fun falseIcon() : Maybe<Icon> = this.falseIcon


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetBooleanFormatValue =
        RowValue8(widgetBooleanFormatTable,
                  ProdValue(this.widgetFormat),
                  PrimValue(this.viewType),
                  ProdValue(this.trueFormat),
                  ProdValue(this.falseFormat),
                  PrimValue(this.trueText),
                  PrimValue(this.falseText),
                  MaybeProdValue(this.trueIcon),
                  MaybeProdValue(this.falseIcon))

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


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<BooleanWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "simple_text" -> effValue<ValueError,BooleanWidgetViewType>(
                                     BooleanWidgetViewType.SimpleText)
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
        val contentLayout = layout.findViewById(R.id.widget_content_layout) as LinearLayout
        contentLayout.removeAllViews()

        val simpleView = this.simpleView()
        contentLayout.addView(simpleView)

        layout.setOnClickListener {
            Router.send(MessageSheetUpdate(BooleanWidgetUpdateToggle(booleanWidget.id)))
        }

    }


    fun simpleView() : LinearLayout
    {
        val layout      = this.simpleViewLayout()

        booleanWidget.variableValue(entityId) apDo { currentValue ->

            val format = booleanWidget.format()

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

            layout.addView(this.simpleTextView(currentValue))

        }

        return layout
    }


    fun simpleViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity      = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    fun simpleTextView(currentValue : Boolean) : TextView
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


}
