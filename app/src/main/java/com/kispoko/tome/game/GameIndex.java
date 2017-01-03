
package com.kispoko.tome.game;


import android.content.Context;

import com.kispoko.tome.ApplicationAssets;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;



/**
 * Game Index
 */
public class GameIndex
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private List<Game> games;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public GameIndex() { }


    public GameIndex(List<Game> games)
    {
        this.games = games;
    }


    /**
     * Load the games from the manifest file which contains all of the officially support games.
     */
    public static GameIndex fromManifest(Context context)
                  throws IOException, YamlException
    {
        InputStream yamlIS = context.getAssets().open(ApplicationAssets.templateManifest);
        Yaml yaml = Yaml.fromFile(yamlIS);

        List<Game> games = yaml.atKey("games").forEach(new Yaml.ForEach<Game>() {
            @Override
            public Game forEach(Yaml yaml, int index) throws YamlException {
                return Game.fromYaml(yaml);
            }
        });

        return new GameIndex(games);
    }


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * Get the official known games.
     * @return An immutable list of Games.
     */
    public List<Game> games()
    {
        return Collections.unmodifiableList(this.games);
    }

}
