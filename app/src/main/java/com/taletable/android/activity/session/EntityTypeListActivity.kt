
package com.taletable.android.activity.session


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.official.gameManifest
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityKind
import com.taletable.android.util.configureToolbar



/**
 * Entity Type List Activity
 */
class EntityTypeListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameId : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_entity_type_list)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as EntityId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // Toolbar
        this.configureToolbar(getString(R.string.session_types))

        // Theme
        this.applyTheme(officialAppThemeLight)

        // Entity Kind List
        this.initializeContentView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?)
    {
//        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2) //matches the result code passed from B
        {
            if (resultCode == 1) {
                this.finish()
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeContentView()
    {
        val content = this.findViewById<LinearLayout>(R.id.content)

        this.gameId?.let { gameId ->
            val gameSummary = gameManifest(this)?.game(gameId)
            if (gameSummary != null) {
                val entityKindListUI = EntityKindListUI(gameSummary.entityKinds,
                                                        gameId,
                                                        officialThemeLight,
                                                        this)
                content?.addView(entityKindListUI.view())
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



class EntityKindListUI(val entityKinds : List<EntityKind>,
                       val gameId : EntityId,
                       val theme : Theme,
                       val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : ScrollView
    {
        val scrollView = this.scrollView()

        scrollView.addView(this.entityListView())

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
        scrollView.backgroundColor  = theme.colorOrBlack(colorTheme)

        return scrollView.scrollView(context)

    }


    private fun entityListView() : LinearLayout
    {
        val layout          = this.entityListViewLayout()

        entityKinds.forEach {
            layout.addView(this.entityKindView(it))
        }

        return layout
    }


    private fun entityListViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun entityKindView(entityKind : EntityKind) : RelativeLayout
    {
        val layout = this.entityKindViewLayout(entityKind)

        layout.addView(this.entityKindInfoView(entityKind))

        layout.addView(this.nextButtonView())

        return layout
    }


    private fun entityKindViewLayout(entityKind : EntityKind) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 1f

        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.onClick          = View.OnClickListener {
            val activity = context as AppCompatActivity
            val intent = Intent(activity, SessionListActivity::class.java)
            intent.putExtra("game_id", gameId)
            intent.putExtra("entity_kind", entityKind)
            activity.startActivityForResult(intent, 2)
        }

        return layout.relativeLayout(context)
    }


    private fun entityKindInfoView(entityKind : EntityKind) : LinearLayout
    {
        val layout          = this.entityKindInfoViewLayout()

        // Name
        layout.addView(this.entityKindHeaderView(entityKind.namePlural))

        // Description
        layout.addView(this.entityKindDescriptionView(entityKind.description))

        return layout
    }


    private fun entityKindInfoViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.rightDp   = 30f

        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        return layout.linearLayout(context)
    }



    private fun entityKindHeaderView(headerString : String) : TextView
    {
        val header          = TextViewBuilder()

        header.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text         = headerString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        header.color        = theme.colorOrBlack(colorTheme)

        header.font         = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Bold,
                                            context)

        header.sizeSp       = 18f

        return header.textView(context)
    }


    private fun entityKindDescriptionView(descriptionString : String) : TextView
    {
        val description         = TextViewBuilder()

        description.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text        = descriptionString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        description.color        = theme.colorOrBlack(colorTheme)

        description.font         = Font.typeface(TextFont.RobotoCondensed,
                                                 TextFontStyle.Regular,
                                                 context)

        description.sizeSp       = 16.5f

//        description.margin.topDp = 4f

        return description.textView(context)
    }



    private fun nextButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.widthDp          = 30
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.gravity          = Gravity.CENTER

        layout.child(icon)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 30
        icon.heightDp           = 30

        icon.image              = R.drawable.icon_chevron_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }




//
//    private fun entityKindFooterView(entityKind : EntityKind) : RelativeLayout
//    {
//        val layout = this.entityKindFooterViewLayout()
//
//        layout.addView(this.viewSessionsButtonView("View ${entityKind.shortNamePlural}"))
//
//        return layout
//    }
//
//
//    private fun entityKindFooterViewLayout() : RelativeLayout
//    {
//        val layout                      = RelativeLayoutBuilder()
//
//        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation              = LinearLayout.HORIZONTAL
//
//        layout.margin.rightDp           = 8f
//        layout.margin.leftDp            = 8f
//
//        return layout.relativeLayout(context)
//    }
//
//
//    private fun viewSessionsButtonView(labelString : String) : TextView
//    {
//        val buttonView                  = TextViewBuilder()
//
//        buttonView.layoutType           = LayoutType.RELATIVE
//        buttonView.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
//        buttonView.height               = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        buttonView.addRule(RelativeLayout.ALIGN_PARENT_END)
//
//        buttonView.margin.topDp         = 10f
//        buttonView.margin.bottomDp      = 4f
////        buttonView.margin.rightDp       = 8f
//
//        buttonView.text                 = labelString.toUpperCase()
//
//        buttonView.font                 = Font.typeface(TextFont.RobotoCondensed,
//                                                        TextFontStyle.Bold,
//                                                        context)
//
//        val nameColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//        buttonView.color                = theme.colorOrBlack(nameColorTheme)
//
//        buttonView.sizeSp               = 15f
//
//        return buttonView.textView(context)
//    }
//

}


