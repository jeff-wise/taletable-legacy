
package com.kispoko.tome.util.value;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.kispoko.tome.rules.programming.program.ProgramValueType;
import com.kispoko.tome.rules.programming.program.statement.ParameterType;
import com.kispoko.tome.rules.programming.variable.VariableType;
import com.kispoko.tome.rules.refinement.RefinementType;
import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.sheet.widget.table.cell.CellType;
import com.kispoko.tome.sheet.widget.table.column.ColumnType;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.util.SerialBitmap;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.ValueNotSerializableError;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Modeler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Primitive Value
 */
public class PrimitiveValue<A> extends Value<A>
                               implements Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Class<A>            valueClass;
    private OnUpdateListener<A> onUpdateListener;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public PrimitiveValue(A value,
                          Class<A> valueClass,
                          OnUpdateListener<A> onUpdateListener)
    {
        super(value);
        this.valueClass       = valueClass;
        this.onUpdateListener = onUpdateListener;
    }


    public PrimitiveValue(A value,
                          Class<A> valueClass)
    {
        super(value);
        this.valueClass       = valueClass;
        this.onUpdateListener = null;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > Column Name
    // --------------------------------------------------------------------------------------

    public String sqlColumnName()
    {
        return this.name();
    }


    // > Set Value
    // --------------------------------------------------------------------------------------

    @Override
    public void setValue(A newValue)
    {
        if (newValue != null) {
            this.value = newValue;
            this.onUpdateListener.onUpdate(newValue);
        }
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** On Update Listener
    // --------------------------------------------------------------------------------------

    /**
     * Set the update listener for the primitive value. Whenever the primitive value is updated,
     * the listener is called with the updated value.
     * @param onUpdateListener The PrimitiveValue OnUpdateListener instance.
     */
    public void setOnUpdateListener(OnUpdateListener<A> onUpdateListener)
    {
        this.onUpdateListener = onUpdateListener;
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
        if (valueClass.isAssignableFrom(String.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(Integer.class)) {
            return SQLValue.Type.INTEGER;
        }
        else if (valueClass.isAssignableFrom(Long.class)) {
            return SQLValue.Type.INTEGER;
        }
        else if (valueClass.isAssignableFrom(Double.class)) {
            return SQLValue.Type.REAL;
        }
        else if (valueClass.isAssignableFrom(Boolean.class)) {
            return SQLValue.Type.INTEGER;
        }
        else if (valueClass.isAssignableFrom(UUID.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(byte[].class)) {
            return SQLValue.Type.BLOB;
        }
        else if (valueClass.isAssignableFrom(WidgetFormat.Size.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(SerialBitmap.class)) {
            return SQLValue.Type.BLOB;
        }
        else if (valueClass.isAssignableFrom(String[].class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetFormat.Alignment.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(CellType.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ColumnType.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(CellAlignment.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(RefinementType.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ProgramValueType.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ProgramValueType[].class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ParameterType.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(VariableType.class)) {
            return SQLValue.Type.TEXT;
        }
        else {
            // value not serializable to
            throw new DatabaseException(
                    new ValueNotSerializableError(
                            ValueNotSerializableError.Type.UNKNOWN_SQL_REPRESENTATION,
                            this.valueClass.getName()),
                    DatabaseException.ErrorType.VALUE_NOT_SERIALIZABLE);
        }
    }

    // > Serialization
    // --------------------------------------------------------------------------------------

    public SQLValue toSQLValue()
           throws DatabaseException
    {
        if (this.isNull()) {
            return SQLValue.newNull();
        }
        else if (this.getValue() instanceof String) {
            return SQLValue.newText((String) this.getValue());
        }
        else if (this.getValue() instanceof Integer) {
            return SQLValue.newInteger(Long.valueOf((Integer) this.getValue()));
        }
        else if (this.getValue() instanceof Long) {
            return SQLValue.newInteger((Long) this.getValue());
        }
        else if (this.getValue() instanceof Double) {
            return SQLValue.newReal((Double) this.getValue());
        }
        else if (this.getValue() instanceof Boolean) {
            long boolAsInt = (Boolean) this.getValue() ? 1 : 0;
            return SQLValue.newInteger(boolAsInt);
        }
        else if (this.getValue() instanceof UUID) {
            return SQLValue.newText(this.getValue().toString());
        }
        else if (this.getValue() instanceof byte[]) {
            return SQLValue.newBlob((byte[]) this.getValue());
        }
        else if (this.getValue() instanceof WidgetFormat.Size) {
            String enumString = ((WidgetFormat.Size) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof SerialBitmap) {
            Bitmap bitmap = ((SerialBitmap) this.getValue()).getBitmap();
            if (bitmap != null) {
                byte[] bytes = Util.getBytes(bitmap);
                return SQLValue.newBlob(bytes);
            } else {
                return SQLValue.newNull();
            }
        }
        else if (this.getValue() instanceof String[]) {
            String arrayString = TextUtils.join("***", ((String[]) this.getValue()));
            return SQLValue.newText(arrayString);
        }
        else if (this.getValue() instanceof WidgetFormat.Alignment) {
            String enumString = ((WidgetFormat.Alignment) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof CellType) {
            String enumString = ((CellType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ColumnType) {
            String enumString = ((ColumnType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof CellAlignment) {
            String enumString = ((CellAlignment) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof RefinementType) {
            String enumString = ((RefinementType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ProgramValueType) {
            String enumString = ((ProgramValueType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ProgramValueType[]) {
            ProgramValueType[] programValueTypeArray = (ProgramValueType[]) this.getValue();
            List<String> programValueTypeStrings = new ArrayList<>();
            for (int i = 0; i < programValueTypeArray.length; i++) {
                programValueTypeStrings.add(programValueTypeArray[i].name().toLowerCase());
            }
            String arrayString = TextUtils.join("***", programValueTypeStrings);
            return SQLValue.newText(arrayString);
        }
        else if (this.getValue() instanceof ParameterType) {
            String enumString = ((ParameterType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof VariableType) {
            String enumString = ((VariableType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else {
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


    // ON UPDATE LISTENER
    // --------------------------------------------------------------------------------------

    public interface OnUpdateListener<A> {
        void onUpdate(A value);
    }
}
