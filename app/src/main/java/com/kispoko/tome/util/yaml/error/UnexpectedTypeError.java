
package com.kispoko.tome.util.yaml.error;


import com.kispoko.tome.util.yaml.YamlObjectType;



/**
 * Yaml Parsing Error: Unexpected ErrorType
 */
public class UnexpectedTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private YamlObjectType expectedType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnexpectedTypeError(YamlObjectType expectedType)
    {
        this.expectedType = expectedType;
    }


    // API
    // --------------------------------------------------------------------------------------

    public YamlObjectType getExpectedType()
    {
        return this.expectedType;
    }


    public String errorMessage()
    {
        return "Unexpected ErrorType Error:\n Expected: " + this.expectedType.toString();
    }

}
