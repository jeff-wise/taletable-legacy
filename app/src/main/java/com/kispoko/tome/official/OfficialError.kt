
package com.kispoko.tome.official


import com.kispoko.tome.app.ApplicationError



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


class AmanaceCharSheetManifestParseError(val errorString : String) : OfficialError()
{
    override fun debugMessage() : String = """
            |Amanace Character Sheet Manifest Parse Error:
            |    $errorString
            """

    override fun logMessage(): String = userMessage()
}
