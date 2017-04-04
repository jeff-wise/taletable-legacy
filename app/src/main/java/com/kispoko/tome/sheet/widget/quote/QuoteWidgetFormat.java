
package com.kispoko.tome.sheet.widget.quote;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextFont;
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
 * Quote Widget Format
 */
public class QuoteWidgetFormat extends Model
                               implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>     quoteStyle;
    private ModelFunctor<TextStyle>     sourceStyle;

    private PrimitiveFunctor<TextColor> iconColor;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public QuoteWidgetFormat()
    {
        this.id             = null;

        this.quoteStyle     = ModelFunctor.empty(TextStyle.class);
        this.sourceStyle    = ModelFunctor.empty(TextStyle.class);

        this.iconColor      = new PrimitiveFunctor<>(null, TextColor.class);
    }


    public QuoteWidgetFormat(UUID id,
                             TextStyle quoteStyle,
                             TextStyle sourceStyle,
                             TextColor iconColor)
    {
        this.id             = id;

        this.quoteStyle     = ModelFunctor.full(quoteStyle, TextStyle.class);
        this.sourceStyle    = ModelFunctor.full(sourceStyle, TextStyle.class);

        this.iconColor      = new PrimitiveFunctor<>(iconColor, TextColor.class);

        this.setQuoteStyle(quoteStyle);
        this.setSourceStyle(sourceStyle);
        this.setIconColor(iconColor);
    }


    /**
     * Create a Quote Widget Format from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Quote Widget Format.
     * @throws YamlParseException
     */
    public static QuoteWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return QuoteWidgetFormat.asDefault();

        UUID      id          = UUID.randomUUID();

        TextStyle quoteStyle  = TextStyle.fromYaml(yaml.atMaybeKey("quote_style"));
        TextStyle sourceStyle = TextStyle.fromYaml(yaml.atMaybeKey("source_style"));

        TextColor iconColor   = TextColor.fromYaml(yaml.atMaybeKey("icon_color"));

        return new QuoteWidgetFormat(id, quoteStyle, sourceStyle, iconColor);
    }


    /**
     * Create a Quote Widget Format with default values.
     * @return The default Quote Widget Format.
     */
    private static QuoteWidgetFormat asDefault()
    {
        QuoteWidgetFormat format = new QuoteWidgetFormat();

        format.setId(UUID.randomUUID());

        format.setQuoteStyle(null);
        format.setSourceStyle(null);

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
            .putYaml("quote_style", this.quoteStyle())
            .putYaml("source_style", this.sourceStyle());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Quote Style
    // --------------------------------------------------------------------------------------

    /**
     * The quote style.
     * @return The quote style.
     */
    public TextStyle quoteStyle()
    {
        return this.quoteStyle.getValue();
    }


    /**
     * Set the quote style. If null, a default style is set.
     * @param style The style
     */
    public void setQuoteStyle(TextStyle style)
    {
        if (style != null) {
            this.quoteStyle.setValue(style);
        }
        else {
            TextStyle defaultQuoteStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_VERY_DARK,
                                                        TextSize.MEDIUM,
                                                        Alignment.CENTER);
            this.quoteStyle.setValue(defaultQuoteStyle);
        }
    }


    // ** Source Style
    // --------------------------------------------------------------------------------------

    /**
     * The source style.
     * @return The source style.
     */
    public TextStyle sourceStyle()
    {
        return this.sourceStyle.getValue();
    }


    /**
     * Set the source style. If null, a default style is set.
     * @param style The style
     */
    public void setSourceStyle(TextStyle style)
    {
        if (style != null) {
            this.sourceStyle.setValue(style);
        }
        else {
            TextStyle defaultSourceStyle = new TextStyle(UUID.randomUUID(),
                                                         TextColor.THEME_VERY_DARK,
                                                         TextSize.VERY_SMALL,
                                                         TextFont.REGULAR,
                                                         Alignment.CENTER);
            this.sourceStyle.setValue(defaultSourceStyle);
        }
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
     * Set the icon color. If null, the default color is THEME_DARK.
     * @param color The icon color.
     */
    public void setIconColor(TextColor color)
    {
        if (color != null)
            this.iconColor.setValue(color);
        else
            this.iconColor.setValue(TextColor.THEME_DARK);
    }


}
