
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Group Row Format
 */
public class GroupRowFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<Alignment>         alignment;
    private PrimitiveFunctor<BackgroundColor>   backgroundColor;

    private ModelFunctor<Spacing>               margins;
    private ModelFunctor<Spacing>               padding;

    private PrimitiveFunctor<DividerType>       dividerType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupRowFormat()
    {
        this.id                 = null;

        this.alignment          = new PrimitiveFunctor<>(null, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(null, BackgroundColor.class);

        this.margins            = ModelFunctor.empty(Spacing.class);
        this.padding            = ModelFunctor.empty(Spacing.class);

        this.dividerType        = new PrimitiveFunctor<>(null, DividerType.class);
    }


    public GroupRowFormat(UUID id,
                          Alignment alignment,
                          BackgroundColor backgroundColor,
                          Spacing margins,
                          Spacing padding,
                          DividerType dividerType)
    {
        this.id                 = id;

        this.alignment          = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(backgroundColor, BackgroundColor.class);

        this.margins            = ModelFunctor.full(margins, Spacing.class);
        this.padding            = ModelFunctor.full(padding, Spacing.class);

        this.dividerType        = new PrimitiveFunctor<>(dividerType, DividerType.class);

        this.setAlignment(alignment);
        this.setBackgroundColor(backgroundColor);

        this.setMargins(margins);
        this.setPadding(padding);

        this.setDividerType(dividerType);
    }


    /**
     * Create a Group Row Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Group Row Format.
     * @throws YamlParseException
     */
    public static GroupRowFormat fromyaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return GroupRowFormat.asDefault();

        UUID            id                = UUID.randomUUID();

        Alignment       alignment         = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        BackgroundColor backgroundColor   = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));

        Spacing         margins           = Spacing.fromYaml(yaml.atMaybeKey("margins"));
        Spacing         padding           = Spacing.fromYaml(yaml.atMaybeKey("padding"));

        DividerType     dividerType       = DividerType.fromYaml(yaml.atMaybeKey("divider"));

        return new GroupRowFormat(id, alignment, backgroundColor, margins, padding, dividerType);
    }


    /**
     * Create a Group Row Format with default values.
     * @return The default Group Row Format.
     */
    private static GroupRowFormat asDefault()
    {
        GroupRowFormat format = new GroupRowFormat();

        format.setId(UUID.randomUUID());

        format.setAlignment(null);
        format.setBackgroundColor(null);

        format.setMargins(null);
        format.setPadding(null);

        format.setDividerType(null);

        return format;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    // ** Id
    // -----------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // -----------------------------------------------------------------------------------------

    /**
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // -----------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("alignment", this.alignment())
                .putYaml("background", this.backgroundColor())
                .putYaml("margins", this.margins())
                .putYaml("padding", this.padding())
                .putYaml("divider", this.dividerType());
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Alignment
    // -----------------------------------------------------------------------------------------

    /**
     * The alignment.
     * @return The alignment.
     */
    public Alignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the alignment. If null, defaults to CENTER.
     * @param alignment The alignment.
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(Alignment.CENTER);
    }


    // ** Background Color
    // -----------------------------------------------------------------------------------------

    /**
     * The background color
     * @return The background color
     */
    public BackgroundColor backgroundColor()
    {
        return this.backgroundColor.getValue();
    }


    /**
     * Set the background color. If null, defaults to NONE (transparent).
     * @param backgroundColor The background color.
     */
    public void setBackgroundColor(BackgroundColor backgroundColor)
    {
        if (backgroundColor != null)
            this.backgroundColor.setValue(backgroundColor);
        else
            this.backgroundColor.setValue(BackgroundColor.EMPTY);
    }


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


    // ** Divider Type
    // ------------------------------------------------------------------------------------------

    /**
     * The divider type.
     * @return The divider type.
     */
    public DividerType dividerType()
    {
        return this.dividerType.getValue();
    }


    /**
     * Set the divider type. If null, defaults to NONE (no divider).
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
