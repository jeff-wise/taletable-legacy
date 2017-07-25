
package com.kispoko.tome.rts.campaign


import android.content.Context
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.*
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.official.OfficialCampaign
import com.kispoko.tome.official.OfficialIndex
import com.kispoko.tome.rts.game.GameManager
import effect.Err
import effect.Val
import effect.effError
import effect.effValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import lulo.document.SpecDoc
import lulo.spec.Spec
import java.io.InputStream



/**
 * Campaign Manager
 */
object CampaignManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var specification : Spec? = null

    private val campaign = "campaign"

    private val campaignById : MutableMap<CampaignId, Campaign> = mutableMapOf()


    // -----------------------------------------------------------------------------------------
    // SPECIFICATION
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Campaign specification (Lulo). If it is null, try to load it.
     */
    fun specification(context : Context) : Spec?
    {
        if (this.specification == null)
            this.loadSpecification(context)

        return this.specification
    }


    /**
     * Get the specification in the Loader context.
     */
    fun specificationLoader(context : Context) : Loader<Spec>
    {
        val currentSpecification = this.specification(context)
        if (currentSpecification != null)
            return effValue(currentSpecification)
        else
            return effError(SpecIsNull(campaign))
    }


    /**
     * Load the specification. If it fails, report any errors.
     */
    fun loadSpecification(context : Context)
    {
        val specLoader = loadLuloSpecification(campaign, context)
        when (specLoader)
        {
            is Val -> this.specification = specLoader.value
            is Err -> ApplicationLog.error(specLoader.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // OFFICIAL
    // -----------------------------------------------------------------------------------------

    suspend fun loadOfficialCampaign(officialCampaign: OfficialCampaign,
                                     officialIndex : OfficialIndex,
                                     context : Context) : LoadResult<Campaign> = run(CommonPool,
    {

        loadOfficialGame(officialCampaign, officialIndex, context)

        val campaignLoader = _loadOfficialCampaign(officialCampaign, context)
        when (campaignLoader)
        {
            is Val ->
            {
                val campaign = campaignLoader.value
                this.campaignById.put(campaign.campaignId.value, campaign)
                LoadResultValue(campaign)
            }
            is Err ->
            {
                val loadError = campaignLoader.error
                ApplicationLog.error(loadError)
                LoadResultError<Campaign>(loadError.userMessage())
            }
        }
    })


    private fun _loadOfficialCampaign(officialCampaign : OfficialCampaign,
                                      context : Context) : Loader<Campaign>
    {
        // LET...
        fun templateFileString(inputStream: InputStream) : Loader<String> =
            effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String,
                             campaignSpec : Spec,
                             gameSpec : Spec) : Loader<SpecDoc>
        {
            val docParse = campaignSpec.parseDocument(templateString,
                                                      listOf(gameSpec))
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(officialCampaign.campaignId.value,
                                                             campaign,
                                                             docParse.error))
            }
        }

        fun campaignFromDocument(specDoc : SpecDoc) : Loader<Campaign>
        {
            val sheetParse = Campaign.fromDocument(specDoc)
            when (sheetParse)
            {
                is Val -> return effValue(sheetParse.value)
                is Err -> return effError(ValueParseError(officialCampaign.campaignId.value,
                                                          sheetParse.error))
            }
        }

        // DO...
        return assetInputStream(context, officialCampaign.filePath)
                .apply(::templateFileString)
                .applyWith(::templateDocument,
                           CampaignManager.specificationLoader(context),
                           GameManager.specificationLoader(context))
                .apply(::campaignFromDocument)
    }


    private suspend fun loadOfficialGame(officialCampaign : OfficialCampaign,
                                         officialIndex : OfficialIndex,
                                         context : Context)
    {
        if (!GameManager.hasGameWithId(officialCampaign.gameId))
        {
            val officialGame = officialIndex.gameById[officialCampaign.gameId]
            if (officialGame != null)
                GameManager.loadOfficialGame(officialGame, context)
            // TODO errors here
        }
    }



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun openCampaigns() : List<Campaign> = this.campaignById.values.toList()


    fun hasCampaignWithId(campaignId : CampaignId) : Boolean =
            this.campaignById.containsKey(campaignId)


    fun campaignWithId(campaignId : CampaignId) : Campaign? = this.campaignById[campaignId]

}
