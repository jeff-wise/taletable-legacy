
package com.kispoko.tome.sheet.widget.expander;


import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Expander Widget Format
 */
public class ExpanderWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>     nameStyleClosed;
    private ModelFunctor<TextStyle>     nameStyleOpen;

    private ModelFunctor<Spacing>       headerPadding;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ExpanderWidgetFormat()
    {
        this.id                 = null;

        this.nameStyleClosed    = ModelFunctor.empty(TextStyle.class);
        this.nameStyleOpen      = ModelFunctor.empty(TextStyle.class);

        this.headerPadding      = ModelFunctor.empty(Spacing.class);
    }


    public ExpanderWidgetFormat(UUID id,
                                TextStyle nameStyleClosed,
                                TextStyle nameStyleOpen,
                                Spacing headerPadding)
    {
        this.id                 = id;

        this.nameStyleClosed    = ModelFunctor.full(nameStyleClosed, TextStyle.class);
        this.nameStyleOpen      = ModelFunctor.full(nameStyleOpen, TextStyle.class);

        this.headerPadding      = ModelFunctor.full(headerPadding, Spacing.class);

        this.setNameStyleClosed(nameStyleClosed);
        this.setNameStyleOpen(nameStyleOpen);
        this.setHeaderPadding(headerPadding);
    }


    /**
     * Create an Expander Widget format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Expander Widget Format.
     * @throws YamlParseException
     */
    public static ExpanderWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return ExpanderWidgetFormat.asDefault();

        UUID      id              = UUID.randomUUID();

        TextStyle nameStyleClosed = TextStyle.fromYaml(yaml.atMaybeKey("name_style_closed"));
        TextStyle nameStyleOpen   = TextStyle.fromYaml(yaml.atMaybeKey("name_style_open"));

        Spacing   headerPadding   = Spacing.fromYaml(yaml.atMaybeKey("header_padding"));

        return new ExpanderWidgetFormat(id, nameStyleClosed, nameStyleOpen, headerPadding);
    }


    /**
     * Create an Expander Widget Format with default values.
     * @return The default Expander Widget Format.
     */
    public static ExpanderWidgetFormat asDefault()
    {
        ExpanderWidgetFormat format = new ExpanderWidgetFormat();

        format.setId(UUID.randomUUID());

        format.setNameStyleClosed(null);
        format.setNameStyleOpen(null);

        format.setHeaderPadding(null);

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
            .putYaml("name_style_closed", this.nameStyleClosed())
            .putYaml("name_style_open", this.nameStyleOpen());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Name Style Closed
    // --------------------------------------------------------------------------------------

    /**
     * The closed name style.
     * @return The style.
     */
    public TextStyle nameStyleClosed()
    {
        return this.nameStyleClosed.getValue();
    }


    /**
     * Set the closed name style. If null, sets a default style.
     * @param style The style
     */
    public void setNameStyleClosed(TextStyle style)
    {
        if (style != null) {
            this.nameStyleClosed.setValue(style);

        }
        else {
            TextStyle defaultNameStyle = new TextStyle(UUID.randomUUID(),
                                                       TextColor.THEME_LIGHT,
                                                       TextSize.MEDIUM);
            this.nameStyleClosed.setValue(defaultNameStyle);
        }
    }


    // ** Name Style Open
    // --------------------------------------------------------------------------------------

    /**
     * The open name style.
     * @return The style.
     */
    public TextStyle nameStyleOpen()
    {
        return this.nameStyleOpen.getValue();
    }


    /**
     * Set the open name style. If null, sets a default style.
     * @param style The style
     */
    public void setNameStyleOpen(TextStyle style)
    {
        if (style != null) {
            this.nameStyleOpen.setValue(style);

        }
        else {
            TextStyle defaultNameStyle = new TextStyle(UUID.randomUUID(),
                                                       TextColor.GOLD_LIGHT,
                                                       TextSize.MEDIUM);
            this.nameStyleOpen.setValue(defaultNameStyle);
        }
    }


    // ** Header Padding
    // --------------------------------------------------------------------------------------

    /**
     * The header padding.
     * @return The header spacing.
     */
    public Spacing headerPadding()
    {
        return this.headerPadding.getValue();
    }


    /**
     * Set the header padding. If null, defaults to all 0.
     * @param spacing The header padding spacing.
     */
    public void setHeaderPadding(Spacing spacing)
    {
        if (spacing != null)
            this.headerPadding.setValue(spacing);
        else
            this.headerPadding.setValue(Spacing.asDefault());
    }

}
