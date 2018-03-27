
package com.kispoko.tome.activity.sheet


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.Theme
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.util.configureToolbar



/**
 * Save Sheet Activity
 */
class SaveSheetActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var sheetId : SheetId? = null

    private var fab : FloatingActionButton? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_save_sheet)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("sheet_id"))
            this.sheetId = this.intent.getSerializableExtra("sheet_id") as SheetId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        this.configureToolbar("Save Sheet")

        // > Theme
        this.applyTheme(officialThemeLight)

        // > Initialize FAB
        this.initializeFAB()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeFAB()
    {
        val fab = this.findViewById(R.id.fab) as FloatingActionButton
        this.fab = fab
    }


    private fun initializeViews()
    {
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
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_main_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }

}
