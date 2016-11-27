
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.database.DatabaseException;

import java.lang.reflect.Field;



/**
 * Value
 */
public abstract class Value<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    protected A       value;
    private   Field   field;
    private   boolean isSaved;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Value(A value)
    {
        this.value            = value;

        this.isSaved          = false;
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


    public void setValue(A newValue)
    {
        if (newValue != null) {
            this.value = newValue;
        }
    }

    // ** Field
    // --------------------------------------------------------------------------------------

    public Field getField()
    {
        return this.field;
    }


    public void setField(Field field)
    {
        this.field = field;
    }


    // > Data
    // --------------------------------------------------------------------------------------

    // ** Is Null
    // --------------------------------------------------------------------------------------

    /**
     * Returns true if the data inside the value is null.
     * @return True if the data in the value is null, or false otherwise.
     */
    public boolean isNull()
    {
        return this.value == null;
    }


    // ** Name
    // --------------------------------------------------------------------------------------

    public String name()
    {
        return this.field.getName().toLowerCase();
    }

}
