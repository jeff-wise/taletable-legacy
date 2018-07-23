
package com.taletable.android.model.engine.variable.error;


import com.taletable.android.model.engine.variable.VariableReference;


/**
 * Variable Error: Unexpected Type
 */
public class UnexpectedVariableTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String              variableName;
    private VariableReference variableReference;
//    private VariableType        expectedType;
//    private VariableType        actualType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

//    public UexpectedVariableTypeError(String variableName)
//                                       VariableType expectedType,
//                                       VariableType actualType)
 //   {
  //      this.variableName = variableName;
//        this.expectedType = expectedType;
//        this.actualType   = actualType;
 //   }


    public UnexpectedVariableTypeError(VariableReference variableReference)
//                                       VariableType expectedType,
//                                       VariableType actualType)
    {
        this.variableReference  = variableReference;
//        this.expectedType       = expectedType;
//        this.actualType         = actualType;
    }


    // API
    // --------------------------------------------------------------------------------------
//
//    public String errorMessage()
//    {
//        if (variableReference != null)
//        {
//            switch(this.variableReference.type())
//            {
//                case NAME:
//                    return "Unexpected Variable Type: " +
//                            "    Variable Name: " + this.variableReference.name() + "\n" +
//                            "    Expected Type: " + this.expectedType.name() + "\n" +
//                            "    Actual Type: " + this.actualType.name();
//                case TAG:
//                    return "Unexpected Variable Type: " +
//                            "    Variable Tag: " + this.variableReference.tag() + "\n" +
//                            "    Expected Type: " + this.expectedType.name() + "\n" +
//                            "    Actual Type: " + this.actualType.name();
//            }
//        }
//        else
//        {
//            return "Unexpected Variable Type: " +
//                    "    Variable Name: " + this.variableName + "\n" +
//                    "    Expected Type: " + this.expectedType.name() + "\n" +
//                    "    Actual Type: " + this.actualType.name();
//        }
//
//        return "";
//    }


}
