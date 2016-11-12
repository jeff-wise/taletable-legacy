
package com.kispoko.tome.util.database;



/**
 * Column Properties
 */
public class ColumnProperties
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String         columnName;
    private SQL.DataType   dataType;
    private SQL.Constraint constraint;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ColumnProperties(String columnName, SQL.DataType dataType, SQL.Constraint constraint)
    {
        this.columnName = columnName;
        this.dataType   = dataType;
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


    public SQL.DataType getDataType()
    {
        return this.dataType;
    }


    public SQL.Constraint getConstraint()
    {
        return this.constraint;
    }

}
