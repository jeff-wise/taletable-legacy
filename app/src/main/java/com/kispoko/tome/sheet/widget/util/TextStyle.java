
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Widget Text Style
 */
public class TextStyle implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<TextColor>     color;
    private PrimitiveFunctor<TextSize>      size;
    private PrimitiveFunctor<Boolean>       isBold;
    private PrimitiveFunctor<Boolean>       isItalic;
    private PrimitiveFunctor<Boolean>       isUnderlined;
    private PrimitiveFunctor<Alignment>     alignment;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TextStyle()
    {
        this.id             = null;

        this.color          = new PrimitiveFunctor<>(null, TextColor.class);
        this.size           = new PrimitiveFunctor<>(null, TextSize.class);
        this.isBold         = new PrimitiveFunctor<>(null, Boolean.class);
        this.isItalic       = new PrimitiveFunctor<>(null, Boolean.class);
        this.isUnderlined   = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size,
                     Boolean isBold,
                     Boolean isItalic,
                     Boolean isUnderlined,
                     Alignment alignment)
    {
        this.id             = id;

        this.color          = new PrimitiveFunctor<>(color, TextColor.class);
        this.size           = new PrimitiveFunctor<>(size, TextSize.class);
        this.isBold         = new PrimitiveFunctor<>(isBold, Boolean.class);
        this.isItalic       = new PrimitiveFunctor<>(isItalic, Boolean.class);
        this.isUnderlined   = new PrimitiveFunctor<>(isUnderlined, Boolean.class);
        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);

        this.setColor(color);
        this.setSize(size);
        this.setIsBold(isBold);
        this.setIsItalic(isItalic);
        this.setIsUnderlined(isUnderlined);
        this.setAlignment(alignment);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size)
    {
        this.id             = id;

        this.color          = new PrimitiveFunctor<>(color, TextColor.class);
        this.size           = new PrimitiveFunctor<>(size, TextSize.class);
        this.isBold         = new PrimitiveFunctor<>(null, Boolean.class);
        this.isItalic       = new PrimitiveFunctor<>(null, Boolean.class);
        this.isUnderlined   = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);

        this.setColor(color);
        this.setSize(size);
        this.setIsBold(null);
        this.setIsItalic(null);
        this.setIsUnderlined(null);
        this.setAlignment(null);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size,
                     Alignment alignment)
    {
        this.id             = id;

        this.color          = new PrimitiveFunctor<>(color, TextColor.class);
        this.size           = new PrimitiveFunctor<>(size, TextSize.class);
        this.isBold         = new PrimitiveFunctor<>(null, Boolean.class);
        this.isItalic       = new PrimitiveFunctor<>(null, Boolean.class);
        this.isUnderlined   = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);

        this.setIsBold(null);
        this.setIsItalic(null);
        this.setIsUnderlined(null);
    }


    /**
     * Create a Widget Text Style from its yaml representation. If the yaml is null, return a
     * default text style.
     * @param yaml The yaml parser.
     * @return The parsed Text Style.
     * @throws YamlParseException
     */
    public static TextStyle fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        return TextStyle.fromYaml(yaml, true);
    }


    /**
     * Create a Text Style from its yaml representation. If the yaml is null, and useDefault is
     * null, returns null.
     * @param yaml The yaml parser.
     * @param useDefault If true, return a default when the yaml is null.
     * @return The parsed Text Style.
     * @throws YamlParseException
     */
    public static TextStyle fromYaml(YamlParser yaml, boolean useDefault)
                  throws YamlParseException
    {
        if (yaml.isNull() && useDefault)
            return TextStyle.asDefault();
        else if (yaml.isNull())
            return null;

        UUID      id            = UUID.randomUUID();

        TextColor color         = TextColor.fromYaml(yaml.atMaybeKey("color"));
        TextSize  size          = TextSize.fromYaml(yaml.atMaybeKey("size"));
        Boolean   isBold        = yaml.atMaybeKey("is_bold").getBoolean();
        Boolean   isItalic      = yaml.atMaybeKey("is_italic").getBoolean();
        Boolean   isUnderlined  = yaml.atMaybeKey("is_underlined").getBoolean();
        Alignment alignment     = Alignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new TextStyle(id, color, size, isBold, isItalic, isUnderlined, alignment);
    }


    public static TextStyle asDefault()
    {
        TextStyle style = new TextStyle();

        style.setId(UUID.randomUUID());
        style.setColor(null);
        style.setSize(null);
        style.setIsBold(null);
        style.setIsItalic(null);
        style.setIsUnderlined(null);
        style.setAlignment(null);

        return style;
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
    // -----------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("color", this.color());
        yaml.putYaml("size", this.size());
        yaml.putBoolean("is_bold", this.isBold());
        yaml.putBoolean("is_italic", this.isItalic());
        yaml.putBoolean("is_underlined", this.isUnderlined());

        return yaml;
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Color
    // -----------------------------------------------------------------------------------------

    /**
     * The text color.
     * @return The text color.
     */
    public TextColor color()
    {
        return this.color.getValue();
    }


    public void setColor(TextColor color)
    {
        if (color != null)
            this.color.setValue(color);
        else
            this.color.setValue(TextColor.MEDIUM);
    }


    // ** Size
    // -----------------------------------------------------------------------------------------

    /**
     * The text size.
     * @return The size.
     */
    public TextSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the text size. If null, defaults to MEDIUM.
     * @param size The text size.
     */
    public void setSize(TextSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(TextSize.MEDIUM);
    }


    // ** Is Bold
    // -----------------------------------------------------------------------------------------

    /**
     * True if the text should be bold.
     * @return Is bold?
     */
    public Boolean isBold()
    {
        return this.isBold.getValue();
    }


    /**
     * Set the next to be bold or non-bold. Defaults to non-bold if null.
     * @param isBold Is bold?
     */
    public void setIsBold(Boolean isBold)
    {
        if (isBold != null)
            this.isBold.setValue(isBold);
        else
            this.isBold.setValue(false);
    }


    // ** Is Underlined
    // -----------------------------------------------------------------------------------------

    /**
     * True if the text should be underlined.
     * @return Is underlined?
     */
    public Boolean isUnderlined()
    {
        return this.isUnderlined.getValue();
    }


    /**
     * Set the text to be underlined or not underlined. Defaults to non underlined if null.
     * @param isUnderlined Is underlined?
     */
    public void setIsUnderlined(Boolean isUnderlined)
    {
        if (isUnderlined != null)
            this.isUnderlined.setValue(isUnderlined);
        else
            this.isUnderlined.setValue(false);
    }


    // ** Is Italic
    // -----------------------------------------------------------------------------------------

    /**
     * True if the text is italicized.
     * @return Is italic?
     */
    public Boolean isItalic()
    {
        return this.isItalic.getValue();
    }


    /**
     * Set the text to be italic or not.
     * @param isItalic Is italic?
     */
    public void setIsItalic(Boolean isItalic)
    {
        if (isItalic != null)
            this.isItalic.setValue(isItalic);
        else
            this.isItalic.setValue(false);
    }


    // ** Alignment
    // -----------------------------------------------------------------------------------------

    /**
     * The alignment.
     * @return The alignment.
     */
    public Alignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the alignment. If null, defaults to LEFT.
     * @param alignment The alignment.
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(Alignment.LEFT);
    }

}
