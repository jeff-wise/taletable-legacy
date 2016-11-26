
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Query Error
 *
 * This error encapsulates any exception that may be thrown in the process of querying a value.
 * The exceptions are undocumented, so this type at least indicates where the exception came from.
 */
public class QueryError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Exception exception;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public QueryError(Exception exception)
    {
        this.exception = exception;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public Exception getException()
    {
        return this.exception;
    }


    public String errorMessage()
    {
        return "Query";
    }

}
