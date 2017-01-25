
package com.kispoko.tome.campaign;


import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.query.CountQuery;
import com.kispoko.tome.util.value.CollectionFunctor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Campaign Index
 */
public class CampaignIndex
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Functors
    // -----------------------------------------------------------------------------------------

    private static CollectionFunctor<Campaign> campaigns;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private static Map<String,Campaign> campaignByName;


    // API
    // -----------------------------------------------------------------------------------------

    public static void initialize()
    {
        CountQuery.OnCountListener onCountListener = new CountQuery.OnCountListener()
        {
            @Override
            public void onCountResult(String tableName, Integer count)
            {
                if (count == 0)
                    initializeFromYaml();
                else
                    initializeFromDB();
            }

            @Override
            public void onCountError(DatabaseException exception)
            {
                // TODO could not initialize campaign index error
            }
        };

        CountQuery.fromModel(Sheet.class)
                  .run(onCountListener);
    }


    private static void initializeFromDB()
    {
        campaigns.setOnLoadListener(new CollectionFunctor.OnLoadListener<Campaign>()
        {
            @Override
            public void onLoad(List<Campaign> value)
            {
                initializeCampaigns();
            }

            @Override
            public void onLoadDBError(DatabaseException exception)
            {
                // TODO
            }

            @Override
            public void onLoadError(Exception exception)
            {
                // TODO
            }
        });

        campaigns.load();
    }


    private static void initializeFromYaml()
    {

    }


    /**
     * All of the campaigns.
     * @return The campaign list.
     */
    public static List<Campaign> campaigns()
    {
        return campaigns.getValue();
    }


    /**
     * Get the campaign with the given name. Returns null if no campaign with that name exists.
     * @param campaignName The campaign name.
     * @return The campaign.
     */
    public static Campaign campaignWithName(String campaignName)
    {
        return campaignByName.get(campaignName);
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    private static void initializeCampaigns()
    {
        campaignByName = new HashMap<>();

        for (Campaign campaign : campaigns()) {
            campaignByName.put(campaign.name(), campaign);
        }
    }


}
