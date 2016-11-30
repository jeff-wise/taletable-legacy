
package com.kispoko.tome.util.database;


import com.kispoko.tome.util.database.error.ColumnDoesNotExistError;
import com.kispoko.tome.util.database.error.NullColumnTypeError;
import com.kispoko.tome.util.database.error.QueryError;
import com.kispoko.tome.util.database.error.UnexpectedSQLTypeError;
import com.kispoko.tome.util.database.error.ValueNotSerializableError;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;


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

    // > Errors
    // -----------------------------------------------------------------------------------------

    public ValueNotSerializableError getValueNotSerializableError()
    {
        return (ValueNotSerializableError) this.error;
    }


    public UnexpectedSQLTypeError getUnexpectedSQLTypeError()
    {
        return (UnexpectedSQLTypeError) this.error;
    }


    public ColumnDoesNotExistError getColumnDoesNotExistError()
    {
        return (ColumnDoesNotExistError) this.error;
    }


    public NullColumnTypeError getNullColumnTypeError()
    {
        return (NullColumnTypeError) this.error;
    }


    public InvalidEnumError getInvalidEnumError()
    {
        return (InvalidEnumError) this.error;
    }


    public QueryError getQueryError()
    {
        return (QueryError) this.error;
    }


    // > Error Message
    // -----------------------------------------------------------------------------------------


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("Database Error: ");

        switch (this.errorType)
        {
            case VALUE_NOT_SERIALIZABLE:
                errorBuilder.append(this.getValueNotSerializableError().errorMessage());
                break;
            case UNEXPECTED_SQL_TYPE:
                errorBuilder.append(this.getUnexpectedSQLTypeError().errorMessage());
                break;
            case COLUMN_DOES_NOT_EXIST:
                errorBuilder.append(this.getColumnDoesNotExistError().errorMessage());
                break;
            case NULL_COLUMN_TYPE:
                errorBuilder.append(this.getNullColumnTypeError().errorMessage());
                break;
            case INVALID_ENUM:
                errorBuilder.append(this.getInvalidEnumError().errorMessage());
                break;
            case QUERY:
                errorBuilder.append(this.getQueryError().errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum ErrorType
    {
        VALUE_NOT_SERIALIZABLE,
        UNEXPECTED_SQL_TYPE,
        COLUMN_DOES_NOT_EXIST,
        NULL_COLUMN_TYPE,
        INVALID_ENUM,
        QUERY
    }

}
