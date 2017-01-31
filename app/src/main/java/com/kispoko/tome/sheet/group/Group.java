
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
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
 * Group
 */
public class Group implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<Boolean>           showName;
    private PrimitiveFunctor<SpaceAbove>        spaceAbove;
    private PrimitiveFunctor<GroupBackground>   background;
    private PrimitiveFunctor<Integer>           index;
    private CollectionFunctor<GroupRow>         rows;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.showName   = new PrimitiveFunctor<>(null, Boolean.class);
        this.spaceAbove = new PrimitiveFunctor<>(null, SpaceAbove.class);
        this.background = new PrimitiveFunctor<>(null, GroupBackground.class);
        this.index      = new PrimitiveFunctor<>(null, Integer.class);

        List<Class<? extends GroupRow>> rowClasses = new ArrayList<>();
        rowClasses.add(GroupRow.class);
        this.rows       = CollectionFunctor.empty(rowClasses);
    }


    public Group(UUID id,
                 String name,
                 Boolean showName,
                 SpaceAbove spaceAbove,
                 GroupBackground background,
                 Integer index,
                 List<GroupRow> groupRows)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.showName   = new PrimitiveFunctor<>(showName, Boolean.class);
        this.spaceAbove = new PrimitiveFunctor<>(spaceAbove, SpaceAbove.class);
        this.background = new PrimitiveFunctor<>(background, GroupBackground.class);
        this.index      = new PrimitiveFunctor<>(index, Integer.class);

        List<Class<? extends GroupRow>> rowClasses = new ArrayList<>();
        rowClasses.add(GroupRow.class);
        this.rows       = CollectionFunctor.full(groupRows, rowClasses);

        this.setShowName(showName);
        this.setSpaceAbove(spaceAbove);
        this.setBackground(background);
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(YamlParser yaml, int groupIndex)
            throws YamlParseException
    {
        UUID            id          = UUID.randomUUID();

        String          label       = yaml.atMaybeKey("label").getString();
        Boolean         showName    = yaml.atMaybeKey("show_name").getBoolean();
        SpaceAbove      spaceAbove  = SpaceAbove.fromYaml(yaml.atMaybeKey("space_above"));
        GroupBackground background  = GroupBackground.fromYaml(yaml.atMaybeKey("background"));
        Integer         index       = groupIndex;

        List<GroupRow> groupRows = yaml.atKey("rows").forEach(new YamlParser.ForEach<GroupRow>() {
            @Override
            public GroupRow forEach(YamlParser yaml, int index) throws YamlParseException {
                return GroupRow.fromYaml(index, yaml);
            }
        });

        return new Group(id, label, showName, spaceAbove, background, index, groupRows);
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
            groupRow.initialize();
        }
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("label", this.name())
                .putBoolean("show_name", this.showName())
                .putList("rows", this.rows());
    }

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Label
    // ------------------------------------------------------------------------------------------

    /**
     * Get the group label.
     *
     * @return The group label String.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Show Name
    // ------------------------------------------------------------------------------------------

    /**
     * True if the group name is displayed in the sheet.
     * @return Show name?
     */
    public Boolean showName()
    {
        return this.showName.getValue();
    }


    public void setShowName(Boolean showName)
    {
        if (showName != null)
            this.showName.setValue(showName);
        else
            this.showName.setValue(true);
    }


    // ** Space Above
    // ------------------------------------------------------------------------------------------

    /**
     * The space above the group on the sheet.
     * @return The Space Above.
     */
    public SpaceAbove spaceAbove()
    {
        return this.spaceAbove.getValue();
    }


    public void setBackground(GroupBackground background)
    {
        if (background != null)
            this.background.setValue(background);
        else
            this.background.setValue(GroupBackground.NONE);
    }


    // ** Group Background
    // ------------------------------------------------------------------------------------------

    /**
     * The group background color.
     * @return The Group Background.
     */
    public GroupBackground background()
    {
        return this.background.getValue();
    }


    public void setSpaceAbove(SpaceAbove spaceAbove)
    {
        if (spaceAbove != null)
            this.spaceAbove.setValue(spaceAbove);
        else
            this.spaceAbove.setValue(SpaceAbove.SMALL);
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


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        // [2] Structure
        // --------------------------------------------------------------------------------------

        LinearLayout layout = this.layout(context);

        if (this.showName())
            layout.addView(this.labelView(context));

        for (GroupRow groupRow : this.rows()) {
            layout.addView(groupRow.view(context));
        }

        return layout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.orientation      = LinearLayout.VERTICAL;

        layout.margin.top       = this.spaceAbove().resourceId();

        // > Background
        if (this.background() != GroupBackground.NONE) {
            layout.backgroundColor  = this.background().resourceId();
            layout.padding.top      = R.dimen.group_background_padding_vert;
            layout.padding.bottom   = R.dimen.group_background_padding_vert;
//            layout.padding.left     = R.dimen.group_background_padding_horz;
//            layout.padding.right    = R.dimen.group_background_padding_horz;
        }
        //else {
            layout.padding.left     = R.dimen.group_padding_horz;
            layout.padding.right    = R.dimen.group_padding_horz;
       // }

        return layout.linearLayout(context);
    }


    private LinearLayout labelView(Context context)
    {
        // [1] Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder labelLayout = new LinearLayoutBuilder();
        TextViewBuilder labelView = new TextViewBuilder();

        // [2 A] Layout
        // --------------------------------------------------------------------------------------

        labelLayout.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        labelLayout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        labelLayout.margin.left = R.dimen.group_label_margin_left;

        labelLayout.child(labelView);

        // [2 B] Text
        // --------------------------------------------------------------------------------------

        labelView.id    = R.id.widget_label;
        labelView.size  = R.dimen.group_label_text_size;
        labelView.color = R.color.gold_hl_9;
        labelView.font  = Font.serifFontRegular(context);
        labelView.text  = this.name();


        return labelLayout.linearLayout(context);
    }

}
