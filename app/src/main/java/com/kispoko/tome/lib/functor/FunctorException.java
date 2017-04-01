
package com.kispoko.tome.lib.functor;


import com.kispoko.tome.lib.functor.error.FunctorAccessError;
import com.kispoko.tome.lib.functor.error.UninitializedFunctorError;
import com.kispoko.tome.util.ApplicationError;



/**
 * Functor Exception
 */
public class FunctorException extends Exception
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    ApplicationError error;
    ErrorType        errorType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private FunctorException(ApplicationError error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static FunctorException functorAccess(FunctorAccessError error)
    {
        return new FunctorException(error, ErrorType.FUNCTOR_ACCESS);
    }


    public static FunctorException uninitializedFunctor(UninitializedFunctorError error)
    {
        return new FunctorException(error, ErrorType.UNINITIALIZED_FUNCTOR);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Error Message
    // -----------------------------------------------------------------------------------------


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Functor Error: ");

        errorBuilder.append(this.error.errorMessage());

        return errorBuilder.toString();
    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum ErrorType
    {
        FUNCTOR_ACCESS,
        UNINITIALIZED_FUNCTOR,
    }

}
