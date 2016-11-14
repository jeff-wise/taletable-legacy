
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

    public static SQLValue newInteger(Integer sqlInteger)
    {
        return new SQLValue(sqlInteger, Type.INTEGER);
    }


    public static SQLValue newReal(Double sqlReal)
    {
        return new SQLValue(sqlReal, Type.REAL);
    }


    public static SQLValue newText(String sqlText)
    {
        return new SQLValue(sqlText, Type.TEXT);
    }


    public static SQLValue newBlob(byte[] sqlBlob)
    {
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


    public Integer getInteger()
           throws DatabaseException
    {
        try {
            return (Integer) this.value;
        } catch (ClassCastException e ) {
            throw new DatabaseException(new UnexpectedSQLTypeError(Type.INTEGER, this.type),
                                        DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
        }
    }


    public Double getReal()
           throws DatabaseException
    {
        try {
            return (Double) this.value;
        } catch (ClassCastException e ) {
            throw new DatabaseException(new UnexpectedSQLTypeError(Type.REAL, this.type),
                    DatabaseException.ErrorType.UNEXPECTED_SQL_TYPE);
        }
    }


    public String getText()
    {
        return (String) this.value;
    }


    public byte[] getBlob()
    {
        return (byte[]) this.value;
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
