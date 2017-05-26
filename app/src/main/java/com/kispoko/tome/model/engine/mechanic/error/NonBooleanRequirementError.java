
package com.kispoko.tome.model.engine.mechanic.error;



/**
 * Mechanic Error: Non-Boolean Requirement Variable
 */
public class NonBooleanRequirementError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String mechanicName;
    private String variableName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public NonBooleanRequirementError(String mechanicName, String variableName)
    {
        this.mechanicName = mechanicName;
        this.variableName = variableName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getVariableName()
    {
        return this.variableName;
    }


    public String errorMessage()
    {
        return "Non-Boolean Requirement: " +
                "    Mechanic Name: " + this.mechanicName + "\n" +
                "    Variable Name: " + this.variableName;
    }


}
