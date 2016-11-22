
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Null Column Type
 */
public class NullColumnTypeError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String tableName;
    private String columnName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NullColumnTypeError(String tableName, String columnName)
    {
        this.tableName  = tableName;
        this.columnName = columnName;
    }


    // API
    // -----------------------------------------------------------------------------------------


    public String getTableName()
    {
        return this.tableName;
    }


    public String getColumnName()
    {
        return this.columnName;
    }

}