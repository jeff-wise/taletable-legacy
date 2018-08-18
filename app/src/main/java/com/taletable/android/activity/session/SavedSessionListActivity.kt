
package com.taletable.android.activity.session


import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import com.taletable.android.R
import com.taletable.android.db.readSessionList
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.session.Session
import com.taletable.android.rts.session.openSession
import com.taletable.android.util.configureToolbar
import java.text.SimpleDateFormat
import java.util.*



/**
 * Saved Session List Activity
 */
class SavedSessionListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_saved_session_list)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // Toolbar
        this.configureToolbar(getString(R.string.saved_sessions), TextFont.RobotoCondensed, TextFontStyle.Bold)

        // Theme
        this.applyTheme(officialAppThemeLight)

        // Session List
        this.initializeSavedSessionList()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeSavedSessionList()
    {
        val content = this.findViewById<LinearLayout>(R.id.content)

        val savedSessionListUI = SavedSessionListUI(officialAppThemeLight, this)
        val sessionList = readSessionList(this)

        content?.addView(savedSessionListUI.recyclerView(sessionList))
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

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

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



class SavedSessionListUI(val theme : Theme,
                         val activity : AppCompatActivity)
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    val context = activity


    // | Views
    // -----------------------------------------------------------------------------------------


    fun recyclerView(sessionRecords : List<Session>) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
        recyclerView.backgroundColor    = theme.colorOrBlack(colorTheme)

        recyclerView.adapter            = SavedSessionsRecyclerViewAdapater(sessionRecords, theme, context)

        recyclerView.padding.bottomDp   = 80f
        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }

}


// -----------------------------------------------------------------------------------------
// | SAVED SESSIONS RECYCLER VIEW ADAPTER
// -----------------------------------------------------------------------------------------

class SavedSessionsRecyclerViewAdapater(private val records : List<Session>,
                                        private val theme : Theme,
                                        private val context : Context)
                                         : RecyclerView.Adapter<SessionRecordViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SessionRecordViewHolder
    {
        val itemView = savedSessionRecordView(theme, context)
        return SessionRecordViewHolder(itemView, context)
    }


    override fun onBindViewHolder(viewHolder : SessionRecordViewHolder, position : Int)
    {
        val record = this.records[position]

        record.timeLastUsed.doMaybe {
            viewHolder.setDate(it)
        }
        viewHolder.setNameText(record.sessionName.value)
    }


    override fun getItemCount() : Int = this.records.size

}


private fun savedSessionRecordView(theme : Theme, context : Context) : View
{
    val layout = savedSessionRecordViewLayout(context)

    layout.addView(savedSessionRecordInfoView(theme, context))

    return layout
}


private fun savedSessionRecordViewLayout(context : Context) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.id               = R.id.session_record_layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.backgroundColor  = Color.WHITE

    layout.margin.topDp     = 2f
    layout.margin.leftDp    = 2f
    layout.margin.rightDp   = 2f

    layout.padding.topDp    = 10f
    layout.padding.bottomDp = 10f
    layout.padding.leftDp   = 8f
    layout.padding.rightDp  = 8f

    return layout.relativeLayout(context)
}


private fun savedSessionRecordInfoView(theme : Theme, context : Context) : LinearLayout
{
    val layout = savedSessionRecordInfoViewLayout(context)

    layout.addView(savedSessionRecordDateView(theme, context))

    layout.addView(savedSessionRecordNameView(theme, context))

    return layout
}


private fun savedSessionRecordInfoViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.layoutType       = LayoutType.RELATIVE
    layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT
    layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.addRule(RelativeLayout.ALIGN_PARENT_START)
    layout.addRule(RelativeLayout.CENTER_VERTICAL)

    layout.orientation      = LinearLayout.VERTICAL

    return layout.linearLayout(context)
}


private fun savedSessionRecordDateView(theme : Theme, context : Context) : TextView
{
    val view                = TextViewBuilder()

    view.id                 = R.id.session_record_last_used

    view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    view.font               = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
    view.color              = theme.colorOrBlack(nameColorTheme)

    view.sizeSp             = 16.5f

    return view.textView(context)
}


private fun savedSessionRecordNameView(theme : Theme, context : Context) : TextView
{
    val view                = TextViewBuilder()

    view.id                 = R.id.session_record_name

    view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    view.font               = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
    view.color              = theme.colorOrBlack(nameColorTheme)

    view.sizeSp             = 19f

    return view.textView(context)
}


/**
 * Session Record View Holder
 */
class SessionRecordViewHolder(itemView : View,
                              val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : ViewGroup?  = null
    var dateView    : TextView?  = null
    var nameView    : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout     = itemView.findViewById(R.id.session_record_layout)
        this.dateView   = itemView.findViewById(R.id.session_record_last_used)
        this.nameView   = itemView.findViewById(R.id.session_record_name)
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun setOnClick(onClick : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClick)
    }


    fun setNameText(name : String)
    {
        this.nameView?.text = name
    }


    fun setDate(calendar : Calendar)
    {
        val format = SimpleDateFormat("EEEE, MMMM dd")
        this.dateView?.text = format.format(calendar.time)
    }

}


