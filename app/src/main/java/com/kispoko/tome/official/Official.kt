
package com.kispoko.tome.official


import android.content.Context
import android.util.Log
import com.kispoko.culebra.*
import com.kispoko.culebra.Parser as YamlParser
import com.kispoko.culebra.Result as YamlResult
import com.kispoko.culebra.Error as YamlError
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId



/**
 * Official Sheet
 */
data class OfficialSheet(val sheetId : SheetId,
                         val campaignId : CampaignId,
                         val gameId : GameId)
{

    val filePath =
        ApplicationAssets.officialDirectoryPath +
            "/" + gameId.value +
            "_" + campaignId.value +
            "_" + sheetId.value + ".yaml"


    companion object
    {

        fun fromYaml(yamlValue : YamlValue) : YamlParser<OfficialSheet> = when (yamlValue)
        {
            is YamlDict ->
            {
                parserApply3(::OfficialSheet,
                             // Sheet Id
                             yamlValue.text("sheet_id") ap { result(SheetId(it)) },
                             // Campaign Id
                             yamlValue.text("campaign_id") ap { result(CampaignId(it)) },
                             // Game Id
                             yamlValue.text("game_id") ap { result(GameId(it)) }
                             )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue)))
        }

    }

}


/**
 * Official Campaign
 */
data class OfficialCampaign(val campaignId : CampaignId,
                            val gameId : GameId)
{

    val filePath =
        ApplicationAssets.officialDirectoryPath +
            "/" + gameId.value +
            "_" + campaignId.value + ".yaml"


    companion object
    {

        fun fromYaml(yamlValue : YamlValue) : YamlParser<OfficialCampaign> = when (yamlValue)
        {
            is YamlDict ->
            {
                parserApply2(::OfficialCampaign,
                             // Campaign Id
                             yamlValue.text("campaign_id") ap { result(CampaignId(it)) },
                             // Game Id
                             yamlValue.text("game_id") ap { result(GameId(it)) }
                             )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue)))
        }

    }

}


/**
 * Official Game
 */
data class OfficialGame(val gameId : GameId)
{

    val filePath =
        ApplicationAssets.officialDirectoryPath +
            "/" + gameId.value + ".yaml"


    companion object
    {

        fun fromYaml(yamlValue : YamlValue) : YamlParser<OfficialGame> = when (yamlValue)
        {
            is YamlDict ->
            {
                parserApply(::OfficialGame,
                             // Game Id
                             yamlValue.text("game_id") ap { result(GameId(it)) }
                             )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue)))
        }

    }

}




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


    companion object
    {

        fun fromYaml(yamlValue : YamlValue) : YamlParser<OfficialTheme> = when (yamlValue)
        {
            is YamlText ->
            {
                when (yamlValue.text)
                {
                    "light" -> result<OfficialTheme>(OfficialTheme.Light)
                    "dark"  -> result<OfficialTheme>(OfficialTheme.Dark)
                    else    -> error(UnexpectedStringValue(yamlValue.text))
                }
            }
            else -> error(UnexpectedTypeFound(YamlType.TEXT, yamlType(yamlValue)))
        }

    }
}


class OfficialIndex(val sheets: List<OfficialSheet>,
                    val campaigns : List<OfficialCampaign>,
                    val games : List<OfficialGame>,
                    val themes : List<OfficialTheme>)
{

    val sheetById : Map<SheetId,OfficialSheet> = sheets.associateBy { it.sheetId }

    val campaignById : Map<CampaignId,OfficialCampaign> = campaigns.associateBy { it.campaignId }

    val gameById : Map<GameId,OfficialGame> = games.associateBy { it.gameId }


    companion object
    {
        fun load(context : Context) : OfficialIndex?
        {
            val manifestFilePath = ApplicationAssets.officialDirectoryPath + "/manifest.yaml"

            val parse = YamlString.parse(context.assets.open(manifestFilePath))
            when (parse)
            {
                is StringResult ->
                {
                    val yamlParse = fromYaml(parse.value)
                    when (yamlParse)
                    {
                        is YamlResult -> return yamlParse.value
                        is YamlError  -> {
                            Log.d("***OFFICIAL", yamlParse.toString())
                            return null
                        }
                    }
                }
                is StringErrors -> parse.errors.forEach { Log.d("***OFFICIAL", it.toString()) }
            }

            return null
        }


        private fun fromYaml(yamlValue : YamlValue) : YamlParser<OfficialIndex> = when (yamlValue)
        {
            is YamlDict ->
            {
                parserApply4(::OfficialIndex,
                             // Sheets
                             yamlValue.array("sheets") ap { yamlList ->
                                 yamlList.map { OfficialSheet.fromYaml(it) }},
                             // Campaigns
                             yamlValue.array("campaigns") ap { yamlList ->
                                 yamlList.map { OfficialCampaign.fromYaml(it) }},
                             // Games
                             yamlValue.array("games") ap { yamlList ->
                                 yamlList.map { OfficialGame.fromYaml(it) }},
                             // Themes
                             yamlValue.array("themes") ap { yamlList ->
                                 yamlList.map { OfficialTheme.fromYaml(it) }}
                            )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue)))
        }

    }
}

