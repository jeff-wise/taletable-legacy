
package com.kispoko.tome.model.sheet.group


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.sheet.widget.Widget
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Group Row
 */
data class GroupRow(override val id : UUID,
                    val format : Func<GroupRowFormat>,
                    val index : Func<Int>,
                    val rows : Coll<Widget>) : Model
{
    companion object : Factory<GroupRow>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<GroupRow> = when (doc)
        {
            is DocDict -> effApply(::GroupRow,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Format
                                   doc.at("format") ap {
                                       effApply(::Comp, GroupRowFormat.fromDocument(it))
                                   },
                                   // Index
                                   effApply(::Prim, doc.int("index")),
                                   // Widgets
                                   doc.list("widgets") ap { docList ->
                                       effApply(::Coll,
                                                docList.map { Widget.fromDocument(it) })
                                   })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



/**
 * Group Row Format
 */
data class GroupRowFormat(override val id : UUID,
                          val alignment : Func<Alignment>,
                          val backgroundColor : Func<ColorId>,
                          val margins : Func<Spacing>,
                          val padding : Func<Spacing>,
                          val dividerColor : Func<ColorId>) : Model
{
    companion object : Factory<GroupRowFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<GroupRowFormat> = when (doc)
        {
            is DocDict -> effApply(::GroupRowFormat,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Alignment
                                   effApply(::Prim, doc.enum<Alignment>("alignment")),
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
                                   // Divider Color
                                   doc.at("divider_color") ap {
                                        effApply(::Prim, ColorId.fromDocument(it))
                                   })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


//
//public class GroupRow extends Model
//                      implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                            id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<Integer>       index;
//    private ModelFunctor<GroupRowFormat>    format;
//    private CollectionFunctor<WidgetUnion>  widgets;
//
//
//    // > Internal
//    // -----------------------------------------------------------------------------------------
//
//    private GroupParent                     groupParent;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public GroupRow()
//    {
//        this.id         = null;
//
//        this.index      = new PrimitiveFunctor<>(null, Integer.class);
//        this.format     = ModelFunctor.empty(GroupRowFormat.class);
//        this.widgets    = CollectionFunctor.empty(WidgetUnion.class);
//    }
//
//
//    public GroupRow(UUID id,
//                    Integer index,
//                    GroupRowFormat format,
//                    List<WidgetUnion> widgets)
//    {
//        this.id         = id;
//
//        this.index      = new PrimitiveFunctor<>(index, Integer.class);
//        this.format     = ModelFunctor.full(format, GroupRowFormat.class);
//        this.widgets    = CollectionFunctor.full(widgets, WidgetUnion.class);
//    }
//
//
//    /**
//     * Create a row from its Yaml representation.
//     * @param yaml The yaml parser.
//     * @return The new row.
//     * @throws YamlParseException
//     */
//    public static GroupRow fromYaml(Integer index, YamlParser yaml)
//                  throws YamlParseException
//    {
//        UUID              id      = UUID.randomUUID();
//
//        GroupRowFormat    format  = GroupRowFormat.fromyaml(yaml.atMaybeKey("format"));
//
//        List<WidgetUnion> widgets = yaml.atKey("widgets").forEach(
//                                                new YamlParser.ForEach<WidgetUnion>() {
//            @Override
//            public WidgetUnion forEach(YamlParser yaml, int index) throws YamlParseException {
//                return WidgetUnion.fromYaml(yaml);
//            }
//        }, true);
//
//        return new GroupRow(id, index, format, widgets);
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
//    public void onLoad() { }
//
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the group row.
//     */
//    public void initialize(GroupParent groupParent, Context context)
//    {
//        this.groupParent = groupParent;
//
//        // Initialize each widget
//        for (WidgetUnion widgetUnion : this.widgets())
//        {
//            widgetUnion.widget().initialize(groupParent, context);
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
//                .putYaml("format", this.format())
//                .putList("widgets", this.widgets());
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The row index.
//     * @return The index.
//     */
//    public Integer index()
//    {
//        return this.index.getValue();
//    }
//
//
//    /**
//     * Get the widgets in the row.
//     * @return A list of widgets.
//     */
//    public List<WidgetUnion> widgets()
//    {
//        return this.widgets.getValue();
//    }
//
//
//    /**
//     * The group row formatting options.
//     * @return The format.
//     */
//    public GroupRowFormat format()
//    {
//        return this.format.getValue();
//    }
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    public LinearLayout view(Context context)
//    {
//        return mainView(context);
//    }
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    private LinearLayout mainView(Context context)
//    {
//        LinearLayout layout = mainViewLayout(context);
//
//        layout.addView(widgetsView(context));
//
//        if (this.format().dividerType() != DividerType.NONE)
//            layout.addView(dividerView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout mainViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation  = LinearLayout.VERTICAL;
//        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.marginSpacing    = this.format().margins();
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout widgetsView(Context context)
//    {
//        LinearLayout layout = widgetsViewLayout(context);
//
//        boolean rowHasTopLabel = false;
//
//        for (WidgetUnion widgetUnion : this.widgets())
//        {
//            if (widgetUnion.widget().data().format().label() != null) {
//                rowHasTopLabel = true;
//            }
//        }
//
//        for (WidgetUnion widgetUnion : this.widgets())
//        {
//            Widget widget = widgetUnion.widget();
//
//            layout.addView(widget.view(rowHasTopLabel, context));
//        }
//
//        return layout;
//    }
//
//    private LinearLayout widgetsViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.HORIZONTAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.paddingSpacing   = this.format().padding();
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout dividerView(Context context)
//    {
//        LinearLayoutBuilder divider = new LinearLayoutBuilder();
//
//        divider.width       = LinearLayout.LayoutParams.MATCH_PARENT;
//        divider.height      = R.dimen.one_dp;
//
//        BackgroundColor backgroundColor = this.format().backgroundColor();
//        if (this.format().backgroundColor() == BackgroundColor.EMPTY)
//            backgroundColor = this.groupParent.background();
//
//        divider.backgroundColor = this.format().dividerType()
//                                      .colorIdWithBackground(backgroundColor);
//
//        return divider.linearLayout(context);
//    }
//
//}
