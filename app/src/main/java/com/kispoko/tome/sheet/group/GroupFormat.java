
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.Spacing;
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
     * The background color of the group.
     */
    private PrimitiveFunctor<BackgroundColor>   background;

    /**
     * The group margins.
     */
    private ModelFunctor<Spacing>               margins;

    /**
     * The group padding.
     */
    private ModelFunctor<Spacing>               padding;

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
    private PrimitiveFunctor<Integer>           dividerPadding;



    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupFormat()
    {
        this.id                 = null;

        this.showName           = new PrimitiveFunctor<>(null, Boolean.class);
        this.background         = new PrimitiveFunctor<>(null, BackgroundColor.class);
        this.margins            = ModelFunctor.empty(Spacing.class);
        this.padding            = ModelFunctor.empty(Spacing.class);
        this.corners            = new PrimitiveFunctor<>(null, Corners.class);
        this.nameStyle          = ModelFunctor.empty(TextStyle.class);
        this.dividerType        = new PrimitiveFunctor<>(null, DividerType.class);
        this.dividerPadding     = new PrimitiveFunctor<>(null, Integer.class);
    }


    public GroupFormat(UUID id,
                       Spacing margins,
                       Spacing padding,
                       BackgroundColor background,
                       Corners corners,
                       Boolean showName,
                       TextStyle nameStyle,
                       DividerType dividerType,
                       Integer dividerPadding)
    {
        this.id                 = id;

        this.margins            = ModelFunctor.full(margins, Spacing.class);
        this.padding            = ModelFunctor.full(padding, Spacing.class);
        this.background         = new PrimitiveFunctor<>(background, BackgroundColor.class);
        this.corners            = new PrimitiveFunctor<>(corners, Corners.class);
        this.showName           = new PrimitiveFunctor<>(showName, Boolean.class);
        this.nameStyle          = ModelFunctor.full(nameStyle, TextStyle.class);
        this.dividerType        = new PrimitiveFunctor<>(dividerType, DividerType.class);
        this.dividerPadding     = new PrimitiveFunctor<>(dividerPadding, Integer.class);

        this.setMargins(margins);
        this.setPadding(padding);
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
        if (yaml.isNull())
            return GroupFormat.asDefault();

        UUID            id                = UUID.randomUUID();

        Spacing         margins           = Spacing.fromYaml(yaml.atMaybeKey("margins"));
        Spacing         padding           = Spacing.fromYaml(yaml.atMaybeKey("padding"));
        BackgroundColor backgroundColor   = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));
        Corners         corners           = Corners.fromYaml(yaml.atMaybeKey("corners"));
        Boolean         showName          = yaml.atMaybeKey("show_name").getBoolean();
        TextStyle       nameStyle         = TextStyle.fromYaml(yaml.atMaybeKey("name_style"));
        DividerType     dividerType       = DividerType.fromYaml(yaml.atMaybeKey("divider"));
        Integer         dividerPadding    = yaml.atMaybeKey("divider_padding").getInteger();

        return new GroupFormat(id, margins, padding, backgroundColor, corners, showName, nameStyle,
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

        format.setMargins(null);
        format.setPadding(null);
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
                .putYaml("margins", this.margins())
                .putYaml("padding", this.padding())
                .putYaml("background", this.background())
                .putYaml("corners", this.corners())
                .putBoolean("show_name", this.showName())
                .putYaml("name_style", this.nameStyle())
                .putYaml("divider", this.dividerType())
                .putInteger("divider_padding", this.dividerPadding());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Margins
    // ------------------------------------------------------------------------------------------

    /**
     * The group row margins.
     * @return The margins.
     */
    public Spacing margins()
    {
        return this.margins.getValue();
    }


    /**
     * Set the group margins. If null, sets the default margins.
     * @param spacing The spacing.
     */
    public void setMargins(Spacing spacing)
    {
        if (spacing != null)
            this.margins.setValue(spacing);
        else
            this.margins.setValue(Spacing.asDefault());
    }


    // ** Padding
    // ------------------------------------------------------------------------------------------

    /**
     * The group row padding.
     * @return The padding.
     */
    public Spacing padding()
    {
        return this.padding.getValue();
    }


    /**
     * Set the group row padding. If null, sets the default padding.
     * @param spacing The spacing.
     */
    public void setPadding(Spacing spacing)
    {
        if (spacing != null)
            this.padding.setValue(spacing);
        else
            this.padding.setValue(Spacing.asDefault());
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
    public Integer dividerPadding()
    {
        return this.dividerPadding.getValue();
    }

    public void setDividerPadding(Integer padding)
    {
        if (padding != null)
            this.dividerPadding.setValue(padding);
        else
            this.dividerPadding.setValue(0);
    }


}
