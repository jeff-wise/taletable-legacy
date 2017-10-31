
package com.kispoko.tome.model.sheet.widget.table


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.MotionEvent
import android.widget.TableLayout
import android.widget.TableRow
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.SheetActivityGlobal
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.variable.VariableNamespace
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Table Widget Row
 */
data class TableWidgetRow(override val id : UUID,
                          val format : Comp<TableWidgetRowFormat>,
                          val cells : Coll<TableWidgetCell>)
                           : ToDocument, SheetComponent, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var namespace : VariableNamespace? = null

    var viewId : Int? = null
    var backgroundColor : Int? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name    = "format"
        this.cells.name     = "cells"

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(cells : MutableList<TableWidgetCell>)
        : this(UUID.randomUUID(),
               Comp(TableWidgetRowFormat.default()),
               Coll(cells))


    constructor(format : TableWidgetRowFormat,
                cells : MutableList<TableWidgetCell>)
        : this(UUID.randomUUID(),
               Comp(format),
               Coll(cells))


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
                         })
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

    fun format() : TableWidgetRowFormat = this.format.value

    fun cells() : List<TableWidgetCell> = this.cells.list


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

    override val name : String = "table_widget_row"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext) {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun openEditor(tableWidget : TableWidget,
                   rowIndex : Int,
                   sheetUIContext : SheetUIContext)
    {
        Log.d("***TABLEWIDGETROW", "opening editor")
        val sheetActivity = sheetUIContext.context as SheetActivity
        val updateTarget = UpdateTargetInsertTableRow(tableWidget)
        tableWidget.selectedRow = rowIndex
        this.addHighlight(sheetUIContext)
        sheetActivity.showTableEditor(this, updateTarget, SheetContext(sheetUIContext))
    }


    fun onEditorClose(sheetUIContext : SheetUIContext)
    {
        Log.d("***TABLEWIDGETROW", "on editor close")
        this.removeHighlight(sheetUIContext)
    }


    fun addHighlight(sheetUIContext : SheetUIContext)
    {
        val viewId = this.viewId
        if (viewId != null)
        {
            val activity = sheetUIContext.context as SheetActivity

            val tableRow = activity.findViewById(viewId) as TableRow?

            val bgDrawable = GradientDrawable()

            val color = SheetManager.color(sheetUIContext.sheetId,
                                           this.format().backgroundColorTheme())

            bgDrawable.setColor(this.backgroundColor ?: color)


            val strokeColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_red_5")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
            val strokeColor = SheetManager.color(sheetUIContext.sheetId, strokeColorTheme)
            bgDrawable.setStroke(1, strokeColor)

            tableRow?.background = bgDrawable
        }
    }


    fun removeHighlight(sheetUIContext : SheetUIContext)
    {
        val viewId = this.viewId
        if (viewId != null)
        {
            val activity = sheetUIContext.context as SheetActivity

            val tableRow = activity.findViewById(viewId) as TableRow?

            val bgDrawable = GradientDrawable()

            val color = SheetManager.color(sheetUIContext.sheetId,
                                           this.format().backgroundColorTheme())

            bgDrawable.setColor(this.backgroundColor ?: color)
            bgDrawable.setStroke(0, Color.WHITE)

            tableRow?.background = bgDrawable
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(tableWidget : TableWidget,
             rowIndex : Int,
             sheetUIContext : SheetUIContext) : TableRow
    {
        val tableRow = TableRowWidgetView(this, tableWidget, rowIndex, sheetUIContext)

        val layoutParams = TableLayout.LayoutParams()
        layoutParams.width  = TableLayout.LayoutParams.MATCH_PARENT
        layoutParams.height  = TableLayout.LayoutParams.WRAP_CONTENT

        val margins = tableWidget.format().rowFormat().margins()
        layoutParams.leftMargin = margins.leftPx()
        layoutParams.rightMargin = margins.rightPx()
        layoutParams.topMargin = margins.topPx()
        layoutParams.bottomMargin = margins.bottomPx()

        tableRow.layoutParams = layoutParams

        val viewId = Util.generateViewId()
        this.viewId = viewId
        tableRow.id = viewId

        val padding = tableWidget.format().rowFormat().padding()
        tableRow.setPadding(padding.leftPx(),
                            padding.topPx(),
                            padding.rightPx(),
                            padding.bottomPx())

        val bgColor = SheetManager.color(sheetUIContext.sheetId,
                                         tableWidget.format().rowFormat().backgroundColorTheme())
        tableRow.setBackgroundColor(bgColor)
        this.backgroundColor = bgColor

//        val tableRow                = TableRowBuilder()
//
//        val viewId = Util.generateViewId()
//        this.viewId = viewId
//        tableRow.id                 = viewId
//
//        tableRow.layoutType         = LayoutType.TABLE
//        tableRow.width              = TableLayout.LayoutParams.MATCH_PARENT
//        tableRow.height             = TableLayout.LayoutParams.WRAP_CONTENT
//
//        tableRow.marginSpacing      = tableWidget.format().rowFormat().margins()
//        tableRow.paddingSpacing     = tableWidget.format().rowFormat().padding()
//
//        tableRow.backgroundColor    = SheetManager.color(sheetUIContext.sheetId,
//                tableWidget.format().rowFormat().backgroundColorTheme())
//
//        this.backgroundColor = tableRow.backgroundColor


//        tableRow.onClick            = View.OnClickListener {
//            val sheetActivity = sheetUIContext.context as SheetActivity
//            val tableRowAction = SheetAction.TableRow(tableWidget.id,
//                                                      rowIndex,
//                                                      tableWidget.tableNameString(),
//                                                      tableWidget.columns())
////            sheetActivity.showActionBar(tableRowAction, SheetContext(sheetUIContext))
//        }

//
//        tableRow.setOnLongClickListener {
//            true
//        }


//         val sheetActivity = sheetUIContext.context as SheetActivity
//
//        val gd = GestureDetectorCompat(sheetActivity,
//            object: GestureDetector.SimpleOnGestureListener() {
//
////                override fun onDown(e: MotionEvent?): Boolean {
////                    Log.d("***WIDGET", "on down table row")
////                    return super.onDown(e)
////                }
//
//                override fun onLongPress(e: MotionEvent?) {
//                    val updateTarget = UpdateTargetInsertTableRow(tableWidget)
//                    Log.d("***TABLEWIDGETROW", "on long press")
//                    tableWidget.selectedRow = rowIndex
//                    toggleHighlight(sheetUIContext)
//                    sheetActivity.showTableEditor(updateTarget, SheetContext(sheetUIContext))
//                }
//
////                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
////                    return super.onSingleTapConfirmed(e)
////                }
//            })
//
//
//        tableRow.setOnTouchListener { _, event ->
//            gd.onTouchEvent(event)
//            false
//        }


        this.cells().forEachIndexed { i, tableWidgetCell ->
            when (tableWidgetCell)
            {
                is TableWidgetBooleanCell ->
                {
                    val column = tableWidget.columns()[i]
                    when (column)
                    {
                        is TableWidgetBooleanColumn ->
                            tableRow.addView(tableWidgetCell.view(this.format(),
                                                                   column,
                                    sheetUIContext))
                        else -> ApplicationLog.error(
                                    CellTypeDoesNotMatchColumnType(TableWidgetCellType.BOOLEAN,
                                                                   column.type()))
                    }
                }
                is TableWidgetNumberCell ->
                {
                    val column = tableWidget.columns()[i]
                    when (column)
                    {
                        is TableWidgetNumberColumn ->
                            tableRow.addView(tableWidgetCell.view(this,
                                                                   column,
                                                                   rowIndex,
                                                                   tableWidget,
                                                                   sheetUIContext))
                        else -> ApplicationLog.error(
                                    CellTypeDoesNotMatchColumnType(TableWidgetCellType.NUMBER,
                                                                   column.type()))
                    }
                }
                is TableWidgetTextCell ->
                {
                    val column = tableWidget.columns()[i]
                    when (column)
                    {
                        is TableWidgetTextColumn ->
                            tableRow.addView(tableWidgetCell.view(this.format(),
                                                                   column,
                                                                   rowIndex,
                                                                   tableWidget,
                                                                   sheetUIContext))
                        else -> ApplicationLog.error(
                                    CellTypeDoesNotMatchColumnType(TableWidgetCellType.TEXT,
                                                                   column.type()))
                    }
                }
            }
        }

        return tableRow //.tableRow(sheetUIContext.context)
    }

}


class TableRowWidgetView(val tableWidgetRow : TableWidgetRow,
                         val tableWidget : TableWidget,
                         val rowIndex : Int,
                         val sheetUIContext : SheetUIContext) : TableRow(sheetUIContext.context)
{


    var clickTime : Long = 0
    var CLICK_DURATION = 500


    override fun onInterceptTouchEvent(ev: MotionEvent?) : Boolean
    {
        if (ev != null)
        {
            Log.d("***TABLEWIDGETROW", ev.action.toString())
            when (ev.action)
            {
                MotionEvent.ACTION_DOWN ->
                {
                    clickTime = System.currentTimeMillis()
                    SheetActivityGlobal.setLongPressRunnable(Runnable {
                        tableWidgetRow.openEditor(tableWidget, rowIndex, sheetUIContext)
                    })
                    Log.d("***TABLEROW", "action down")
                }
                MotionEvent.ACTION_UP ->
                {
                    SheetActivityGlobal.cancelLongPressRunnable()
                    Log.d("***TABLEROW", "action up")
//                    val upTime = System.currentTimeMillis()
//                    if ((upTime - clickTime) > CLICK_DURATION) {
//                        tableWidgetRow.openEditor(tableWidget, rowIndex, sheetUIContext)
//                        Log.d("***TABLEROW", "on long click")
//                    }
                }
                MotionEvent.ACTION_OUTSIDE ->
                {
                    //SheetActivityGlobal.touchHandler.removeCallbacks(runnable)
                    SheetActivityGlobal.cancelLongPressRunnable()
                }
                MotionEvent.ACTION_SCROLL ->
                {
                    SheetActivityGlobal.cancelLongPressRunnable()
                }
                MotionEvent.ACTION_CANCEL ->
                {
                    //SheetActivityGlobal.touchHandler.removeCallbacks(runnable)
                    SheetActivityGlobal.cancelLongPressRunnable()
                }
            }
        }
        return false
    }


}


/**
 * Table Widget Row Format
 */
data class TableWidgetRowFormat(override val id : UUID,
                                val textStyle : Comp<TextStyle>,
                                val backgroundColorTheme : Prim<ColorTheme>,
                                val margins : Comp<Spacing>,
                                val padding : Comp<Spacing>,
                                val cellHeight : Prim<Height>)
                                 : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.textStyle.name             = "text_style"
        this.backgroundColorTheme.name  = "background_color_theme"
        this.margins.name               = "margins"
        this.padding.name               = "padding"
        this.cellHeight.name            = "cell_height"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textStyle : TextStyle,
                backgroundColorTheme : ColorTheme,
                margins : Spacing,
                padding : Spacing,
                cellHeight : Height)
        : this(UUID.randomUUID(),
               Comp(textStyle),
               Prim(backgroundColorTheme),
               Comp(margins),
               Comp(padding),
               Prim(cellHeight))


    companion object : Factory<TableWidgetRowFormat>
    {

        private val defaultTextStyle            = TextStyle.default()
        private val defaultBackgroundColorTheme = ColorTheme.transparent
        private val defaultMargins              = Spacing.default()
        private val defaultPadding              = Spacing.default()
        private val defaultCellHeight           = Height.Wrap


        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetRowFormat> = when (doc)
        {
            is DocDict -> effApply(::TableWidgetRowFormat,
                                   // Text Style
                                   split(doc.maybeAt("text_style"),
                                         effValue(defaultTextStyle),
                                         { TextStyle.fromDocument(it) }),
                                   // Background Color Theme
                                   split(doc.maybeAt("background_color_theme"),
                                         effValue(defaultBackgroundColorTheme),
                                         { ColorTheme.fromDocument(it) }),
                                   // Margins
                                   split(doc.maybeAt("margins"),
                                         effValue(defaultMargins),
                                         { Spacing.fromDocument(it) }),
                                   // Padding
                                   split(doc.maybeAt("padding"),
                                         effValue(defaultPadding),
                                         { Spacing.fromDocument(it) }),
                                   // Cell Height
                                   split(doc.maybeAt("cell_height"),
                                         effValue<ValueError,Height>(defaultCellHeight),
                                         { Height.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TableWidgetRowFormat(defaultTextStyle,
                                             defaultBackgroundColorTheme,
                                             defaultMargins,
                                             defaultPadding,
                                             defaultCellHeight)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "text_style" to this.textStyle().toDocument(),
        "background_color_theme" to this.backgroundColorTheme().toDocument(),
        "margins" to this.margins().toDocument(),
        "padding" to this.padding().toDocument(),
        "cell_height" to this.cellHeight().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun cellHeight() : Height = this.cellHeight.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value

    fun margins() : Spacing = this.margins.value

    fun padding() : Spacing = this.padding.value

    fun textStyle() : TextStyle = this.textStyle.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_row_format"

    override val modelObject = this

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

