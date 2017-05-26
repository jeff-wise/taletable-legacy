
package com.kispoko.tome.model.sheet.widget.table.cell;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.kispoko.tome.engine.definition.variable.Variable;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.model.sheet.BackgroundColor;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;

import java.util.List;
import java.util.UUID;



/**
 * Cell Interface
 */
public abstract class Cell extends Model
{

    // ABSTRACT
    // -----------------------------------------------------------------------------------------

    public abstract List<Variable>  namespacedVariables();
    public abstract Alignment       alignment();
    public abstract BackgroundColor background();
    public abstract void            openEditor(AppCompatActivity activity);
    public abstract UUID            parentTableWidgetId();
    public abstract void            setUnionId(UUID id);
    public abstract UUID            unionId();


    // SHARED METHODS
    // -----------------------------------------------------------------------------------------

    protected LinearLayout layout(Column column,
                                  TextSize textSize,
                                  Height cellHeight,
                                  Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.layoutType       = LayoutType.TABLE_ROW;
        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = 0;
        layout.height           = TableRow.LayoutParams.WRAP_CONTENT;
        layout.weight           = column.width().floatValue();

        // > Alignment
        Alignment cellAlignment = this.alignment();

        if (column.alignment() != null)
            cellAlignment = column.alignment();

        layout.gravity          = cellAlignment.gravityConstant() | Gravity.CENTER_VERTICAL;


        if (cellHeight == null)
        {
            switch (textSize)
            {
                case VERY_SMALL:
                    cellHeight = Height.VERY_SMALL;
                    break;
                case SMALL:
                    cellHeight = Height.SMALL;
                    break;
                case MEDIUM_SMALL:
                    cellHeight = Height.MEDIUM_SMALL;
                    break;
                case MEDIUM:
                    cellHeight = Height.MEDIUM;
                    break;
                case MEDIUM_LARGE:
                    cellHeight = Height.MEDIUM_LARGE;
                    break;
                case LARGE:
                    cellHeight = Height.LARGE;
                    break;
                default:
                    cellHeight = Height.MEDIUM_SMALL;
            }

        }

        layout.backgroundColor      = this.background().colorId();
        layout.backgroundResource   = cellHeight.cellBackgroundResourceId();


        return layout.linearLayout(context);
    }


}
