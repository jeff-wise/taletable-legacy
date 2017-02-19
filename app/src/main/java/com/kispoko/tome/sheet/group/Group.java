
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
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
import java.util.ArrayList;
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

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<Boolean>           showName;
    private PrimitiveFunctor<Spacing>           spaceAbove;
    private PrimitiveFunctor<Spacing>           spaceBelow;
    private PrimitiveFunctor<GroupBackground>   background;
    private ModelFunctor<TextStyle>             labelStyle;
    private PrimitiveFunctor<Boolean>           divider;
    private PrimitiveFunctor<Integer>           index;
    private CollectionFunctor<GroupRow>         rows;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.showName       = new PrimitiveFunctor<>(null, Boolean.class);
        this.spaceAbove     = new PrimitiveFunctor<>(null, Spacing.class);
        this.spaceBelow     = new PrimitiveFunctor<>(null, Spacing.class);
        this.background     = new PrimitiveFunctor<>(null, GroupBackground.class);
        this.labelStyle     = ModelFunctor.empty(TextStyle.class);
        this.divider        = new PrimitiveFunctor<>(null, Boolean.class);
        this.index          = new PrimitiveFunctor<>(null, Integer.class);

        this.rows       = CollectionFunctor.empty(GroupRow.class);
    }


    public Group(UUID id,
                 String name,
                 Boolean showName,
                 Spacing spaceAbove,
                 Spacing spaceBelow,
                 GroupBackground background,
                 TextStyle labelStyle,
                 Boolean divider,
                 Integer index,
                 List<GroupRow> groupRows)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.showName       = new PrimitiveFunctor<>(showName, Boolean.class);
        this.spaceAbove     = new PrimitiveFunctor<>(spaceAbove, Spacing.class);
        this.spaceBelow     = new PrimitiveFunctor<>(spaceBelow, Spacing.class);
        this.background     = new PrimitiveFunctor<>(background, GroupBackground.class);
        this.labelStyle     = ModelFunctor.full(labelStyle, TextStyle.class);
        this.divider        = new PrimitiveFunctor<>(divider, Boolean.class);
        this.index          = new PrimitiveFunctor<>(index, Integer.class);

        List<Class<? extends GroupRow>> rowClasses = new ArrayList<>();
        rowClasses.add(GroupRow.class);
        this.rows       = CollectionFunctor.full(groupRows, rowClasses);

        this.setShowName(showName);
        this.setSpaceAbove(spaceAbove);
        this.setSpaceBelow(spaceBelow);
        this.setBackground(background);
        this.setLabelStyle(labelStyle);
        this.setDivider(divider);
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(YamlParser yaml, int groupIndex)
            throws YamlParseException
    {
        UUID            id              = UUID.randomUUID();

        String          label           = yaml.atMaybeKey("name").getString();
        Boolean         showName        = yaml.atMaybeKey("show_name").getBoolean();
        Spacing         spaceAbove      = Spacing.fromYaml(yaml.atMaybeKey("space_above"));
        Spacing         spaceBelow      = Spacing.fromYaml(yaml.atMaybeKey("space_below"));
        GroupBackground background      = GroupBackground.fromYaml(yaml.atMaybeKey("background"));
        TextStyle       labelStyle      = TextStyle.fromYaml(yaml.atMaybeKey("label_style"), false);
        Boolean         bottomBorder = yaml.atMaybeKey("divider").getBoolean();
        Integer         index        = groupIndex;

        List<GroupRow> groupRows = yaml.atKey("rows").forEach(new YamlParser.ForEach<GroupRow>() {
            @Override
            public GroupRow forEach(YamlParser yaml, int index) throws YamlParseException {
                return GroupRow.fromYaml(index, yaml);
            }
        });

        return new Group(id, label, showName, spaceAbove, spaceBelow, background,
                         labelStyle, bottomBorder, index, groupRows);
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
                .putString("label", this.name())
                .putBoolean("show_name", this.showName())
                .putYaml("space_above", this.spaceAbove())
                .putYaml("space_below", this.spaceBelow())
                .putYaml("background", this.background())
                .putYaml("label_style", this.labelStyle())
                .putBoolean("divider", this.bottomBorder())
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
    public Spacing spaceAbove()
    {
        return this.spaceAbove.getValue();
    }


    public void setSpaceAbove(Spacing spaceAbove)
    {
        if (spaceAbove != null)
            this.spaceAbove.setValue(spaceAbove);
        else
            this.spaceAbove.setValue(Spacing.SMALL);
    }


    // ** Space Below
    // ------------------------------------------------------------------------------------------

    /**
     * The space at the bottom of the group.
     * @return The Space Above.
     */
    public Spacing spaceBelow()
    {
        return this.spaceBelow.getValue();
    }


    public void setSpaceBelow(Spacing spacing)
    {
        if (spacing != null)
            this.spaceBelow.setValue(spacing);
        else
            this.spaceBelow.setValue(Spacing.SMALL);
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


    public void setBackground(GroupBackground background)
    {
        if (background != null)
            this.background.setValue(background);
        else
            this.background.setValue(GroupBackground.MEDIUM);
    }


    // ** Label Style
    // ------------------------------------------------------------------------------------------

    /**
     * The group label style.
     * @return The label Text Style.
     */
    public TextStyle labelStyle()
    {
        return this.labelStyle.getValue();
    }


    /**
     * Set the group label style. If null, sets a default style.
     * @param labelStyle The group label Text Style.
     */
    public void setLabelStyle(TextStyle labelStyle)
    {
        if (labelStyle != null) {
            this.labelStyle.setValue(labelStyle);
        }
        else {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.GOLD_VERY_LIGHT,
                                                        TextSize.MEDIUM,
                                                        true,
                                                        false,
                                                        false,
                                                        Alignment.LEFT);
            this.labelStyle.setValue(defaultLabelStyle);

        }
    }


    // ** Bottom Border
    // ------------------------------------------------------------------------------------------

    /**
     * True if the group has a bottom border (functioning as a divider).
     * @return Bottom border?
     */
    public Boolean bottomBorder()
    {
        return this.divider.getValue();
    }


    public void setDivider(Boolean divider)
    {
        if (divider != null)
            this.divider.setValue(divider);
        else
            this.divider.setValue(false);
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
        LinearLayout layout = this.layout(context);

        if (this.showName())
            layout.addView(this.labelView(context));

        for (GroupRow groupRow : this.rows()) {
            layout.addView(groupRow.view(context));
        }

        if (this.bottomBorder())
            layout.addView(dividerView(context));

        return layout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top      = this.spaceAbove().resourceId();

        if (!this.bottomBorder())
            layout.padding.bottom   = this.spaceBelow().resourceId();

        // > Background
        layout.backgroundColor  = this.background().resourceId();

        return layout.linearLayout(context);
    }


    private LinearLayout labelView(Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = this.labelStyle().alignment().gravityConstant();

        layout.margin.left      = R.dimen.group_label_margin_left;

        layout.child(label);

        // [3] Label
        // --------------------------------------------------------------------------------------

        label.width     = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height    = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.id        = R.id.widget_label;
        label.text      = this.name();
        label.size      = this.labelStyle().size().resourceId();
        label.color     = this.labelStyle().color().resourceId();

        // > Font
        if (this.labelStyle().isBold())
            label.font = Font.serifFontBold(context);
        else
            label.font = Font.serifFontRegular(context);

        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder border = new LinearLayoutBuilder();

        border.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        border.height           = R.dimen.one_dp;

        // > Color
        switch (this.background())
        {
            case LIGHT:
                border.backgroundColor = R.color.dark_blue_4;
                break;
            case MEDIUM:
                border.backgroundColor = R.color.dark_blue_4;
                break;
            case DARK:
                border.backgroundColor = R.color.dark_blue_6;
                break;
        }

        border.margin.top   = this.spaceBelow().resourceId();

        return border.linearLayout(context);
    }

}
