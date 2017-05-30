
package com.kispoko.tome.model.game.engine.summation;


import com.kispoko.tome.model.game.engine.summation.error.NullTermError;
import com.kispoko.tome.model.game.engine.summation.error.SummationVariableError;
import com.kispoko.tome.util.ApplicationError;


/**
 * Summation Exception
 */
public class SummationException extends Exception
{


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ApplicationError error;
    private ErrorType        errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private SummationException(ApplicationError error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static SummationException variable(SummationVariableError error)
    {
        return new SummationException(error, ErrorType.VARIABLE);
    }


    public static SummationException nullTerm(NullTermError error)
    {
        return new SummationException(error, ErrorType.NULL_TERM);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Summation Error: ");
        errorBuilder.append(this.error.errorMessage());

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum ErrorType
    {
        VARIABLE,
        NULL_TERM;
    }


}
