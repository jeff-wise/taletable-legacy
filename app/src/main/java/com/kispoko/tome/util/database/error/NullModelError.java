
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Null Model
 */
public class NullModelError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String modelName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NullModelError(String modelName)
    {
        this.modelName = modelName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Null Model: Model: " + this.modelName;
    }


}
