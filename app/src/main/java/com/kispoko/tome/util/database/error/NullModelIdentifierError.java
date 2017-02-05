
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Null Model Id
 */
public class NullModelIdentifierError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String modelName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NullModelIdentifierError(String modelName)
    {
        this.modelName = modelName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Null Model ID: Model: " + this.modelName;
    }


}
