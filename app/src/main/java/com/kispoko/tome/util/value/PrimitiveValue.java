
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.database.ColumnProperties;
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

    private ColumnProperties columnProperties;
    private Class<A>         valueClass;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public PrimitiveValue(A value,
                          Model model,
                          Class<A> valueClass,
                          ColumnProperties columnProperties)
    {
        super(value, model);
        this.columnProperties = columnProperties;
        this.valueClass       = valueClass;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    public ColumnProperties getColumnProperties()
    {
        return this.columnProperties;
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
            return SQLValue.newInteger((Integer) this.getValue());
        }
        else if (this.getValue() instanceof Double) {
            return SQLValue.newReal((Double) this.getValue());
        }
        else if (this.getValue() instanceof Boolean) {
            int boolAsInt = (Boolean) this.getValue() ? 1 : 0;
            return SQLValue.newInteger(boolAsInt);
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
                    new ValueNotSerializableError(ValueNotSerializableError.Direction.TO,
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
                    new ValueNotSerializableError(ValueNotSerializableError.Direction.FROM,
                                                  this.getValue().getClass().getName()),
                    DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE);
        }
    }

}
