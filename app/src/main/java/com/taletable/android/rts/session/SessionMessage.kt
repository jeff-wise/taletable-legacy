
package com.taletable.android.rts.session


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
