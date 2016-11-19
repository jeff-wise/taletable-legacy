
package com.kispoko.tome.sheet;


import android.content.Context;
import android.os.AsyncTask;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.exception.TemplateFileException;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.error.TemplateFileReadError;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.util.model.Model;
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
public class Sheet implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                 id;

    private PrimitiveValue<Long> lastUsed;

    private ModelValue<Game> game;
    private ModelValue<Roleplay> roleplay;
    private ModelValue<Rules>    rules;

    private Map<UUID,Widget> componentById;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet(UUID id,
                 Game game,
                 Roleplay roleplay,
                 Rules rules)
    {
        this.id = id;

        Long currentTimeMS = System.currentTimeMillis();

        this.lastUsed = new PrimitiveValue<>(currentTimeMS, this, Long.class);

        this.game     = new ModelValue<>(game, this, Game.class);
        this.roleplay = new ModelValue<>(roleplay, this, Roleplay.class);
        this.rules    = new ModelValue<>(rules, this, Rules.class);

        indexComponents();
    }


    public static Sheet fromYaml(Yaml yaml)
                  throws YamlException
    {
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

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------


    public Widget componentWithId(UUID componentId)
    {
        return this.componentById.get(componentId);
    }


//    public WidgetData componentWithLabel(String componentLabel)
//    {
//        return this.componentByLabel.get(componentLabel.toLowerCase());
//    }


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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void indexComponents()
    {
        // Index components
        componentById = new HashMap<>();
//        componentByLabel = new HashMap<>();

        for (Page page : this.roleplay.getValue().getPages())
        {
            for (Group group : page.getGroups())
            {
                for (Widget widget : group.getWidgets())
                {
                    componentById.put(widget.getId(), widget);

//                    if (widgetData.hasLabel())
//                        componentByLabel.put(widgetData.getLabel().toLowerCase(), widgetData);
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


    // Listeners
    // ------------------------------------------------------------------------------------------

    public interface OnSheetListener {
        void onSheet(Sheet sheet);
    }



}
