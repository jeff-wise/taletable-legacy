
package com.kispoko.tome.sheet.widget.table.cell;


import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.sheet.widget.table.column.ColumnType;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Table Widget CellUnion
 */
public class CellUnion implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                     id;

    private ModelValue<TextCell>     textCell;
    private ModelValue<NumberCell>   numberCell;
    private ModelValue<BooleanCell>  booleanCell;

    private PrimitiveValue<CellType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public CellUnion()
    {
        this.id          = null;

        this.textCell    = ModelValue.empty(TextCell.class);
        this.numberCell  = ModelValue.empty(NumberCell.class);
        this.booleanCell = ModelValue.empty(BooleanCell.class);

        this.type        = new PrimitiveValue<>(null, CellType.class);
    }


    private CellUnion(UUID id, Object cell, CellType type)
    {
        this.id          = id;


        this.textCell    = ModelValue.full(null, TextCell.class);
        this.numberCell  = ModelValue.full(null, NumberCell.class);
        this.booleanCell = ModelValue.full(null, BooleanCell.class);

        this.type        = new PrimitiveValue<>(type, CellType.class);

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
     * @throws YamlException
     */
    public static CellUnion fromYaml(Yaml yaml, ColumnUnion columnUnion)
                  throws YamlException
    {
        UUID     id   = UUID.randomUUID();

        switch (columnUnion.getType())
        {
            case TEXT:
                TextCell textCell = TextCell.fromYaml(yaml, columnUnion.getTextColumn());
                return CellUnion.asText(id, textCell);
            case NUMBER:
                NumberCell numberCell = NumberCell.fromYaml(yaml, columnUnion.getNumberColumn());
                return CellUnion.asNumber(id, numberCell);
            case BOOLEAN:
                BooleanCell booleanCell = BooleanCell.fromYaml(yaml,
                                                               columnUnion.getBooleanColumn());
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


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the cell type.
     * @return The Cell Type.
     */
    public CellType getType()
    {
        return this.type.getValue();
    }


    // ** Cells
    // ------------------------------------------------------------------------------------------

    /**
     * Get the text column case.
     * @return The Text Column.
     */
    public TextCell getTextCell()
    {
        if (this.getType() != CellType.TEXT) {
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
    public NumberCell getNumberCell()
    {
        if (this.getType() != CellType.NUMBER) {
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
    public BooleanCell getBooleanCell()
    {
        if (this.getType() != CellType.BOOLEAN) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("boolean", this.type.toString())));
        }
        return this.booleanCell.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(ColumnUnion columnUnion)
    {
        View cellView = null;

        switch (this.type.getValue())
        {
            case TEXT:
                cellView = this.getTextCell().view(columnUnion.getTextColumn());
                break;
            case NUMBER:
                cellView = this.getNumberCell().view(columnUnion.getNumberColumn());
                break;
            case BOOLEAN:
                cellView = this.getBooleanCell().view(columnUnion.getBooleanColumn());
                break;
        }

        setCellViewAlignment(cellView, columnUnion);
        setCellViewWidth(cellView, columnUnion);

        return cellView;
    }


    private void setCellViewAlignment(View cellView, ColumnUnion columnUnion)
    {
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) cellView.getLayoutParams();

        CellAlignment cellAlignment = columnUnion.getColumn().getAlignment();

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


    private void setCellViewWidth(View cellView, ColumnUnion columnUnion)
    {
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) cellView.getLayoutParams();

        Integer width = columnUnion.getColumn().getWidth();
        if (width != null) {
            layoutParams.width = 0;
            layoutParams.weight = width;
        }
    }


}

