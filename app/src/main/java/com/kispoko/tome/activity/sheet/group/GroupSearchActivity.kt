
package com.kispoko.tome.activity.sheet.group


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
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.engine.tag.TagQuery
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.groups
import com.kispoko.tome.util.configureToolbar



/**
 * Group Search Activity
 */
class GroupSearchActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var title     : String?    = null
    private var tagQuery  : TagQuery?  = null
    private var textQuery : String?    = null
    private var entityId  : EntityId?  = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_group_search)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("title"))
            this.title = this.intent.getStringExtra("title")

        if (this.intent.hasExtra("tag_query"))
            this.tagQuery = this.intent.getSerializableExtra("tag_query") as TagQuery

        if (this.intent.hasExtra("text_query"))
            this.textQuery = this.intent.getStringExtra("text_query") as String

        if (this.intent.hasExtra("entity_id"))
            this.entityId = this.intent.getSerializableExtra("entity_id") as EntityId

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        val title = this.title
        if (title != null)
            this.configureToolbar(title)
        else
            this.configureToolbar(getString(R.string.groups))

        this.applyTheme(officialAppThemeLight)

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
        val contentView = this.findViewById<LinearLayout>(R.id.content)

        this.entityId?.let {
            val groupSearchUI = GroupSearchUI(this.tagQuery, officialAppThemeLight, it, this)
            contentView?.addView(groupSearchUI.view())
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

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)


            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
//            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.decorView.setSystemUiVisibility(flags)
//            this.getWindow().setStatusBarColor(Color.WHITE);
//        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_back_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_search_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }

}


// -----------------------------------------------------------------------------------------
// GROUP SEARCH UI
// -----------------------------------------------------------------------------------------

class GroupSearchUI(val tagQuery : TagQuery?,
                    val theme : Theme,
                    val entityId : EntityId,
                    val context : Context)
{


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        return searchResultsRecyclerView()
    }


    private fun searchResultsRecyclerView() : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        recyclerView.backgroundColor    = colorOrBlack(colorTheme, entityId)

        Log.d("***GROUP SEARCH", "tag query: $tagQuery")

        tagQuery?.let { tagQuery ->
            val groupList = groups(tagQuery, entityId)
            Log.d("***GROUP SEARCH", "groups: $groupList")
            recyclerView.adapter = GroupSearchResultRecyclerViewAdapter(groupList, theme, entityId, context)
        }

        return recyclerView.recyclerView(context)
    }


}



// -----------------------------------------------------------------------------------------
// GROUP SEARCH RECYCLER VIEW ADAPTER
// -----------------------------------------------------------------------------------------

class GroupSearchResultRecyclerViewAdapter(val items : List<Any>,
                                           val theme : Theme,
                                           val entityId : EntityId,
                                           val context : Context)
                                            : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private val HEADER = 0
    private val RESULT = 1


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
        val itemAtPosition = this.items[position]

        return when (itemAtPosition) {
            is String -> HEADER
            is Group  -> RESULT
            else      -> -1
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder =
        when (viewType)
        {
            HEADER ->
            {
                val headerView = groupSearchHeaderView(theme, context)
                GroupSearchHeaderViewHolder(headerView)
            }
            // RESULT
            else ->
            {
                val resultView = groupSearchResultView(theme, context)
                GroupSearchResultViewHolder(resultView, entityId, context)
            }
        }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val item = this.items[position]

        when (item)
        {
            is String ->
            {
                val headerViewHolder = viewHolder as GroupSearchHeaderViewHolder
                headerViewHolder.setHeaderText(item)
            }
            is Group ->
            {
                val resultViewHolder = viewHolder as GroupSearchResultViewHolder

                resultViewHolder.setNameString(item.name().value)
                resultViewHolder.setSummaryString(item.summary().value)
            }
        }
    }


    override fun getItemCount() = this.items.size

}


// ---------------------------------------------------------------------------------------------
// VIEW HOLDER: GROUP SEARCH RESULT
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class GroupSearchResultViewHolder(itemView : View, val entityId : EntityId, val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var nameView    : TextView?  = null
    var summaryView : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.nameView    = itemView.findViewById(R.id.group_search_result_name)
        this.summaryView = itemView.findViewById(R.id.group_search_result_summary)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setNameString(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setSummaryString(summaryString : String)
    {
        this.summaryView?.text = summaryString
    }

}


// ---------------------------------------------------------------------------------------------
// VIEW HOLDER: GROUP SEARCH HEADER
// ---------------------------------------------------------------------------------------------

class GroupSearchHeaderViewHolder(val headerView : View) : RecyclerView.ViewHolder(headerView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var headerTextView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.headerTextView = headerView.findViewById(R.id.group_search_result_header)
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun setHeaderText(headerString : String)
    {
        this.headerTextView?.text = headerString
    }

}


// ---------------------------------------------------------------------------------------------
// VIEWS > Search Header
// ---------------------------------------------------------------------------------------------

private fun groupSearchHeaderView(theme : Theme, context : Context) : LinearLayout
{
    val layout = groupSearchHeaderLayout(context)

    layout.addView(groupSearchHeaderTextView(theme, context))

    return layout
}


private fun groupSearchHeaderLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.backgroundColor  = Color.WHITE

    layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

    layout.margin.leftDp    = 2f
    layout.margin.rightDp   = 2f
    layout.margin.topDp     = 4f

    layout.padding.leftDp   = 8f
    layout.padding.rightDp  = 8f
    layout.padding.topDp    = 6f
    layout.padding.bottomDp = 6f

    return layout.linearLayout(context)
}


private fun groupSearchHeaderTextView(theme : Theme, context : Context) : TextView
{
    val headerView                = TextViewBuilder()

    headerView.id                 = R.id.group_search_result_header

    headerView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    headerView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    headerView.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    headerView.color              = theme.colorOrBlack(nameColorTheme)

    headerView.sizeSp             = 16f


    return headerView.textView(context)
}


// ---------------------------------------------------------------------------------------------
// VIEWS > Search Result
// ---------------------------------------------------------------------------------------------

private fun groupSearchResultView(theme : Theme, context : Context) : LinearLayout
{
    val layout = groupSearchResultViewLayout(context)

    layout.addView(groupSearchResultMainView(theme, context))

    return layout
}


private fun groupSearchResultMainView(theme : Theme, context : Context) : LinearLayout
{
    val layout = groupSearchResultMainViewLayout(context)

    // Name
    layout.addView(groupSearchResultNameView(theme, context))

    // Summary
    layout.addView(groupSearchResultSummaryView(theme, context))

    return layout
}


private fun groupSearchResultViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.backgroundColor  = Color.WHITE

    layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

    layout.margin.leftDp    = 2f
    layout.margin.rightDp   = 2f
    layout.margin.topDp     = 2f

    layout.padding.leftDp   = 8f
    layout.padding.rightDp  = 8f
    layout.padding.topDp    = 6f
    layout.padding.bottomDp = 6f


    return layout.linearLayout(context)
}



private fun groupSearchResultMainViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    return layout.linearLayout(context)
}


private fun groupSearchResultNameView(theme : Theme, context : Context) : TextView
{
    val nameView                = TextViewBuilder()

    nameView.id                 = R.id.group_search_result_name

    nameView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    nameView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    nameView.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    nameView.color              = theme.colorOrBlack(nameColorTheme)

    nameView.sizeSp             = 18f

    return nameView.textView(context)
}


private fun groupSearchResultSummaryView(theme : Theme, context : Context) : TextView
{
    val nameView                = TextViewBuilder()

    nameView.id                 = R.id.group_search_result_summary

    nameView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    nameView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    nameView.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
    nameView.color              = theme.colorOrBlack(nameColorTheme)

    nameView.sizeSp             = 16f


    return nameView.textView(context)
}

