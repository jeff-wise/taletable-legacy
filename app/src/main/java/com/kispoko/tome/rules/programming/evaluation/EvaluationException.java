
package com.kispoko.tome.rules.programming.evaluation;



/**
 * Evaluation Excdption
 */
public class EvaluationException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private EvaluationError error;
    private EvaluationError.Type errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EvaluationException() { }

    public EvaluationException(EvaluationError error, EvaluationError.Type errorType)
    {
        this.errorType = errorType;
    }

}

