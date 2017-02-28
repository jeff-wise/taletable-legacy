
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.Background;
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

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The padding at the top of the group.
     */
    private PrimitiveFunctor<Spacing>       spaceAbove;

    /**
     * The padding at the bottom of the group.
     */
    private PrimitiveFunctor<Spacing>       spaceBelow;

    /**
     * The background color of the group.
     */
    private PrimitiveFunctor<Background>    background;

    /**
     * If true, displays the group name above its content.
     */
    private PrimitiveFunctor<Boolean>       showName;

    /**
     * The text style of the groups name.
     */
    private ModelFunctor<TextStyle>         nameStyle;

    /**
     * The type of divider at the bottom of the group.
     */
    private PrimitiveFunctor<DividerType>   dividerType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupFormat()
    {
        this.id             = null;

        this.showName       = new PrimitiveFunctor<>(null, Boolean.class);
        this.spaceAbove     = new PrimitiveFunctor<>(null, Spacing.class);
        this.spaceBelow     = new PrimitiveFunctor<>(null, Spacing.class);
        this.background     = new PrimitiveFunctor<>(null, Background.class);
        this.nameStyle      = ModelFunctor.empty(TextStyle.class);
        this.dividerType    = new PrimitiveFunctor<>(null, DividerType.class);
    }


    public GroupFormat(UUID id,
                       Spacing spaceAbove,
                       Spacing spaceBelow,
                       Background background,
                       Boolean showName,
                       TextStyle nameStyle,
                       DividerType dividerType)
    {
        this.id             = id;

        this.spaceAbove     = new PrimitiveFunctor<>(spaceAbove, Spacing.class);
        this.spaceBelow     = new PrimitiveFunctor<>(spaceBelow, Spacing.class);
        this.background     = new PrimitiveFunctor<>(background, Background.class);
        this.showName       = new PrimitiveFunctor<>(showName, Boolean.class);
        this.nameStyle      = ModelFunctor.full(nameStyle, TextStyle.class);
        this.dividerType    = new PrimitiveFunctor<>(dividerType, DividerType.class);

        this.setSpaceAbove(spaceAbove);
        this.setSpaceBelow(spaceBelow);
        this.setBackground(background);
        this.setShowName(showName);
        this.setNameStyle(nameStyle);
        this.setDividerType(dividerType);
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
        UUID        id          = UUID.randomUUID();

        Spacing     spaceAbove  = Spacing.fromYaml(yaml.atMaybeKey("space_above"));
        Spacing     spaceBelow  = Spacing.fromYaml(yaml.atMaybeKey("space_below"));
        Background  background  = Background.fromYaml(yaml.atMaybeKey("background"));
        Boolean     showName    = yaml.atMaybeKey("show_name").getBoolean();
        TextStyle   nameStyle   = TextStyle.fromYaml(yaml.atMaybeKey("name_style"), false);
        DividerType dividerType = DividerType.fromYaml(yaml.atMaybeKey("divider"));

        return new GroupFormat(id, spaceAbove, spaceBelow, background,
                               showName, nameStyle, dividerType);
    }


    /**
     * A Group Format with default values.
     * @return The default Group Format.
     */
    public static GroupFormat asDefault()
    {
        GroupFormat format = new GroupFormat();

        format.setId(UUID.randomUUID());

        format.setSpaceAbove(null);
        format.setSpaceBelow(null);
        format.setBackground(null);
        format.setShowName(null);
        format.setNameStyle(null);
        format.setDividerType(null);

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
                .putYaml("background", this.background())
                .putBoolean("show_name", this.showName())
                .putYaml("name_style", this.nameStyle())
                .putYaml("divider", this.dividerType());
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
    public Background background()
    {
        return this.background.getValue();
    }


    public void setBackground(Background background)
    {
        if (background != null)
            this.background.setValue(background);
        else
            this.background.setValue(Background.MEDIUM);
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
                                                       TextColor.GOLD_VERY_LIGHT,
                                                       TextSize.MEDIUM,
                                                       TextFont.BOLD,
                                                       false,
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


}
