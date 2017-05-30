
package com.kispoko.tome.rts.game.engine.interpreter.error;



/**
 * Interpreter Error: Null Result
 */
public class NullResultError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String functionName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public NullResultError(String functionName)
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
        return "Null Function Result: " + this.functionName;
    }

}
