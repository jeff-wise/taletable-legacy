
package com.kispoko.tome.activity.sheet.group


import android.content.Context
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
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.engine.tag.TagQuery
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.group.GroupReference
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.groups
import com.kispoko.tome.util.configureToolbar



/**
 * Group List Activity
 */
class GroupListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var groupRefs : List<GroupReference> = listOf()
    private var title     : String?              = null
    private var tagQuery  : TagQuery?            = null
    private var entityId  : EntityId?            = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_group_list)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("title"))
            this.title = this.intent.getStringExtra("title")

        if (this.intent.hasExtra("group_references"))
            this.groupRefs = this.intent.getSerializableExtra("group_references") as List<GroupReference>

        if (this.intent.hasExtra("tag_query"))
            this.tagQuery = this.intent.getSerializableExtra("tag_query") as TagQuery

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
            val groupListUI = GroupListUI(this.groupRefs,
                                          this.tagQuery,
                                          officialAppThemeLight,
                                          it,
                                          this)
            contentView?.addView(groupListUI.view())
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

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.setSystemUiVisibility(flags)
            this.getWindow().setStatusBarColor(Color.WHITE);
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



class GroupListUI(val groupRefs : List<GroupReference>,
                  val query : TagQuery?,
                  val theme : Theme,
                  val entityId : EntityId,
                  val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val scrollView = this.scrollView()

        val listView = this.listView()
        scrollView.addView(listView)

        groups(this.groupRefs, entityId).forEach {
            listView.addView(this.groupItemView(it))
        }

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)
    }


    private fun listView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


    // VIEWS > Group Item
    // -----------------------------------------------------------------------------------------

    private fun groupItemView(group : Group) : LinearLayout
    {
        val layout = this.groupItemViewLayout()

        // Drag Icon
        layout.addView(this.dragHandleIconView())

        // Info
        layout.addView(this.groupItemContentView(group.name().value, group.summary().value))

        return layout
    }


    private fun groupItemViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f
        layout.margin.topDp     = 2f

        return layout.linearLayout(context)
    }


    private fun groupItemContentView(name : String, summary : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val nameView            = TextViewBuilder()
        val summaryView         = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.margin.leftDp        = 8f

        layout.child(nameView)
              .child(summaryView)

        // (3 A) Name
        // -------------------------------------------------------------------------------------

        nameView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        nameView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        nameView.text               = name

        nameView.font               = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        nameView.color              = theme.colorOrBlack(nameColorTheme)

        nameView.sizeSp             = 18f

        // (3 B) Summary
        // -------------------------------------------------------------------------------------

        summaryView.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summaryView.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summaryView.text            = summary

        summaryView.font            = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val summaryColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        summaryView.color           = theme.colorOrBlack(summaryColorTheme)

        summaryView.sizeSp          = 16f

        return layout.linearLayout(context)
    }



    private fun dragHandleIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.child(iconView)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 30
        iconView.heightDp           = 30

        iconView.image              = R.drawable.icon_drag_handle

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        iconView.color              = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }

}
