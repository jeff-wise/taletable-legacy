
package com.kispoko.tome.engine.programming.interpreter.error;



/**
 * Evaluation Error: Undefined Program
 */
public class UndefinedProgramError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String programName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UndefinedProgramError(String programName)
    {
        this.programName = programName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getProgramName()
    {
        return this.programName;
    }


    public String errorMessage()
    {
        return "Undefined Program: " + this.programName;
    }

}