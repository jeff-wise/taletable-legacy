
package com.kispoko.tome.model.campaign;


/**
 * Campaign Description
 */
//public class CampaignDescription
//                                 implements Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                            id;
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>        summary;
//    private PrimitiveFunctor<String>        description;
//
//    private CollectionFunctor<GameMaster>   gameMasters;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public CampaignDescription()
//    {
//        this.id             = null;
//
//        this.summary        = new PrimitiveFunctor<>(null, String.class);
//        this.description    = new PrimitiveFunctor<>(null, String.class);
//
//        this.gameMasters    = CollectionFunctor.empty(GameMaster.class);
//    }
//
//
//    public CampaignDescription(UUID id,
//                               String summary,
//                               String description,
//                               List<GameMaster> gameMasters)
//    {
//        this.id             = id;
//
//        this.summary        = new PrimitiveFunctor<>(summary, String.class);
//        this.description    = new PrimitiveFunctor<>(description, String.class);
//
//        this.gameMasters    = CollectionFunctor.full(gameMasters, GameMaster.class);
//    }
//
//
//    // API
//    // -----------------------------------------------------------------------------------------
//
//    // API > Model
//    // -----------------------------------------------------------------------------------------
//
//    // API > Model > Id
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * Get the model identifier.
//     * @return The model UUID.
//     */
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    /**
//     * Set the model identifier.
//     * @param id The new model UUID.
//     */
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // API > Model > On Load
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the RulesEngine is completely loaded for the first time.
//     */
//    public void onLoad() { }
//
//
//    // API > State
//    // -----------------------------------------------------------------------------------------
//
//    // API > State > Summary
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * A short description of the campaign.
//     * @return The campaign summary
//     */
//    public String summary()
//    {
//        return this.summary.getValue();
//    }
//
//
//    /**
//     * Set the campaign summary.
//     * @param summary The summary.
//     */
//    public void setSummary(String summary)
//    {
//        if (summary != null)
//            this.summary.setValue(summary);
//        else
//            this.summary.setValue("");
//    }
//
//
//    // API > State > Description
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The full description of the camapaign. Defaults to empty string if null.
//     * @return The campaign description.
//     */
//    public String description()
//    {
//        return this.description.getValue();
//    }
//
//
//    /**
//     * Set the full campaign description. Defaults to empty string if null.
//     * @param description The description.
//     */
//    public void setDescription(String description)
//    {
//        if (description != null)
//            this.description.setValue(description);
//        else
//            this.description.setValue("");
//    }
//
//
//    // API > State > Game Masters
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The campaign game masters.
//     * @return The campaign game masters.
//     */
//    public List<GameMaster> gameMasters()
//    {
//        return this.gameMasters.getValue();
//    }
//}
