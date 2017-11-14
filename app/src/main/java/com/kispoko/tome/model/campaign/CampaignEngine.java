
package com.kispoko.tome.model.campaign;


/**
 * Campaign Engine
 *
 * Selects elements from the game engine that are available in the campaign.
 */
//public class CampaignEngine
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > ProdType
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                                id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private ModelFunctor<EngineElementSelector> valueSetSelector;
//    private ModelFunctor<EngineElementSelector> functionSelector;
//    private ModelFunctor<EngineElementSelector> programSelector;
//    private ModelFunctor<EngineElementSelector> mechanicSelector;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public CampaignEngine()
//    {
//        this.id                 = null;
//
//        this.valueSetSelector   = ModelFunctor.empty(EngineElementSelector.class);
//        this.functionSelector   = ModelFunctor.empty(EngineElementSelector.class);
//        this.programSelector    = ModelFunctor.empty(EngineElementSelector.class);
//        this.mechanicSelector   = ModelFunctor.empty(EngineElementSelector.class);
//    }
//
//
//    public CampaignEngine(UUID id,
//                          EngineElementSelector valueSetSelector,
//                          EngineElementSelector functionSelector,
//                          EngineElementSelector programSelector,
//                          EngineElementSelector mechanicSelector)
//    {
//        this.id             = id;
//
//        this.valueSetSelector   = ModelFunctor.full(valueSetSelector, EngineElementSelector.class);
//        this.functionSelector   = ModelFunctor.full(functionSelector, EngineElementSelector.class);
//        this.programSelector    = ModelFunctor.full(programSelector, EngineElementSelector.class);
//        this.mechanicSelector   = ModelFunctor.full(mechanicSelector, EngineElementSelector.class);
//    }
//
//
//    // API
//    // -----------------------------------------------------------------------------------------
//
//    // > ProdType
//    // -----------------------------------------------------------------------------------------
//
//    // ** Id
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * Get the prodType identifier.
//     * @return The prodType UUID.
//     */
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    /**
//     * Set the prodType identifier.
//     * @param id The new prodType UUID.
//     */
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the RulesEngine is completely loaded for the first time.
//     */
//    public void onLoad() { }
//
//
//
//}
