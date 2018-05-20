
package com.kispoko.tome.model.sheet.widget.table


import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import com.kispoko.tome.R
import com.kispoko.tome.R.string.cell
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.SheetActivityGlobal
import com.kispoko.tome.activity.sheet.dialog.TableDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue1
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TableRowBuilder
import com.kispoko.tome.model.engine.variable.VariableNamespace
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Table Widget Row
 */
data class TableWidgetRow(val format : TableWidgetRowFormat,
                          val cells : MutableList<TableWidgetCell>)
                           : ToDocument, SheetComponent, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var namespace : VariableNamespace? = null

    var viewId : Int? = null
    var backgroundColor : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(cells : MutableList<TableWidgetCell>)
        : this(TableWidgetRowFormat.default(),
               cells)


    companion object : Factory<TableWidgetRow>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetRow> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetRow,
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TableWidgetRowFormat.default()),
                               { TableWidgetRowFormat.fromDocument(it) }),
                         // Cells
                         doc.list("cells") ap { docList ->
                             docList.mapMut { TableWidgetCell.Companion.fromDocument(it) }
                         }
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format().toDocument(),
        "cells" to DocList(this.cells().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TableWidgetRowFormat = this.format


    fun cells() : List<TableWidgetCell> = this.cells


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context) {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun openEditor(tableWidget : TableWidget,
                   rowIndex : Int,
                   entityId : EntityId,
                   context : Context)
    {
        val sheetActivity = context as SheetActivity
        val updateTarget = UpdateTargetInsertTableRow(tableWidget)
        tableWidget.selectedRow = rowIndex

        val dialog = TableDialog.newInstance(updateTarget, entityId)
        dialog.show(sheetActivity.supportFragmentManager, "")
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(tableWidget : TableWidget,
             entityId : EntityId,
             context : Context) : TableRow
    {
        val rowUI = TableWidgetRowUI(this, tableWidget, entityId, context)
        return rowUI.view()
    }


//    fun view(tableWidget : TableWidget,
//             rowIndex : Int,
//             entityId : EntityId,
//             context : Context) : TableRow
//    {
//        val tableRow = TableRowWidgetView(this, tableWidget, rowIndex, entityId, context)
//
//        val layoutParams = TableLayout.LayoutParams()
//        layoutParams.width  = TableLayout.LayoutParams.MATCH_PARENT
//        layoutParams.height  = TableLayout.LayoutParams.WRAP_CONTENT
//
//        val margins = tableWidget.format().rowFormat().textFormat().elementFormat().margins()
//        layoutParams.leftMargin = margins.leftPx()
//        layoutParams.rightMargin = margins.rightPx()
//        layoutParams.topMargin = margins.topPx()
//        layoutParams.bottomMargin = margins.bottomPx()
//
//        tableRow.layoutParams = layoutParams
//
//        tableRow.gravity        = Gravity.CENTER_VERTICAL
//
//        val viewId = Util.generateViewId()
//        this.viewId = viewId
//        tableRow.id = viewId
//
//        val padding = tableWidget.format().rowFormat().textFormat().elementFormat().padding()
//        tableRow.setPadding(padding.leftPx(),
//                            padding.topPx(),
//                            padding.rightPx(),
//                            padding.bottomPx())
//
//        val bgColor = colorOrBlack(
//                          tableWidget.format().rowFormat().textFormat().elementFormat().backgroundColorTheme(),
//                          entityId)
//        tableRow.setBackgroundColor(bgColor)
//        this.backgroundColor = bgColor
//
//        val rowElementFormat = tableWidget.format().rowFormat().textFormat().elementFormat()
//        tableRow.addView(editRowButtonView(false, rowElementFormat, entityId, context))
//
//
//        return tableRow //.tableRow(sheetUIContext.context)
//    }



}



class TableWidgetRowUI(val row : TableWidgetRow,
                       val tableWidget : TableWidget,
                       val entityId : EntityId,
                       val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : TableRow
    {
        val tableRow = this.rowView()

        row.cells().forEachIndexed { columnIndex, cell ->

            val column = tableWidget.columns().getOrNull(columnIndex)

            when (cell)
            {
                is TableWidgetBooleanCell -> this.addBooleanCell(cell, tableRow, column)
                is TableWidgetImageCell   -> this.addImageCell(cell, tableRow, column)
                is TableWidgetNumberCell  -> this.addNumberCell(cell, tableRow, column)
                is TableWidgetTextCell    -> this.addTextCell(cell, tableRow, column)
            }
        }

        return tableRow
    }


    private fun rowView() : TableRow
    {
        val tableRow                = TableRowBuilder()
        val rowFormat               = tableWidget.format().rowFormat()

        tableRow.width              = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.height             = TableLayout.LayoutParams.MATCH_PARENT

        tableRow.marginSpacing      = rowFormat.textFormat().elementFormat().margins()
        tableRow.paddingSpacing     = rowFormat.textFormat().elementFormat().padding()

        tableRow.gravity            = Gravity.CENTER_VERTICAL

        tableRow.backgroundColor    = colorOrBlack(rowFormat.textFormat().elementFormat().backgroundColorTheme(), entityId)

        return tableRow.tableRow(context)
    }


    private fun addBooleanCell(cell : TableWidgetBooleanCell,
                               row : TableRow,
                               column : TableWidgetColumn?)
    {
        if (column != null)
        {
            when (column)
            {
                is TableWidgetBooleanColumn ->
                {
                    row.addView(cell.view(column, entityId, context))
                }
                else -> ApplicationLog.error(
                        CellTypeDoesNotMatchColumnType(TableWidgetCellType.BOOLEAN,
                                column.type()))
            }
        }
    }


    private fun addImageCell(cell : TableWidgetImageCell,
                             row : TableRow,
                             column : TableWidgetColumn?)
    {
        if (column != null)
        {
            when (column)
            {
                is TableWidgetImageColumn ->
                {
                    row.addView(cell.view(column, entityId, context))
                }
                else -> ApplicationLog.error(
                        CellTypeDoesNotMatchColumnType(TableWidgetCellType.IMAGE,
                                column.type()))
            }
        }
    }


    private fun addNumberCell(cell : TableWidgetNumberCell,
                              row : TableRow,
                              column : TableWidgetColumn?)
    {
        if (column != null)
        {
            when (column)
            {
                is TableWidgetNumberColumn ->
                {
                    row.addView(cell.view(column, tableWidget.widgetId(), entityId, context))
                }
                else -> ApplicationLog.error(
                        CellTypeDoesNotMatchColumnType(TableWidgetCellType.NUMBER,
                                column.type()))
            }
        }
    }


    private fun addTextCell(cell : TableWidgetTextCell,
                            row : TableRow,
                            column : TableWidgetColumn?)
    {
        if (column != null)
        {
            when (column)
            {
                is TableWidgetTextColumn ->
                {
                    row.addView(cell.view(column, tableWidget.widgetId(), entityId, context))
                }
                else -> ApplicationLog.error(
                        CellTypeDoesNotMatchColumnType(TableWidgetCellType.TEXT,
                                column.type()))
            }
        }
    }


    private fun editRowButtonView(isPlaceholder : Boolean) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.id               = R.id.table_row_edit_button

        layout.layoutType       = LayoutType.TABLE_ROW
        layout.widthDp          = 20
        layout.height           = TableRow.LayoutParams.WRAP_CONTENT

        layout.onClick          = View.OnClickListener {
        }

        layout.visibility       = View.GONE

        layout.gravity          = Gravity.CENTER_VERTICAL
        layout.layoutGravity    = Gravity.CENTER_VERTICAL

//        layout.margin.rightDp   = rowFormat.margins().rightDp()

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 18
        icon.heightDp           = 18

        if (!isPlaceholder)
            icon.image              = R.drawable.icon_vertical_ellipsis

        icon.layoutGravity      = Gravity.CENTER

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        icon.color              = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }



}



/**
 * Table Widget Row Format
 */
data class TableWidgetRowFormat(override val id : UUID,
                                val textFormat : TextFormat)
                                 : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textFormat : TextFormat)
        : this(UUID.randomUUID(),
               textFormat)


    companion object : Factory<TableWidgetRowFormat>
    {

        private fun defaultTextFormat()    = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetRowFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetRowFormat,
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue(defaultTextFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TableWidgetRowFormat(defaultTextFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "text_format" to this.textFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textFormat() : TextFormat = this.textFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableRowFormatValue =
        RowValue1(widgetTableRowFormatTable,
                  ProdValue(this.textFormat))


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

