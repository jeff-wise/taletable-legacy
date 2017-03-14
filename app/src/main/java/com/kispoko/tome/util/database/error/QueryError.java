
package com.kispoko.tome.util.database.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Database Error: Query Error
 *
 * This error encapsulates any exception that may be thrown in the process of querying a value.
 * The exceptions are undocumented, so this type at least indicates where the exception came from.
 */
public class QueryError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String query;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public QueryError(String query)
    {
        this.query = query;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String getQuery()
    {
        return this.query;
    }


    public String errorMessage()
    {
        return "Query: " + this.query;
    }

}
