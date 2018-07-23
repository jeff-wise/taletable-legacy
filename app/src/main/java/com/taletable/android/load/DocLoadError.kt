
package com.taletable.android.load


import com.taletable.android.app.ApplicationError
import lulo.document.DocParseError
import lulo.value.ValueError



/**
 * Load Errors
 */
sealed class DocLoadError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class SchemaIsNull(val specName : String) : DocLoadError()
{
    override fun debugMessage(): String = """
            Load Error: Specification is NULL
                Spec Name: $specName
            """

    override fun logMessage(): String = userMessage()
}


data class CannotOpenSpecFile(val filepath : String) : DocLoadError()
{
    override fun debugMessage(): String = """
            Load Error: Cannot Open Spec File
                path: $filepath
            """

    override fun logMessage(): String = userMessage()
}


data class CannotOpenTemplateFile(val filepath : String) : DocLoadError()
{
    override fun debugMessage(): String = """
            Load Error: Cannot Open Template File
                path: $filepath
            """

    override fun logMessage(): String = userMessage()
}


data class SchemaParseError(val error : String) : DocLoadError()
{
    override fun debugMessage(): String = """
            |Load Error: Error parsing specification
            |    error: $error
            """.trimMargin()

    override fun logMessage(): String = userMessage()
}


data class DocumentParseError(val documentName : String,
                              val specName : String,
                              val errors : List<DocParseError> ) : DocLoadError()
{
    override fun debugMessage(): String
    {
        var errorsString = ""

        for (docParseError in errors) {
            errorsString += docParseError.toString() + "\n"
        }

        return """Load Error: Error parsing document '$documentName' with schema '$specName'
                      (Document did not match schema)
                      All errors:
                      $errorsString
               """
    }

    override fun logMessage(): String = userMessage()
}


data class ValueParseError(val docName : String, val error : ValueError) : DocLoadError()
{
    override fun debugMessage(): String = """
            |Load Error: Error mapping document to object
            |    Document Name: $docName
            |    $error
            """.trimMargin()

    override fun logMessage(): String = userMessage()
}



class ContextIsNull : DocLoadError()
{
    override fun debugMessage(): String = """
            Load Error: Context is null
            """

    override fun logMessage(): String = userMessage()
}
