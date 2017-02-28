
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Background;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Group
 */
public class Group implements GroupParent, Model, ToYaml, Serializable
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
        });

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
    public void initialize()
    {
        // Initialize each row
        for (GroupRow groupRow : this.rows()) {
            groupRow.initialize(this);
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
    public Background background()
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
        LinearLayout layout = this.layout(context);

        if (this.format().showName())
            layout.addView(this.nameView(context));

        for (GroupRow groupRow : this.rows()) {
            layout.addView(groupRow.view(context));
        }

        if (this.format().dividerType() != DividerType.NONE)
            layout.addView(dividerView(context));

        return layout;
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top      = this.format().spaceAbove().resourceId();

        if (this.format().dividerType() == DividerType.NONE)
            layout.padding.bottom   = this.format().spaceBelow().resourceId();

        // > Background
        layout.backgroundColor  = this.background().colorId();

        return layout.linearLayout(context);
    }


    private LinearLayout nameView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     name  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = this.format().nameStyle().alignment().gravityConstant();

//        if (this.format().nameStyle().alignment() == Alignment.LEFT)
//            layout.margin.left      = R.dimen.group_label_margin_left;
//
//        if (this.format().nameStyle().alignment() == Alignment.RIGHT)

        layout.margin.left      = R.dimen.group_label_margins_horz;
        layout.margin.right     = R.dimen.group_label_margins_horz;

        layout.child(name);

        // [3] Label
        // -------------------------------------------------------------------------------------

        name.width     = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height    = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.gravity   = this.format().nameStyle().alignment().gravityConstant();

        name.id        = R.id.widget_label;
        name.text      = this.name();

        this.format().nameStyle().styleTextViewBuilder(name, context);

        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder border = new LinearLayoutBuilder();

        border.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        border.height           = R.dimen.one_dp;

        // > Color
        border.backgroundColor  = this.format().dividerType()
                                      .colorIdWithBackground(this.background());

        border.margin.top   = this.format().spaceBelow().resourceId();

        return border.linearLayout(context);
    }

}
