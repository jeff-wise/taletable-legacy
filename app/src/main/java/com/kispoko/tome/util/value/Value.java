
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

    private A        value;
    private Model    model;

    private boolean  isSaved;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Value(A value, Model model)
    {
        this.value      = value;
        this.model      = model;

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
        //    model.onUpdateModel(this.name);
        }
    }


    public boolean isNull()
    {
        return this.value == null;
    }


}
