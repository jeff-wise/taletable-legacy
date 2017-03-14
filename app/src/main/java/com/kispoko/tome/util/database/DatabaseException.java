
package com.kispoko.tome.util.database;


import com.kispoko.tome.util.ApplicationError;
import com.kispoko.tome.util.database.error.ColumnDoesNotExistError;
import com.kispoko.tome.util.database.error.InvalidEnumError;
import com.kispoko.tome.util.database.error.ModelRowDoesNotExistError;
import com.kispoko.tome.util.database.error.NullColumnTypeError;
import com.kispoko.tome.util.database.error.NullModelIdentifierError;
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

    ApplicationError error;
    ErrorType        errorType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private DatabaseException(ApplicationError error, ErrorType errorType)
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


    public static DatabaseException modelRowDoesNotExist(ModelRowDoesNotExistError error)
    {
        return new DatabaseException(error, ErrorType.MODEL_ROW_DOES_NOT_EXIST);
    }


    public static DatabaseException nullColumnType(NullColumnTypeError error)
    {
        return new DatabaseException(error, ErrorType.NULL_COLUMN_TYPE);
    }


    public static DatabaseException nullModelId(NullModelIdentifierError error)
    {
        return new DatabaseException(error, ErrorType.NULL_MODEL_ID);
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

    // > Error Message
    // -----------------------------------------------------------------------------------------


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Database Error: ");

        errorBuilder.append(this.error.errorMessage());

        return errorBuilder.toString();
    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum ErrorType
    {
        VALUE_NOT_SERIALIZABLE,
        UNEXPECTED_SQL_TYPE,
        COLUMN_DOES_NOT_EXIST,
        MODEL_ROW_DOES_NOT_EXIST,
        NULL_COLUMN_TYPE,
        NULL_MODEL,
        NULL_MODEL_ID,
        UNINITIALIZED_FUNCTOR,
        INVALID_ENUM,
        SERIALIZATION,
        QUERY
    }

}
