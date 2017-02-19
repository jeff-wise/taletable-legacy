
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.widget.LinearLayout;

import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.WidgetUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
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
    private PrimitiveFunctor<Alignment>  alignment;
    private PrimitiveFunctor<RowWidth>      width;
    private PrimitiveFunctor<Spacing>       spaceAbove;
    private CollectionFunctor<WidgetUnion>  widgets;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private GroupParent                     groupParent;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupRow()
    {
        this.id             = null;

        this.index          = new PrimitiveFunctor<>(null, Integer.class);
        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
        this.width          = new PrimitiveFunctor<>(null, RowWidth.class);
        this.spaceAbove     = new PrimitiveFunctor<>(null, Spacing.class);

        List<Class<? extends WidgetUnion>> widgetClasses = new ArrayList<>();
        widgetClasses.add(WidgetUnion.class);
        this.widgets       = CollectionFunctor.empty(widgetClasses);
    }


    public GroupRow(UUID id,
                    Integer index,
                    List<WidgetUnion> widgets,
                    Alignment alignment,
                    RowWidth width,
                    Spacing spaceAbove)
    {
        this.id             = id;

        this.index          = new PrimitiveFunctor<>(index, Integer.class);
        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.width          = new PrimitiveFunctor<>(width, RowWidth.class);
        this.spaceAbove     = new PrimitiveFunctor<>(spaceAbove, Spacing.class);

        List<Class<? extends WidgetUnion>> widgetClasses = new ArrayList<>();
        widgetClasses.add(WidgetUnion.class);
        this.widgets        = CollectionFunctor.full(widgets, widgetClasses);

        this.setAlignment(alignment);
        this.setWidth(width);
        this.setSpaceAbove(spaceAbove);
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
        UUID         id             = UUID.randomUUID();

        Alignment alignment     = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        RowWidth      width         = RowWidth.fromYaml(yaml.atMaybeKey("width"));
        Spacing separation       = Spacing.fromYaml(yaml.atMaybeKey("space_above"));

        List<WidgetUnion> widgets = yaml.atKey("widgets").forEach(
                                                new YamlParser.ForEach<WidgetUnion>() {
            @Override
            public WidgetUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return WidgetUnion.fromYaml(yaml);
            }
        }, true);

        return new GroupRow(id, index, widgets, alignment, width, separation);
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
                .putYaml("alignment", this.alignment())
                .putYaml("width", this.width())
                .putYaml("space_above", this.spaceAbove())
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


    // ** Alignment
    // ------------------------------------------------------------------------------------------

    /**
     * The row alignment.
     * @return The row alignment.
     */
    public Alignment alignment()
    {
        return this.alignment.getValue();
    }


    public void setAlignment(Alignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(Alignment.CENTER);
    }


    // ** Width
    // ------------------------------------------------------------------------------------------

    /**
     * Get the width of the row. The width is a value between 1 and 100, and represents a
     * percentage width of the screen that the row should span.
     * @return The width.
     */
    public RowWidth width()
    {
        return this.width.getValue();
    }


    public void setWidth(RowWidth width)
    {
        if (width != null)
            this.width.setValue(width);
        else
            this.width.setValue(RowWidth.FULL);
    }


    // ** Separation
    // ------------------------------------------------------------------------------------------

    /**
     * The row separation (the vertical row margins).
     * @return The row separtaion.
     */
    public Spacing spaceAbove()
    {
        return this.spaceAbove.getValue();
    }


    public void setSpaceAbove(Spacing spaceAbove)
    {
        if (spaceAbove != null)
            this.spaceAbove.setValue(spaceAbove);
        else
            this.spaceAbove.setValue(Spacing.MEDIUM);
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public LinearLayout view(Context context)
    {
        LinearLayout layout = this.layout(context);

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


    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.top       = this.spaceAbove().resourceId();

        int paddingDimenId = this.width().resourceId();
        layout.padding.left     = paddingDimenId;
        layout.padding.right    = paddingDimenId;

        return layout.linearLayout(context);
    }

}
