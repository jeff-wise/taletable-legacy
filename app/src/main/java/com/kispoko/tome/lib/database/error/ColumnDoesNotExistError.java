
package com.kispoko.tome.lib.database.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Database Error: ColumnUnion Does Not Exist
 */
public class ColumnDoesNotExistError implements ApplicationError
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


    public String errorMessage()
    {
        return "Column Does Not Exist: Column Name: " + this.columnName;
    }

}
