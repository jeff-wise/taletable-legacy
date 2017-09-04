
package com.kispoko.tome.model.sheet.widget


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import com.kispoko.tome.R
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
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
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Table Widget Format
 */
data class TableWidgetFormat(override val id : UUID,
                             val widgetFormat : Comp<WidgetFormat>,
                             val headerFormat : Comp<TableWidgetRowFormat>,
                             val rowFormat : Comp<TableWidgetRowFormat>,
                             val showDivider : Prim<ShowTableDividers>,
                             val dividerColorTheme : Prim<ColorTheme>,
                             val cellHeight : Prim<Height>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.headerFormat.name      = "header_format"
        this.rowFormat.name         = "row_format"
        this.showDivider.name       = "show_divider"
        this.dividerColorTheme.name = "divider_color_theme"
        this.cellHeight.name        = "cell_height"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                headerFormat : TableWidgetRowFormat,
                rowFormat : TableWidgetRowFormat,
                showDivider : ShowTableDividers,
                dividerColorTheme : ColorTheme,
                cellHeight : Height)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(headerFormat),
               Comp(rowFormat),
               Prim(showDivider),
               Prim(dividerColorTheme),
               Prim(cellHeight))


    companion object : Factory<TableWidgetFormat>
    {

        private val defaultWidgetFormat      = WidgetFormat.default()
        private val defaultHeaderFormat      = TableWidgetRowFormat.default()
        private val defaultRowFormat         = TableWidgetRowFormat.default()
        private val defaultShowDivider       = ShowTableDividers(false)
        private val defaultDividerColorTheme = ColorTheme.black
        private val defaultCellHeight        = Height.Wrap


        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::TableWidgetFormat,
                                   // Widget Format
                                   split(doc.maybeAt("widget_format"),
                                         effValue(defaultWidgetFormat),
                                         { WidgetFormat.fromDocument(it) }),
                                   // Header Format
                                   split(doc.maybeAt("header_format"),
                                         effValue(defaultHeaderFormat),
                                         { TableWidgetRowFormat.fromDocument(it) }),
                                   // Row Format
                                   split(doc.maybeAt("row_format"),
                                         effValue(defaultRowFormat),
                                         { TableWidgetRowFormat.fromDocument(it) }),
                                   // Show Divider
                                   split(doc.maybeAt("show_divider"),
                                         effValue(defaultShowDivider),
                                         { ShowTableDividers.fromDocument(it) }),
                                   // Divider Color
                                   split(doc.maybeAt("divider_color"),
                                         effValue(defaultDividerColorTheme),
                                         { ColorTheme.fromDocument(it) }),
                                   // Height
                                   split(doc.maybeAt("height"),
                                         effValue<ValueError,Height>(defaultCellHeight),
                                         { Height.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : TableWidgetFormat =
                TableWidgetFormat(defaultWidgetFormat,
                                  defaultHeaderFormat,
                                  defaultRowFormat,
                                  defaultShowDivider,
                                  defaultDividerColorTheme,
                                  defaultCellHeight)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun headerFormat() : TableWidgetRowFormat = this.headerFormat.value

    fun rowFormat() : TableWidgetRowFormat = this.rowFormat.value

    fun showDivider() : Boolean = this.showDivider.value.value

    fun dividerColorTheme() : ColorTheme = this.dividerColorTheme.value

    fun cellHeight() : Height = this.cellHeight.value


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_format"

    override val modelObject = this

}


/**
 * Table Widget Name
 */
data class TableWidgetName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetName> = when (doc)
        {
            is DocText -> effValue(TableWidgetName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Show Table Dividers
 */
data class ShowTableDividers(val value : Boolean) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Table Sort
 */
data class TableSort(val columnIndex : Int,
                     val sortOrder : TableSortOrder) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ columnIndex.toString() + " " +  sortOrder })

}


sealed class TableSortOrder : SQLSerializable, Serializable
{

    object Asc : TableSortOrder()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "asc" })
    }

    object Desc : TableSortOrder()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "desc" })
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
             sheetUIContext : SheetUIContext) : View
    {
        val layout = WidgetView.layout(format.widgetFormat(), sheetUIContext)

        val tableLayout = this.tableLayout(format, sheetUIContext)
        val tableLayoutId = Util.generateViewId()
        tableLayout.id = tableLayoutId
        tableWidget.tableLayoutId = tableLayoutId

        layout.addView(tableLayout)

        tableLayout.addView(this.headerRowView(tableWidget.columns(),
                                               tableWidget.format(),
                                               sheetUIContext))

        tableWidget.rows().forEachIndexed { rowIndex, tableWidgetRow ->
            tableLayout.addView(tableWidgetRow.view(tableWidget,
                                                    rowIndex,
                                                    sheetUIContext))
        }

        return layout
    }


    private fun tableLayout(format : TableWidgetFormat,
                            sheetUIContext : SheetUIContext) : TableLayout
    {
        val layout = TableLayoutBuilder()

        layout.layoutType           = LayoutType.LINEAR
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.shrinkAllColumns     = true

        layout.backgroundColor      = SheetManager.color(
                                            sheetUIContext.sheetId,
                                            format.widgetFormat().backgroundColorTheme())

        // Divider
        // -------------------------------------------------------------------------------------

        if (format.showDivider())
        {
            val dividerDrawable = ContextCompat.getDrawable(sheetUIContext.context,
                                                            R.drawable.table_row_divider)

            val dividerColor = SheetManager.color(sheetUIContext.sheetId,
                                                  format.dividerColorTheme())

            dividerDrawable.colorFilter =
                    PorterDuffColorFilter(dividerColor, PorterDuff.Mode.SRC_IN)
            layout.divider = dividerDrawable
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


        return layout.tableLayout(sheetUIContext.context)
    }


    private fun headerRowView(columns : List<TableWidgetColumn>,
                              format : TableWidgetFormat,
                              sheetUIContext: SheetUIContext) : TableRow
    {
        val tableRow = TableRowBuilder()

        tableRow.layoutType     = LayoutType.TABLE
        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.height         = TableLayout.LayoutParams.WRAP_CONTENT

        tableRow.paddingSpacing = format.headerFormat().padding()
        tableRow.marginSpacing  = format.headerFormat().margins()

        tableRow.backgroundColor    = SheetManager.color(sheetUIContext.sheetId,
                                        format.headerFormat().backgroundColorTheme())

        columns.forEach { column ->

            val cellView = this.headerCellView(format.headerFormat(),
                                               column,
                                               CellFormat.default,
                    sheetUIContext)
            tableRow.rows.add(cellView)
        }

        return tableRow.tableRow(sheetUIContext.context)
    }


    private fun headerCellView(rowFormat : TableWidgetRowFormat,
                               column : TableWidgetColumn,
                               cellFormat : CellFormat,
                               sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = TableWidgetCellView.layout(rowFormat,
                                                column.columnFormat(),
                                                cellFormat,
                sheetUIContext)

        val textView = TextViewBuilder()

        textView.layoutType     = LayoutType.TABLE_ROW
        textView.width          = TableRow.LayoutParams.WRAP_CONTENT
        textView.height         = TableRow.LayoutParams.WRAP_CONTENT

        textView.text           = column.nameString()

        rowFormat.textStyle().styleTextViewBuilder(textView, sheetUIContext)

        layout.addView(textView.textView(sheetUIContext.context))

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
//            TextStyle headerCellStyle = new TextStyle(UUID.randomUUID(),
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

