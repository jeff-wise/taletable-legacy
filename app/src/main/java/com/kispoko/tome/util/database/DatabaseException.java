
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
        LITERAL_VALUE_HAS_UNEXPECTED_TYPE,
        NO_PARSER_FOUND_FOR_JAVA_VALUE,
        MODEL_ROW_NOT_UNIQUE,
        VALUE_NOT_SERIALIZABLE_TO_DB_TYPE
    }

}
