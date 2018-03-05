
package com.kispoko.tome.official


import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId



// ---------------------------------------------------------------------------------------------
// SHEET
// ---------------------------------------------------------------------------------------------

/**
 * Official Sheet Id
 */
data class OfficialSheetId(val sheetId : SheetId,
                           val campaignId : CampaignId,
                           val gameId : GameId)
{

    val filePath = officialDirectoryPath +
                    "/" + gameId.value +
                     "/sheets/" + sheetId.value + ".yaml"


    fun officialCampaignId() = OfficialCampaignId(this.campaignId, this.gameId)

}


// ---------------------------------------------------------------------------------------------
// CAMPAIGN
// ---------------------------------------------------------------------------------------------

/**
 * Official Campaign
 */
data class OfficialCampaignId(val campaignId : CampaignId, val gameId : GameId)
{

    val filePath = officialDirectoryPath +
            "/" + gameId.value +
            "/campaigns/" + campaignId.value + ".yaml"


    fun officialGameId() = OfficialGameId(this.gameId)
}


// ---------------------------------------------------------------------------------------------
// GAME
// ---------------------------------------------------------------------------------------------

/**
 * Official Game
 */
data class OfficialGameId(val gameId : GameId)
{

    val filePath = officialDirectoryPath + "/" + gameId.value +
                        "/" + gameId.value +  ".yaml"


}


// ---------------------------------------------------------------------------------------------
// THEME
// ---------------------------------------------------------------------------------------------

sealed class OfficialTheme
{


    abstract val filePath : String


    object Light: OfficialTheme()
    {
        override val filePath = ApplicationAssets.officialThemeDirectoryPath + "/light.yaml"

        override fun toString() : String = "Light Theme"

    }

    object Dark: OfficialTheme()
    {
        override val filePath = ApplicationAssets.officialThemeDirectoryPath + "/dark.yaml"

        override fun toString() : String = "Dark Theme"

    }


    override fun toString() : String = when (this)
    {
        is Light -> "Light"
        is Dark  -> "Dark"
    }
}


// ---------------------------------------------------------------------------------------------
// FILE DIRECTORIES
// ---------------------------------------------------------------------------------------------

val officialDirectoryPath = "official"


