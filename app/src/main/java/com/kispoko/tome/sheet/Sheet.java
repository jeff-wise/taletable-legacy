
package com.kispoko.tome.sheet;


import java.util.Map;



/**
 * Character Sheet Format
 *
 * This class represents the structure and representation of character sheet. Character sheets
 * can therefore be customized for different roleplaying games or even different campaigns.
 */
public class Sheet
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;

    private Roleplay roleplay;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet(String name, Roleplay roleplay)
    {
        this.name = name;
        this.roleplay = roleplay;
    }


    @SuppressWarnings("unchecked")
    public static Sheet fromYaml(Map<String, Object> sheetYaml)
    {
        String name = (String) sheetYaml.get("name");

        Map<String,Object> sections = (Map<String,Object>) sheetYaml.get("sections");

        Map<String,Object> roleplayYaml = (Map<String,Object>) sections.get("roleplay");

        Roleplay roleplay = Roleplay.fromYaml(roleplayYaml);

        return new Sheet(name, roleplay);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Get the sheet's roleplay format.
     * @return The roleplay format.
     */
    public Roleplay getRoleplay()
    {
        return this.roleplay;
    }

}
