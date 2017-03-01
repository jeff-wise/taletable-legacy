
package com.kispoko.tome.sheet.widget.quote;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextFont;
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
 * Quote Widget Format
 */
public class QuoteWidgetFormat implements Model, ToYaml, Serializable
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


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public QuoteWidgetFormat()
    {
        this.id             = null;

        this.quoteStyle     = ModelFunctor.empty(TextStyle.class);
        this.sourceStyle    = ModelFunctor.empty(TextStyle.class);
    }


    public QuoteWidgetFormat(UUID id, TextStyle quoteStyle, TextStyle sourceStyle)
    {
        this.id             = id;

        this.quoteStyle     = ModelFunctor.full(quoteStyle, TextStyle.class);
        this.sourceStyle    = ModelFunctor.full(sourceStyle, TextStyle.class);

        this.setQuoteStyle(quoteStyle);
        this.setSourceStyle(sourceStyle);
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

        return new QuoteWidgetFormat(id, quoteStyle, sourceStyle);
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


}
