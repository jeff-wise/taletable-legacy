
package com.taletable.android.activity.home


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.activity.search.*
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
class HomeActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // | PROPERTIES
    // -----------------------------------------------------------------------------------------

    var hasSavedSessions : Boolean = false

    var selectedSession : Session? = null

    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()



    private var searchHistory : MutableList<String> = mutableListOf()


    // -----------------------------------------------------------------------------------------
    // | ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)


        with(window) {

            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                enterTransition = Slide(Gravity.START)
                allowEnterTransitionOverlap = true
            } else {
                // Swap without transition
            }
        }


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


    private fun initializeSearchView()
    {
        this.findViewById<TextView>(R.id.searchbar_text_view)?.let { textView ->
            textView.textSize = Util.spToPx(5f, this).toFloat()
            textView.typeface = Font.typeface(TextFont.RobotoCondensed, TextFontStyle.Bold, this)
        }

        val searchFragment = SearchFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .add(R.id.content, searchFragment)
                .commit()
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



