
package com.kispoko.tome.util.value;



import java.lang.reflect.Field;



/**
 * Value
 */
public abstract class Value<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    protected A                value;

    private   Field            field;

    private   OnUpdateListener onUpdateListener;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Value(A value)
    {
        this.value            = value;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    // ** Value
    // --------------------------------------------------------------------------------------

    public A getValue()
    {
        return this.value;
    }


    public void setValue(A newValue)
    {
        if (newValue != null)
        {
            this.value = newValue;

            if (this.onUpdateListener != null)
                this.onUpdateListener.onUpdate();
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


    // ** Value
    // --------------------------------------------------------------------------------------

    public void setOnUpdateListener(OnUpdateListener onUpdateListener)
    {
        this.onUpdateListener = onUpdateListener;
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


    // LISTENERS
    // ------------------------------------------------------------------------------------------

    public interface OnUpdateListener {
        void onUpdate();
    }


}
