
package com.kispoko.tome.sheet;


import java.util.ArrayList;
import java.util.List;
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

    private ArrayList<Page> pages;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay(ArrayList<Page> pages)
    {
        this.pages = pages;
    }


    @SuppressWarnings("unchecked")
    public static Roleplay fromYaml(Map<String, Object> roleplayYaml)
    {
        // Roleplay pages
        ArrayList<Map<String,Object>> pagesYaml =
                (ArrayList<Map<String,Object>>) roleplayYaml.get("pages");
        ArrayList<Page> pages = new ArrayList<>();

        for (Map<String,Object> pageYaml : pagesYaml)
        {
            Page page = Page.fromYaml(pageYaml);
            pages.add(page);
        }

        return new Roleplay(pages);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the pages in the roleplay section.
     * @return The roleplay pages.
     */
    public List<Page> getPages()
    {
        return this.pages;
    }
}
