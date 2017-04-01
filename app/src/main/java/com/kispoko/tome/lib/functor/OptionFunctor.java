
package com.kispoko.tome.lib.functor;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.SQL;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.util.EnumUtils;

import java.io.Serializable;



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
        super(value, isRequired);

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

}
