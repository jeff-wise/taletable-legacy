
package com.kispoko.tome.util.yaml.error;


import com.kispoko.tome.util.yaml.Yaml;



/**
 * Yaml Error: Unexpected Type
 */
public class UnexpectedTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Yaml.ObjectType expectedType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnexpectedTypeError(Yaml.ObjectType expectedType)
    {
        this.expectedType = expectedType;
    }


    // API
    // --------------------------------------------------------------------------------------

    public Yaml.ObjectType getExpectedType()
    {
        return this.expectedType;
    }

}
