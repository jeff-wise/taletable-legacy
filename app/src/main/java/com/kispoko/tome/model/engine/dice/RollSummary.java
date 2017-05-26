
package com.kispoko.tome.model.engine.dice;


import java.util.ArrayList;
import java.util.List;



/**
 * Roll Summary
 */
public class RollSummary
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private List<DieRollResult> rollResults;
    private List<RollModifier>  modifiers;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private Integer             value;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public RollSummary(List<DieRollResult> rollResults,
                       List<RollModifier> modifiers)
    {
        this.rollResults = rollResults;
        this.modifiers   = modifiers;
    }


    public RollSummary(List<DieRollResult> rollResults)
    {
        this.rollResults = rollResults;
        this.modifiers   = new ArrayList<>();
    }


    // API
    // -----------------------------------------------------------------------------------------

    /**
     * Get the value of the roll. Calculates the value lazily, since this class is immutable.
     * @return The roll value.
     */
    public int rollValue()
    {
        // > Value is already calculated
        if (this.value != null)
            return this.value;

        // > Calculate value once
        this.value = 0;

        for (DieRollResult result : this.rollResults) {
            this.value += result.value();
        }

        for (RollModifier modifier : this.modifiers()) {
            this.value += modifier.value();
        }

        return this.value;
    }


    public List<DieRollResult> rollResults()
    {
        return this.rollResults;
    }


    public List<RollModifier> modifiers()
    {
        return this.modifiers;
    }


    public RollSummary addSummary(RollSummary rollSummary)
    {
        List<DieRollResult> combinedRollResults = new ArrayList<>();
        combinedRollResults.addAll(this.rollResults());
        combinedRollResults.addAll(rollSummary.rollResults());

        List<RollModifier> combinedModifiers = new ArrayList<>();
        combinedModifiers.addAll(this.modifiers());
        combinedModifiers.addAll(rollSummary.modifiers());

        return new RollSummary(combinedRollResults, combinedModifiers);
    }

}
