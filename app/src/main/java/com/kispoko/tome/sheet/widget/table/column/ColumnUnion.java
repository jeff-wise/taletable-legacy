
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Table ColumnUnion
 *
 * Contains metadata about the cells in a table column.
 */
public class ColumnUnion implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                       id;

    private ModelValue<TextColumn>     textColumn;
    private ModelValue<NumberColumn>   numberColumn;
    private ModelValue<BooleanColumn>  booleanColumn;

    private PrimitiveValue<ColumnType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ColumnUnion()
    {
        this.id            = null;

        this.textColumn    = ModelValue.empty(TextColumn.class);
        this.numberColumn  = ModelValue.empty(NumberColumn.class);
        this.booleanColumn = ModelValue.empty(BooleanColumn.class);

        this.type          = new PrimitiveValue<>(null, ColumnType.class);
    }


    private ColumnUnion(UUID id, Object column, ColumnType type)
    {
        this.id   = id;

        this.textColumn    = ModelValue.full(null, TextColumn.class);
        this.numberColumn  = ModelValue.full(null, NumberColumn.class);
        this.booleanColumn = ModelValue.full(null, BooleanColumn.class);

        this.type          = new PrimitiveValue<>(type, ColumnType.class);

        switch (type)
        {
            case TEXT:
                this.textColumn.setValue((TextColumn) column);
                break;
            case NUMBER:
                this.numberColumn.setValue((NumberColumn) column);
                break;
            case BOOLEAN:
                this.booleanColumn.setValue((BooleanColumn) column);
                break;
        }
    }


    /**
     * Create the "text" variant.
     * @param id The Model id.
     * @param textColumn The text column.
     * @return The new ColumnUnion as the text case.
     */
    public static ColumnUnion asText(UUID id, TextColumn textColumn)
    {
        return new ColumnUnion(id, textColumn, ColumnType.TEXT);
    }


    /**
     * Create the "number" variant.
     * @param id The Model id.
     * @param numberColumn The number column.
     * @return The new ColumnUnion as the number case.
     */
    public static ColumnUnion asNumber(UUID id, NumberColumn numberColumn)
    {
        return new ColumnUnion(id, numberColumn, ColumnType.NUMBER);
    }


    /**
     * Create the "boolean" variant.
     * @param id The Model id.
     * @param booleanColumn The boolean column.
     * @return The new ColumnUnion as the boolean case.
     */
    public static ColumnUnion asBoolean(UUID id, BooleanColumn booleanColumn)
    {
        return new ColumnUnion(id, booleanColumn, ColumnType.BOOLEAN);
    }


    public static ColumnUnion fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID       id   = UUID.randomUUID();
        ColumnType type = ColumnType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case TEXT:
                TextColumn textColumn = TextColumn.fromYaml(yaml.atKey("column"));
                return ColumnUnion.asText(id, textColumn);
            case NUMBER:
                NumberColumn numberColumn = NumberColumn.fromYaml(yaml.atKey("column"));
                return ColumnUnion.asNumber(id, numberColumn);
            case BOOLEAN:
                BooleanColumn booleanColumn = BooleanColumn.fromYaml(yaml.atKey("column"));
                return ColumnUnion.asBoolean(id, booleanColumn);
        }

        // CANNOT REACH HERE. If VariableKind is null, an InvalidEnum exception would be thrown.
        return null;

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
     * This method is called when the Column Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Column Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the type of column in the union.
     * @return The column type.
     */
    public ColumnType getType()
    {
        return this.type.getValue();
    }


    // ** Column
    // ------------------------------------------------------------------------------------------

    public Column getColumn()
    {
        switch (this.getType())
        {
            case TEXT:
                return this.textColumn.getValue();
            case NUMBER:
                return this.numberColumn.getValue();
            case BOOLEAN:
                return this.booleanColumn.getValue();
        }

        // CANNOT REACH HERE. No constructor allows null column type.
        return null;
    }


    /**
     * Get the text column case.
     * @return The Text Column.
     */
    public TextColumn getTextColumn()
    {
        if (this.getType() != ColumnType.TEXT) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("text", this.type.toString())));
        }
        return this.textColumn.getValue();
    }


    /**
     * Get the number column case.
     * @return The Number Column.
     */
    public NumberColumn getNumberColumn()
    {
        if (this.getType() != ColumnType.NUMBER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("number", this.type.toString())));
        }
        return this.numberColumn.getValue();
    }


    /**
     * Get the boolean column case.
     * @return The Boolean Column.
     */
    public BooleanColumn getBooleanColumn()
    {
        if (this.getType() != ColumnType.BOOLEAN) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("boolean", this.type.toString())));
        }
        return this.booleanColumn.getValue();
    }

}
