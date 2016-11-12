
package com.kispoko.tome.util.database.error;



/**
 * The query for the table row representing the model either returned no rows, or more than
 * one row.
 */
public class ModelRowNotUniqueError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Integer rowsReturned;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ModelRowNotUniqueError(Integer rowsReturned)
    {
        this.rowsReturned = rowsReturned;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public Integer getRowsReturned()
    {
        return this.rowsReturned;
    }

}
