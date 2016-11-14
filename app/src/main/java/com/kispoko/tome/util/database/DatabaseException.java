
package com.kispoko.tome.util.database;



/**
 * Database Exception
 */
public class DatabaseException extends Exception
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    Object    error;
    ErrorType errorType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public DatabaseException() { }


    public DatabaseException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    // API
    // -----------------------------------------------------------------------------------------



    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum ErrorType
    {
        VALUE_NOT_SERIALIZABLE,
        UNEXPECTED_SQL_TYPE,
        COLUMN_DOES_NOT_EXIST,
        NULL_COLUMN_TYPE,
        QUERY
    }

}
