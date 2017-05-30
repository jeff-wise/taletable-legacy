
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Table Widget Format
 */
data class TableWidgetFormat(override val id : UUID,
                             val widgetFormat : Func<WidgetFormat>,
                             val showDivider : Func<Boolean>,
                             val dividerColor : Func<ColorId>,
                             val cellHeight : Func<Height>) : Model
{
    companion object : Factory<TableWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::TableWidgetFormat,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Widget Format
                                   split(doc.maybeAt("widget_format"),
                                         valueResult<Func<WidgetFormat>>(Null()),
                                         fun(d : SpecDoc) : ValueParser<Func<WidgetFormat>> =
                                             effApply(::Comp, WidgetFormat.fromDocument(d))),
                                   // Show Divider
                                   split(doc.maybeBoolean("show_divider"),
                                         valueResult<Func<Boolean>>(Null()),
                                         { valueResult(Prim(it)) }),
                                   // Divider Color
                                   split(doc.maybeAt("divider_color"),
                                         valueResult<Func<ColorId>>(Null()),
                                         fun(d : SpecDoc) : ValueParser<Func<ColorId>> =
                                             effApply(::Prim, ColorId.fromDocument(d))),
                                   // Height
                                   split(doc.maybeEnum<Height>("height"),
                                         valueResult<Func<Height>>(Null()),
                                         { valueResult(Prim(it))  })
                                   )
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


//
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Create the tableWidget view.
//     * @return
//     */
//    @Override
//    public View view(boolean rowHasLabel, Context context)
//    {
//        // [1] Declarations
//        // --------------------------------------------------------------------------------------
//
//        LinearLayout widgetLayout  = this.widgetLayout(context);
//        TableLayout  tableLayout = this.tileTableLayout(context);
//
//        // [2] Structure
//        // --------------------------------------------------------------------------------------
//
//        widgetLayout.addView(tableLayout);
//
//        // > Header
//        // --------------------------------------------------------------------------------------
//
//        tableLayout.addView(this.headerTableRow(context));
//
//        // > Rows
//        // --------------------------------------------------------------------------------------
//
//        for (TableRow row : this.rows.getValue())
//        {
//            android.widget.TableRow tableRow = this.tableRow(row, context);
//            tableLayout.addView(tableRow);
//        }
//
//        return widgetLayout;
//    }
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
//
//    private android.widget.TableRow tableRow(TableRow row, Context context)
//    {
//        TableRowBuilder tableRow = new TableRowBuilder();
//
//        tableRow.layoutType     = LayoutType.TABLE;
//        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT;
//        tableRow.height         = TableLayout.LayoutParams.WRAP_CONTENT;
//
//        tableRow.padding.left   = R.dimen.widget_table_row_padding_horz;
//        tableRow.padding.right  = R.dimen.widget_table_row_padding_horz;
//
//        android.widget.TableRow tableRowView = tableRow.tableRow(context);
//
//        for (int i = 0; i < row.width(); i++)
//        {
//            CellUnion cell = row.cellAtIndex(i);
//            View cellView = cell.view(this.columnAtIndex(i), row.format(), context);
//            tableRowView.addView(cellView);
//        }
//
//        return tableRowView;
//    }
//
//
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
//    private TableLayout tileTableLayout(Context context)
//    {
//        TableLayoutBuilder layout = new TableLayoutBuilder();
//
//        layout.layoutType           = LayoutType.LINEAR;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.shrinkAllColumns     = true;
//
//        layout.backgroundColor      = this.data().format().background().colorId();
//
//        // > Set Divider
//        // -------------------------------------------------------------------------------------
//
//        if (this.format().dividerType() != DividerType.NONE)
//        {
//            Drawable dividerDrawable = ContextCompat.getDrawable(context,
//                                                                 R.drawable.table_row_divider);
//
//            BackgroundColor backgroundColor = this.data().format().background();
//            if (this.data().format().background() == BackgroundColor.NONE)
//                backgroundColor = this.groupParent.background();
//
//            int colorResourceId = this.format().dividerType()
//                                      .colorIdWithBackground(backgroundColor);
//            int      color      = ContextCompat.getColor(context, colorResourceId);
//
//            dividerDrawable.setColorFilter(
//                                new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
//
//            layout.divider = dividerDrawable;
//        }
//
//
//        return layout.tableLayout(context);
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


