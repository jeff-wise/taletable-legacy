
package com.kispoko.tome.campaign;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.TemplateFileReadError;
import com.kispoko.tome.exception.TemplateFileException;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.query.CountQuery;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.IOException;
import java.io.InputStream;
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

    public static void initialize(final Context context)
    {
        CountQuery.OnCountListener onCountListener = new CountQuery.OnCountListener()
        {
            @Override
            public void onCountResult(String tableName, Integer count)
            {
                if (count == 0)
                    initializeFromYaml(context);
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
        campaigns = CollectionFunctor.empty(Campaign.class);

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

        // campaigns.load();
    }


    private static void initializeFromYaml(final Context context)
    {
        final String campaignsFileName = "template/campaigns.yaml";

        new AsyncTask<Void,Void,Object>()
        {

            protected Object doInBackground(Void... args)
            {

                try
                {
                    InputStream yamlIS = context.getAssets().open(campaignsFileName);
                    YamlParser yaml = YamlParser.fromFile(yamlIS);

                    List<Campaign> yamlCampaigns;

                    yamlCampaigns = yaml.atKey("campaigns").forEach(new YamlParser.ForEach<Campaign>() {
                        @Override
                        public Campaign forEach(YamlParser yaml, int index)
                               throws YamlParseException {
                            return Campaign.fromYaml(yaml);
                        }
                    }, true);

                    return yamlCampaigns;
                }
                catch (YamlParseException exception)
                {
                    return exception;
                }
                catch (IOException exception)
                {
                    // TODO read file errors better
                    return new TemplateFileException(
                                new TemplateFileReadError(campaignsFileName),
                            TemplateFileException.ErrorType.TEMPLATE_FILE_READ);
                }
                catch (Exception exception)
                {
                    return exception;
                }
            }

            @SuppressWarnings("unchecked")
            protected void onPostExecute(Object maybeCampaignList)
            {
                if (maybeCampaignList instanceof TemplateFileException)
                {
                    ApplicationFailure.templateFile((TemplateFileException) maybeCampaignList);
                }
                else if (maybeCampaignList instanceof YamlParseException)
                {
                    ApplicationFailure.yaml((YamlParseException) maybeCampaignList);
                }
                else if (maybeCampaignList instanceof Exception)
                {
                    Log.d("***CAMPAIGNINDEX", "template load exception",
                            (Exception) maybeCampaignList);
                }
                else if (maybeCampaignList instanceof List<?>)
                {
                    List<Campaign> campaignList = (List<Campaign>) maybeCampaignList;

                    campaigns = CollectionFunctor.full(campaignList, Campaign.class);

                    initializeCampaigns();

                    CollectionFunctor.OnSaveListener onSaveListener =
                                                new CollectionFunctor.OnSaveListener()
                    {
                        @Override
                        public void onSave()
                        {
                        }

                        @Override
                        public void onSaveDBError(DatabaseException exception)
                        {
                            ApplicationFailure.database(exception);
                        }

                        @Override
                        public void onSaveError(Exception exception)
                        {
                        }
                    };

                    campaigns.setOnSaveListener(onSaveListener);

                    campaigns.saveAsync();
                }
            }

        }.execute();


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

        SheetManager.campaignIndexReady();
    }


}
