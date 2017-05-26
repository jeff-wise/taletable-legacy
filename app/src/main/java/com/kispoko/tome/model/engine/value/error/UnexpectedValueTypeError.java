
package com.kispoko.tome.model.engine.value.error;


import com.kispoko.tome.model.engine.value.ValueType;



/**
 * Value Error: Unexpected Value Type
 */
public class UnexpectedValueTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String    valueSetName;
    private String    valueName;
    private ValueType expectedValue;
    private ValueType actualValue;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnexpectedValueTypeError(String valueSetName,
                                    String valueName,
                                    ValueType expectedValue,
                                    ValueType actualValue)
    {
        this.valueSetName   = valueSetName;
        this.valueName      = valueName;
        this.expectedValue  = expectedValue;
        this.actualValue    = actualValue;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Unexpected Value Type: " +
                "    Value Set Name: " + this.valueSetName + "\n" +
                "    Value Name: " + this.valueName + "\n" +
                "    Expected Value: " + this.expectedValue.name() + "\n" +
                "    Actual Value: " + this.actualValue.name();
    }

}
