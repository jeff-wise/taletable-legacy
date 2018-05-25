
package com.kispoko.tome.activity.session


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
import android.view.WindowManager
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.book.BookActivity
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookReferenceBook
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.rts.entity.Entity
import com.kispoko.tome.rts.session.Session
import com.kispoko.tome.rts.session.activeSession
import com.kispoko.tome.util.configureToolbar
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

        this.configureToolbar(getString(R.string.session))

        this.applyTheme(officialThemeLight)

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
                val sessionUI = SessionUI(activeSession.value, officialThemeLight, this)
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

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
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



class SessionUI(val session : Session,
                val theme : Theme,
                val sessionActivity : SessionActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : ScrollView
    {
        val scrollView = scrollView()

        scrollView.addView(this.entityListView())

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height           = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)
    }


    private fun entityListView() : LinearLayout
    {
        val layout = this.entityListViewLayout()

        val recordMap = session.entityRecordsByType()

        recordMap.keys.sortedBy { it }.forEach { entityType ->

            val entities = recordMap[entityType]

            layout.addView(this.headerView(entityType))

            val typeLayout = this.entityTypeLayout()
            entities?.forEach {
                typeLayout.addView(entityView(it))
            }

            layout.addView(typeLayout)
        }

        return layout
    }


    private fun entityListViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f

        layout.padding.bottomDp = 72f

        return layout.linearLayout(context)
    }


    private fun entityTypeLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    private fun entityView(entity : Entity) : RelativeLayout
    {
        val layout = this.entityViewLayout()

        layout.addView(this.entityNameView(entity.name()))

        layout.addView(this.entityNextIconView())

        layout.setOnClickListener {
            when (entity) {
                is Book -> {
                    val intent = Intent(sessionActivity, BookActivity::class.java)
                    val bookReference = BookReferenceBook(entity.bookId())
                    intent.putExtra("book_reference", bookReference)
                    sessionActivity.startActivity(intent)
                }
            }

        }

        return layout
    }


    private fun entityNameView(nameString : String) : TextView
    {
        val name                = TextViewBuilder()

        name.layoutType         = LayoutType.RELATIVE
        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.ALIGN_PARENT_START)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        name.text               = nameString

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        name.color              = theme.colorOrBlack(colorTheme)

        name.sizeSp             = 20f

        return name.textView(context)
    }


    private fun entitySummaryView(summaryString : String) : TextView
    {
        val summary             = TextViewBuilder()

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


    private fun entityViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.backgroundColor      = Color.WHITE

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.padding.leftDp       = 6f
        layout.padding.rightDp      = 6f

        return layout.relativeLayout(context)
    }


    private fun entityNextIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 30
        icon.heightDp           = 30

        icon.image              = R.drawable.icon_chevron_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        icon.color          = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }


    fun entityFooterView() : LinearLayout
    {
        val layout = entityFooterViewLayout()

        // OPEN BUTTON
        layout.addView(entityFooterButtonView(context.getString(R.string.go_to).toUpperCase()))

        return layout
    }


    private fun entityFooterViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.rightDp  = 6f

        return layout.linearLayout(context)
    }


    private fun entityFooterButtonView(label : String) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        button.text                 = label

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        button.margin.leftDp       = 25f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_90"))))
        button.color                = theme.colorOrBlack(colorTheme)

        button.sizeSp               = 15f

        return button.textView(context)
    }


    fun headerView(labelString : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 12f
        layout.margin.bottomDp  = 1f

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f
        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f

        layout.child(label)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = "${labelString}s"

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        label.color             = theme.colorOrBlack(colorTheme)

        label.sizeSp            = 16f

        return layout.linearLayout(context)
    }

}

