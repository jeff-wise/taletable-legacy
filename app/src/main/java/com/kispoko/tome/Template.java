
package com.kispoko.tome;


import android.content.Context;

import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.error.TemplateFileReadError;
import com.kispoko.tome.util.yaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Template
 */
public class Template
{



    @SuppressWarnings("unchecked")
    public static Map<String, ArrayList<Name>> templateNamesByGame(Context context)
    {
        Map<String, ArrayList<Name>> namesByGame = new HashMap<>();

        try {
            InputStream yamlIS = context.getAssets().open(ApplicationAssets.templateManifest);
            Yaml yaml = Yaml.fromFile(yamlIS);
            sheet = Sheet.fromYaml(yaml);
        } catch (IOException e) {
            return new TemplateFileReadError(
                new TemplateFileReadError.ProblemReadingFile(ApplicationAssets.templateManifest),
                TemplateFileReadError.Type.PROBLEM_READING_FILE);
        }


        try
        {

            InputStream yamlIS = context.getAssets().open("template/manifest.yaml");
            Yaml yaml = new Yaml();
            Map<String,Object> yamlObject = (Map<String,Object>) yaml.load(yamlIS);

            ArrayList<Map<String,Object>> templatesYaml =
                    (ArrayList<Map<String,Object>>) yamlObject.get("templates");

            for (Map<String,Object> templateYaml : templatesYaml)
            {
                String gameId = (String) templateYaml.get("game");

                Map<String,Object> templateDataYaml =
                        (Map<String,Object>) templateYaml.get("template");

                String id = (String) templateDataYaml.get("id");
                String label = (String) templateDataYaml.get("label");
                String description = (String) templateDataYaml.get("description");

                if (!namesByGame.containsKey(gameId))
                    namesByGame.put(gameId, new ArrayList<Name>());

                ArrayList<Name> names = namesByGame.get(gameId);
                names.add(new Name(id, label, description));
            }
        }
        catch (IOException e)
        {
            // TODO
        }

        return namesByGame;
    }


    /**
     * Read the template manifest file and retrive the list of games available.
     * @param context Context for accessing assets.
     * @return ListType of template games.
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Game> templateGames(Context context)
    {
        ArrayList<Game> games = new ArrayList<>();

        try
        {
            InputStream yamlIS = context.getAssets().open("template/manifest.yaml");
            Yaml yaml = new Yaml();
            Map<String,Object> yamlObject = (Map<String,Object>) yaml.load(yamlIS);

            ArrayList<Map<String,Object>> gamesYaml =
                    (ArrayList<Map<String,Object>>) yamlObject.get("games");
            for (Map<String,Object> gameYaml : gamesYaml)
            {
                String id  = (String) gameYaml.get("id");
                String label = (String) gameYaml.get("label");
                String description = (String) gameYaml.get("description");
                games.add(new Game(id, label, description));
            }
        }
        catch (IOException e)
        {
            // TODO
        }

        return games;
    }


    public static String officialTemplateId(String gameId, String templateName)
    {
        return "official_" + gameId + "_" + templateName;
    }



}
