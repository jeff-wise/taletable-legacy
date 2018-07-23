
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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.taletable.android.R
import com.taletable.android.R.string.name
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.Book
import com.taletable.android.model.book.BookReferenceBook
import com.taletable.android.model.campaign.Campaign
import com.taletable.android.model.game.Game
import com.taletable.android.model.sheet.Sheet
import com.taletable.android.model.sheet.style.Corners
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

        recyclerView.layoutManager      = GridLayoutManager(context, 2)

        recyclerView.adapter            = ActiveSessionRecyclerViewAdapter(session.entities(),
                                                                           theme,
                                                                           context)

        recyclerView.padding.leftDp     = 2f
        recyclerView.padding.rightDp    = 2f

        recyclerView.padding.topDp      = 4f

        recyclerView.padding.bottomDp   = 60f
        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }


}



// -----------------------------------------------------------------------------------------
// ENTITY CARD VIEW
// -----------------------------------------------------------------------------------------

fun entityCardView(theme : Theme, sessionActivity : SessionActivity) : LinearLayout
{
    val layout = entityCardViewLayout(sessionActivity)

    layout.addView(entityCardNameView(theme, sessionActivity))

    layout.addView(entityCardDefaultImageView(theme, sessionActivity))

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

    layout.elevation            = 3f

    layout.gravity              = Gravity.CENTER

    layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

    layout.padding.topDp        = 10f
    layout.padding.bottomDp     = 10f

    layout.padding.leftDp       = 8f
    layout.padding.rightDp      = 8f

    layout.margin.rightDp       = 1f
    layout.margin.leftDp        = 1f

    layout.margin.bottomDp      = 2f

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

    name.sizeSp             = 18f

    return name.textView(context)
}


private fun entityCardDefaultImageView(theme : Theme, context : Context) : LinearLayout
{
    // (1) Declarations
    // -----------------------------------------------------------------------------------------

    val layout              = LinearLayoutBuilder()
    val imageView           = ImageViewBuilder()

    // (2) Layout
    // -----------------------------------------------------------------------------------------

    layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
//    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.margin.topDp     = 8f
    layout.margin.bottomDp     = 8f

    layout.child(imageView)

    // (3) Image
    // -----------------------------------------------------------------------------------------

    imageView.id            = R.id.image_view

    imageView.widthDp       = 30
    imageView.heightDp      = 30

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    imageView.color         = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


private fun entityCardSummaryView(summaryString : String,
                                  theme : Theme,
                                  context : Context) : TextView
{
    val summary             = TextViewBuilder()

    summary.id              = R.id.entity_card_summary

    summary.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    summary.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    summary.text             = summaryString

    summary.font             = Font.typeface(TextFont.default(),
                                             TextFontStyle.Regular,
                                             context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
    summary.color            = theme.colorOrBlack(colorTheme)

    summary.sizeSp           = 16f

    return summary.textView(context)
}




// -----------------------------------------------------------------------------------------
// RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class ActiveSessionRecyclerViewAdapter(val entityList : List<Entity>,
                                       val theme : Theme,
                                       val sessionActivity : SessionActivity)
        : RecyclerView.Adapter<EntityCardViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : EntityCardViewHolder
    {
        return EntityCardViewHolder(entityCardView(theme, sessionActivity),
                                    theme,
                                    sessionActivity)
    }


    override fun onBindViewHolder(viewHolder : EntityCardViewHolder, position : Int)
    {
        this.entityList[position]?.let {
            viewHolder.setEntity(it)
        }
    }


    override fun getItemCount() = this.entityList.size

}





// ---------------------------------------------------------------------------------------------
// VIEW HOLDER
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
    var imageView               : ImageView? = null


    val context = sessionActivity

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout             = itemView.findViewById(R.id.entity_card_layout)
        this.nameView           = itemView.findViewById(R.id.entity_card_name)
        this.summaryView        = itemView.findViewById(R.id.entity_card_summary)
        this.imageView          = itemView.findViewById(R.id.image_view)
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


        var defaultDrawable : Drawable? = null
        when (entity)
        {
            is Sheet    -> defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_document)
            is Campaign -> defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_adventure)
            is Game     -> defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_dice_roll)
            is Book     -> defaultDrawable = ContextCompat.getDrawable(context, R.drawable.icon_open_book)
        }

        this.imageView?.setImageDrawable(defaultDrawable)
    }



}

