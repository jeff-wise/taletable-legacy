
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.UUID;



/**
 * Number ColumnUnion
 *
 * Contains metadata about the cells in a table's number column.
 */
public class NumberColumn implements Model, Column, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private PrimitiveFunctor<Integer>       defaultValue;
    private PrimitiveFunctor<String>        defaultLabel;
    private PrimitiveFunctor<CellAlignment> alignment;

    /**
     * The column's text style. Any style elements defined are applied to each cell in the column.
     */
    private ModelFunctor<TextStyle>         style;

    private PrimitiveFunctor<Integer>       width;
    private PrimitiveFunctor<Boolean>       isNamespaced;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberColumn()
    {
        this.id           = null;

        this.name         = new PrimitiveFunctor<>(null, String.class);
        this.defaultValue = new PrimitiveFunctor<>(null, Integer.class);
        this.defaultLabel = new PrimitiveFunctor<>(null, String.class);
        this.alignment    = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.style        = ModelFunctor.empty(TextStyle.class);
        this.width        = new PrimitiveFunctor<>(null, Integer.class);
        this.isNamespaced = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public NumberColumn(UUID id,
                        String name,
                        Integer defaultValue,
                        String defaultLabel,
                        CellAlignment alignment,
                        TextStyle style,
                        Integer width,
                        Boolean isNamespaced)
    {
        this.id           = id;

        this.name         = new PrimitiveFunctor<>(name, String.class);
        this.defaultValue = new PrimitiveFunctor<>(defaultValue, Integer.class);
        this.defaultLabel = new PrimitiveFunctor<>(defaultLabel, String.class);
        this.alignment    = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.style        = ModelFunctor.full(style, TextStyle.class);
        this.width        = new PrimitiveFunctor<>(width, Integer.class);
        this.isNamespaced = new PrimitiveFunctor<>(isNamespaced, Boolean.class);

        this.setAlignment(alignment);
        this.setStyle(style);
        this.setIsNamespaced(isNamespaced);
    }


    /**
     * Create a number column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Number ColumnUnion.
     * @throws YamlParseException
     */
    public static NumberColumn fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID          id           = UUID.randomUUID();

        String        name         = yaml.atKey("name").getString();
        Integer       defaultValue = yaml.atKey("default_value").getInteger();
        String        defaultLabel = yaml.atMaybeKey("default_label").getString();
        CellAlignment alignment    = CellAlignment.fromYaml(yaml.atKey("alignment"));
        TextStyle     style        = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        Integer       width        = yaml.atKey("width").getInteger();
        Boolean       isNamespaced = yaml.atMaybeKey("namespaced").getBoolean();

        return new NumberColumn(id, name, defaultValue, defaultLabel, alignment, style,
                                width, isNamespaced);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Number Column is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Number Column's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putInteger("default_value", this.defaultValue())
                .putString("default_label", this.defaultLabel())
                .putYaml("alignment", this.alignment())
                .putYaml("style", this.style())
                .putInteger("width", this.width())
                .putBoolean("namespaced", this.isNamespaced());
    }


    // > Column
    // ------------------------------------------------------------------------------------------

    /**
     * Get the column name.
     * @return The column name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * Get the alignment of this cell.
     * @return The cell Alignment.
     */
    public CellAlignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Get the column width. All cells in the column should have the same width.
     * @return The column width.
     */
    public Integer width()
    {
        return this.width.getValue();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Aligment
    // ------------------------------------------------------------------------------------------

    public void setAlignment(CellAlignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(CellAlignment.CENTER);
    }


    // ** Style
    // ------------------------------------------------------------------------------------------

    /**
     * The column's text style. Any style elements defined are applied to each cell in the column.
     * @return The column Text Style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the number column text style that is applied to all cells in the column. If the style is
     * null, then a default style is created.
     * @param style The text style.
     */
    public void setStyle(TextStyle style)
    {
        if (style != null) {
            this.style.setValue(style);
        }
        else {
            TextStyle defaultNumberColumnStyle = new TextStyle(UUID.randomUUID(),
                                                               TextColor.MEDIUM,
                                                               TextSize.MEDIUM_SMALL);
            this.style.setValue(defaultNumberColumnStyle);
        }
    }


    // ** Default Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the default column value. Cells with null values are given this value (if this value
     * is not null).
     * @return The default value.
     */
    public Integer defaultValue()
    {
        return this.defaultValue.getValue();
    }


    // ** Default Label
    // ------------------------------------------------------------------------------------------

    /**
     * The default column label. Variables in this column will get this label, if their label is
     * null (and this is not null).
     * @return The default value.
     */
    public String defaultLabel()
    {
        return this.defaultLabel.getValue();
    }


    // ** Is Namespaced
    // ------------------------------------------------------------------------------------------

    /**
     * True if the cells in this column are namespaced.
     * @return Is namespaced?
     */
    public Boolean isNamespaced()
    {
        return this.isNamespaced.getValue();
    }


    /**
     * Set to true if the column is namespaced.
     * @param isNamespaced True if this column is namespaced.
     */
    public void setIsNamespaced(Boolean isNamespaced)
    {
        if (isNamespaced != null)
            this.isNamespaced.setValue(isNamespaced);
        else
            this.isNamespaced.setValue(false);
    }


}
