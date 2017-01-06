
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Uninitialized Functor
 */
public class UninitializedFunctorError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String className;
    private String fieldName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public UninitializedFunctorError(String className, String fieldName)
    {
        this.className = className;
        this.fieldName  = fieldName;
    }


    // API
    // -----------------------------------------------------------------------------------------


    public String errorMessage()
    {
        return "Uninitialized Functor Error:\n" +
                "    Class: " + this.className + "\n" +
                "    Field: " + this.fieldName;
    }


}