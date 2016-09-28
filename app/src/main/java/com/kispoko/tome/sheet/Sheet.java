
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

    private Roleplay roleplayFormat;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet(Roleplay roleplayFormat)
    {
        this.roleplayFormat = roleplayFormat;
    }


    @SuppressWarnings("unchecked")
    public static Sheet fromYaml(Map<String, Object> sheetFormatYaml)
    {
        // TODO how to do logging
        Map<String,Object> roleplayFormatYaml =
                (Map<String,Object>) sheetFormatYaml.get("roleplay");
        Roleplay roleplayFormat = Roleplay.fromYaml(roleplayFormatYaml);

        return new Sheet(roleplayFormat);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Get the sheet's roleplay format.
     * @return The roleplay format.
     */
    public Roleplay getRoleplayFormat()
    {
        return this.roleplayFormat;
    }

}
