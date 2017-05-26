
package com.kispoko.tome.model.campaign;


import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.model.Model;

import java.io.Serializable;
import java.util.UUID;



/**
 * Campaign Engine
 *
 * Selects elements from the game engine that are available in the campaign.
 */
public class CampaignEngine extends Model
                            implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<EngineElementSelector> valueSetSelector;
    private ModelFunctor<EngineElementSelector> functionSelector;
    private ModelFunctor<EngineElementSelector> programSelector;
    private ModelFunctor<EngineElementSelector> mechanicSelector;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public CampaignEngine()
    {
        this.id                 = null;

        this.valueSetSelector   = ModelFunctor.empty(EngineElementSelector.class);
        this.functionSelector   = ModelFunctor.empty(EngineElementSelector.class);
        this.programSelector    = ModelFunctor.empty(EngineElementSelector.class);
        this.mechanicSelector   = ModelFunctor.empty(EngineElementSelector.class);
    }


    public CampaignEngine(UUID id,
                          EngineElementSelector valueSetSelector,
                          EngineElementSelector functionSelector,
                          EngineElementSelector programSelector,
                          EngineElementSelector mechanicSelector)
    {
        this.id             = id;

        this.valueSetSelector   = ModelFunctor.full(valueSetSelector, EngineElementSelector.class);
        this.functionSelector   = ModelFunctor.full(functionSelector, EngineElementSelector.class);
        this.programSelector    = ModelFunctor.full(programSelector, EngineElementSelector.class);
        this.mechanicSelector   = ModelFunctor.full(mechanicSelector, EngineElementSelector.class);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    // ** Id
    // -----------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // -----------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }



}
