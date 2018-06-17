
package com.kispoko.tome.rts.session


import com.kispoko.tome.rts.entity.EntityId
import java.io.Serializable



/**
 * Official Sheet Loaded
 */
//data class SessionMessageOfficialSheetLoaded(val sheetId : SheetId) : Serializable
//
//
//data class SessionMessageOfficialCampaignLoaded(val campaignId : CampaignId) : Serializable
//
//
//data class SessionMessageOfficialGameLoaded(val gameId : GameId) : Serializable


sealed class MessageSessionLoad : Serializable


data class MessageSessionLoaded(val sessionId : SessionId) : MessageSessionLoad()

data class MessageSessionEntityLoaded(val update : SessionLoadUpdate) : MessageSessionLoad()
