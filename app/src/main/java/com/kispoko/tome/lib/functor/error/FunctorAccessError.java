
package com.kispoko.tome.lib.functor.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Functor Access Error
 */
public class FunctorAccessError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String    className;
    private Exception exception;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public FunctorAccessError(String className, Exception exception)
    {
        this.className = className;
        this.exception = exception;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Functor Access Error: " + "\n" +
                "    Class: " + this.className + "\n" +
                "    Exception:\n" + this.exception.getMessage();
    }

}
