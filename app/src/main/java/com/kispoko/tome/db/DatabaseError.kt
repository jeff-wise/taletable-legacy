
package com.kispoko.tome.db


import com.kispoko.tome.app.ApplicationError



/**
 * Datbaase Error
 */
sealed class DatabaseError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


/**
 * The theme does not have a color with the given id.
 */
class CouldNotAccessDatabase : DatabaseError()

{
    override fun debugMessage() : String =
            """Database Error: Could Not Access Database"""

    override fun logMessage() : String = userMessage()
}


