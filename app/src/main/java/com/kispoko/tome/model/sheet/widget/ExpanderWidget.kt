
package com.kispoko.tome.model.sheet.widget


import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.db.DB_WidgetExpanderFormatValue
import com.kispoko.tome.db.widgetExpanderFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue7
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Button Widget Format
 */
data class ExpanderWidgetFormat(override val id : UUID,
                                val widgetFormat : WidgetFormat,
                                val headerOpenFormat : TextFormat,
                                val headerClosedFormat : TextFormat,
                                val headerLabelOpenFormat : TextFormat,
                                val headerLabelClosedFormat : TextFormat,
                                val headerIconOpenFormat : TextFormat,
                                val headerIconClosedFormat : TextFormat)
                                 : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    constructor(widgetFormat: WidgetFormat,
                headerOpenFormat: TextFormat,
                headerClosedFormat: TextFormat,
                headerLabelOpenFormat: TextFormat,
                headerLabelClosedFormat: TextFormat,
                headerIconOpenFormat : TextFormat,
                headerIconClosedFormat : TextFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               headerOpenFormat,
               headerClosedFormat,
               headerLabelOpenFormat,
               headerLabelClosedFormat,
               headerIconOpenFormat,
               headerIconClosedFormat)


    companion object : Factory<ExpanderWidgetFormat>
    {

        private fun defaultWidgetFormat()           = WidgetFormat.default()
        private fun defaultHeaderOpenFormat()       = TextFormat.default()
        private fun defaultHeaderClosedFormat()     = TextFormat.default()
        private fun defaultHeaderLabelOpenFormat()       = TextFormat.default()
        private fun defaultHeaderLabelClosedFormat()     = TextFormat.default()
        private fun defaultHeaderIconOpenFormat()   = TextFormat.default()
        private fun defaultHeaderIconClosedFormat() = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ExpanderWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ExpanderWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Header Open
                      split(doc.maybeAt("header_open_format"),
                            effValue(defaultHeaderOpenFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Closed
                      split(doc.maybeAt("header_closed_format"),
                            effValue(defaultHeaderClosedFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Label Open
                      split(doc.maybeAt("header_label_open_format"),
                            effValue(defaultHeaderLabelOpenFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Label Closed
                      split(doc.maybeAt("header_label_closed_format"),
                            effValue(defaultHeaderLabelClosedFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Icon Open
                      split(doc.maybeAt("header_icon_open_format"),
                            effValue(defaultHeaderIconOpenFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Closed
                      split(doc.maybeAt("header_icon_closed_format"),
                            effValue(defaultHeaderIconClosedFormat()),
                            { TextFormat.fromDocument(it) }))
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ExpanderWidgetFormat(defaultWidgetFormat(),
                                       defaultHeaderOpenFormat(),
                                       defaultHeaderClosedFormat(),
                                       defaultHeaderLabelOpenFormat(),
                                       defaultHeaderLabelClosedFormat(),
                                       defaultHeaderIconOpenFormat(),
                                       defaultHeaderIconClosedFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "header_open_format" to this.headerOpenFormat().toDocument(),
        "header_closed_format" to this.headerClosedFormat().toDocument(),
        "header_label_open_format" to this.headerLabelOpenFormat().toDocument(),
        "header_label_closed_format" to this.headerLabelClosedFormat().toDocument(),
        "header_icon_open_format" to this.headerIconOpenFormat().toDocument(),
        "header_icon_closed_format" to this.headerIconClosedFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun headerOpenFormat() : TextFormat = this.headerOpenFormat


    fun headerClosedFormat() : TextFormat = this.headerClosedFormat


    fun headerLabelOpenFormat() : TextFormat = this.headerLabelOpenFormat


    fun headerLabelClosedFormat() : TextFormat = this.headerLabelClosedFormat


    fun headerIconOpenFormat() : TextFormat = this.headerIconOpenFormat


    fun headerIconClosedFormat() : TextFormat = this.headerIconClosedFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetExpanderFormatValue =
        RowValue7(widgetExpanderFormatTable,
                  ProdValue(this.widgetFormat),
                  ProdValue(this.headerOpenFormat),
                  ProdValue(this.headerClosedFormat),
                  ProdValue(this.headerLabelOpenFormat),
                  ProdValue(this.headerLabelClosedFormat),
                  ProdValue(this.headerIconOpenFormat),
                  ProdValue(this.headerIconClosedFormat))

}


/**
 * Expander Widget Label
 */
data class ExpanderWidgetLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ExpanderWidgetLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ExpanderWidgetLabel> = when (doc)
        {
            is DocText -> effValue(ExpanderWidgetLabel(doc.text))
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




class ExpanderWidgetViewBuilder(val expanderWidget : ExpanderWidget,
                                val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    var isOpen : Boolean = false

    var groupsLayout : LinearLayout? = null

    var iconView : ImageView? = null


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(expanderWidget.widgetFormat(), sheetUIContext)

        val contentLayout = layout.findViewById(R.id.widget_content_layout) as LinearLayout
        contentLayout.orientation       = LinearLayout.VERTICAL

        // Header
        contentLayout.addView(this.headerView())

        // Groups
//        val groupsLayout = this.groupsLayout()
//        this.groupsLayout = groupsLayout
//
//        contentLayout.addView(groupsLayout)

        contentLayout.setOnClickListener {
            // CLOSE
            if (this.isOpen)
            {
                this.isOpen = false
                contentLayout.removeAllViews()
                contentLayout.addView(this.headerView())

//                this.iconView?.setImageDrawable(ContextCompat.getDrawable(sheetUIContext.context, R.drawable.icon_chevron_right))
            }
            // OPEN
            else
            {
                this.isOpen = true

                contentLayout.removeAllViews()
                contentLayout.addView(this.headerView())
                expanderWidget.groups().forEach {
                    contentLayout.addView(it.view(sheetUIContext))
                }

//                this.iconView?.setImageDrawable(ContextCompat.getDrawable(sheetUIContext.context, R.drawable.icon_chevron_down))
            }
        }

        return layout
    }


    // -----------------------------------------------------------------------------------------
    // HEADER
    // -----------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout          = this.headerViewLayout()

        // Icon
        val iconLayout = this.headerIconLayoutView()
        val iconView = this.headerIconView()
        this.iconView = iconView
        iconLayout.addView(iconView)
        // layout.addView(iconLayout)

        // Label
        layout.addView(this.headerLabelView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format = if (this.isOpen)
            expanderWidget.format().headerOpenFormat()
        else
            expanderWidget.format().headerClosedFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

//        layout.corners          = format.elementFormat().corners()
        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, format.elementFormat().backgroundColorTheme())

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun headerIconLayoutView() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        val format = if (this.isOpen)
            expanderWidget.format().headerIconOpenFormat()
        else
            expanderWidget.format().headerIconClosedFormat()

        layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, format.elementFormat().backgroundColorTheme())

        layout.corners      = format.elementFormat().corners()

        layout.paddingSpacing      = format.elementFormat().padding()
        layout.marginSpacing      = format.elementFormat().margins()

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun headerIconView() : ImageView
    {
        val icon = ImageViewBuilder()

        val format = if (this.isOpen)
            expanderWidget.format().headerIconOpenFormat()
        else
            expanderWidget.format().headerIconClosedFormat()

        icon.widthDp        = format.iconFormat().size().width
        icon.heightDp       = format.iconFormat().size().height

        if (this.isOpen)
            icon.image      = R.drawable.icon_chevron_down
        else
            icon.image      = R.drawable.icon_chevron_right

        icon.color          = SheetManager.color(sheetUIContext.sheetId, format.iconFormat().colorTheme())


        return icon.imageView(sheetUIContext.context)



    }


    private fun headerLabelView() : TextView
    {
        val title           = TextViewBuilder()

        val format          = if (this.isOpen)
                                  expanderWidget.format().headerLabelOpenFormat()
                              else
                                  expanderWidget.format().headerLabelClosedFormat()

        title.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text          = expanderWidget.label().value

        format.styleTextViewBuilder(title, sheetUIContext)

        title.color           = SheetManager.color(sheetUIContext.sheetId, format.colorTheme())

        title.marginSpacing = format.elementFormat().margins()
        title.paddingSpacing = format.elementFormat().padding()

        return title.textView(sheetUIContext.context)
    }


    private fun groupsLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }

}
