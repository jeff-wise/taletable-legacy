
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.Icon
import com.taletable.android.model.sheet.style.IconType
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Expander Widget Format
 */
data class ExpanderWidgetFormat(val id : UUID,
                                val widgetFormat : WidgetFormat,
                                val viewType : ExpanderWidgetViewType,
                                val headerOpenFormat : TextFormat,
                                val headerClosedFormat : TextFormat,
                                val headerLabelOpenFormat : TextFormat,
                                val headerLabelClosedFormat : TextFormat,
                                val headerOpenIcon : Icon,
                                val headerClosedIcon : Icon)
                                 : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    constructor(widgetFormat: WidgetFormat,
                viewType : ExpanderWidgetViewType,
                headerOpenFormat: TextFormat,
                headerClosedFormat: TextFormat,
                headerLabelOpenFormat: TextFormat,
                headerLabelClosedFormat: TextFormat,
                headerOpenIcon : Icon,
                headerClosedIcon : Icon)
        : this(UUID.randomUUID(),
               widgetFormat,
               viewType,
               headerOpenFormat,
               headerClosedFormat,
               headerLabelOpenFormat,
               headerLabelClosedFormat,
               headerOpenIcon,
               headerClosedIcon)


    companion object : Factory<ExpanderWidgetFormat>
    {

        private fun defaultWidgetFormat()             = WidgetFormat.default()
        private fun defaultViewType()                 = ExpanderWidgetViewType.Plain
        private fun defaultHeaderOpenFormat()         = TextFormat.default()
        private fun defaultHeaderClosedFormat()       = TextFormat.default()
        private fun defaultHeaderLabelOpenFormat()    = TextFormat.default()
        private fun defaultHeaderLabelClosedFormat()  = TextFormat.default()
        private fun defaultHeaderOpenIcon()           = Icon.default(IconType.ChevronDownBold)
        private fun defaultHeaderClosedIcon()         = Icon.default(IconType.ChevronRightBold)


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ExpanderWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ExpanderWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,ExpanderWidgetViewType>(defaultViewType()),
                            { ExpanderWidgetViewType.fromDocument(it) }),
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
                      // Header Open Icon
                      split(doc.maybeAt("header_open_icon"),
                            effValue(defaultHeaderOpenIcon()),
                            { Icon.fromDocument(it) }),
                      // Header Closed Icon
                      split(doc.maybeAt("header_closed_icon"),
                            effValue(defaultHeaderClosedIcon()),
                            { Icon.fromDocument(it) }))
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ExpanderWidgetFormat(defaultWidgetFormat(),
                                             defaultViewType(),
                                             defaultHeaderOpenFormat(),
                                             defaultHeaderClosedFormat(),
                                             defaultHeaderLabelOpenFormat(),
                                             defaultHeaderLabelClosedFormat(),
                                             defaultHeaderOpenIcon(),
                                             defaultHeaderClosedIcon())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType.toDocument(),
        "header_open_format" to this.headerOpenFormat().toDocument(),
        "header_closed_format" to this.headerClosedFormat().toDocument(),
        "header_label_open_format" to this.headerLabelOpenFormat().toDocument(),
        "header_label_closed_format" to this.headerLabelClosedFormat().toDocument(),
        "header_open_icon" to this.headerOpenIcon.toDocument(),
        "header_closed_icon" to this.headerClosedIcon.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : ExpanderWidgetViewType = this.viewType


    fun headerOpenFormat() : TextFormat = this.headerOpenFormat


    fun headerClosedFormat() : TextFormat = this.headerClosedFormat


    fun headerLabelOpenFormat() : TextFormat = this.headerLabelOpenFormat


    fun headerLabelClosedFormat() : TextFormat = this.headerLabelClosedFormat


    fun headerOpenIcon() : Icon = this.headerOpenIcon


    fun headerClosedIcon() : Icon = this.headerClosedIcon


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
//    override fun rowValue() = RowValue0()

}


/**
 * Expander Widget View Type
 */
sealed class ExpanderWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object Plain : ExpanderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "plain" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("plain")

    }


    object IconLeft : ExpanderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "icon_left" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("icon_left")

    }


    object IconRight : ExpanderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "icon_right" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("icon_right")

    }


    object ButtonBottom : ExpanderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "button_bottom" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("button_bottom")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ExpanderWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "icon_left"     -> effValue<ValueError,ExpanderWidgetViewType>(
                                            ExpanderWidgetViewType.IconLeft)
                "icon_right"    -> effValue<ValueError,ExpanderWidgetViewType>(
                                            ExpanderWidgetViewType.IconRight)
                "plain"         -> effValue<ValueError,ExpanderWidgetViewType>(
                                            ExpanderWidgetViewType.Plain)
                "button_bottom" -> effValue<ValueError,ExpanderWidgetViewType>(
                                                       ExpanderWidgetViewType.ButtonBottom)
                else            -> effError<ValueError,ExpanderWidgetViewType>(
                                            UnexpectedValue("ExpanderWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

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


class ExpanderWidgetUI(val expanderWidget : ExpanderWidget,
                       val entityId : EntityId,
                       val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val activity = context as AppCompatActivity

    var isOpen : Boolean = false

    var groupsLayout : LinearLayout? = null

    var iconView : ImageView? = null
    var showMoreView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(expanderWidget.widgetFormat(), entityId, context)

        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
        contentLayout.orientation       = LinearLayout.VERTICAL

        // Header
        val headerView = this.headerView()
        headerView.setOnClickListener { onClick(contentLayout) }
        contentLayout.addView(headerView)

        return layout
    }


    private fun onClick(contentLayout : LinearLayout)
    {
        // CLOSE
        if (this.isOpen)
        {
            this.isOpen = false
            contentLayout.removeAllViews()

            val headerView = this.headerView()
            headerView.setOnClickListener { onClick(contentLayout) }
            contentLayout.addView(headerView)
        }
        // OPEN
        else
        {
            this.isOpen = true

            contentLayout.removeAllViews()
            val headerView = this.headerView()
            headerView.setOnClickListener { onClick(contentLayout) }
            contentLayout.addView(headerView)
            expanderWidget.groups().forEach {
                contentLayout.addView(it.view(entityId, context, expanderWidget.groupContext))
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // HEADER
    // -----------------------------------------------------------------------------------------

    private fun headerView() : View = when (expanderWidget.format().viewType())
    {
        is ExpanderWidgetViewType.Plain -> plainHeaderView()
        is ExpanderWidgetViewType.IconLeft -> iconLeftHeaderView()
        is ExpanderWidgetViewType.IconRight -> iconRightHeaderView()
        is ExpanderWidgetViewType.ButtonBottom -> this.buttonBottomHeaderView()
    }


    // HEADER > Common Views
    // -----------------------------------------------------------------------------------------

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

        format.styleTextViewBuilder(title, entityId, context)

        title.color           = colorOrBlack(format.colorTheme(), entityId)

        title.marginSpacing = format.elementFormat().margins()
        title.paddingSpacing = format.elementFormat().padding()

        return title.textView(context)
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

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

        expanderWidget.bookReference.doMaybe { bookRef ->
            layout.onLongClick = View.OnLongClickListener {
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("book_reference", bookRef)
                activity.startActivity(intent)
                true
            }
        }

        return layout.linearLayout(context)
    }


    // HEADER > Plain
    // -----------------------------------------------------------------------------------------

    private fun plainHeaderView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.headerLabelView())

        return layout
    }


    // HEADER > Left Icon
    // -----------------------------------------------------------------------------------------

    private fun iconLeftHeaderView() : LinearLayout
    {
        val layout          = this.headerViewLayout()

        // Icon
        val iconLayout = this.headerIconLayoutView()
        val iconView = this.headerIconView()
        this.iconView = iconView
        iconLayout.addView(iconView)
        layout.addView(iconLayout)

        // Label
        layout.addView(this.headerLabelView())

        return layout
    }



    private fun headerIconLayoutView() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        val format = if (this.isOpen)
            expanderWidget.format().headerOpenIcon().elementFormat()
        else
            expanderWidget.format().headerClosedIcon().elementFormat()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor      = colorOrBlack(format.backgroundColorTheme(),
                                                   entityId)

        layout.corners              = format.corners()

        layout.paddingSpacing       = format.padding()
        layout.marginSpacing        = format.margins()

        return layout.linearLayout(context)
    }


    private fun headerIconView() : ImageView
    {
        val icon = ImageViewBuilder()

        val format = if (this.isOpen)
            expanderWidget.format().headerOpenIcon().iconFormat()
        else
            expanderWidget.format().headerClosedIcon().iconFormat()

        icon.widthDp        = format.size().width
        icon.heightDp       = format.size().height

        if (this.isOpen)
            icon.image      = expanderWidget.format().headerOpenIcon().iconType().drawableResId()
        else
            icon.image      = expanderWidget.format().headerClosedIcon().iconType().drawableResId()

        icon.color          = colorOrBlack(format.colorTheme(), entityId)


        return icon.imageView(context)
    }


    // HEADER > Right Icon
    // -----------------------------------------------------------------------------------------

    private fun iconRightHeaderView() : LinearLayout
    {
        val layout          = this.headerViewLayout()

        // Label
        val labelView = this.headerLabelView()
        val labelLayoutParams = labelView.layoutParams as LinearLayout.LayoutParams
        labelLayoutParams.width = 0
        labelLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        labelLayoutParams.weight  = 1f
        labelView.layoutParams = labelLayoutParams

        layout.addView(labelView)

        // Icon
        val iconLayout = this.headerIconLayoutView()

        val iconView = this.headerIconView()
        this.iconView = iconView
        iconLayout.addView(iconView)
        layout.addView(iconLayout)

        return layout
    }


    // HEADER > Button Bottom
    // -----------------------------------------------------------------------------------------

    private fun buttonBottomHeaderView() : LinearLayout
    {
        val layout          = this.buttonBottomHeaderViewLayout()

        // Label
        layout.addView(this.headerLabelView())

        // Icon
//        val showMoreView = this.showMoreButtonView()
//        this.showMoreView = showMoreView
//        layout.addView(showMoreView)

        return layout
    }


    private fun buttonBottomHeaderViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format = if (this.isOpen)
            expanderWidget.format().headerOpenFormat()
        else
            expanderWidget.format().headerClosedFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

        return layout.linearLayout(context)
    }


//    private fun showMoreButtonView() : TextView
//    {
//        val title               = TextViewBuilder()
//
//        val format              = if (this.isOpen)
//                                    expanderWidget.format().headerIconOpenFormat()
//                                else
//                                    expanderWidget.format().headerIconClosedFormat()
//
//        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        title.textId            = R.string.show_more
//
//        format.styleTextViewBuilder(title, entityId, context)
//
//        title.color             = colorOrBlack(format.colorTheme(), entityId)
//
//        title.marginSpacing     = format.elementFormat().margins()
//        title.paddingSpacing    = format.elementFormat().padding()
//
//        return title.textView(context)
//    }

}
