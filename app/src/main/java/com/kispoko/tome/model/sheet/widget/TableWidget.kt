
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.R.string.edit
import com.kispoko.tome.R.string.table
import com.kispoko.tome.activity.entity.book.BookActivity
import com.kispoko.tome.activity.sheet.widget.table.TableEditorActivity
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import java.io.Serializable



/**
 * Table Widget Format
 */
data class TableWidgetFormat(val widgetFormat : WidgetFormat,
                             val viewType : TableWidgetViewType,
                             val headerFormat : TableWidgetRowFormat,
                             val rowFormat : TableWidgetRowFormat,
                             val titleBarFormat : ElementFormat,
                             val titleFormat : TextFormat,
                             val editButtonFormat : TextFormat)
                              : ToDocument, Serializable
{

    companion object : Factory<TableWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = TableWidgetViewType.TitleBar
        private fun defaultHeaderFormat()       = TableWidgetRowFormat.default()
        private fun defaultRowFormat()          = TableWidgetRowFormat.default()
        private fun defaultTitleBarFormat()     = ElementFormat.default()
        private fun defaultTitleFormat()        = TextFormat.default()
        private fun defaultEditButtonFormat()   = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,TableWidgetViewType>(defaultViewType()),
                            { TableWidgetViewType.fromDocument(it) }),
                      // Header Format
                      split(doc.maybeAt("header_format"),
                            effValue(defaultHeaderFormat()),
                            { TableWidgetRowFormat.fromDocument(it) }),
                      // Row Format
                      split(doc.maybeAt("row_format"),
                            effValue(defaultRowFormat()),
                            { TableWidgetRowFormat.fromDocument(it) }),
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
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TableWidgetFormat(defaultWidgetFormat(),
                                          defaultViewType(),
                                          defaultHeaderFormat(),
                                          defaultRowFormat(),
                                          defaultTitleBarFormat(),
                                          defaultTitleFormat(),
                                          defaultEditButtonFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "header_format" to this.headerFormat().toDocument(),
        "row_format" to this.rowFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : TableWidgetViewType = this.viewType


    fun headerFormat() : TableWidgetRowFormat = this.headerFormat


    fun rowFormat() : TableWidgetRowFormat = this.rowFormat


    fun titleFormat() : TextFormat = this.titleFormat


    fun titleBarFormat() : ElementFormat = this.titleBarFormat


    fun editButtonFormat() : TextFormat = this.editButtonFormat

}


/**
 * Table Widget View Type
 */
sealed class TableWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object TitleBar : TableWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "title_bar" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("title_bar")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "tile_bar" -> effValue<ValueError,TableWidgetViewType>(
                                    TableWidgetViewType.TitleBar)
                else       -> effError<ValueError,TableWidgetViewType>(
                                    UnexpectedValue("TableWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Table Sort
 */
data class TableSort(val columnIndex : Int,
                     val sortOrder : TableSortOrder)
                      : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableSort>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TableSort> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableSort,
                         // Column Index
                         doc.int("column_index"),
                         // Sort Order
                         doc.at("sort_order") ap { TableSortOrder.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "column_index" to DocNumber(this.columnIndex.toDouble()),
        "sort_order" to this.sortOrder.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ columnIndex.toString() + " " +  sortOrder })

}


sealed class TableSortOrder : ToDocument, SQLSerializable, Serializable
{

    object Asc : TableSortOrder()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "asc" })

        override fun toDocument() = DocText("asc")
    }

    object Desc : TableSortOrder()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "desc" })

        override fun toDocument() = DocText("desc")
    }

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TableSortOrder> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "asc"  -> effValue<ValueError,TableSortOrder>(TableSortOrder.Asc)
                "desc" -> effValue<ValueError,TableSortOrder>(TableSortOrder.Desc)
                else   -> effError<ValueError,TableSortOrder>(
                                    UnexpectedValue("TableSortOrder", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


class TableWidgetUI(val tableWidget : TableWidget,
                    val entityId : EntityId,
                    val context : Context)
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var activity = context as AppCompatActivity

    private var editMode : Boolean = false

    private var editButtonTextView : TextView? = null

    private var tableRowViews : MutableList<TableRow> = mutableListOf()
    private var headerRowView : TableRow? = null


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
            this.tableRowViews.forEach { tableRowView ->

                val editButtonView = tableRowView.findViewById<LinearLayout>(R.id.table_row_edit_button)
                if (editButtonView != null)
                    editButtonView.visibility = View.VISIBLE

            }
            val headerRowEditButtonView = headerRowView?.findViewById<LinearLayout>(R.id.table_row_edit_button)
            if (headerRowEditButtonView != null)
                headerRowEditButtonView.visibility = View.VISIBLE
        }
        else
        {
            this.tableRowViews.forEach { tableRowView ->

                val editButtonView = tableRowView.findViewById<LinearLayout>(R.id.table_row_edit_button)
                if (editButtonView != null)
                    editButtonView.visibility = View.GONE

            }

            val headerRowEditButtonView = headerRowView?.findViewById<LinearLayout>(R.id.table_row_edit_button)
            if (headerRowEditButtonView != null)
                headerRowEditButtonView.visibility = View.GONE
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.widgetTouchLayout(tableWidget.format().widgetFormat(), entityId, context)

        val tableLayout = this.tableLayout()
        val tableLayoutId = Util.generateViewId()
        tableLayout.id = tableLayoutId
        tableWidget.tableLayoutId = tableLayoutId

        layout.addView(this.titleBarView())

        layout.addView(tableLayout)

        val headerRowView = this.headerRowView(tableWidget.columns(),
                                               tableWidget.format(),
                                               entityId,
                                               context)
        tableLayout.addView(headerRowView)
        this.headerRowView = headerRowView

        tableWidget.rows().forEachIndexed { rowIndex, tableWidgetRow ->
            val tableRowView = tableWidgetRow.view(tableWidget,
                                                    rowIndex,
                                                    entityId,
                                                    context)
            tableLayout.addView(tableRowView)
            this.tableRowViews.add(tableRowView)
        }

        return layout
    }


    private fun tableLayout() : TableLayout
    {
        val layout = TableLayoutBuilder()
        val format = tableWidget.format()

        layout.layoutType           = LayoutType.LINEAR
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.shrinkAllColumns     = true

        layout.backgroundColor      = colorOrBlack(
                                            format.widgetFormat().elementFormat().backgroundColorTheme(),
                                            entityId)

//        layout.onLongClick          = View.OnLongClickListener {
//
//            tableWidget.bookReference().doMaybe {
//                val intent = Intent(activity, BookActivity::class.java)
//                intent.putExtra("book_reference", it)
//                activity.startActivity(intent)
//            }
//
//            true
//        }

        // Divider
        // -------------------------------------------------------------------------------------

        val bottomBorder = format.rowFormat().textFormat().elementFormat().border().bottom()
        when (bottomBorder)
        {
            is Just ->
            {
                val dividerDrawable = ContextCompat.getDrawable(context,
                                                                R.drawable.table_row_divider)

                val dividerColor = colorOrBlack(bottomBorder.value.colorTheme(), entityId)

                dividerDrawable.colorFilter =
                        PorterDuffColorFilter(dividerColor, PorterDuff.Mode.SRC_IN)
                layout.divider = dividerDrawable
            }
        }

//
//        // On Long Click
//        layout.onLongClick = View.OnLongClickListener {
//            val sheetActivity = sheetUIContext.context as SheetActivity
//            val tableRowAction = SheetAction.TableRow()
//            sheetActivity.showActionBar(SheetContext(sheetUIContext))
//
//
//            true
//        }


        return layout.tableLayout(context)
    }


    private fun headerRowView(columns : List<TableWidgetColumn>,
                              format : TableWidgetFormat,
                              entityId : EntityId,
                              context : Context) : TableRow
    {
        val tableRow = TableRowBuilder()

        tableRow.layoutType     = LayoutType.TABLE
        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.height         = TableLayout.LayoutParams.WRAP_CONTENT

        tableRow.paddingSpacing = format.headerFormat().textFormat().elementFormat().padding()
        tableRow.marginSpacing  = format.headerFormat().textFormat().elementFormat().margins()

        tableRow.backgroundColor    = colorOrBlack(
                                        format.headerFormat().textFormat().elementFormat().backgroundColorTheme(),
                                        entityId)

        tableRow.rows.add(editRowButtonView(true, format.rowFormat().textFormat().elementFormat(), entityId, context))
        columns.forEach { column ->

            val cellView = this.headerCellView(format.headerFormat(),
                                               column,
                                               entityId,
                                               context)
            tableRow.rows.add(cellView)
        }

        tableRow.onLongClick        = View.OnLongClickListener {
            tableWidget.bookReference().doMaybe {
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("book_reference", it)
                activity.startActivity(intent)
            }

            true
        }

        return tableRow.tableRow(context)
    }


    private fun headerCellView(rowFormat : TableWidgetRowFormat,
                               column : TableWidgetColumn,
                               entityId : EntityId,
                               context : Context) : LinearLayout
    {
        val layout = TableWidgetCellView.layout(column.columnFormat(),
                                                entityId,
                                                context)

        val textView = TextViewBuilder()

        textView.layoutType     = LayoutType.TABLE_ROW
        textView.width          = TableRow.LayoutParams.WRAP_CONTENT
        textView.height         = TableRow.LayoutParams.WRAP_CONTENT

        textView.text           = column.nameString()

        rowFormat.textFormat().styleTextViewBuilder(textView, entityId, context)

        layout.addView(textView.textView(context))

        return layout
    }


    // VIEWS > Title Bar
    // -----------------------------------------------------------------------------------------

    private fun titleBarView() : View
    {
        val layout = this.titleBarViewLayout()

        val titleTextView = this.titleTextView()
        layout.addView(titleTextView)

        layout.addView(this.editButtonView())


        layout.setOnLongClickListener {
            tableWidget.bookReference().doMaybe {
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("book_reference", it)
                activity.startActivity(intent)
            }

            true
        }

        return layout
    }


    private fun titleBarViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()
        val format              = tableWidget.format().titleBarFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        layout.corners          = format.corners()

        return layout.relativeLayout(context)
    }


    private fun titleTextView() : TextView
    {
        val title               = TextViewBuilder()
        val format              = tableWidget.format().titleFormat()

        title.layoutType        = LayoutType.RELATIVE
        title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        title.addRule(RelativeLayout.ALIGN_PARENT_START)
        title.addRule(RelativeLayout.CENTER_VERTICAL)

        tableWidget.title(entityId).doMaybe { titleString ->
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


    private fun editButtonView() : LinearLayout
    {
        val layout      = this.editButtonViewLayout()

        val v = this.editButtonTextView()
        layout.addView(v)
        this.editButtonTextView = v

        return layout
    }


    private fun editButtonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.MATCH_PARENT

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 5f

        layout.addRule(RelativeLayout.CENTER_VERTICAL)
        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.onClick          = View.OnClickListener {
            this.toggleEditMode()
        }

        return layout.linearLayout(context)
    }


    private fun editButtonTextView() : TextView
    {
        val label               = TextViewBuilder()

        val format              = tableWidget.format().editButtonFormat()


        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.edit
//
//        label.color             = colorOrBlack(format.colorTheme(), entityId)
//
//        label.sizeSp            = format.sizeSp()
//
//        label.font              = Font.typeface(format.font(),
//                                                format.fontStyle(),
//                                                context)
//
        format.styleTextViewBuilder(label, entityId, context)

        return label.textView(context)
    }

}
