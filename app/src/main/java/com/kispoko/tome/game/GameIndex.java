
package com.kispoko.tome.game;


import android.content.Context;

import com.kispoko.tome.ApplicationAssets;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

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
                  throws IOException, YamlParseException
    {
        InputStream yamlIS = context.getAssets().open(ApplicationAssets.templateManifest);
        YamlParser yaml = YamlParser.fromFile(yamlIS);

        List<Game> games = yaml.atKey("games").forEach(new YamlParser.ForEach<Game>() {
            @Override
            public Game forEach(YamlParser yaml, int index) throws YamlParseException {
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
