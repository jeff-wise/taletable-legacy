
package com.kispoko.tome.lib.functor.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Functor Error: Property Does Not Exist
 *
 * Can happen during Model.updateProperty when trying to update a functor at a property name
 * that does not exist.
 */
public class FunctorPropertyDoesNotExistError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String    propertyName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public FunctorPropertyDoesNotExistError(String propertyName)
    {
        this.propertyName   = propertyName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Functor Property Does Not Exist: " + "\n" +
                "    Property Name: " + this.propertyName;
    }

}
