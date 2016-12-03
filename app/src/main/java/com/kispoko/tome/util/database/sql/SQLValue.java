
package com.kispoko.tome.util.database.sql;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.UnexpectedSQLTypeError;



/**
 * SQL Value
 */
public class SQLValue
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object        value;
    private SQLValue.Type type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private SQLValue(Object value, Type type)
    {
        this.value = value;
        this.type  = type;
    }


    // > Case Constructors
    //   For stronger type safety, ensure we can only create valid SQLValues where the
    //   values match the types
    // ------------------------------------------------------------------------------------------

    public static SQLValue newInteger(Long sqlInteger)
                  throws DatabaseException
    {
//        if (sqlInteger == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.INTEGER, ErrorType.NULL),
//                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        return new SQLValue(sqlInteger, Type.INTEGER);
    }


    public static SQLValue newReal(Double sqlReal)
                  throws DatabaseException
    {
//        if (sqlReal == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.REAL, ErrorType.NULL),
//                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        return new SQLValue(sqlReal, Type.REAL);
    }


    public static SQLValue newText(String sqlText)
                  throws DatabaseException
    {
//        if (sqlText == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.TEXT, ErrorType.NULL),
//                    DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        return new SQLValue(sqlText, Type.TEXT);
    }


    public static SQLValue newBlob(byte[] sqlBlob)
                  throws DatabaseException
    {
//        if (sqlBlob == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.BLOB, ErrorType.NULL),
//                    DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        return new SQLValue(sqlBlob, Type.BLOB);
    }


    public static SQLValue newNull()
    {
        return new SQLValue(null, Type.NULL);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public Type getType()
    {
        return this.type;
    }


    public Long getInteger()
           throws DatabaseException
    {
//        if (this.value == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.INTEGER, ErrorType.NULL),
//                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        try {
            return (Long) this.value;
        } catch (ClassCastException e ) {
            throw new DatabaseException(new UnexpectedSQLTypeError(Type.INTEGER, this.type),
                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
        }
    }


    public Double getReal()
           throws DatabaseException
    {
//        if (this.value == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.REAL, ErrorType.NULL),
//                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        try {
            return (Double) this.value;
        } catch (ClassCastException e ) {
            throw new DatabaseException(new UnexpectedSQLTypeError(Type.REAL, this.type),
                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
        }
    }


    public String getText()
           throws DatabaseException
    {
//        if (this.value == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.TEXT, ErrorType.NULL),
//                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        return (String) this.value;
    }


    public byte[] getBlob()
           throws DatabaseException
    {
//        if (this.value == null) {
//            throw new DatabaseException(new UnexpectedSQLTypeError(ErrorType.BLOB, ErrorType.NULL),
//                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
//        }

        return (byte[]) this.value;
    }


    public boolean isNull()
    {
        return this.value == null;
    }


    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        INTEGER,
        REAL,
        TEXT,
        BLOB,
        NULL
    }


}
