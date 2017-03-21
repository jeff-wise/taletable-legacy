
package com.kispoko.tome.sheet.widget.button;


import android.support.annotation.Nullable;

import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
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

    private PrimitiveFunctor<Height>        height;

    private ModelFunctor<TextStyle>         labelStyle;
    private ModelFunctor<TextStyle>         descriptionStyle;
    private PrimitiveFunctor<Position>      descriptionPosition;
    private PrimitiveFunctor<ButtonColor>   buttonColor;
    private PrimitiveFunctor<TextColor>     iconColor;

    private PrimitiveFunctor<Integer>       paddingHorizontal;
    private PrimitiveFunctor<Integer>       paddingVertical;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ButtonWidgetFormat()
    {
        this.id                     = null;

        this.height                 = new PrimitiveFunctor<>(null, Height.class);

        this.labelStyle             = ModelFunctor.empty(TextStyle.class);
        this.descriptionStyle       = ModelFunctor.empty(TextStyle.class);
        this.descriptionPosition    = new PrimitiveFunctor<>(null, Position.class);
        this.buttonColor            = new PrimitiveFunctor<>(null, ButtonColor.class);
        this.iconColor              = new PrimitiveFunctor<>(null, TextColor.class);

        this.paddingHorizontal      = new PrimitiveFunctor<>(null, Integer.class);
        this.paddingVertical        = new PrimitiveFunctor<>(null, Integer.class);
    }


    public ButtonWidgetFormat(UUID id,
                              Height height,
                              TextStyle labelStyle,
                              TextStyle descriptionStyle,
                              Position descriptionPosition,
                              ButtonColor buttonColor,
                              TextColor iconColor,
                              Integer paddingHorizontal,
                              Integer paddingVertical)
    {
        this.id                     = id;

        this.height                 = new PrimitiveFunctor<>(height, Height.class);

        this.labelStyle             = ModelFunctor.full(labelStyle, TextStyle.class);
        this.descriptionStyle       = ModelFunctor.full(descriptionStyle, TextStyle.class);
        this.descriptionPosition    = new PrimitiveFunctor<>(descriptionPosition, Position.class);
        this.buttonColor            = new PrimitiveFunctor<>(buttonColor, ButtonColor.class);
        this.iconColor              = new PrimitiveFunctor<>(iconColor, TextColor.class);

        this.paddingHorizontal      = new PrimitiveFunctor<>(paddingHorizontal, Integer.class);
        this.paddingVertical        = new PrimitiveFunctor<>(paddingVertical, Integer.class);

        // > Set defaults for null values
        this.setHeight(height);
        this.setLabelStyle(labelStyle);
        this.setDescriptionStyle(descriptionStyle);
        this.setDescriptionPosition(descriptionPosition);
        this.setButtonColor(buttonColor);
        this.setIconColor(iconColor);

        this.setPaddingVertical(paddingVertical);
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

        UUID        id                  = UUID.randomUUID();

        Height      height              = Height.fromYaml(yaml.atMaybeKey("height"));

        TextStyle   labelStyle          = TextStyle.fromYaml(yaml.atMaybeKey("label_style"), false);
        TextStyle   descriptionStyle    = TextStyle.fromYaml(yaml.atMaybeKey("description_style"),
                                                             false);
        Position    descriptionPosition = Position.fromYaml(
                                                        yaml.atMaybeKey("description_position"));
        ButtonColor buttonColor         = ButtonColor.fromYaml(yaml.atMaybeKey("color"));
        TextColor   iconColor           = TextColor.fromYaml(yaml.atMaybeKey("icon_color"));

        Integer     paddingHorizontal   = yaml.atMaybeKey("padding_horizontal").getInteger();
        Integer     paddingVertical     = yaml.atMaybeKey("padding_vertical").getInteger();

        return new ButtonWidgetFormat(id, height, labelStyle, descriptionStyle, descriptionPosition,
                                      buttonColor, iconColor, paddingHorizontal, paddingVertical);
    }


    /**
     * Create a Button Widget Format with default values.
     * @return The default Button Widget Format.
     */
    private static ButtonWidgetFormat asDefault()
    {
        ButtonWidgetFormat format = new ButtonWidgetFormat();

        format.setId(UUID.randomUUID());

        format.setHeight(null);

        format.setLabelStyle(null);
        format.setDescriptionStyle(null);
        format.setDescriptionPosition(null);
        format.setButtonColor(null);
        format.setIconColor(null);

        format.setPaddingHorizontal(null);
        format.setPaddingVertical(null);

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
                .putYaml("height", this.height())
                .putYaml("label_style", this.labelStyle())
                .putYaml("description_style", this.descriptionStyle())
                .putYaml("color", this.buttonColor())
                .putYaml("icon_color", this.iconColor())
                .putInteger("padding_horizontal", this.paddingHorizontal())
                .putInteger("padding_vertical", this.paddingVertical());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Height
    // --------------------------------------------------------------------------------------

    /**
     * The height.
     * @return The height.
     */
    public Height height()
    {
        return this.height.getValue();
    }


    /**
     * Set the button height. If null, defaults to WRAP.
     * @param height The height.
     */
    public void setHeight(Height height)
    {
        if (height != null)
            this.height.setValue(height);
        else
            this.height.setValue(Height.WRAP);
    }


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


    // ** Description Position
    // --------------------------------------------------------------------------------------

    /**
     * The position of the button's description relative to the button.
     * @return The description position.
     */
    public Position descriptionPosition()
    {
        return this.descriptionPosition.getValue();
    }


    /**
     * Set the description position. If null, defaults to appearing to the right of the button.
     * @param position The position.
     */
    public void setDescriptionPosition(Position position)
    {
        if (position != null)
            this.descriptionPosition.setValue(position);
        else
            this.descriptionPosition.setValue(Position.RIGHT);
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


    // ** Padding Horizontal
    // --------------------------------------------------------------------------------------

    /**
     * The horizontal padding around the widget content.
     * @return The padding.
     */
    @Nullable
    public Integer paddingHorizontal()
    {
        return this.paddingHorizontal.getValue();
    }


    /**
     * Set the horizontal padding.
     * @param padding The padding.
     */
    public void setPaddingHorizontal(Integer padding)
    {
        this.paddingHorizontal.setValue(padding);
    }


    // ** Padding Vertical
    // --------------------------------------------------------------------------------------

    /**
     * The vertical padding around the widget content. Applied only when the height is WRAP.
     * @return The vertical padding.
     */
    public Integer paddingVertical()
    {
        return this.paddingVertical.getValue();
    }


    /**
     * Set the vertical padding. If null, sets 0 as the default.
     * @param padding The padding.
     */
    public void setPaddingVertical(Integer padding)
    {
        if (padding != null)
            this.paddingVertical.setValue(padding);
        else
            this.paddingVertical.setValue(0);
    }


}
