
package com.kispoko.tome.sheet;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
import java.util.Arrays;
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
public class Roleplay implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                  id;

    private CollectionValue<Page> pages;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay() { }


    public Roleplay(UUID id, List<Page> pages)
    {
        this.id = id;

        List<Class<? extends Page>> pageClasses = new ArrayList<>();
        pageClasses.add(Page.class);
        this.pages = new CollectionValue<>(pages, this, pageClasses);

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

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


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

}
