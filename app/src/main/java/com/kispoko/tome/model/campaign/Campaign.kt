
package com.kispoko.tome.model.campaign


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.GameId
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Campaign
 */
data class Campaign(override val id : UUID,
                    val campaignId : Prim<CampaignId>,
                    val campaignName : Prim<CampaignName>,
                    val campaignSummary : Prim<CampaignSummary>,
                    val gameId : Prim<GameId>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.campaignId.name        = "campaign_id"
        this.campaignName.name      = "campaign_name"
        this.campaignSummary.name   = "campaign_summary"
        this.gameId.name            = "game_id"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(campaignId : CampaignId,
                campaignName : CampaignName,
                campaignSummary : CampaignSummary,
                gameId : GameId)
        : this(UUID.randomUUID(),
               Prim(campaignId),
               Prim(campaignName),
               Prim(campaignSummary),
               Prim(gameId))


    companion object : Factory<Campaign>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Campaign> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Campaign,
                         // Campaign Id
                         doc.at("id") ap { CampaignId.fromDocument(it) },
                         // Campaign Name
                         doc.at("campaign_name") ap { CampaignName.fromDocument(it) },
                         // Campaign Summary
                         doc.at("campaign_summary") ap { CampaignSummary.fromDocument(it) },
                         // Game Id
                         doc.at("game_id") ap { GameId.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun campaignId() : CampaignId = this.campaignId.value

    fun campaignName() : String = this.campaignName.value.value

    fun campaignSummary() : String = this.campaignSummary.value.value

    fun gameId() : GameId = this.gameId.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "campaign"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------
}



/**
 * Campaign Id
 */
data class CampaignId(val value : String) : SQLSerializable, Serializable
{

    companion object : Factory<CampaignId>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<CampaignId> = when (doc)
        {
            is DocText -> effValue(CampaignId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Campaign Name
 */
data class CampaignName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CampaignName>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<CampaignName> = when (doc)
        {
            is DocText -> effValue(CampaignName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText( {this.value} )

}


/**
 * Campaign Summary
 */
data class CampaignSummary(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CampaignSummary>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<CampaignSummary> = when (doc)
        {
            is DocText -> effValue(CampaignSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText( {this.value} )

}




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
