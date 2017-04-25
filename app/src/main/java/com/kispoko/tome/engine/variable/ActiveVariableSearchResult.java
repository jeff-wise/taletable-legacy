
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.engine.search.EngineSearchResult;



/**
 * Active Variable Search Result
 *
 * A search result representing a variable that is currently active.
 */
public class ActiveVariableSearchResult implements EngineSearchResult
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String variableName;
    private String variableLabel;


    // > Search Result
    // -----------------------------------------------------------------------------------------

    private float    ranking;


    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------

    public ActiveVariableSearchResult(String variableName, String variableLabel, float ranking)
    {
        this.variableName   = variableName;
        this.variableLabel  = variableLabel;

        this.ranking        = ranking;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > State
    // -----------------------------------------------------------------------------------------

    /**
     * The result variable's name.
     * @return The variable name String.
     */
    public String variableName()
    {
        return this.variableName;
    }


    /**
     * The result variable's label.
     * @return The variable label String.
     */
    public String variableLabel()
    {
        return this.variableLabel;
    }


    // > Search Result
    // -----------------------------------------------------------------------------------------

    public float ranking()
    {
        return this.ranking;
    }


    public void addToRanking(float f)
    {
        this.ranking += f;
    }
}
