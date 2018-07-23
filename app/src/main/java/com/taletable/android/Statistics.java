
package com.taletable.android;



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


    public static int templatesCreated(String templateId)
    {
        switch (templateId)
        {
            case "official_dungeons_and_dragons_4_monk_level_1":
                return 345;
            case "official_dungeons_and_dragons_5_barbarian_level_1":
                return 7945;
            case "official_dungeons_and_dragons_5_bard_level_1":
                return 2455;
            case "official_dungeons_and_dragons_5_cleric_level_1":
                return 5612;
            case "official_dungeons_and_dragons_5_druid_level_1":
                return 3394;
            case "official_dungeons_and_dragons_5_fighter_level_1":
                return 12883;
            case "official_dungeons_and_dragons_5_monk_level_1":
                return 7711;
            case "official_dungeons_and_dragons_5_paladin_level_1":
                return 8014;
            case "official_dungeons_and_dragons_5_ranger_level_1":
                return 9778;
            case "official_dungeons_and_dragons_5_rogue_level_1":
                return 11034;
            case "official_dungeons_and_dragons_5_sorceror_level_1":
                return 6255;
            case "official_dungeons_and_dragons_5_warlock_level_1":
                return 4339;
            case "official_dungeons_and_dragons_5_wizard_level_1":
                return 10087;
            case "official_pathfinder_fighter_level_1":
                return 15478;
            default:
                return 0;
        }
    }

}
