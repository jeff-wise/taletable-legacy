
package com.kispoko.tome.model.engine.variable;


import java.io.Serializable;



/**
 * Variable Namespace
 */
public class Namespace implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String name;
    private String label;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Namespace(String name, String label)
    {
        this.name  = name;
        this.label = label;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String name()
    {
        return this.name;
    }


    public String label()
    {
        return this.label;
    }


}
