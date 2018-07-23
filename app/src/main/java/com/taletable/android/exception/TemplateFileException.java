
package com.taletable.android.exception;



/**
 * Exception: Template File
 */
public class TemplateFileException extends Exception
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private Object error;
    private ErrorType   errorType;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public TemplateFileException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    // API
    // -------------------------------------------------------------------------------------------

    public Object getError()
    {
        return this.error;
    }


    public ErrorType getErrorType()
    {
        return this.errorType;
    }


    // NESTED DEFINITIONS
    // -------------------------------------------------------------------------------------------

    public enum ErrorType
    {
        TEMPLATE_FILE_READ
    }

}
