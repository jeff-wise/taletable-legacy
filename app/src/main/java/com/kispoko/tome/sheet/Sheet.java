
package com.kispoko.tome.sheet;


import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
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
        // Sheet name
        String name = (String) sheetYaml.get("name");

        // Types
        ArrayList<Map<String,Object>> typesYaml =
                (ArrayList<Map<String,Object>>) sheetYaml.get("types");
        ArrayList<Type> types = new ArrayList<>();
        for (Map<String,Object> typeYaml : typesYaml)
        {
            Type typ = Type.fromYaml(typeYaml);
            types.add(typ);
        }
        RulesEngine.addTypes(types);

        // Sheet sections
        Map<String,Object> sections = (Map<String,Object>) sheetYaml.get("sections");

        // Roleplay section
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
