
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.flexbox.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.book.BookActivity
import com.kispoko.tome.activity.sheet.dialog.ValueChooserDialogFragment
import com.kispoko.tome.activity.sheet.dialog.openVariableEditorDialog
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppStateError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.engine.EngineTextListValue
import com.kispoko.tome.model.engine.constraint.ConstraintTypeTextListMaxSize
import com.kispoko.tome.model.engine.constraint.TextListConstraintMaxSize
import com.kispoko.tome.model.engine.message.Message
import com.kispoko.tome.model.engine.reference.TextReferenceLiteral
import com.kispoko.tome.model.engine.value.Value
import com.kispoko.tome.model.engine.value.ValueId
import com.kispoko.tome.model.engine.value.ValueReference
import com.kispoko.tome.model.engine.value.ValueSetId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.rts.entity.textListVariable
import com.kispoko.tome.rts.entity.value
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



/**
 * List Widget Format
 */
data class ListWidgetFormat(val widgetFormat : WidgetFormat,
                            val viewType : ListViewType,
                            val listFormat : ElementFormat,
                            val itemFormat : TextFormat,
                            val itemInactiveFormat : TextFormat,
                            val defaultItemText : ListWidgetDefaultItemText,
                            val descriptionFormat : TextFormat,
                            val titleBarFormat : ElementFormat,
                            val titleFormat : TextFormat,
                            val editButtonFormat : TextFormat,
                            val constraintFormat : ListWidgetConstraintFormat)
                             : ToDocument, Serializable
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    companion object : Factory<ListWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = ListViewType.ParagraphCommas
        private fun defaultListFormat()         = ElementFormat.default()
        private fun defaultItemFormat()         = TextFormat.default()
        private fun defaultItemInactiveFormat() = TextFormat.default()
        private fun defaultItemText()           = ListWidgetDefaultItemText("")
        private fun defaultDescriptionFormat()  = TextFormat.default()
        private fun defaultTitleBarFormat()     = ElementFormat.default()
        private fun defaultTitleFormat()        = TextFormat.default()
        private fun defaultEditButtonFormat()   = TextFormat.default()
        private fun defaultConstraintFormat()   = ListWidgetConstraintFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ListWidgetFormat,
                     // Widget Format
                     split(doc.maybeAt("widget_format"),
                           effValue(defaultWidgetFormat()),
                           { WidgetFormat.fromDocument(it) }),
                     // View Type
                     split(doc.maybeAt("view_type"),
                           effValue<ValueError,ListViewType>(defaultViewType()),
                           { ListViewType.fromDocument(it) }),
                     // List Format
                     split(doc.maybeAt("list_format"),
                           effValue(defaultListFormat()),
                           { ElementFormat.fromDocument(it) }),
                     // Item Format
                     split(doc.maybeAt("item_format"),
                           effValue(defaultItemFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Item Inactive Format
                     split(doc.maybeAt("item_inactive_format"),
                           effValue(defaultItemInactiveFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("default_item_text"),
                           effValue(defaultItemText()),
                           { ListWidgetDefaultItemText.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("description_format"),
                           effValue(defaultDescriptionFormat()),
                           { TextFormat.fromDocument(it) }),
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
                     // Constraint Format
                     split(doc.maybeAt("constraint_format"),
                           effValue(defaultConstraintFormat()),
                           { ListWidgetConstraintFormat.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ListWidgetFormat(defaultWidgetFormat(),
                                         defaultViewType(),
                                         defaultListFormat(),
                                         defaultItemFormat(),
                                         defaultItemInactiveFormat(),
                                         defaultItemText(),
                                         defaultDescriptionFormat(),
                                         defaultTitleBarFormat(),
                                         defaultTitleFormat(),
                                         defaultEditButtonFormat(),
                                         defaultConstraintFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "item_format" to this.itemFormat().toDocument(),
        "description_format" to this.descriptionFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : ListViewType = this.viewType


    fun listFormat() : ElementFormat = this.listFormat


    fun itemFormat() : TextFormat = this.itemFormat


    fun itemInactiveFormat() : TextFormat = this.itemInactiveFormat


    fun defaultItemText() : ListWidgetDefaultItemText = this.defaultItemText


    fun descriptionFormat() : TextFormat = this.descriptionFormat


    fun titleFormat() : TextFormat = this.titleFormat


    fun titleBarFormat() : ElementFormat = this.titleBarFormat


    fun editButtonFormat() : TextFormat = this.editButtonFormat


    fun constraintFormat() : ListWidgetConstraintFormat = this.constraintFormat

}



/**
 * List Widget Constraint Format
 */
data class ListWidgetConstraintFormat(val textFormat : TextFormat,
                                      val failTextFormat : TextFormat,
                                      val message : Maybe<Message>)
                                       : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetConstraintFormat>
    {

        private fun defaultTextFormat()     = TextFormat.default()
        private fun defaultFailTextFormat() = TextFormat.default()
        private fun defaultMessage()        = Nothing<Message>()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetConstraintFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ListWidgetConstraintFormat,
                     // Text Format
                     split(doc.maybeAt("text_format"),
                           effValue(defaultTextFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Fail Text Format
                     split(doc.maybeAt("fail_text_format"),
                           effValue(defaultFailTextFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Message
                     split(doc.maybeAt("message"),
                           effValue<ValueError,Maybe<Message>>(defaultMessage()),
                           { apply(::Just, Message.fromDocument(it))  })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ListWidgetConstraintFormat(defaultTextFormat(),
                                                   defaultFailTextFormat(),
                                                   defaultMessage())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "text_format" to this.textFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textFormat() : TextFormat = this.textFormat


    fun failTextFormat() : TextFormat = this.failTextFormat


    fun message() : Maybe<Message> = this.message

}


/**
 * List View Type
 */
sealed class ListViewType : ToDocument, SQLSerializable, Serializable
{

    object ParagraphCommas : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "paragraph_commas" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("paragraph_commas")

    }

    object Rows : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "rows" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("rows")

    }

    object RowsSimpleEditor : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "rows_simple_editor" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("rows_simple_editor")

    }

    object Pool : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "pool" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("pool")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ListViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "paragraph_commas"   -> effValue<ValueError,ListViewType>(ListViewType.ParagraphCommas)
                "rows"               -> effValue<ValueError,ListViewType>(ListViewType.Rows)
                "rows_simple_editor" -> effValue<ValueError,ListViewType>(ListViewType.RowsSimpleEditor)
                "pool"               -> effValue<ValueError,ListViewType>(ListViewType.Pool)
                else                 -> effError<ValueError,ListViewType>(
                                            UnexpectedValue("ListViewType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}



/**
 * List Widget Description
 */
data class ListWidgetDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetDescription> = when (doc)
        {
            is DocText -> effValue(ListWidgetDescription(doc.text))
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


/**
 * List Widget Default Item Text
 */
data class ListWidgetDefaultItemText(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetDefaultItemText>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetDefaultItemText> = when (doc)
        {
            is DocText -> effValue(ListWidgetDefaultItemText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}



sealed class ListWidgetEditType
{
    object Simple : ListWidgetEditType()
    object Advanced : ListWidgetEditType()
}




class ListWidgetUI(val listWidget : ListWidget,
                   val entityId : EntityId,
                   val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val activity = context as AppCompatActivity

    private var editMode : Boolean = false

    private var editButtonTextView : TextView? = null
    private var rowViews : MutableList<LinearLayout> = mutableListOf()


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun openSubsetEditDialog()
    {
        val textListVariable =  listWidget.variable(entityId)
        when (textListVariable) {
            is Val -> {
                openVariableEditorDialog(textListVariable.value,
                                         null,
                                         UpdateTargetListWidget(listWidget.widgetId()),
                                         entityId,
                                         context)
            }
        }
    }


    private fun openListEditDialog(valueString : String? = null)
    {
//        val valueIdsEff = listWidget.variable(entityId)
//                                 .apply { it.setv(entityId) }
//                                 .apply { effValue<AppError,List<ValueId>>(it.map { ValueId(it) }) }

//

        listWidget.variable(entityId) apDo { variable ->

            val setVariable = variable.setVariable(entityId)
            when (setVariable)
            {
                is Just ->
                {
                    val values = setVariable.value.values(entityId)
                    variable.valueSetId.doMaybe { valueSetId ->
                        val chooseItemDialog = ValueChooserDialogFragment.newInstance(
                                                                valueSetId,
                                                                values.map { it.valueId() },
                                                                valueString?.let { ValueId(it) },
                                                                UpdateTargetListWidget(listWidget.widgetId()),
                                                                entityId)
                        chooseItemDialog.show(activity.supportFragmentManager, "")
                    }
                }
                is Nothing ->
                {
                    variable.valueSetId().doMaybe { valueSetId ->
                        val chooseItemDialog = ValueChooserDialogFragment.newInstance(
                                                                valueSetId,
                                                                listOf(),
                                                                valueString?.let { ValueId(it) },
                                                                UpdateTargetListWidget(listWidget.widgetId()),
                                                                entityId)
                        chooseItemDialog.show(activity.supportFragmentManager, "")
                    }

                }
            }


        }



    }



    private fun toggleEditMode()
    {
        editMode = !editMode

        this.toggleTableRowEditButtons()
        this.toggleEditButton()
    }


    private fun toggleEditButton()
    {
        if (editMode)
            this.editButtonTextView?.text = context.getString(R.string.view_only)
        else
            this.editButtonTextView?.text = context.getString(R.string.edit)
    }


    private fun toggleTableRowEditButtons()
    {
        if (editMode)
        {
            this.rowViews.forEach { tableRowView ->

                val editButtonView = tableRowView.findViewById<LinearLayout>(R.id.list_item_edit_button)
                if (editButtonView != null)
                    editButtonView.visibility = View.VISIBLE

            }
//            val headerRowEditButtonView = headerRowView?.findViewById<LinearLayout>(R.id.table_row_edit_button)
//            if (headerRowEditButtonView != null)
//                headerRowEditButtonView.visibility = View.VISIBLE
        }
        else
        {
            this.rowViews.forEach { tableRowView ->

                val editButtonView = tableRowView.findViewById<LinearLayout>(R.id.list_item_edit_button)
                if (editButtonView != null)
                    editButtonView.visibility = View.GONE
            }

//            val headerRowEditButtonView = headerRowView?.findViewById<LinearLayout>(R.id.table_row_edit_button)
//            if (headerRowEditButtonView != null)
//                headerRowEditButtonView.visibility = View.GONE
        }
    }



    private fun itemStrings() : List<String>
    {
        val idStrings = listWidget.valueIdStrings(entityId)
        return when (idStrings)
        {
            is Val -> {
                idStrings.value.sorted()
            }
            is Err -> {
                ApplicationLog.error(idStrings.error)
                listOf()
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(listWidget.widgetFormat(), entityId, context)

        val layoutId = Util.generateViewId()
        layout.id = layoutId
        listWidget.layoutViewId = layoutId

        this.updateView(layout)

        return layout
    }


    fun listView() : View = when (listWidget.format().viewType())
    {
        is ListViewType.ParagraphCommas  -> inlineView()
        is ListViewType.Rows             -> rowsView(ListWidgetEditType.Advanced)
        is ListViewType.RowsSimpleEditor -> rowsView(ListWidgetEditType.Simple)
        is ListViewType.Pool             -> poolView()
    }


    fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)

        contentLayout?.removeAllViews()

        contentLayout?.addView(this.listView())
    }


    // -----------------------------------------------------------------------------------------
    // VIEW > Inline
    // -----------------------------------------------------------------------------------------

    fun inlineView() : TextView
    {
        val paragraph           = TextViewBuilder()
        val format              = listWidget.format().descriptionFormat()

        paragraph.width         = LinearLayout.LayoutParams.MATCH_PARENT
        paragraph.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        val description = listWidget.description()
        val valueSetId = listWidget.variable(entityId).apply {
                            note<AppError,ValueSetId>(it.valueSetId().toNullable(),
                                                      AppStateError(VariableDoesNotHaveValueSet(it.variableId())))
                         }
        when (description) {
            is Just -> {
                when (valueSetId) {
                    is Val -> {
                        val values = listWidget.value(entityId) ap { valueIds ->
                                            valueIds.mapM { valueId ->
                                                val valueRef = ValueReference(TextReferenceLiteral(valueSetId.value.value),
                                                                              TextReferenceLiteral(valueId))
                                                value(valueRef, entityId)
                                            }
                                     }
                        when (values) {
                            is Val -> {
                                val valueStrings = values.value.map { it.valueString() }
                                paragraph.textSpan = this.spannableString(description.value.value, valueStrings)
                            }
                            is Err -> ApplicationLog.error(values.error)
                        }
                    }
                }
            }
        }

        paragraph.onClick       = View.OnClickListener {

            val textListVariable =  listWidget.variable(entityId)
            when (textListVariable) {
                is Val -> {
                    openVariableEditorDialog(textListVariable.value,
                                             null,
                                             UpdateTargetListWidget(listWidget.widgetId()),
                                             entityId,
                                             context)
                }
            }

        }

        return paragraph.textView(context)
    }


    private fun spannableString(description : String, valueStrings : List<String>) : SpannableStringBuilder
    {
        val builder = SpannableStringBuilder()
        var currentIndex = 0

        val parts = description.split("$$$")
        val part1 : String = parts[0]

        // > Part 1
        builder.append(part1)

        this.formatSpans(listWidget.format().descriptionFormat()).forEach {
            builder.setSpan(it, 0, part1.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        currentIndex += part1.length

        val items = valueStrings.take(valueStrings.size - 1)
        val lastItem = valueStrings.elementAt(valueStrings.size - 1)

        // > Items
        items.forEach { item ->
            builder.append(item)

            this.formatSpans(listWidget.format().itemFormat()).forEach {
                builder.setSpan(it, currentIndex, currentIndex + item.length, SPAN_INCLUSIVE_EXCLUSIVE)
            }

            currentIndex += item.length

            if (items.size > 1) {
                builder.append(", ")

                this.formatSpans(listWidget.format().descriptionFormat()).forEach {
                    builder.setSpan(it, currentIndex, currentIndex + 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }

                currentIndex += 2
            }
        }

        if (items.size == 1)
        {
            builder.append(" and ")

            this.formatSpans(listWidget.format().descriptionFormat()).forEach {
                builder.setSpan(it, currentIndex, currentIndex + 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }

            currentIndex += 5
        }
        else if (items.size > 1)
        {
            builder.append("and ")

            this.formatSpans(listWidget.format().descriptionFormat()).forEach {
                builder.setSpan(it, currentIndex, currentIndex + 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }

            currentIndex += 4

        }

        builder.append(lastItem)

        this.formatSpans(listWidget.format().itemFormat()).forEach {
            builder.setSpan(it, currentIndex, currentIndex + lastItem.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        return builder
    }


    private fun formatSpans(textFormat : TextFormat) : List<Any>
    {
        val sizePx = Util.spToPx(textFormat.sizeSp(), context)
        val sizeSpan = AbsoluteSizeSpan(sizePx)

        val typeface = Font.typeface(textFormat.font(), textFormat.fontStyle(), context)

        val typefaceSpan = CustomTypefaceSpan(typeface)

        var color = colorOrBlack(textFormat.colorTheme(), entityId)
        val colorSpan = ForegroundColorSpan(color)

        var bgColor = colorOrBlack(textFormat.elementFormat().backgroundColorTheme(), entityId)
        val bgColorSpan = BackgroundColorSpan(bgColor)

        return listOf(sizeSpan, typefaceSpan, colorSpan, bgColorSpan)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW > Rows
    // -----------------------------------------------------------------------------------------

    private fun rowsView(editType : ListWidgetEditType) : LinearLayout
    {
        val layout = this.rowsViewLayout()

        layout.addView(this.titleBarView(editType))

        listWidget.variable(entityId) apDo { variable ->
            variable.constraint().doMaybe { constraint ->
            constraint.constraintOfType(ConstraintTypeTextListMaxSize).doMaybe {
                if (it is TextListConstraintMaxSize)
                {
                    SheetData.number(it.maxSize, entityId).apDo {
                        it.doMaybe {
                            val currentSizeString = this.itemStrings().size.toString()
                            val maxSizeString = Util.doubleString(it)
                            val counterString = "$currentSizeString\u2008/\u2008$maxSizeString"
                            listWidget.format().constraintFormat().message().doMaybe {
                                val constraintString = it.templateString(listOf(counterString))
                                layout.addView(this.rowsConstraintView(constraintString))
                            }
                        }
                    }
                }
            } }
        }

        layout.addView(this.itemsView())

        return layout
    }


    private fun rowsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun itemsView() : LinearLayout
    {
        val layout = this.itemsViewLayout()

        val itemStrings = listWidget.valueIdStrings(entityId)
        when (itemStrings)
        {
            is Val -> {
                itemStrings.value.sorted().forEach {
                    val rowView = this.rowView(it)
                    this.rowViews.add(rowView)
                    layout.addView(rowView)
                }
            }
            is Err -> {
                ApplicationLog.error(itemStrings.error)
            }
        }

        return layout
    }


    private fun itemsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun rowView(itemString : String) : LinearLayout
    {
        val layout = this.rowViewLayout()
        val format = listWidget.format().itemFormat()

        format.elementFormat().border().top().doMaybe {
            layout.addView(this.rowBorderView(it))
        }

        layout.addView(this.rowItemContentView(itemString))

        format.elementFormat().border().bottom().doMaybe {
            layout.addView(this.rowBorderView(it))
        }

        return layout
    }


    private fun rowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = listWidget.format().itemFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        layout.corners          = format.elementFormat().corners()

        layout.marginSpacing    = format.elementFormat().margins()

        return layout.linearLayout(context)
    }


    private fun rowItemContentView(itemString : String) : LinearLayout
    {
        val layout = this.rowItemContentViewLayout()

        layout.addView(this.rowItemIconView())

        layout.addView(this.rowItemTextView(itemString))

        return layout
    }


    private fun rowItemContentViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = listWidget.format().itemFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.paddingSpacing   = format.elementFormat().padding()

        return layout.linearLayout(context)
    }


    private fun rowItemIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        val format              = listWidget.format().itemFormat()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.id               = R.id.list_item_edit_button

        layout.widthDp          = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.visibility       = View.GONE

        layout.gravity          = Gravity.CENTER_VERTICAL
        layout.layoutGravity    = Gravity.CENTER_VERTICAL

        layout.margin.rightDp   = format.elementFormat().padding().leftDp()

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 18
        icon.heightDp           = 18

        icon.image              = R.drawable.icon_vertical_ellipsis

        icon.layoutGravity      = Gravity.CENTER

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        icon.color              = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun rowItemTextView(itemString : String) : TextView
    {
        val item                = TextViewBuilder()
        val format              = listWidget.format().itemFormat()

        item.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        item.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        listWidget.title(entityId).doMaybe { titleString ->
            item.text           = titleString
        }

        item.text               = itemString

        item.color              = colorOrBlack(format.colorTheme(), entityId)

        item.sizeSp             = format.sizeSp()

        item.font               = Font.typeface(format.font(),
                                                format.fontStyle(),
                                                context)


        return item.textView(context)
    }


    private fun rowBorderView(format : BorderEdge) : LinearLayout
    {
        val border                  = LinearLayoutBuilder()

        border.width                = LinearLayout.LayoutParams.MATCH_PARENT
        border.heightDp             = format.thickness().value

        border.backgroundColor      = colorOrBlack(format.colorTheme(), entityId)

        return border.linearLayout(context)
    }


    // VIEWS > Rows > Title Bar
    // -----------------------------------------------------------------------------------------

    private fun titleBarView(editType : ListWidgetEditType) : ViewGroup
    {
        val layout = this.titleBarViewLayout()

        val titleTextView = this.titleTextView()
        layout.addView(titleTextView)

        layout.addView(this.editButtonView(editType))

        layout.setOnLongClickListener {
            listWidget.bookReference().doMaybe {
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("book_reference", it)
                activity.startActivity(intent)
            }

            true
        }

        return layout
    }


    private fun titleBarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = listWidget.format().titleBarFormat()

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


    private fun titleTextView() : TextView
    {
        val title               = TextViewBuilder()
        val format              = listWidget.format().titleFormat()

//        title.layoutType        = LayoutType.RELATIVE
//        title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
//        title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT
        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
        title.weight            = 1f

        title.addRule(RelativeLayout.ALIGN_PARENT_START)
        title.addRule(RelativeLayout.CENTER_VERTICAL)

        listWidget.title(entityId).doMaybe { titleString ->
            title.text          = titleString
        }

        title.color             = colorOrBlack(format.colorTheme(), entityId)

        title.sizeSp            = format.sizeSp()

        title.font              = Font.typeface(format.font(),
                                                format.fontStyle(),
                                                context)

        title.paddingSpacing    = format.elementFormat().padding()
        title.marginSpacing     = format.elementFormat().margins()

        return title.textView(context)
    }


    private fun editButtonView(editType : ListWidgetEditType) : LinearLayout
    {
        val layout      = this.editButtonViewLayout(editType)

        val v = this.editButtonTextView()
        layout.addView(v)
        this.editButtonTextView = v

        return layout
    }


    private fun editButtonViewLayout(editType : ListWidgetEditType) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

//        layout.layoutType       = LayoutType.RELATIVE
//        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 5f

        layout.addRule(RelativeLayout.CENTER_VERTICAL)
        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.onClick          = View.OnClickListener {
            when (editType) {
                is ListWidgetEditType.Simple   -> this.openSubsetEditDialog()
                is ListWidgetEditType.Advanced -> this.toggleEditMode()
            }

        }

        return layout.linearLayout(context)
    }


    private fun editButtonTextView() : TextView
    {
        val label               = TextViewBuilder()
        val format              = listWidget.format().editButtonFormat()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.edit

        format.styleTextViewBuilder(label, entityId, context)

        return label.textView(context)
    }


    // VIEWS > Rows > Constraint
    // -----------------------------------------------------------------------------------------

    private fun rowsConstraintView(constraintString : String) : LinearLayout
    {
        val layout = this.rowsConstraintViewLayout()

        layout.addView(this.rowConstraintTextView(constraintString))

        return layout
    }


    private fun rowsConstraintViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()
        var format                  = listWidget.format().constraintFormat().textFormat()

        listWidget.variable(entityId).apDo { variable ->
            variable.constraint().doMaybe {
                if (!it.matchesValue(EngineTextListValue(this.itemStrings()), entityId)) {
                    format = listWidget.format().constraintFormat().failTextFormat()
                }
            }
        }

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor      = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        layout.paddingSpacing       = format.elementFormat().padding()
        layout.marginSpacing        = format.elementFormat().margins()

        return layout.linearLayout(context)
    }

    private fun rowConstraintTextView(constraintString : String) : TextView
    {
        val constraintView              = TextViewBuilder()

        var format                  = listWidget.format().constraintFormat().textFormat()

        listWidget.variable(entityId).apDo { variable ->
            variable.constraint().doMaybe {
                if (!it.matchesValue(EngineTextListValue(this.itemStrings()), entityId)) {
                    format = listWidget.format().constraintFormat().failTextFormat()
                }
            }
        }

        constraintView.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        constraintView.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        constraintView.text             = constraintString



//        listWidget.variable(entityId).apDo { variable ->
//            variable.constraint().doMaybe {
//                when (it) {
//                    is TextListConstraintMaxSize -> {
//                        SheetData.number(it.maxSize, entityId).apDo {
//                            it.doMaybe {
//
//                            }
//                        }
//                    }
//                }
//            }
//        }

        constraintView.color             = colorOrBlack(format.colorTheme(), entityId)

        constraintView.sizeSp            = format.sizeSp()

        constraintView.font              = Font.typeface(format.font(),
                                                     format.fontStyle(),
                                                     context)

        return constraintView.textView(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW > Pool
    // -----------------------------------------------------------------------------------------

    private fun poolView() : LinearLayout
    {
        val layout          = this.poolViewLayout()

        layout.addView(this.titleBarView(ListWidgetEditType.Advanced))

        layout.addView(this.poolItemsView())

        return layout
    }


    private fun poolViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = listWidget.format().listFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.paddingSpacing   = format.padding()
        layout.marginSpacing    = format.margins()

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        return layout.linearLayout(context)
    }



    private fun poolItemsView() : FlexboxLayout
    {
        val layout = this.poolItemsViewLayout()

        listWidget.variable(entityId) apDo { variable ->
            variable.constraint().doMaybe { constraint ->
            constraint.constraintOfType(ConstraintTypeTextListMaxSize).doMaybe {
                if (it is TextListConstraintMaxSize)
                {
                    SheetData.number(it.maxSize, entityId).apDo {
                        it.doMaybe {
                            val values = variable.values(entityId)

                            Log.d("***LIST WIDGET", "values: $values ")

                            values.forEach {
                                layout.addView(this.poolItemActiveView(it))
                            }

                            val maxSize = it.toInt()
                            val inactiveItems = maxSize - values.size

                            for (i in 1..inactiveItems) {
                                layout.addView(this.poolItemInactiveView())
                            }

                        }
                    }
                }
            } }
        }

        return layout
    }


    private fun poolItemsViewLayout() : FlexboxLayout
    {
        val layout              = FlexboxLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

//        when (format.widgetFormat().elementFormat().alignment()) {
//            is Alignment.Center -> layout.justification = JustifyContent.CENTER
//        }

        layout.direction        = FlexDirection.ROW
        layout.wrap             = FlexWrap.WRAP

//        when (format.widgetFormat().elementFormat().verticalAlignment()) {
//            is VerticalAlignment.Middle -> layout.itemAlignment = AlignItems.CENTER
//        }

        return layout.flexboxLayout(context)

    }


    private fun poolItemActiveView(value : Value) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val label               = TextViewBuilder()

        val format              = listWidget.format().itemFormat()

        // (2) Declarations
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.FLEXBOX

        val width = format.elementFormat().width()
        when (width) {
            is Width.Fixed -> layout.widthDp = width.value.toInt()
            else           -> layout.width = FlexboxLayout.LayoutParams.WRAP_CONTENT
        }

        val height = format.elementFormat().height()
        when (height) {
            is Height.Fixed -> layout.heightDp = height.value.toInt()
            else            -> layout.height = FlexboxLayout.LayoutParams.WRAP_CONTENT
        }

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

        layout.corners          = format.elementFormat().corners()

        layout.gravity          = Gravity.CENTER

        layout.paddingSpacing    = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        layout.onClick          = View.OnClickListener {
            this.openListEditDialog(value.valueId().value)
        }

        layout.child(label)

        // (2) Declarations
        // -------------------------------------------------------------------------------------

        label.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text         = value.valueString()

        format.styleTextViewBuilder(label, entityId, context)

        return layout.linearLayout(context)
    }


    private fun poolItemInactiveView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val label               = TextViewBuilder()

        val format              = listWidget.format().itemInactiveFormat()

        // (2) Declarations
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.FLEXBOX

        val width = format.elementFormat().width()
        when (width) {
            is Width.Fixed -> layout.widthDp = width.value.toInt()
            else           -> layout.width = FlexboxLayout.LayoutParams.WRAP_CONTENT
        }

        val height = format.elementFormat().height()
        when (height) {
            is Height.Fixed -> layout.heightDp = height.value.toInt()
            else            -> layout.height = FlexboxLayout.LayoutParams.WRAP_CONTENT
        }

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

        layout.corners          = format.elementFormat().corners()

        layout.gravity          = Gravity.CENTER

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        layout.onClick          = View.OnClickListener {
            this.openListEditDialog()
        }

        layout.child(label)

        // (2) Declarations
        // -------------------------------------------------------------------------------------

        label.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text         = listWidget.format().defaultItemText().value

        format.styleTextViewBuilder(label, entityId, context)

        return layout.linearLayout(context)
    }



}



