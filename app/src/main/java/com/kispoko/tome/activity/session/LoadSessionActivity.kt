
package com.kispoko.tome.activity.session


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
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.feed.CardViewHolder
import com.kispoko.tome.activity.entity.feed.FeedRecyclerViewAdapater
import com.kispoko.tome.activity.entity.feed.FeedUI
import com.kispoko.tome.activity.sheet.group.SwipeAndDragHelper
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.feed.Feed
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.session.SessionId
import com.kispoko.tome.rts.session.SessionRecord
import com.kispoko.tome.util.configureToolbar
import io.reactivex.disposables.CompositeDisposable
import maybe.Just
import java.text.SimpleDateFormat
import java.util.*


class LoadSessionActivity : AppCompatActivity()
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

        setContentView(R.layout.activity_open_session)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.load_session))

        this.applyTheme(com.kispoko.tome.model.theme.official.officialAppThemeLight)

        this.initializeView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeView()
    {

    }


    private fun applyTheme(theme : Theme)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        val toolbarBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(toolbarBgColorTheme))

        // Toolbar > Icons
        val toolbarIconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_close_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(theme.colorOrBlack(toolbarIconColorTheme), PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(theme.colorOrBlack(toolbarIconColorTheme), PorterDuff.Mode.SRC_IN)

    }

}



// ---------------------------------------------------------------------------------------------
// LOAD SESSION UI
// ---------------------------------------------------------------------------------------------

class LoadSessionUI(val theme : Theme,
                    val activity : AppCompatActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

//    private fun savedSessionRecords() : List<SessionRecord>
//    {
//
//    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View = recyclerView()


    private fun recyclerView() : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
        recyclerView.backgroundColor    = theme.colorOrBlack(colorTheme)

//        recyclerView.adapter            = SavedSessionsRecyclerViewAdapater(feed, this, theme, context)

        recyclerView.padding.bottomDp   = 80f
        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }


    // VIEWS > Record View
    // -----------------------------------------------------------------------------------------

    fun recordView() : View
    {
        val layout = this.recordViewLayout()

        layout.addView(this.recordLastUsedView())

        layout.addView(this.recordNameView())

        return layout
    }


    private fun recordViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun recordLastUsedView() : TextView
    {
        val view                = TextViewBuilder()

        view.id                 = R.id.session_record_last_used

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 15f

        return view.textView(context)
    }


    private fun recordNameView() : TextView
    {
        val view                = TextViewBuilder()

        view.id                 = R.id.session_record_name

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 18f

        return view.textView(context)
    }

}



// ---------------------------------------------------------------------------------------------
// SAVED SESSIONS: RECYCLER VIEW ADPATER
// ---------------------------------------------------------------------------------------------


class SavedSessionsRecyclerViewAdapater(private val sessionRecords : List<SessionRecord>,
                                        private val loadSessionUI : LoadSessionUI,
                                        private val theme : Theme,
                                        private val context : Context)
                                         : RecyclerView.Adapter<SessionRecordViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var touchHelper : ItemTouchHelper? = null


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SessionRecordViewHolder
    {
        val recordView = loadSessionUI.recordView()
        return SessionRecordViewHolder(recordView, context)
    }


    override fun onBindViewHolder(viewHolder : SessionRecordViewHolder, position : Int)
    {
        val record = this.sessionRecords[position]

        viewHolder.setLastUsed(record.lastUsed)
        viewHolder.setName(record.sessionName.value)
    }


    override fun getItemCount() : Int = this.sessionRecords.size

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

    var lastUsedView : TextView?  = null
    var nameView     : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.lastUsedView   = itemView.findViewById(R.id.session_record_last_used)
        this.nameView       = itemView.findViewById(R.id.session_record_name)
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun setLastUsed(lastUsed : Calendar)
    {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        this.lastUsedView?.text = dateFormat.format(lastUsed.time)
    }


    fun setName(nameString : String)
    {
        this.nameView?.text = nameString
    }

}
