
package com.taletable.android.rts.entity.campaign


import com.taletable.android.model.campaign.Campaign
import com.taletable.android.rts.entity.EntityId
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

    private val session : MutableMap<EntityId,CampaignRecord> = mutableMapOf()



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

//    fun addCampaignToSession(campaign : Campaign, isSaved : Boolean)
//    {
//        val campaignRecord = CampaignRecord(campaign)
//        this.session[campaign.entityId()] = campaignRecord
//
//        // Save if needed
//        if (!isSaved)
//            launch(UI) { campaignRecord.save() }
//    }


    fun openCampaigns() : List<Campaign> = this.session.values.map { it.campaign() }


//    fun campaignWithId(campaignId : CampaignId) : AppEff<Campaign> =
//        note(this.session[campaignId]?.campaign(),
//             AppCampaignError(CampaignDoesNotExist(campaignId)))

}



// ---------------------------------------------------------------------------------------------
// CAMPAIGN RECORD
// ---------------------------------------------------------------------------------------------

data class CampaignRecord(private val campaign : Campaign)
{

    fun campaign() : Campaign = this.campaign

    /**
     * This method saves the entire campaign in the database. It is intended to be used to saveSheet
     * a campaign that has just been loaded and has not ever been saved.
     *
     * This method is run asynchronously in the `CommonPool` context.
     */
    suspend fun save()
    {
        //this.campaign.saveAsync(true, true)
    }

}
