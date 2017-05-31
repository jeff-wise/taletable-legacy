
package com.kispoko.tome.model.sheet.section


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.page.Page
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Section
 */
data class Section(override val id : UUID,
                   val name : Func<SectionName>,
                   val pages : Coll<Page>) : Model
{
    companion object : Factory<Section>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Section> = when (doc)
        {
            is DocDict -> effApply(::Section,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Campaign Name
                                   doc.at("name") ap {
                                       effApply(::Prim, SectionName.fromDocument(it))
                                   },
                                   // Page List
                                   doc.list("pages") ap { docList ->
                                       effApply(::Coll, docList.mapIndexed {
                                           doc, index -> Page.fromDocument(doc, index) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // ON ACTIVE
    // -----------------------------------------------------------------------------------------

    fun onActive()
    {
        this.pages.list.forEach { it.onActive() }
    }
}


/**
 * Section Name
 */
data class SectionName(val name : String)
{

    companion object : Factory<SectionName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<SectionName> = when (doc)
        {
            is DocText -> effValue(SectionName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


//public class Section extends Model
//                     implements ToYaml
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    private UUID                        id;
//
//
//    // > Functors
//    // ------------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>    name;
//    private CollectionFunctor<Page>     pages;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public Section()
//    {
//        this.id     = null;
//
//        this.name   = new PrimitiveFunctor<>(null, String.class);
//
//        this.pages  = CollectionFunctor.empty(Page.class);
//    }
//
//
//    public Section(UUID id, String name, List<Page> pages)
//    {
//        this.id     = id;
//
//        this.name   = new PrimitiveFunctor<>(name, String.class);
//
//        this.pages  = CollectionFunctor.full(pages, Page.class);
//
//        this.initializeSection();
//    }
//
//
////    public static Section asCampaign(UUID id, List<Page> pages)
////    {
////        return new Section(id, SectionType.CAMPAIGN, pages);
////    }
//
//
//    public static Section fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        UUID id = UUID.randomUUID();
//
//        // TODO make this not true and catch exceptions when it's empty
//        List<Page> pages = yaml.atKey("pages").forEach(new YamlParser.ForEach<Page>() {
//            @Override
//            public Page forEach(YamlParser yaml, int index) throws YamlParseException {
//                return Page.fromYaml(yaml, index);
//            }
//        }, true);
//
//        return new Section(id, "section name", pages);
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    // ** Id
//    // ------------------------------------------------------------------------------------------
//
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the Roleplay is completely loaded for the first time.
//     */
//    public void onLoad()
//    {
//        this.initializeSection();
//    }
//
//
//    // > Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Section's yaml representation.
//     * @return The Yaml Builder
//     */
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putList("pages", this.pages());
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//
//    // ** Pages
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Returns the pages in the roleplay section.
//     * @return The roleplay pages.
//     */
//    public List<Page> pages()
//    {
//        return this.pages.getValue();
//    }
//
//
//    // > Render
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Render the roleplay.
//     * @param pagePagerAdapter Render needs the pager adapter view so that the roleplay can update
//     *                         the view when the pages change.
//     */
//    public void render(PagePagerAdapter pagePagerAdapter)
//    {
//        pagePagerAdapter.setPages(this.pages());
//        pagePagerAdapter.notifyDataSetChanged();
//    }
//
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the section.
//     */
//    public void initialize(Context context)
//    {
//        // Initialize the pages
//        for (Page page : this.pages()) {
//            page.initialize(context);
//        }
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the section state.
//     */
//    private void initializeSection()
//    {
//        // [1] Sort the pages
//        // --------------------------------------------------------------------------------------
//
//        sortPages();
//
//        // [2] Add an update listener on pages to ensure that they are always sorted
//        // --------------------------------------------------------------------------------------
//
//         this.pages.setOnUpdateListener(new Functor.OnUpdateListener() {
//            @Override
//            public void onUpdate() {
//                sortPages();
//            }
//        });
//    }
//
//
//    /**
//     * Sort the pages by their index value, so they are displayed in the intended order.
//     */
//    private void sortPages()
//    {
//        if (this.pages.isNull())
//            return;
//
//        Collections.sort(this.pages.getValue(), new Comparator<Page>() {
//            @Override
//            public int compare(Page page1, Page page2) {
//                if (page1.index() > page2.index())
//                    return 1;
//                if (page1.index() < page2.index())
//                    return -1;
//                return 0;
//            }
//        });
//
//    }
//
//
//}
