
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.functor.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Table Widget Format
 */
public class TableWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<DividerType>       dividerType;
    private PrimitiveFunctor<Height>            cellHeight;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TableWidgetFormat()
    {
        this.id             = null;

        this.dividerType    = new PrimitiveFunctor<>(null, DividerType.class);
        this.cellHeight     = new PrimitiveFunctor<>(null, Height.class);
    }


    public TableWidgetFormat(UUID id, DividerType dividerType, Height cellHeight)
    {
        this.id             = id;

        this.dividerType    = new PrimitiveFunctor<>(dividerType, DividerType.class);
        this.cellHeight     = new PrimitiveFunctor<>(cellHeight, Height.class);

        this.setDividerType(dividerType);
        this.setCellHeight(cellHeight);
    }


    /**
     * Create a Table Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Table Widget Format.
     * @throws YamlParseException
     */
    public static TableWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TableWidgetFormat.asDefault();

        UUID            id              = UUID.randomUUID();

        DividerType     dividerType     = DividerType.fromYaml(yaml.atMaybeKey("divider_type"));
        Height          cellHeight      = Height.fromYaml(yaml.atMaybeKey("cell_height"));

        return new TableWidgetFormat(id, dividerType, cellHeight);
    }


    /**
     * Create a Table Widget Format with default values.
     * @return The default Table Widget Format.
     */
    private static TableWidgetFormat asDefault()
    {
        TableWidgetFormat format = new TableWidgetFormat();

        format.setId(UUID.randomUUID());
        format.setDividerType(null);
        format.setCellHeight(null);

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
                .putYaml("divider_type", this.dividerType())
                .putYaml("cell_height", this.cellHeight());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Divider Type
    // --------------------------------------------------------------------------------------

    /**
     * The table row divider type.
     * @return The divider type.
     */
    public DividerType dividerType()
    {
        return this.dividerType.getValue();
    }


    /**
     * Set the table row divider type. If null, defaults to light dividers.
     * @param dividerType The divider type.
     */
    public void setDividerType(DividerType dividerType)
    {
        if (dividerType != null)
            this.dividerType.setValue(dividerType);
        else
            this.dividerType.setValue(DividerType.LIGHT);
    }


    // ** Cell Height
    // --------------------------------------------------------------------------------------

    /**
     * The height of the cells in the table.
     * @return The cell height.
     */
    public Height cellHeight()
    {
        return this.cellHeight.getValue();
    }


    /**
     * Set the cell height.
     * @param cellHeight The cell height.
     */
    public void setCellHeight(Height cellHeight)
    {
        this.cellHeight.setValue(cellHeight);
    }

}
