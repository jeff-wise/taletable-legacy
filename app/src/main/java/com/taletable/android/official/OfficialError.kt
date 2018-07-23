
package com.taletable.android.official


import com.taletable.android.app.ApplicationError



/**
 * Official Error
 */
sealed class OfficialError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class GameManifestParseError(val errorString : String) : OfficialError()
{
    override fun debugMessage() : String = """
            |Game Manifest Parse Error:
            |    $errorString
            """

    override fun logMessage(): String = userMessage()
}


class SheetManifestParseError(val errorString : String) : OfficialError()
{
    override fun debugMessage() : String = """
            |Sheet Manifest Parse Error:
            |    $errorString
            """

    override fun logMessage(): String = userMessage()
}


class HeroesCharSheetManifestParseError(val errorString : String) : OfficialError()
{
    override fun debugMessage() : String = """
            |The Magic of Heroes Character Sheet Manifest Parse Error:
            |    $errorString
            """

    override fun logMessage(): String = userMessage()
}


class HeroesCreatureSheetManifestParseError(val errorString : String) : OfficialError()
{
    override fun debugMessage() : String = """
            |The Magic of Heroes Creature Sheet Manifest Parse Error:
            |    $errorString
            """

    override fun logMessage(): String = userMessage()
}


class HeroesNPCSheetManifestParseError(val errorString : String) : OfficialError()
{
    override fun debugMessage() : String = """
            |The Magic of Heroes NPC Sheet Manifest Parse Error:
            |    $errorString
            """

    override fun logMessage(): String = userMessage()
}
