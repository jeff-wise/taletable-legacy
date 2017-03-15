
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.functor.PrimitiveFunctor;
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
     * The type of divider at the bottom of the group.
     */
    private PrimitiveFunctor<DividerType>       dividerType;

    /**
     * The space between the divider end and the page boundary.
     */
    private PrimitiveFunctor<Integer>           dividerPadding;

    /**
     * The thickness of the divider (in dp).
     */
    private PrimitiveFunctor<Integer>           dividerThickness;



    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupFormat()
    {
        this.id                 = null;

        this.background         = new PrimitiveFunctor<>(null, BackgroundColor.class);
        this.corners            = new PrimitiveFunctor<>(null, Corners.class);

        this.margins            = ModelFunctor.empty(Spacing.class);
        this.padding            = ModelFunctor.empty(Spacing.class);

        this.dividerType        = new PrimitiveFunctor<>(null, DividerType.class);
        this.dividerPadding     = new PrimitiveFunctor<>(null, Integer.class);
        this.dividerThickness   = new PrimitiveFunctor<>(null, Integer.class);
    }


    public GroupFormat(UUID id,
                       BackgroundColor background,
                       Corners corners,
                       Spacing margins,
                       Spacing padding,
                       DividerType dividerType,
                       Integer dividerPadding,
                       Integer dividerThickness)
    {
        this.id                 = id;

        this.background         = new PrimitiveFunctor<>(background, BackgroundColor.class);
        this.corners            = new PrimitiveFunctor<>(corners, Corners.class);

        this.margins            = ModelFunctor.full(margins, Spacing.class);
        this.padding            = ModelFunctor.full(padding, Spacing.class);

        this.dividerType        = new PrimitiveFunctor<>(dividerType, DividerType.class);
        this.dividerPadding     = new PrimitiveFunctor<>(dividerPadding, Integer.class);
        this.dividerThickness   = new PrimitiveFunctor<>(dividerThickness, Integer.class);

        this.setBackground(background);
        this.setCorners(corners);

        this.setMargins(margins);
        this.setPadding(padding);

        this.setDividerType(dividerType);
        this.setDividerPadding(dividerPadding);
        this.setDividerThickness(dividerThickness);
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

        BackgroundColor backgroundColor   = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));
        Corners         corners           = Corners.fromYaml(yaml.atMaybeKey("corners"));

        Spacing         margins           = Spacing.fromYaml(yaml.atMaybeKey("margins"));
        Spacing         padding           = Spacing.fromYaml(yaml.atMaybeKey("padding"));

        DividerType     dividerType       = DividerType.fromYaml(yaml.atMaybeKey("divider"));
        Integer         dividerPadding    = yaml.atMaybeKey("divider_padding").getInteger();
        Integer         dividerThickness  = yaml.atMaybeKey("divider_thickness").getInteger();

        return new GroupFormat(id, backgroundColor, corners, margins, padding,
                               dividerType, dividerPadding, dividerThickness);
    }


    /**
     * A Group Format with default values.
     * @return The default Group Format.
     */
    public static GroupFormat asDefault()
    {
        GroupFormat format = new GroupFormat();

        format.setId(UUID.randomUUID());

        format.setBackground(null);
        format.setCorners(null);

        format.setMargins(null);
        format.setPadding(null);

        format.setDividerType(null);
        format.setDividerPadding(null);
        format.setDividerThickness(null);

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
                .putYaml("background", this.background())
                .putYaml("corners", this.corners())
                .putYaml("margins", this.margins())
                .putYaml("padding", this.padding())
                .putYaml("divider", this.dividerType())
                .putInteger("divider_padding", this.dividerPadding())
                .putInteger("divider_thickness", this.dividerThickness());
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


    /**
     * Set the divider padding (horizontal).
     * @param padding The padding.
     */
    public void setDividerPadding(Integer padding)
    {
        if (padding != null)
            this.dividerPadding.setValue(padding);
        else
            this.dividerPadding.setValue(0);
    }


    // ** Divider Thickness
    // ------------------------------------------------------------------------------------------

    /**
     * The divider thickness.
     * @return The divider thickness.
     */
    public Integer dividerThickness()
    {
        return this.dividerThickness.getValue();
    }


    public void setDividerThickness(Integer thickness)
    {
        if (thickness == null)
            this.dividerThickness.setValue(1);
        else if (thickness > 30)
            this.dividerThickness.setValue(30);
        else
            this.dividerThickness.setValue(thickness);

    }


}
