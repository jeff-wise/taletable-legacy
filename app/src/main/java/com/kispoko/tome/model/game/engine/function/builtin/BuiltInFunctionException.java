
package com.kispoko.tome.model.game.engine.function.builtin;



/**
 * Built-In Function Exception
 */
public class BuiltInFunctionException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private Type   errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private BuiltInFunctionException(Object error, Type errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }
//
//
//    public static BuiltInFunctionException
//                        wrongNumberOfParameters(WrongNumberOfParametersError error)
//    {
//        return new BuiltInFunctionException(error, Type.WRONG_NUMBER_OF_PARAMETERS);
//    }
//
//
//    public static BuiltInFunctionException invalidParameterType(InvalidParameterTypeError error)
//    {
//        return new BuiltInFunctionException(error, Type.INVALID_PARAMETER_TYPE);
//    }


    // API
    // ------------------------------------------------------------------------------------------


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Built-In Function Error: ");

//        switch (this.errorType)
//        {
//            case WRONG_NUMBER_OF_PARAMETERS:
//                errorBuilder.append(((WrongNumberOfParametersError) this.error).errorMessage());
//                break;
//            case INVALID_PARAMETER_TYPE:
//                errorBuilder.append(((InvalidParameterTypeError) this.error).errorMessage());
//                break;
//        }

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        WRONG_NUMBER_OF_PARAMETERS,
        INVALID_PARAMETER_TYPE,
    }

}

