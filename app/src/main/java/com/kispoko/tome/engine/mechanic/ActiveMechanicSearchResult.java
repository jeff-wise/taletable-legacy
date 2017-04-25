
package com.kispoko.tome.engine.mechanic;


import com.kispoko.tome.engine.search.EngineSearchResult;



/**
 * Active Mechanic Search Result
 *
 * A search result that represents an active mechanic was found.
 */
public class ActiveMechanicSearchResult implements EngineSearchResult
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String  mechanicName;
    private String  mechanicLabel;


    // > Search Result
    // -----------------------------------------------------------------------------------------

    private float   ranking;



    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------

    public ActiveMechanicSearchResult(String mechanicName, String mechanicLabel, float ranking)
    {
        this.mechanicName  = mechanicName;
        this.mechanicLabel = mechanicLabel;

        this.ranking       = ranking;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > State
    // -----------------------------------------------------------------------------------------

    /**
     * The result mechanic's name.
     * @return The mechanic name String.
     */
    public String mechanicName()
    {
        return this.mechanicName;
    }


    /**
     * The result mechanic's label.
     * @return The mechanic label String.
     */
    public String mechanicLabel()
    {
        return this.mechanicLabel;
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
