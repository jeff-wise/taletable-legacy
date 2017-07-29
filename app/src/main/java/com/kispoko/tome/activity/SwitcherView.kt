
package com.kispoko.tome.activity


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.campaign.CampaignManager
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import android.content.Intent
import com.kispoko.tome.activity.game.GameActivity
import com.kispoko.tome.activity.sheet.OpenSheetActivity
import com.kispoko.tome.activity.sheet.SheetActivity



/**
 * SwitcherView
 */
object SwitcherView
{

    fun view(sheetUIContext : SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

//        layout.addView(this.dividerView(sheetUIContext))
        layout.addView(this.sheetSwitcherView(sheetUIContext))

//        layout.addView(this.dividerView(sheetUIContext))
        layout.addView(this.campaignSwitcherView(sheetUIContext))

        //layout.addView(this.dividerView(sheetUIContext))
        layout.addView(this.gameSwitcherView(sheetUIContext))

        return layout
    }


    fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 40f

        val labelCcolorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, labelCcolorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun dividerView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 2

        val labelCcolorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, labelCcolorTheme)

        layout.margin.topDp     = 15f
        layout.margin.bottomDp  = 15f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // GENERAL VIEWS
    // -----------------------------------------------------------------------------------------

    fun switcherViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.topDp     = 20f

        return layout.linearLayout(context)
    }


    fun switcherHeaderView(labelId : Int,
                           onNewButtonClick : View.OnClickListener,
                           sheetUIContext : SheetUIContext) : LinearLayout
    {

        val layout          = this.switcherHeaderViewLayout(sheetUIContext)

        layout.addView(this.switcherLabelView(labelId, sheetUIContext))

        val newButton = this.newButtonView(sheetUIContext)
        newButton.setOnClickListener(onNewButtonClick)

        layout.addView(newButton)

        return layout
    }


    private fun switcherHeaderViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 54

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.leftDp    = 5f
        layout.margin.rightDp   = 5f

        layout.padding.leftDp   = 5f

        val labelBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, labelBgColorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(1f),
                                          TopRightCornerRadius(1f),
                                          BottomRightCornerRadius(1f),
                                          BottomLeftCornerRadius(1f))

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun switcherLabelView(labelId : Int, sheetUIContext : SheetUIContext) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
        label.weight            = 4f

        label.text              = sheetUIContext.context.getString(labelId).toUpperCase()

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.layoutGravity     = Gravity.CENTER_VERTICAL

        val labelCcolorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, labelCcolorTheme)


        label.sizeSp            = 13f

        return label.textView(sheetUIContext.context)
    }


    private fun newButtonView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout  = LinearLayoutBuilder()
        val icon    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp           = 25
        icon.heightDp          = 25
        icon.weight            = 1f

        icon.image             = R.drawable.ic_switcher_new

        icon.layoutGravity     = Gravity.CENTER_VERTICAL

        val buttonColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_17")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color             = SheetManager.color(sheetUIContext.sheetId, buttonColorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    fun switcherCardHeaderView(nameString : String, sheetUIContext : SheetUIContext) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        header.text             = nameString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color            = SheetManager.color(sheetUIContext.sheetId, colorTheme)


        header.sizeSp           = 15.5f

        return header.textView(sheetUIContext.context)
    }


    fun switcherCardSummaryView(summaryString : String,
                                sheetUIContext : SheetUIContext) : TextView
    {
        val summary             = TextViewBuilder()

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.font            = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        summary.text            = summaryString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        summary.color           = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        summary.sizeSp          = 13f

        return summary.textView(sheetUIContext.context)
    }


    fun switcherListViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 5f
        layout.padding.rightDp  = 5f

        layout.margin.topDp     = 5f

        return layout.linearLayout(context)
    }


    fun switcherCardViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f
        layout.padding.topDp    = 7f
        layout.padding.bottomDp = 7f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(1f),
                                          TopRightCornerRadius(1f),
                                          BottomRightCornerRadius(1f),
                                          BottomLeftCornerRadius(1f))

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // GAME SWITCHER VIEW
    // -----------------------------------------------------------------------------------------

    fun gameSwitcherView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherViewLayout(sheetUIContext.context)

        val onNewGameClick = View.OnClickListener {  }

        layout.addView(this.switcherHeaderView(R.string.open_games,
                                               onNewGameClick,
                                               sheetUIContext))

        layout.addView(this.openGamesView(sheetUIContext))

        return layout
    }


    fun openGamesView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherListViewLayout(sheetUIContext.context)

        GameManager.openGames().forEach {
            layout.addView(this.openGameView(it, sheetUIContext))
        }

        return layout
    }


    fun openGameView(game : Game,
                     sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherCardViewLayout(sheetUIContext)

        layout.addView(this.switcherCardHeaderView(game.description().gameName(), sheetUIContext))

        layout.addView(this.switcherCardSummaryView(game.description().summary(), sheetUIContext))

        val sheetActivity = sheetUIContext.context as SheetActivity
        layout.setOnClickListener {
            val intent = Intent(sheetActivity, GameActivity::class.java)
            intent.putExtra("game", game)
            sheetActivity.startActivity(intent)
        }

        return layout
    }



    // -----------------------------------------------------------------------------------------
    // CAMPAIGN SWITCHER VIEW
    // -----------------------------------------------------------------------------------------

    fun campaignSwitcherView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherViewLayout(sheetUIContext.context)

        val onNewCampaignClick = View.OnClickListener {  }

        layout.addView(this.switcherHeaderView(R.string.open_campaigns,
                                               onNewCampaignClick,
                                               sheetUIContext))

        layout.addView(this.openCampaignsView(sheetUIContext))

        return layout
    }


    fun openCampaignsView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherListViewLayout(sheetUIContext.context)

        CampaignManager.openCampaigns().forEach {
            layout.addView(this.openCampaignView(it.campaignName(),
                                                 it.campaignSummary(),
                                                 sheetUIContext))
        }

        return layout
    }


    fun openCampaignView(campaignName : String,
                         campaignSummary : String,
                         sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherCardViewLayout(sheetUIContext)

        layout.addView(this.switcherCardHeaderView(campaignName, sheetUIContext))

        layout.addView(this.switcherCardSummaryView(campaignSummary, sheetUIContext))

        return layout
    }


    // -----------------------------------------------------------------------------------------
    // SHEET SWITCHER VIEW
    // -----------------------------------------------------------------------------------------

    fun sheetSwitcherView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherViewLayout(sheetUIContext.context)

        val sheetActivity = sheetUIContext.context as SheetActivity
        val onNewSheetClick = View.OnClickListener {
            val intent = Intent(sheetActivity, OpenSheetActivity::class.java)
            sheetActivity.startActivity(intent)
        }

        layout.addView(this.switcherHeaderView(R.string.open_sheets,
                                               onNewSheetClick,
                                               sheetUIContext))

        layout.addView(this.openSheetsView(sheetUIContext))

        return layout
    }


    fun openSheetsView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherListViewLayout(sheetUIContext.context)

        SheetManager.openSheets().forEach {
            val sheetName = SheetManager.evalSheetName(sheetUIContext.sheetId,
                                                       it.settings().sheetName())
            val sheetSummary = SheetManager.evalSheetSummary(sheetUIContext.sheetId,
                                                             it.settings().sheetSummary())
            layout.addView(this.openSheetView(sheetName, sheetSummary, sheetUIContext))
        }

        return layout
    }


    fun openSheetView(sheetName : String,
                      sheetSummary : String,
                      sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherCardViewLayout(sheetUIContext)

        layout.addView(this.switcherCardHeaderView(sheetName, sheetUIContext))

        layout.addView(this.switcherCardSummaryView(sheetSummary, sheetUIContext))

        return layout
    }


}

