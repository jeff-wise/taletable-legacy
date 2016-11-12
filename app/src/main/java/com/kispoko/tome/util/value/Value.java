
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.error.ValueNotSerializableError;

import java.util.UUID;



/**
 * Value
 */
public abstract class Value<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private A       value;
    private Model   model;

    private boolean isSaved;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Value(A value, Model model)
    {
        this.value   = value;
        this.model   = model;

        this.isSaved = false;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    // ** Is Saved
    // --------------------------------------------------------------------------------------

    public void setIsSaved(boolean isSaved)
    {
        this.isSaved = isSaved;
    }


    public boolean getIsSaved()
    {
        return this.isSaved;
    }


    // ** Value
    // --------------------------------------------------------------------------------------

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


    public boolean isNull()
    {
        return this.value == null;
    }


    // > Database Serialization
    // ------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void fromInteger(Integer dbInteger)
           throws DatabaseException
    {
        if (this.value instanceof Integer) {
            this.setValue((A) dbInteger);
        }
        else {
            throw new DatabaseException(
                            new ValueNotSerializableError(ValueNotSerializableError.Direction.FROM,
                                                     SQL.DataType.INTEGER,
                                                     this.value.getClass().getName()),
                            DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE_TO_DB_TYPE);
        }
    }


    @SuppressWarnings("unchecked")
    public void fromText(String dbString)
           throws DatabaseException
    {
        if (this.value instanceof String) {
            this.setValue((A) dbString);
        }
        if (this.value instanceof UUID) {
            this.setValue((A) UUID.fromString(dbString));
        }
        else {
            throw new DatabaseException(
                            new ValueNotSerializableError(ValueNotSerializableError.Direction.FROM,
                                                     SQL.DataType.TEXT,
                                                     this.value.getClass().getName()),
                            DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE_TO_DB_TYPE);
        }
    }


    @SuppressWarnings("unchecked")
    public void fromBlob(byte[] dbByteArray)
           throws DatabaseException
    {
        if (this.value instanceof byte[]) {
            this.setValue((A) dbByteArray);
        }
        else {
            throw new DatabaseException(
                            new ValueNotSerializableError(ValueNotSerializableError.Direction.FROM,
                                                     SQL.DataType.BLOB,
                                                     this.value.getClass().getName()),
                            DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE_TO_DB_TYPE);
        }
    }


    public Integer asInteger()
           throws DatabaseException
    {
        if (this.value instanceof Integer) {
            return (Integer) this.value;
        }
        else {
            throw new DatabaseException(
                            new ValueNotSerializableError(ValueNotSerializableError.Direction.TO,
                                                     SQL.DataType.INTEGER,
                                                     this.value.getClass().getName()),
                            DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE_TO_DB_TYPE);
        }
    }


    public String asText()
           throws DatabaseException
    {
        if (this.value instanceof String) {
            return this.value.toString();
        }
        else if (this.value instanceof UUID) {
            return this.value.toString();
        }
        else {
            throw new DatabaseException(
                            new ValueNotSerializableError(ValueNotSerializableError.Direction.TO,
                                                     SQL.DataType.TEXT,
                                                     this.value.getClass().getName()),
                            DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE_TO_DB_TYPE);
        }
    }


    public byte[] asBlob()
           throws DatabaseException
    {
        if (this.value instanceof byte[]) {
            return (byte[]) this.value;
        }
        else {
            throw new DatabaseException(
                            new ValueNotSerializableError(ValueNotSerializableError.Direction.TO,
                                                     SQL.DataType.BLOB,
                                                     this.value.getClass().getName()),
                            DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE_TO_DB_TYPE);
        }
    }

}
