
package com.kispoko.tome.sheet.widget.table.cell;


import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.table.column.Column;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;

import java.util.List;



/**
 * Cell Interface
 */
public abstract class Cell extends Model
{

    // ABSTRACT
    // -----------------------------------------------------------------------------------------

    public abstract List<Variable> namespacedVariables();
    public abstract Alignment      alignment();
    public abstract BackgroundColor background();


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
