
package com.kispoko.tome.engine.interpreter.error;


import com.kispoko.tome.engine.EngineDataType;


/**
 * Evaluation Error: Undefined Variable
 */
public class UnexpectedProgramVariableTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String           variableName;
    private EngineDataType actualVariableType;
    private EngineDataType expectedVariableType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnexpectedProgramVariableTypeError(String variableName,
                                              EngineDataType actualVariableType,
                                              EngineDataType expectedVariableType)
    {
        this.variableName         = variableName;
        this.actualVariableType   = actualVariableType;
        this.expectedVariableType = expectedVariableType;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Unexpected Program Variable ErrorType:\n" +
               "    Variable Name: " + this.variableName + "\n" +
               "    Actual Variable ErrorType: " + this.actualVariableType.toString() + "\n" +
               "    Expected Variable ErrorType: " + this.expectedVariableType.toString();
    }

}