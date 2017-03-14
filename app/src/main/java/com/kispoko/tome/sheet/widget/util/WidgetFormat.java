
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.UUID;



/**
 * Widget Format
 */
public class WidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<String>            label;
    private PrimitiveFunctor<Integer>           width;
    private PrimitiveFunctor<Alignment>         alignment;
    private ModelFunctor<TextStyle>             labelStyle;
    private PrimitiveFunctor<BackgroundColor>   background;
    private PrimitiveFunctor<Corners>           corners;
    private PrimitiveFunctor<Integer>           underlineThickness;
    private PrimitiveFunctor<TextColor>         underlineColor;
    private ModelFunctor<Spacing>               margins;
    private PrimitiveFunctor<Integer>           elevation;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public WidgetFormat()
    {
        this.id                 = null;

        this.name               = new PrimitiveFunctor<>(null, String.class);
        this.label              = new PrimitiveFunctor<>(null, String.class);
        this.width              = new PrimitiveFunctor<>(null, Integer.class);
        this.alignment          = new PrimitiveFunctor<>(null, Alignment.class);
        this.labelStyle         = ModelFunctor.empty(TextStyle.class);
        this.background         = new PrimitiveFunctor<>(null, BackgroundColor.class);
        this.corners            = new PrimitiveFunctor<>(null, Corners.class);
        this.underlineThickness = new PrimitiveFunctor<>(null, Integer.class);
        this.underlineColor     = new PrimitiveFunctor<>(null, TextColor.class);
        this.margins            = ModelFunctor.empty(Spacing.class);
        this.elevation          = new PrimitiveFunctor<>(null, Integer.class);
    }


    public WidgetFormat(UUID id,
                        String name,
                        String label,
                        Integer width,
                        Alignment alignment,
                        TextStyle labelStyle,
                        BackgroundColor background,
                        Corners corners,
                        Integer underlineThickness,
                        TextColor underlineColor,
                        Spacing margins,
                        Integer elevation)
    {
        this.id                 = id;

        this.name               = new PrimitiveFunctor<>(name, String.class);
        this.label              = new PrimitiveFunctor<>(label, String.class);
        this.width              = new PrimitiveFunctor<>(width, Integer.class);
        this.alignment          = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.labelStyle         = ModelFunctor.full(labelStyle, TextStyle.class);
        this.background         = new PrimitiveFunctor<>(background, BackgroundColor.class);
        this.corners            = new PrimitiveFunctor<>(corners, Corners.class);
        this.underlineThickness = new PrimitiveFunctor<>(underlineThickness, Integer.class);
        this.underlineColor     = new PrimitiveFunctor<>(underlineColor, TextColor.class);
        this.margins            = ModelFunctor.full(margins, Spacing.class);
        this.elevation          = new PrimitiveFunctor<>(elevation, Integer.class);

        this.setWidth(width);
        this.setAlignment(alignment);
        this.setLabelStyle(labelStyle);
        this.setBackground(background);
        this.setCorners(corners);
        this.setUnderlineThickness(underlineThickness);
        this.setUnderlineColor(underlineColor);
        this.setMargins(margins);
        this.setElevation(elevation);
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
        defaultFormat.setUnderlineThickness(null);
        defaultFormat.setUnderlineColor(null);
        defaultFormat.setMargins(null);
        defaultFormat.setElevation(null);

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

        UUID            id           = UUID.randomUUID();

        String          name         = yaml.atMaybeKey("name").getString();
        String          label        = yaml.atMaybeKey("label").getTrimmedString();

        Integer         width        = yaml.atMaybeKey("width").getInteger();
        Alignment       alignment    = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        TextStyle       labelStyle   = TextStyle.fromYaml(
                                                            yaml.atMaybeKey("label_style"),
                                                            false);
        BackgroundColor background   = BackgroundColor.fromYaml(
                                                                yaml.atMaybeKey("background"));
        Corners         corners      = Corners.fromYaml(yaml.atMaybeKey("corners"));
        Integer         ulThickness  = yaml.atMaybeKey("underline_thickness").getInteger();
        TextColor       ulColor      = TextColor.fromYaml(yaml.atMaybeKey("underline_color"));

        Spacing         margins      = Spacing.fromYaml(yaml.atMaybeKey("margins"));
        Integer         elevation    = yaml.atMaybeKey("elevation").getInteger();

        return new WidgetFormat(id, name, label, width, alignment, labelStyle, background, corners,
                                ulThickness, ulColor, margins, elevation);
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
        return  YamlBuilder.map()
                    .putString("name", this.name())
                    .putString("label", this.label())
                    .putInteger("width", this.width())
                    .putYaml("alignment", this.alignment())
                    .putYaml("label_style", this.labelStyle())
                    .putYaml("background", this.background())
                    .putYaml("corners", this.corners())
                    .putInteger("underline_thickness", this.underlineThickness())
                    .putYaml("underline_color", this.underlineColor())
                    .putYaml("margins", this.margins())
                    .putInteger("elevation", this.elevation());
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
     * True if the alignment value is a default value.
     * @return Is default alignment?
     */
    public boolean alignmentIsDefault()
    {
        return this.alignment.isDefault();
    }


    /**
     * Set the components alignment within its box. Defaults to CENTER alignment.
     * @param alignment The alignment
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null) {
            this.alignment.setValue(alignment);
            this.alignment.setIsDefault(false);
        }
        else {
            this.alignment.setValue(Alignment.CENTER);
            this.alignment.setIsDefault(true);
        }
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The widget's background. Determines how dark or light (or transparent) the widget's
     * background will be.
     * @return The widget background value.
     */
    public BackgroundColor background()
    {
        return this.background.getValue();
    }


    /**
     * True if the background value is a default.
     * @return Is default background?
     */
    public boolean backgroundIsDefault()
    {
        return this.background.isDefault();
    }


    /**
     * Set the background. If null, defaults to NONE.
     * @param background The background
     */
    public void setBackground(BackgroundColor background)
    {
        if (background != null) {
            this.background.setValue(background);
            this.background.setIsDefault(false);
        }
        else {
            this.background.setValue(BackgroundColor.NONE);
            this.background.setIsDefault(true);
        }
    }


    // ** Corners
    // --------------------------------------------------------------------------------------

    /**
     * The widget's corner radius.
     * @return The Widget Corners.
     */
    public Corners corners()
    {
        return this.corners.getValue();
    }


    /**
     * True if the corners value is a default value.
     * @return Is default corners?
     */
    public boolean cornersIsDefault()
    {
        return this.corners.isDefault();
    }


    /**
     * The set widget corner radius.
     * @param corners The widget corners.
     */
    public void setCorners(Corners corners)
    {
        if (corners != null) {
            this.corners.setValue(corners);
            this.corners.setIsDefault(false);
        }
        else {
            this.corners.setValue(Corners.SMALL);
            this.corners.setIsDefault(true);
        }
    }


    // ** Underline Thickness
    // --------------------------------------------------------------------------------------

    /**
     * The underline thickness.
     * @return The thickness
     */
    public Integer underlineThickness()
    {
        return this.underlineThickness.getValue();
    }


    /**
     * True if the underline thickness value is a default value.
     * @return Is default?
     */
    public Boolean underlineThicknessIsDefault()
    {
        return this.underlineThickness.isDefault();
    }


    /**
     * Set the underline thickness. If null, defaults to 0 (no underline).
     * @param underlineThickness The underline thickness.
     */
    public void setUnderlineThickness(Integer underlineThickness)
    {
        if (underlineThickness != null) {
            this.underlineThickness.setValue(underlineThickness);
            this.underlineThickness.setIsDefault(false);
        }
        else {
            this.underlineThickness.setValue(0);
            this.underlineThickness.setIsDefault(true);
        }
    }


    // ** Underline Color
    // --------------------------------------------------------------------------------------

    /**
     * The underline color.
     * @return the underline color.
     */
    public TextColor underlineColor()
    {
        return this.underlineColor.getValue();
    }


    /**
     * True if the underline color value is a default value.
     * @return Is default?
     */
    public boolean underlineColorIsDefault()
    {
        return this.underlineColor.isDefault();
    }


    public void setUnderlineColor(TextColor color)
    {
        if (color != null) {
            this.underlineColor.setValue(color);
            this.underlineColor.setIsDefault(false);
        }
        else {
            this.underlineColor.setValue(TextColor.THEME_MEDIUM);
            this.underlineColor.setIsDefault(true);
        }
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


    // ** Margins
    // ------------------------------------------------------------------------------------------

    /**
     * The widget margins.
     * @return The margins.
     */
    public Spacing margins()
    {
        return this.margins.getValue();
    }


    /**
     * Set the widget margins. If null, sets the default margins.
     * @param spacing The spacing.
     */
    public void setMargins(Spacing spacing)
    {
        if (spacing != null)
            this.margins.setValue(spacing);
        else
            this.margins.setValue(Spacing.asDefault());
    }


    // ** Elevation
    // ------------------------------------------------------------------------------------------

    /**
     * The widget elevation. May be null.
     * @return The elevation.
     */
    public Integer elevation()
    {
        return this.elevation.getValue();
    }


    /**
     * Set the widget elevation.
     * @param elevation The elevation
     */
    public void setElevation(Integer elevation)
    {
        this.elevation.setValue(elevation);
    }


}

