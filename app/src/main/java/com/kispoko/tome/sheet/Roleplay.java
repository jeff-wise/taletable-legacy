
package com.kispoko.tome.sheet;


import java.util.Map;

/**
 * Character Sheet Format
 *
 * This class represents the structure and representation of character sheet. Character sheets
 * can therefore be customized for different roleplaying games or even different campaigns.
 */
public class Roleplay
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Profile profile;
//    private Stats      statsFormat;
//    private Abilities  abilitiesFormat;
//    private Backpack   backpackFormat;
//    private Spellbook  spellbookFormat;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay(Profile profile)
//                          Stats     statsFormat,
//                          Abilities abilitiesFormat,
//                          Backpack  backpackFormat,
//                          Spellbook spellbookFormat)
    {
        this.profile = profile;
//        this.statsFormat = statsFormat;
//        this.abilitiesFormat = abilitiesFormat;
//        this.backpackFormat = backpackFormat;
//        this.spellbookFormat = spellbookFormat;
    }


    @SuppressWarnings("unchecked")
    public static Roleplay fromYaml(Map<String, Object> roleplayFormatYaml)
    {
        Map<String,Object> profileFormatYaml =
                (Map<String,Object>) roleplayFormatYaml.get("profile");
        Profile profile = Profile.fromYaml(profileFormatYaml);

        return new Roleplay(profile);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the profile format belonging to this roleplay format.
     * @return The profile format.
     */
    public Profile getProfile()
    {
        return this.profile;
    }
}
