
package com.kispoko.tome.model.sheet.widget


import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.db.DB_WidgetActionFormatValue
import com.kispoko.tome.db.widgetActionFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.Icon
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.style.Width
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Action Widget Format
 */
data class ActionWidgetFormat(override val id : UUID,
                              val widgetFormat : WidgetFormat,
                              val viewType : ActionWidgetViewType,
                              val descriptionFormat : TextFormat,
                              val buttonFormat : TextFormat,
                              val buttonIcon : Maybe<Icon>)
                               : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                viewType : ActionWidgetViewType,
                descriptionFormat : TextFormat,
                buttonFormat : TextFormat,
                buttonIcon : Maybe<Icon>)
        : this(UUID.randomUUID(),
               widgetFormat,
               viewType,
               descriptionFormat,
               buttonFormat,
               buttonIcon)


    companion object : Factory<ActionWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = ActionWidgetViewType.InlineLeftButton
        private fun defaultDescriptionFormat()  = TextFormat.default()
        private fun defaultButtonFormat()       = TextFormat.default()
        private fun defaultButtonIcon()         = Nothing<Icon>()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::ActionWidgetFormat,
                     // Widget Format
                     split(doc.maybeAt("widget_format"),
                           effValue(defaultWidgetFormat()),
                           { WidgetFormat.fromDocument(it) }),
                     // View Type
                     split(doc.maybeAt("view_type"),
                           effValue<ValueError,ActionWidgetViewType>(defaultViewType()),
                           { ActionWidgetViewType.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("description_format"),
                           effValue(defaultDescriptionFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Button Format
                     split(doc.maybeAt("button_format"),
                           effValue(defaultButtonFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Button Icon
                     split(doc.maybeAt("button_icon"),
                           effValue<ValueError,Maybe<Icon>>(defaultButtonIcon()),
                           { apply(::Just, Icon.fromDocument(it)) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ActionWidgetFormat(defaultWidgetFormat(),
                                           defaultViewType(),
                                           defaultDescriptionFormat(),
                                           defaultButtonFormat(),
                                           defaultButtonIcon())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType().toDocument(),
        "description_format" to this.descriptionFormat().toDocument(),
        "button_format" to this.buttonFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : ActionWidgetViewType = this.viewType


    fun descriptionFormat() : TextFormat = this.descriptionFormat


    fun buttonFormat() : TextFormat = this.buttonFormat


    fun buttonIcon() : Maybe<Icon> = this.buttonIcon


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetActionFormatValue =
        RowValue5(widgetActionFormatTable,
                  ProdValue(this.widgetFormat),
                  PrimValue(this.viewType),
                  ProdValue(this.descriptionFormat),
                  ProdValue(this.buttonFormat),
                  MaybePrimValue(this.buttonIcon))

}


/**
 * Action Widget View Type
 */
sealed class ActionWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object InlineLeftButton : ActionWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "inline_left_button" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("inline_left_button")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "inline_left_button" -> effValue<ValueError,ActionWidgetViewType>(
                                            ActionWidgetViewType.InlineLeftButton)
                else                 -> effError<ValueError,ActionWidgetViewType>(
                                            UnexpectedValue("ActionWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Action Widget Description
 */
data class ActionWidgetDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ActionWidgetDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidgetDescription> = when (doc)
        {
            is DocText -> effValue(ActionWidgetDescription(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


class ActionWidgetViewBuilder(val actionWidget : ActionWidget,
                              val sheetUIContext : SheetUIContext)
{

    // STATE
    // -----------------------------------------------------------------------------------------

    var buttonLayout : LinearLayout? = null
    var buttonIconView : ImageView? = null
    var descriptionTextView : TextView? = null


    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(actionWidget.widgetFormat(), sheetUIContext)

        layout.addView(this.inlineLeftButtonView())


        return layout
    }


    private fun inlineLeftButtonView() : LinearLayout
    {
        val layout = this.inlineLeftButtonViewLayout()

        // Button
        val buttonLayout = this.inlineLeftButtonButtonViewLayout()
        this.buttonLayout = buttonLayout

        // Button > Icon
        val buttonIconView = this.inlineLeftButtonButtonIconView()
        buttonLayout.addView(buttonIconView)
        this.buttonIconView = buttonIconView

        // Button > Text
        val buttonTextView = this.inlineLeftButtonButtonTextView()
        buttonLayout.addView(buttonTextView)
        layout.addView(buttonLayout)

        val descriptionView = this.inlineLeftButtonDescriptionView()
        this.descriptionTextView = descriptionView
        layout.addView(descriptionView)

        return layout
    }


    private fun inlineLeftButtonViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        val width = actionWidget.widgetFormat().elementFormat().width()
        when (width) {
            is Width.Wrap -> {
                layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp = width.value.toInt()
            }
        }

        val height = actionWidget.widgetFormat().elementFormat().height()
        when (height) {
            is Height.Wrap -> {
                layout.height = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Height.Fixed -> {
                layout.heightDp = height.value.toInt()
            }
        }

        layout.orientation  = LinearLayout.HORIZONTAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun inlineLeftButtonButtonViewLayout() : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()

        val format      = actionWidget.format().buttonFormat()

        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val width = format.elementFormat().width()
        when (width) {
            is Width.Wrap -> {
                layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp = width.value.toInt()
            }
        }

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     format.elementFormat().backgroundColorTheme())

        layout.corners          = format.elementFormat().corners()

        layout.gravity    = format.elementFormat().verticalAlignment().gravityConstant() or
                                        format.elementFormat().alignment().gravityConstant()

        layout.paddingSpacing   = format.elementFormat().padding()

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun inlineLeftButtonButtonIconView() : ImageView
    {
        val icon        = ImageViewBuilder()
        val format      = actionWidget.format().buttonFormat()

        icon.widthDp    = format.iconFormat().size().width
        icon.heightDp   = format.iconFormat().size().height

        Log.d("***ACTION WIDGET", "icon size: ${format.iconFormat().size()}")

        val iconType = actionWidget.format().buttonIcon()
        when (iconType) {
            is Just    -> icon.image = iconType.value.drawableResId()
            is Nothing -> icon.image = R.drawable.icon_dice_roll_filled
        }

        icon.color          = SheetManager.color(sheetUIContext.sheetId, format.colorTheme())

    //    icon.iconSize       = format.iconFormat().size()

        return icon.imageView(sheetUIContext.context)
    }


    private fun inlineLeftButtonButtonTextView() : TextView
    {
        val result        = TextViewBuilder()

        val format = actionWidget.format().buttonFormat()

        result.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        result.height         = LinearLayout.LayoutParams.MATCH_PARENT

        result.gravity        = format.elementFormat().verticalAlignment().gravityConstant() or
                                format.elementFormat().alignment().gravityConstant()

        result.font           = Font.typeface(format.font(),
                                            format.fontStyle(),
                                            sheetUIContext.context)

        result.sizeSp         = format.sizeSp()

        result.color          = SheetManager.color(sheetUIContext.sheetId, format.colorTheme())

        result.visibility     = View.GONE

        return result.textView(sheetUIContext.context)
    }


    private fun inlineLeftButtonDescriptionView() : TextView
    {
        val description         = TextViewBuilder()

        val format              = actionWidget.format().descriptionFormat()

        description.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height      = LinearLayout.LayoutParams.MATCH_PARENT

        description.gravity    = format.elementFormat().verticalAlignment().gravityConstant() or
                format.elementFormat().alignment().gravityConstant()

        val descriptionString = actionWidget.description()
        when (descriptionString) {
            is Just -> description.text = descriptionString.value.value
        }

        description.font        = Font.typeface(format.font(),
                                                format.fontStyle(),
                                                sheetUIContext.context)

        description.sizeSp          = format.sizeSp()

        description.color           = SheetManager.color(sheetUIContext.sheetId,
                                                         format.colorTheme())
        description.backgroundColor = SheetManager.color(
                                                sheetUIContext.sheetId,
                                                format.elementFormat().backgroundColorTheme())

        description.corners         = format.elementFormat().corners()

        description.paddingSpacing  = format.elementFormat().padding()

        return description.textView(sheetUIContext.context)
    }

}
