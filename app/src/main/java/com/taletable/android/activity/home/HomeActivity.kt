
package com.taletable.android.activity.home


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.session.*
import com.taletable.android.util.Util
import com.taletable.android.util.configureToolbar
import io.reactivex.disposables.CompositeDisposable
import java.util.*

// pinned
// news
// welcome to tome beta
// > still prototype, schedule
// > content still being added, no licensed content yet
// > about Tome / plan
// > FAQ
//
// support app, donate
//
// core rulebook quick link
//
// random creature
//
// random character sheet
//
// player quote / moment
//
// survey
//
// random spell
//
// random weapon
//
// random rule



/**
 * Feed Activity
 */
class HomeActivity : AppCompatActivity() //, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Any>
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var hasSavedSessions : Boolean = false

    var selectedSession : Session? = null

    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()


    private var currentQuery : String = ""

    private var searchHistory : MutableList<String> = mutableListOf()


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_home)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        searchHistory.add("")

        this.configureToolbar(getString(R.string.tale_table), TextFont.RobotoCondensed, TextFontStyle.Bold, 19f)


        this.applyTheme(officialAppThemeLight)

        this.initializeSearchView()

        //this.initializeListeners()

        this.updateSearchView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }


    // -----------------------------------------------------------------------------------------
    // MESSAGING
    // -----------------------------------------------------------------------------------------
//
//    private fun initializeListeners()
//    {
////        val newSessionMessageDisposable = Router.listen(NewSessionMessage::class.java)
////                                                .subscribe(this::onMessage)
////        this.messageListenerDisposable.add(newSessionMessageDisposable)
//
//        val sessionMessageDisposable = Router.listen(MessageSessionLoad::class.java)
//                                             .subscribe(this::onSessionLoadMessage)
//        this.messageListenerDisposable.add(sessionMessageDisposable)
//    }

//
//    private fun onSessionLoadMessage(message : MessageSessionLoad)
//    {
//        when (message)
//        {
//            is MessageSessionEntityLoaded ->
//            {
//                val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
//                progressBar?.let { bar ->
//                    val updateAmount = bar.progress + (72 / message.update.totalEntities)
//                    bar.progress = updateAmount
//                }
//            }
//            is MessageSessionLoaded ->
//            {
//                this.selectedSession?.let {
//                    val mainEntityId = it.mainEntityId
//                    val intent = Intent(this, SessionActivity::class.java)
//                    intent.putExtra("sheet_id", mainEntityId)
//                    startActivity(intent)
//                }
//            }
//        }
//    }



    private fun initializeSearchView()
    {
        this.findViewById<TextView>(R.id.searchbar_text_view)?.let { textView ->
//            textView.textSize = Util.spToPx(4.8f, this).toFloat()
            textView.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Regular, this)
        }
    }


    private fun defaultSearchResults() : List<SearchResult>
    {
        val news  = SearchResultIcon("News", "Gaming related news and updates.", R.drawable.icon_news, "news", 23)
        val games = SearchResultIcon("Games", "Browse, play and create games.", R.drawable.icon_die, "games", 24)
        val books = SearchResultIcon("Books", "Discover and read interactive books.", R.drawable.icon_books, "books", 25)
        val recommended = SearchResultIcon("Recommended", "Suggestions based on what you like.", R.drawable.icon_wand, "recommended")
        val people = SearchResultIcon("People", "Players, authors, game designers, ...", R.drawable.icon_people, "people", 25)

        return listOf(recommended, games, books, news, people)
    }


    private fun bookSearchResults() : List<SearchResult>
    {
        val pageGroupHeaderResult = SearchResultPageGroupHeader("Pathfinder 2 Playtest Books")

        val rulebookResult = SearchResultPage("Pathfinder 2 Playtest Rulebook",
                                              "OGL rules for the Pathfinder 2 Playtest",
                                              "books pathfinder 2 playtest rulebook")

        val pageGroupSearchResult = SearchResultPageGroupSearch("More Pathfinder 2 Playtest books")

        return listOf(pageGroupHeaderResult, rulebookResult, pageGroupSearchResult)
    }


    private fun pathfinder2PlaytestBookSearchResults() : List<SearchResult>
    {
        val bookSessionId = SessionId(UUID.fromString("2c383a1b-b695-4553-bcf3-22eb4ed16b1c"))

        val sessionResult = SearchResultSession("Pathfinder 2 Playtest: Core Rules", "Core rulebook (OGL) for the Pathfinder 2 Playtest", bookSessionId)

        return listOf(sessionResult)
    }


    private fun gamesSearchResults() : List<SearchResult>
    {
        val pageGroupHeaderResult = SearchResultPageGroupHeader("Games")

        val game1Result = SearchResultPage("Pathfinder 2",
                                           "Pathfinder 2 OGL ruleset (Paizo)",
                                           "games pathfinder 2")
        val game2Result = SearchResultPage("5th Edition",
                                           "5th Edition OGL ruleset",
                                           "games 5th edition")
        val game3Result = SearchResultPage("Starfinder",
                                           "Starfinder OGL ruleset (Paizo)",
                                            "games 5th edition")

        val pageGroupSearchResult = SearchResultPageGroupSearch("More games")

        return listOf(pageGroupHeaderResult, game1Result, game2Result, game3Result, pageGroupSearchResult)
    }


    private fun pathfinder2Results() : List<SearchResult>
    {

        val pathfinder2SessionId = SessionId(UUID.fromString("b3b4894d-7f2a-4f11-9b27-6e8133f7000f"))
        val gameSessionResult = SearchResultSession("Pathfinder 2", "The roleplaying game for Pathfinder 2", pathfinder2SessionId)

        val suggestion1Result = SearchResultSimple("Pathfinder 2", "Pathfinder 2", "Sessions")
        val suggestion2Result = SearchResultSimple("Starfinder", "Pathfinder 2", "Rules")

        val charGroupHeaderResult = SearchResultPageGroupHeader("Player Characters")

        val char1Result = SearchResultPage("Casmey Dalseya",
                                           "1st Level Human Rogue",
                                           "games pathfinder 2 session casmey dalseya")
        val char2Result = SearchResultPage("Mazar (of The Clan)",
                                           "1st Level Half-Orc Barbarian",
                                           "casmey dalseya")
        val char3Result = SearchResultPage("Darius Bristlebottoms",
                                           "1st Level Human Bard",
                                           "casmey dalseya")
                                           //kSearchResultPageTargetSession(SessionId(UUID.fromString("b3b4894d-7f2a-4f11-9b27-6e8133f7000f"))))

        val charGroupSearchResult = SearchResultPageGroupSearch("More PCs")


        return listOf(gameSessionResult,
                      suggestion1Result,
                      suggestion2Result,
                      charGroupHeaderResult,
                      char1Result,
                      char2Result,
                      char3Result,
                      charGroupSearchResult)
    }


    private fun casmeyDalseyaSearchResults() : List<SearchResult>
    {
        val casmeySessionId = SessionId(UUID.fromString("56897634-288a-478e-bb59-eedf09d8aab6"))
        val sessionResult = SearchResultSession("Casmey Dalseya", "1st Level Human Rogue", casmeySessionId)

        return listOf(sessionResult)
    }



//    fun setGameCategory()
//    {
//        findViewById<RecyclerView>(R.id.search_results)?.let { recyclerView ->
//
//            gameManifest(this)?.let { gameManifest ->
//
//                Log.d("***HOME ACTIVITY", "game manifest: $gameManifest")
//
//                val layoutManager = LinearLayoutManager(this)
//                recyclerView.layoutManager = layoutManager
//
////                val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.list_layout_fall_down)
////                recyclerView.layoutAnimation = animation
//
//                recyclerView.adapter = SearchAllRecyclerViewAdapter(gameManifest.gameSummaries, officialAppThemeLight, this)
//
//                recyclerView.invalidate()
//            }
//        }
//    }


    fun searchResults() : List<SearchResult> = when (this.currentQuery) {
        "books"                             -> this.bookSearchResults()
        "games"                             -> this.gamesSearchResults()
        "games pathfinder 2"                -> this.pathfinder2Results()
        "games pathfinder 2 session casmey dalseya" -> this.casmeyDalseyaSearchResults()
        "books pathfinder 2 playtest rulebook" -> this.pathfinder2PlaytestBookSearchResults()
        else                                -> this.defaultSearchResults()
    }


    fun updateSearch(newQuery : String)
    {
        this.currentQuery = newQuery

        this.searchHistory.add(newQuery)

        this.updateSearchView()
    }


    fun clearSearch()
    {
        updateSearch("")
    }


    fun previousSearch()
    {
        val previousSearch = this.searchHistory.removeAt(this.searchHistory.size - 1)
        this.searchHistory.lastOrNull()?.let { lastSearch ->
            Log.d("***HOME ACTIVITY", "going back to: $lastSearch")
            updateSearch(lastSearch)
        }
    }


    fun updateSearchView()
    {
        findViewById<RecyclerView>(R.id.search_results)?.let { recyclerView ->

            val results = this.searchResults()

            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.list_layout_fall_down)
            recyclerView.layoutAnimation = animation

            recyclerView.adapter = SearchAllRecyclerViewAdapter(results, officialAppThemeLight, this)

            recyclerView.invalidate()
        }


        this.findViewById<TextView>(R.id.searchbar_text_view)?.let { textView ->
            //            textView.textSize = Util.spToPx(4.8f, this).toFloat()
            //textView.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Regular, this)
            textView.text = this.currentQuery
        }

        this.findViewById<ImageView>(R.id.searchbar_left_button_view)?.let { buttonView ->
            if (this.currentQuery.isEmpty())
            {
                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_searchbar_search))
            }
            else
            {
                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_searchbar_back))
                buttonView.setOnClickListener {
                    previousSearch()
                }
            }
        }

        this.findViewById<ImageView>(R.id.searchbar_right_button_view)?.let { buttonView ->
            if (this.currentQuery.isEmpty())
            {
                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_toolbar_options))
            }
            else
            {
                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_toolbar_close))
                buttonView.setOnClickListener {
                    clearSearch()
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // ------------------------------------------------------------------------------------
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.setSystemUiVisibility(flags)
            this.getWindow().setStatusBarColor(Color.WHITE);
        }
    }



}


//private fun searchToolbarCategoryView(category : Category,
//                                      theme : Theme,
//                                      homeActivity : HomeActivity) : LinearLayout
//{
//
//    val layout = searchToolbarCategoryViewLayout(homeActivity)
//
//    layout.addView(searchToolbarCategoryLabelView(theme, homeActivity))
//
//    layout.addView(searchToolbarCategorySelectionView(category, theme, homeActivity))
//
//
//    return layout
//}
//
//
//
//private fun searchToolbarCategoryViewLayout(context : Context) : LinearLayout
//{
//    val layout                      = LinearLayoutBuilder()
//
//    layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.orientation              = LinearLayout.HORIZONTAL
//
//    layout.gravity                  = Gravity.CENTER_VERTICAL
//
//    layout.padding.leftDp           = 20f
//
//    return layout.linearLayout(context)
//}
//
//
//private fun searchToolbarCategoryLabelView(theme : Theme, context : Context) : TextView
//{
//    val title               = TextViewBuilder()
//
//    title.id                = R.id.name_view
//
//    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    title.textId            = R.string.search_in
//
//    title.font              = Font.typeface(TextFont.Roboto,
//                                            TextFontStyle.Medium,
//                                            context)
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
//    title.color           = theme.colorOrBlack(colorTheme)
//
//    title.sizeSp          = 15f
//
//    return title.textView(context)
//}
//
//
//fun searchToolbarCategorySelectionView(category : Category,
//                                       theme : Theme,
//                                       homeActivity : HomeActivity) : LinearLayout
//{
//    val layout = searchToolbarCategorySelectionViewLayout(homeActivity)
//
//    layout.addView(searchToolbarCategorySelectionIconView(category.iconId,
//                                                          category.smallIconSize,
//                                                          theme,
//                                                          homeActivity))
//
//    layout.addView(searchToolbarCategorySelectionNameView(category.name, theme, homeActivity))
//
//    layout.setOnClickListener {
//        homeActivity.showCategoryList()
//    }
//
//    return layout
//}
//
//
//fun searchToolbarCategorySelectionViewLayout(context : Context) : LinearLayout
//{
//    val layout              = LinearLayoutBuilder()
//
//    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.orientation      = LinearLayout.HORIZONTAL
//
//    layout.gravity          = Gravity.CENTER_VERTICAL
//
//    layout.margin.leftDp    = 10f
//
//    layout.padding.topDp            = 12f
//    layout.padding.bottomDp         = 12f
//
//    return layout.linearLayout(context)
//}
//
//
//private fun searchToolbarCategorySelectionNameView(name : String,
//                                                   theme : Theme,
//                                                   context : Context) : TextView
//{
//    val title               = TextViewBuilder()
//
//    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    title.text              = name
//
//    title.font              = Font.typeface(TextFont.Roboto,
//                                            TextFontStyle.Medium,
//                                            context)
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
//    title.color           = theme.colorOrBlack(colorTheme)
//
//    title.sizeSp          = 15f
//
//    return title.textView(context)
//}
//
//
//private fun searchToolbarCategorySelectionIconView(iconId : Int,
//                                                   iconSize : Int,
//                                                   theme : Theme,
//                                                   context : Context) : LinearLayout
//{
//    // 1 | Declarations
//    // -----------------------------------------------------------------------------------------
//
//    val layout                  = LinearLayoutBuilder()
//    val iconView                = ImageViewBuilder()
//
//    // 2 | Layout
//    // -----------------------------------------------------------------------------------------
//
//    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.margin.rightDp       = 6f
//
//    layout.child(iconView)
//
//    // 3 | Icon
//    // -----------------------------------------------------------------------------------------
//
//    iconView.widthDp            = iconSize
//    iconView.heightDp           = iconSize
//
//    iconView.image              = iconId
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
//    iconView.color              = theme.colorOrBlack(colorTheme)
//
//    return layout.linearLayout(context)
//}



private fun searchListToolbarView(isCategoryList : Boolean,
                                  theme : Theme,
                                  homeActivity : HomeActivity) : LinearLayout
{
    val layout = searchListToolbarViewLayout(homeActivity)

    if (isCategoryList)
    {
        layout.addView(searchListTitleView("Browse Categories", theme, homeActivity))
    }
    else
    {

    }

    return layout
}


private fun searchListToolbarViewLayout(context : Context) : LinearLayout
{
    val layout                      = LinearLayoutBuilder()

    layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation              = LinearLayout.HORIZONTAL

    layout.gravity                  = Gravity.CENTER_VERTICAL

    layout.padding.leftDp           = 8f

    return layout.linearLayout(context)
}


private fun searchListTitleView(title : String, theme : Theme, context : Context) : TextView
{
    val titleView               = TextViewBuilder()

    titleView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    titleView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    titleView.padding.topDp     = 8f
    titleView.padding.bottomDp  = 7f

    titleView.text              = title

    titleView.font              = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Bold,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_18"))))
    titleView.color           = theme.colorOrBlack(colorTheme)

    titleView.sizeSp          = 16f

    return titleView.textView(context)
}



class SearchAllRecyclerViewAdapter(val items : List<Any>,
                                   val theme : Theme,
                                   val homeActivity : HomeActivity)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private val SEARCH_RESULT_SIMPLE = 0
    private val SEARCH_RESULT_ICON = 1
    private val SEARCH_RESULT_PAGE = 2
    private val SEARCH_RESULT_PAGE_GROUP_HEADER = 3
    private val SEARCH_RESULT_PAGE_GROUP_SEARCH = 4
    private val SEARCH_RESULT_SESSION = 5


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
        val itemAtPosition = this.items[position]

        return when (itemAtPosition) {
            is SearchResultSimple           -> SEARCH_RESULT_SIMPLE
            is SearchResultIcon             -> SEARCH_RESULT_ICON
            is SearchResultPage             -> SEARCH_RESULT_PAGE
            is SearchResultPageGroupHeader  -> SEARCH_RESULT_PAGE_GROUP_HEADER
            is SearchResultPageGroupSearch  -> SEARCH_RESULT_PAGE_GROUP_SEARCH
            is SearchResultSession          -> SEARCH_RESULT_SESSION
            else                            -> SEARCH_RESULT_ICON
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder =
        when (viewType)
        {
            SEARCH_RESULT_SIMPLE ->
            {
                val simpleView = searchResultSimpleView(theme, homeActivity)
                SearchResultSimpleViewHolder(simpleView, theme, homeActivity)
            }
            SEARCH_RESULT_ICON ->
            {
                val iconView = searchResultIconView(theme, homeActivity)
                SearchResultIconViewHolder(iconView, theme, homeActivity)
            }
            SEARCH_RESULT_PAGE ->
            {
                val imageView = searchResultImageView(theme, homeActivity)
                SearchResultImageViewHolder(imageView, theme, homeActivity)
            }
            SEARCH_RESULT_PAGE_GROUP_SEARCH ->
            {
                val pageGroupSearchView = searchResultPageGroupSearchView(theme, homeActivity)
                SearchResultPageGroupSearchViewHolder(pageGroupSearchView, theme, homeActivity)
            }
            SEARCH_RESULT_PAGE_GROUP_HEADER ->
            {
                val pageGroupHeaderView = searchResultPageGroupHeaderView(theme, homeActivity)
                SearchResultPageGroupHeaderViewHolder(pageGroupHeaderView, theme, homeActivity)
            }
            SEARCH_RESULT_SESSION ->
            {
                val sessionView = searchResultSessionView(theme, homeActivity)
                SearchResultSessionViewHolder(sessionView, theme, homeActivity)
            }
            else ->
            {
                val otherView = searchResultSimpleView(theme, homeActivity)
                SearchResultSimpleViewHolder(otherView, theme, homeActivity)
            }
        }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val item = this.items[position]

        when (item)
        {
            is SearchResultSimple -> {
                val resultSimpleViewHolder = viewHolder as SearchResultSimpleViewHolder
                if (item.prefix != null && item.suggestion != null) {
                    resultSimpleViewHolder.setComplexTerm(item.prefix, item.suggestion)
                }
                else {
                    resultSimpleViewHolder.setSimpleTerm(item.term)
                }
            }
            is SearchResultIcon -> {
                val resultIconViewHolder = viewHolder as SearchResultIconViewHolder
                resultIconViewHolder.setName(item.term)
                resultIconViewHolder.setDescription(item.description)
                resultIconViewHolder.setIcon(item.iconId, item.iconSize)
                resultIconViewHolder.setOnClick(View.OnClickListener {
                    homeActivity.updateSearch(item.newSearchQuery)
                })
            }
            is SearchResultPage -> {
                val resultPageViewHolder = viewHolder as SearchResultImageViewHolder
                resultPageViewHolder.setName(item.term)
                resultPageViewHolder.setDescription(item.description)
                resultPageViewHolder.setOnClick(View.OnClickListener {
                    homeActivity.updateSearch(item.newSearchQuery)
                })
            }
            is SearchResultPageGroupHeader -> {
                val pageGroupHeaderViewHolder = viewHolder as SearchResultPageGroupHeaderViewHolder
                pageGroupHeaderViewHolder.setName(item.pageGroupHeader)
            }
            is SearchResultPageGroupSearch -> {
                val pageGroupSearchViewHolder = viewHolder as SearchResultPageGroupSearchViewHolder
                pageGroupSearchViewHolder.setName(item.pageGroupName)
            }
            is SearchResultSession -> {
                val sessionViewHolder = viewHolder as SearchResultSessionViewHolder
                sessionViewHolder.setName(item.name)
                sessionViewHolder.setDescription(item.description)

                sessionViewHolder.setOnClick(View.OnClickListener {
                    val intent = Intent(homeActivity, SessionActivity::class.java)
                    intent.putExtra("session_id", item.sessionId)
                    homeActivity.startActivity(intent)
                })

            }
        }
    }


    override fun getItemCount() = this.items.size

}


class SearchResultPageGroupHeaderViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : RelativeLayout? = null
    var nameView    : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.nameView    = itemView.findViewById(R.id.name_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------


    fun setName(name : String)
    {
        this.nameView?.text = name
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}


class SearchResultPageGroupSearchViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout? = null
    var nameView    : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.nameView    = itemView.findViewById(R.id.name_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------


    fun setName(name : String)
    {
        this.nameView?.text = name
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}


class SearchResultSimpleViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : RelativeLayout? = null
    var nameView    : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.nameView    = itemView.findViewById(R.id.name_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------


    fun setSimpleTerm(name : String)
    {
        this.nameView?.text = name
    }


    fun setComplexTerm(prefix : String, suffix : String)
    {
        this.nameView?.text = searchResultSimpleNameSpannable(prefix, suffix, theme, context)
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}


class SearchResultSessionViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout          : LinearLayout? = null
    var nameView        : TextView? = null
    var descriptionView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout          = itemView.findViewById(R.id.layout)
        this.nameView        = itemView.findViewById(R.id.name_view)
        this.descriptionView = itemView.findViewById(R.id.description_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setName(name : String)
    {
        this.nameView?.text = name
    }


    fun setDescription(description : String)
    {
        this.descriptionView?.text = description
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}


class SearchResultIconViewHolder(itemView : View, val theme : Theme, val context : Context)
                                    : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout          : RelativeLayout? = null
    var iconView        : ImageView? = null
    var nameView        : TextView? = null
    var descriptionView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout          = itemView.findViewById(R.id.layout)
        this.iconView        = itemView.findViewById(R.id.icon_view)
        this.nameView        = itemView.findViewById(R.id.name_view)
        this.descriptionView = itemView.findViewById(R.id.description_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setIcon(iconId : Int, iconSize : Int? = null)
    {
        this.iconView?.let {

            it.setImageDrawable(ContextCompat.getDrawable(context, iconId))

            if (iconSize != null)
            {
                val layoutParams = it.layoutParams
                layoutParams.width = Util.dpToPixel(iconSize.toFloat())
                layoutParams.height = Util.dpToPixel(iconSize.toFloat())

                it.layoutParams = layoutParams
            }
        }
    }


    fun setName(name : String)
    {
        this.nameView?.text = name
    }


    fun setDescription(description : String)
    {
        this.descriptionView?.text = description
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}


class SearchResultImageViewHolder(itemView : View, val theme : Theme, val context : Context)
                                    : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout          : LinearLayout? = null
    var iconView        : ImageView? = null
    var nameView        : TextView? = null
    var descriptionView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout          = itemView.findViewById(R.id.layout)
        this.nameView        = itemView.findViewById(R.id.name_view)
        this.descriptionView = itemView.findViewById(R.id.description_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setName(name : String)
    {
        this.nameView?.text = name
    }

    fun setDescription(description : String)
    {
        this.descriptionView?.text = description
    }

    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}



fun searchResultSimpleView(theme : Theme, homeActivity : HomeActivity) : RelativeLayout
{
    val layout = searchResultSimpleViewLayout(homeActivity)

    layout.addView(searchResultSimpleNameView(theme, homeActivity))

    layout.addView(searchResultAddIconView(theme, homeActivity))

    return layout
}


fun searchResultSimpleViewLayout(context : Context) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.margin.topDp     = 4f
    layout.margin.leftDp    = 12f
    layout.margin.rightDp   = 12f

    layout.padding.topDp    = 12f
    layout.padding.bottomDp = 12f

    return layout.relativeLayout(context)
}



private fun searchResultSimpleNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.layoutType        = LayoutType.RELATIVE
    title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
    title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 20f

    return title.textView(context)
}


private fun searchResultSimpleNameSpannable(prefix : String,
                                            suffix : String,
                                            theme : Theme,
                                            context : Context) : SpannableStringBuilder
{
    val builder = SpannableStringBuilder()

    val totalLength = prefix.length + suffix.length

    val sizePx = Util.spToPx(21f, context)
    val sizeSpan = AbsoluteSizeSpan(sizePx)

    // | Prefix
    // ------------------------------------------------

    builder.append(prefix)

    val prefixColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
    val prefixColor              = theme.colorOrBlack(prefixColorTheme)
    val prefixColorSpan = ForegroundColorSpan(prefixColor)

    builder.setSpan(prefixColorSpan, 0, prefix.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setSpan(sizeSpan, 0, prefix.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

    // | Suffix
    // ------------------------------------------------

    builder.append(" ")
    builder.append(suffix)

    val suffixColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    val suffixColor              = theme.colorOrBlack(suffixColorTheme)
    val suffixColorSpan = ForegroundColorSpan(suffixColor)

    builder.setSpan(suffixColorSpan, prefix.length + 1, totalLength + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setSpan(sizeSpan, prefix.length + 1, totalLength + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

    return builder
}


private fun searchResultAddIconView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.margin.rightDp       = 4f

    layout.addRule(RelativeLayout.CENTER_VERTICAL)
    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.widthDp            = 21
    iconView.heightDp           = 21

    iconView.image              = R.drawable.icon_arrow_up_left

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


fun searchResultPageGroupHeaderView(theme : Theme, homeActivity : HomeActivity) : RelativeLayout
{
    val layout = searchResultPageGroupHeaderViewLayout(homeActivity, theme)

    layout.addView(searchResultPageGroupHeaderNameView(theme, homeActivity))

    return layout
}


fun searchResultPageGroupHeaderViewLayout(context : Context, theme : Theme) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.padding.topDp    = 4f

    return layout.relativeLayout(context)
}


private fun searchResultPageGroupHeaderNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.layoutType        = LayoutType.RELATIVE
    title.width             = RelativeLayout.LayoutParams.MATCH_PARENT
    title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
    title.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    title.padding.leftDp    = 12f
    title.padding.rightDp   = 12f
    title.padding.topDp     = 16f
    title.padding.bottomDp  = 8f

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 16f

    return title.textView(context)
}



fun searchResultPageGroupSearchView(theme : Theme, homeActivity : HomeActivity) : LinearLayout
{
    val layout = searchResultPageGroupSearchViewLayout(homeActivity, theme)

    layout.addView(searchResultPageGroupSearchNameView(theme, homeActivity))

    layout.addView(searchResultPageGroupSearchIconView(theme, homeActivity))

    return layout
}


fun searchResultPageGroupSearchViewLayout(context : Context, theme : Theme) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.padding.bottomDp = 4f

    layout.gravity          = Gravity.CENTER_VERTICAL

    return layout.linearLayout(context)
}


private fun searchResultPageGroupSearchNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    //title.layoutType        = LayoutType.RELATIVE
    title.width             = 0
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
    title.weight            = 1f

    title.padding.leftDp    = 12f
    title.padding.rightDp   = 12f
    title.padding.topDp     = 16f
    title.padding.bottomDp  = 16f

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
    title.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 19f

    return title.textView(context)
}


private fun searchResultPageGroupSearchIconView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    //layout.layoutType           = LayoutType.RELATIVE
    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    layout.padding.topDp        = 8f
    layout.padding.bottomDp     = 8f
    layout.padding.rightDp      = 12f

    layout.gravity              = Gravity.CENTER_VERTICAL

//    layout.addRule(RelativeLayout.CENTER_VERTICAL)
//    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.widthDp            = 18
    iconView.heightDp           = 18

    iconView.image              = R.drawable.icon_arrow_forward

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}



fun searchResultIconView(theme : Theme, homeActivity : HomeActivity) : RelativeLayout
{
    val layout = searchResultIconViewLayout(homeActivity)

    val mainLayout = searchResultIconMainViewLayout(homeActivity)

    val headerLayout = searchResultIconHeaderViewLayout(homeActivity)
    headerLayout.addView(searchResultIconIconView(theme, homeActivity))
    headerLayout.addView(searchResultIconNameView(theme, homeActivity))

    val descriptionView = searchResultIconDescriptionView(theme, homeActivity)

    mainLayout.addView(headerLayout)
    mainLayout.addView(descriptionView)

    layout.addView(mainLayout)

    return layout
}


fun searchResultIconViewLayout(context : Context) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.margin.leftDp    = 13f
    layout.margin.rightDp   = 13f

    layout.padding.topDp    = 12f
    layout.padding.bottomDp = 12f

    return layout.relativeLayout(context)
}


fun searchResultIconMainViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.layoutType       = LayoutType.RELATIVE
    layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT
    layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    return layout.linearLayout(context)
}


fun searchResultIconHeaderViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.gravity          = Gravity.CENTER_VERTICAL

    return layout.linearLayout(context)
}



private fun searchResultIconNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.addRule(RelativeLayout.CENTER_VERTICAL)
    title.addRule(RelativeLayout.ALIGN_PARENT_START)

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 20f

    return title.textView(context)
}


private fun searchResultIconDescriptionView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.description_view

    title.layoutType        = LayoutType.RELATIVE
    title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
    title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

    title.addRule(RelativeLayout.CENTER_VERTICAL)
    title.addRule(RelativeLayout.ALIGN_PARENT_START)

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 18f

    return title.textView(context)
}


private fun searchResultIconIconView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.margin.rightDp       = 12f

    layout.addRule(RelativeLayout.CENTER_VERTICAL)
    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.id                 = R.id.icon_view

    iconView.widthDp            = 22
    iconView.heightDp           = 22

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


fun searchResultImageView(theme : Theme, homeActivity : HomeActivity) : ViewGroup
{
    val layout = searchResultImageViewLayout(homeActivity)

    layout.addView(searchResultImageImageView(theme, homeActivity))

    val mainLayout = searchResultImageMainViewLayout(homeActivity)

    mainLayout.addView(searchResultImageNameView(theme, homeActivity))
    mainLayout.addView(searchResultImageDescriptionView(theme, homeActivity))

    layout.addView(mainLayout)

    return layout
}


fun searchResultImageViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.margin.leftDp    = 12f
    layout.margin.rightDp   = 12f

    layout.padding.topDp    = 12f
    layout.padding.bottomDp = 12f

    return layout.linearLayout(context)
}


fun searchResultImageMainViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    return layout.linearLayout(context)
}


private fun searchResultImageNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 21f

    return title.textView(context)
}


private fun searchResultImageDescriptionView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.description_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 16f

    return title.textView(context)
}


private fun searchResultImageImageView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
//    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.margin.rightDp       = 12f

    layout.backgroundResource   = R.drawable.avatar_game

//    layout.addRule(RelativeLayout.CENTER_VERTICAL)
//    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

//    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

//    iconView.id                 = R.id.icon_view
//
//    iconView.widthDp            = 22
//    iconView.heightDp           = 22
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
//    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


fun searchResultSessionView(theme : Theme, homeActivity : HomeActivity) : LinearLayout
{
    val layout = searchResultSessionViewLayout(theme, homeActivity)

    layout.addView(searchResultSessionNameView(theme, homeActivity))

    layout.addView(searchResultSessionDescriptionView(theme, homeActivity))

    layout.addView(searchResultSessionFooterView(theme, homeActivity))

    return layout
}


fun searchResultSessionViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.margin.topDp     = 4f

    layout.padding.topDp    = 6f
    layout.padding.bottomDp = 6f

    return layout.linearLayout(context)
}


private fun searchResultSessionNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.width             = LinearLayout.LayoutParams.MATCH_PARENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.padding.leftDp    = 10f
    title.padding.rightDp   = 10f
    title.padding.topDp     = 10f

    //title.corners           = Corners(3.0, 3.0, 0.0, 0.0)

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
    title.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 20f

    return title.textView(context)
}


private fun searchResultSessionDescriptionView(theme : Theme, context : Context) : TextView
{
    val viewBuilder               = TextViewBuilder()

    viewBuilder.id                = R.id.description_view

    viewBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    viewBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    viewBuilder.padding.leftDp    = 10f
    viewBuilder.padding.rightDp   = 10f

    viewBuilder.padding.topDp    = 4f
    viewBuilder.padding.bottomDp = 4f

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
    viewBuilder.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    viewBuilder.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
    viewBuilder.color           = theme.colorOrBlack(colorTheme)

    viewBuilder.sizeSp          = 18f

    return viewBuilder.textView(context)
}



private fun searchResultSessionFooterView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultSessionFooterViewLayout(context)

    layout.addView(searchResultSessionOpenButtonView(theme, context))

    return layout
}


private fun searchResultSessionFooterViewLayout(context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.backgroundColor      = Color.WHITE

    layout.corners              = Corners(0.0, 0.0, 3.0, 3.0)

    layout.padding.topDp        = 8f
    layout.padding.bottomDp     = 8f

    return layout.linearLayout(context)
}


private fun searchResultSessionOpenButtonView(theme : Theme, context : Context) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder                   = LinearLayoutBuilder()
    val iconViewBuilder                 = ImageViewBuilder()
    val labelViewBuilder                = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width                 = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height                = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

    layoutBuilder.padding.topDp         = 12f
    layoutBuilder.padding.bottomDp      = 12f

    layoutBuilder.margin.topDp          = 8f
    layoutBuilder.margin.leftDp         = 10f
    layoutBuilder.margin.rightDp        = 10f

    layoutBuilder.corners               = Corners(2.0, 2.0, 2.0, 2.0)

    layoutBuilder.gravity               = Gravity.CENTER

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_green"))))
    layoutBuilder.backgroundColor       = theme.colorOrBlack(bgColorTheme)

    layoutBuilder.child(iconViewBuilder)
                 .child(labelViewBuilder)

    // | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 24
    iconViewBuilder.heightDp            = 24

    iconViewBuilder.image               = R.drawable.icon_open

    iconViewBuilder.color               = Color.WHITE

    iconViewBuilder.margin.rightDp      = 8f

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text               = context.getString(R.string.open_session).toUpperCase()

    labelViewBuilder.font               = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Regular,
                                                        context)

    labelViewBuilder.color              = Color.WHITE

    labelViewBuilder.gravity            = Gravity.CENTER

    labelViewBuilder.sizeSp             = 18f

    return layoutBuilder.linearLayout(context)
}



//data class Category(val iconId : Int,
//                    val iconSize : Int,
//                    val smallIconSize : Int,
//                    val name : String) : Serializable



sealed class SearchResult(term : String)



data class SearchResultSimple(val term : String,
                              val prefix : String? = null,
                              val suggestion : String? = null) : SearchResult(term)


data class SearchResultPageGroupHeader(val pageGroupHeader : String) : SearchResult(pageGroupHeader)


data class SearchResultPageGroupSearch(val pageGroupName : String) : SearchResult(pageGroupName)


data class SearchResultHeader(val header : String) : SearchResult(header)


data class SearchResultIcon(val term : String,
                            val description : String,
                            val iconId : Int,
                            val newSearchQuery : String,
                            val iconSize : Int? = null) : SearchResult(term)


data class SearchResultPage(val term : String,
                            val description : String,
                            val newSearchQuery : String,
                            val iconId : Int? = null) : SearchResult(term)



data class SearchResultSession(val name : String,
                               val description : String,
                               val sessionId : SessionId,
                               val iconId : Int? = null) : SearchResult("")






sealed class SearchResultPageTarget

data class SearchResultPageTargetSession(val sessionId : SessionId) : SearchResultPageTarget()


//
//private fun searchResultGameInfoHeaderView(theme : Theme, context : Context) : LinearLayout
//{
//    val layout = searchResultGameInfoHeaderViewLayout(context)
//
//    layout.addView(searchResultGameInfoPlayersView(6701, theme, context))
//
//    layout.addView(searchResultGameInfoRatingView(7, theme, context))
//
//    return layout
//}
//
//
//
//private fun searchResultGameInfoHeaderViewLayout(context : Context) : LinearLayout
//{
//    val layout                  = LinearLayoutBuilder()
//
//    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.orientation          = LinearLayout.HORIZONTAL
//
//    return layout.linearLayout(context)
//}
//
//
//private fun searchResultGameInfoPlayersView(playersCount : Int,
//                                            theme : Theme,
//                                            context : Context) : LinearLayout
//{
//    // 1 | Declarations
//    // -----------------------------------------------------------------------------------------
//
//    val layout                  = LinearLayoutBuilder()
//    val iconView                = ImageViewBuilder()
//    val labelView               = TextViewBuilder()
//
//    // 2 | Layout
//    // -----------------------------------------------------------------------------------------
//
//    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.orientation          = LinearLayout.HORIZONTAL
//
//    layout.gravity              = Gravity.CENTER_VERTICAL
//
//    layout.child(iconView)
//          .child(labelView)
//
//    // 3 | Icon View
//    // -----------------------------------------------------------------------------------------
//
//    iconView.widthDp            = 18
//    iconView.heightDp           = 18
//
//    iconView.image              = R.drawable.icon_users
//
//    val iconColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
//    iconView.color              = theme.colorOrBlack(iconColorTheme)
//
//    // 4 | Label View
//    // -----------------------------------------------------------------------------------------
//
//    labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//    labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    labelView.text              = playersCount.toString()
//
//    labelView.font              = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Bold,
//                                                context)
//
//    val labelColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
//    labelView.color             = theme.colorOrBlack(labelColorTheme)
//
//    labelView.sizeSp            = 18f
//
//
//    return layout.linearLayout(context)
//}
//
//
//
//private fun searchResultGameInfoRatingView(rating : Int,
//                                           theme : Theme,
//                                           context : Context) : LinearLayout
//{
//    val layout = searchResultGameInfoRatingViewLayout(context)
//
//    val total : Int = 10
//    val fullStars = total / rating
//    val halfStars = if (rating % 2 == 0) 0 else 1
//    val emptyStars = 5 - fullStars - halfStars
//
//
//    for (i in 1..fullStars) {
//        layout.addView(searchResultGameInfoRatingIconView(StarType.Full, theme, context))
//    }
//
//    if (halfStars == 1) {
//        layout.addView(searchResultGameInfoRatingIconView(StarType.Half, theme, context))
//    }
//
//    for (i in 1..emptyStars) {
//        layout.addView(searchResultGameInfoRatingIconView(StarType.Empty, theme, context))
//    }
//
//    return layout
//}
//
//
//private fun searchResultGameInfoRatingViewLayout(context : Context) : LinearLayout
//{
//    val layout                  = LinearLayoutBuilder()
//
//    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.orientation          = LinearLayout.HORIZONTAL
//
//    layout.gravity              = Gravity.CENTER_VERTICAL
//
//    return layout.linearLayout(context)
//}
//
//
//sealed class StarType
//{
//
//    object Empty : StarType()
//    object Half : StarType()
//    object Full : StarType()
//}
//
//
//private fun searchResultGameInfoRatingIconView(starType : StarType,
//                                               theme : Theme,
//                                               context : Context) : ImageView
//{
//    val iconView                = ImageViewBuilder()
//
//    iconView.widthDp            = 18
//    iconView.heightDp           = 18
//
//    when (starType) {
//        is StarType.Full -> {
//            iconView.image = R.drawable.icon_star
//        }
//        is StarType.Half -> {
//            iconView.image = R.drawable.icon_star_half
//        }
//        is StarType.Empty -> {
//            iconView.image = R.drawable.icon_star_border
//        }
//    }
//
//    val iconColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
//    iconView.color              = theme.colorOrBlack(iconColorTheme)
//
//
//    return iconView.imageView(context)
//}
