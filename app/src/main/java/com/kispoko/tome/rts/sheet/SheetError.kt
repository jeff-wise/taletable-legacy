
package com.kispoko.tome.rts.sheet


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.section.SectionName
import com.kispoko.tome.model.sheet.widget.table.TableWidgetCellType
import com.kispoko.tome.model.sheet.widget.table.TableWidgetColumnType
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ThemeId



/**
 * Sheet Error
 */
sealed class SheetError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class SheetDoesNotExist(val sheetId : SheetId, val context : String = "Unknown") : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Sheet Not Found
                Sheet Id: $sheetId
            """

    override fun logMessage(): String = userMessage()
}


class CampaignDoesNotExist(val sheetId : SheetId, val campaignId : CampaignId) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Campaign Not Found
                Sheet Id: $sheetId
                Campaign Id: $campaignId
            """

    override fun logMessage(): String = userMessage()
}


class SectionDoesNotExist(val sheetId : SheetId, val sectionName : SectionName) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Sheet Not Found
                Sheet Id: $sheetId
                Section Name: $sectionName
            """

    override fun logMessage(): String = userMessage()
}



/**
 * The theme does not have a color with the given id.
 */
class PageFragmentIsMissingContext() : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Page Fragment has a null SheetGameContext
            """

    override fun logMessage(): String = userMessage()
}


/**
 * The theme does not have a color with the given id.
 */
class CellTypeDoesNotMatchColumnType(val cellType : TableWidgetCellType,
                                     val columnType : TableWidgetColumnType) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Cell Type Does Not Match Its Column Type
                Cell Type: $cellType
                Column Type: $columnType
            """

    override fun logMessage(): String = userMessage()
}