
package com.kispoko.tome.sheet.widget.util;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.functor.PrimitiveFunctor;
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

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<TextColor>         color;
    private PrimitiveFunctor<TextSize>          size;
    private PrimitiveFunctor<TextFont>          font;
    private PrimitiveFunctor<Boolean>           isUnderlined;
    private PrimitiveFunctor<Alignment>         alignment;
    private PrimitiveFunctor<BackgroundColor>   backgroundColor;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TextStyle()
    {
        this.id                 = null;

        this.color              = new PrimitiveFunctor<>(null, TextColor.class);
        this.size               = new PrimitiveFunctor<>(null, TextSize.class);
        this.font               = new PrimitiveFunctor<>(null, TextFont.class);
        this.isUnderlined       = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment          = new PrimitiveFunctor<>(null, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(null, BackgroundColor.class);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size,
                     TextFont font,
                     Boolean isUnderlined,
                     Alignment alignment,
                     BackgroundColor backgroundColor)
    {
        this.id                 = id;

        this.color              = new PrimitiveFunctor<>(color, TextColor.class);
        this.size               = new PrimitiveFunctor<>(size, TextSize.class);
        this.font               = new PrimitiveFunctor<>(font, TextFont.class);
        this.isUnderlined       = new PrimitiveFunctor<>(isUnderlined, Boolean.class);
        this.alignment          = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(backgroundColor, BackgroundColor.class);

        this.setColor(color);
        this.setSize(size);
        this.setFont(font);
        this.setIsUnderlined(isUnderlined);
        this.setAlignment(alignment);
        this.setBackgroundColor(backgroundColor);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size)
    {
        this.id                 = id;

        this.color              = new PrimitiveFunctor<>(color, TextColor.class);
        this.size               = new PrimitiveFunctor<>(size, TextSize.class);
        this.font               = new PrimitiveFunctor<>(null, TextFont.class);
        this.isUnderlined       = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment          = new PrimitiveFunctor<>(null, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(null, BackgroundColor.class);

        this.setColor(color);
        this.setSize(size);
        this.setFont(null);
        this.setIsUnderlined(null);
        this.setAlignment(null);
        this.setBackgroundColor(null);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size,
                     Alignment alignment)
    {
        this.id                 = id;

        this.color              = new PrimitiveFunctor<>(color, TextColor.class);
        this.size               = new PrimitiveFunctor<>(size, TextSize.class);
        this.font               = new PrimitiveFunctor<>(null, TextFont.class);
        this.isUnderlined       = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment          = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(null, BackgroundColor.class);

        this.setColor(color);
        this.setSize(size);
        this.setFont(null);
        this.setIsUnderlined(null);
        this.setAlignment(alignment);
        this.setBackgroundColor(null);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size,
                     TextFont font)
    {
        this.id                 = id;

        this.color              = new PrimitiveFunctor<>(color, TextColor.class);
        this.size               = new PrimitiveFunctor<>(size, TextSize.class);
        this.font               = new PrimitiveFunctor<>(font, TextFont.class);
        this.isUnderlined       = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment          = new PrimitiveFunctor<>(null, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(null, BackgroundColor.class);

        this.setColor(color);
        this.setSize(size);
        this.setFont(font);
        this.setIsUnderlined(null);
        this.setAlignment(null);
        this.setBackgroundColor(null);
    }


    public TextStyle(UUID id,
                     TextColor color,
                     TextSize size,
                     TextFont font,
                     Alignment alignment)
    {
        this.id                 = id;

        this.color              = new PrimitiveFunctor<>(color, TextColor.class);
        this.size               = new PrimitiveFunctor<>(size, TextSize.class);
        this.font               = new PrimitiveFunctor<>(font, TextFont.class);
        this.isUnderlined       = new PrimitiveFunctor<>(null, Boolean.class);
        this.alignment          = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.backgroundColor    = new PrimitiveFunctor<>(null, BackgroundColor.class);

        this.setColor(color);
        this.setSize(size);
        this.setFont(font);
        this.setIsUnderlined(null);
        this.setAlignment(alignment);
        this.setBackgroundColor(null);
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
        return TextStyle.fromYaml(yaml, false);
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

        UUID            id           = UUID.randomUUID();

        TextColor       color        = TextColor.fromYaml(yaml.atMaybeKey("color"));
        TextSize        size         = TextSize.fromYaml(yaml.atMaybeKey("size"));
        TextFont        font         = TextFont.fromYaml(yaml.atMaybeKey("font"));
        Boolean         isUnderlined = yaml.atMaybeKey("is_underlined").getBoolean();
        Alignment       alignment    = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        BackgroundColor bgColor      = BackgroundColor.fromYaml(
                                                yaml.atMaybeKey("background_color"));

        return new TextStyle(id, color, size, font, isUnderlined, alignment, bgColor);
    }


    public static TextStyle asDefault()
    {
        TextStyle style = new TextStyle();

        style.setId(UUID.randomUUID());
        style.setColor(null);
        style.setSize(null);
        style.setFont(null);
        style.setIsUnderlined(null);
        style.setAlignment(null);
        style.setBackgroundColor(null);

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
        return YamlBuilder.map()
                .putYaml("color", this.color())
                .putYaml("size", this.size())
                .putYaml("font", this.font())
                .putBoolean("is_underlined", this.isUnderlined())
                .putYaml("alignment", this.alignment())
                .putYaml("background_color", this.backgroundColor());
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
            this.color.setValue(TextColor.THEME_MEDIUM);
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


    // ** Font
    // -----------------------------------------------------------------------------------------

    /**
     * The font.
     * @return The font.
     */
    public TextFont font()
    {
        return this.font.getValue();
    }


    /**
     * Set the text font
     * @param font The font.
     */
    public void setFont(TextFont font)
    {
        if (font != null)
            this.font.setValue(font);
        else
            this.font.setValue(TextFont.REGULAR);
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


    // ** Background Color
    // -----------------------------------------------------------------------------------------

    /**
     * The background color.
     * @return The background color.
     */
    public BackgroundColor backgroundColor()
    {
        return this.backgroundColor.getValue();
    }


    /**
     * Set the background color. If null, defaults to MEDIUM.
     * @param backgroundColor The background color.
     */
    public void setBackgroundColor(BackgroundColor backgroundColor)
    {
        if (backgroundColor != null)
            this.backgroundColor.setValue(backgroundColor);
        else
            this.backgroundColor.setValue(BackgroundColor.MEDIUM);
    }


    // > Typeface
    // -----------------------------------------------------------------------------------------

    public Typeface typeface(Context context)
    {
        switch (this.font())
        {
            case REGULAR:
                return Font.serifFontRegular(context);
            case BOLD:
                return Font.serifFontBold(context);
            case ITALIC:
                return Font.serifFontItalic(context);
            case BOLD_ITALIC:
                return Font.serifFontBoldItalic(context);
            default:
                return Font.serifFontRegular(context);
        }
    }


    // > Style Text View
    // -----------------------------------------------------------------------------------------

    /**
     * Format a text view with this style.
     * @param textView The text view.
     */
    public void styleTextView(TextView textView, Context context)
    {
        // ** Color
        textView.setTextColor(ContextCompat.getColor(context, this.color().resourceId()));

        // ** Size
        textView.setTextSize(context.getResources().getDimension(this.size().resourceId()));

        // ** Font
        textView.setTypeface(this.typeface(context));
    }


    // > Style Text View Builder
    // -----------------------------------------------------------------------------------------

    /**
     * Set the text view builder style options to match this style.
     * @param viewBuilder The Text View Builder.
     */
    public void styleTextViewBuilder(TextViewBuilder viewBuilder, Context context)
    {
        // ** Color
        viewBuilder.color   = this.color().resourceId();

        // ** Size
        viewBuilder.size    = this.size().resourceId();

        // ** Fton
        viewBuilder.font    = this.typeface(context);
    }

}
