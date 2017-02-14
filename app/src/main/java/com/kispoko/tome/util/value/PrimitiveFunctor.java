
package com.kispoko.tome.util.value;


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
import com.kispoko.tome.mechanic.dice.DiceType;
import com.kispoko.tome.sheet.SectionType;
import com.kispoko.tome.sheet.group.GroupBackground;
import com.kispoko.tome.sheet.group.GroupLabelType;
import com.kispoko.tome.sheet.group.RowAlignment;
import com.kispoko.tome.sheet.group.Spacing;
import com.kispoko.tome.sheet.group.RowWidth;
import com.kispoko.tome.sheet.widget.WidgetType;
import com.kispoko.tome.sheet.widget.action.ActionColor;
import com.kispoko.tome.sheet.widget.action.ActionSize;
import com.kispoko.tome.sheet.widget.action.ActionWidgetFormat;
import com.kispoko.tome.sheet.widget.number.NumberWidgetStyle;
import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.sheet.widget.table.cell.CellType;
import com.kispoko.tome.sheet.widget.table.column.ColumnType;
import com.kispoko.tome.sheet.widget.util.InlineLabelPosition;
import com.kispoko.tome.sheet.widget.util.WidgetBackground;
import com.kispoko.tome.sheet.widget.util.WidgetContentAlignment;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.WidgetCorners;
import com.kispoko.tome.sheet.widget.util.WidgetLabelAlignment;
import com.kispoko.tome.sheet.widget.util.WidgetTextTint;
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
        else if (valueClass.isAssignableFrom(DiceType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetContentSize.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetBackground.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetLabelAlignment.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetTextTint.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(NumberWidgetStyle.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetCorners.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ActionColor.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ActionSize.class))
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
        else if (valueClass.isAssignableFrom(WidgetContentAlignment.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(CellType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(SectionType.class))
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
        else if (valueClass.isAssignableFrom(InlineLabelPosition.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(RowWidth.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(GroupBackground.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(GroupLabelType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(Spacing.class))
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
        else if (valueClass.isAssignableFrom(ParameterType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(VariableType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(WidgetType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(VariableReferenceType.class))
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
        else if (valueClass.isAssignableFrom(DiceRollTermValue.Kind.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(TermType.class))
        {
            return SQLValue.Type.TEXT;
        }
        else if (valueClass.isAssignableFrom(ValueType.class))
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
        else if (this.getValue() instanceof WidgetBackground)
        {
            String enumString = ((WidgetBackground) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof WidgetLabelAlignment)
        {
            String enumString = ((WidgetLabelAlignment) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof WidgetTextTint)
        {
            String enumString = ((WidgetTextTint) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof NumberWidgetStyle)
        {
            String enumString = ((NumberWidgetStyle) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof WidgetCorners)
        {
            String enumString = ((WidgetCorners) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ActionColor)
        {
            String enumString = ((ActionColor) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ActionSize)
        {
            String enumString = ((ActionSize) this.getValue()).name().toLowerCase();
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
        else if (this.getValue() instanceof DiceType)
        {
            String enumString = ((DiceType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
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
        else if (this.getValue() instanceof SectionType)
        {
            String enumString = ((SectionType) this.getValue()).name().toLowerCase();
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
        else if (this.getValue() instanceof InlineLabelPosition)
        {
            String enumString = ((InlineLabelPosition) this.getValue()).name().toLowerCase();
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
        else if (this.getValue() instanceof GroupBackground)
        {
            String enumString = ((GroupBackground) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof GroupLabelType)
        {
            String enumString = ((GroupLabelType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof Spacing)
        {
            String enumString = ((Spacing) this.getValue()).name().toLowerCase();
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
        else if (this.getValue() instanceof ParameterType)
        {
            String enumString = ((ParameterType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof VariableType)
        {
            String enumString = ((VariableType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof Spacing)
        {
            String enumString = ((Spacing) this.getValue()).name().toLowerCase();
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
        else if (this.getValue() instanceof ParameterType)
        {
            String enumString = ((ParameterType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof VariableType)
        {
            String enumString = ((VariableType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof WidgetType)
        {
            String enumString = ((WidgetType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof VariableReferenceType)
        {
            String enumString = ((VariableReferenceType) this.getValue()).name().toLowerCase();
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
        else if (this.getValue() instanceof DiceRollTermValue.Kind)
        {
            String enumString = ((DiceRollTermValue.Kind) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof TermType)
        {
            String enumString = ((TermType) this.getValue()).name().toLowerCase();
            return SQLValue.newText(enumString);
        }
        else if (this.getValue() instanceof ValueType)
        {
            String enumString = ((ValueType) this.getValue()).name().toLowerCase();
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
        else if (this.valueClass.isAssignableFrom(WidgetContentSize.class))
        {
            WidgetContentSize size = WidgetContentSize.fromSQLValue(sqlValue);
            this.setValue((A) size);
        }
        else if (this.valueClass.isAssignableFrom(WidgetBackground.class))
        {
            WidgetBackground background = WidgetBackground.fromSQLValue(sqlValue);
            this.setValue((A) background);
        }
        else if (this.valueClass.isAssignableFrom(WidgetLabelAlignment.class))
        {
            WidgetLabelAlignment alignment = WidgetLabelAlignment.fromSQLValue(sqlValue);
            this.setValue((A) alignment);
        }
        else if (this.valueClass.isAssignableFrom(WidgetTextTint.class))
        {
            WidgetTextTint tint = WidgetTextTint.fromSQLValue(sqlValue);
            this.setValue((A) tint);
        }
        else if (this.valueClass.isAssignableFrom(NumberWidgetStyle.class))
        {
            NumberWidgetStyle style = NumberWidgetStyle.fromSQLValue(sqlValue);
            this.setValue((A) style);
        }
        else if (this.valueClass.isAssignableFrom(WidgetCorners.class))
        {
            WidgetCorners corners = WidgetCorners.fromSQLValue(sqlValue);
            this.setValue((A) corners);
        }
        else if (this.valueClass.isAssignableFrom(ActionColor.class))
        {
            ActionColor color = ActionColor.fromSQLValue(sqlValue);
            this.setValue((A) color);
        }
        else if (this.valueClass.isAssignableFrom(ActionSize.class))
        {
            ActionSize size = ActionSize.fromSQLValue(sqlValue);
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
        else if (this.valueClass.isAssignableFrom(InlineLabelPosition.class))
        {
            InlineLabelPosition position = InlineLabelPosition.fromSQLValue(sqlValue);
            this.setValue((A) position);
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
        else if (this.valueClass.isAssignableFrom(GroupBackground.class))
        {
            GroupBackground background = GroupBackground.fromSQLValue(sqlValue);
            this.setValue((A) background);
        }
        else if (this.valueClass.isAssignableFrom(GroupLabelType.class))
        {
            GroupLabelType groupLabelType = GroupLabelType.fromSQLValue(sqlValue);
            this.setValue((A) groupLabelType);
        }
        else if (this.valueClass.isAssignableFrom(Spacing.class))
        {
            Spacing separation = Spacing.fromSQLValue(sqlValue);
            this.setValue((A) separation);
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
        else if (this.valueClass.isAssignableFrom(WidgetContentAlignment.class))
        {
            WidgetContentAlignment alignment = WidgetContentAlignment.fromSQLValue(sqlValue);
            this.setValue((A) alignment);
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
}
