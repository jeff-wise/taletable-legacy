
package com.kispoko.tome.engine.interpreter.error;



/**
 * Evaluation Error: Undefined Variable
 */
public class FunctionNotFoundError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String functionName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public FunctionNotFoundError(String functionName)
    {
        this.functionName = functionName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getFunctionName()
    {
        return this.functionName;
    }


    public String errorMessage()
    {
        return "Function Not Found: " + this.functionName;
    }

}
