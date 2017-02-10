
package com.kispoko.tome.engine.summation.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Summation Error: Null Term
 */
public class NullTermError implements ApplicationError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String termName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public NullTermError(String termName)
    {
        this.termName = termName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Null Term: " + this.termName;
    }


}
