
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Open Sheet Activity
 */
class OpenSheetActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_open_sheet)

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.open_sheet))

        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        val contentLayout = this.findViewById(R.id.content) as LinearLayout
        contentLayout.addView(this.view(this))
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
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_back_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

    }


    private fun view(context : Context) : LinearLayout
    {
        val layout = this.viewLayout(context)

        // Official Header
        // -------------------------------------------------------------------------------------

        layout.addView(this.headerView(R.string.official, context))

        // Template Section
        // -------------------------------------------------------------------------------------

        val fromTemplateButtonView =
                this.buttonView(R.drawable.icon_new_file,
                                R.string.sheet_from_official_template,
                                R.string.sheet_from_official_template_description,
                                context)
        layout.addView(fromTemplateButtonView)

        // Divider
        // -------------------------------------------------------------------------------------
        layout.addView(this.dividerView(context))

        // Import Header
        // -------------------------------------------------------------------------------------

        layout.addView(this.headerView(R.string.import_s, context))

        // > File
        // -------------------------------------------------------------------------------------
        val fromFileButtonView = this.buttonView(R.drawable.icon_upload_file,
                                                 R.string.sheet_from_file,
                                                 R.string.sheet_from_file_description,
                                                 context)
        layout.addView(fromFileButtonView)

        // > Community
        // -------------------------------------------------------------------------------------
        val communityButtonView = this.buttonView(R.drawable.icon_cloud_download,
                                                  R.string.sheet_from_community,
                                                  R.string.sheet_from_community_description,
                                                  context)
        layout.addView(communityButtonView)

        // Divider
        // -------------------------------------------------------------------------------------
        layout.addView(this.dividerView(context))

        // New Section
        // -------------------------------------------------------------------------------------

        layout.addView(this.headerView(R.string.new_s, context))

        val duplicateButtonView = this.buttonView(R.drawable.icon_copy,
                                                  R.string.sheet_from_duplicate,
                                                  R.string.sheet_from_duplicate_description,
                                                  context)
        layout.addView(duplicateButtonView)

        val emptyButtonView = this.buttonView(R.drawable.icon_add_file,
                                              R.string.sheet_from_blank,
                                              R.string.sheet_from_blank_description,
                                              context)
        layout.addView(emptyButtonView)

        return layout
    }


    private fun viewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = this.appSettings.color(colorTheme)

        layout.padding.topDp    = 15f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        return layout.linearLayout(context)
    }


    private fun dividerView(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = this.appSettings.color(colorTheme)

        layout.margin.topDp     = 15f
        layout.margin.bottomDp  = 15f

        return layout.linearLayout(context)
    }


    private fun headerView(textId : Int, context : Context) : TextView
    {
        val header          = TextViewBuilder()

        header.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId       = textId

        header.gravity      = Gravity.CENTER_VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_blue_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color        = this.appSettings.color(colorTheme)

        header.font         = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            context)

        header.sizeSp       = 14f

        return header.textView(context)
    }


    private fun buttonView(iconId : Int,
                           labelId : Int,
                           descriptionId : Int,
                           context : Context) : LinearLayout
    {
        val layout              = this.buttonViewLayout(context)

        // Header
        layout.addView(this.buttonHeaderView(iconId, labelId, context))

        // Description
        layout.addView(this.buttonDescriptionView(descriptionId, context))

        return layout
    }


    private fun buttonViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.topDp     = 15f

        return layout.linearLayout(context)
    }


    private fun buttonHeaderView(iconId : Int, labelId : Int, context : Context) : LinearLayout
    {
        val layout = this.buttonHeaderViewLayout(context)

        // Icon
        layout.addView(this.buttonIconView(iconId, context))

        // Label
        layout.addView(this.buttonLabelView(labelId, context))

        return layout
    }


    private fun buttonDescriptionView(descriptionId : Int, context : Context) : TextView
    {
        val description             = TextViewBuilder()

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        description.textId          = descriptionId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        description.color           = this.appSettings.color(colorTheme)

        description.font            = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        description.sizeSp          = 14f

        return description.textView(context)

    }


    private fun buttonHeaderViewLayout(context : Context) : LinearLayout
    {
         val layout             = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    private fun buttonLabelView(labelId : Int, context : Context) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = this.appSettings.color(colorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        label.sizeSp            = 18f

        return label.textView(context)
    }


    private fun buttonIconView(iconId : Int, context : Context) : ImageView
    {
        val icon                = ImageViewBuilder()

        icon.widthDp            = 17
        icon.heightDp           = 17

        icon.image              = iconId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_19")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = this.appSettings.color(colorTheme)

        icon.margin.rightDp     = 5f

        return icon.imageView(context)
    }

}

