
package com.kispoko.tome.lib.functor.error;


import com.kispoko.tome.util.ApplicationError;

/**
 * Functor Error: Property Update
 *
 * Occurs when the updateProperty method is called to update a functor on a model by its name
 * and an error occurs when trying to set the value.
 */
public class FunctorPropertyUpdateError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String    propertyName;
    private Object    value;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public FunctorPropertyUpdateError(String propertyName, Object value)
    {
        this.propertyName   = propertyName;
        this.value          = value;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Functor Property Update Error: " + "\n" +
                "    Property Name: " + this.propertyName + "\n" +
                "    Value Type:\n" + this.value.getClass().getName();
    }

}
