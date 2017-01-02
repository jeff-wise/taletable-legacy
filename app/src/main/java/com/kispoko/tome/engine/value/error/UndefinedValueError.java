
package com.kispoko.tome.engine.value.error;


import com.kispoko.tome.engine.value.ValueType;



/**
 * Undefined Value Error
 */
public class UndefinedValueError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String valueSetName;
    private String valueName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UndefinedValueError(String valueSetName,
                               String valueName)
    {
        this.valueSetName   = valueSetName;
        this.valueName      = valueName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Undefined Value: " +
                "    Value Set Name: " + this.valueSetName + "\n" +
                "    Value Name: " + this.valueName;
    }

}
