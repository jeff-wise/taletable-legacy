
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.activity.sheet.dialog.openTextListVariableEditorDialog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.variable.TextListVariable
import com.taletable.android.model.sheet.style.BorderEdge
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.table.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.UpdateTargetTableWidget
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
 * Table Widget Format
 */
data class TableWidgetFormat(val widgetFormat : WidgetFormat,
                             val viewType : TableWidgetViewType,
                             val headerFormat : TableWidgetRowFormat,
                             val rowFormat : TableWidgetRowFormat,
                             val titleBarFormat : ElementFormat,
                             val titleFormat : TextFormat,
                             val editButtonFormat : TextFormat,
                             val divider : Maybe<BorderEdge>)
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
                            { TextFormat.fromDocument(it) }),
                      // Divider
                      split(doc.maybeAt("divider"),
                            effValue<ValueError,Maybe<BorderEdge>>(Nothing()),
                            { apply(::Just, BorderEdge.fromDocument(it)) })
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
                                          defaultEditButtonFormat(),
                                          Nothing())

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


    fun divider() : Maybe<BorderEdge> = this.divider

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


    object FixedFirstColumn : TableWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "fixed_first_column" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("fixed_first_column")

    }


    object RowsOnly : TableWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "rows_only" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("rows_only")

    }


    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "tile_bar"          -> effValue<ValueError,TableWidgetViewType>(
                                                TableWidgetViewType.TitleBar)
                "fixed_first_column" -> effValue<ValueError,TableWidgetViewType>(
                                                TableWidgetViewType.FixedFirstColumn)
                "rows_only"         -> effValue<ValueError,TableWidgetViewType>(
                                            TableWidgetViewType.RowsOnly)
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



    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun openSubsetEditor(variable : TextListVariable)
    {
        val updateTarget = UpdateTargetTableWidget(tableWidget.widgetId())
        openTextListVariableEditorDialog(variable,
                                         updateTarget,
                                         tableWidget.editorOptions(),
                                         entityId,
                                         context)
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
        val layout = WidgetView.layout(tableWidget.format().widgetFormat(), entityId, context)

        val layoutId = Util.generateViewId()
        layout.id = layoutId
        tableWidget.layoutId = layoutId

        this.updateView(layout)

        return layout
    }


    fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)

        contentLayout.removeAllViews()

        contentLayout.addView(this.mainView())
    }


    private fun mainView() : View
    {
        val layout = this.mainLayout()


        when (tableWidget.format().viewType()) {
            is TableWidgetViewType.TitleBar -> {
                layout.addView(this.titleBarView())
                layout.addView(this.tableView())
            }
            is TableWidgetViewType.FixedFirstColumn -> layout.addView(this.tableFixedColumnView())
            is TableWidgetViewType.RowsOnly -> {
                layout.addView(this.tableNoHeaderView())
            }
        }


        return layout
    }


    private fun mainLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun tableView() : TableLayout
    {
        val layout = this.tableLayout()

        if (tableWidget.cachedRows().isNotEmpty())
        {
            val headerRowView = this.headerRowView(tableWidget.columns(),
                                               tableWidget.format(),
                                               entityId,
                                               context)
            layout.addView(headerRowView)
            this.headerRowView = headerRowView
        }
        else
        {
            layout.addView(this.dummyRowView())
        }

        tableWidget.cachedRows().forEach { tableWidgetRow ->
            val tableRowView = tableWidgetRow.view(tableWidget,
                                                   entityId,
                                                   context)
            layout.addView(tableRowView)
            this.tableRowViews.add(tableRowView)
        }

        return layout
    }


    private fun tableNoHeaderView() : TableLayout
    {
        val layout = this.tableLayout()

        tableWidget.cachedRows().forEach { tableWidgetRow ->
            val tableRowView = tableWidgetRow.view(tableWidget,
                                                   entityId,
                                                   context)
            layout.addView(tableRowView)
            this.tableRowViews.add(tableRowView)
        }

        return layout
    }



    private fun tableLayout() : TableLayout
    {
        val layout = TableLayoutBuilder()
        val format = tableWidget.format()

        val tableLayoutId = Util.generateViewId()
        layout.id = tableLayoutId
        tableWidget.tableLayoutId = tableLayoutId

        layout.layoutType           = LayoutType.LINEAR
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.shrinkAllColumns     = true

        layout.backgroundColor      = colorOrBlack(
                                            format.widgetFormat().elementFormat().backgroundColorTheme(),
                                            entityId)

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

                dividerDrawable?.colorFilter =
                        PorterDuffColorFilter(dividerColor, PorterDuff.Mode.SRC_IN)
                layout.divider = dividerDrawable
            }
        }

        format.divider().doMaybe { divider ->
            val dividerDrawable = ContextCompat.getDrawable(context,
                                                            R.drawable.table_row_divider)

            val dividerColor = colorOrBlack(divider.colorTheme(), entityId)

            dividerDrawable?.colorFilter =
                    PorterDuffColorFilter(dividerColor, PorterDuff.Mode.SRC_IN)
            layout.divider = dividerDrawable
        }

        return layout.tableLayout(context)
    }




    private fun headerRowView(columns : List<TableWidgetColumn>,
                              format : TableWidgetFormat,
                              entityId : EntityId,
                              context : Context) : TableRow
    {
        Log.d("***TABLE WIDGET", "header row view columns: ${columns.size}")
        val tableRow = TableRowBuilder()

        tableRow.layoutType     = LayoutType.TABLE
        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT
        //tableRow.width          = TableLayout.LayoutParams.WRAP_CONTENT
        tableRow.height         = TableLayout.LayoutParams.WRAP_CONTENT

        tableRow.paddingSpacing = format.headerFormat().textFormat().elementFormat().padding()
        tableRow.marginSpacing  = format.headerFormat().textFormat().elementFormat().margins()

        tableRow.backgroundColor    = colorOrBlack(
                                        format.headerFormat().textFormat().elementFormat().backgroundColorTheme(),
                                        entityId)

//        tableRow.rows.add(editRowButtonView(true, format.rowFormat().textFormat().elementFormat(), entityId, context))

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


    private fun dummyRowView() : TableRow
    {
        val tableRow = TableRowBuilder()

        val rowFormat = tableWidget.format().rowFormat()

        tableRow.layoutType     = LayoutType.TABLE
        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.height         = TableLayout.LayoutParams.WRAP_CONTENT

        tableRow.paddingSpacing = rowFormat.textFormat().elementFormat().padding()
        tableRow.marginSpacing  = rowFormat.textFormat().elementFormat().margins()

        tableRow.backgroundColor    = colorOrBlack(
                                        rowFormat.textFormat().elementFormat().backgroundColorTheme(),
                                        entityId)


        tableWidget.columns().firstOrNull()?.let { firstColumn ->

            val cellView = this.dummyCellView(rowFormat,
                                              firstColumn.columnFormat(),
                                              "None",
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


    private fun dummyCellView(rowFormat : TableWidgetRowFormat,
                              columnFormat : ColumnFormat,
                              text : String,
                              entityId : EntityId,
                              context : Context) : LinearLayout
    {
        val layout = TableWidgetCellView.layout(columnFormat,
                                                entityId,
                                                context)

        val textView = TextViewBuilder()

        textView.layoutType     = LayoutType.TABLE_ROW
        textView.width          = TableRow.LayoutParams.WRAP_CONTENT
        textView.height         = TableRow.LayoutParams.WRAP_CONTENT

        textView.text           = text

        columnFormat.textFormat().styleTextViewBuilder(textView, entityId, context)

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
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

//        layout.padding.leftDp   = 10f
//        layout.padding.rightDp  = 5f

        layout.addRule(RelativeLayout.CENTER_VERTICAL)
        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.onClick          = View.OnClickListener {
            val rowSetVariable = tableWidget.rowSetVariable(entityId)
            when (rowSetVariable)
            {
                is Val -> this.openSubsetEditor(rowSetVariable.value)
                is Err -> this.toggleEditMode()
            }
        }

        return layout.linearLayout(context)
    }


    private fun editButtonTextView() : TextView
    {
        val label               = TextViewBuilder()

        val format              = tableWidget.format().editButtonFormat()


        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.backgroundColor   = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        label.corners           = format.elementFormat().corners()

        label.paddingSpacing    = format.elementFormat().padding()

        label.textId            = R.string.edit_table

        format.styleTextViewBuilder(label, entityId, context)

        return label.textView(context)
    }


    private fun tableFixedColumnView() : HorizontalScrollView
    {
        val layout = this.tableFixedColumnViewLayout()

        val firstColLayout  = this.tableFirstColumnLayout()

        val scrollView = this.tableOtherColumnsScrollView()
        val otherColsLayout = this.tableOtherColumnsLayout()

        //layout.addView(otherColsLayout)
        scrollView.addView(firstColLayout)

        //layout.addView(firstColLayout)
        //layout.addView(scrollView)

        if (tableWidget.cachedRows().isNotEmpty())
        {
            val numberOfCols = tableWidget.columns().size
            if (numberOfCols > 0)
            {
                val headerRowView1 = this.headerRowView(tableWidget.columns().take(1),
                                                        tableWidget.format(),
                                                        entityId,
                                                        context)

                val headerRowView2 = this.headerRowView(tableWidget.columns().drop(1),
                                                        tableWidget.format(),
                                                        entityId,
                                                        context)
                firstColLayout.addView(headerRowView1)
                otherColsLayout.addView(headerRowView2)
            }
        }
//        else
//        {
//            layout.addView(this.dummyRowView())
//        }

        val otherColIndices = mutableListOf<Int>()
        for (i in 1 until tableWidget.columns().size) {
            otherColIndices.add(i)
        }

        Log.d("***TABLE WIDGET", "other indices: ${otherColIndices}")

        tableWidget.cachedRows().forEach { tableWidgetRow ->


            val tableRowView1 = tableWidgetRow.view(tableWidget,
                                                    entityId,
                                                    context, listOf(0))

            val tableRowView2 = tableWidgetRow.view(tableWidget,
                                                    entityId,
                                                    context,
                                                    otherColIndices)

            firstColLayout.addView(tableRowView1)
            otherColsLayout.addView(tableRowView2)
        }

        //return layout
        //return otherColsLayout
        return scrollView
    }


    private fun tableFixedColumnViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }


    private fun tableFirstColumnLayout() : TableLayout
    {
        val layout = TableLayoutBuilder()
        val format = tableWidget.format()

        val tableLayoutId = Util.generateViewId()
        layout.id = tableLayoutId
        tableWidget.tableLayoutId = tableLayoutId

        layout.layoutType           = LayoutType.FRAME
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.heightDp             = 200
        layout.height               = FrameLayout.LayoutParams.WRAP_CONTENT
        layout.widthDp              = 100
        //layout.weight               = 1f
        //layout.shrinkAllColumns     = true

        layout.backgroundColor      = colorOrBlack(
                                            format.widgetFormat().elementFormat().backgroundColorTheme(),
                                            entityId)

        return layout.tableLayout(context)
    }


    private fun tableOtherColumnsScrollView() : HorizontalScrollView
    {
        val scrollView          = HorizontalScrollView(context)

        scrollView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        return scrollView
    }

    private fun tableOtherColumnsLayout() : TableLayout
    {
        val layout                  = TableLayoutBuilder()
        val format                  = tableWidget.format()

        val tableLayoutId = Util.generateViewId()
        layout.id = tableLayoutId
        tableWidget.tableLayoutId   = tableLayoutId

        layout.layoutType           = LayoutType.FRAME
        layout.width                = FrameLayout.LayoutParams.MATCH_PARENT
        layout.height               = FrameLayout.LayoutParams.WRAP_CONTENT

//        layout.layoutType           = LayoutType.LINEAR
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
        //layout.shrinkAllColumns     = true

        layout.backgroundColor      = colorOrBlack(
                                            format.widgetFormat().elementFormat().backgroundColorTheme(),
                                            entityId)

        return layout.tableLayout(context)
    }


}
