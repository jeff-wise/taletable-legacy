
package com.kispoko.tome.rts.campaign


import com.kispoko.tome.app.AppCampaignError
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.functor.Prod
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import effect.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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

    private val session : MutableMap<CampaignId,CampaignRecord> = mutableMapOf()



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun addCampaignToSession(campaign : Campaign, isSaved : Boolean)
    {
        val campaignRecord = CampaignRecord(campaign)
        this.session.put(campaign.campaignId(), campaignRecord)

        // Save if needed
        if (!isSaved)
            launch(UI) { campaignRecord.save() }
    }


    fun openCampaigns() : List<Campaign> = this.session.values.map { it.campaign() }


    fun campaignWithId(campaignId : CampaignId) : AppEff<Campaign> =
        note(this.session[campaignId]?.campaign(),
             AppCampaignError(CampaignDoesNotExist(campaignId)))

}



// ---------------------------------------------------------------------------------------------
// CAMPAIGN RECORD
// ---------------------------------------------------------------------------------------------

data class CampaignRecord(val campaign : Prod<Campaign>)
{

    constructor(campaign : Campaign) : this(Prod(campaign))


    fun campaign() : Campaign = this.campaign.value

    /**
     * This method saves the entire campaign in the database. It is intended to be used to saveSheet
     * a campaign that has just been loaded and has not ever been saved.
     *
     * This method is run asynchronously in the `CommonPool` context.
     */
    suspend fun save()
    {
        this.campaign.saveAsync(true, true)
    }

}
