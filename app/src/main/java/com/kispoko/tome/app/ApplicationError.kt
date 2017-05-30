
package com.kispoko.tome.app



/**
 * Application Error
 */
interface ApplicationError
{
    fun userMessage() : String
    fun debugMessage() : String
    fun logMessage() : String
}
