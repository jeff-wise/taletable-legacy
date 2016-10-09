
package com.kispoko.tome;



/**
 * Application Statistics
 *
 * Fetches data tracked in the server about template usage.
 */
public class Statistics
{


    public static int gamePlayers(String gameId)
    {
        switch (gameId)
        {
            case "dungeons_and_dragons_5":
                return 34893;
            case "dungeons_and_dragons_4":
                return 4507;
            case "pathfinder":
                return 21176;
            default:
                return 0;
        }
    }

}
