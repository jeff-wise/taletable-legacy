
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Null Functor
 *
 * This error occurs when a functor is null on an object that is being serialized to the database.
 */
public class NullFunctorError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String className;
    private String fieldName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NullFunctorError(String className, String fieldName)
    {
        this.className = className;
        this.fieldName = fieldName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Null Functor Error:\n" +
                "    Class: " + this.className + "\n" +
                "    Field: " + this.fieldName;
    }

}
