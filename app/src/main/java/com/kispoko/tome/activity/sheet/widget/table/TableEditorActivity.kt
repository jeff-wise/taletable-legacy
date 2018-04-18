
package com.kispoko.tome.activity.sheet.widget.table


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.table.TableWidgetColumn
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.textVariable
import com.kispoko.tome.rts.entity.variable
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val
import maybe.Just
import java.util.*



/**
 * Table Editor Activity
 */
class TableEditorActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var tableWidget : TableWidget? = null
    private var entityId : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_table_editor)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("table_widget"))
            this.tableWidget = this.intent.getSerializableExtra("table_widget") as TableWidget

        if (this.intent.hasExtra("entity_id"))
            this.entityId = this.intent.getSerializableExtra("entity_id") as EntityId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // Toolbar
        val titleVariableId = tableWidget?.titleVariableId()?.toNullable()
        val entityId = this.entityId


        if (titleVariableId != null && entityId != null) {
            textVariable(titleVariableId, entityId) apDo {
            it.valueString(entityId) apDo {
                this.configureToolbar(it)
            } }
        }
        else {
            this.configureToolbar(getString(R.string.table_editor))
        }

        // Theme
        this.applyTheme(officialThemeLight)

        // Entity Kind List
        this.initializeContentView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeContentView()
    {

        val tableWidget = this.tableWidget
        val entityId = this.entityId
        if (tableWidget != null && entityId != null)
        {
            val tableEditorUI = TableEditorUI(tableWidget,
                                              officialThemeLight,
                                              entityId,
                                              this)

            val content = this.findViewById<LinearLayout>(R.id.content)
            content?.addView(tableEditorUI.view())
        }

    }


    private fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }


        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_back_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }

}



class TableEditorUI(val tableWidget : TableWidget,
                    val theme : Theme,
                    val entityId : EntityId,
                    val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.tableView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
    }


    // VIEWS > Table View
    // -----------------------------------------------------------------------------------------

    private fun tableView() : HorizontalScrollView
    {
        val scrollView = HorizontalScrollView(context)

        val table = FancyTable(context)

        table.orientation = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        table.setBackgroundColor(colorOrBlack(bgColorTheme, entityId))

        table.addView(this.tableHeaderView())

        table.addView(this.tableBodyView())

        scrollView.addView(table)

        return scrollView
    }


    private fun tableViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()


        return layout.linearLayout(context)
    }

    // VIEWS > Table Sidebar
    // -----------------------------------------------------------------------------------------

//    private fun tableSiderbarView() : LinearLayout
//    {
//
//    }
//
//
//    private fun tableSidebarViewLayout() : LinearLayout
//    {
//
//    }
//
    // VIEWS > Table Header
    // -----------------------------------------------------------------------------------------

    private fun tableHeaderView() : TableLayout
    {
        val headerLayout = this.tableHeaderViewLayout()

        headerLayout.addView(this.tableHeaderRowView())

        return headerLayout
    }


    private fun tableHeaderViewLayout() : TableLayout
    {
        val table                   = TableLayoutBuilder()

        table.id                    = R.id.table_header

        table.layoutType            = LayoutType.LINEAR
        table.width                 = LinearLayout.LayoutParams.MATCH_PARENT
        table.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        table.shrinkAllColumns      = true

//        table.backgroundColor      = colorOrBlack(
//                                            format.widgetFormat().elementFormat().backgroundColorTheme(),
//                                            entityId)

        return table.tableLayout(context)
    }


    private fun tableHeaderRowView() : TableRow
    {
        val tableRow = TableRowBuilder()

        tableRow.layoutType     = LayoutType.TABLE

        tableRow.width          = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.heightDp       = 70

//        tableRow.paddingSpacing = format.headerFormat().textFormat().elementFormat().padding()
//        tableRow.marginSpacing  = format.headerFormat().textFormat().elementFormat().margins()

        tableWidget.columns().forEach { column ->

            val cellView = this.headerCellView(column)
            tableRow.rows.add(cellView)
        }

//        tableRow.padding.topDp      = 10f
//        tableRow.padding.bottomDp   = 10f

        return tableRow.tableRow(context)
    }


    private fun headerCellView(column : TableWidgetColumn) : TextView
    {
        val label               = TextViewBuilder()

        label.layoutType        = LayoutType.TABLE_ROW
        label.width             = TableRow.LayoutParams.WRAP_CONTENT
        label.heightDp          = 42

        label.text              = column.nameString()

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        label.backgroundColor   = Color.WHITE

        label.margin.rightDp    = 1f

        label.gravity           = Gravity.CENTER_VERTICAL

        label.padding.leftDp    = 12f
        label.padding.rightDp   = 12f

        label.sizeSp            = 15f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color         = colorOrBlack(colorTheme, entityId)

        return label.textView(context)
    }


    private fun headerPlaceholderCellView(column : TableWidgetColumn) : TextView
    {
        val label               = TextViewBuilder()

        label.layoutType        = LayoutType.TABLE_ROW
        label.width             = TableRow.LayoutParams.WRAP_CONTENT
        label.heightDp          = 42

        label.text              = column.nameString()

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        label.backgroundColor   = Color.WHITE

        label.margin.rightDp    = 1f

        label.gravity           = Gravity.CENTER_VERTICAL

        label.padding.leftDp    = 12f
        label.padding.rightDp   = 12f

        label.sizeSp            = 15f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color         = colorOrBlack(colorTheme, entityId)

        return label.textView(context)
    }



    // VIEWS > Table Body
    // -----------------------------------------------------------------------------------------

    private fun tableBodyView() : LinearLayout
    {
        val layout = this.tableBodyViewLayout()

        tableWidget.rows().forEach { row ->

            val tableRow = this.tableBodyRowView()

            row.cells().forEach { cell ->
                val cellValueString = cell.variableIdOrError() ap {
                                        variable(it, entityId)   ap {
                                            it.valueString(entityId)
                                        } }

                when (cellValueString) {
                    is Val -> {
                        tableRow.addView(this.tableBodyCellView(cellValueString.value))
                    }
                    is Err -> {
                        tableRow.addView(this.tableBodyCellView("Blank"))
                    }
                }
            }

            layout.addView(tableRow)
        }

        return layout
    }


    private fun tableBodyViewLayout() : TableLayout
    {
        val table                   = TableLayoutBuilder()

        table.id                    = R.id.table_body

        table.layoutType            = LayoutType.LINEAR
        table.width                 = LinearLayout.LayoutParams.MATCH_PARENT
        table.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        table.shrinkAllColumns      = true

//        table.backgroundColor      = colorOrBlack(
//                                            format.widgetFormat().elementFormat().backgroundColorTheme(),
//                                            entityId)

        return table.tableLayout(context)
    }


    private fun tableBodyRowView() : TableRow
    {
        val tableRow                = TableRowBuilder()

        tableRow.layoutType         = LayoutType.TABLE

        tableRow.width              = TableLayout.LayoutParams.MATCH_PARENT
        tableRow.heightDp           = 100

//        tableRow.backgroundColor    = Color.WHITE

//        tableRow.padding.topDp      = 10f
//        tableRow.padding.bottomDp   = 10f

        tableRow.margin.topDp       = 1f

        return tableRow.tableRow(context)
    }


    private fun tableBodyCellView(valueString : String) : TextView
    {
        val label               = TextViewBuilder()

        label.layoutType        = LayoutType.TABLE_ROW
        label.width             = TableRow.LayoutParams.WRAP_CONTENT
        label.heightDp          = 50

        label.text              = valueString

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        label.padding.leftDp    = 12f
        label.padding.rightDp   = 12f

        label.margin.rightDp    = 1f

        label.gravity           = Gravity.CENTER_VERTICAL

        label.backgroundColor   = Color.WHITE

        label.sizeSp            = 17f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color         = colorOrBlack(colorTheme, entityId)

        return label.textView(context)
    }
}


class FancyTable : LinearLayout
{
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int)
    {
        super.onLayout(changed, l, t, r, b)

        val headerColWidths = mutableMapOf<Int,Int>()
        val colWidths = mutableMapOf<Int,Int>()

        val header = findViewById<TableLayout>(R.id.table_header)
        val body = findViewById<TableLayout>(R.id.table_body)

        if (header != null && body != null)
        {

            if (header.childCount >= 1)
            {
                val headerRow = header.getChildAt(0) as TableRow
                for (headerCellIndex in 0 until headerRow.childCount) {
                    val cellView = headerRow.getChildAt(headerCellIndex)
                    headerColWidths[headerCellIndex] = cellView.width
                }
            }

            if (body.childCount >= 1)
            {
                val firstRow = body.getChildAt(0) as TableRow
                for (cellIndex in 0 until firstRow.childCount) {
                    val cellView = firstRow.getChildAt(cellIndex)
                    Log.d("***TABLE EDITOR ACT", "cell width: ${cellView.width}")

                    val cellViewWidth = cellView.width
                    val headerWidth = headerColWidths[cellIndex]
                    if (headerWidth != null && cellViewWidth > headerWidth)
                        colWidths[cellIndex] = cellView.width
                    else if (headerWidth != null)
                        colWidths[cellIndex] = headerWidth
                    else
                        colWidths[cellIndex] = 0
                }

            }

            if (header.childCount >= 1)
            {
                val headerRow = header.getChildAt(0) as TableRow
                for (headerCellIndex in 0 until headerRow.childCount) {
                    val cellView = headerRow.getChildAt(headerCellIndex)
                    val params = cellView.layoutParams as TableRow.LayoutParams
                    colWidths[headerCellIndex]?.let {
                        params.width = it
                        cellView.layoutParams = params
                    }
                }
            }

            for (bodyRowIndex in 0 until body.childCount) {
                val row = body.getChildAt(bodyRowIndex) as TableRow
                for (cellIndex in 0 until row.childCount) {
                    val cellView = row.getChildAt(cellIndex)
                    val params = cellView.layoutParams as TableRow.LayoutParams
                    colWidths[cellIndex]?.let {
                        params.width = it
                        cellView.layoutParams = params
                    }
                }
            }


//            for (rownum in 0 until body.childCount) {
//                val row = body.getChildAt(rownum) as TableRow
//                for (cellnum in 0 until row.getChildCount()) {
//                    val cell = row.getChildAt(cellnum)
//                    val cellWidth = cell.getWidth()
//                    if (colWidths.size <= cellnum) {
//                        colWidths.add(cellWidth)
//                    } else {
//                        val current = colWidths.get(cellnum)
//                        if (cellWidth > current) {
//                            colWidths.removeAt(cellnum)
//                            colWidths.add(cellnum, cellWidth)
//                        }
//                    }
//                }
//            }
//


        }

    }

}

