
package com.kispoko.tome.util.database.error;


import com.kispoko.tome.util.database.SQL;



/**
 * Database Type Conversion Error
 *
 * This error occurs when a value is parsed correctly from the database, but the parsed value
 * provided is not of the correct type for converting to the target Java value. This should not
 * occur because of the logic in the query generation code.
 */
public class LiteralValueHasUnexpectedTypeError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Object       dbValue;
    private SQL.DataType dbValueType;

    private String       targetType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public LiteralValueHasUnexpectedTypeError(Object dbValue,
                                              SQL.DataType dbValueType,
                                              String targetType)
    {
        this.dbValue     = dbValue;
        this.dbValueType = dbValueType;
        this.targetType  = targetType;
    }


    // API
    // -----------------------------------------------------------------------------------------

}
