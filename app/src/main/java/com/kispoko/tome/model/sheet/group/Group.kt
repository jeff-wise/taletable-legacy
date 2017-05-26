
package com.kispoko.tome.model.sheet.group


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.page.PageName
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.DividerMargin
import com.kispoko.tome.model.sheet.style.DividerThickness
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Group
 */
data class Group(override val id : UUID,
                 val name : Func<GroupName>,
                 val format : Func<GroupFormat>,
                 val index : Func<Int>,
                 val rows : Coll<GroupRow>) : Model
{
    companion object : Factory<Group>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Group> = when (doc)
        {
            is DocDict -> effApply5(::Group,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Name
                                    doc.at("name") ap {
                                        effApply(::Prim, GroupName.fromDocument(it))
                                    },
                                    // Format
                                    doc.at("format") ap {
                                        effApply(::Comp, GroupFormat.fromDocument(it))
                                    },
                                    // Index
                                    effApply(::Prim, doc.int("index")),
                                    // Groups
                                    doc.list("rows") ap { docList ->
                                        effApply(::Coll,
                                                 docList.map { GroupRow.fromDocument(it) })
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Group Name
 */
data class GroupName(val name : String)
{

    companion object : Factory<GroupName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<GroupName> = when (doc)
        {
            is DocText -> valueResult(GroupName(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Group Format
 */
data class GroupFormat(override val id : UUID,
                       val backgroundColor : Func<ColorId>,
                       val margins : Func<Spacing>,
                       val padding : Func<Spacing>,
                       val corners : Func<Corners>,
                       val dividerColor : Func<ColorId>,
                       val dividerMargins : Func<DividerMargin>,
                       val dividerThickness : Func<DividerThickness>) : Model
{
    companion object : Factory<GroupFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<GroupFormat> = when (doc)
        {
            is DocDict -> effApply8(::GroupFormat,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Background Color
                                    doc.at("background_color") ap {
                                        effApply(::Prim, ColorId.fromDocument(it))
                                    },
                                    // Margins
                                    doc.at("margins") ap {
                                        effApply(::Comp, Spacing.fromDocument(it))
                                    },
                                    // Padding
                                    doc.at("padding") ap {
                                        effApply(::Comp, Spacing.fromDocument(it))
                                    },
                                    // Corners
                                    effApply(::Prim, doc.enum<Corners>("corners")),
                                    // Divider Color
                                    doc.at("divider_color") ap {
                                        effApply(::Prim, ColorId.fromDocument(it))
                                    },
                                    // Divider Margins
                                    doc.at("divider_margins") ap {
                                        effApply(::Prim, DividerMargin.fromDocument(it))
                                    },
                                    // Divider Thickness
                                    doc.at("divider_thickness") ap {
                                        effApply(::Prim, DividerThickness.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}

//
//public class Group extends Model
//                   implements GroupParent, ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    private UUID                        modelId;
//
//
//    // > Functors
//    // ------------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>    groupId;
//
//    private PrimitiveFunctor<Integer>   index;
//    private CollectionFunctor<GroupRow> rows;
//
//    private ModelFunctor<GroupFormat>   format;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public Group()
//    {
//        this.modelId    = null;
//
//        this.groupId    = new PrimitiveFunctor<>(null, String.class);
//        this.index      = new PrimitiveFunctor<>(null, Integer.class);
//
//        this.rows       = CollectionFunctor.empty(GroupRow.class);
//
//        this.format     = ModelFunctor.empty(GroupFormat.class);
//    }
//
//
//    public Group(UUID modelId,
//                 String groupId,
//                 Integer index,
//                 List<GroupRow> groupRows,
//                 GroupFormat format)
//    {
//        this.modelId = modelId;
//
//        this.groupId = new PrimitiveFunctor<>(groupId, String.class);
//        this.index      = new PrimitiveFunctor<>(index, Integer.class);
//
//        this.rows       = CollectionFunctor.full(groupRows, GroupRow.class);
//
//        this.format     = ModelFunctor.full(format, GroupFormat.class);
//    }
//
//
//    @SuppressWarnings("unchecked")
//    public static Group fromYaml(YamlParser yaml, int groupIndex)
//            throws YamlParseException
//    {
//        UUID           id        = UUID.randomUUID();
//
//        String         name      = yaml.atMaybeKey("name").getString();
//        Integer        index     = groupIndex;
//
//        List<GroupRow> groupRows = yaml.atKey("rows").forEach(new YamlParser.ForEach<GroupRow>() {
//            @Override
//            public GroupRow forEach(YamlParser yaml, int index) throws YamlParseException {
//                return GroupRow.fromYaml(index, yaml);
//            }
//        }, true);
//
//        GroupFormat     format   = GroupFormat.fromYaml(yaml.atMaybeKey("format"));
//
//        return new Group(id, name, index, groupRows, format);
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
//     * This method is called when the Roleplay is completely loaded for the first time.
//     */
//    public void onLoad() { }
//
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the group
//     */
//    public void initialize(Context context)
//    {
//        // Initialize each row
//        for (GroupRow groupRow : this.rows()) {
//            groupRow.initialize(this, context);
//        }
//    }
//
//
//    // > Yaml
//    // ------------------------------------------------------------------------------------------
//
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putString("name", this.name())
//                .putList("rows", this.rows())
//                .putYaml("format", this.format());
//    }
//
//
//    // > Group Parent
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public BackgroundColor background()
//    {
//        return this.format().background();
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    // ** Name
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The group name.
//     * @return The group label String.
//     */
//    public String name()
//    {
//        return this.groupId.getValue();
//    }
//
//
//    // ** Index
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the group's index (starting at 0) , which is its position in the page.
//     * @return The group index.
//     */
//    public Integer index()
//    {
//        return this.index.getValue();
//    }
//
//
//    // ** Rows
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the group's rows.
//     * @return A list of rows.
//     */
//    public List<GroupRow> rows()
//    {
//        return this.rows.getValue();
//    }
//
//
//    // ** Format
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The group formatting options.
//     * @return The format.
//     */
//    public GroupFormat format()
//    {
//        return this.format.getValue();
//    }
//
//
//    // > View
//    // ------------------------------------------------------------------------------------------
//
//    public View view(Context context)
//    {
//        LinearLayout layout = this.viewLayout(context);
//
//        layout.addView(rowsView(context));
//
//        if (this.format().dividerType() != DividerType.NONE)
//            layout.addView(dividerView(context));
//
//        return layout;
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private LinearLayout viewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.marginSpacing        = this.format().margins();
//
//        layout.backgroundColor      = this.background().colorId();
//        layout.backgroundResource   = this.format().corners().resourceId();
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout rowsView(Context context)
//    {
//        LinearLayout layout = rowsViewLayout(context);
//
//        for (GroupRow groupRow : this.rows()) {
//            layout.addView(groupRow.view(context));
//        }
//
//        return layout;
//    }
//
//    private LinearLayout rowsViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.paddingSpacing   = this.format().padding();
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout dividerView(Context context)
//    {
//        LinearLayoutBuilder divider = new LinearLayoutBuilder();
//
//        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT;
//        divider.heightDp            = this.format().dividerThickness();
//
//        divider.backgroundColor     = this.format().dividerType()
//                                          .colorIdWithBackground(this.background());
//
//        divider.margin.leftDp       = this.format().dividerPadding().floatValue();
//        divider.margin.rightDp      = this.format().dividerPadding().floatValue();
//
//        return divider.linearLayout(context);
//    }
//
//}
