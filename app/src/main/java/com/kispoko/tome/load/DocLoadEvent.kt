
package com.kispoko.tome.load


import com.kispoko.tome.app.ApplicationEvent



/**
 * Doc Load Event
 */
sealed class DocLoadEvent : ApplicationEvent


data class SchemaLoaded(val schemaName : String) : DocLoadEvent()
{
    override fun debugMessage() : String = """Schema Loaded:
            |    Name: $schemaName""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class OfficialSheetLoaded(val sheetName : String) : DocLoadEvent()
{
    override fun debugMessage() : String = """Official Sheet Loaded:
            |    Name: $sheetName""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class OfficialCampaignLoaded(val campaignName : String) : DocLoadEvent()
{
    override fun debugMessage() : String = """Official Campaign Loaded:
            |    Name: $campaignName""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class OfficialGameLoaded(val gameName : String) : DocLoadEvent()
{
    override fun debugMessage() : String = """Official Game Loaded:
            |    Name: $gameName""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class OfficialGameBookLoaded(val gameName : String, val bookName : String) : DocLoadEvent()
{
    override fun debugMessage() : String = """Official Game Book Loaded:
            |    Game: $gameName
            |    Book: $bookName""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class OfficialThemeLoaded(val themeName : String) : DocLoadEvent()
{
    override fun debugMessage() : String = """Official Theme Loaded:
            |    Name: $themeName""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class OfficialBookLoaded(val bookName : String) : DocLoadEvent()
{
    override fun debugMessage() : String = """Official Book Loaded:
            |    Name: $bookName""".trimMargin()

    override fun logMessage(): String = debugMessage()
}
