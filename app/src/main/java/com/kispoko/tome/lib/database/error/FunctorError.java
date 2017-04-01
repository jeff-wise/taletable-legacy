
package com.kispoko.tome.lib.database.error;


import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.util.ApplicationError;



/**
 * Functor Error
 */
public class FunctorError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private FunctorException functorException;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public FunctorError(FunctorException functorException)
    {
        this.functorException = functorException;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Functor Exception: " + this.functorException.errorMessage();
    }

}
