
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Column Does Not Exist
 */
public class ColumnDoesNotExistError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String columnName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ColumnDoesNotExistError(String columnName)
    {
        this.columnName = columnName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String getColumnName()
    {
        return this.columnName;
    }
}
