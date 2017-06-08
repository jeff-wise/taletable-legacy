
package com.kispoko.tome.official.game;


import android.content.Context;

import com.kispoko.tome.ApplicationAssets;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;



/**
 * GameSummary Index
 */
public class GameSummaryIndex
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private List<GameSummary> games;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public GameSummaryIndex() { }


    public GameSummaryIndex(List<GameSummary> games)
    {
        this.games = games;
    }


    /**
     * Load the games from the manifest file which contains all of the officially support games.
     */
    public static GameSummaryIndex fromManifest(Context context)
                  throws IOException, YamlParseException
    {
        InputStream yamlIS = context.getAssets().open(ApplicationAssets.officialManifest);
        YamlParser yaml = YamlParser.fromFile(yamlIS);

        List<GameSummary> games = yaml.atKey("games").forEach(new YamlParser.ForEach<GameSummary>() {
            @Override
            public GameSummary forEach(YamlParser yaml, int index) throws YamlParseException {
                return GameSummary.fromYaml(yaml);
            }
        });

        return new GameSummaryIndex(games);
    }


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * Get the official known games.
     * @return An immutable list of Games.
     */
    public List<GameSummary> games()
    {
        return Collections.unmodifiableList(this.games);
    }

}
