
package com.kispoko.tome.official


import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId



// ---------------------------------------------------------------------------------------------
// SHEET
// ---------------------------------------------------------------------------------------------

/**
 * Official Sheet
 */
data class OfficialSheet(val sheetId : SheetId,
                         val campaignId : CampaignId,
                         val gameId : GameId)
{

    val filePath = officialDirectoryPath +
                    "/" + gameId.value +
                     "/sheets/" + sheetId.value + ".yaml"


    fun officialCampaign() = OfficialCampaign(this.campaignId, this.gameId)

}


// ---------------------------------------------------------------------------------------------
// CAMPAIGN
// ---------------------------------------------------------------------------------------------

/**
 * Official Campaign
 */
data class OfficialCampaign(val campaignId : CampaignId, val gameId : GameId)
{

    val filePath = officialDirectoryPath +
            "/" + gameId.value +
            "/campaigns/" + campaignId.value + ".yaml"


    fun officialGame() = OfficialGame(this.gameId)
}


// ---------------------------------------------------------------------------------------------
// GAME
// ---------------------------------------------------------------------------------------------

/**
 * Official Game
 */
data class OfficialGame(val gameId : GameId)
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



//
//class OfficialIndex(val sheets: List<OfficialSheet>,
//                    val campaigns : List<OfficialCampaign>,
//                    val games : List<OfficialGame>,
//                    val themes : List<OfficialTheme>)
//{
//
//    val sheetById : Map<SheetId,OfficialSheet> = sheets.associateBy { it.sheetId }
//
//    val campaignById : Map<CampaignId,OfficialCampaign> = campaigns.associateBy { it.campaignId }
//
//    val gameById : Map<GameId,OfficialGame> = games.associateBy { it.gameId }
//
//
//    companion object
//    {
//        fun load(context : Context) : OfficialIndex?
//        {
//            val manifestFilePath = ApplicationAssets.officialDirectoryPath + "/manifest.yaml"
//
//            val officialIndexParser = parseYaml(context.assets.open(manifestFilePath),
//                                                this::fromYaml)
//
//            when (officialIndexParser)
//            {
//                is Val -> return officialIndexParser.value
//            }
////
////            val parse = YamlString.parse(context.assets.open(manifestFilePath))
////            when (parse)
////            {
////                is YamlParseValue ->
////                {
////                    val yamlParse = fromYaml(parse.value)
////                    when (yamlParse)
////                    {
////                        is Val ->
////                        is Err -> {
////                            Log.d("***OFFICIAL", yamlParse.toString())
////                            return null
////                        }
////                    }
////                }
////                is YamlParseError -> pa
////            }
//
//            return null
//        }
//
//
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<OfficialIndex> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                apply(::OfficialIndex,
//                      // Sheets
//                      yamlValue.array("sheets") ap {
//                          it.mapApply { OfficialSheet.fromYaml(it) }},
//                      // Campaigns
//                      yamlValue.array("campaigns") ap {
//                          it.mapApply { OfficialCampaign.fromYaml(it) }},
//                      // Games
//                      yamlValue.array("games") ap {
//                          it.mapApply { OfficialGame.fromYaml(it) }},
//                      // Themes
//                      yamlValue.array("themes") ap {
//                          it.mapApply { OfficialTheme.fromYaml(it) }}
//                     )
//            }
//            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//
//    }
//}
//
//

