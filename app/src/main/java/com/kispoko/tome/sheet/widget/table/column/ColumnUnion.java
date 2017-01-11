
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Table ColumnUnion
 *
 * Contains metadata about the cells in a table column.
 */
public class ColumnUnion implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                       id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<TextColumn> textColumn;
    private ModelFunctor<NumberColumn> numberColumn;
    private ModelFunctor<BooleanColumn> booleanColumn;

    private PrimitiveFunctor<ColumnType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ColumnUnion()
    {
        this.id            = null;

        this.textColumn    = ModelFunctor.empty(TextColumn.class);
        this.numberColumn  = ModelFunctor.empty(NumberColumn.class);
        this.booleanColumn = ModelFunctor.empty(BooleanColumn.class);

        this.type          = new PrimitiveFunctor<>(null, ColumnType.class);
    }


    private ColumnUnion(UUID id, Object column, ColumnType type)
    {
        this.id   = id;

        this.textColumn    = ModelFunctor.full(null, TextColumn.class);
        this.numberColumn  = ModelFunctor.full(null, NumberColumn.class);
        this.booleanColumn = ModelFunctor.full(null, BooleanColumn.class);

        this.type          = new PrimitiveFunctor<>(type, ColumnType.class);

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


    public static ColumnUnion fromYaml(YamlParser yaml)
                  throws YamlParseException
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Column Union's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder columnYaml = null;
        switch (this.type())
        {
            case TEXT:
                columnYaml = this.textColumn().toYaml();
                break;
            case NUMBER:
                columnYaml = this.numberColumn().toYaml();
                break;
            case BOOLEAN:
                columnYaml = this.booleanColumn().toYaml();
                break;
        }

        return YamlBuilder.map()
                .putYaml("type", this.type())
                .putYaml("column", columnYaml);

    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Column Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the type of column in the union.
     * @return The column type.
     */
    public ColumnType type()
    {
        return this.type.getValue();
    }


    // ** Column
    // ------------------------------------------------------------------------------------------

    public Column column()
    {
        switch (this.type())
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
    public TextColumn textColumn()
    {
        if (this.type() != ColumnType.TEXT) {
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
    public NumberColumn numberColumn()
    {
        if (this.type() != ColumnType.NUMBER) {
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
    public BooleanColumn booleanColumn()
    {
        if (this.type() != ColumnType.BOOLEAN) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("boolean", this.type.toString())));
        }
        return this.booleanColumn.getValue();
    }

}
