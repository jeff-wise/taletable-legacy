
package com.kispoko.tome.activity


import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.session.GameActionLoadSession
import com.kispoko.tome.activity.session.GameActionNewSession
import com.kispoko.tome.activity.session.GamesListActivity
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import java.io.InputStream



/**
 * Main Activity
 */
class MainActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_main)

        // (2) Initialize Views
        // -------------------------------------------------------------------------------------

        // Toolbar
//        this.configureToolbar("Tome")

        // Theme
        this.applyTheme(officialThemeLight)

        this.initializeViews()
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
        val content = this.findViewById<LinearLayout>(R.id.content)

        val mainUI = MainUI(officialThemeLight, this)
        content?.addView(mainUI.view())
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
//        val toolbar = findViewById(R.id.toolbar) as Toolbar?
//
//        // Toolbar > Background
//        toolbar?.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))
//
//        // Toolbar > Icons
//        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())
//
//        val menuLeftButton = this.findViewById(R.id.toolbar_main_button) as ImageButton?
//        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
//
//        val menuRightButton = this.findViewById(R.id.toolbar_options_button) as ImageButton?
//        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
//        val titleView = this.findViewById(R.id.toolbar_title) as TextView
//        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))
    }

}



class MainUI(val theme : Theme,
             val activity : AppCompatActivity)
{


    val context = activity as Context


    fun view() : View
    {
        val layout = this.viewLayout()

        // Logo
        layout.addView(this.logoView())

        // Buttons
        layout.addView(this.buttonsView())

        return layout
    }


    private fun viewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

//        layout.backgroundBitmapPath = "images/logo/splash.png"

        return layout.relativeLayout(context)
    }


    // VIEWS > Logo
    // -----------------------------------------------------------------------------------------

    private fun logoView() : ImageView
    {
        val image               = ImageViewBuilder()

        image.layoutType        = LayoutType.RELATIVE
        image.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        image.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        image.addRule(RelativeLayout.CENTER_VERTICAL)

        image.scaleType         = ImageView.ScaleType.FIT_XY
        image.adjustViewBounds  = true

        image.margin.leftDp     = 90f
        image.margin.rightDp    = 90f

        image.padding.bottomDp  = 30f

        val imagePath = "images/logo/logo.png"

        var stream : InputStream? = null
        try
        {
            stream = context.assets.open(imagePath)
            if (stream != null)
                image.bitmap = BitmapFactory.decodeStream(stream)
        }
        finally {
            try
            {
                if(stream != null)
                {
                    stream.close()
                }
            } catch (e : Exception) {}
        }

        return image.imageView(context)
    }


    // VIEWS > Buttons
    // -----------------------------------------------------------------------------------------

    private fun buttonsView() : LinearLayout
    {
        val layout = this.buttonsViewLayout()

        // Load Session
        layout.addView(this.loadSessionButtonView())

        // New Session
        layout.addView(this.newSessionButtonView())

        return layout
    }


    private fun buttonsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        layout.orientation      = LinearLayout.VERTICAL

        layout.layoutGravity    = Gravity.CENTER_HORIZONTAL

        layout.margin.leftDp    = 34f
        layout.margin.rightDp   = 34f

        layout.margin.bottomDp  = 40f

        return layout.linearLayout(context)
    }


    private fun loadSessionButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val label                   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER

        layout.backgroundResource   = R.drawable.bg_button_main

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.onClick          = View.OnClickListener {
            val intent = Intent(activity, GamesListActivity::class.java)
            intent.putExtra("game_action", GameActionLoadSession)
            activity.startActivity(intent)
        }

        layout.child(label)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.load_session

        label.color             = Color.WHITE

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        label.sizeSp            = 22f

        return layout.linearLayout(context)
    }


    private fun newSessionButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val label           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

//        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.margin.topDp     = 20f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_tint_5"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        layout.corners          = Corners(4.0, 4.0, 4.0, 4.0)

        layout.onClick          = View.OnClickListener {
            val intent = Intent(activity, GamesListActivity::class.java)
            intent.putExtra("game_action", GameActionNewSession)
            activity.startActivity(intent)
        }

        layout.child(label)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.new_session

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        label.color             = theme.colorOrBlack(colorTheme)

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        label.sizeSp            = 22f

        return layout.linearLayout(context)
    }

}