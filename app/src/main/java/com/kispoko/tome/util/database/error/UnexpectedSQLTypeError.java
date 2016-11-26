
package com.kispoko.tome.util.database.error;


import com.kispoko.tome.util.database.sql.SQLValue;

/**
 * Database Error: Unexpected SQL Type
 */
public class UnexpectedSQLTypeError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private SQLValue.Type expectedType;
    private SQLValue.Type actualType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public UnexpectedSQLTypeError(SQLValue.Type expectedType, SQLValue.Type actualType)
    {
        this.expectedType = expectedType;
        this.actualType   = actualType;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public SQLValue.Type getExpectedType()
    {
        return this.expectedType;
    }


    public SQLValue.Type getActualType()
    {
        return this.actualType;
    }


    public String errorMessage()
    {
        return "Unexpected SQL Type:\n" +
               "    Expected Type: " + this.expectedType.toString() + "\n" +
               "    Actual Type: " + this.actualType.toString();
    }

}
