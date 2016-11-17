
package com.kispoko.tome.exception;



/**
 * Exception: Invalid Data
 */
public class InvalidDataException extends Exception
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private Object error;
    private ErrorType   errorType;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public InvalidDataException(Object error, ErrorType errorType)
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
        INVALID_ENUM
    }

}
