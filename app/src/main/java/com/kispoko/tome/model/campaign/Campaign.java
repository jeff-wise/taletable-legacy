
package com.kispoko.tome.model.campaign;


import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Campaign
 */
//public class Campaign extends Model
//                      implements Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                                id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>            label;
//    private ModelFunctor<CampaignDescription>   description;
//    private ModelFunctor<Universe>              setting;
//
//    private PrimitiveFunctor<UUID[]>            playerIds;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public Campaign()
//    {
//        this.id             = null;
//
//        this.label          = new PrimitiveFunctor<>(null, String.class);
//        this.description    = ModelFunctor.empty(CampaignDescription.class);
//        this.setting        = ModelFunctor.empty(Universe.class);
//
//        this.playerIds      = new PrimitiveFunctor<>(null, UUID[].class);
//    }
//
//
//    public Campaign(UUID id,
//                    String label,
//                    CampaignDescription description,
//                    Universe setting,
//                    List<UUID> playerIds)
//    {
//        this.id             = id;
//
//        this.label          = new PrimitiveFunctor<>(label, String.class);
//        this.description    = ModelFunctor.full(description, CampaignDescription.class);
//        this.setting        = ModelFunctor.full(setting, Universe.class);
//
//        UUID[] playerIdArray = playerIds.toArray(new UUID[playerIds.size()]);
//        this.playerIds      = new PrimitiveFunctor<>(playerIdArray, UUID[].class);
//    }
//
//
//    // API
//    // -----------------------------------------------------------------------------------------
//
//    // API > Model
//    // ------------------------------------------------------------------------------------------
//
//    // API > Model > Id
//    // ------------------------------------------------------------------------------------------
//
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // API > Model > On Load
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the Sheet is completely loaded for the first time.
//     */
//    public void onLoad() { }
//
//
//    // API > State
//    // -----------------------------------------------------------------------------------------
//
//    // API > State > Label
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The campaign name in a format that is pleasant to read.
//     * @return The campaign name.
//     */
//    public String label()
//    {
//        return this.label.getValue();
//    }
//
//
//    // API > State > Description
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * All of the data that describes the campaign.
//     * @return The campaign description.
//     */
//    public CampaignDescription description()
//    {
//        return this.description.getValue();
//    }
//
//
//    // API > State > Description
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The campaign setting.
//     * @return The campaign setting.
//     */
//    public Universe setting()
//    {
//        return this.setting.getValue();
//    }
//
//
//    // API > State > Player Ids
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The IDs of the players in the campaign.
//     * @return The player ids.
//     */
//    public List<UUID> playerIds()
//    {
//        return Arrays.asList(this.playerIds.getValue());
//    }
//}
