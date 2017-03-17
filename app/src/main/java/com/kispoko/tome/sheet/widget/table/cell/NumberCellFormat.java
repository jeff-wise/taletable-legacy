
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.TextColor;
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
 * Number Cell Format
 */
public class NumberCellFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The alignment of the content in the cell.
     */
    private PrimitiveFunctor<Alignment>     alignment;

    /**
     * The cell background color.
     */
    private PrimitiveFunctor<BackgroundColor>    background;

    /**
     * The cell value text style.
     */
    private ModelFunctor<TextStyle>         style;


    /**
     * A prefix that is prepended to the number value string.
     */
    private PrimitiveFunctor<String>        valuePrefix;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberCellFormat()
    {
        this.id             = null;

        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
        this.background     = new PrimitiveFunctor<>(null, BackgroundColor.class);
        this.style          = ModelFunctor.empty(TextStyle.class);
        this.valuePrefix    = new PrimitiveFunctor<>(null, String.class);
    }


    public NumberCellFormat(UUID id,
                            Alignment alignment,
                            BackgroundColor background,
                            TextStyle style,
                            String valuePrefix)
    {
        this.id             = id;

        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.background     = new PrimitiveFunctor<>(background, BackgroundColor.class);
        this.style          = ModelFunctor.full(style, TextStyle.class);
        this.valuePrefix    = new PrimitiveFunctor<>(valuePrefix, String.class);

        this.setAlignment(alignment);
        this.setBackground(background);
        this.setStyle(style);
    }


    /**
     * Create a Number Cell Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Number Cell Format.
     * @throws YamlParseException
     */
    public static NumberCellFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return NumberCellFormat.asDefault();

        UUID       id          = UUID.randomUUID();

        Alignment  alignment   = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        BackgroundColor background  = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));
        TextStyle  style       = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        String     valuePrefix = yaml.atMaybeKey("value_prefix").getString();

        return new NumberCellFormat(id, alignment, background, style, valuePrefix);
    }


    /**
     * A Number Cell Format with default values.
     * @return The default Number Cell Format.
     */
    public static NumberCellFormat asDefault()
    {
        NumberCellFormat format = new NumberCellFormat();

        format.setId(UUID.randomUUID());
        format.setAlignment(null);
        format.setBackground(null);
        format.setStyle(null);
        format.setValuePrefix(null);

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
                .putYaml("alignment", this.alignment())
                .putYaml("background", this.background())
                .putYaml("style", this.style())
                .putString("value_prefix", this.valuePrefix());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Alignment
    // --------------------------------------------------------------------------------------

    /**
     * The default column alignment.
     * @return The alignment
     */
    public Alignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the default column alignment.
     * @param alignment The alignment
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null) {
            this.alignment.setValue(alignment);
            this.alignment.setIsDefault(false);
        }
        else {
            this.alignment.setValue(Alignment.CENTER);
            this.alignment.setIsDefault(true);
        }
    }


    /**
     * Resolve the alignment setting between the column and the cell.
     * @param columnAlignment The column alignment (could be null).
     * @return The appropriate alignment.
     */
    public Alignment resolveAlignment(Alignment columnAlignment)
    {
        if (columnAlignment == null)
            return this.alignment();

        if (this.alignment.isDefault())
            return columnAlignment;

        return this.alignment();
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The cell background color.
     * @return The background.
     */
    public BackgroundColor background()
    {
        return this.background.getValue();
    }


    /**
     * Set the cell background color.
     * @param background The background.
     */
    public void setBackground(BackgroundColor background)
    {
        if (background != null) {
            this.background.setValue(background);
            this.background.setIsDefault(false);
        }
        else {
            this.background.setValue(BackgroundColor.MEDIUM);
            this.background.setIsDefault(true);
        }
    }


    /**
     * Resolve the background value between the column and the cell.
     * @param columnBackground The column background (could be null).
     * @return The appropriate background.
     */
    public BackgroundColor resolveBackground(BackgroundColor columnBackground)
    {
        if (columnBackground == null)
            return this.background();

        if (this.background.isDefault())
            return columnBackground;

        return this.background();
    }


    // ** Style
    // --------------------------------------------------------------------------------------

    /**
     * The default cell style.
     * @return The style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the default cell style. If null, a default style is set.
     * @param style The style.
     */
    public void setStyle(TextStyle style)
    {
        if (style != null) {
            this.style.setValue(style);
            this.style.setIsDefault(false);
        }
        else {
            TextStyle defaultTextCellStyle = new TextStyle(UUID.randomUUID(),
                                                           TextColor.THEME_MEDIUM,
                                                           TextSize.MEDIUM_SMALL);
            this.style.setValue(defaultTextCellStyle);
            this.style.setIsDefault(true);
        }
    }


    /**
     * Resolve the style setting between the column and the cell.
     * @param columnStyle The column style (could be null).
     * @return The appropriate style.
     */
    public TextStyle resolveStyle(TextStyle columnStyle)
    {
        if (columnStyle == null)
            return this.style();

        if (this.style.isDefault())
            return columnStyle;

        return this.style();
    }


    // ** Value Prefix
    // --------------------------------------------------------------------------------------

    /**
     * The value prefix (may be null).
     * @return The prefix string.
     */
    public String valuePrefix()
    {
        return this.valuePrefix.getValue();
    }


    public void setValuePrefix(String valuePrefix)
    {
        this.valuePrefix.setValue(valuePrefix);
    }


    public String resolveValuePrefix(String columnValuePrefix)
    {
        if (columnValuePrefix == null)
            return this.valuePrefix();

        if (this.valuePrefix.isNull())
            return columnValuePrefix;

        return this.valuePrefix();
    }


}
