
package com.kispoko.tome.sheet.widget.text;


import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.WidgetTextTint;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;


import java.io.Serializable;
import java.util.UUID;



/**
 * Text Widget Format
 */
public class TextWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<WidgetContentSize> size;
    private PrimitiveFunctor<WidgetTextTint>    tint;
    private PrimitiveFunctor<Boolean>           isQuote;
    private PrimitiveFunctor<String>            quoteSource;
    private PrimitiveFunctor<String>            inlineLabel;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TextWidgetFormat()
    {
        this.id             = null;

        this.size           = new PrimitiveFunctor<>(null, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(null, WidgetTextTint.class);
        this.isQuote        = new PrimitiveFunctor<>(null, Boolean.class);
        this.quoteSource    = new PrimitiveFunctor<>(null, String.class);
        this.inlineLabel    = new PrimitiveFunctor<>(null, String.class);
    }


    public TextWidgetFormat(UUID id,
                            WidgetContentSize size,
                            WidgetTextTint tint,
                            Boolean isQuote,
                            String quoteSource,
                            String inlineLabel)
    {
        this.id             = id;

        this.size           = new PrimitiveFunctor<>(size, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(tint, WidgetTextTint.class);
        this.isQuote        = new PrimitiveFunctor<>(isQuote, Boolean.class);
        this.quoteSource    = new PrimitiveFunctor<>(quoteSource, String.class);
        this.inlineLabel    = new PrimitiveFunctor<>(inlineLabel, String.class);

        this.setIsQuote(isQuote);
        this.setSize(size);
        this.setQuoteSource(quoteSource);
        this.setTint(tint);
    }


    public static TextWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TextWidgetFormat.asDefault();

        UUID              id          = UUID.randomUUID();

        WidgetContentSize size        = WidgetContentSize.fromYaml(yaml.atMaybeKey("size"));
        WidgetTextTint    tint        = WidgetTextTint.fromYaml(yaml.atMaybeKey("tint"));
        Boolean           isQuote     = yaml.atMaybeKey("is_quote").getBoolean();
        String            quoteSource = yaml.atMaybeKey("quote_source").getString();
        String            inlineLabel = yaml.atMaybeKey("inline_label").getString();

        return new TextWidgetFormat(id, size, tint, isQuote, quoteSource, inlineLabel);
    }


    private static TextWidgetFormat asDefault()
    {
        TextWidgetFormat textWidgetFormat = new TextWidgetFormat();

        textWidgetFormat.setId(UUID.randomUUID());
        textWidgetFormat.setSize(null);
        textWidgetFormat.setTint(null);
        textWidgetFormat.setIsQuote(null);
        textWidgetFormat.setQuoteSource(null);
        textWidgetFormat.setInlineLabel(null);

        return textWidgetFormat;
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

        yaml.putYaml("size", this.size());
        yaml.putBoolean("is_quote", this.isQuote());
        yaml.putString("quote_source", this.quoteSource());

        return yaml;
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Size
    // --------------------------------------------------------------------------------------

    /**
     * The Text Widget's text size.
     * @return The Widget Content Size.
     */
    public WidgetContentSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the text widget's text size.
     * @param size The text size.
     */
    public void setSize(WidgetContentSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(WidgetContentSize.MEDIUM);
    }


    // ** Tint
    // --------------------------------------------------------------------------------------

    /**
     * The text widget tint.
     * @return The tint.
     */
    public WidgetTextTint tint()
    {
        return this.tint.getValue();
    }


    public void setTint(WidgetTextTint tint)
    {
        if (tint != null)
            this.tint.setValue(tint);
        else
            this.tint.setValue(WidgetTextTint.MEDIUM);
    }


    // ** Is Quote
    // --------------------------------------------------------------------------------------

    /**
     * True if this text widget represents a quote.
     * @return Is Quote Boolean.
     */
    public Boolean isQuote()
    {
        return this.isQuote.getValue();
    }


    /**
     * Set the is quote value. True if the text widget is a quote.
     * @param isQuote Is Quote?
     */
    public void setIsQuote(Boolean isQuote)
    {
        if (isQuote != null)
            this.isQuote.setValue(isQuote);
        else
            this.isQuote.setValue(false);
    }


    // ** Quote Source
    // --------------------------------------------------------------------------------------

    /**
     * The quote source (who said it).
     * @return The quote source.
     */
    public String quoteSource()
    {
        return this.quoteSource.getValue();
    }


    /**
     * Set the quote source (who said the quote)
     * @param quoteSource The quote source.
     */
    public void setQuoteSource(String quoteSource)
    {
        if (quoteSource != null)
            this.quoteSource.setValue(quoteSource);
        else
            this.quoteSource.setValue("");
    }


    // ** Inline Label
    // --------------------------------------------------------------------------------------

    /**
     * The text widget's inline label (may be null).
     * @return The inline label.
     */
    public String inlineLabel()
    {
        return this.inlineLabel.getValue();
    }


    /**
     * Set the inline label.
     * @param inlineLabel The label.
     */
    public void setInlineLabel(String inlineLabel)
    {
        this.inlineLabel.setValue(inlineLabel);
    }


}
