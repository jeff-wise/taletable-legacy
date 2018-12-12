
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.sheet.group.GroupListActivity
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.UpdateTargetGroupWidget
import com.taletable.android.util.Util
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Group Widget Format
 */
data class GroupWidgetFormat(val widgetFormat : WidgetFormat,
                             val viewType : GroupWidgetViewType,
                             val titleBarFormat : ElementFormat,
                             val titleFormat : TextFormat,
                             val editButtonFormat : TextFormat,
                             val groupsFormat : ElementFormat)
                              : ToDocument, Serializable
{

    companion object : Factory<GroupWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = GroupWidgetViewType.Normal
        private fun defaultTitleBarFormat()     = ElementFormat.default()
        private fun defaultTitleFormat()        = TextFormat.default()
        private fun defaultEditButtonFormat()   = TextFormat.default()
        private fun defaultGroupsFormat()       = ElementFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::GroupWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,GroupWidgetViewType>(defaultViewType()),
                            { GroupWidgetViewType.fromDocument(it) }),
                      // Title Bar Format
                      split(doc.maybeAt("title_bar_format"),
                            effValue(defaultTitleBarFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Title Format
                      split(doc.maybeAt("title_format"),
                            effValue(defaultTitleFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Edit Button Format
                      split(doc.maybeAt("edit_button_format"),
                            effValue(defaultEditButtonFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Groups Format
                      split(doc.maybeAt("groups_format"),
                            effValue(defaultGroupsFormat()),
                            { ElementFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = GroupWidgetFormat(defaultWidgetFormat(),
                                          defaultViewType(),
                                          defaultTitleBarFormat(),
                                          defaultTitleFormat(),
                                          defaultEditButtonFormat(),
                                          defaultGroupsFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : GroupWidgetViewType = this.viewType


    fun titleFormat() : TextFormat = this.titleFormat


    fun titleBarFormat() : ElementFormat = this.titleBarFormat


    fun editButtonFormat() : TextFormat = this.editButtonFormat


    fun groupsFormat() : ElementFormat = this.groupsFormat

}



/**
 * Group Widget View Type
 */
sealed class GroupWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object Normal : GroupWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "normal" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("normal")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<GroupWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "normal" -> effValue<ValueError,GroupWidgetViewType>(
                                    GroupWidgetViewType.Normal)
                else     -> effError<ValueError,GroupWidgetViewType>(
                                    UnexpectedValue("GroupWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}



class GroupWidgetUI(val groupWidget : WidgetGroup,
                    val entityId : EntityId,
                    val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val activity = context as AppCompatActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        groupWidget.title(entityId).doMaybe {
            layout.addView(this.titleBarView(it))
        }

        val contentLayout = this.contentLayout()
        contentLayout.addView(this.groupsView())

        layout.addView(contentLayout)

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // VIEWS > Groups View
    // -----------------------------------------------------------------------------------------

    fun contentLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        val layoutId = Util.generateViewId()
        groupWidget.contentLayoutId = layoutId
        layout.id  = layoutId

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    fun groupsView() : LinearLayout
    {
        val layout = this.groupsViewLayout()

        groupWidget.groups(entityId).forEach {
            layout.addView(it.view(entityId, context, groupWidget.groupContext))
        }

        return layout
    }


    private fun groupsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = groupWidget.format().groupsFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        return layout.linearLayout(context)
    }


    // VIEWS > Title Bar
    // -----------------------------------------------------------------------------------------

    private fun titleBarView(titleString : String) : ViewGroup
    {
        val layout = this.titleBarViewLayout()

        val titleTextView = this.titleTextView(titleString)
        layout.addView(titleTextView)

        layout.addView(this.editButtonView(titleString))

//        layout.setOnLongClickListener {
//            listWidget.bookReference().doMaybe {
//                val intent = Intent(activity, BookActivity::class.java)
//                intent.putExtra("book_reference", it)
//                activity.startActivity(intent)
//            }
//
//            true
//        }

        return layout
    }


    private fun titleBarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = groupWidget.format().titleBarFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.corners          = format.corners()

        return layout.linearLayout(context)
    }


    private fun titleTextView(titleString : String) : TextView
    {
        val title               = TextViewBuilder()
        val format              = groupWidget.format().titleFormat()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
        title.weight            = 1f

        title.addRule(RelativeLayout.ALIGN_PARENT_START)
        title.addRule(RelativeLayout.CENTER_VERTICAL)

        title.text              = titleString

        title.color             = colorOrBlack(format.colorTheme(), entityId)

        title.sizeSp            = format.sizeSp()

        title.font              = Font.typeface(format.font(),
                                                format.fontStyle(),
                                                context)

        title.paddingSpacing    = format.elementFormat().padding()
        title.marginSpacing     = format.elementFormat().margins()

        return title.textView(context)
    }


    private fun editButtonView(titleString : String) : LinearLayout
    {
        val layout      = this.editButtonViewLayout()

        layout.addView(this.editButtonIconView())

        layout.setOnClickListener {
            val intent = Intent(activity, GroupListActivity::class.java)
            intent.putExtra("group_references", groupWidget.groupReferences() as Serializable)
            intent.putExtra("title", titleString)
            intent.putExtra("tag_query", groupWidget.groupQuery())
            intent.putExtra("entity_id", entityId)
            intent.putExtra("update_target", UpdateTargetGroupWidget(groupWidget.widgetId))
            activity.startActivity(intent)
        }

        return layout
    }


    private fun editButtonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 5f

        layout.addRule(RelativeLayout.CENTER_VERTICAL)
        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        return layout.linearLayout(context)
    }


    private fun editButtonTextView() : TextView
    {
        val label               = TextViewBuilder()
        val format              = groupWidget.format().editButtonFormat()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.edit

        format.styleTextViewBuilder(label, entityId, context)

        return label.textView(context)
    }


    private fun editButtonIconView() : ImageView
    {
        val iconView            = ImageViewBuilder()
        val format              = groupWidget.format().editButtonFormat().iconFormat()

        iconView.widthDp        = format.size().width
        iconView.heightDp       = format.size().height

        iconView.image          = R.drawable.icon_gear_filled

        iconView.color          = colorOrBlack(format.colorTheme(), entityId)

//        format.styleTextViewBuilder(label, entityId, context)

        return iconView.imageView(context)
    }

}
