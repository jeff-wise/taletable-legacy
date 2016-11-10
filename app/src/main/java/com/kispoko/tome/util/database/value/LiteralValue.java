
package com.kispoko.tome.util.database.value;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.error.DatabaseError;
import com.kispoko.tome.util.database.error.LiteralValueHasUnexpectedTypeError;

import java.util.UUID;


/**
 * A reference to a value in the database that is completed contained in one column.
 */
public class LiteralValue
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String         columnName;
    private SQL.DataType   dataType;
    private SQL.Constraint constraint;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public LiteralValue(String columnName, SQL.DataType dataType, SQL.Constraint constraint)
    {
        this.columnName = columnName;
        this.dataType   = dataType;
        this.constraint = constraint;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > State
    // -----------------------------------------------------------------------------------------

    public String getColumnName()
    {
        return this.columnName;
    }


    public SQL.DataType getDataType()
    {
        return this.dataType;
    }


    public SQL.Constraint getConstraint()
    {
        return this.constraint;
    }


    // > Type Conversion
    // -----------------------------------------------------------------------------------------

    public static String toString(Object dbValue, SQL.DataType dbValueType)
                  throws DatabaseException
    {
        String stringValue;
        try {
            stringValue = (String) dbValue;
        } catch (ClassCastException e) {
            throw new DatabaseException(
                    new DatabaseError(new LiteralValueHasUnexpectedTypeError(
                                                  dbValue, dbValueType, "string"),
                                      DatabaseError.Type.LITERAL_VALUE_HAS_UNEXPECTED_TYPE));
        }
        return stringValue;
    }


    public static UUID toUUID(Object dbValue, SQL.DataType dbValueType)
                  throws DatabaseException
    {
        UUID uuidValue;
        try {
            uuidValue = UUID.fromString((String) dbValue);
        } catch (ClassCastException e) {
            throw new DatabaseException(
                    new DatabaseError(new LiteralValueHasUnexpectedTypeError(
                                                    dbValue, dbValueType, "uuid"),
                                        DatabaseError.Type.LITERAL_VALUE_HAS_UNEXPECTED_TYPE));
        }
        return uuidValue;
    }


}
