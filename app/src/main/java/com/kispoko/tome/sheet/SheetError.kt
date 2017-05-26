
package com.kispoko.tome.sheet

import com.kispoko.tome.app.ApplicationError


/**
 * Sheet Errors
 */
sealed class SheetError : ApplicationError


object SpecIsNull : SheetError()
{
    override fun userMessage() : String =
            """
            Sheet Error: Specification is NULL
            """

    override fun logMessage(): String = userMessage()
}


data class CannotOpenSpecFile(val filepath : String) : SheetError()
{
    override fun userMessage(): String =
            """
            Sheet Error: Cannot Open Spec File
                path: $filepath
            """

    override fun logMessage(): String = userMessage()
}


data class CannotOpenTemplateFile(val filepath : String) : SheetError()
{
    override fun userMessage(): String =
            """
            Sheet Error: Cannot Open Template File
                path: $filepath
            """

    override fun logMessage(): String = userMessage()
}


data class SpecParseError(val error : String) : SheetError()
{
    override fun userMessage(): String =
            """
            Sheet Error: Error parsing specification
                error: $error
            """

    override fun logMessage(): String = userMessage()
}


data class TemplateParseError(val error : String) : SheetError()
{
    override fun userMessage(): String =
            """
            Sheet Error: Error parsing template
                error: $error
            """

    override fun logMessage(): String = userMessage()
}
