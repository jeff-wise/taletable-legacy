
package com.taletable.android.activity.session


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.Book
import com.taletable.android.model.book.BookReferenceBook
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.Entity
import com.taletable.android.rts.session.Session
import com.taletable.android.rts.session.activeSession
import com.taletable.android.util.configureToolbar
import maybe.Just



/**
 * Session Activity
 */
class SessionActivity : AppCompatActivity()
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

        setContentView(R.layout.activity_session)

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.session),
                              TextFont.RobotoCondensed,
                              TextFontStyle.Bold,
                              18f)

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

        val activeSession = activeSession()
        when (activeSession)
        {
            is Just -> {
                val sessionUI = ActiveSessionUI(activeSession.value, officialThemeLight, this)
                contentView.addView(sessionUI.view())
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

//            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())

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


class ActiveSessionUI(val session : Session,
                      val theme : Theme,
                      val sessionActivity : SessionActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // | VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() = activeSessionRecyclerView()


    private fun activeSessionRecyclerView() : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

//        recyclerView.layoutManager      = GridLayoutManager(context, 2)
        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = ActiveSessionRecyclerViewAdapter(session.entityAndHeaders(),
                                                                           theme,
                                                                           context)

        recyclerView.padding.topDp      = 6f

        recyclerView.padding.bottomDp   = 60f
        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }


}


// | VIEW > Header
// -----------------------------------------------------------------------------------------

fun headerView(theme : Theme, sessionActivity : SessionActivity) : LinearLayout
{
    val layout = headerViewLayout(sessionActivity)

    // layout.addView(headerIconView(theme, sessionActivity))

    layout.addView(headerLabelView(theme, sessionActivity))

    return layout
}


private fun headerViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.gravity          = Gravity.CENTER_VERTICAL

    layout.margin.bottomDp  = 4f
    layout.margin.leftDp    = 12f
    layout.margin.rightDp   = 12f

    return layout.linearLayout(context)
}


private fun headerLabelView(theme : Theme,
                            context : Context) : TextView
{
    val name                = TextViewBuilder()

    name.id                 = R.id.label_view

    name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    name.font               = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
    name.color              = theme.colorOrBlack(colorTheme)

    name.sizeSp             = 16f

    return name.textView(context)
}


private fun headerIconView(theme : Theme, context : Context) : LinearLayout
{
    // (1) Declarations
    // -----------------------------------------------------------------------------------------

    val layout              = LinearLayoutBuilder()
    val imageView           = ImageViewBuilder()

    // (2) Layout
    // -----------------------------------------------------------------------------------------

    layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.child(imageView)

    // (3) Image
    // -----------------------------------------------------------------------------------------

    imageView.id            = R.id.icon_view

    imageView.widthDp       = 16
    imageView.heightDp      = 16

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
    imageView.color         = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


// | VIEW > Entiy Card
// -----------------------------------------------------------------------------------------

fun entityCardView(theme : Theme, sessionActivity : SessionActivity) : LinearLayout
{
    val layout = entityCardViewLayout(sessionActivity)

    layout.addView(entityCardNameView(theme, sessionActivity))

    layout.addView(entityCardSummaryView(theme, sessionActivity))

    return layout
}


private fun entityCardViewLayout(context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.id                   = R.id.entity_card_layout

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

    layout.backgroundColor      = Color.WHITE

    layout.elevation            = 2f

    layout.gravity              = Gravity.CENTER_VERTICAL

    layout.padding.topDp        = 8f
    layout.padding.bottomDp     = 8f

    layout.padding.leftDp       = 12f
    layout.padding.rightDp      = 12f

    layout.margin.bottomDp      = 4f

    return layout.linearLayout(context)
}


private fun entityCardNameView(theme : Theme,
                               context : Context) : TextView
{
    val name                = TextViewBuilder()

    name.id                 = R.id.entity_card_name

    name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    name.font               = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Bold,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    name.color              = theme.colorOrBlack(colorTheme)

    name.sizeSp             = 19f

    name.margin.bottomDp    = 2f

    return name.textView(context)
}



private fun entityCardSummaryView(theme : Theme,
                                  context : Context) : TextView
{
    val summary             = TextViewBuilder()

    summary.id              = R.id.entity_card_summary

    summary.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    summary.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    summary.font             = Font.typeface(TextFont.RobotoCondensed,
                                             TextFontStyle.Regular,
                                             context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    summary.color            = theme.colorOrBlack(colorTheme)

    summary.sizeSp           = 17f

    return summary.textView(context)
}


// -----------------------------------------------------------------------------------------
// RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class ActiveSessionRecyclerViewAdapter(val items : List<Any>,
                                       val theme : Theme,
                                       val sessionActivity : SessionActivity)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // | Properties
    // -------------------------------------------------------------------------------------

    private val HEADER = 0
    private val ENTITY  = 1


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
        val itemAtPosition = this.items[position]

        return when (itemAtPosition) {
            is String   -> HEADER
            is Entity   -> ENTITY
            else        -> ENTITY
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder = when (viewType)
    {
        HEADER ->
        {
            val headerView = headerView(theme, sessionActivity)
            HeaderViewHolder(headerView, theme, sessionActivity)
        }
        else ->
        {
            val cardView = entityCardView(theme, sessionActivity)
            EntityCardViewHolder(cardView, theme, sessionActivity)
        }
    }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val item = this.items[position]

        when (item) {
            is String -> {
                val headerViewHolder = viewHolder as HeaderViewHolder
                headerViewHolder.setHeader(item)
            }
            is Entity -> {
                val entityViewHolder = viewHolder as EntityCardViewHolder
                entityViewHolder.setEntity(item)
            }
        }
    }


    override fun getItemCount() = this.items.size

}





// ---------------------------------------------------------------------------------------------
// | View Holder: Entity Card
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class EntityCardViewHolder(itemView : View,
                           val theme : Theme,
                           val sessionActivity : SessionActivity)
                           : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout                  : LinearLayout? = null
    var nameView                : TextView? = null
    var summaryView             : TextView? = null

    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout             = itemView.findViewById(R.id.entity_card_layout)
        this.nameView           = itemView.findViewById(R.id.entity_card_name)
        this.summaryView        = itemView.findViewById(R.id.entity_card_summary)
    }



    fun setEntity(entity : Entity)
    {
        this.layout?.setOnClickListener {
            when (entity) {
                is Book -> {
                    val intent = Intent(sessionActivity, BookActivity::class.java)
                    val bookReference = BookReferenceBook(entity.entityId())
                    intent.putExtra("book_reference", bookReference)
                    sessionActivity.startActivity(intent)
                }
            }
        }

        this.nameView?.text = entity.name()

        this.summaryView?.text = entity.summary()
    }



}


// ---------------------------------------------------------------------------------------------
// | View Holder: Header
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class HeaderViewHolder(itemView : View,
                       val theme : Theme,
                       val sessionActivity : SessionActivity)
                       : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var textView : TextView? = null
    var iconView : ImageView? = null

    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.textView = itemView.findViewById(R.id.label_view)
        this.iconView = itemView.findViewById(R.id.icon_view)
    }



    fun setHeader(header : String)
    {
        var defaultDrawable : Drawable? = null
        when (header)
        {
            "Sheet"    -> {
                defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_document)
            }
            "Campaign" -> {
                defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_adventure)
            }
            "Game"     -> {
                defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_die)
            }
            "Book"     -> {
                defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_book)
            }
        }

        this.textView?.text = header
        // this.iconView?.setImageDrawable(defaultDrawable)
    }



}

