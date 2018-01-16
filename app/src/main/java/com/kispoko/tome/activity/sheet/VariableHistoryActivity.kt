
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.variable.NumberVariable
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.util.SimpleDividerItemDecoration
import com.kispoko.tome.util.Util
import com.kispoko.tome.util.configureToolbar
import effect.Err
import maybe.Just
import effect.Val



/**
 * Variable History Activity
 */
class VariableHistoryActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var variable : Variable? = null
    private var sheetContext : SheetContext? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_variable_history)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("variable"))
            this.variable = this.intent.getSerializableExtra("variable") as Variable

        if (this.intent.hasExtra("sheet_context"))
            this.sheetContext = this.intent.getSerializableExtra("sheet_context") as SheetContext

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        val variable = this.variable
        if (variable != null) {
            val toolbarTitle = variable.label().value + " " + this.getString(R.string.history)
            this.configureToolbar(toolbarTitle)
        }

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // > Tab Views
        this.initializeViews()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeViews()
    {
        val contentView = this.findViewById(R.id.content) as LinearLayout

        val variable = this.variable
        val sheetContext = this.sheetContext
        if (variable != null && sheetContext != null)
        {
            val entryItems = variableEntryItems(variable, sheetContext)
            val viewBuilder = VariableHistoryViewBuilder(entryItems,
                                                         this.appSettings.themeId(),
                                                         this)
            contentView.addView(viewBuilder.view())
        }
    }


    private fun applyTheme(uiColors : UIColors)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = this.appSettings.color(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_back_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))
    }

}



// -----------------------------------------------------------------------------------------
// ENTRY ITEM
// -----------------------------------------------------------------------------------------

data class EntryItem(val value : String, val note : String?)


fun variableEntryItems(variable : Variable, sheetContext : SheetContext) : List<EntryItem> =
    when (variable)
    {
        is NumberVariable ->
        {
            variable.history().entries().mapNotNull {
                val valueEff = it.value().value(sheetContext)
                when (valueEff) {
                    is Val -> {
                        val value = valueEff.value
                        when (value) {
                            is Just -> EntryItem(Util.doubleString(value.value), it.description().toNullable()?.value)
                            else -> null
                        }
                    }
                    is Err -> {
                        ApplicationLog.error(valueEff.error)
                        null
                    }
                }
            }.reversed()
        }
        else -> listOf()
    }




// ---------------------------------------------------------------------------------------------
// HISTORY VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class VariableHistoryViewBuilder(val entryItems : List<EntryItem>,
                                 val themeId : ThemeId,
                                 val context : Context)
{

    init {

        Log.d("***VARHISTORY", "entry items size: " + entryItems.size.toString())
    }

    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.historyView())

        return layout
    }


    fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }


    // History
    // -----------------------------------------------------------------------------------------

    private fun historyView() : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = EntryRecyclerViewAdapter(entryItems, themeId)

        val dividerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val dividerColor = ThemeManager.color(themeId, dividerColorTheme)
        if (dividerColor != null)
            recyclerView.divider        = SimpleDividerItemDecoration(context, dividerColor)

        return recyclerView.recyclerView(context)
    }

}


// ---------------------------------------------------------------------------------------------
// ENTRY VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class EntryItemViewHolder(itemView : View,
                          val themeId : ThemeId,
                          val context : Context) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var valueView : TextView? = null
    var noteView : TextView? = null
    var iconView : LinearLayout? = null


    val lightFont = Font.typeface(TextFont.FiraSans,
                                  TextFontStyle.Light,
                                  context)

    val shortColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
    val shortColor = ThemeManager.color(themeId, shortColorTheme)

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.valueView = itemView.findViewById(R.id.variable_history_entry_value) as TextView
        this.noteView  = itemView.findViewById(R.id.variable_history_entry_note) as TextView
        this.iconView  = itemView.findViewById(R.id.variable_history_entry_icon) as LinearLayout
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setValueText(valueString : String)
    {
        this.valueView?.text = valueString

        if (valueString.length < 8) {
            this.valueView?.textSize = 37f

            if (shortColor != null)
                this.valueView?.setTextColor(shortColor)

            this.valueView?.typeface = lightFont
        }
    }


    fun setNoteText(noteString : String)
    {
        this.noteView?.text = noteString
        this.noteView?.visibility = View.VISIBLE
    }


    fun showIcon()
    {
        this.iconView?.visibility = View.VISIBLE
    }

}


// -----------------------------------------------------------------------------------------
// ENTRY RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class EntryRecyclerViewAdapter(val items : List<EntryItem>, val themeId : ThemeId)
        : RecyclerView.Adapter<EntryItemViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : EntryItemViewHolder
    {
        val viewBuilder = EntryViewBuilder(themeId, parent.context)
        return EntryItemViewHolder(viewBuilder.view(), themeId, parent.context)
    }


    override fun onBindViewHolder(viewHolder : EntryItemViewHolder, position : Int)
    {
        val item = this.items[position]

        viewHolder.setValueText(item.value)

        if (item.note != null)
            viewHolder.setNoteText(item.note)
    }


    override fun getItemCount() = this.items.size

}


// ---------------------------------------------------------------------------------------------
// ENTRY VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class EntryViewBuilder(val themeId : ThemeId, val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Value
        layout.addView(this.valueView())

        // Note
        layout.addView(this.noteView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
        layout.padding.leftDp   = 12f

        return layout.linearLayout(context)
    }


    // Value View
    // -----------------------------------------------------------------------------------------

    fun valueView() : View
    {
        val layout = this.valueViewLayout()

        // Icon
        layout.addView(this.iconView())

        // Text
        layout.addView(this.valueTextView())

        return layout
    }


    private fun valueViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }


    private fun valueTextView() : TextView
    {
        val value               = TextViewBuilder()

        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.id                = R.id.variable_history_entry_value

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color             = ThemeManager.color(themeId, colorTheme)

        value.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        value.sizeSp            = 14f

        return value.textView(context)
    }


    private fun iconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.id           = R.id.variable_history_entry_icon

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.visibility   = View.GONE

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 20
        icon.heightDp       = 20

        icon.image          = R.drawable.icon_check

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color          = ThemeManager.color(themeId, iconColorTheme)

        icon.margin.rightDp = 12f

        return layout.linearLayout(context)
    }



    // Note View
    // -----------------------------------------------------------------------------------------

    private fun noteView() : TextView
    {
        val note                = TextViewBuilder()

        note.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        note.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        note.id                 = R.id.variable_history_entry_note

        note.visibility         = View.GONE

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        note.color              = ThemeManager.color(themeId, colorTheme)

        note.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        note.sizeSp             = 13f

        return note.textView(context)
    }


}

