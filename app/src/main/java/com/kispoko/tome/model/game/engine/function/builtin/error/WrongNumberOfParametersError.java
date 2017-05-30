
package com.kispoko.tome.model.game.engine.function.builtin.error;



/**
 * Built-In Funtion Error: Wrong Number of Parameters
 */
public class WrongNumberOfParametersError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private int numberOfParametersFound;
    private int numberOfParametersRequired;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public WrongNumberOfParametersError(int numberOfParametersFound, int numberOfParametersRequired)
    {
        this.numberOfParametersFound    = numberOfParametersFound;
        this.numberOfParametersRequired = numberOfParametersRequired;
    }


    // API
    // --------------------------------------------------------------------------------------

    public int getNumberOfParametersFound()
    {
        return this.numberOfParametersFound;
    }


    public int getNumberOfParametersRequired()
    {
        return this.numberOfParametersRequired;
    }


    public String errorMessage()
    {
        return "Wrong Number Of Parameters:\n" +
               "    Number Found: " + this.numberOfParametersFound + "\n" +
               "    Number Required: " + this.numberOfParametersRequired;
    }

}
