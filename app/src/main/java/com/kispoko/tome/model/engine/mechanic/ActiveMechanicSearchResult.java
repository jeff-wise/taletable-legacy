
package com.kispoko.tome.model.engine.mechanic;


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

    private String  name;
    private boolean nameIsMatched;

    private String  label;
    private boolean labelIsMatched;

    private String  variables;
    private boolean variableIsMatched;


    // > Search Result
    // -----------------------------------------------------------------------------------------

    private float   ranking;


    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------

    public ActiveMechanicSearchResult(String name, String label, String variables)
    {
        this.name               = name;
        this.nameIsMatched      = false;

        this.label              = label;
        this.labelIsMatched     = false;

        this.variables          = variables;
        this.variableIsMatched  = false;

        this.ranking            = 1f;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > State
    // -----------------------------------------------------------------------------------------

    // ** Name
    // -----------------------------------------------------------------------------------------

    /**
     * The result mechanic's name.
     * @return The mechanic name String.
     */
    public String name()
    {
        return this.name;
    }


    public boolean nameIsMatch()
    {
        return this.nameIsMatched;
    }


    public void setNameIsMatch()
    {
        this.nameIsMatched = true;
    }


    // ** Label
    // -----------------------------------------------------------------------------------------

    /**
     * The result mechanic's label.
     * @return The mechanic label String.
     */
    public String label()
    {
        return this.label;
    }


    public boolean labelIsMatch()
    {
        return this.labelIsMatched;
    }


    public void setLabelIsMatch()
    {
        this.labelIsMatched = true;
    }


    // ** Variables
    // -----------------------------------------------------------------------------------------

    /**
     * A comma-separted  list of the mechanic's variables.
     * @return The mechanic variable list String.
     */
    public String variables()
    {
        return this.variables;
    }


    public boolean variablesIsMatch()
    {
        return this.variableIsMatched;
    }


    public void setVariablesIsMatch()
    {
        this.variableIsMatched = true;
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
