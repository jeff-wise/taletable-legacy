
package com.kispoko.tome.util.database.error;


import com.kispoko.tome.util.database.sql.SQLValue;

/**
 * Database Error: Unexpected SQL ErrorType
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

    public String errorMessage()
    {
        return "Unexpected SQL ErrorType:\n" +
               "    Expected ErrorType: " + this.expectedType.toString() + "\n" +
               "    Actual ErrorType: " + this.actualType.toString();
    }

}
