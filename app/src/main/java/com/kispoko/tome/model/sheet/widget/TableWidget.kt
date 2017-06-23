
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
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
import com.kispoko.tome.model.sheet.DividerType
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Table Widget Format
 */
data class TableWidgetFormat(override val id : UUID,
                             val widgetFormat : Comp<WidgetFormat>,
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
        this.showDivider.name       = "show_divider"
        this.dividerColorTheme.name = "divider_color_theme"
        this.cellHeight.name        = "cell_height"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                showDivider : ShowTableDividers,
                dividerColorTheme : ColorTheme,
                cellHeight : Height)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Prim(showDivider),
               Prim(dividerColorTheme),
               Prim(cellHeight))


    companion object : Factory<TableWidgetFormat>
    {

        private val defaultWidgetFormat      = WidgetFormat.default()
        private val defaultShowDivider       = ShowTableDividers(false)
        private val defaultDividerColorTheme = ColorTheme.black
        private val defaultCellHeight        = Height.MediumSmall


        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::TableWidgetFormat,
                                   // Widget Format
                                   split(doc.maybeAt("widget_format"),
                                         effValue(defaultWidgetFormat),
                                         { WidgetFormat.fromDocument(it) }),
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
                                  defaultShowDivider,
                                  defaultDividerColorTheme,
                                  defaultCellHeight)
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

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
 * Show Table Dividers
 */
data class ShowTableDividers(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowTableDividers>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ShowTableDividers> = when (doc)
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



object TableWidgetView
{


    fun view(tableWidget : TableWidget,
             format : TableWidgetFormat,
             sheetContext : SheetContext) : View
    {
        val layout = WidgetView.layout(format.widgetFormat(), sheetContext.context)

        val tableLayout = this.tableLayout(format, sheetContext)

        layout.addView(tableLayout)

        tableLayout.addView(this.headerRowView(tableWidget.columns(),
                                               tableWidget.format(),
                                               sheetContext))

        for (row in tableWidget.rows())
        {
            tableLayout.addView(row.view(tableWidget.columns(),
                                         tableWidget.format(),
                                         sheetContext))
        }

        return layout
    }


    private fun tableLayout(format : TableWidgetFormat,
                            sheetContext: SheetContext) : TableLayout
    {
        val layout = TableLayoutBuilder()

        layout.layoutType           = LayoutType.LINEAR
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.shrinkAllColumns     = true

        layout.backgroundColor      = SheetManager.color(
                                            sheetContext.sheetId,
                                            format.widgetFormat().backgroundColorTheme())

        // Divider
        // -------------------------------------------------------------------------------------

        if (format.showDivider())
        {
            val dividerDrawable = ContextCompat.getDrawable(sheetContext.context,
                                                            R.drawable.table_row_divider)

            val dividerColor = SheetManager.color(sheetContext.sheetId,
                                                  format.dividerColorTheme())

            dividerDrawable.colorFilter =
                    PorterDuffColorFilter(dividerColor, PorterDuff.Mode.SRC_IN)
            layout.divider = dividerDrawable
        }


        return layout.tableLayout(sheetContext.context)
    }


    private fun headerRowView(columns : List<TableWidgetColumn>,
                              format : TableWidgetFormat,
                              sheetContext : SheetContext) : TableRow
    {
        val tableRow = TableRowBuilder()

        tableRow.layoutType     = LayoutType.TABLE
        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.height         = TableLayout.LayoutParams.WRAP_CONTENT

        tableRow.paddingSpacing = format.widgetFormat().padding()

        columns.forEach { column ->

            val cellView = this.headerCellView(TableWidgetRowFormat.default,
                                               column,
                                               CellFormat.default,
                                               sheetContext)
            tableRow.rows.add(cellView)
        }

        return tableRow.tableRow(sheetContext.context)
    }


    private fun headerCellView(rowFormat : TableWidgetRowFormat,
                               column : TableWidgetColumn,
                               cellFormat : CellFormat,
                               sheetContext : SheetContext) : LinearLayout
    {
        val layout = TableWidgetCellView.layout(rowFormat,
                                                column.columnFormat(),
                                                cellFormat,
                                                sheetContext)

        val textView = TextViewBuilder()

        textView.layoutType     = LayoutType.TABLE_ROW
        textView.width          = TableRow.LayoutParams.WRAP_CONTENT
        textView.height         = TableRow.LayoutParams.WRAP_CONTENT

        textView.text           = column.nameString()

        layout.addView(textView.textView(sheetContext.context))

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

