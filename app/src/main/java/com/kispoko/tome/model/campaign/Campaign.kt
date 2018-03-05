
package com.kispoko.tome.model.campaign


import com.kispoko.tome.db.DB_CampaignValue
import com.kispoko.tome.db.campaignTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.Engine
import effect.apply
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
                    val campaignId : CampaignId,
                    val engine : Engine,
                    val campaignName : CampaignName,
                    val campaignSummary : CampaignSummary,
                    val gameId : GameId)
                     : ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(campaignId : CampaignId,
                engine : Engine,
                campaignName : CampaignName,
                campaignSummary : CampaignSummary,
                gameId : GameId)
        : this(UUID.randomUUID(),
               campaignId,
               engine,
               campaignName,
               campaignSummary,
               gameId)


    companion object : Factory<Campaign>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Campaign> = when (doc)
        {
            is DocDict ->
            {
                apply(::Campaign,
                      // Campaign Id
                      doc.at("id") ap { CampaignId.fromDocument(it) },
                      // Engine
                      doc.at("engine") ap { Engine.fromDocument(it) },
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

    fun campaignId() : CampaignId = this.campaignId


    fun engine() : Engine = this.engine


    fun campaignName() : String = this.campaignName.value


    fun campaignSummary() : String = this.campaignSummary.value


    fun gameId() : GameId = this.gameId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_CampaignValue =
        RowValue5(campaignTable, PrimValue(campaignId),
                                 ProdValue(this.engine),
                                 PrimValue(campaignName),
                                 PrimValue(campaignSummary),
                                 PrimValue(gameId))

}



/**
 * Campaign Id
 */
data class CampaignId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CampaignId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<CampaignId> = when (doc)
        {
            is DocText -> effValue(CampaignId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

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
        override fun fromDocument(doc: SchemaDoc): ValueParser<CampaignName> = when (doc)
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
        override fun fromDocument(doc: SchemaDoc): ValueParser<CampaignSummary> = when (doc)
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
//    // > ProdType
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
//    // API > ProdType
//    // ------------------------------------------------------------------------------------------
//
//    // API > ProdType > Id
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
//    // API > ProdType > On Load
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
