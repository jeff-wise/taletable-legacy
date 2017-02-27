
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
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

    private UUID                                id;

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<String>            label;
    private PrimitiveFunctor<Integer>           width;
    private PrimitiveFunctor<Alignment>         alignment;
    private ModelFunctor<TextStyle>             labelStyle;
    private PrimitiveFunctor<Background>  background;
    private PrimitiveFunctor<WidgetCorners>     corners;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public WidgetFormat()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.width          = new PrimitiveFunctor<>(null, Integer.class);
        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
        this.labelStyle     = ModelFunctor.empty(TextStyle.class);
        this.background     = new PrimitiveFunctor<>(null, Background.class);
        this.corners        = new PrimitiveFunctor<>(null, WidgetCorners.class);
    }


    public WidgetFormat(UUID id,
                        String name,
                        String label,
                        Integer width,
                        Alignment alignment,
                        TextStyle labelStyle,
                        Background background,
                        WidgetCorners corners)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.width          = new PrimitiveFunctor<>(width, Integer.class);
        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.labelStyle     = ModelFunctor.full(labelStyle, TextStyle.class);
        this.background     = new PrimitiveFunctor<>(background, Background.class);
        this.corners        = new PrimitiveFunctor<>(corners, WidgetCorners.class);
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
        defaultFormat.setLabelStyle(null);
        defaultFormat.setBackground(null);
        defaultFormat.setCorners(null);

        return defaultFormat;
    }


    /**
     * Create a WidgetFormat object from its yaml representation.
     * @param yaml The yaml parser at the format node.
     * @return The parsed WidgetFormat object.
     */
    @SuppressWarnings("unchecked")
    protected static WidgetFormat fromYaml(YamlParser yaml, boolean useDefault)
                     throws YamlParseException
    {
        if (yaml.isNull() && useDefault)
            return WidgetFormat.asDefault();
        else if (yaml.isNull())
            return new WidgetFormat();

        UUID                id             = UUID.randomUUID();

        String              name           = yaml.atMaybeKey("name").getString();
        String              label          = yaml.atMaybeKey("label").getTrimmedString();

        Integer             width          = yaml.atMaybeKey("width").getInteger();
        Alignment           alignment      = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        TextStyle           labelStyle     = TextStyle.fromYaml(
                                                            yaml.atMaybeKey("label_style"),
                                                            false);
        Background background     = Background.fromYaml(
                                                                yaml.atMaybeKey("background"));
        WidgetCorners       corners        = WidgetCorners.fromYaml(yaml.atMaybeKey("corners"));

        return new WidgetFormat(id, name, label, width, alignment, labelStyle, background, corners);
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
        yaml.putYaml("label_style", this.labelStyle());
        yaml.putYaml("background", this.background());
        yaml.putYaml("corners", this.corners());

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
    public Alignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the components alignment within its box. Defaults to CENTER alignment.
     * @param alignment
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(Alignment.CENTER);
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The widget's background. Determines how dark or light (or transparent) the widget's
     * background will be.
     * @return The widget background value.
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
            this.background.setValue(Background.DARK);
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


    // ** Label Style
    // --------------------------------------------------------------------------------------

    /**
     * The label style.
     * @return The label style.
     */
    public TextStyle labelStyle()
    {
        return this.labelStyle.getValue();
    }


    /**
     * Set the label style.
     * @param labelStyle The label style.
     */
    public void setLabelStyle(TextStyle labelStyle)
    {
        if (labelStyle != null) {
            this.labelStyle.setValue(labelStyle);
        }
        else {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_DARK,
                                                        TextSize.VERY_SMALL);
            this.labelStyle.setValue(defaultLabelStyle);
        }
    }

}

