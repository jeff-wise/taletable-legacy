
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.WidgetUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.functor.CollectionFunctor;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.functor.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Row
 *
 * A row of widgets in a group.
 */
public class GroupRow implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<Integer>       index;
    private ModelFunctor<GroupRowFormat>    format;
    private CollectionFunctor<WidgetUnion>  widgets;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private GroupParent                     groupParent;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupRow()
    {
        this.id         = null;

        this.index      = new PrimitiveFunctor<>(null, Integer.class);
        this.format     = ModelFunctor.empty(GroupRowFormat.class);
        this.widgets    = CollectionFunctor.empty(WidgetUnion.class);
    }


    public GroupRow(UUID id,
                    Integer index,
                    GroupRowFormat format,
                    List<WidgetUnion> widgets)
    {
        this.id         = id;

        this.index      = new PrimitiveFunctor<>(index, Integer.class);
        this.format     = ModelFunctor.full(format, GroupRowFormat.class);
        this.widgets    = CollectionFunctor.full(widgets, WidgetUnion.class);
    }


    /**
     * Create a row from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The new row.
     * @throws YamlParseException
     */
    public static GroupRow fromYaml(Integer index, YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id      = UUID.randomUUID();

        GroupRowFormat    format  = GroupRowFormat.fromyaml(yaml.atMaybeKey("format"));

        List<WidgetUnion> widgets = yaml.atKey("widgets").forEach(
                                                new YamlParser.ForEach<WidgetUnion>() {
            @Override
            public WidgetUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return WidgetUnion.fromYaml(yaml);
            }
        }, true);

        return new GroupRow(id, index, format, widgets);
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
    public void onLoad() { }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the group row.
     */
    public void initialize(GroupParent groupParent)
    {
        this.groupParent = groupParent;

        // Initialize each widget
        for (WidgetUnion widgetUnion : this.widgets())
        {
            widgetUnion.widget().initialize(groupParent);
        }
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("format", this.format())
                .putList("widgets", this.widgets());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The row index.
     * @return The index.
     */
    public Integer index()
    {
        return this.index.getValue();
    }


    /**
     * Get the widgets in the row.
     * @return A list of widgets.
     */
    public List<WidgetUnion> widgets()
    {
        return this.widgets.getValue();
    }


    /**
     * The group row formatting options.
     * @return The format.
     */
    public GroupRowFormat format()
    {
        return this.format.getValue();
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public LinearLayout view(Context context)
    {
        return mainView(context);
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout mainView(Context context)
    {
        LinearLayout layout = mainViewLayout(context);

        layout.addView(widgetsView(context));

        if (this.format().dividerType() != DividerType.NONE)
            layout.addView(dividerView(context));

        return layout;
    }


    private LinearLayout mainViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation  = LinearLayout.VERTICAL;
        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.marginSpacing    = this.format().margins();

        return layout.linearLayout(context);
    }


    private LinearLayout widgetsView(Context context)
    {
        LinearLayout layout = widgetsViewLayout(context);

        boolean rowHasTopLabel = false;

        for (WidgetUnion widgetUnion : this.widgets())
        {
            if (widgetUnion.widget().data().format().label() != null) {
                rowHasTopLabel = true;
            }
        }

        for (WidgetUnion widgetUnion : this.widgets())
        {
            Widget widget = widgetUnion.widget();

            layout.addView(widget.view(rowHasTopLabel, context));
        }

        return layout;
    }

    private LinearLayout widgetsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.paddingSpacing   = this.format().padding();

        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width       = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.height      = R.dimen.one_dp;

        BackgroundColor backgroundColor = this.format().backgroundColor();
        if (this.format().backgroundColor() == BackgroundColor.EMPTY)
            backgroundColor = this.groupParent.background();

        divider.backgroundColor = this.format().dividerType()
                                      .colorIdWithBackground(backgroundColor);

        return divider.linearLayout(context);
    }

}
