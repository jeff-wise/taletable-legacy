
package com.kispoko.tome.rts.entity


import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.ThemeId
import java.io.Serializable



// ---------------------------------------------------------------------------------------------
// ENTITY CONTEXT
// ---------------------------------------------------------------------------------------------

sealed class EntityContext(val entityId : EntityId) : Serializable


data class EntitySheetContext(val sheetId : SheetId,
                              val campaignId : CampaignId,
                              val gameId : GameId) : EntityContext(EntitySheetId(sheetId))
{

    val entitySheetId    = EntitySheetId(sheetId)
    val entityCampaignId = EntityCampaignId(campaignId)
    val entityGameId     = EntityGameId(gameId)
}



// ---------------------------------------------------------------------------------------------
// ENTITY ID
// ---------------------------------------------------------------------------------------------

sealed class EntityId : Serializable

data class EntitySheetId(val sheetId : SheetId) : EntityId()

data class EntityCampaignId(val campaignId : CampaignId) : EntityId()

data class EntityGameId(val gameId : GameId) : EntityId()

data class EntityThemeId(val themeId : ThemeId) : EntityId()

data class EntityBookId(val bookId : BookId) : EntityId()

