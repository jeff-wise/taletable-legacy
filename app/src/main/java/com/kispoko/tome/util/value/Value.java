
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.AsyncFunction;
import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.error.DatabaseError;
import com.kispoko.tome.util.database.error.NoParserFoundForJavaValueError;
import com.kispoko.tome.util.database.value.DBValue;
import com.kispoko.tome.util.database.value.LiteralValue;

import java.util.UUID;

/**
 * Value
 */
public class Value<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String  name;
    private A       value;
    private DBValue dbValue;
    private Model   model;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Value(String name, A value, DBValue dbValue, Model model)
    {
        this.name    = name;
        this.value   = value;
        this.dbValue = dbValue;
        this.model   = model;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    public DBValue getDBValue()
    {
        return this.dbValue;
    }


    public A getValue()
    {
        return this.value;
    }


    public void setValue(A value)
    {
        if (this.value != null) {
            this.value = value;
            model.onUpdateModel(this.name);
        }
    }


    @SuppressWarnings("unchecked")
    public void setValueFromDBLiteralValue(String object, SQL.DataType literalValueType)
           throws DatabaseException
    {
        if (this.value instanceof String) {
            this.setValue((A) LiteralValue.toString(object, literalValueType));
        } else if (this.value instanceof UUID) {
            this.setValue((A) LiteralValue.toUUID(object, literalValueType));
        } else {
            throw new DatabaseException(
                    new DatabaseError(new NoParserFoundForJavaValueError(
                                                     this.value.getClass().getName()),
                                      DatabaseError.Type.NO_PARSER_FOUND_FOR_JAVA_VALUE));
        }
    }


    public void setValue(final AsyncFunction<A> asyncFunction)
    {
        asyncFunction.run(new AsyncFunction.OnReady<A>() {
            @Override
            public void run(A result) {
                setValue(result);
            }
        });
    }


    public boolean isNull()
    {
        return this.value == null;
    }


}
