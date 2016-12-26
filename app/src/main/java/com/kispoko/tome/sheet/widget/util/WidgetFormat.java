
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Widget Format
 */
public class WidgetFormat implements Model, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private UUID                                   id;

    private PrimitiveFunctor<String> label;
    private PrimitiveFunctor<Integer> width;
    private PrimitiveFunctor<WidgetContentAlignment> alignment;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public WidgetFormat()
    {
        this.id        = null;

        this.label     = new PrimitiveFunctor<>(null, String.class);
        this.width     = new PrimitiveFunctor<>(null, Integer.class);
        this.alignment = new PrimitiveFunctor<>(null, WidgetContentAlignment.class);

    }


    public WidgetFormat(UUID id,
                        String label,
                        Integer width,
                        WidgetContentAlignment alignment)
    {
        this.id = id;

        this.label     = new PrimitiveFunctor<>(label, String.class);
        this.width     = new PrimitiveFunctor<>(width, Integer.class);
        this.alignment = new PrimitiveFunctor<>(alignment, WidgetContentAlignment.class);

        this.setLabel(label);
        this.setWidth(width);
        this.setAlignment(alignment);
    }


    /**
     * Create a widget format with default values.
     * @return The WidgetFormat
     */
    public static WidgetFormat asDefault()
    {
        WidgetFormat defaultFormat = new WidgetFormat();

        defaultFormat.setId(UUID.randomUUID());
        defaultFormat.setLabel(null);
        defaultFormat.setWidth(null);
        defaultFormat.setAlignment(null);

        return defaultFormat;
    }


    /**
     * Create a WidgetFormat object from its yaml representation.
     * @param yaml The yaml parser at the format node.
     * @return The parsed WidgetFormat object.
     */
    @SuppressWarnings("unchecked")
    protected static WidgetFormat fromYaml(Yaml yaml)
                     throws YamlException
    {
        if (yaml.isNull())
            return WidgetFormat.asDefault();

        UUID                   id        = UUID.randomUUID();

        String                 label     = yaml.atMaybeKey("label").getString();
        Integer                width     = yaml.atMaybeKey("width").getInteger();
        WidgetContentAlignment alignment = WidgetContentAlignment.fromYaml(
                                                                yaml.atMaybeKey("alignment"));

        return new WidgetFormat(id, label, width, alignment);
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


    // > State
    // --------------------------------------------------------------------------------------

    // ** Label
    // --------------------------------------------------------------------------------------

    /**
     * Get the component label, used to identify the component in the user interface.
     * @return The component label string.
     */
    public String getLabel()
    {
        return this.label.getValue();
    }


    /**
     * Set the component's label. Defaults to empty string.
     * @param label The component's display label.
     */
    public void setLabel(String label)
    {
        this.label.setValue(label);
    }


    // ** Width
    // --------------------------------------------------------------------------------------

    /**
     * Get the component width. The width of the component is a relative value that determines how
     * much space the component takes up horizontally in the group row.
     * @return The component width.
     */
    public Integer getWidth()
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
    public WidgetContentAlignment getAlignment()
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

}

