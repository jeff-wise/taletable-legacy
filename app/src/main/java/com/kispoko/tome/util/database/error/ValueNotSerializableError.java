
package com.kispoko.tome.util.database.error;


import com.kispoko.tome.util.database.SQL;

/**
 * Value Not Serializable to Database Type Error
 */
public class ValueNotSerializableError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Direction    direction;
    private SQL.DataType dbValueType;
    private String       javaTypeName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ValueNotSerializableError(Direction direction, SQL.DataType dbValueType, String javaTypeName)
    {
        this.direction    = direction;
        this.dbValueType  = dbValueType;
        this.javaTypeName = javaTypeName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public Direction getDirection()
    {
        return this.direction;
    }


    public SQL.DataType getDBValueType()
    {
        return this.dbValueType;
    }


    public String getJavaTypeName()
    {
        return this.javaTypeName;
    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum Direction
    {
        TO,
        FROM
    }



}
