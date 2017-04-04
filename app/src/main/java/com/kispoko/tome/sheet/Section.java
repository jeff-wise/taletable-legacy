
package com.kispoko.tome.sheet;


import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.Functor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;



/**
 * Section
 *
 * A section is a collection of pages. It represents a certain aspect to a sheet, such as static
 * content, dynamic content, campaign information, etc...
 */
public class Section extends Model
                     implements ToYaml
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<SectionType>   type;
    private CollectionFunctor<Page>         pages;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Section()
    {
        this.id     = null;

        this.type   = new PrimitiveFunctor<>(null, SectionType.class);

        this.pages  = CollectionFunctor.empty(Page.class);
    }


    public Section(UUID id, SectionType sectionType, List<Page> pages)
    {
        this.id     = id;

        this.type   = new PrimitiveFunctor<>(sectionType, SectionType.class);

        this.pages  = CollectionFunctor.full(pages, Page.class);

        this.initializeSection();
    }


    /**
     * A Campaign Section.
     * @param id The model id.
     * @param pages The campaign pages.
     * @return The Campaign Section.
     */
    public static Section asCampaign(UUID id, List<Page> pages)
    {
        return new Section(id, SectionType.CAMPAIGN, pages);
    }


    public static Section fromYaml(SectionType sectionType, YamlParser yaml)
                  throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        // TODO make this not true and catch exceptions when it's empty
        List<Page> pages = yaml.atKey("pages").forEach(new YamlParser.ForEach<Page>() {
            @Override
            public Page forEach(YamlParser yaml, int index) throws YamlParseException {
                return Page.fromYaml(yaml, index);
            }
        }, true);

        return new Section(id, sectionType, pages);
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


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Roleplay is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeSection();
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Section's yaml representation.
     * @return The Yaml Builder
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putList("pages", this.pages());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * The section type.
     * @return The section type.
     */
    public SectionType type()
    {
        return this.type.getValue();
    }


    // ** Pages
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the pages in the roleplay section.
     * @return The roleplay pages.
     */
    public List<Page> pages()
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
        pagePagerAdapter.setPages(this.pages());
        pagePagerAdapter.notifyDataSetChanged();
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the section.
     */
    public void initialize()
    {
        // Initialize the pages
        for (Page page : this.pages()) {
            page.initialize();
        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the section state.
     */
    private void initializeSection()
    {
        // [1] Sort the pages
        // --------------------------------------------------------------------------------------

        sortPages();

        // [2] Add an update listener on pages to ensure that they are always sorted
        // --------------------------------------------------------------------------------------

         this.pages.setOnUpdateListener(new Functor.OnUpdateListener() {
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
                if (page1.index() > page2.index())
                    return 1;
                if (page1.index() < page2.index())
                    return -1;
                return 0;
            }
        });

    }


}
