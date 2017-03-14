
package com.kispoko.tome.util.database.error;


import com.kispoko.tome.util.ApplicationError;

import java.util.UUID;



/**
 * Database Error: Model Row Does Not Exist
 *
 * Occurs when a model query is made, but for some reason no row with the given ID is found.
 * (probably the model wasn't saved for some reason)
 */
public class ModelRowDoesNotExistError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String queryString;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ModelRowDoesNotExistError(String queryString)
    {
        this.queryString = queryString;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Model Row Does Not Exist: " + "\n" +
                "    Query: " + this.queryString;
    }


}
