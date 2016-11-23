
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.ValueNotSerializableError;
import com.kispoko.tome.util.database.sql.SQLValue;

import java.util.UUID;



/**
 * Primitive Value
 */
public class PrimitiveValue<A> extends Value<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Class<A>         valueClass;
    private String           columnName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public PrimitiveValue(A value,
                          Model model,
                          Class<A> valueClass)
    {
        super(value, model);
        this.valueClass       = valueClass;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    // ** ColumnUnion Name
    // --------------------------------------------------------------------------------------

    public String getColumnName()
    {
        return this.columnName;
    }


    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }


    // > Helpers
    // --------------------------------------------------------------------------------------

    /**
     * Determine the SQL representation of this value based on its class.
     * @return
     */
    public SQLValue.Type sqlType()
           throws DatabaseException
    {
        if (this.getValue() instanceof String) {
            return SQLValue.Type.TEXT;
        }
        else if (this.getValue() instanceof Integer) {
            return SQLValue.Type.INTEGER;
        }
        else if (this.getValue() instanceof Long) {
            return SQLValue.Type.INTEGER;
        }
        else if (this.getValue() instanceof Double) {
            return SQLValue.Type.REAL;
        }
        else if (this.getValue() instanceof Boolean) {
            return SQLValue.Type.INTEGER;
        }
        else if (this.getValue() instanceof UUID) {
            return SQLValue.Type.TEXT;
        }
        else if (this.getValue() instanceof byte[]) {
            return SQLValue.Type.BLOB;
        }
        else if (this.isNull()) {
            return SQLValue.Type.NULL;
        } else {
            // value not serializable to
            throw new DatabaseException(
                    new ValueNotSerializableError(
                            ValueNotSerializableError.Type.UNKNOWN_SQL_REPRESENTATION,
                            this.getValue().getClass().getName()),
                    DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE);
        }
    }

    // > Serialization
    // --------------------------------------------------------------------------------------

    public SQLValue toSQLValue()
           throws DatabaseException
    {
        if (this.getValue() instanceof String) {
            return SQLValue.newText((String) this.getValue());
        }
        else if (this.getValue() instanceof Integer) {
            return SQLValue.newInteger((Long) this.getValue());
        }
        else if (this.getValue() instanceof Long) {
            return SQLValue.newInteger((Long) this.getValue());
        }
        else if (this.getValue() instanceof Double) {
            return SQLValue.newReal((Double) this.getValue());
        }
        else if (this.getValue() instanceof Boolean) {
            long boolAsInt = (Boolean) this.getValue() ? 1 : 0;
            return SQLValue.newInteger(Long.valueOf(boolAsInt));
        }
        else if (this.getValue() instanceof UUID) {
            return SQLValue.newText(this.getValue().toString());
        }
        else if (this.getValue() instanceof byte[]) {
            return SQLValue.newBlob((byte[]) this.getValue());
        }
        else if (this.isNull()) {
            return SQLValue.newNull();
        } else {
            // value not serializable to
            throw new DatabaseException(
                    new ValueNotSerializableError(ValueNotSerializableError.Type.TO,
                                                  this.getValue().getClass().getName()),
                    DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE);
        }
    }


    @SuppressWarnings("unchecked")
    public void fromSQLValue(SQLValue sqlValue)
           throws DatabaseException
    {
        if (this.valueClass.isAssignableFrom(String.class)) {
            this.setValue((A) sqlValue.getText());
        }
        else if (this.valueClass.isAssignableFrom(Integer.class)) {
            this.setValue((A) sqlValue.getInteger());
        }
        else if (this.valueClass.isAssignableFrom(Long.class)) {
            this.setValue((A) sqlValue.getInteger());
        }
        else if (this.valueClass.isAssignableFrom(Double.class)) {
            this.setValue((A) sqlValue.getReal());
        }
        else if (this.valueClass.isAssignableFrom(Boolean.class)) {
            Boolean boolFromInt = sqlValue.getInteger() == 1;
            this.setValue((A) boolFromInt);
        }
        else if (this.valueClass.isAssignableFrom(UUID.class)) {
            this.setValue((A) UUID.fromString(sqlValue.getText()));
        }
        else if (this.valueClass.isAssignableFrom(byte[].class)) {
            this.setValue((A) sqlValue.getBlob());
        } else {
            throw new DatabaseException(
                    new ValueNotSerializableError(ValueNotSerializableError.Type.FROM,
                                                  this.getValue().getClass().getName()),
                    DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE);
        }
    }

}
