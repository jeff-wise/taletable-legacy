
package com.kispoko.tome.util.database;


import com.kispoko.tome.util.database.sql.SQLValue;

/**
 * ColumnUnion Properties
 */
public class ColumnProperties
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String         columnName;
    private SQL.Constraint constraint;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ColumnProperties(String columnName, SQL.Constraint constraint)
    {
        this.columnName = columnName;
        this.constraint = constraint;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > State
    // -----------------------------------------------------------------------------------------

    public String getColumnName()
    {
        return this.columnName;
    }


    public SQL.Constraint getConstraint()
    {
        return this.constraint;
    }

}
