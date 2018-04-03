
package com.kispoko.tome.activity.session


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.official.heroes.pcView
import com.kispoko.tome.db.DatabaseManager
import com.kispoko.tome.db.Schema
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.rts.session.SessionLoader
import com.kispoko.tome.util.configureToolbar
import java.text.SimpleDateFormat



/**
 * Load Session Activity
 */
class LoadSessionActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameId : GameId? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_load_session)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as GameId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // Toolbar
        this.configureToolbar("Load Session")

        // Theme
        this.applyTheme(officialThemeLight)
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
        val content = this.findViewById(R.id.content) as LinearLayout
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
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_main_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))
    }

}


class LoadSessionUI(val theme : Theme,
                    val activity : AppCompatActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity as Context


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun loadSessionSummaries() : List<SessionLoader>
    {
//        val summaries : MutableList<SessionSummary> = mutableListOf<>()
//
//
//
//        summaries.add(SessionSummary(sessionName : SessionName,
//                                     lastUsedTime : Calendar)
//
//        return summaries


        val db = DatabaseManager(context).readableDatabase

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED} DESC"

        val cursor = db.query(
                Schema.SessionTable.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                arrayOf(),          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        )


        val sessionLoaders : MutableList<SessionLoader> = mutableListOf()
//
//        with(cursor) {
//            while (moveToNext()) {
//
//                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
//
//                itemIds.add(itemId)
//            }
//        }


        return listOf()
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        val summaries = loadSessionSummaries()
        layout.addView(this.recyclerView(summaries, theme, context))

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun recyclerView(summaries : List<SessionLoader>,
                             theme : Theme,
                             context : Context) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = SavedSessionsRecyclerViewAdapter(summaries,
                                                                           theme,
                                                                           context)

        recyclerView.padding.leftDp     = 6f
        recyclerView.padding.rightDp    = 6f
        recyclerView.padding.bottomDp   = 60f

        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }


}


// -----------------------------------------------------------------------------------------
// SESSIONS RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class SavedSessionsRecyclerViewAdapter(val items : List<SessionLoader>,
                                       val theme : Theme,
                                       val context : Context)
        : RecyclerView.Adapter<SessionSummaryViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SessionSummaryViewHolder
    {
        return SessionSummaryViewHolder(pcView(theme, parent.context), theme, parent.context)
    }


    override fun onBindViewHolder(viewHolder : SessionSummaryViewHolder, position : Int)
    {
        val summary = this.items[position]

        viewHolder.setNameText(summary.sessionName.value)

//        val format = SimpleDateFormat("MMM d, yyyy")
//        val dateString = format.format(summary.lastUsedTime.time)
//        viewHolder.setDateText(dateString)
    }


    override fun getItemCount() = this.items.size

}


// ---------------------------------------------------------------------------------------------
// SESSION SUMMARY VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class SessionSummaryViewHolder(itemView : View,
                               val theme : Theme,
                               val context : Context)
                                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutView     : LinearLayout? = null
    var nameView       : TextView?  = null
    var dateView       : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layoutView     = itemView.findViewById(R.id.load_session_item_layout) as LinearLayout
        this.nameView       = itemView.findViewById(R.id.load_session_item_name) as TextView
        this.dateView       = itemView.findViewById(R.id.load_session_item_name) as TextView
    }


    fun setNameText(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setDateText(dateString : String)
    {
        this.dateView?.text = dateString
    }

}




