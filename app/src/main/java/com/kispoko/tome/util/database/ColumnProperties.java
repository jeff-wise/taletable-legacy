
package com.kispoko.tome.util.database;


import com.kispoko.tome.util.database.sql.SQLValue;

/**
 * Column Properties
 */
public class ColumnProperties
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String         columnName;
    private SQLValue.Type  sqlType;
    private SQL.Constraint constraint;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ColumnProperties(String columnName, SQLValue.Type sqlType, SQL.Constraint constraint)
    {
        this.columnName = columnName;
        this.sqlType    = sqlType;
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


    public SQLValue.Type getSQLType()
    {
        return this.sqlType;
    }


    public SQL.Constraint getConstraint()
    {
        return this.constraint;
    }

}
