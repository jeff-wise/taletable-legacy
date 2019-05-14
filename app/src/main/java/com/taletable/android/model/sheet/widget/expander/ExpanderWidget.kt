
package com.taletable.android.model.sheet.widget.expander


import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.group.RowLayoutType
import com.taletable.android.model.sheet.widget.ExpanderWidget
import com.taletable.android.model.sheet.widget.NumberWidget
import com.taletable.android.model.sheet.widget.Widget
import com.taletable.android.model.sheet.widget.WidgetView
import com.taletable.android.model.sheet.widget.number.NumberWidgetFormatCustom
import com.taletable.android.model.sheet.widget.number.NumberWidgetFormatOfficial
import com.taletable.android.model.sheet.widget.number.numberWidgetCustomView
import com.taletable.android.model.sheet.widget.number.numberWidgetOfficialView
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityTypeSheet
import com.taletable.android.rts.entity.entityType
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



// -----------------------------------------------------------------------------------------
// | VIEW
// -----------------------------------------------------------------------------------------

/**
 * Expander Widget view
 */
fun expanderWidgetViewGroup(
        expanderWidget : ExpanderWidget,
        rowLayoutType : RowLayoutType,
        entityId : EntityId,
        context : Context
) : ViewGroup
{
    val layout = WidgetView.layout(expanderWidget.widgetFormat(), entityId, context, rowLayoutType)

//    configureNumberWidgetViewClick(layout, numberWidget, entityId, context)

    return layout
}

///**
// * Number Widget view
// */
//private fun configureNumberWidgetViewClick(
//        viewGroup : ViewGroup,
//        widget : Widget,
//        entityId : EntityId,
//        context : Context
//)
//{
//    entityType(entityId).apDo { entityType ->
//        when (entityType)
//        {
//            is EntityTypeSheet ->
//            {
//                // NORMAL CLICK
//                viewGroup.setOnClickListener {
//                    widget.primaryAction(entityId, context)
//                }
//                // LONG CLICK
//                viewGroup.setOnLongClickListener {
//                    widget.secondaryAction(entityId, context)
//                    true
//                }
//            }
//        }
//
//    }
//}


fun expanderWidgetView(
        expanderWidget : ExpanderWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()) : View
{
    val format = expanderWidget.format

    return when (format) {
        is ExpanderWidgetFormatCustom ->
            expanderWidgetCustomView(expanderWidget, format, entityId, context, groupContext)
        is ExpanderWidgetFormatOfficial ->
            expanderWidgetOfficialView(format, expanderWidget, entityId, context, groupContext)
    }
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


    object IconRightLink : ExpanderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "icon_right_link" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("icon_right_link")

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


    object CircleAvatarLeft : ExpanderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "circle_avatar_left" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("circle_avatar_left")

    }


    object CheckboxLeft : ExpanderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "checkbox_left" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText( "checkbox_left" )

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
                "icon_left"          -> effValue<ValueError, ExpanderWidgetViewType>(
                        IconLeft)
                "icon_right"         -> effValue<ValueError, ExpanderWidgetViewType>(
                        IconRight)
                "icon_right_link"    -> effValue<ValueError, ExpanderWidgetViewType>(
                        IconRightLink)
                "plain"              -> effValue<ValueError, ExpanderWidgetViewType>(
                        Plain)
                "button_bottom"      -> effValue<ValueError, ExpanderWidgetViewType>(
                        ButtonBottom)
                "circle_avatar_left" -> effValue<ValueError, ExpanderWidgetViewType>(
                        CircleAvatarLeft)
                "checkbox_left"      -> effValue<ValueError, ExpanderWidgetViewType>(
                        CheckboxLeft)
                else            -> effError<ValueError, ExpanderWidgetViewType>(
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


//class ExpanderWidgetUI(val expanderWidget : ExpanderWidget,
//                       val entityId : EntityId,
//                       val context : Context,
//                       val groupContext : Maybe<GroupContext> = Nothing())
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    val activity = context as AppCompatActivity
//
//    var isOpen : Boolean = false
//
//    var groupsLayout : LinearLayout? = null
//
//    var iconView : ImageView? = null
//    var showMoreView : TextView? = null
//
//    var checkboxLayout : LinearLayout? = null
//
//
//    // | Checkbox
//    // -----------------------------------------------------------------------------------------
//
//
//    fun select(entityId : EntityId)
//    {
//    }
//
//
//    // | Update
//    // -----------------------------------------------------------------------------------------
//
//    fun updateCheckboxView(layout : LinearLayout)
//    {
//        layout.removeAllViews()
//        layout.addView(this.checkboxView())
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEWS
//    // -----------------------------------------------------------------------------------------
//
//    fun view() : View
//    {
//        val layout = WidgetView.layout(expanderWidget.widgetFormat(), entityId, context)
//
//        val layoutId = Util.generateViewId()
//        layout.id = layoutId
//        expanderWidget.layoutId = layoutId
//
//        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
//        contentLayout.orientation       = LinearLayout.VERTICAL
//
//        // Header
//        val headerView = this.headerView()
//        headerView.setOnClickListener { onClick(contentLayout) }
//        contentLayout.addView(headerView)
//
//        Log.d("***EXPANDER WIDGET", "context is $groupContext")
//
//        return layout
//    }
//

//
//

//
//    // -----------------------------------------------------------------------------------------
//    // HEADER
//    // -----------------------------------------------------------------------------------------
//
//    private fun headerView() : View = when (expanderWidget.format().viewType())
//    {
//        is ExpanderWidgetViewType.Plain -> plainHeaderView()
//        is ExpanderWidgetViewType.IconLeft -> iconLeftHeaderView()
//        is ExpanderWidgetViewType.IconRight -> iconRightHeaderView()
//        is ExpanderWidgetViewType.IconRightLink -> iconRightLinkHeaderView()
//        is ExpanderWidgetViewType.ButtonBottom -> this.buttonBottomHeaderView()
//        is ExpanderWidgetViewType.CircleAvatarLeft -> this.circleAvatarLeftView()
//        is ExpanderWidgetViewType.CheckboxLeft -> this.checkboxLeftHeaderView()
//    }
//
//
//    // HEADER > Common Views
//    // -----------------------------------------------------------------------------------------
//
//    private fun headerLabelView() : TextView
//    {
//        val title           = TextViewBuilder()
//
//        val format          = if (this.isOpen)
//                                  expanderWidget.format().headerLabelOpenFormat()
//                              else
//                                  expanderWidget.format().headerLabelClosedFormat()
//
//        title.width         = LinearLayout.LayoutParams.WRAP_CONTENT
//        title.height        = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        title.text          = expanderWidget.labelValue(entityId, groupContext)
//
//        format.styleTextViewBuilder(title, entityId, context)
//
//        title.color           = colorOrBlack(format.colorTheme(), entityId)
//
//        title.marginSpacing = format.elementFormat().margins()
//
//        when (expanderWidget.format().viewType()) {
//            is ExpanderWidgetViewType.CheckboxLeft -> {
//                title.padding.topDp    = format.elementFormat().padding().topDp()
//                title.padding.bottomDp = format.elementFormat().padding().bottomDp()
//                title.padding.rightDp   = format.elementFormat().padding().rightDp()
//            }
//            else -> {
//                title.paddingSpacing = format.elementFormat().padding()
//            }
//        }
//
//        return title.textView(context)
//    }
//
//
//    private fun headerViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        val format = if (this.isOpen)
//            expanderWidget.format().headerOpenFormat()
//        else
//            expanderWidget.format().headerClosedFormat()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.marginSpacing    = format.elementFormat().margins()
//
//        // Padding
//        when (expanderWidget.format().viewType()) {
//            is ExpanderWidgetViewType.CheckboxLeft -> {
//                //layout.padding.leftDp   = format.elementFormat().padding().leftDp()
//                layout.padding.rightDp   = format.elementFormat().padding().rightDp()
//            }
//            else -> {
//                layout.paddingSpacing   = format.elementFormat().padding()
//            }
//        }
//
//        layout.corners          = format.elementFormat().corners()
//        // layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)
//
//        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
//                                               entityId)
//
//        expanderWidget.bookReference.doMaybe { bookRef ->
//            layout.onLongClick = View.OnLongClickListener {
//                val intent = Intent(activity, BookActivity::class.java)
//                intent.putExtra("book_reference", bookRef)
//                activity.startActivity(intent)
//                true
//            }
//        }
//
//        return layout.linearLayout(context)
//    }
//
//
//    // HEADER > Plain
//    // -----------------------------------------------------------------------------------------
//
//    private fun plainHeaderView() : LinearLayout
//    {
//        val layout = this.headerViewLayout()
//
//        layout.addView(this.headerLabelView())
//
//        return layout
//    }
//
//
//    // HEADER > Left Icon
//    // -----------------------------------------------------------------------------------------
//
//    private fun iconLeftHeaderView() : LinearLayout
//    {
//        val layout          = this.headerViewLayout()
//
//        // Icon
//        val iconLayout = this.headerIconLayoutView()
//        val iconView = this.headerIconView()
//        this.iconView = iconView
//        iconLayout.addView(iconView)
//        layout.addView(iconLayout)
//
//        // Label
//        layout.addView(this.headerLabelView())
//
//        return layout
//    }
//
//
//
//    private fun headerIconLayoutView() : LinearLayout
//    {
//        val layout = LinearLayoutBuilder()
//
//        val format = if (this.isOpen)
//            expanderWidget.format().headerOpenIcon().elementFormat()
//        else
//            expanderWidget.format().headerClosedIcon().elementFormat()
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.backgroundColor      = colorOrBlack(format.backgroundColorTheme(),
//                                                   entityId)
//
//        layout.corners              = format.corners()
//
//        layout.paddingSpacing       = format.padding()
//        layout.marginSpacing        = format.margins()
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun headerIconView(iconId : Int? = null) : ImageView
//    {
//        val icon = ImageViewBuilder()
//
//        val format = if (this.isOpen)
//            expanderWidget.format().headerOpenIcon().iconFormat()
//        else
//            expanderWidget.format().headerClosedIcon().iconFormat()
//
//        icon.widthDp        = format.size().width
//        icon.heightDp       = format.size().height
//
//        if (this.isOpen) {
//            if (iconId != null) {
//                icon.image = iconId
//            } else {
//                icon.image      = expanderWidget.format().headerOpenIcon().iconType().drawableResId()
//            }
//        }
//        else {
//            if (iconId != null) {
//                icon.image  = iconId
//            } else {
//                icon.image      = expanderWidget.format().headerClosedIcon().iconType().drawableResId()
//            }
//        }
//
//        icon.color          = colorOrBlack(format.colorTheme(), entityId)
//
//        return icon.imageView(context)
//    }
//
//
//    // HEADER > Right Icon
//    // -----------------------------------------------------------------------------------------
//
//    private fun iconRightHeaderView() : LinearLayout
//    {
//        val layout          = this.headerViewLayout()
//
//        // Label
//        val labelView = this.headerLabelView()
//        val labelLayoutParams = labelView.layoutParams as LinearLayout.LayoutParams
//        labelLayoutParams.width = 0
//        labelLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
//        labelLayoutParams.weight  = 1f
//        labelView.layoutParams = labelLayoutParams
//
//        layout.addView(labelView)
//
//        // Icon
//        val iconLayout = this.headerIconLayoutView()
//
//        val iconView = this.headerIconView()
//        this.iconView = iconView
//        iconLayout.addView(iconView)
//        layout.addView(iconLayout)
//
//        return layout
//    }
//
//
//    // HEADER > Right Icon Link
//    // -----------------------------------------------------------------------------------------
//
//    private fun iconRightLinkHeaderView() : LinearLayout
//    {
//        val layout          = this.headerViewLayout()
//
//        // Label
//        val labelView = this.headerLabelView()
//        val labelLayoutParams = labelView.layoutParams as LinearLayout.LayoutParams
//        labelLayoutParams.width = 0
//        labelLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
//        labelLayoutParams.weight  = 1f
//        labelView.layoutParams = labelLayoutParams
//
//        layout.addView(labelView)
//
//        // Icon
//        val iconLayout = this.headerIconLayoutView()
//
//        val iconView = this.headerIconView(R.drawable.icon_open_in_window)
//        this.iconView = iconView
//        iconLayout.addView(iconView)
//        layout.addView(iconLayout)
//
//        return layout
//    }
//
//
//
//    // HEADER > Button Bottom
//    // -----------------------------------------------------------------------------------------
//
//    private fun buttonBottomHeaderView() : LinearLayout
//    {
//        val layout          = this.buttonBottomHeaderViewLayout()
//
//        // Label
//        layout.addView(this.headerLabelView())
//
//        // Icon
////        val showMoreView = this.showMoreButtonView()
////        this.showMoreView = showMoreView
////        layout.addView(showMoreView)
//
//        return layout
//    }
//
//
//    private fun buttonBottomHeaderViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        val format = if (this.isOpen)
//            expanderWidget.format().headerOpenFormat()
//        else
//            expanderWidget.format().headerClosedFormat()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.paddingSpacing   = format.elementFormat().padding()
//        layout.marginSpacing    = format.elementFormat().margins()
//
//        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)
//
//        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
//                                               entityId)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun circleAvatarLeftView() : LinearLayout
//    {
//        val headerFormat = if (this.isOpen)
//                               expanderWidget.format().headerOpenFormat()
//                           else
//                               expanderWidget.format().headerClosedFormat()
//
//        val layout = this.circleAvatarLeftViewLayout(headerFormat)
//
//        expanderWidget.format().avatarText.doMaybe   { avatarText ->
//        expanderWidget.format().avatarFormat.doMaybe { avatarFormat ->
//            layout.addView(this.circleTextAvatarView(avatarText, avatarFormat))
//        } }
//
//        val groupsLayout = this.circleAvatarGroupsViewLayout()
//
//        val groups = expanderWidget.headerGroups(entityId)
//        groups.forEach {
//            //val view = it.view(entityId, context, expanderWidget.groupContext)
//            val view = it.view(entityId, context, groupContext)
//            groupsLayout.addView(view)
//        }
//
//        layout.addView(groupsLayout)
//
//        return layout
//    }
//
//
//    private fun circleAvatarLeftViewLayout(headerFormat : TextFormat) : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.paddingSpacing       = headerFormat.elementFormat().padding()
//        layout.marginSpacing        = headerFormat.elementFormat().margins()
//
//        layout.gravity              = Gravity.CENTER_VERTICAL
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun circleTextAvatarView(text : String, format : TextFormat) : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout                  = LinearLayoutBuilder()
//        val textView                = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.widthDp              = 48
//        layout.heightDp             = 48
//
//        layout.gravity              = Gravity.CENTER
//
//        layout.corners              = Corners(24.0, 24.0, 24.0, 24.0)
//
//        layout.backgroundColor      = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)
//
//        layout.child(textView)
//
//        // (3) Text
//        // -------------------------------------------------------------------------------------
//
//        textView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        textView.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        textView.text               = text
//
//        format.styleTextViewBuilder(textView, entityId, context)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun circleAvatarGroupsViewLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        return layout.linearLayout(context)
//    }
//
//
//    // HEADER > Checkbox Left
//    // -----------------------------------------------------------------------------------------
//
//    private fun checkboxLeftHeaderView() : LinearLayout
//    {
//        val layout = this.headerViewLayout()
//
//        val checkboxLayout = this.checkboxLayout()
//        val checkboxLayoutId = Util.generateViewId()
//        checkboxLayout.id = checkboxLayoutId
//        expanderWidget.checkboxLayoutId = checkboxLayoutId
//
//        checkboxLayout.addView(this.checkboxView())
//
//        layout.addView(checkboxLayout)
//
//        val headerLayout = checkboxLeftHeaderGroupsLayout()
//
//        val groups = expanderWidget.headerGroups(entityId)
//        groups.forEach {
//            val view = it.view(entityId, context, groupContext)
//            headerLayout.addView(view)
//        }
//
//        layout.addView(headerLayout)
//
//        return layout
//    }
//
//
//    private fun checkboxLeftHeaderGroupsLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun checkboxLayout() : LinearLayout
//    {
//        val layout = LinearLayoutBuilder()
//
//        val headerFormat = if (this.isOpen)
//            expanderWidget.format().headerOpenFormat()
//        else
//            expanderWidget.format().headerClosedFormat()
//
//        val labelFormat = if (this.isOpen)
//            expanderWidget.format().headerLabelOpenFormat()
//        else
//            expanderWidget.format().headerLabelClosedFormat()
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.padding.topDp        = headerFormat.elementFormat().padding().topDp()
//        layout.padding.bottomDp     = headerFormat.elementFormat().padding().bottomDp()
//        layout.padding.leftDp       = headerFormat.elementFormat().padding().leftDp()
//        layout.padding.rightDp      = 8f
//
//        layout.onClick              = View.OnClickListener {
//            Router.send(MessageSheetUpdate(ExpanderWidgetUpdateToggle(expanderWidget.widgetId())))
//        }
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun checkboxView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout = LinearLayoutBuilder()
//        val icon   = ImageViewBuilder()
//
//        val headerFormat = if (this.isOpen)
//            expanderWidget.format().headerOpenFormat()
//        else
//            expanderWidget.format().headerClosedFormat()
//
//        val isSelected = expanderWidget.isSelected(entityId)
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        if (isSelected) {
//            layout.backgroundResource   = R.drawable.bg_widget_expander_checkbox_checked
//        }
//        else {
//            layout.backgroundResource   = R.drawable.bg_widget_expander_checkbox
//        }
//
//        layout.gravity              = Gravity.CENTER
//
//        layout.child(icon)
//
//        // (3 A) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.id                     = R.id.dialog_list_editor_checkbox_icon
//
//        icon.widthDp                = 16
//        icon.heightDp               = 16
//
//        icon.image                  = R.drawable.icon_check_bold
//
//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//        icon.color                  = colorOrBlack(iconColorTheme, entityId)
//
//        if (isSelected)
//        {
//            icon.visibility             = View.VISIBLE
//        }
//        else
//        {
//            icon.visibility             = View.GONE
//        }
//
//        return layout.linearLayout(context)
//    }
//
//
//
//}
