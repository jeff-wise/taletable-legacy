
package com.kispoko.tome.sheet;


import android.util.Log;

import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.Value;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
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


    // > Values
    // ------------------------------------------------------------------------------------------

    private CollectionValue<Page> pages;



    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay()
    {
        this.id = null;

        // ** Configure Pages Value
        List<Class<? extends Page>> pageClasses = new ArrayList<>();
        pageClasses.add(Page.class);

        this.pages = CollectionValue.empty(pageClasses);

        this.initialize();
    }


    public Roleplay(UUID id, List<Page> pages)
    {
        this.id = id;

        // ** Configure Pages Value
        List<Class<? extends Page>> pageClasses = new ArrayList<>();
        pageClasses.add(Page.class);

        this.pages = CollectionValue.full(pages, pageClasses);

        this.initialize();
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


    // > Render
    // ------------------------------------------------------------------------------------------

    /**
     * Render the roleplay.
     * @param pagePagerAdapter Render needs the pager adapter view so that the roleplay can update
     *                         the view when the pages change.
     */
    public void render(PagePagerAdapter pagePagerAdapter)
    {
        Log.d("***ROLEPLAY", "pages: " + Integer.toString(this.getPages().size()));
        pagePagerAdapter.setPages(this.getPages());
        pagePagerAdapter.notifyDataSetChanged();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        sortPages();

        this.pages.setOnUpdateListener(new Value.OnUpdateListener() {
            @Override
            public void onUpdate() {
                sortPages();
            }
        });
    }

    /**
     * Sort the pages by their index value, so they are displayed in the intended order.
     */
    private void sortPages()
    {
        if (this.pages.isNull())
            return;

        Collections.sort(this.pages.getValue(), new Comparator<Page>() {
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


}
