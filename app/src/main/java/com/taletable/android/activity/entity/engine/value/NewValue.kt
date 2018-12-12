
package com.taletable.android.activity.entity.engine.value


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.app.AppSettings
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.Form
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.engine.value.Value
import com.taletable.android.model.engine.value.ValueSet
import com.taletable.android.model.engine.value.ValueSetId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.model.theme.UIColors
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.game.GameManager
import com.taletable.android.rts.entity.theme.ThemeManager
import com.taletable.android.util.configureToolbar
import effect.Err
import effect.Val



/**
 * New Value Activity
 */
class NewValueActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameId     : EntityId?   = null
    private var valueSetId : ValueSetId? = null

    private var valueSet   : ValueSet?   = null
    private var value      : Value?      = null

    private val appSettings    : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_form_new)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as EntityId

        if (this.intent.hasExtra("value_set_id"))
            this.valueSetId = this.intent.getSerializableExtra("value_set_id") as ValueSetId


        // (3) Get Value Set
        // -------------------------------------------------------------------------------------

        val valueSetId = this.valueSetId
        val gameId = this.gameId
        if (valueSetId != null && gameId != null)
        {
            val valueSet = GameManager.engine(gameId)
                                .apply { it.valueSet(valueSetId)  }
            when (valueSet)
            {
                is Val -> this.valueSet = valueSet.value
                is Err -> ApplicationLog.error(valueSet.error)
            }

        }

        // (4) Create New Value
        // -------------------------------------------------------------------------------------



        // (5) Initialize Views
        // -------C------------------------------------------------------------------------------

        val value = this.value

        // TOOLBAR
        if (value != null)
        {
            // Toolbar Title
            this.configureToolbar(this.getString(R.string.value))

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
        if (value != null)
        {
            val contentLayout = this.findViewById<ScrollView>(R.id.content)
            val viewBuilder = NewValueFormViewBuilder(value, this.appSettings.themeId(), this)
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

//        val searchButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
//        searchButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TOOLBAR TITLE
        // -------------------------------------------------------------------------------------
        val toolbarTitleView = this.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

    }

}


// ---------------------------------------------------------------------------------------------
// VALUE FORM VIEW
// ---------------------------------------------------------------------------------------------

class NewValueFormViewBuilder(val value : Value,
                           val themeId : ThemeId,
                           val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Value Id
        layout.addView(Form.textFieldView(context.getString(R.string.value_field_id_label),
                                          value.valueId().value,
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