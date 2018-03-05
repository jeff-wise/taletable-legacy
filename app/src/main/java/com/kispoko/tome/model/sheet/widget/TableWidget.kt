
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import com.kispoko.tome.R
import com.kispoko.tome.db.DB_WidgetTableFormatValue
import com.kispoko.tome.db.widgetTableFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.TableLayoutBuilder
import com.kispoko.tome.lib.ui.TableRowBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Height
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
import java.util.*



/**
 * Table Widget Format
 */
data class TableWidgetFormat(override val id : UUID,
                             val widgetFormat : WidgetFormat,
                             val headerFormat : TableWidgetRowFormat,
                             val rowFormat : TableWidgetRowFormat,
                             val cellHeight : Height)
                              : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                headerFormat : TableWidgetRowFormat,
                rowFormat : TableWidgetRowFormat,
                cellHeight : Height)
        : this(UUID.randomUUID(),
               widgetFormat,
               headerFormat,
               rowFormat,
               cellHeight)


    companion object : Factory<TableWidgetFormat>
    {

        private fun defaultWidgetFormat()      = WidgetFormat.default()
        private fun defaultHeaderFormat()      = TableWidgetRowFormat.default()
        private fun defaultRowFormat()         = TableWidgetRowFormat.default()
        private fun defaultCellHeight()        = Height.Wrap


        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Header Format
                      split(doc.maybeAt("header_format"),
                            effValue(defaultHeaderFormat()),
                            { TableWidgetRowFormat.fromDocument(it) }),
                      // Row Format
                      split(doc.maybeAt("row_format"),
                            effValue(defaultRowFormat()),
                            { TableWidgetRowFormat.fromDocument(it) }),
                      // Height
                      split(doc.maybeAt("height"),
                            effValue<ValueError,Height>(defaultCellHeight()),
                            { Height.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TableWidgetFormat(defaultWidgetFormat(),
                                          defaultHeaderFormat(),
                                          defaultRowFormat(),
                                          defaultCellHeight())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "header_format" to this.headerFormat().toDocument(),
        "row_format" to this.rowFormat().toDocument(),
        "height" to this.cellHeight().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun headerFormat() : TableWidgetRowFormat = this.headerFormat


    fun rowFormat() : TableWidgetRowFormat = this.rowFormat


    fun cellHeight() : Height = this.cellHeight


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableFormatValue =
        RowValue4(widgetTableFormatTable,
                  ProdValue(this.widgetFormat),
                  ProdValue(this.headerFormat),
                  ProdValue(this.rowFormat),
                  PrimValue(this.cellHeight))

}


/**
 * Table Widget Name
 */
data class TableWidgetName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetName> = when (doc)
        {
            is DocText -> effValue(TableWidgetName(doc.text))
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
 * Show Table Dividers
 */
data class ShowTableDividers(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowTableDividers>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ShowTableDividers> = when (doc)
        {
            is DocBoolean -> effValue(ShowTableDividers(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })

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


object TableWidgetView
{


    fun view(tableWidget : TableWidget,
             format : TableWidgetFormat,
             entityId : EntityId,
             context : Context) : View
    {
        val layout = WidgetView.widgetTouchLayout(format.widgetFormat(), entityId, context)

        val tableLayout = this.tableLayout(format, entityId, context)
        val tableLayoutId = Util.generateViewId()
        tableLayout.id = tableLayoutId
        tableWidget.tableLayoutId = tableLayoutId

        layout.addView(tableLayout)

        tableLayout.addView(this.headerRowView(tableWidget.columns(),
                                               tableWidget.format(),
                                               entityId,
                                               context))

        tableWidget.rows().forEachIndexed { rowIndex, tableWidgetRow ->
            tableLayout.addView(tableWidgetRow.view(tableWidget,
                                                    rowIndex,
                                                    entityId,
                                                    context))
        }

        return layout
    }


    private fun tableLayout(format : TableWidgetFormat,
                            entityId : EntityId,
                            context : Context) : TableLayout
    {
        val layout = TableLayoutBuilder()

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

        columns.forEach { column ->

            val cellView = this.headerCellView(format.headerFormat(),
                                               column,
                                               entityId,
                                               context)
            tableRow.rows.add(cellView)
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



//    private LinearLayout widgetLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation         = LinearLayout.VERTICAL;
//        layout.width               = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height              = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//

//
//    private android.widget.TableRow headerTableRow(Context context)
//    {
//
//        TableRowBuilder headerRow = new TableRowBuilder();
//
//        headerRow.width          = android.widget.TableRow.LayoutParams.MATCH_PARENT;
//        headerRow.height         = android.widget.TableRow.LayoutParams.WRAP_CONTENT;
//        headerRow.padding.left   = R.dimen.widget_table_row_padding_horz;
//        headerRow.padding.right  = R.dimen.widget_table_row_padding_horz;
//
//        android.widget.TableRow headerRowView = headerRow.tableRow(context);
//
//
//        for (int i = 0; i < this.width(); i++)
//        {
//            CellUnion headerCell = this.headerRow.cellAtIndex(i);
//
//            Column column = this.columnAtIndex(i).column();
//            TextColumnFormat format = new TextColumnFormat(UUID.randomUUID(),
//                                                           null,
//                                                           column.alignment(),
//                                                           column.width(),
//                                                           null);
//            TextColumn textColumn = new TextColumn(null, null, null, null, format,
//                                                   false, false);
//            ColumnUnion columnUnion = ColumnUnion.asText(null, textColumn);
//
//            LinearLayout headerCellView =
//                    (LinearLayout) headerCell.view(columnUnion, this.headerRow.format(), context);
//
//            headerRowView.addView(headerCellView);
//        }
//
//        return headerRowView;
//    }



}


//
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//

//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeTableWidget()
//    {
//        // [1] Apply default formats
//        // -------------------------------------------------------------------------------------
//
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//
//        // [2] The header row is derived from the column information, so create it each time the
//        //     table widget is instantiated
//        // -------------------------------------------------------------------------------------
//
//        List<CellUnion> headerCells = new ArrayList<>();
//
//        for (ColumnUnion columnUnion : this.columns())
//        {
//            TextVariable headerCellValue =
//                    TextVariable.asText(UUID.randomUUID(),
//                                        columnUnion.column().name().toUpperCase());
//            TextFormat headerCellStyle = new TextFormat(UUID.randomUUID(),
//                                                      TextColor.THEME_DARK,
//                                                      TextSize.SUPER_SMALL);
//
//            TextCellFormat format = new TextCellFormat(UUID.randomUUID(),
//                                                       columnUnion.column().alignment(),
//                                                       BackgroundColor.NONE,
//                                                       headerCellStyle);
//            TextCell headerCell = new TextCell(UUID.randomUUID(),
//                                               headerCellValue,
//                                               format);
//            CellUnion headerCellUnion = CellUnion.asText(null, headerCell);
//            headerCells.add(headerCellUnion);
//        }
//
//        TableRowFormat headerRowFormat = new TableRowFormat(UUID.randomUUID(), null);
//
//        this.headerRow = new TableRow(null, headerCells, headerRowFormat);
//
//        // [3] Index all of the cells in the table
//        // -------------------------------------------------------------------------------------
//
//        this.cellById = new HashMap<>();
//
//        for (TableRow row : this.rows()) {
//            for (CellUnion cellUnion : row.cells()) {
//                this.cellById.put(cellUnion.getId(), cellUnion);
//            }
//        }
//    }
//

