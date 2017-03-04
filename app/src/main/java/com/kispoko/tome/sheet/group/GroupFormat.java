
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextFont;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Group Format
 */
public class GroupFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The padding at the top of the group.
     */
    private PrimitiveFunctor<Spacing>           paddingTop;

    /**
     * The padding at the bottom of the group.
     */
    private PrimitiveFunctor<Spacing>           paddingBottom;

    /**
     * The space to the left and the right of the group.
     */
    private PrimitiveFunctor<Spacing>           marginHorizontal;

    /**
     * Space above the group, but outside its background boundary.
     */
    private PrimitiveFunctor<Spacing>           marginTop;

    /**
     * Space below the group, but outside its background boundary.
     */
    private PrimitiveFunctor<Spacing>           marginBottom;

    /**
     * The background color of the group.
     */
    private PrimitiveFunctor<BackgroundColor>   background;


    /**
     * The background corner size.
     */
    private PrimitiveFunctor<Corners>           corners;

    /**
     * If true, displays the group name above its content.
     */
    private PrimitiveFunctor<Boolean>           showName;

    /**
     * The text style of the groups name.
     */
    private ModelFunctor<TextStyle>             nameStyle;

    /**
     * The type of divider at the bottom of the group.
     */
    private PrimitiveFunctor<DividerType>       dividerType;

    /**
     * The space between the divider end and the page boundary.
     */
    private PrimitiveFunctor<Spacing>           dividerPadding;



    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupFormat()
    {
        this.id                 = null;

        this.showName           = new PrimitiveFunctor<>(null, Boolean.class);
        this.paddingTop         = new PrimitiveFunctor<>(null, Spacing.class);
        this.paddingBottom      = new PrimitiveFunctor<>(null, Spacing.class);
        this.marginHorizontal   = new PrimitiveFunctor<>(null, Spacing.class);
        this.marginTop          = new PrimitiveFunctor<>(null, Spacing.class);
        this.marginBottom       = new PrimitiveFunctor<>(null, Spacing.class);
        this.background         = new PrimitiveFunctor<>(null, BackgroundColor.class);
        this.corners            = new PrimitiveFunctor<>(null, Corners.class);
        this.nameStyle          = ModelFunctor.empty(TextStyle.class);
        this.dividerType        = new PrimitiveFunctor<>(null, DividerType.class);
        this.dividerPadding     = new PrimitiveFunctor<>(null, Spacing.class);
    }


    public GroupFormat(UUID id,
                       Spacing paddingTop,
                       Spacing paddingBottom,
                       Spacing marginHorizontal,
                       Spacing marginTop,
                       Spacing marginBottom,
                       BackgroundColor background,
                       Corners corners,
                       Boolean showName,
                       TextStyle nameStyle,
                       DividerType dividerType,
                       Spacing dividerPadding)
    {
        this.id                 = id;

        this.paddingTop         = new PrimitiveFunctor<>(paddingTop, Spacing.class);
        this.paddingBottom      = new PrimitiveFunctor<>(paddingBottom, Spacing.class);
        this.marginHorizontal   = new PrimitiveFunctor<>(marginHorizontal, Spacing.class);
        this.marginTop          = new PrimitiveFunctor<>(marginTop, Spacing.class);
        this.marginBottom       = new PrimitiveFunctor<>(marginBottom, Spacing.class);
        this.background         = new PrimitiveFunctor<>(background, BackgroundColor.class);
        this.corners            = new PrimitiveFunctor<>(corners, Corners.class);
        this.showName           = new PrimitiveFunctor<>(showName, Boolean.class);
        this.nameStyle          = ModelFunctor.full(nameStyle, TextStyle.class);
        this.dividerType        = new PrimitiveFunctor<>(dividerType, DividerType.class);
        this.dividerPadding     = new PrimitiveFunctor<>(dividerPadding, Spacing.class);

        this.setPaddingTop(paddingTop);
        this.setPaddingBottom(paddingBottom);
        this.setMarginHorizontal(marginHorizontal);
        this.setMarginBottom(marginBottom);
        this.setMarginTop(marginTop);
        this.setBackground(background);
        this.setCorners(corners);
        this.setShowName(showName);
        this.setNameStyle(nameStyle);
        this.setDividerType(dividerType);
        this.setDividerPadding(dividerPadding);
    }


    /**
     * Create a Group Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Group Format.
     * @throws YamlParseException
     */
    public static GroupFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID            id                = UUID.randomUUID();

        Spacing         spaceAbove        = Spacing.fromYaml(yaml.atMaybeKey("space_above"));
        Spacing         spaceBelow        = Spacing.fromYaml(yaml.atMaybeKey("space_below"));
        Spacing         paddingHorizontal = Spacing.fromYaml(yaml.atMaybeKey("padding_horizontal"));
        Spacing         marginTop         = Spacing.fromYaml(yaml.atMaybeKey("margin_top"));
        Spacing         marginBottom      = Spacing.fromYaml(yaml.atMaybeKey("margin_bottom"));
        BackgroundColor backgroundColor   = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));
        Corners         corners           = Corners.fromYaml(yaml.atMaybeKey("corners"));
        Boolean         showName          = yaml.atMaybeKey("show_name").getBoolean();
        TextStyle       nameStyle         = TextStyle.fromYaml(yaml.atMaybeKey("name_style"));
        DividerType     dividerType       = DividerType.fromYaml(yaml.atMaybeKey("divider"));
        Spacing         dividerPadding    = Spacing.fromYaml(yaml.atMaybeKey("divider_padding"));

        return new GroupFormat(id, spaceAbove, spaceBelow, paddingHorizontal, marginTop,
                               marginBottom, backgroundColor, corners, showName, nameStyle,
                               dividerType, dividerPadding);
    }


    /**
     * A Group Format with default values.
     * @return The default Group Format.
     */
    public static GroupFormat asDefault()
    {
        GroupFormat format = new GroupFormat();

        format.setId(UUID.randomUUID());

        format.setPaddingTop(null);
        format.setPaddingBottom(null);
        format.setMarginHorizontal(null);
        format.setMarginTop(null);
        format.setMarginBottom(null);
        format.setBackground(null);
        format.setCorners(null);
        format.setShowName(null);
        format.setNameStyle(null);
        format.setDividerType(null);
        format.setDividerPadding(null);

        return format;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // --------------------------------------------------------------------------------------

    // ** Id
    // --------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // --------------------------------------------------------------------------------------

    /**
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // --------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("space_above", this.spaceAbove())
                .putYaml("space_below", this.spaceBelow())
                .putYaml("padding_horizontal", this.paddingHorizontal())
                .putYaml("margin_bottom", this.marginBottom())
                .putYaml("background", this.background())
                .putYaml("corners", this.corners())
                .putBoolean("show_name", this.showName())
                .putYaml("name_style", this.nameStyle())
                .putYaml("divider", this.dividerType())
                .putYaml("divider_padding", this.dividerPadding());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Space Above
    // ------------------------------------------------------------------------------------------

    /**
     * The space above the group on the sheet.
     * @return The Space Above.
     */
    public Spacing spaceAbove()
    {
        return this.paddingTop.getValue();
    }


    public void setPaddingTop(Spacing paddingTop)
    {
        if (paddingTop != null)
            this.paddingTop.setValue(paddingTop);
        else
            this.paddingTop.setValue(Spacing.SMALL);
    }


    // ** Space Below
    // ------------------------------------------------------------------------------------------

    /**
     * The space at the bottom of the group.
     * @return The Space Above.
     */
    public Spacing spaceBelow()
    {
        return this.paddingBottom.getValue();
    }


    public void setPaddingBottom(Spacing spacing)
    {
        if (spacing != null)
            this.paddingBottom.setValue(spacing);
        else
            this.paddingBottom.setValue(Spacing.SMALL);
    }


    // ** Padding Horizontal
    // ------------------------------------------------------------------------------------------

    /**
     * The space to the left and right of the group.
     * @return The padding.
     */
    public Spacing paddingHorizontal()
    {
        return this.marginHorizontal.getValue();
    }


    public void setMarginHorizontal(Spacing padding)
    {
        if (padding != null)
            this.marginHorizontal.setValue(padding);
        else
            this.marginHorizontal.setValue(Spacing.NONE);
    }


    // ** Margin Top
    // ------------------------------------------------------------------------------------------

    /**
     * The top margin.
     * @return The margin.
     */
    public Spacing marginTop()
    {
        return this.marginTop.getValue();
    }


    public void setMarginTop(Spacing margin)
    {
        if (margin != null)
            this.marginTop.setValue(margin);
        else
            this.marginTop.setValue(Spacing.NONE);
    }


    // ** Margin Bottom
    // ------------------------------------------------------------------------------------------

    /**
     * The bottom margin.
     * @return The margin..
     */
    public Spacing marginBottom()
    {
        return this.marginBottom.getValue();
    }


    public void setMarginBottom(Spacing margin)
    {
        if (margin != null)
            this.marginBottom.setValue(margin);
        else
            this.marginBottom.setValue(Spacing.NONE);
    }


    // ** Group Background
    // ------------------------------------------------------------------------------------------

    /**
     * The group background color.
     * @return The Group Background.
     */
    public BackgroundColor background()
    {
        return this.background.getValue();
    }


    public void setBackground(BackgroundColor background)
    {
        if (background != null)
            this.background.setValue(background);
        else
            this.background.setValue(BackgroundColor.MEDIUM);
    }


    // ** Corners
    // ------------------------------------------------------------------------------------------

    /**
     * The backround corner size.
     * @return The Group Background.
     */
    public Corners corners()
    {
        return this.corners.getValue();
    }


    /**
     * Set the corners. If null, defaults to NONE (no corners).
     * @param corners The corners.
     */
    public void setCorners(Corners corners)
    {
        if (corners != null)
            this.corners.setValue(corners);
        else
            this.corners.setValue(Corners.NONE);
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


    // ** Name Style
    // ------------------------------------------------------------------------------------------

    /**
     * The group name style.
     * @return The name Text Style.
     */
    public TextStyle nameStyle()
    {
        return this.nameStyle.getValue();
    }


    /**
     * Set the group name style. If null, sets a default style.
     * @param nameStyle The group name Text Style.
     */
    public void setNameStyle(TextStyle nameStyle)
    {
        if (nameStyle != null) {
            this.nameStyle.setValue(nameStyle);
        }
        else {
            TextStyle defaultNameStyle = new TextStyle(UUID.randomUUID(),
                                                       TextColor.GOLD_LIGHT,
                                                       TextSize.MEDIUM,
                                                       TextFont.BOLD,
                                                       Alignment.LEFT);
            this.nameStyle.setValue(defaultNameStyle);
        }
    }


    // ** Divider Type
    // ------------------------------------------------------------------------------------------

    /**
     * The type divider at the bottom of the group.
     * @return Thd divider type.
     */
    public DividerType dividerType()
    {
        return this.dividerType.getValue();
    }


    /**
     * Set the group divider type. If null, it defaults to NONE (no divider).
     * @param dividerType The divider type.
     */
    public void setDividerType(DividerType dividerType)
    {
        if (dividerType != null)
            this.dividerType.setValue(dividerType);
        else
            this.dividerType.setValue(DividerType.NONE);
    }


    // ** Divider Padding
    // ------------------------------------------------------------------------------------------

    /**
     * The divider padding.
     * @return The divider padding.
     */
    public Spacing dividerPadding()
    {
        return this.dividerPadding.getValue();
    }

    public void setDividerPadding(Spacing padding)
    {
        if (padding != null)
            this.dividerPadding.setValue(padding);
        else
            this.dividerPadding.setValue(Spacing.NONE);
    }


}
