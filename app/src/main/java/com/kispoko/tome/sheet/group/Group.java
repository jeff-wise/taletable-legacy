
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Group
 */
public class Group extends Model
                   implements GroupParent, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;

    private PrimitiveFunctor<Integer>   index;
    private CollectionFunctor<GroupRow> rows;

    private ModelFunctor<GroupFormat>   format;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.index      = new PrimitiveFunctor<>(null, Integer.class);

        this.rows       = CollectionFunctor.empty(GroupRow.class);

        this.format     = ModelFunctor.empty(GroupFormat.class);
    }


    public Group(UUID id,
                 String name,
                 Integer index,
                 List<GroupRow> groupRows,
                 GroupFormat format)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.index      = new PrimitiveFunctor<>(index, Integer.class);

        this.rows       = CollectionFunctor.full(groupRows, GroupRow.class);

        this.format     = ModelFunctor.full(format, GroupFormat.class);
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(YamlParser yaml, int groupIndex)
            throws YamlParseException
    {
        UUID           id        = UUID.randomUUID();

        String         name      = yaml.atMaybeKey("name").getString();
        Integer        index     = groupIndex;

        List<GroupRow> groupRows = yaml.atKey("rows").forEach(new YamlParser.ForEach<GroupRow>() {
            @Override
            public GroupRow forEach(YamlParser yaml, int index) throws YamlParseException {
                return GroupRow.fromYaml(index, yaml);
            }
        }, true);

        GroupFormat     format   = GroupFormat.fromYaml(yaml.atMaybeKey("format"));

        return new Group(id, name, index, groupRows, format);
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
     * Initialize the group
     */
    public void initialize(Context context)
    {
        // Initialize each row
        for (GroupRow groupRow : this.rows()) {
            groupRow.initialize(this, context);
        }
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putList("rows", this.rows())
                .putYaml("format", this.format());
    }


    // > Group Parent
    // ------------------------------------------------------------------------------------------

    @Override
    public BackgroundColor background()
    {
        return this.format().background();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * The group name.
     * @return The group label String.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Index
    // ------------------------------------------------------------------------------------------

    /**
     * Get the group's index (starting at 0) , which is its position in the page.
     * @return The group index.
     */
    public Integer index()
    {
        return this.index.getValue();
    }


    // ** Rows
    // ------------------------------------------------------------------------------------------

    /**
     * Get the group's rows.
     * @return A list of rows.
     */
    public List<GroupRow> rows()
    {
        return this.rows.getValue();
    }


    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The group formatting options.
     * @return The format.
     */
    public GroupFormat format()
    {
        return this.format.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        layout.addView(rowsView(context));

        if (this.format().dividerType() != DividerType.NONE)
            layout.addView(dividerView(context));

        return layout;
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.marginSpacing        = this.format().margins();

        layout.backgroundColor      = this.background().colorId();
        layout.backgroundResource   = this.format().corners().resourceId();

        return layout.linearLayout(context);
    }


    private LinearLayout rowsView(Context context)
    {
        LinearLayout layout = rowsViewLayout(context);

        for (GroupRow groupRow : this.rows()) {
            layout.addView(groupRow.view(context));
        }

        return layout;
    }

    private LinearLayout rowsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.paddingSpacing   = this.format().padding();


        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.heightDp            = this.format().dividerThickness();

        divider.backgroundColor     = this.format().dividerType()
                                          .colorIdWithBackground(this.background());

        divider.margin.leftDp       = this.format().dividerPadding().floatValue();
        divider.margin.rightDp      = this.format().dividerPadding().floatValue();

        return divider.linearLayout(context);
    }

}
