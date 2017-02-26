
package com.kispoko.tome.sheet.widget.option;


import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

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

    private UUID                    id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle> descriptionStyle;
    private ModelFunctor<TextStyle> valueStyle;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public OptionWidgetFormat()
    {
        this.id                 = null;

        this.descriptionStyle   = ModelFunctor.empty(TextStyle.class);
        this.valueStyle         = ModelFunctor.empty(TextStyle.class);
    }


    public OptionWidgetFormat(UUID id, TextStyle descriptionStyle, TextStyle valueStyle)
    {
        this.id                 = id;

        this.descriptionStyle   = ModelFunctor.full(descriptionStyle, TextStyle.class);
        this.valueStyle         = ModelFunctor.full(valueStyle, TextStyle.class);

        this.setDescriptionStyle(descriptionStyle);
        this.setValueStyle(valueStyle);
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

        TextStyle descriptionStyle  = TextStyle.fromYaml(yaml.atMaybeKey("description_style"),
                                                         false);
        TextStyle valueStyle        = TextStyle.fromYaml(yaml.atMaybeKey("value_style"), false);

        return new OptionWidgetFormat(id, descriptionStyle, valueStyle);
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
                .putYaml("value_style", this.valueStyle());
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


}
