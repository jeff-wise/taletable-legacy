
package com.kispoko.tome.sheet.widget.action;


import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextFont;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
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
 * Action Widget Format
 */
public class ActionWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>     descriptionStyle;
    private ModelFunctor<TextStyle>     actionStyle;

    /**
     * The horizontal padding around the content. If null, the widget will stretch to fill the
     * parent's space.
     */
    private PrimitiveFunctor<Integer>   paddingHorizontal;

    /**
     * Vertical padding around the content. Only applies when height is wrap.
     */
    private PrimitiveFunctor<Integer>   paddingVertical;

    private PrimitiveFunctor<Height>    height;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ActionWidgetFormat()
    {
        this.id                 = null;

        this.descriptionStyle   = ModelFunctor.empty(TextStyle.class);
        this.actionStyle        = ModelFunctor.empty(TextStyle.class);

        this.paddingHorizontal  = new PrimitiveFunctor<>(null, Integer.class);
        this.paddingVertical    = new PrimitiveFunctor<>(null, Integer.class);

        this.height             = new PrimitiveFunctor<>(null, Height.class);
    }


    public ActionWidgetFormat(UUID id,
                              TextStyle descriptionStyle,
                              TextStyle actionStyle,
                              Integer paddingHorizontal,
                              Integer paddingVertical,
                              Height height)
    {
        this.id                 = id;

        this.descriptionStyle   = ModelFunctor.full(descriptionStyle, TextStyle.class);
        this.actionStyle        = ModelFunctor.full(actionStyle, TextStyle.class);

        this.paddingHorizontal  = new PrimitiveFunctor<>(paddingHorizontal, Integer.class);
        this.paddingVertical    = new PrimitiveFunctor<>(paddingVertical, Integer.class);

        this.height             = new PrimitiveFunctor<>(height, Height.class);

        this.setDescriptionStyle(descriptionStyle);
        this.setActionStyle(actionStyle);

        this.setPaddingHorizontal(paddingHorizontal);
        this.setPaddingVertical(paddingVertical);

        this.setHeight(height);
    }


    /**
     * Create an ActionWidgetFormat from its yaml representation.
     * @param yaml The yaml parrser.
     * @return The parsed Action Widget Format.
     * @throws YamlParseException
     */
    public static ActionWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return ActionWidgetFormat.asDefault();

        UUID      id                = UUID.randomUUID();

        TextStyle descriptionStyle  = TextStyle.fromYaml(yaml.atMaybeKey("description_style"));
        TextStyle actionStyle       = TextStyle.fromYaml(yaml.atMaybeKey("action_style"));

        Integer   paddingHorizontal = yaml.atMaybeKey("padding_horizontal").getInteger();
        Integer   paddingVertical   = yaml.atMaybeKey("padding_vertical").getInteger();

        Height    height            = Height.fromYaml(yaml.atMaybeKey("height"));

        return new ActionWidgetFormat(id, descriptionStyle, actionStyle, paddingHorizontal,
                                      paddingVertical, height);
    }


    /**
     * Create an Action Widget Format with default values.
     * @return The default Action Widget Format.
     */
    private static ActionWidgetFormat asDefault()
    {
        ActionWidgetFormat format = new ActionWidgetFormat();

        format.setId(UUID.randomUUID());

        format.setDescriptionStyle(null);
        format.setActionStyle(null);

        format.setPaddingHorizontal(null);
        format.setPaddingVertical(null);

        format.setHeight(null);

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
                .putYaml("action_style", this.actionStyle())
                .putYaml("description_style", this.descriptionStyle())
                .putInteger("padding_horizontal", this.paddingHorizontal())
                .putInteger("padding_vertical", this.paddingVertical())
                .putYaml("height", this.height());
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Description Style
    // -----------------------------------------------------------------------------------------

    /**
     * The description text style.
     * @return The style
     */
    public TextStyle descriptionStyle()
    {
        return this.descriptionStyle.getValue();
    }


    /**
     * Set the description text style. If null, a default style is set.
     * @param style The style
     */
    public void setDescriptionStyle(TextStyle style)
    {
        if (style != null) {
            this.descriptionStyle.setValue(style);
        }
        else {
            TextStyle defaultDescriptionStyle = new TextStyle(UUID.randomUUID(),
                                                              TextColor.THEME_DARK,
                                                              TextSize.MEDIUM_SMALL,
                                                              TextFont.REGULAR);
            this.descriptionStyle.setValue(defaultDescriptionStyle);
        }
    }


    // ** Action Style
    // -----------------------------------------------------------------------------------------

    /**
     * The action text style.
     * @return The style
     */
    public TextStyle actionStyle()
    {
        return this.actionStyle.getValue();
    }


    /**
     * Set the action text style. If null, a default style is set.
     * @param style The style
     */
    public void setActionStyle(TextStyle style)
    {
        if (style != null) {
            this.actionStyle.setValue(style);
        }
        else {
            TextStyle defaultActionStyle = new TextStyle(UUID.randomUUID(),
                                                         TextColor.THEME_VERY_LIGHT,
                                                         TextSize.MEDIUM_SMALL,
                                                         TextFont.BOLD);
            this.actionStyle.setValue(defaultActionStyle);
        }
    }


    // ** Padding Horizontal
    // -----------------------------------------------------------------------------------------

    /**
     * The horizontal padding.
     * @return The spacing.
     */
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
        if (padding != null)
            this.paddingHorizontal.setValue(padding);
        else
            this.paddingHorizontal.setValue(0);
    }


    // ** Padding Vertical
    // -----------------------------------------------------------------------------------------

    /**
     * The vertical padding.
     * @return The vertical padding.
     */
    public Integer paddingVertical()
    {
        return this.paddingVertical.getValue();
    }


    /**
     * Set the vertical padding.
     * @param padding The padding.
     */
    public void setPaddingVertical(Integer padding)
    {
        if (padding != null)
            this.paddingVertical.setValue(padding);
        else
            this.paddingVertical.setValue(0);
    }


    // ** Height
    // --------------------------------------------------------------------------------------

    /**
     * The height. May be null.
     * @return The height.
     */
    public Height height()
    {
        return this.height.getValue();
    }


    /**
     * Set the height.
     * @param height The height.
     */
    public void setHeight(Height height)
    {
        if (height != null)
            this.height.setValue(height);
        else
            this.height.setValue(Height.WRAP);
    }


}
