
package com.kispoko.tome.util;



/**
 * Value
 */
public abstract class Value<A>
{

    private String name;
    private A value;
    private Model model;


    public Value(String name, A value, Model model)
    {
        this.name = name;
        this.value = value;
        this.model = model;
    }


    public A getValue()
    {
        return this.value;
    }


    public void setValue(A value)
    {
        if (this.value != null) {
            this.value = value;
            model.onUpdateModel(this.name);
        }
    }


    public boolean isNull()
    {
        return this.value == null;
    }
}
