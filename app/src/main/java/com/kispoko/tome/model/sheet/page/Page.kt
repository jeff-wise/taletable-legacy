
package com.kispoko.tome.model.sheet.page


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.rts.sheet.State
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Page
 */
data class Page(override val id : UUID,
                val name : Func<PageName>,
                val format : Func<PageFormat>,
                val index : Func<Int>,
                val groups : Coll<Group>) : Model
{
    companion object
    {
        fun fromDocument(doc : SpecDoc, index : Int) : ValueParser<Page> = when (doc)
        {
            is DocDict -> effApply(::Page,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Name
                                   doc.at("name") ap {
                                       effApply(::Prim, PageName.fromDocument(it))
                                   },
                                   // Format
                                   split(doc.maybeAt("format"),
                                         nullEff<PageFormat>(),
                                         { effApply(::Comp, PageFormat.fromDocument(it)) }),
                                   // Index
                                   effValue(Prim(index)),
                                   // Groups
                                   doc.list("groups") ap { docList ->
                                       effApply(::Coll, docList.mapIndexed {
                                           doc, index -> Group.fromDocument(doc,index) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // ON ACTIVE
    // -----------------------------------------------------------------------------------------

    fun onActive(state : State)
    {
        this.groups.list.forEach { it.onActive(state) }
    }

}


/**
 * Page Name
 */
data class PageName(val name : String)
{

    companion object : Factory<PageName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<PageName> = when (doc)
        {
            is DocText -> effValue(PageName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Page Format
 */
data class PageFormat(override val id : UUID,
                      val backgroundColor : Func<ColorId>) : Model
{
    companion object : Factory<PageFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<PageFormat> = when (doc)
        {
            is DocDict -> effApply(::PageFormat,
                                   // Model Id
                                    effValue(UUID.randomUUID()),
                                   // Background Color
                                   doc.at("background_color") ap {
                                       effApply(::Prim, ColorId.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}

//
//public class Page extends Model
//                  implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    private UUID                                modelId;
//
//    private PrimitiveFunctor<String>            pageId;
//    private ModelFunctor<PageFormat>            format;
//    private PrimitiveFunctor<Integer>           index;
//    private CollectionFunctor<Group>            groups;
//
//
//    // > Internal
//    // ------------------------------------------------------------------------------------------
//
//    private int                                 pageViewId;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public Page()
//    {
//        this.modelId        = null;
//
//        this.pageId         = new PrimitiveFunctor<>(null, String.class);
//        this.format         = ModelFunctor.empty(PageFormat.class);
//        this.index          = new PrimitiveFunctor<>(null, Integer.class);
//
//        this.groups         = CollectionFunctor.empty(Group.class);
//    }
//
//
//    public Page(UUID modelId,
//                String pageId,
//                PageFormat format,
//                Integer index,
//                List<Group> groups)
//    {
//        this.modelId = modelId;
//
//        this.pageId = new PrimitiveFunctor<>(pageId, String.class);
//        this.format     = ModelFunctor.full(format, PageFormat.class);
//        this.index          = new PrimitiveFunctor<>(index, Integer.class);
//
//        this.groups         = CollectionFunctor.full(groups, Group.class);
//
//        this.initializePage();
//    }
//
//
//    /**
//     * Create a Page from its yaml representation.
//     * @param yaml The yaml parser.
//     * @param pageIndex The page index.
//     * @return The parsed Page.
//     * @throws YamlParseException
//     */
//    public static Page fromYaml(YamlParser yaml, int pageIndex)
//                  throws YamlParseException
//    {
//        UUID            id          = UUID.randomUUID();
//
//        String          name        = yaml.atKey("name").getString();
//        BackgroundColor background  = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));
//        Integer             index   = pageIndex;
//
//        List<Group>     groups      = yaml.atKey("groups").forEach(new YamlParser.ForEach<Group>() {
//            @Override
//            public Group forEach(YamlParser yaml, int index) throws YamlParseException {
//                return Group.fromYaml(yaml, index);
//            }
//        }, true);
//
//        return new Page(id, name, null, index, groups);
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
//        return this.modelId;
//    }
//
//
//    public void setId(UUID id)
//    {
//        this.modelId = id;
//    }
//
//
//    // ** On Load
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the Page is completely loaded for the first time.
//     */
//    public void onLoad()
//    {
//        this.initializePage();
//    }
//
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the page.
//     */
//    public void initialize(Context context)
//    {
//        // Initialize each group
//        for (Group group : this.groups()) {
//            group.initialize(context);
//        }
//    }
//
//
//    // > Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The page's yaml representation.
//     * @return The Yaml Builder.
//     */
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putString("name", this.name())
//                .putList("groups", this.groups());
//    }
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    // ** Label
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Returns the label of this page.
//     * @return The page label.
//     */
//    public String name()
//    {
//        return this.pageId.getValue();
//    }
//
//
//    // ** Index
//    // ------------------------------------------------------------------------------------------
//
//    public Integer index()
//    {
//        return this.index.getValue();
//    }
//
//
//    // ** Groups
//    // ------------------------------------------------------------------------------------------
//
//    public List<Group> groups()
//    {
//        return this.groups.getValue();
//    }
//
//
//    // > View
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Returns an android view that represents this page.
//     * @return A View of this page.
//     */
//    public View view()
//    {
//        Context context = SheetManagerOld.currentSheetContext();
//
//        LinearLayout layout = viewLayout(context);
//
//        updateView(layout);
//
//        return layout;
//    }
//
//
//    private LinearLayout viewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        this.pageViewId         = Util.generateViewId();
//        layout.id               = this.pageViewId;
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        // layout.backgroundColor  = this.background().colorId();
//
//        return layout.linearLayout(context);
//    }
//
//
//    private void updateView(LinearLayout pageLayout)
//    {
//        // > Ensure page layout is available
//        // --------------------------------------------------------------------------------------
//        Context context = SheetManagerOld.currentSheetContext();
//        if (pageLayout == null)
//            pageLayout = (LinearLayout) ((Activity) context).findViewById(this.pageViewId);
//
//        // > Views
//        // --------------------------------------------------------------------------------------
//
//        // ** Groups
//        // --------------------------------------------------------------------------------------
//        if (!this.groups.isNull()) {
//            for (Group group : this.groups.getValue()) {
//                pageLayout.addView(group.view(context));
//            }
//        }
//    }
//
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the page state.
//     */
//    private void initializePage()
//    {
//        // [1] Sort the groups
//        // --------------------------------------------------------------------------------------
//
//        this.sortGroups();
//
//        // [2] Add group update listener to ensure they are always sorted
//        // --------------------------------------------------------------------------------------
//
//        this.groups.setOnUpdateListener(new Functor.OnUpdateListener() {
//            @Override
//            public void onUpdate() {
//                sortGroups();
//            }
//        });
//    }
//
//
//    /**
//     * Sort the pages by their index value, so they are displayed in the intended order.
//     */
//    private void sortGroups()
//    {
//        if (this.groups.isNull())
//            return;
//
//        // Make sure groups are sorted
//        Collections.sort(this.groups.getValue(), new Comparator<Group>() {
//            @Override
//            public int compare(Group group1, Group group2) {
//                if (group1.index() > group2.index())
//                    return 1;
//                if (group1.index() < group2.index())
//                    return -1;
//                return 0;
//            }
//        });
//    }
//
//
//}

