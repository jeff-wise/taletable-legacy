
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
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
 * Text Cell Format
 */
public class TextCellFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The alignment of the content in the cell.
     */
    private PrimitiveFunctor<Alignment> alignment;

    /**
     * The cell value text style.
     */
    private ModelFunctor<TextStyle>     style;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TextCellFormat()
    {
        this.id         = null;

        this.alignment  = new PrimitiveFunctor<>(null, Alignment.class);
        this.style      = ModelFunctor.empty(TextStyle.class);
    }


    public TextCellFormat(UUID id, Alignment alignment, TextStyle style)
    {
        this.id         = id;

        this.alignment  = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.style      = ModelFunctor.full(style, TextStyle.class);

        this.setAlignment(alignment);
        this.setStyle(style);
    }


    /**
     * Create a Text Cell Format from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Text Cell Format.
     * @throws YamlParseException
     */
    public static TextCellFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TextCellFormat.asDefault();

        UUID      id        = UUID.randomUUID();

        Alignment alignment = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        TextStyle style     = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);

        return new TextCellFormat(id, alignment, style);
    }


    /**
     * Create a Text Cell Format with default values.
     * @return The default Text Cell Format.
     */
    public static TextCellFormat asDefault()
    {
        TextCellFormat format = new TextCellFormat();

        format.setId(UUID.randomUUID());
        format.setAlignment(null);
        format.setStyle(null);

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
                .putYaml("style", this.style());
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


}
