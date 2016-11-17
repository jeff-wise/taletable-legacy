
package com.kispoko.tome.sheet;


import com.kispoko.tome.util.model.Modeler;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;



/**
 * Roleplay Section
 *
 * The roleplay section of the character sheet contains all of the static information and stats
 * for a character.
 */
public class Roleplay extends Modeler
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private CollectionValue<Page> pages;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay(UUID id, List<Page> pages)
    {
        super(id);

        this.pages = new CollectionValue<>(pages, this, Page.class);

        // Make sure pages are sorted
        Collections.sort(pages, new Comparator<Page>() {
            @Override
            public int compare(Page page1, Page page2) {
                if (page1.getIndex() > page2.getIndex())
                    return 1;
                if (page1.getIndex() < page2.getIndex())
                    return -1;
                return 0;
            }
        });
    }


    public static Roleplay fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();
        List<Page> pages = yaml.atKey("pages").forEach(new Yaml.ForEach<Page>() {
            @Override
            public Page forEach(Yaml yaml, int index) throws YamlException {
                return Page.fromYaml(yaml, index);
            }
        });

        return new Roleplay(id, pages);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Pages
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the pages in the roleplay section.
     * @return The roleplay pages.
     */
    public List<Page> getPages()
    {
        return this.pages.getValue();
    }


    // > Modeler
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String fieldName) { }


}
