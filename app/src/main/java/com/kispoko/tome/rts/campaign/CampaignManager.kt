
package com.kispoko.tome.rts.campaign


import com.kispoko.tome.app.AppCampaignError
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import effect.*
import lulo.schema.Schema



/**
 * Campaign Manager
 */
object CampaignManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var specification : Schema? = null

    private val campaign = "campaign"

    private val campaignById : MutableMap<CampaignId, Campaign> = mutableMapOf()



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun addCampaign(campaign : Campaign)
    {
        this.campaignById.put(campaign.campaignId(), campaign)
    }


    fun openCampaigns() : List<Campaign> = this.campaignById.values.toList()


    fun hasCampaignWithId(campaignId : CampaignId) : Boolean =
            this.campaignById.containsKey(campaignId)


    fun campaignWithId(campaignId : CampaignId) : AppEff<Campaign> =
        note(this.campaignById[campaignId],
             AppCampaignError(CampaignDoesNotExist(campaignId)))

}
