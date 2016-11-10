
package com.kispoko.tome.util.database.value;


import com.kispoko.tome.util.database.SQL;



/**
 * Database Reference
 */
public class DBValue
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Object value;
    private Type   type;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public DBValue(Object value, Type type)
    {
        this.value = value;
        this.type      = type;
    }


    // API
    // --------------------------------------------------------------------------------------

    public Type getType()
    {
        return this.type;
    }


    public LiteralValue getColumnValue()
    {
        return (LiteralValue) this.value;
    }


    public SharedValue getSharedValue()
    {
        return (SharedValue) this.value;
    }


    // NESTED DEFINITIONS
    // --------------------------------------------------------------------------------------

    /**
     * Defines the types of database references.
     */
    public enum Type
    {
        COLUMN_VALUE,
        FOREIGN_VALUE
    }


}
