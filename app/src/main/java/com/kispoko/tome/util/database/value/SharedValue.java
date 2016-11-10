
package com.kispoko.tome.util.database.value;


import com.kispoko.tome.util.database.SQL;



/**
 * A reference to a value that is a foreign key in a column and is represented by another
 * class/table.
 */
public class SharedValue
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String         columnName;
    private SQL.Constraint constraint;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public SharedValue(String columnName, SQL.Constraint constraint)
    {
        this.columnName = columnName;
        this.constraint = constraint;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getColumnName()
    {
        return this.columnName;
    }

}

