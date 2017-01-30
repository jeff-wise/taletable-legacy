
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Widget Format
 */
public class WidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private UUID                                        id;

    private PrimitiveFunctor<String>                    name;
    private PrimitiveFunctor<String>                    label;
    private PrimitiveFunctor<Integer>                   width;
    private PrimitiveFunctor<WidgetContentAlignment>    alignment;
    private PrimitiveFunctor<WidgetBackground>          background;
    private PrimitiveFunctor<WidgetCorners>             corners;
    private PrimitiveFunctor<Boolean>                   isBold;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public WidgetFormat()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.label      = new PrimitiveFunctor<>(null, String.class);
        this.width      = new PrimitiveFunctor<>(null, Integer.class);
        this.alignment  = new PrimitiveFunctor<>(null, WidgetContentAlignment.class);
        this.background = new PrimitiveFunctor<>(null, WidgetBackground.class);
        this.corners    = new PrimitiveFunctor<>(null, WidgetCorners.class);
        this.isBold     = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public WidgetFormat(UUID id,
                        String name,
                        String label,
                        Integer width,
                        WidgetContentAlignment alignment,
                        WidgetBackground background,
                        WidgetCorners corners,
                        Boolean isBold)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.label      = new PrimitiveFunctor<>(label, String.class);
        this.width      = new PrimitiveFunctor<>(width, Integer.class);
        this.alignment  = new PrimitiveFunctor<>(alignment, WidgetContentAlignment.class);
        this.background = new PrimitiveFunctor<>(background, WidgetBackground.class);
        this.corners    = new PrimitiveFunctor<>(corners, WidgetCorners.class);
        this.isBold     = new PrimitiveFunctor<>(isBold, Boolean.class);

        this.setName(name);
        this.setWidth(width);
        this.setAlignment(alignment);
        this.setBackground(background);
        this.setCorners(corners);
        this.setIsBold(isBold);
    }


    /**
     * Create a widget format with default values.
     * @return The WidgetFormat
     */
    public static WidgetFormat asDefault()
    {
        WidgetFormat defaultFormat = new WidgetFormat();

        defaultFormat.setId(UUID.randomUUID());
        defaultFormat.setWidth(null);
        defaultFormat.setAlignment(null);
        defaultFormat.setBackground(null);
        defaultFormat.setCorners(null);
        defaultFormat.setIsBold(null);

        return defaultFormat;
    }


    /**
     * Create a WidgetFormat object from its yaml representation.
     * @param yaml The yaml parser at the format node.
     * @return The parsed WidgetFormat object.
     */
    @SuppressWarnings("unchecked")
    protected static WidgetFormat fromYaml(YamlParser yaml)
                     throws YamlParseException
    {
        if (yaml.isNull())
            return WidgetFormat.asDefault();

        UUID                   id         = UUID.randomUUID();

        String                 name       = yaml.atMaybeKey("name").getString();
        String                 label      = yaml.atMaybeKey("label").getString();

        if (label != null)
            label = label.trim();

        Integer                width      = yaml.atMaybeKey("width").getInteger();
        WidgetContentAlignment alignment  = WidgetContentAlignment.fromYaml(
                                                                yaml.atMaybeKey("alignment"));
        WidgetBackground       background = WidgetBackground.fromYaml(
                                                                yaml.atMaybeKey("background"));
        WidgetCorners          corners    = WidgetCorners.fromYaml(yaml.atMaybeKey("corners"));
        Boolean                isBold     = yaml.atMaybeKey("bold").getBoolean();

        return new WidgetFormat(id, name, label, width, alignment, background, corners, isBold);
    }


    // API
    // --------------------------------------------------------------------------------------

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
     * This method is called when the Widget Format is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // --------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putString("name", this.name());
        yaml.putString("label", this.label());
        yaml.putInteger("width", this.width());
        yaml.putYaml("alignment", this.alignment());
        yaml.putYaml("background", this.background());
        yaml.putYaml("corners", this.corners());
        yaml.putBoolean("bold", this.isBold());

        return yaml;
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Name
    // --------------------------------------------------------------------------------------

    /**
     * The Widget's name. May not be null.
     * @return The widget name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    public void setName(String name)
    {
        if (name != null)
            this.name.setValue(name);
        else
            this.name.setValue("");
    }


    // ** Label
    // --------------------------------------------------------------------------------------

    /**
     * Get the component label, used to identify the component in the user interface.
     * @return The component label string.
     */
    public String label()
    {
        return this.label.getValue();
    }


    // ** Width
    // --------------------------------------------------------------------------------------

    /**
     * Get the component width. The width of the component is a relative value that determines how
     * much space the component takes up horizontally in the group row.
     * @return The component width.
     */
    public Integer width()
    {
        return this.width.getValue();
    }


    /**
     * Set the component width. Defaults to 1.
     * @param width
     */
    public void setWidth(Integer width)
    {
        if (width != null)
            this.width.setValue(width);
        else
            this.width.setValue(1);
    }


    // ** Alignment
    // --------------------------------------------------------------------------------------

    /**
     * Get the component's alignment. The alignment determines how the component's display
     * data is positioned within the component's area.
     * @return The component's alignment.
     */
    public WidgetContentAlignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the components alignment within its box. Defaults to CENTER alignment.
     * @param alignment
     */
    public void setAlignment(WidgetContentAlignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(WidgetContentAlignment.CENTER);
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The widget's background. Determines how dark or light (or transparent) the widget's
     * background will be.
     * @return The widget background value.
     */
    public WidgetBackground background()
    {
        return this.background.getValue();
    }


    public void setBackground(WidgetBackground background)
    {
        if (background != null)
            this.background.setValue(background);
        else
            this.background.setValue(WidgetBackground.DARK);
    }


    // ** Corners
    // --------------------------------------------------------------------------------------

    /**
     * The widget's corner radius.
     * @return The Widget Corners.
     */
    public WidgetCorners corners()
    {
        return this.corners.getValue();
    }


    /**
     * The set widget corner radius.
     * @param corners The widget corners.
     */
    public void setCorners(WidgetCorners corners)
    {
        if (corners != null)
            this.corners.setValue(corners);
        else
            this.corners.setValue(WidgetCorners.SMALL);
    }


    // ** Is Bold
    // --------------------------------------------------------------------------------------

    /**
     * True if the widget's text is BOLD.
     * @return True if the widget is bold.
     */
    public Boolean isBold()
    {
        return this.isBold.getValue();
    }


    public void setIsBold(Boolean isBold)
    {
        if (isBold != null)
            this.isBold.setValue(isBold);
        else
            this.isBold.setValue(false);
    }
}

