
package com.kispoko.tome.util.value;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.kispoko.tome.engine.programming.program.invocation.InvocationParameterType;
import com.kispoko.tome.engine.programming.program.ProgramValueType;
import com.kispoko.tome.engine.programming.program.statement.ParameterType;
import com.kispoko.tome.engine.programming.summation.term.BooleanTermValue;
import com.kispoko.tome.engine.programming.summation.term.IntegerTermValue;
import com.kispoko.tome.engine.programming.variable.BooleanVariable;
import com.kispoko.tome.engine.programming.variable.NumberVariable;
import com.kispoko.tome.engine.programming.variable.TextVariable;
import com.kispoko.tome.engine.refinement.RefinementType;
import com.kispoko.tome.sheet.group.RowAlignment;
import com.kispoko.tome.sheet.group.RowWidth;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.sheet.widget.table.cell.CellType;
import com.kispoko.tome.sheet.widget.table.column.ColumnType;
import com.kispoko.tome.sheet.widget.util.WidgetContentAlignment;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.util.SerialBitmap;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.error.ValueNotSerializableError;
import com.kispoko.tome.util.database.sql.SQLValue;

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


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public PrimitiveValue(A value,
                          Class<A> valueClass)
    {
        super(value);
        this.valueClass       = valueClass;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > Column Name
    // --------------------------------------------------------------------------------------

    public String sqlColumnName()
    {
        return SQL.asValidIdentifier(this.name());
    }


    // > Set Value
    // --------------------------------------------------------------------------------------

    @Override
    public void setValue(A newValue)
    {
        if (newValue != null) {
            this.value = newValue;
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
    /*
    public void setOnUpdateListener(OnUpdateListener<A> onUpdateListener)
    {
        this.onUpdateListener = onUpdateListener;
    }
    */



    // > Helpers
    // --------------------------------------------------------------------------------------

    /**
     * Determine the SQL representation of this value based on its class.
     * @return
     */
    public SQLValue.Type sqlType()
           throws DatabaseException
    {
        if (valueClass.isAssignableFrom(String.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(Integer.class))
        {
            return SQLValue.Type.INTEGER;
        }
        else if (valueClass.isAssignableFrom(Long.class))
        {
            return SQLValue.Type.INTEGER;
        }
        else if (valueClass.isAssignableFrom(Double.class))
        {
            return SQLValue.Type.REAL;
        }
        else if (valueClass.isAssignableFrom(Boolean.class))
        {
            return SQLValue.Type.INTEGER;
        }
        else if (valueClass.isAssignableFrom(UUID.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(byte[].class))
        {
            return SQLValue.Type.BLOB;
        }
        else if (valueClass.isAssignableFrom(WidgetContentSize.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(SerialBitmap.class))
        {
            return SQLValue.Type.BLOB;
        }
        else if (valueClass.isAssignableFrom(String[].class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetContentAlignment.class)) {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(CellType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ColumnType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(CellAlignment.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(RowAlignment.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(RowWidth.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(RefinementType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ProgramValueType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ProgramValueType[].class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(Action[].class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ParameterType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(TextVariable.Kind.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(NumberVariable.Kind.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(BooleanVariable.Kind.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(InvocationParameterType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(IntegerTermValue.Kind.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(BooleanTermValue.Kind.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(Action.class))
        {
            return SQLValue.Type.TEXT;
        }
        else
        {
            // value not serializable to
            throw DatabaseException.valueNotSerializable(
                    new ValueNotSerializableError(
                            ValueNotSerializableError.Type.UNKNOWN_SQL_REPRESENTATION,
                            this.valueClass.getName()));
        }
    }

    // > Serialization
    // --------------------------------------------------------------------------------------

    public SQLValue toSQLValue()
           throws DatabaseException
    {
        if (this.isNull())
        {
            return SQLValue.newNull();
        }
        else if (this.getValue() instanceof String)
        {
            return SQLValue.newText((String) this.getValue());
        }
        else if (this.getValue() instanceof Integer)
        {
            return SQLValue.newInteger(Long.valueOf((Integer) this.getValue()));
        }
        else if (this.getValue() instanceof Long)
        {
            return SQLValue.newInteger((Long) this.getValue());
        }
        else if (this.getValue() instanceof Double)
        {
            return SQLValue.newReal((Double) this.getValue());
        }
        else if (this.getValue() instanceof Boolean)
        {
            long boolAsInt = (Boolean) this.getValue() ? 1 : 0;
            return SQLValue.newInteger(boolAsInt);
        }
        else if (this.getValue() instanceof UUID)
        {
            return SQLValue.newText(this.getValue().toString());
        }
        else if (this.getValue() instanceof byte[])
        {
            return SQLValue.newBlob((byte[]) this.getValue());
        }
        else if (this.getValue() instanceof WidgetContentSize)
        {
            String enumString = ((WidgetContentSize) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof SerialBitmap)
        {
            Bitmap bitmap = ((SerialBitmap) this.getValue()).getBitmap();
            if (bitmap != null) {
                byte[] bytes = Util.getBytes(bitmap);
                return SQLValue.newBlob(bytes);
            } else {
                return SQLValue.newNull();
            }
        }
        else if (this.getValue() instanceof String[])
        {
            String arrayString = TextUtils.join("***", ((String[]) this.getValue()));
            return SQLValue.newText(arrayString);
        }
        else if (this.getValue() instanceof WidgetContentAlignment)
        {
            String enumString = ((WidgetContentAlignment) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof CellType)
        {
            String enumString = ((CellType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ColumnType)
        {
            String enumString = ((ColumnType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof CellAlignment)
        {
            String enumString = ((CellAlignment) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof RowAlignment)
        {
            String enumString = ((RowAlignment) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof RowWidth)
        {
            String enumString = ((RowWidth) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof RefinementType)
        {
            String enumString = ((RefinementType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ProgramValueType)
        {
            String enumString = ((ProgramValueType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ProgramValueType[])
        {
            ProgramValueType[] programValueTypeArray = (ProgramValueType[]) this.getValue();
            List<String> programValueTypeStrings = new ArrayList<>();
            for (int i = 0; i < programValueTypeArray.length; i++) {
                programValueTypeStrings.add(programValueTypeArray[i].name().toLowerCase());
            }
            String arrayString = TextUtils.join("***", programValueTypeStrings);
            return SQLValue.newText(arrayString);
        }
        else if (this.getValue() instanceof Action[])
        {
            Action[] actionArray = (Action[]) this.getValue();
            List<String> actionStrings = new ArrayList<>();
            for (int i = 0; i < actionArray.length; i++) {
                actionStrings.add(actionArray[i].name().toLowerCase());
            }
            String arrayString = TextUtils.join("***", actionStrings);
            return SQLValue.newText(arrayString);
        }
        else if (this.getValue() instanceof ParameterType)
        {
            String enumString = ((ParameterType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof TextVariable.Kind)
        {
            String enumString = ((TextVariable.Kind) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof NumberVariable.Kind)
        {
            String enumString = ((NumberVariable.Kind) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof BooleanVariable.Kind)
        {
            String enumString = ((BooleanVariable.Kind) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof IntegerTermValue.Kind)
        {
            String enumString = ((IntegerTermValue.Kind) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof BooleanTermValue.Kind)
        {
            String enumString = ((BooleanTermValue.Kind) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof InvocationParameterType)
        {
            String enumString = ((InvocationParameterType)
                                            this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof Action)
        {
            String enumString = ((Action) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else
        {
            throw DatabaseException.valueNotSerializable(
                    new ValueNotSerializableError(ValueNotSerializableError.Type.TO,
                                                  this.getValue().getClass().getName()));
        }
    }


    @SuppressWarnings("unchecked")
    public void fromSQLValue(SQLValue sqlValue)
           throws DatabaseException
    {
        if (sqlValue.isNull()) return;

        if (this.valueClass.isAssignableFrom(String.class))
        {
            this.setValue((A) sqlValue.getText());
        }
        else if (this.valueClass.isAssignableFrom(Integer.class))
        {
            Integer intValue = (int) (long) sqlValue.getInteger();
            this.setValue((A) intValue);
        }
        else if (this.valueClass.isAssignableFrom(Long.class))
        {
            this.setValue((A) sqlValue.getInteger());
        }
        else if (this.valueClass.isAssignableFrom(Double.class))
        {
            this.setValue((A) sqlValue.getReal());
        }
        else if (this.valueClass.isAssignableFrom(Boolean.class))
        {
            Boolean boolFromInt = sqlValue.getInteger() == 1;
            this.setValue((A) boolFromInt);
        }
        else if (this.valueClass.isAssignableFrom(UUID.class))
        {
            this.setValue((A) UUID.fromString(sqlValue.getText()));
        }
        else if (this.valueClass.isAssignableFrom(byte[].class))
        {
            this.setValue((A) sqlValue.getBlob());
        }
        else if (this.valueClass.isAssignableFrom(CellType.class))
        {
            CellType cellType = CellType.fromSQLValue(sqlValue);
            this.setValue((A) cellType);
        }
        else if (this.valueClass.isAssignableFrom(ColumnType.class))
        {
            ColumnType columnType = ColumnType.fromSQLValue(sqlValue);
            this.setValue((A) columnType);
        }
        else if (this.valueClass.isAssignableFrom(String[].class))
        {
            String arrayString = sqlValue.getText();
            if (arrayString != null) {
                String[] stringArray = TextUtils.split(arrayString, "\\*\\*\\*");
                this.setValue((A) stringArray);
            }
            else {
                this.setValue(null);
            }
        }
        else if (this.valueClass.isAssignableFrom(WidgetContentSize.class))
        {
            WidgetContentSize size = WidgetContentSize.fromSQLValue(sqlValue);
            this.setValue((A) size);
        }
        else if (this.valueClass.isAssignableFrom(ProgramValueType.class))
        {
            ProgramValueType programValueType = ProgramValueType.fromSQLValue(sqlValue);
            this.setValue((A) programValueType);
        }
        else if (this.valueClass.isAssignableFrom(ProgramValueType[].class))
        {
            String arrayString = sqlValue.getText();
            if (arrayString != null) {
                String[] stringArray = TextUtils.split(arrayString, "\\*\\*\\*");
                ProgramValueType[] programValueTypes = new ProgramValueType[stringArray.length];
                for (int i = 0; i < stringArray.length; i++) {
                    programValueTypes[i] = ProgramValueType.fromSQLValue(
                                                                SQLValue.newText(stringArray[i]));
                }
                this.setValue((A) programValueTypes);
            }
            else {
                this.setValue(null);
            }
        }
        else if (this.valueClass.isAssignableFrom(Action[].class))
        {
            String arrayString = sqlValue.getText();
            if (arrayString != null) {
                String[] stringArray = TextUtils.split(arrayString, "\\*\\*\\*");
                Action[] actions = new Action[stringArray.length];
                for (int i = 0; i < stringArray.length; i++) {
                    actions[i] = Action.fromSQLValue(SQLValue.newText(stringArray[i]));
                }
                this.setValue((A) actions);
            }
            else {
                this.setValue(null);
            }
        }
        else if (this.valueClass.isAssignableFrom(SerialBitmap.class))
        {
            byte[] bitmapBlob = sqlValue.getBlob();
            if (bitmapBlob != null) {
                Bitmap bitmap = Util.getImage(bitmapBlob);
                SerialBitmap serialBitmap = new SerialBitmap(bitmap);
                this.setValue((A) serialBitmap);
            } else {
                this.setValue(null);
            }
        }
        else if (this.valueClass.isAssignableFrom(CellAlignment.class))
        {
            CellAlignment cellAlignment = CellAlignment.fromSQLValue(sqlValue);
            this.setValue((A) cellAlignment);
        }
        else if (this.valueClass.isAssignableFrom(RowAlignment.class))
        {
            RowAlignment alignment = RowAlignment.fromSQLValue(sqlValue);
            this.setValue((A) alignment);
        }
        else if (this.valueClass.isAssignableFrom(RowWidth.class))
        {
            RowWidth width = RowWidth.fromSQLValue(sqlValue);
            this.setValue((A) width);
        }
        else if (this.valueClass.isAssignableFrom(TextVariable.Kind.class))
        {
            TextVariable.Kind kind = TextVariable.Kind.fromSQLValue(sqlValue);
            this.setValue((A) kind);
        }
        else if (this.valueClass.isAssignableFrom(NumberVariable.Kind.class))
        {
            NumberVariable.Kind kind = NumberVariable.Kind.fromSQLValue(sqlValue);
            this.setValue((A) kind);
        }
        else if (this.valueClass.isAssignableFrom(BooleanVariable.Kind.class))
        {
            BooleanVariable.Kind kind = BooleanVariable.Kind.fromSQLValue(sqlValue);
            this.setValue((A) kind);
        }
        else if (this.valueClass.isAssignableFrom(IntegerTermValue.Kind.class))
        {
            IntegerTermValue.Kind kind = IntegerTermValue.Kind.fromSQLValue(sqlValue);
            this.setValue((A) kind);
        }
        else if (this.valueClass.isAssignableFrom(BooleanTermValue.Kind.class))
        {
            BooleanTermValue.Kind kind = BooleanTermValue.Kind.fromSQLValue(sqlValue);
            this.setValue((A) kind);
        }
        else if (this.valueClass.isAssignableFrom(InvocationParameterType.class))
        {
            InvocationParameterType invocationParameterType =
                    InvocationParameterType.fromSQLValue(sqlValue);
            this.setValue((A) invocationParameterType);
        }
        else if (this.valueClass.isAssignableFrom(WidgetContentAlignment.class))
        {
            WidgetContentAlignment alignment = WidgetContentAlignment.fromSQLValue(sqlValue);
            this.setValue((A) alignment);
        }
        else if (this.valueClass.isAssignableFrom(RefinementType.class))
        {
            RefinementType refinementType = RefinementType.fromSQLValue(sqlValue);
            this.setValue((A) refinementType);
        }
        else if (this.valueClass.isAssignableFrom(ParameterType.class))
        {
            ParameterType parameterType = ParameterType.fromSQLValue(sqlValue);
            this.setValue((A) parameterType);
        }
        else if (this.valueClass.isAssignableFrom(Action.class))
        {
            Action action = Action.fromSQLValue(sqlValue);
            this.setValue((A) action);
        }
        else
        {
            throw DatabaseException.valueNotSerializable(
                    new ValueNotSerializableError(ValueNotSerializableError.Type.FROM,
                                                  valueClass.getName()));
        }
    }


    // ON UPDATE LISTENER
    // --------------------------------------------------------------------------------------

    public interface OnUpdateListener<A> {
        void onUpdate(A value);
    }
}
