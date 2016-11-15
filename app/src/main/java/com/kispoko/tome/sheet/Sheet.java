
package com.kispoko.tome.sheet;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.Global;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.exception.TemplateFileException;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.error.TemplateFileReadError;
import com.kispoko.tome.sheet.widget.WidgetData;
import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.database.ColumnProperties;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Character Sheet
 *
 * This class represents the structure and representation of character sheet. Character sheets
 * can therefore be customized for different roleplaying games or even different campaigns.
 */
public class Sheet extends Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<Long> lastUsed;

    private ModelValue<Game> game;
    private ModelValue<Roleplay> roleplay;
    private ModelValue<Rules>    rules;

    private Map<UUID,WidgetData> componentById;
    private Map<String,WidgetData> componentByLabel;


    // > Internal
    // ------------------------------------------------------------------------------------------

    final private static String modelName = "sheet";


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet(UUID id,
                 Game game,
                 Roleplay roleplay,
                 Rules rules)
    {
        super(id);

        // CREATE model values
        // --------------------------------------------------------------------------------------
        Long currentTimeMS = System.currentTimeMillis();

        this.lastUsed = new PrimitiveValue<>(currentTimeMS, this, Long.class,
                                             new ColumnProperties("last_used", null));

        this.game     = new ModelValue<>(game, this, Game.class);
        this.roleplay = new ModelValue<>(roleplay, this, Roleplay.class);
        this.rules    = new ModelValue<>(rules, this, Rules.class);

        indexComponents();
    }


    @SuppressWarnings("unchecked")
    public static Sheet fromYaml(Yaml yaml)
                  throws YamlException
    {
        // Values to parse
        UUID id = UUID.randomUUID();
        Game game = Game.fromYaml(yaml.atKey("game"));
        Roleplay roleplay = Roleplay.fromYaml(yaml.atKey("roleplay"));
        Rules rules = Rules.fromYaml(yaml.atKey("rules"));

        return new Sheet(id, game, roleplay, rules);
    }


    /**
     * Create a sheet from a sheet template file.
     * @param sheetListener The listener for the new sheet.
     * @param context The context object, for looking up the assets.
     * @param templateId The ID of the template yaml file to load.
     */
    public static void fromFile(final OnSheetListener sheetListener,
                                final Context context,
                                String templateId)
    {
        final String templateFileName = "template/" + templateId + ".yaml";

        new AsyncTask<Void,Void,Object>()
        {

            protected Object doInBackground(Void... args)
            {
                Sheet sheet;
                try {
                    InputStream yamlIS = context.getAssets().open(templateFileName);
                    Yaml yaml = Yaml.fromFile(yamlIS);
                    sheet = Sheet.fromYaml(yaml);
                } catch (IOException e) {
                    return new TemplateFileException(
                                new TemplateFileReadError(templateFileName),
                            TemplateFileException.ErrorType.TEMPLATE_FILE_READ);
                } catch (YamlException e) {
                    return e;
                }

                return sheet;
            }

            protected void onPostExecute(Object maybeSheet)
            {
                if (maybeSheet instanceof TemplateFileException) {
                    ApplicationFailure.templateFile((TemplateFileException) maybeSheet);
                }
                else if (maybeSheet instanceof YamlException) {
                    ApplicationFailure.yaml((YamlException) maybeSheet);
                }
                else if (maybeSheet instanceof Sheet) {
                    sheetListener.onSheet((Sheet) maybeSheet);
                }
            }

        }.execute();
    }



    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------


    public WidgetData componentWithId(UUID componentId)
    {
        return this.componentById.get(componentId);
    }


    public WidgetData componentWithLabel(String componentLabel)
    {
        return this.componentByLabel.get(componentLabel.toLowerCase());
    }


    public Roleplay getRoleplay()
    {
        return this.roleplay.getValue();
    }


    public Game getGame()
    {
        return this.game.getValue();
    }


    public Rules getRules()
    {
        return this.rules.getValue();
    }


    public static void loadMostRecent(final SheetActivity sheetActivity)
    {
        new AsyncTask<Void,Void,Sheet>()
        {

            protected Sheet doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                String mostRecentSheetIdQuery =
                    "SELECT sheet.sheet_id, sheet.game_id " +
                    "FROM sheet " +
                    "ORDER BY datetime(sheet.last_used) DESC " +
                    "LIMIT 1";

                Cursor cursor = database.rawQuery(mostRecentSheetIdQuery, null);

                UUID sheetId;
                String gameId;
                try {
                    cursor.moveToFirst();
                    sheetId = UUID.fromString(cursor.getString(0));
                    gameId  = cursor.getString(1);
                }
                finally {
                    cursor.close();
                }

                return new Sheet(sheetId, gameId);
            }

            protected void onPostExecute(Sheet sheet)
            {
                // Create an asynchronous Sheet constructor
                TrackerId sheetTrackerId = sheet.addAsyncTracker(sheetActivity, true);

                // Load the sheet components
                sheet.getGame().load(sheetTrackerId);
                sheet.getRoleplay().load(sheetTrackerId);
                sheet.getRules().load(sheetTrackerId);
            }

        }.execute();
    }

    // >>>> Queries
    // ------------------------------------------------------------------------------------------

    public static Integer count(SQLiteDatabase database)
    {
        String sheetCountQuery = "SELECT count(*) FROM Sheet";

        Cursor cursor = database.rawQuery(sheetCountQuery, null);

        Integer count;
        try {
            cursor.moveToFirst();
            count = cursor.getInt(0);
        }
        finally {
            cursor.close();
        }

        return count;
    }



    // >>> Files
    // ------------------------------------------------------------------------------------------



    // >> View Methods
    // ------------------------------------------------------------------------------------------


    // > INTERNAL
    // ------------------------------------------------------------------------------------------

    private void indexComponents()
    {
        // Index components
        componentById = new HashMap<>();
        componentByLabel = new HashMap<>();

        for (Page page : this.roleplay.getPages())
        {
            for (Group group : page.getGroups())
            {
                for (WidgetData widgetData : group.getWidgetDatas())
                {
                    componentById.put(widgetData.getId(), widgetData);

                    if (widgetData.hasLabel())
                        componentByLabel.put(widgetData.getLabel().toLowerCase(), widgetData);
                }
            }
        }

    }



    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------


    public static class Name
    {
        private String name;
        private String label;
        private String description;

        public Name(String name, String label, String description)
        {
            this.name = name;
            this.label = label;
            this.description = description;
        }

        public String getName()
        {
            return this.name;
        }

        public String getLabel()
        {
            return this.label;
        }

        public String getDescription()
        {
            return this.description;
        }

    }


    // > Listeners
    // ------------------------------------------------------------------------------------------

    public interface OnSheetListener {
        void onSheet(Sheet sheet);
    }



}
