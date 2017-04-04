
package com.kispoko.tome.lib.functor;


import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.kispoko.tome.engine.program.invocation.InvocationParameterType;
import com.kispoko.tome.engine.program.ProgramValueType;
import com.kispoko.tome.engine.program.statement.ParameterType;
import com.kispoko.tome.engine.summation.term.BooleanTermValue;
import com.kispoko.tome.engine.summation.term.DiceRollTermValue;
import com.kispoko.tome.engine.summation.term.IntegerTermValue;
import com.kispoko.tome.engine.summation.term.TermType;
import com.kispoko.tome.engine.value.ValueType;
import com.kispoko.tome.engine.variable.BooleanVariable;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.VariableReferenceType;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.lib.model.form.FieldOptions;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.mechanic.dice.DiceType;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.SectionType;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.WidgetType;
import com.kispoko.tome.sheet.widget.table.cell.CellType;
import com.kispoko.tome.sheet.widget.table.column.ColumnType;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.widget.util.TextFont;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.util.SerialBitmap;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.SQL;
import com.kispoko.tome.lib.database.error.ValueNotSerializableError;
import com.kispoko.tome.lib.database.sql.SQLValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;



/**
 * Primitive Value
 *
 * TODO Generalize the enum serialization code here
 */
public class PrimitiveFunctor<A> extends Functor<A>
                                 implements Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Class<A>            valueClass;



    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public PrimitiveFunctor(A value,
                            Class<A> valueClass,
                            boolean isRequired)
    {
        super(value, isRequired);
        this.valueClass       = valueClass;
    }


    public PrimitiveFunctor(A value,
                            Class<A> valueClass)
    {
        super(value);
        this.valueClass       = valueClass;
    }


    /**
     * Create a primitive functor that is required to have a non-null value before being saved.
     * @param value The value.
     * @param valueClass The value's class object.
     * @param <A> The value type.
     * @return The "required" Primitive Functor.
     */
    public static <A> PrimitiveFunctor required(A value, Class<A> valueClass)
    {
        return new PrimitiveFunctor<>(value, valueClass, true);
    }


    // API
    // --------------------------------------------------------------------------------------

    // > Column Name
    // --------------------------------------------------------------------------------------

    public String sqlColumnName()
    {
        return SQL.asValidIdentifier(this.name().toLowerCase());
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


    // > Helpers
    // --------------------------------------------------------------------------------------

    // TODO delete all enum cases
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
        else if (valueClass.isAssignableFrom(GregorianCalendar.class))
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
        else if (valueClass.isEnum())
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
        else if (valueClass.isAssignableFrom(ProgramValueType[].class))
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
        else if (this.getValue() instanceof GregorianCalendar)
        {
            Long milliseconds = ((GregorianCalendar) this.getValue()).getTimeInMillis();
            return SQLValue.newInteger(milliseconds);
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
        else if (this.getValue().getClass().isEnum())
        {
            String enumString = ((Enum) this.getValue()).name().toLowerCase();
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
        else if (this.valueClass.isAssignableFrom(GregorianCalendar.class))
        {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTimeInMillis(sqlValue.getInteger());
            this.setValue((A) gregorianCalendar);
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
        else if (this.valueClass.isAssignableFrom(DiceType.class))
        {
            DiceType diceType = DiceType.fromSQLValue(sqlValue);
            this.setValue((A) diceType);
        }
        else if (this.valueClass.isAssignableFrom(CellType.class))
        {
            CellType cellType = CellType.fromSQLValue(sqlValue);
            this.setValue((A) cellType);
        }
        else if (this.valueClass.isAssignableFrom(Position.class))
        {
            Position position = Position.fromSQLValue(sqlValue);
            this.setValue((A) position);
        }
        else if (this.valueClass.isAssignableFrom(Height.class))
        {
            Height height = Height.fromSQLValue(sqlValue);
            this.setValue((A) height);
        }
        else if (this.valueClass.isAssignableFrom(TextFont.class))
        {
            TextFont textFont = TextFont.fromSQLValue(sqlValue);
            this.setValue((A) textFont);
        }
        else if (this.valueClass.isAssignableFrom(SectionType.class))
        {
            SectionType sectionType = SectionType.fromSQLValue(sqlValue);
            this.setValue((A) sectionType);
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
        else if (this.valueClass.isAssignableFrom(TextSize.class))
        {
            TextSize size = TextSize.fromSQLValue(sqlValue);
            this.setValue((A) size);
        }
        else if (this.valueClass.isAssignableFrom(BackgroundColor.class))
        {
            BackgroundColor background = BackgroundColor.fromSQLValue(sqlValue);
            this.setValue((A) background);
        }
        else if (this.valueClass.isAssignableFrom(DividerType.class))
        {
            DividerType dividerType = DividerType.fromSQLValue(sqlValue);
            this.setValue((A) dividerType);
        }
        else if (this.valueClass.isAssignableFrom(Alignment.class))
        {
            Alignment alignment = Alignment.fromSQLValue(sqlValue);
            this.setValue((A) alignment);
        }
        else if (this.valueClass.isAssignableFrom(TextColor.class))
        {
            TextColor tint = TextColor.fromSQLValue(sqlValue);
            this.setValue((A) tint);
        }
        else if (this.valueClass.isAssignableFrom(Corners.class))
        {
            Corners corners = Corners.fromSQLValue(sqlValue);
            this.setValue((A) corners);
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
        else if (this.valueClass.isAssignableFrom(VariableType.class))
        {
            VariableType variableType = VariableType.fromSQLValue(sqlValue);
            this.setValue((A) variableType);
        }
        else if (this.valueClass.isAssignableFrom(WidgetType.class))
        {
            WidgetType widgetType = WidgetType.fromSQLValue(sqlValue);
            this.setValue((A) widgetType);
        }
        else if (this.valueClass.isAssignableFrom(ValueType.class))
        {
            ValueType valueType = ValueType.fromSQLValue(sqlValue);
            this.setValue((A) valueType);
        }
        else if (this.valueClass.isAssignableFrom(VariableReferenceType.class))
        {
            VariableReferenceType variableReferenceType =
                    VariableReferenceType.fromSQLValue(sqlValue);
            this.setValue((A) variableReferenceType);
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
        else if (this.valueClass.isAssignableFrom(DiceRollTermValue.Kind.class))
        {
            DiceRollTermValue.Kind kind = DiceRollTermValue.Kind.fromSQLValue(sqlValue);
            this.setValue((A) kind);
        }
        else if (this.valueClass.isAssignableFrom(TermType.class))
        {
            TermType termType = TermType.fromSQLValue(sqlValue);
            this.setValue((A) termType);
        }
        else if (this.valueClass.isAssignableFrom(InvocationParameterType.class))
        {
            InvocationParameterType invocationParameterType =
                    InvocationParameterType.fromSQLValue(sqlValue);
            this.setValue((A) invocationParameterType);
        }
        else if (this.valueClass.isAssignableFrom(ParameterType.class))
        {
            ParameterType parameterType = ParameterType.fromSQLValue(sqlValue);
            this.setValue((A) parameterType);
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


    // FORM
    // --------------------------------------------------------------------------------------

    public Field field(UUID modelId, boolean isEditMode, Context context)
    {
        // > Field Data

        // ** Name
        String fieldName = this.name();

        // ** Label
        String fieldLabel = "";
        if (this.label() != null)
            fieldLabel = this.label();
        else if (this.labelId() != null)
            fieldLabel = context.getString(this.labelId());

        // ** Description
        String fieldDescription = "";
        if (this.description() != null)
            fieldDescription = this.description();
        else if (this.descriptionId() != null)
            fieldDescription = context.getString(this.descriptionId());

        String fieldValue = null;
        if (!this.isNull())
            fieldValue = this.value.toString();

        // > Field Options
        FieldOptions fieldOptions;
        if (isEditMode)
            fieldOptions = FieldOptions.newField(this.isRequired());
        else
            fieldOptions = new FieldOptions();

        if (isEditMode)
            return Field.text(modelId, fieldName, fieldLabel, fieldValue);
        else
            return Field.textNew(modelId, fieldName, fieldLabel, fieldDescription);

    }

}
