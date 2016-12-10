
package com.kispoko.tome.sheet.widget.util;


import android.content.Context;

import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.UUID;



/**
 * Widget Format
 */
public class WidgetFormat implements Model, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private UUID                      id;

    private PrimitiveValue<String>    label;
    private PrimitiveValue<Boolean>   showLabel;
    private PrimitiveValue<Integer>   row;
    private PrimitiveValue<Integer>   column;
    private PrimitiveValue<Integer>   width;
    private PrimitiveValue<Alignment> alignment;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public WidgetFormat()
    {
        this.id        = null;

        this.label     = new PrimitiveValue<>(null, String.class);
        this.showLabel = new PrimitiveValue<>(null, Boolean.class);
        this.row       = new PrimitiveValue<>(null, Integer.class);
        this.column    = new PrimitiveValue<>(null, Integer.class);
        this.width     = new PrimitiveValue<>(null, Integer.class);
        this.alignment = new PrimitiveValue<>(null, Alignment.class);

    }


    public WidgetFormat(UUID id,
                        String label,
                        Boolean showLabel,
                        Integer row,
                        Integer column,
                        Integer width,
                        Alignment alignment)
    {
        this.id = id;

        this.label     = new PrimitiveValue<>(label, String.class);
        this.showLabel = new PrimitiveValue<>(showLabel, Boolean.class);
        this.row       = new PrimitiveValue<>(row, Integer.class);
        this.column    = new PrimitiveValue<>(column, Integer.class);
        this.width     = new PrimitiveValue<>(width, Integer.class);
        this.alignment = new PrimitiveValue<>(alignment, Alignment.class);

        this.setLabel(label);
        this.setShowLabel(showLabel);
        this.setRow(row);
        this.setColumn(column);
        this.setWidth(width);
        this.setAlignment(alignment);
    }


    /**
     * Create a WidgetFormat object from its yaml representation.
     * @param yaml The yaml parser at the format node.
     * @return The parsed WidgetFormat object.
     */
    @SuppressWarnings("unchecked")
    protected static WidgetFormat fromYaml(Yaml yaml)
                     throws YamlException
    {
        UUID      id        = UUID.randomUUID();

        String    label     = yaml.atMaybeKey("label").getString();
        Boolean   showLabel = yaml.atMaybeKey("show_label").getBoolean();
        Integer   row       = yaml.atMaybeKey("row").getInteger();
        Integer   column    = yaml.atMaybeKey("column").getInteger();
        Integer   width     = yaml.atMaybeKey("width").getInteger();
        Alignment alignment = Alignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new WidgetFormat(id, label, showLabel, row, column, width, alignment);
    }


    // API
    // --------------------------------------------------------------------------------------

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
     * This method is called when the Widget Format is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Label
    // --------------------------------------------------------------------------------------

    /**
     * Get the component label, used to identify the component in the user interface.
     * @return The component label string.
     */
    public String getLabel()
    {
        return this.label.getValue();
    }


    /**
     * Set the component's label. Defaults to empty string.
     * @param label The component's display label.
     */
    public void setLabel(String label)
    {
        if (label != null)
            this.label.setValue(label);
        else
            this.label.setValue("");
    }


    // ** Show Label
    // --------------------------------------------------------------------------------------

    /**
     * Returns a flag that indicates whether the component label should be displayed.
     * @return WidgetData label display flag.
     */
    public Boolean getShowLabel()
    {
        return this.showLabel.getValue();
    }


    /**
     * Set a flag that determines whether the component's label is displayed. Defaults to true.
     * @param showLabel The showLabel flag.
     */
    public void setShowLabel(Boolean showLabel)
    {
        if (showLabel != null)
            this.showLabel.setValue(showLabel);
        else
            this.showLabel.setValue(true);
    }


    // ** Row
    // --------------------------------------------------------------------------------------

    /**
     * Get the row index that the component is in within its group.
     * @return The group row index of the component.
     */
    public Integer getRow()
    {
        return this.row.getValue();
    }


    /**
     * Set the group row of the component. Defaults to 1.
     * @param row The component's group row.
     */
    public void setRow(Integer row)
    {
        if (row != null)
            this.row.setValue(row);
        else
            this.row.setValue(1);
    }


    // ** ColumnUnion
    // --------------------------------------------------------------------------------------


    /**
     * Get the column index of the component. The column determines the component's position within
     * its row within the group.
     * @return The column index of the component.
     */
    public Integer getColumn()
    {
        return this.column.getValue();
    }


    /**
     * Set the column index of the component. Defaults to 1.
     * @param column
     */
    public void setColumn(Integer column)
    {
        if (column != null)
            this.column.setValue(column);
        else
            this.column.setValue(1);
    }


    // ** Width
    // --------------------------------------------------------------------------------------

    /**
     * Get the component width. The width of the component is a relative value that determines how
     * much space the component takes up horizontally in the group row.
     * @return The component width.
     */
    public Integer getWidth()
    {
        return this.width.getValue();
    }


    /**
     * Set the component width. Defaults to 1.
     * @param width
     */
    public void setWidth(Integer width)
    {
        if (width != null)
            this.width.setValue(width);
        else
            this.width.setValue(1);
    }


    // ** Alignment
    // --------------------------------------------------------------------------------------

    /**
     * Get the component's alignment. The alignment determines how the component's display
     * data is positioned within the component's area.
     * @return The component's alignment.
     */
    public Alignment getAlignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the components alignment within its box. Defaults to CENTER alignment.
     * @param alignment
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(Alignment.CENTER);
    }


    // NESTED DEFINTIONS
    // --------------------------------------------------------------------------------------

    /**
     * WidgetData alignment.
     */
    public enum Alignment
    {
        LEFT,
        CENTER,
        RIGHT;

        public static Alignment fromString(String alignmentString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Alignment.class, alignmentString);
        }


        public static Alignment fromYaml(Yaml yaml)
                      throws YamlException
        {
            String alignmentString = yaml.getString();
            try {
                return Alignment.fromString(alignmentString);
            } catch (InvalidDataException e) {
                throw YamlException.invalidEnum(new InvalidEnumError(alignmentString));
            }
        }


        public static Alignment fromSQLValue(SQLValue sqlValue)
                      throws DatabaseException
        {
            String enumString = "";
            try {
                enumString = sqlValue.getText();
                Alignment alignment = Alignment.fromString(enumString);
                return alignment;
            } catch (InvalidDataException e) {
                throw DatabaseException.invalidEnum(
                        new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
            }
        }

    }


    public enum Size
    {
        SMALL,
        MEDIUM,
        LARGE;


        public static Size fromString(String sizeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Size.class, sizeString);
        }


        /**
         * Creates a Size enum from its Yaml representation. If there is no Yaml representation
         * (it is null), then use MEDIUM as a default size.
         * @param yaml The Yaml parser.
         * @return A new Size enum, with MEDIUM as the default.
         * @throws YamlException
         */
        public static Size fromYaml(Yaml yaml)
                      throws YamlException
        {
            if (yaml.isNull()) return MEDIUM;

            String sizeString = yaml.getString();
            try {
                return Size.fromString(sizeString);
            } catch (InvalidDataException e) {
                throw YamlException.invalidEnum(new InvalidEnumError(sizeString));
            }
        }


        public static Size fromSQLValue(SQLValue sqlValue)
                      throws DatabaseException
        {
            String enumString = "";
            try {
                enumString = sqlValue.getText();
                Size size = Size.fromString(enumString);
                return size;
            } catch (InvalidDataException e) {
                throw DatabaseException.invalidEnum(
                        new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
            }
        }


        public float toSP(Context context)
        {
            switch (this)
            {
                case SMALL:
                    return context.getResources().getDimension(R.dimen.text_size_small);
                case MEDIUM:
                    return context.getResources().getDimension(R.dimen.text_size_medium);
                case LARGE:
                    return context.getResources().getDimension(R.dimen.text_size_large);
            }
            return 0;
        }


        public int resourceId()
        {
            switch (this)
            {
                case SMALL:
                    return R.dimen.text_size_small;
                case MEDIUM:
                    return R.dimen.text_size_medium;
                case LARGE:
                    return R.dimen.text_size_large;
            }

            return 0;
        }


    }


}

