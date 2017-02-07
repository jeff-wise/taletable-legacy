
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.Cell;
import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Text Column
 *
 * Contains metadata about the cells in a table's text column.
 */
public class TextColumn implements Model, Column, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private PrimitiveFunctor<String>        defaultValue;
    private PrimitiveFunctor<CellAlignment> alignment;
    private PrimitiveFunctor<Boolean>       isBold;
    private PrimitiveFunctor<Integer>       width;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextColumn()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.defaultValue   = new PrimitiveFunctor<>(null, String.class);
        this.alignment      = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.isBold         = new PrimitiveFunctor<>(null, Boolean.class);
        this.width          = new PrimitiveFunctor<>(null, Integer.class);
    }


    public TextColumn(UUID id,
                      String name,
                      String defaultValue,
                      CellAlignment alignment,
                      Boolean isBold,
                      Integer width)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.defaultValue   = new PrimitiveFunctor<>(defaultValue, String.class);
        this.alignment      = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.isBold         = new PrimitiveFunctor<>(isBold, Boolean.class);
        this.width          = new PrimitiveFunctor<>(width, Integer.class);

        this.setAlignment(alignment);
        this.setIsBold(isBold);
    }


    /**
     * Create a text column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Text ColumnUnion.
     * @throws YamlParseException
     */
    public static TextColumn fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID          id           = UUID.randomUUID();

        String        name         = yaml.atKey("name").getString();
        String        defaultValue = yaml.atKey("default_value").getString();
        CellAlignment alignment    = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        Boolean       isBold       = yaml.atMaybeKey("is_bold").getBoolean();
        Integer       width        = yaml.atKey("width").getInteger();

        return new TextColumn(id, name, defaultValue, alignment, isBold, width);
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
     * This method is called when the Text Column is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Column's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("default_value", this.defaultValue())
                .putYaml("alignment", this.alignment())
                .putBoolean("is_bold", this.isBold())
                .putInteger("width", this.width());
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
     * Get the alignment of this column. All cells in the column should have the same alignment.
     * @return The column alignment.
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

    // ** Alignment
    // ------------------------------------------------------------------------------------------

    /**
     * Set the alignment of the cells in the column.
     * @param alignment The cell alignment. If null, defaults to CENTER.
     */
    public void setAlignment(CellAlignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(CellAlignment.CENTER);
    }


    // ** Is Bold
    // ------------------------------------------------------------------------------------------

    /**
     * True if every cell in this column should have bold text.
     * @return Is Bold?
     */
    public Boolean isBold()
    {
        return this.isBold.getValue();
    }


    /**
     * Set the boldness of the cells in the column.
     * @param isBold True if bold. If null, defaults to false.
     */
    public void setIsBold(Boolean isBold)
    {
        if (isBold != null)
            this.isBold.setValue(isBold);
        else
            this.isBold.setValue(false);
    }


    // ** Default Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the default column value. Cells with null values are given this value (if this value
     * is not null).
     * @return The default value.
     */
    public String defaultValue()
    {
        return this.defaultValue.getValue();
    }

}
