
package com.taletable.android.model.sheet.widget.table


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.activity.sheet.dialog.TableDialog
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LayoutType
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TableRowBuilder
import com.taletable.android.model.engine.variable.VariableNamespace
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.TableWidget
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Maybe
import java.io.Serializable


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

    override fun onSheetComponentActive(entityId : EntityId, context : Context, groupContext : Maybe<GroupContext>) {
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
        val sheetActivity = context as SessionActivity
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
             context : Context,
             columnIndices : List<Int> = listOf()) : TableRow
    {
        var colIndices : MutableList<Int> = mutableListOf()

        if (columnIndices.isEmpty()) {
            for (i in 0 until tableWidget.columns().size) { colIndices.add(i) }
        } else {
            colIndices = columnIndices.toMutableList()
        }


        val rowUI = TableWidgetRowUI(this, tableWidget, colIndices, entityId, context)
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
                       val columnIndices : List<Int>,
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

            if (columnIndices.contains(columnIndex))
            {
                val column = tableWidget.columns().getOrNull(columnIndex)

                when (cell)
                {
                    is TableWidgetBooleanCell -> this.addBooleanCell(cell, tableRow, column)
                    is TableWidgetImageCell   -> this.addImageCell(cell, tableRow, column)
                    is TableWidgetNumberCell  -> this.addNumberCell(cell, tableRow, column)
                    is TableWidgetTextCell    -> this.addTextCell(cell, tableRow, column)
                }
            }
        }

        return tableRow
    }


    private fun rowView() : TableRow
    {
        val tableRow                = TableRowBuilder()
        val rowFormat               = tableWidget.format().rowFormat()

        //tableRow.layoutType         = LayoutType.TABLE
        tableRow.width              = TableLayout.LayoutParams.MATCH_PARENT
        //tableRow.width              = TableLayout.LayoutParams.WRAP_CONTENT
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
data class TableWidgetRowFormat(val textFormat : TextFormat)
                                 : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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

