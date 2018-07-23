
package com.taletable.android.rts.entity.campaign


import com.taletable.android.app.ApplicationError
import com.taletable.android.model.campaign.CampaignId



/**
 * Campaign Error
 */
sealed class CampaignError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class CampaignDoesNotExist(val campaignId : CampaignId) : CampaignError()
{
    override fun debugMessage() : String = """
            |Campaign Error: Game Not Found
            |    Campaign Id: $campaignId
            """

    override fun logMessage(): String = userMessage()
}

