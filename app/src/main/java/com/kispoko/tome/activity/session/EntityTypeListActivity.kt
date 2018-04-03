
package com.kispoko.tome.activity.session


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.ScrollViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.official.GameSummary
import com.kispoko.tome.rts.entity.EntityKind



/**
 * Entity Type List Activity
 */
class EntityTypeListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameSummary : GameSummary? = null


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

        if (this.intent.hasExtra("game_summary"))
            this.gameSummary = this.intent.getSerializableExtra("game_summary") as GameSummary

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // Toolbar
        this.initializeToolbarView(officialThemeLight)

        // Theme
        this.applyTheme(officialThemeLight)

        // Entity Kind List
        this.initializeContentView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeToolbarView(theme : Theme)
    {
        // Back label text
        val backLabelView = this.findViewById(R.id.toolbar_back_label) as TextView
        backLabelView.typeface = Font.typeface(TextFont.default(), TextFontStyle.default(), this)
        backLabelView.text     = getString(R.string.back_to_games)

        val backLabelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_15"))))
        backLabelView.setTextColor(theme.colorOrBlack(backLabelColorTheme))

        // Back button
        val backButton = this.findViewById(R.id.toolbar_back_button) as LinearLayout?
        backButton?.setOnClickListener {
            this.finish()
        }

        // Breadcrumbs
        val breadcrumbsLayout = this.findViewById(R.id.breadcrumbs) as LinearLayout?

        val breadcrumbs : MutableList<String> = mutableListOf()
        this.gameSummary?.let { breadcrumbs.add(it.name) }
        breadcrumbs.add(getString(R.string.what_are_you_looking_for))

        val breadcrumbsUI = SessionBreadcrumbsUI(breadcrumbs, true, officialThemeLight, this)
        breadcrumbsLayout?.addView(breadcrumbsUI.view())
    }


    private fun initializeContentView()
    {
        val content = this.findViewById(R.id.content) as LinearLayout?

        this.gameSummary?.let {
            val entityKindListUI = EntityKindListUI(it, officialThemeLight, this)
            content?.addView(entityKindListUI.view())
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

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

    }

}



class EntityKindListUI(val gameSummary : GameSummary,
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        scrollView.backgroundColor  = theme.colorOrBlack(colorTheme)

        return scrollView.scrollView(context)

    }


    private fun entityListView() : LinearLayout
    {
        val layout          = this.entityListViewLayout()

        gameSummary.entityKinds.forEach {
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

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f

        return layout.linearLayout(context)
    }


    private fun entityKindView(entityKind : EntityKind) : LinearLayout
    {
        val layout          = this.entityKindViewLayout(entityKind)

        // Name
        layout.addView(this.entityKindHeaderView(entityKind.name))

        // Description
        layout.addView(this.entityKindDescriptionView(entityKind.description))

        return layout
    }


    private fun entityKindViewLayout(entityKind : EntityKind) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 8f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.onClick          = View.OnClickListener {
            val activity = context as AppCompatActivity
            val intent = Intent(activity, SessionListActivity::class.java)
            intent.putExtra("game_summary", gameSummary)
            intent.putExtra("entity_kind", entityKind)
            activity.startActivity(intent)
        }

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color        = theme.colorOrBlack(colorTheme)

        header.font         = Font.typeface(TextFont.default(),
                                            TextFontStyle.Medium,
                                            context)

        header.sizeSp       = 20f

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color        = theme.colorOrBlack(colorTheme)

        description.font         = Font.typeface(TextFont.default(),
                                                 TextFontStyle.Regular,
                                                 context)

        description.sizeSp       = 17f

        description.margin.topDp = 4f

        return description.textView(context)
    }


}


