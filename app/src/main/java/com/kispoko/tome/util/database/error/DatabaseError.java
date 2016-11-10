
package com.kispoko.tome.util.database.error;




/**
 * Database Error
 */
public class DatabaseError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Object error;
    private Type   type;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public DatabaseError(Object error, Type type)
    {
        this.error = error;
        this.type  = type;
    }


    // API
    // -----------------------------------------------------------------------------------------



    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------


    public enum Type
    {
        LITERAL_VALUE_HAS_UNEXPECTED_TYPE,
        NO_PARSER_FOUND_FOR_JAVA_VALUE
    }

}
