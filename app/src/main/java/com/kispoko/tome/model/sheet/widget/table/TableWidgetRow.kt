
package com.kispoko.tome.model.sheet.widget.table


import android.content.Context
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import com.kispoko.tome.R.string.cell
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.TableRowBuilder
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.widget.TableWidgetFormat
import com.kispoko.tome.rts.sheet.CellTypeDoesNotMatchColumnType
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Table Widget Row
 */
data class TableWidgetRow(override val id : UUID,
                          val format : Comp<TableWidgetRowFormat>,
                          val cells : Coll<TableWidgetCell>) : SheetComponent, Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : TableWidgetRowFormat, cells : MutableList<TableWidgetCell>)
        : this(UUID.randomUUID(), Comp(format), Coll(cells))


    companion object : Factory<TableWidgetRow>
    {

        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetRow> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetRow,
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TableWidgetRowFormat.default),
                               { TableWidgetRowFormat.fromDocument(it) }),
                         // Cells
                         doc.list("cells") ap { docList ->
                             docList.mapMut { TableWidgetCell.Companion.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TableWidgetRowFormat = this.format.value

    fun cells() : List<TableWidgetCell> = this.cells.list


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(columns : List<TableWidgetColumn>,
             format : TableWidgetFormat,
             sheetContext : SheetContext) : View
    {
        val tableRow = TableRowBuilder()

        tableRow.layoutType     = LayoutType.TABLE
        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.height         = TableLayout.LayoutParams.WRAP_CONTENT

        tableRow.paddingSpacing = format.widgetFormat().padding()


        this.cells().forEachIndexed { i, tableWidgetCell ->
            when (tableWidgetCell)
            {
                is TableWidgetBooleanCell ->
                {
                    val column = columns[i]
                    when (column)
                    {
                        is TableWidgetBooleanColumn ->
                            tableRow.rows.add(tableWidgetCell.view(this.format(),
                                                                   column,
                                                                   sheetContext))
                        else -> ApplicationLog.error(
                                    CellTypeDoesNotMatchColumnType(TableWidgetCellType.BOOLEAN,
                                                                   column.type()))
                    }
                }
                is TableWidgetNumberCell ->
                {
                    val column = columns[i]
                    when (column)
                    {
                        is TableWidgetNumberColumn ->
                            tableRow.rows.add(tableWidgetCell.view(this.format(),
                                                                   column,
                                                                   sheetContext))
                        else -> ApplicationLog.error(
                                    CellTypeDoesNotMatchColumnType(TableWidgetCellType.NUMBER,
                                                                   column.type()))
                    }
                }
                is TableWidgetTextCell ->
                {
                    val column = columns[i]
                    when (column)
                    {
                        is TableWidgetTextColumn ->
                            tableRow.rows.add(tableWidgetCell.view(this.format(),
                                                                   column,
                                                                   sheetContext))
                        else -> ApplicationLog.error(
                                    CellTypeDoesNotMatchColumnType(TableWidgetCellType.TEXT,
                                                                   column.type()))
                    }
                }
            }
        }

        return tableRow.tableRow(sheetContext.context)
    }

}


/**
 * Table Widget Row Format
 */
data class TableWidgetRowFormat(override val id : UUID,
                                val cellHeight : Prim<Height>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(cellHeight : Height)
        : this(UUID.randomUUID(), Prim(cellHeight))


    companion object : Factory<TableWidgetRowFormat>
    {

        private val defaultCellHeight = Height.MediumSmall

        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetRowFormat> = when (doc)
        {
            is DocDict -> effApply(::TableWidgetRowFormat,
                                   // Cell Height
                                   split(doc.maybeAt("cell_height"),
                                         effValue<ValueError,Height>(defaultCellHeight),
                                         { Height.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : TableWidgetRowFormat = TableWidgetRowFormat(defaultCellHeight)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun cellHeight() : Height = this.cellHeight.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}




//
//
//
//
//
//    // > Widget Container
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Set the container namespace.
//     * @param namespace The namespace.
//     */
//    @Override
//    public void setNamespace(Namespace namespace)
//    {
//        this.namespace = namespace;
//
//        // > Update all namespaced variables
//        for (Variable variable : this.namespacedVariables)
//        {
//            //String newName = this.namespace + "." + variable.name();
//            variable.setNamespace(this.namespace);
//        }
//    }
//
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    public void initialize(List<ColumnUnion> columns,
//                           TableWidgetFormat tableFormat,
//                           UUID tableWidgetId)
//    {
//        // [1] Apply default row/cell height
//        // --------------------------------------------------------------------------------------
//        if (tableFormat.cellHeight() != null && this.format().cellHeight() == null)
//            this.format().setCellHeight(tableFormat.cellHeight());
//
//        // [1] Initialize the cells
//        // --------------------------------------------------------------------------------------
//        for (int i = 0; i < this.width(); i++)
//        {
//            CellUnion   cell = this.cellAtIndex(i);
//            ColumnUnion column = columns.get(i);
//
//            switch (cell.type())
//            {
//                case TEXT:
//                    cell.textCell().initialize(column.textColumn(), this, tableWidgetId);
//                    break;
//                case NUMBER:
//                    cell.numberCell().initialize(column.numberColumn(), tableWidgetId);
//                    break;
//                case BOOLEAN:
//                    cell.booleanCell().initialize(column.booleanColumn(), tableWidgetId);
//                    break;
//            }
//        }
//
//        // [2] Configure namespaces
//        // --------------------------------------------------------------------------------------
//
//        this.namespace              = null;
//
//        // > Index each namespaced variable
//        // --------------------------------------------------------------------------------------
//
//        this.namespacedVariables = new ArrayList<>();
//        for (CellUnion cellUnion : this.cells()) {
//            List<Variable> variables = cellUnion.cell().namespacedVariables();
//            this.namespacedVariables.addAll(variables);
//        }
//
//        // > Set the namespace if one is found
//        // --------------------------------------------------------------------------------------
//
//        for (CellUnion cellUnion : this.cells())
//        {
//            if (cellUnion.type() == CellType.TEXT)
//            {
//                TextCell textCell = cellUnion.textCell();
//                if (textCell.valueVariable().definesNamespace())
//                {
//                    try {
//                        this.setNamespace(textCell.valueVariable().namespace());
//                    }
//                    catch (NullVariableException exception) {
//
//                    }
//                }
//            }
//        }
//
//    }

