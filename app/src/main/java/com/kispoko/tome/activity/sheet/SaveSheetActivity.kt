
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.R.string.value
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.rts.entity.EntitySheetId
import com.kispoko.tome.rts.entity.textVariable
import com.kispoko.tome.util.configureToolbar
import maybe.Just


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

        // Toolbar
        this.configureToolbar("Save Sheet")

        // Theme
        this.applyTheme(officialThemeLight)

        // Initialize FAB
        this.initializeFAB()

        // Initialize Views
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

    private fun initializeFAB()
    {
//        val fab = this.findViewById(R.id.fab) as FloatingActionButton
//        this.fab = fab
    }


    private fun initializeViews()
    {
        val sheetId = this.sheetId
        if (sheetId != null)
        {
            val content = this.findViewById<LinearLayout>(R.id.content)
            val saveSheetUI = SaveSheetUI(sheetId, officialThemeLight, this)
            content.addView(saveSheetUI.view())
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


class SaveSheetUI(val sheetId : SheetId,
                  val theme : Theme,
                  val context : Context)
{


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun save()
    {

        // create new session

        // save sheet, get row id

    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // File Name
//        layout.addView(this.fileNameView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 2f
        layout.padding.rightDp  = 2f

        layout.padding.topDp    = 10f

        return layout.linearLayout(context)
    }


    private fun headerView(labelStringId : Int, index : Int) : TextView
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val index                   = TextViewBuilder()
        val label                   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.child(index)
              .child(label)

        // (3) Index
        // -------------------------------------------------------------------------------------

        index.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        index.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        index.text                  = index.toString()

        val indexColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        label.color                 = theme.colorOrBlack(indexColorTheme)

        label.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        label.sizeSp                = 14f

        // (4) Label
        // -------------------------------------------------------------------------------------

        label.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId                 = labelStringId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        label.color                  = theme.colorOrBlack(labelColorTheme)

        label.font                   = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        label.sizeSp                 = 22f

        return label.textView(context)
    }


    private fun bottomBorderView(): LinearLayout
    {
        val divider                 = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_9"))))
        divider.backgroundColor     = theme.colorOrBlack(colorTheme)

        divider.margin.topDp        = 10f

        return divider.linearLayout(context)
    }


    // VIEW > File Name
    // -----------------------------------------------------------------------------------------

    private fun fileNameInputView() : LinearLayout
    {
        val layout = this.fileNameInputViewLayout()

        layout.addView(this.fileNameInputValueView())

        layout.addView(this.bottomBorderView())

        return layout
    }


    private fun fileNameInputViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.padding.topDp    = 10f

        return layout.linearLayout(context)
    }


    private fun fileNameInputValueView() : EditText
    {
        val value                   = EditTextBuilder()

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        value.color                 = theme.colorOrBlack(textColorTheme)

        value.sizeSp                = 20f

        value.backgroundResource    = R.drawable.bg_edit_text_no_style

        value.gravity               = Gravity.TOP

        value.padding.leftDp        = 8f
        value.padding.rightDp       = 8f

        textVariable(VariableId("name"), EntitySheetId(sheetId)) apDo { textVar ->
        textVar.value(EntitySheetId(sheetId))                    apDo { maybeName ->
            when (maybeName) {
                is Just    -> value.text = maybeName.value
                is Nothing -> value.text = "Sheet Name"
            }
        } }

        return value.editText(context)
    }


}

