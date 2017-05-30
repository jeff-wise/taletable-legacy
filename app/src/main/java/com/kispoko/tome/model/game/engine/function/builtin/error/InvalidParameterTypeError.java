
package com.kispoko.tome.model.game.engine.function.builtin.error;


/**
 * Built-In Funtion Error: Invalid Parameter ErrorType
 */
public class InvalidParameterTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private int              parameterIndex;
//    private EngineDataType actualParameterType;
//    private EngineDataType expectedParameterType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public InvalidParameterTypeError(int parameterIndex)
//                                     EngineDataType actualParameterType,
//                                     EngineDataType expectedParameterType
    {
        this.parameterIndex        = parameterIndex;
//        this.actualParameterType   = actualParameterType;
//        this.expectedParameterType = expectedParameterType;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Invalid Parameter ErrorType:\n" +
               "    Parameter Index: " + Integer.toString(this.parameterIndex) + "\n";
//               "    Actual Parameter ErrorType: " + this.actualParameterType.toString() + "\n" +
//               "    Expected Parameter ErrorType: " + this.expectedParameterType.toString();
    }

}
