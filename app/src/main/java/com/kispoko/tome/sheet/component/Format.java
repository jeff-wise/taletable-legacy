
package com.kispoko.tome.sheet.component;


import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;



/**
 * Component Format
 */
public class Format implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String label;
    private Boolean showLabel;
    private Integer row;
    private Integer column;
    private Integer width;
    private Alignment alignment;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Format(String label,
                  Boolean showLabel,
                  Integer row,
                  Integer column,
                  Integer width,
                  Alignment alignment)
    {
        this.setLabel(label);
        this.setShowLabel(showLabel);
        this.setRow(row);
        this.setColumn(column);
        this.setWidth(width);
        this.setAlignment(alignment);
    }


    /**
     * Create a Format object from its yaml representation.
     * @param yaml The yaml parser at the format node.
     * @return The parsed Format object.
     */
    @SuppressWarnings("unchecked")
    protected static Format fromYaml(Yaml yaml)
                     throws YamlException
    {
        String    label     = yaml.atMaybeKey("label").getString();
        Boolean   showLabel = yaml.atMaybeKey("show_label").getBoolean();
        Integer   row       = yaml.atMaybeKey("row").getInteger();
        Integer   column    = yaml.atMaybeKey("column").getInteger();
        Integer   width     = yaml.atMaybeKey("width").getInteger();
        Alignment alignment = Alignment.fromString(yaml.atMaybeKey("alignment").getString());

        return new Format(label, showLabel, row, column, width, alignment);
    }


    // API
    // --------------------------------------------------------------------------------------

    // > Label
    // --------------------------------------------------------------------------------------

    /**
     * Get the component label, used to identify the component in the user interface.
     * @return The component label string.
     */
    public String getLabel()
    {
        return this.label;
    }


    /**
     * Set the component's label. Defaults to empty string.
     * @param label The component's display label.
     */
    public void setLabel(String label)
    {
        if (label != null)
            this.label = label;
        else
            this.label = "";
    }


    // > Show Label
    // --------------------------------------------------------------------------------------

    /**
     * Returns a flag that indicates whether the component label should be displayed.
     * @return Component label display flag.
     */
    public Boolean getShowLabel()
    {
        return this.showLabel;
    }


    /**
     * Set a flag that determines whether the component's label is displayed. Defaults to true.
     * @param showLabel The showLabel flag.
     */
    public void setShowLabel(Boolean showLabel)
    {
        if (showLabel != null)
            this.showLabel = showLabel;
        else
            this.showLabel = true;
    }


    /**
     * Get the row index that the component is in within its group.
     * @return The group row index of the component.
     */
    public Integer getRow()
    {
        return this.row;
    }


    /**
     * Set the group row of the component. Defaults to 1.
     * @param row The component's group row.
     */
    public void setRow(Integer row)
    {
        if (row != null)
            this.row = row;
        else
            this.row = 1;
    }


    /**
     * Get the column index of the component. The column determines the component's position within
     * its row within the group.
     * @return The column index of the component.
     */
    public Integer getColumn()
    {
        return this.column;
    }


    /**
     * Set the column index of the component. Defaults to 1.
     * @param column
     */
    public void setColumn(Integer column)
    {
        if (column != null)
            this.column = column;
        else
            this.column = 1;
    }


    /**
     * Get the component width. The width of the component is a relative value that determines how
     * much space the component takes up horizontally in the group row.
     * @return The component width.
     */
    public Integer getWidth()
    {
        return this.width;
    }


    /**
     * Set the component width. Defaults to 1.
     * @param width
     */
    public void setWidth(Integer width)
    {
        if (width != null)
            this.width = width;
        else
            this.width = 1;
    }


    /**
     * Get the component's alignment. The alignment determines how the component's display
     * data is positioned within the component's area.
     * @return The component's alignment.
     */
    public Alignment getAlignment()
    {
        return this.alignment;
    }


    /**
     * Set the components alignment within its box. Defaults to CENTER alignment.
     * @param alignment
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null)
            this.alignment = alignment;
        else
            this.alignment = Alignment.CENTER;
    }


    // NESTED DEFINTIONS
    // --------------------------------------------------------------------------------------

    /**
     * Component alignment.
     */
    public enum Alignment
    {
        LEFT,
        CENTER,
        RIGHT;

        public static Alignment fromString(String alignment)
        {
            if (alignment != null)
                return Alignment.valueOf(alignment.toUpperCase());
            return Alignment.LEFT;
        }

    }


}

