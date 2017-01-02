
package com.kispoko.tome.util.database;


import com.kispoko.tome.util.database.error.ColumnDoesNotExistError;
import com.kispoko.tome.util.database.error.InvalidEnumError;
import com.kispoko.tome.util.database.error.NullColumnTypeError;
import com.kispoko.tome.util.database.error.QueryError;
import com.kispoko.tome.util.database.error.SerializationError;
import com.kispoko.tome.util.database.error.UnexpectedSQLTypeError;
import com.kispoko.tome.util.database.error.UninitializedFunctorError;
import com.kispoko.tome.util.database.error.ValueNotSerializableError;



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

    private DatabaseException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static DatabaseException valueNotSerializable(ValueNotSerializableError error)
    {
        return new DatabaseException(error, ErrorType.VALUE_NOT_SERIALIZABLE);
    }


    public static DatabaseException unexpectedSQLType(UnexpectedSQLTypeError error)
    {
        return new DatabaseException(error, ErrorType.UNEXPECTED_SQL_TYPE);
    }


    public static DatabaseException columnDoesNotExist(ColumnDoesNotExistError error)
    {
        return new DatabaseException(error, ErrorType.COLUMN_DOES_NOT_EXIST);
    }


    public static DatabaseException nullColumnType(NullColumnTypeError error)
    {
        return new DatabaseException(error, ErrorType.NULL_COLUMN_TYPE);
    }


    public static DatabaseException uninitializedFunctor(UninitializedFunctorError error)
    {
        return new DatabaseException(error, ErrorType.UNINITIALIZED_FUNCTOR);
    }


    public static DatabaseException invalidEnum(InvalidEnumError error)
    {
        return new DatabaseException(error, ErrorType.INVALID_ENUM);
    }


    public static DatabaseException serialization(SerializationError error)
    {
        return new DatabaseException(error, ErrorType.SERIALIZATION);
    }


    public static DatabaseException query(QueryError error)
    {
        return new DatabaseException(error, ErrorType.QUERY);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Errors
    // -----------------------------------------------------------------------------------------



    // > Error Message
    // -----------------------------------------------------------------------------------------


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("Database Error: ");

        switch (this.errorType)
        {
            case VALUE_NOT_SERIALIZABLE:
                errorBuilder.append(((ValueNotSerializableError) this.error).errorMessage());
                break;
            case UNEXPECTED_SQL_TYPE:
                errorBuilder.append(((UnexpectedSQLTypeError) this.error).errorMessage());
                break;
            case COLUMN_DOES_NOT_EXIST:
                errorBuilder.append(((ColumnDoesNotExistError) this.error).errorMessage());
                break;
            case NULL_COLUMN_TYPE:
                errorBuilder.append(((NullColumnTypeError) this.error).errorMessage());
                break;
            case UNINITIALIZED_FUNCTOR:
                errorBuilder.append(((UninitializedFunctorError) this.error).errorMessage());
                break;
            case INVALID_ENUM:
                errorBuilder.append(((InvalidEnumError) this.error).errorMessage());
                break;
            case SERIALIZATION:
                errorBuilder.append(((SerializationError) this.error).errorMessage());
                break;
            case QUERY:
                errorBuilder.append(((QueryError) this.error).errorMessage());
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
        UNINITIALIZED_FUNCTOR,
        INVALID_ENUM,
        SERIALIZATION,
        QUERY
    }

}
