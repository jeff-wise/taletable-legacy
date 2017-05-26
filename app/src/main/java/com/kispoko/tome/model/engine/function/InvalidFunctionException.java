
package com.kispoko.tome.model.engine.function;



/**
 * Invalid Function Exception
 *
 * Thrown when a function is created that violates specified constraints.
 */
public class InvalidFunctionException extends Exception
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Object    error;
    private ErrorType errorType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public InvalidFunctionException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    // API
    // -----------------------------------------------------------------------------------------

    /**
     * Get the error type of the Invalid Function Exception.
     * @return The error type.
     */
    public InvalidFunctionException.ErrorType getErrorType()
    {
        return this.errorType;
    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum ErrorType
    {
        INVALID_TUPLE_LENGTH
    }

}
