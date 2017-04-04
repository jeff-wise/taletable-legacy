
package com.kispoko.tome.sheet.widget.table.cell;


import android.content.Context;
import android.view.View;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.widget.table.TableRowFormat;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Table Widget CellUnion
 */
public class CellUnion extends Model
                       implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<TextCell>      textCell;
    private ModelFunctor<NumberCell>    numberCell;
    private ModelFunctor<BooleanCell>   booleanCell;

    private PrimitiveFunctor<CellType>  type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public CellUnion()
    {
        this.id          = null;

        this.textCell    = ModelFunctor.empty(TextCell.class);
        this.numberCell  = ModelFunctor.empty(NumberCell.class);
        this.booleanCell = ModelFunctor.empty(BooleanCell.class);

        this.type        = new PrimitiveFunctor<>(null, CellType.class);
    }


    private CellUnion(UUID id, Object cell, CellType type)
    {
        this.id          = id;


        this.textCell    = ModelFunctor.full(null, TextCell.class);
        this.numberCell  = ModelFunctor.full(null, NumberCell.class);
        this.booleanCell = ModelFunctor.full(null, BooleanCell.class);

        this.type        = new PrimitiveFunctor<>(type, CellType.class);

        switch (type)
        {
            case TEXT:
                this.textCell.setValue((TextCell) cell);
                break;
            case NUMBER:
                this.numberCell.setValue((NumberCell) cell);
                break;
            case BOOLEAN:
                this.booleanCell.setValue((BooleanCell) cell);
                break;
        }
    }


    /**
     * Create the "text" variant.
     * @param id The Model id.
     * @param textCell The text cell.
     * @return The new CellUnion as the text case.
     */
    public static CellUnion asText(UUID id, TextCell textCell)
    {
        return new CellUnion(id, textCell, CellType.TEXT);
    }


    /**
     * Create the "number" variant.
     * @param id The Model id.
     * @param numberCell The number cell.
     * @return The new CellUnion as the number case.
     */
    public static CellUnion asNumber(UUID id, NumberCell numberCell)
    {
        return new CellUnion(id, numberCell, CellType.NUMBER);
    }


    /**
     * Create the "boolean" variant.
     * @param id The Model id.
     * @param booleanCell The boolean cell.
     * @return The new CellUnion as the boolean case.
     */
    public static CellUnion asBoolean(UUID id, BooleanCell booleanCell)
    {
        return new CellUnion(id, booleanCell, CellType.BOOLEAN);
    }


    /**
     * Create a Cell Union from its Yaml representation.
     * @param yaml The Yaml parser.
     * @param columnUnion The column the cell belongs to.
     * @return The parsed Cell Union.
     * @throws YamlParseException
     */
    public static CellUnion fromYaml(YamlParser yaml, ColumnUnion columnUnion)
                  throws YamlParseException
    {
        UUID     id   = UUID.randomUUID();

        switch (columnUnion.type())
        {
            case TEXT:
                TextCell textCell = TextCell.fromYaml(yaml);
                return CellUnion.asText(id, textCell);
            case NUMBER:
                NumberCell numberCell = NumberCell.fromYaml(yaml);
                return CellUnion.asNumber(id, numberCell);
            case BOOLEAN:
                BooleanCell booleanCell = BooleanCell.fromYaml(yaml);
                return CellUnion.asBoolean(id, booleanCell);
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
     * This method is called when the Cell Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Cell Union's yaml representation.
     * @return
     */
    public YamlBuilder toYaml()
    {
        switch (this.type())
        {
            case TEXT:
                return this.textCell().toYaml();
            case NUMBER:
                return this.numberCell().toYaml();
            case BOOLEAN:
                return this.booleanCell().toYaml();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(CellType.class.getName())));
        }

        return null;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Cell
    // ------------------------------------------------------------------------------------------

    public Cell cell()
    {
        switch (this.type())
        {
            case TEXT:
                return this.textCell();
            case NUMBER:
                return this.numberCell();
            case BOOLEAN:
                return this.booleanCell();
        }

        return null;
    }


    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the cell type.
     * @return The Cell Type.
     */
    public CellType type()
    {
        return this.type.getValue();
    }


    // ** Cells
    // ------------------------------------------------------------------------------------------

    /**
     * Get the text column case.
     * @return The Text Column.
     */
    public TextCell textCell()
    {
        if (this.type() != CellType.TEXT) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("text", this.type.toString())));
        }
        return this.textCell.getValue();
    }


    /**
     * Get the text column case.
     * @return The Text Column.
     */
    public NumberCell numberCell()
    {
        if (this.type() != CellType.NUMBER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("number", this.type.toString())));
        }
        return this.numberCell.getValue();
    }


    /**
     * Get the text column case.
     * @return The Text Column.
     */
    public BooleanCell booleanCell()
    {
        if (this.type() != CellType.BOOLEAN) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("boolean", this.type.toString())));
        }
        return this.booleanCell.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(ColumnUnion columnUnion, TableRowFormat rowFormat, Context context)
    {
        View cellView = null;

        switch (this.type.getValue())
        {
            case TEXT:
                cellView = this.textCell().view(columnUnion.textColumn(), rowFormat, context);
                break;
            case NUMBER:
                cellView = this.numberCell().view(columnUnion.numberColumn(), rowFormat, context);
                break;
            case BOOLEAN:
                cellView = this.booleanCell().view(columnUnion.booleanColumn(), rowFormat, context);
                break;
        }

        //setCellViewAlignment(cellView, columnUnion);
        //setCellViewWidth(cellView, columnUnion);

        return cellView;
    }

/*
    private void setCellViewAlignment(View cellView, ColumnUnion columnUnion)
    {
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) cellView.getLayoutParams();

        CellAlignment cellAlignment = columnUnion.column().alignment();

        switch (cellAlignment)
        {
            case LEFT:
                layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                if (cellView instanceof TextView)
                    ((TextView) cellView).setGravity(Gravity.LEFT);
                break;
            case CENTER:
                layoutParams.gravity = Gravity.CENTER | Gravity.CENTER_VERTICAL;
                if (cellView instanceof TextView)
                    ((TextView) cellView).setGravity(Gravity.CENTER);
                break;
            case RIGHT:
                layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                if (cellView instanceof TextView)
                    ((TextView) cellView).setGravity(Gravity.RIGHT);
                break;
        }

    }
    */


    /*
    private void setCellViewWidth(View cellView, ColumnUnion columnUnion)
    {
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) cellView.getLayoutParams();

        Integer width = columnUnion.column().width();
        if (width != null) {
            layoutParams.width = 0;
            layoutParams.weight = width;
        }
    }
    */


}

