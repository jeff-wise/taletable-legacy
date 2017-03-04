
package com.kispoko.tome.sheet.group;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

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

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<Alignment> alignment;
    private PrimitiveFunctor<RowWidth>  width;
    private PrimitiveFunctor<Spacing>   marginTop;
    private PrimitiveFunctor<Spacing>   marginBottom;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GroupRowFormat()
    {
        this.id             = null;

        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
        this.width          = new PrimitiveFunctor<>(null, RowWidth.class);
        this.marginTop      = new PrimitiveFunctor<>(null, Spacing.class);
        this.marginBottom   = new PrimitiveFunctor<>(null, Spacing.class);
    }


    public GroupRowFormat(UUID id,
                          Alignment alignment,
                          RowWidth width,
                          Spacing marginTop,
                          Spacing marginBottom)
    {
        this.id             = id;

        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.width          = new PrimitiveFunctor<>(width, RowWidth.class);
        this.marginTop      = new PrimitiveFunctor<>(marginTop, Spacing.class);
        this.marginBottom   = new PrimitiveFunctor<>(marginBottom, Spacing.class);

        this.setAlignment(alignment);
        this.setWidth(width);
        this.setMarginTop(marginTop);
        this.setMarginBottom(marginBottom);
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

        UUID      id         = UUID.randomUUID();

        Alignment alignment  = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        RowWidth  width      = RowWidth.fromYaml(yaml.atMaybeKey("width"));
        Spacing   spaceAbove = Spacing.fromYaml(yaml.atMaybeKey("margin_top"));
        Spacing   spaceBelow = Spacing.fromYaml(yaml.atMaybeKey("margin_bottom"));

        return new GroupRowFormat(id, alignment, width, spaceAbove, spaceBelow);
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
        format.setWidth(null);
        format.setMarginTop(null);
        format.setMarginBottom(null);

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
                .putYaml("width", this.width())
                .putYaml("margin_top", this.marginTop())
                .putYaml("margin_bottom", this.marginBottom());
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


    // ** Width
    // ------------------------------------------------------------------------------------------

    /**
     * The row width.
     * @return The width.
     */
    public RowWidth width()
    {
        return this.width.getValue();
    }


    /**
     * Set the row width. If null, defaults to FULL width.
     * @param width The width.
     */
    public void setWidth(RowWidth width)
    {
        if (width != null)
            this.width.setValue(width);
        else
            this.width.setValue(RowWidth.FULL);
    }


    // ** Space Above
    // ------------------------------------------------------------------------------------------

    /**
     * The space above the row.
     * @return The spacing.
     */
    public Spacing marginTop()
    {
        return this.marginTop.getValue();
    }


    /**
     * Set the space above. If null, defaults to MEDIUM spacing.
     * @param marginTop The space above.
     */
    public void setMarginTop(Spacing marginTop)
    {
        if (marginTop != null)
            this.marginTop.setValue(marginTop);
        else
            this.marginTop.setValue(Spacing.MEDIUM);
    }


    // ** Space Below
    // ------------------------------------------------------------------------------------------

    /**
     * The space below the row.
     * @return The spacing.
     */
    public Spacing marginBottom()
    {
        return this.marginBottom.getValue();
    }


    /**
     * Set the space below. If null, defaults to MEDIUM spacing.
     * @param marginBottom The space below.
     */
    public void setMarginBottom(Spacing marginBottom)
    {
        if (marginBottom != null)
            this.marginBottom.setValue(marginBottom);
        else
            this.marginBottom.setValue(Spacing.MEDIUM);
    }


}
