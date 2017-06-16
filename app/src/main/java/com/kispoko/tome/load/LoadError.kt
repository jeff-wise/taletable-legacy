
package com.kispoko.tome.load


import com.kispoko.tome.app.ApplicationError
import lulo.document.DocParseError
import lulo.value.ValueError



/**
 * Load Errors
 */
sealed class LoadError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class SpecIsNull(val specName : String) : LoadError()
{
    override fun debugMessage(): String =
            """
            Load Error: Specification is NULL
                Spec Name: $specName
            """

    override fun logMessage(): String = userMessage()
}


data class CannotOpenSpecFile(val filepath : String) : LoadError()
{
    override fun debugMessage(): String =
            """
            Load Error: Cannot Open Spec File
                path: $filepath
            """

    override fun logMessage(): String = userMessage()
}


data class CannotOpenTemplateFile(val filepath : String) : LoadError()
{
    override fun debugMessage(): String =
            """
            Load Error: Cannot Open Template File
                path: $filepath
            """

    override fun logMessage(): String = userMessage()
}


data class SpecParseError(val error : String) : LoadError()
{
    override fun debugMessage(): String =
            """
            Load Error: Error parsing specification
                error: $error
            """

    override fun logMessage(): String = userMessage()
}


data class ValueParseError(val docType : String, val error : ValueError) : LoadError()
{
    override fun debugMessage(): String =
            """
            Load Error: Document did not match specification '$docType'
                $error
            """

    override fun logMessage(): String = userMessage()
}


data class DocumentParseError(val documentName: String,
                              val specName : String,
                              val errors : List<DocParseError> ) : LoadError()
{
    override fun debugMessage(): String
    {
        var errorsString = ""

        for (docParseError in errors) {
            errorsString += docParseError.toString() + "\n"
        }

        return """
               Load Error: Error parsing document '$documentName' with spec '$specName'
                   All errors:
                   $errorsString
               """
    }

    override fun logMessage(): String = userMessage()
}


class ContextIsNull : LoadError()
{
    override fun debugMessage(): String =
            """
            Load Error: Context is null
            """

    override fun logMessage(): String = userMessage()
}
