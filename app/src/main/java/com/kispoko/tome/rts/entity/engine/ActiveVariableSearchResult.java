
package com.kispoko.tome.rts.entity.engine;


import com.kispoko.tome.rts.entity.engine.search.EngineSearchResult;



/**
 * Active Variable Search Result
 *
 * A search result representing a variable that is currently active.
 */
public class ActiveVariableSearchResult implements EngineSearchResult
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String      name;
    private boolean     nameIsMatched;

    private String      label;
    private boolean     labelIsMatched;


    // > Search Result
    // -----------------------------------------------------------------------------------------

    private float       ranking;


    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------

    public ActiveVariableSearchResult(String name, String label)
    {
        this.name           = name;
        this.nameIsMatched  = false;

        this.label          = label;
        this.labelIsMatched = false;

        this.ranking        = 1f;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > State
    // -----------------------------------------------------------------------------------------

    // ** Variable Name
    // -----------------------------------------------------------------------------------------

    /**
     * The result variable's name.
     * @return The variable name String.
     */
    public String name()
    {
        return this.name;
    }


    public boolean nameIsMatched()
    {
        return this.nameIsMatched;
    }


    public void setNameIsMatched()
    {
        this.nameIsMatched = true;
    }


    // ** Variable Label
    // -----------------------------------------------------------------------------------------

    /**
     * The result variable's label.
     * @return The variable label String.
     */
    public String label()
    {
        return this.label;
    }


    public boolean labelIsMatched()
    {
        return this.labelIsMatched;
    }


    public void setLabelIsMatched()
    {
        this.labelIsMatched = true;
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
