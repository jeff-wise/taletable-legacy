
package com.kispoko.tome.sheet.widget.option;


import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Option Widget Format
 */
public class OptionWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>             descriptionStyle;
    private ModelFunctor<TextStyle>             valueStyle;
    private ModelFunctor<TextStyle>             valueItemStyle;

    private PrimitiveFunctor<Height>            height;
    private PrimitiveFunctor<Integer>           verticalPadding;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public OptionWidgetFormat()
    {
        this.id                 = null;

        this.descriptionStyle   = ModelFunctor.empty(TextStyle.class);
        this.valueStyle         = ModelFunctor.empty(TextStyle.class);
        this.valueItemStyle     = ModelFunctor.empty(TextStyle.class);

        this.height             = new PrimitiveFunctor<>(null, Height.class);
        this.verticalPadding    = new PrimitiveFunctor<>(null, Integer.class);
    }


    public OptionWidgetFormat(UUID id,
                              TextStyle descriptionStyle,
                              TextStyle valueStyle,
                              TextStyle valueItemStyle,
                              Height height,
                              Integer verticalPadding)
    {
        this.id                 = id;

        this.descriptionStyle   = ModelFunctor.full(descriptionStyle, TextStyle.class);
        this.valueStyle         = ModelFunctor.full(valueStyle, TextStyle.class);
        this.valueItemStyle     = ModelFunctor.full(valueItemStyle, TextStyle.class);

        this.height             = new PrimitiveFunctor<>(height, Height.class);
        this.verticalPadding    = new PrimitiveFunctor<>(verticalPadding, Integer.class);

        // > Set defaults
        this.setDescriptionStyle(descriptionStyle);
        this.setValueStyle(valueStyle);
        this.setValueItemStyle(valueItemStyle);

        this.setHeight(height);
        this.setVerticalPadding(verticalPadding);
    }


    /**
     * Create an Option Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Option Widget Format.
     * @throws YamlParseException
     */
    public static OptionWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return OptionWidgetFormat.asDefault();

        UUID      id                = UUID.randomUUID();

        TextStyle descriptionStyle  = TextStyle.fromYaml(yaml.atMaybeKey("description_style"));
        TextStyle valueStyle        = TextStyle.fromYaml(yaml.atMaybeKey("value_style"));
        TextStyle valueItemStyle    = TextStyle.fromYaml(yaml.atMaybeKey("value_item_style"));

        Height    height            = Height.fromYaml(yaml.atMaybeKey("height"));
        Integer   verticalPadding   = yaml.atMaybeKey("vertical_padding").getInteger();

        return new OptionWidgetFormat(id, descriptionStyle, valueStyle, valueItemStyle,
                                      height, verticalPadding);
    }


    /**
     * Create an Option Widget Format with all default values.
     * @return The default Option Widget Format.
     */
    private static OptionWidgetFormat asDefault()
    {
        OptionWidgetFormat format = new OptionWidgetFormat();

        format.setId(UUID.randomUUID());

        format.setDescriptionStyle(null);
        format.setValueStyle(null);
        format.setValueItemStyle(null);

        format.setHeight(null);
        format.setVerticalPadding(null);

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
                .putYaml("description_style", this.descriptionStyle())
                .putYaml("value_style", this.valueStyle())
                .putYaml("value_item_style", this.valueItemStyle());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Description Style
    // --------------------------------------------------------------------------------------

    /**
     * The description style.
     * @return The description style.
     */
    public TextStyle descriptionStyle()
    {
        return this.descriptionStyle.getValue();
    }


    /**
     * Set the description style. If null, provides a default.
     * @param style The style.
     */
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


    // ** Value Style
    // --------------------------------------------------------------------------------------

    /**
     * The value style.
     * @return The value style.
     */
    public TextStyle valueStyle()
    {
        return this.valueStyle.getValue();
    }


    /**
     * Set the value style. If null, a default is set.
     * @param style The style
     */
    public void setValueStyle(TextStyle style)
    {
        if (style != null) {
            this.valueStyle.setValue(style);
        }
        else {
            TextStyle defaultValueStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_MEDIUM,
                                                        TextSize.MEDIUM_SMALL);
            this.valueStyle.setValue(defaultValueStyle);
        }
    }


    // ** Value Item Style
    // --------------------------------------------------------------------------------------

    /**
     * The value item style.
     * @return The value item style.
     */
    public TextStyle valueItemStyle()
    {
        return this.valueItemStyle.getValue();
    }


    /**
     * Set the value style. If null, a default is set.
     * @param style The style
     */
    public void setValueItemStyle(TextStyle style)
    {
        if (style != null) {
            this.valueItemStyle.setValue(style);
        }
        else {
            TextStyle defaultValueItemStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_DARK,
                                                        TextSize.MEDIUM_SMALL);
            this.valueItemStyle.setValue(defaultValueItemStyle);
        }
    }


    // ** Height
    // --------------------------------------------------------------------------------------

    /**
     * The height of the widget.
     * @return The height
     */
    public Height height()
    {
        return this.height.getValue();
    }


    /**
     * Set the height of the option widget. If null, defaults to WRAP.
     * @param height The height.
     */
    public void setHeight(Height height)
    {
        if (height != null)
            this.height.setValue(height);
        else
            this.height.setValue(Height.WRAP);
    }


    // ** Vertical Padding
    // --------------------------------------------------------------------------------------

    /**
     * The vertical padding (dp) around the widget content. The padding is only used when the
     * height of the widget is set to WRAP.
     * @return The vertical padding.
     */
    public Integer verticalPadding()
    {
        return this.verticalPadding.getValue();
    }


    /**
     * Set the vertical padding. If null, defaults to 0.
     * @param padding The padding.
     */
    public void setVerticalPadding(Integer padding)
    {
        if (padding != null)
            this.verticalPadding.setValue(padding);
        else
            this.verticalPadding.setValue(padding);
    }


}
