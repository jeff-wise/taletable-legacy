
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.Background;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Number Column Format
 */
public class NumberColumnFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The column's default width.
     */
    private PrimitiveFunctor<Integer>       width;

    /**
     * The column's default cell alignment.
     */
    private PrimitiveFunctor<Alignment>     alignment;

    /**
     * The column's default cell text style.
     */
    private ModelFunctor<TextStyle>         style;

    /**
     * A prefix that is displayed before the values in the number column.
     */
    private PrimitiveFunctor<String>        valuePrefix;

    /**
     * The default background for cells in the number column.
     */
    private PrimitiveFunctor<Background>    background;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberColumnFormat()
    {
        this.id             = null;

        this.width          = new PrimitiveFunctor<>(null, Integer.class);
        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
        this.style          = ModelFunctor.empty(TextStyle.class);
        this.valuePrefix    = new PrimitiveFunctor<>(null, String.class);
        this.background     = new PrimitiveFunctor<>(null, Background.class);
    }


    public NumberColumnFormat(UUID id,
                              Integer width,
                              Alignment alignment,
                              TextStyle style,
                              String valuePrefix,
                              Background background)
    {
        this.id             = id;

        this.width          = new PrimitiveFunctor<>(width, Integer.class);
        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.style          = ModelFunctor.full(style, TextStyle.class);
        this.valuePrefix    = new PrimitiveFunctor<>(valuePrefix, String.class);
        this.background     = new PrimitiveFunctor<>(background, Background.class);
    }


    /**
     * Create a Number Column Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Number Column Format.
     * @throws YamlParseException
     */
    public static NumberColumnFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return NumberColumnFormat.asDefault();

        UUID       id          = UUID.randomUUID();

        Integer    width       = yaml.atMaybeKey("width").getInteger();
        Alignment  alignment   = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        TextStyle  style       = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        String     valuePrefix = yaml.atMaybeKey("value_prefix").getString();
        Background background  = Background.fromYaml(yaml.atMaybeKey("background"));

        return new NumberColumnFormat(id, width, alignment, style, valuePrefix, background);
    }


    /**
     * Create a default Number Column Format with null values.
     * @return The default Number Column Format.
     */
    private static NumberColumnFormat asDefault()
    {
        NumberColumnFormat format = new NumberColumnFormat();

        format.setId(UUID.randomUUID());

        format.setAlignment(null);
        format.setWidth(null);

        format.setStyle(null);
        format.setValuePrefix(null);
        format.setBackground(null);

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
                .putInteger("width", this.width())
                .putYaml("style", this.style())
                .putYaml("background", this.background())
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
        this.alignment.setValue(alignment);
    }


    // ** Width
    // --------------------------------------------------------------------------------------

    /**
     * The default column width.
     * @return The width.
     */
    public Integer width()
    {
        return this.width.getValue();
    }


    /**
     * Set the default column width.
     * @param width The width.
     */
    public void setWidth(Integer width)
    {
        this.width.setValue(width);
    }


    // ** Style
    // --------------------------------------------------------------------------------------

    /**
     * The default column style.
     * @return The style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the default column style.
     * @param style The style.
     */
    public void setStyle(TextStyle style)
    {
        this.style.setValue(style);
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The number column default cell background.
     * @return The Background.
     */
    public Background background()
    {
        return this.background.getValue();
    }


    /**
     * Set the default cell background.
     * @param background The background.
     */
    public void setBackground(Background background)
    {
        this.background.setValue(background);
    }


    // ** Value Prefix
    // --------------------------------------------------------------------------------------

    /**
     * The default prefix for values in the column.
     * @return The value prefix.
     */
    public String valuePrefix()
    {
        return this.valuePrefix.getValue();
    }


    /**
     * Set the column's default value prefix.
     * @param valuePrefix The value prefix.
     */
    public void setValuePrefix(String valuePrefix)
    {
        this.valuePrefix.setValue(valuePrefix);
    }


}
