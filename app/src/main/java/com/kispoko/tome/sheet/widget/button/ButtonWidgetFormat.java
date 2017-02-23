
package com.kispoko.tome.sheet.widget.button;


import com.kispoko.tome.sheet.widget.util.TextColor;
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
 * Button Widget Format
 */
public class ButtonWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>         labelStyle;
    private ModelFunctor<TextStyle>         descriptionStyle;
    private PrimitiveFunctor<ButtonColor>   buttonColor;
    private PrimitiveFunctor<TextColor>     iconColor;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ButtonWidgetFormat()
    {
        this.id                 = null;

        this.labelStyle         = ModelFunctor.empty(TextStyle.class);
        this.descriptionStyle   = ModelFunctor.empty(TextStyle.class);
        this.buttonColor        = new PrimitiveFunctor<>(null, ButtonColor.class);
        this.iconColor          = new PrimitiveFunctor<>(null, TextColor.class);
    }


    public ButtonWidgetFormat(UUID id,
                              TextStyle labelStyle,
                              TextStyle descriptionStyle,
                              ButtonColor buttonColor,
                              TextColor iconColor)
    {
        this.id                 = id;

        this.labelStyle         = ModelFunctor.full(labelStyle, TextStyle.class);
        this.descriptionStyle   = ModelFunctor.full(descriptionStyle, TextStyle.class);
        this.buttonColor        = new PrimitiveFunctor<>(buttonColor, ButtonColor.class);
        this.iconColor          = new PrimitiveFunctor<>(iconColor, TextColor.class);

        // > Set defaults for null values
        this.setLabelStyle(labelStyle);
        this.setDescriptionStyle(descriptionStyle);
        this.setButtonColor(buttonColor);
        this.setIconColor(iconColor);
    }


    /**
     * Create a Button Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Button Widget Format.
     * @throws YamlParseException
     */
    public static ButtonWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return ButtonWidgetFormat.asDefault();

        UUID        id               = UUID.randomUUID();

        TextStyle   labelStyle       = TextStyle.fromYaml(yaml.atMaybeKey("label_style"), false);
        TextStyle   descriptionStyle = TextStyle.fromYaml(yaml.atMaybeKey("description_style"),
                                                         false);
        ButtonColor buttonColor      = ButtonColor.fromYaml(yaml.atMaybeKey("color"));
        TextColor   iconColor        = TextColor.fromYaml(yaml.atMaybeKey("icon_color"));

        return new ButtonWidgetFormat(id, labelStyle, descriptionStyle, buttonColor, iconColor);
    }


    /**
     * Create a Button Widget Format with default values.
     * @return The default Button Widget Format.
     */
    private static ButtonWidgetFormat asDefault()
    {
        ButtonWidgetFormat format = new ButtonWidgetFormat();

        format.setId(UUID.randomUUID());
        format.setLabelStyle(null);
        format.setDescriptionStyle(null);
        format.setButtonColor(null);
        format.setIconColor(null);

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
                .putYaml("label_style", this.labelStyle())
                .putYaml("description_style", this.descriptionStyle())
                .putYaml("color", this.buttonColor())
                .putYaml("icon_color", this.iconColor());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Label Style
    // --------------------------------------------------------------------------------------

    /**
     * The button label style.
     * @return The button label style.
     */
    public TextStyle labelStyle()
    {
        return this.labelStyle.getValue();
    }


    public void setLabelStyle(TextStyle style)
    {
        if (style != null) {
            this.labelStyle.setValue(style);
        }
        else {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_LIGHT,
                                                        TextSize.MEDIUM);
            this.labelStyle.setValue(defaultLabelStyle);
        }
    }


    // ** Description Style
    // --------------------------------------------------------------------------------------

    /**
     * The button description style.
     * @return The button description style.
     */
    public TextStyle descriptionStyle()
    {
        return this.descriptionStyle.getValue();
    }


    public void setDescriptionStyle(TextStyle style)
    {
        if (style != null) {
            this.descriptionStyle.setValue(style);
        }
        else {
            TextStyle defaultDescriptionStyle = new TextStyle(UUID.randomUUID(),
                                                              TextColor.THEME_MEDIUM,
                                                              TextSize.MEDIUM_SMALL);
            this.descriptionStyle.setValue(defaultDescriptionStyle);
        }
    }


    // ** Button Color
    // --------------------------------------------------------------------------------------

    /**
     * The button (background) color.
     * @return The button color.
     */
    public ButtonColor buttonColor()
    {
        return this.buttonColor.getValue();
    }


    /**
     * Set the button background color. If null, defaults to THEME_MEDIUM.
     * @param buttonColor The button color.
     */
    public void setButtonColor(ButtonColor buttonColor)
    {
        if (this.buttonColor != null)
            this.buttonColor.setValue(buttonColor);
        else
            this.buttonColor.setValue(ButtonColor.THEME_MEDIUM);
    }


    // ** Icon Color
    // --------------------------------------------------------------------------------------

    /**
     * The icon color.
     * @return The icon color.
     */
    public TextColor iconColor()
    {
        return this.iconColor.getValue();
    }


    /**
     * Set the icon color. If null, defaults to THEME MEDIUM LIGHT.
     * @param iconColor The icon color.
     */
    public void setIconColor(TextColor iconColor)
    {
        if (this.iconColor != null)
            this.iconColor.setValue(iconColor);
        else
            this.iconColor.setValue(TextColor.THEME_MEDIUM_LIGHT);
    }
}
