
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
    private String       javaTypeName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ValueNotSerializableError(Direction direction, String javaTypeName)
    {
        this.direction    = direction;
        this.javaTypeName = javaTypeName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public Direction getDirection()
    {
        return this.direction;
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
