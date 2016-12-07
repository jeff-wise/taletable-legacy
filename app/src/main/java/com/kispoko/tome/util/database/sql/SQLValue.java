
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
        return new SQLValue(sqlInteger, Type.INTEGER);
    }


    public static SQLValue newReal(Double sqlReal)
                  throws DatabaseException
    {
        return new SQLValue(sqlReal, Type.REAL);
    }


    public static SQLValue newText(String sqlText)
                  throws DatabaseException
    {
        return new SQLValue(sqlText, Type.TEXT);
    }


    public static SQLValue newBlob(byte[] sqlBlob)
                  throws DatabaseException
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


    public Long getInteger()
           throws DatabaseException
    {
        try {
            return (Long) this.value;
        } catch (ClassCastException e ) {
            throw DatabaseException.unexpectedSQLType(
                    new UnexpectedSQLTypeError(Type.INTEGER, this.type));
        }
    }


    public Double getReal()
           throws DatabaseException
    {
        try {
            return (Double) this.value;
        } catch (ClassCastException e ) {
            throw DatabaseException.unexpectedSQLType(
                    new UnexpectedSQLTypeError(Type.REAL, this.type));
        }
    }


    public String getText()
           throws DatabaseException
    {
        return (String) this.value;
    }


    public byte[] getBlob()
           throws DatabaseException
    {
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
