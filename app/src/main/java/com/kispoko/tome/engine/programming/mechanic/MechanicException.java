
package com.kispoko.tome.engine.programming.mechanic;


import com.kispoko.tome.engine.programming.mechanic.error.NonBooleanRequirementError;



/**
 * Mechanic Exception
 */
public class MechanicException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private ErrorType errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private MechanicException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static MechanicException nonBooleanRequirement(NonBooleanRequirementError error)
    {
        return new MechanicException(error, ErrorType.NONBOOLEAN_REQUIRMENT);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Mechanic Error: ");

        switch (this.errorType)
        {
            case NONBOOLEAN_REQUIRMENT:
                errorBuilder.append(((NonBooleanRequirementError) this.error).errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum ErrorType
    {
        NONBOOLEAN_REQUIRMENT
    }


}
