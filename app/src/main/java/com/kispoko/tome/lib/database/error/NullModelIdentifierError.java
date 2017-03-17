
package com.kispoko.tome.lib.database.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Database Error: Null Model Id
 */
public class NullModelIdentifierError implements ApplicationError
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
