
package com.kispoko.tome.util.database.error;


/**
 * Value Not Serializable to Database ErrorType Error
 */
public class ValueNotSerializableError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Type   type;
    private String javaTypeName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ValueNotSerializableError(Type type, String javaTypeName)
    {
        this.type = type;
        this.javaTypeName = javaTypeName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public Type getType()
    {
        return this.type;
    }


    public String getJavaTypeName()
    {
        return this.javaTypeName;
    }


    public String errorMessage()
    {
        return "Value Not Serializable:\n" +
               "    Problem: " + this.type.toString() + "\n" +
               "    Java ErrorType: " + this.getJavaTypeName();

    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum Type
    {
        TO,
        FROM,
        UNKNOWN_SQL_REPRESENTATION
    }



}
