
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.database.ColumnProperties;



/**
 * Primitive Value
 */
public class PrimitiveValue<A> extends Value<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private ColumnProperties columnProperties;

    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public PrimitiveValue(A value, Model model, ColumnProperties columnProperties)
    {
        super(value, model);
        this.columnProperties = columnProperties;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    // ** Column Properties
    // --------------------------------------------------------------------------------------

    public ColumnProperties getColumnProperties()
    {
        return this.columnProperties;
    }



}
