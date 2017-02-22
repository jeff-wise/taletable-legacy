
package com.kispoko.tome.sheet.widget.button;


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
 * Button Widget Format
 */
public class ButtonWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>     labelStyle;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ButtonWidgetFormat()
    {
        this.id             = null;

        this.labelStyle     = ModelFunctor.empty(TextStyle.class);
    }


    public ButtonWidgetFormat(UUID id, TextStyle labelStyle)
    {
        this.id             = id;

        this.labelStyle     = ModelFunctor.full(labelStyle, TextStyle.class);

        // > Set defaults for null values
        this.setLabelStyle(labelStyle);
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

        UUID      id         = UUID.randomUUID();

        TextStyle labelStyle = TextStyle.fromYaml(yaml.atMaybeKey("label_style"), false);

        return new ButtonWidgetFormat(id, labelStyle);
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
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("label_style", this.labelStyle());

        return yaml;
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
                                                        TextColor.LIGHT,
                                                        TextSize.MEDIUM);
            this.labelStyle.setValue(defaultLabelStyle);
        }
    }


}
