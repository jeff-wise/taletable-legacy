
package com.kispoko.tome.activity.entity.engine.function


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Form
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.engine.function.Function
import com.kispoko.tome.model.engine.function.FunctionId
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.game.GameManager
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Function Activity
 */
class FunctionActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var functionId  : FunctionId? = null
    private var gameId      : GameId?     = null

    private var function    : Function? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_form)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("function_id"))
            this.functionId = this.intent.getSerializableExtra("function_id") as FunctionId

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as GameId

        // (3) Lookup Function
        // -------------------------------------------------------------------------------------

        val gameId = this.gameId
        val functionId = this.functionId
        if (gameId != null && functionId != null)
        {
            val function = GameManager.engine(gameId)
                                      .apply { it.function(functionId) }

            when (function) {
                is Val -> this.function = function.value
                is Err -> ApplicationLog.error(function.error)
            }
        }

        // (4) Initialize Views
        // -------------------------------------------------------------------------------------

        val function = this.function

        // TOOLBAR
        if (function != null)
        {
            // Toolbar Title
            this.configureToolbar(function.labelString())

            // Form Toolbar
            val formToolbarLayout = this.findViewById<LinearLayout>(R.id.form_toolbar)
            formToolbarLayout?.addView(Form.toolbarView(this.appSettings.themeId(), this))
        }

        // THEME
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // FORM
        if (function != null)
        {
            val contentLayout = this.findViewById<ScrollView>(R.id.content)
            val viewBuilder = FunctionFormViewBuilder(function, this.appSettings.themeId(), this)
            contentLayout?.addView(viewBuilder.view())
        }
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------


    private fun applyTheme(uiColors : UIColors)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = this.appSettings.color(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_back_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val searchButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        searchButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TOOLBAR TITLE
        // -------------------------------------------------------------------------------------
        val toolbarTitleView = this.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

    }

}


// ---------------------------------------------------------------------------------------------
// FUNCTION FORM VIEW
// ---------------------------------------------------------------------------------------------

class FunctionFormViewBuilder(val function : Function,
                              val themeId : ThemeId,
                              val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Function Id
        layout.addView(Form.textFieldView(context.getString(R.string.function_field_id_label),
                                          function.functionId().value,
                                          themeId,
                                          context))

        // -------------------------------
        layout.addView(Form.dividerView(themeId, context))

        // Label
        layout.addView(Form.textFieldView(context.getString(R.string.function_field_label_label),
                                          function.labelString(),
                                          themeId,
                                          context))

        // -------------------------------
        layout.addView(Form.dividerView(themeId, context))

        // Description
        layout.addView(Form.textFieldView(context.getString(R.string.function_field_description_label),
                                          function.descriptionString(),
                                          themeId,
                                          context))

        // -------------------------------
        layout.addView(Form.dividerView(themeId, context))

        // Type Signature
        layout.addView(Form.modelFieldView(context.getString(R.string.function_field_type_signature_label),
                                           themeId,
                                           context))

        // -------------------------------
        layout.addView(Form.dividerView(themeId, context))

        // Tuples
        layout.addView(Form.listFieldView(context.getString(R.string.function_field_tuples_label),
                                          themeId,
                                          context))

        // -------------------------------
        layout.addView(Form.dividerView(themeId, context))

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }

}
