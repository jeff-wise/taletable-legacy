
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
 * Text Column Format
 */
public class TextColumnFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The column's default text style.
     */
    private ModelFunctor<TextStyle>         style;

    /**
     * The column's default alignment.
     */
    private PrimitiveFunctor<Alignment>     alignment;

    /**
     * The column's default width.
     */
    private PrimitiveFunctor<Integer>       width;

    /**
     * The column's default cell background.
     */
    private PrimitiveFunctor<Background>    background;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TextColumnFormat()
    {
        this.id         = null;

        this.style      = ModelFunctor.empty(TextStyle.class);
        this.alignment  = new PrimitiveFunctor<>(null, Alignment.class);
        this.width      = new PrimitiveFunctor<>(null, Integer.class);
        this.background = new PrimitiveFunctor<>(null, Background.class);
    }


    public TextColumnFormat(UUID id,
                            TextStyle style,
                            Alignment alignment,
                            Integer width,
                            Background background)
    {
        this.id         = id;

        this.style      = ModelFunctor.full(style, TextStyle.class);
        this.alignment  = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.width      = new PrimitiveFunctor<>(width, Integer.class);
        this.background = new PrimitiveFunctor<>(background, Background.class);
    }


    /**
     * Create a Text Column Format from its Yaml representation.
     *
     * @param yaml The yaml parser.
     * @return The parsed Text Column Format.
     * @throws YamlParseException
     */
    public static TextColumnFormat fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        if (yaml.isNull())
            return TextColumnFormat.asDefault();

        UUID       id         = UUID.randomUUID();

        TextStyle  style      = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        Alignment  alignment  = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        Integer    width      = yaml.atKey("width").getInteger();
        Background background = Background.fromYaml(yaml.atMaybeKey("background"));

        return new TextColumnFormat(id, style, alignment, width, background);
    }


    /**
     * Create a Text Column Format object with default values. The default values are all nulls,
     * because a column will override cell values, so if no value is specified, there should be no
     * default value.
     *
     * @return The default Text Column Format.
     */
    public static TextColumnFormat asDefault()
    {
        TextColumnFormat format = new TextColumnFormat();

        format.setId(UUID.randomUUID());
        format.setStyle(null);
        format.setAlignment(null);
        format.setWidth(null);
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
                .putYaml("style", this.style())
                .putYaml("alignment", this.alignment())
                .putInteger("width", this.width())
                .putYaml("background", this.background());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Style
    // --------------------------------------------------------------------------------------

    /**
     * The text column style. This style will override any cell style in the column.
     *
     * @return The text column style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the text style. The style may be null.
     *
     * @param style The text column style.
     */
    public void setStyle(TextStyle style)
    {
        this.style.setValue(style);
    }


    // ** Alignment
    // --------------------------------------------------------------------------------------

    /**
     * The text column alignment. This alignment will be the default for each cell in the column,
     * unless the cell specifies its own alignment.
     *
     * @return The text column alignment.
     */
    public Alignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the text column alignment.
     * @param alignment The alignment.
     */
    public void setAlignment(Alignment alignment)
    {
        this.alignment.setValue(alignment);
    }


    // ** Width
    // --------------------------------------------------------------------------------------

    /**
     * The text column width. This width will be the default for each cell in the column, unless
     * the cell specifies its own width.
     *
     * @return The width.
     */
    public Integer width()
    {
        return this.width.getValue();
    }


    /**
     * Set the text column width.
     * @param width The width.
     */
    public void setWidth(Integer width)
    {
        this.width.setValue(width);
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The text column default cell background.
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


}
