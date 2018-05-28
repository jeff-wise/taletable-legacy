
package com.kispoko.tome.activity.sheet.group


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.engine.tag.TagQuery
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.group.GroupId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.groups
import com.kispoko.tome.util.configureToolbar
import java.io.Serializable



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

    private var searchMode : SearchMode = SearchMode.ChooseMultiple


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

        if (this.intent.hasExtra("search_mode"))
            this.searchMode = this.intent.getSerializableExtra("search_mode") as SearchMode

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        val title = this.title
        if (title != null)
            this.configureToolbar(title)
        else
            this.configureToolbar(getString(R.string.groups))

        this.applyTheme(officialAppThemeLight)

        this.initializeView()

        when (this.searchMode)
        {
            is SearchMode.ChooseMultiple ->
            {
                this.initializeFAB()
            }

        }
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
            val groupSearchUI = GroupSearchUI(this.searchMode, officialAppThemeLight, it, this)
            contentView?.addView(groupSearchUI.view(this.tagQuery))
        }
    }


    private fun initializeFAB()
    {
        val fab = this.findViewById<FloatingActionButton>(R.id.fab)

        when (this.searchMode)
        {
            is SearchMode.ChooseMultiple ->
            {
                fab.visibility = View.VISIBLE
                fab.setOnClickListener {

                }
            }
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


sealed class SearchMode : Serializable
{
    object ChooseMultiple : SearchMode()
}



// -----------------------------------------------------------------------------------------
// GROUP SEARCH UI
// -----------------------------------------------------------------------------------------

class GroupSearchUI(val searchMode : SearchMode,
                    val theme : Theme,
                    val entityId : EntityId,
                    val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var selectedGroupIds : MutableSet<GroupId> = mutableSetOf()


    val checkSelectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    val checkSelectedColor = theme.colorOrBlack(checkSelectedColorTheme)


    val checkUnselectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_25"))))
    val checkUnselectedColor = theme.colorOrBlack(checkUnselectedColorTheme)


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view(tagQuery : TagQuery?) : View
    {
        return searchResultsRecyclerView(tagQuery)
    }


    private fun searchResultsRecyclerView(tagQuery : TagQuery?) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        recyclerView.backgroundColor    = colorOrBlack(colorTheme, entityId)

        tagQuery?.let { tagQuery ->
            val groupList = groups(tagQuery, entityId)
            recyclerView.adapter = GroupSearchResultRecyclerViewAdapter(groupList, selectedGroupIds, searchMode, theme, entityId, context)
        }

        return recyclerView.recyclerView(context)
    }


    // ---------------------------------------------------------------------------------------------
    // VIEWS > Search Header
    // ---------------------------------------------------------------------------------------------

    fun groupSearchHeaderView() : LinearLayout
    {
        val layout = groupSearchHeaderLayout()

        layout.addView(groupSearchHeaderTextView())

        return layout
    }


    private fun groupSearchHeaderLayout() : LinearLayout
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


    private fun groupSearchHeaderTextView() : TextView
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

    fun groupSearchResultView() : LinearLayout
    {
        val layout = groupSearchResultViewLayout()

        when (searchMode) {
            is SearchMode.ChooseMultiple -> {
                layout.addView(this.resultCheckboxView())
            }
        }

        layout.addView(groupSearchResultMainView())

        return layout
    }


    private fun groupSearchResultMainView() : LinearLayout
    {
        val layout = groupSearchResultMainViewLayout()

        // Name
        layout.addView(groupSearchResultNameView())

        // Summary
        layout.addView(groupSearchResultSummaryView())

        return layout
    }


    private fun groupSearchResultViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.id               = R.id.group_search_result_layout

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


    private fun resultCheckboxView() : LinearLayout
    {
        val layout = resultCheckboxViewLayout()

        val checkboxIconView = this.resultCheckboxIconView()
        layout.addView(checkboxIconView)

        return layout
    }


    private fun resultCheckboxViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

//        layout.id                   = R.id.group_search_result_selected_icon_layout

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.layoutGravity        = Gravity.CENTER
        layout.gravity              = Gravity.CENTER

        layout.backgroundResource   = R.drawable.bg_checkbox_unselected

        layout.margin.leftDp        = 7f
        layout.margin.rightDp       = 15f

        return layout.linearLayout(context)
    }


    private fun resultCheckboxIconView() : ImageView
    {
        val icon                    = ImageViewBuilder()

        icon.id                     = R.id.group_search_result_selected_icon

        icon.widthDp                = 18
        icon.heightDp               = 18

        icon.image                  = R.drawable.icon_check

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        icon.color                  = theme.colorOrBlack(colorTheme)

        icon.visibility             = View.GONE

        return icon.imageView(context)
    }



    private fun groupSearchResultMainViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun groupSearchResultNameView() : TextView
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


    private fun groupSearchResultSummaryView() : TextView
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



}



// -----------------------------------------------------------------------------------------
// GROUP SEARCH RECYCLER VIEW ADAPTER
// -----------------------------------------------------------------------------------------

class GroupSearchResultRecyclerViewAdapter(val items : List<Any>,
                                           val selectedGroupIds : MutableSet<GroupId>,
                                           val searchMode : SearchMode,
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


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder
    {
        val groupSearchUI = GroupSearchUI(searchMode, theme, entityId, context)

        return when (viewType)
        {
            HEADER -> {
                val headerView = groupSearchUI.groupSearchHeaderView()
                GroupSearchHeaderViewHolder(headerView)
            }
            // RESULT
            else -> {
                val resultView = groupSearchUI.groupSearchResultView()
                GroupSearchResultViewHolder(resultView, theme, entityId, context)
            }
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

                resultViewHolder.setOnClick( {

                    if (selectedGroupIds.contains(item.id))
                        selectedGroupIds.remove(item.id)
                    else
                        selectedGroupIds.add(item.id)
                })

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
class GroupSearchResultViewHolder(itemView : View,
                                  val theme : Theme,
                                  val entityId : EntityId,
                                  val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout         : LinearLayout?  = null
    var nameView       : TextView?      = null
    var summaryView    : TextView?      = null
    var iconView       : ImageView?     = null

    var isSelected     : Boolean        = false


    val checkSelectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    val checkSelectedColor = theme.colorOrBlack(checkSelectedColorTheme)


    val checkUnselectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_25"))))
    val checkUnselectedColor = theme.colorOrBlack(checkUnselectedColorTheme)


    val textUnselectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    val textUnselectedColor = theme.colorOrBlack(textUnselectedColorTheme)


    val textSelectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    val textSelectedColor = theme.colorOrBlack(textSelectedColorTheme)


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout         = itemView.findViewById(R.id.group_search_result_layout)
        this.nameView       = itemView.findViewById(R.id.group_search_result_name)
        this.summaryView    = itemView.findViewById(R.id.group_search_result_summary)
        this.iconView       = itemView.findViewById(R.id.group_search_result_selected_icon)
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


    fun setOnClick(onClick : () -> Unit)
    {
        layout?.setOnClickListener {

            onClick()

            if (this.isSelected) {
//                iconView?.colorFilter = PorterDuffColorFilter(checkUnselectedColor, PorterDuff.Mode.SRC_IN)
                iconView?.visibility = View.GONE
                nameView?.setTextColor(textUnselectedColor)
                this.isSelected = false

            }
            else {
//                iconView?.colorFilter = PorterDuffColorFilter(checkSelectedColor, PorterDuff.Mode.SRC_IN)
                iconView?.visibility = View.VISIBLE
                nameView?.setTextColor(textSelectedColor)
                this.isSelected = true
            }
        }
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


