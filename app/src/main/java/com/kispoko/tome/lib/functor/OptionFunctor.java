
package com.kispoko.tome.lib.functor;


import android.content.Context;

import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.SQL;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.util.EnumUtils;

import java.io.Serializable;
import java.util.UUID;


/**
 * Option Functor.
 *
 * A functor that holds Enum Values
 */
public class OptionFunctor<A extends Enum> extends Functor<A>
                                            implements Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Class<A>            valueClass;



    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public OptionFunctor(A value,
                         Class<A> valueClass,
                         boolean isRequired)
    {
        super(value);

        this.valueClass       = valueClass;
    }


    public OptionFunctor(A value,
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
    public static <A extends Enum> OptionFunctor required(A value, Class<A> valueClass)
    {
        return new OptionFunctor<>(value, valueClass, true);
    }


    // API
    // --------------------------------------------------------------------------------------

    // > Column Name
    // --------------------------------------------------------------------------------------

    public String sqlColumnName()
    {
        return SQL.asValidIdentifier(this.name());
    }


    // > To SQL Value
    // --------------------------------------------------------------------------------------

    public SQLValue toSQLValue()
           throws DatabaseException
    {
        return SQLValue.newText(this.getValue().name());
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


    // > From SQL Value
    // --------------------------------------------------------------------------------------

    public void fromSQLValue(SQLValue sqlValue)
            throws DatabaseException
    {
        String valueString = sqlValue.getText();

        try
        {
            this.setValue((A) EnumUtils.fromString(this.valueClass, valueString));

        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                new com.kispoko.tome.lib.database.error.InvalidEnumError(valueString));
        }
    }



    // FORM
    // --------------------------------------------------------------------------------------

    public Field field(UUID modelId, Context context)
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

        // ** Value
        String valueString = null;
        if (this.value != null)
            valueString = this.value.name().toUpperCase();

        return Field.option(modelId, fieldName, fieldLabel, fieldDescription, valueString);
    }
}
