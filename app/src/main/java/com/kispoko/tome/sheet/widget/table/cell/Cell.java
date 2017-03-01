
package com.kispoko.tome.sheet.widget.table.cell;


import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.table.column.Column;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;

import java.util.List;



/**
 * Cell Interface
 */
public abstract class Cell
{

    // ABSTRACT
    // -----------------------------------------------------------------------------------------

    public abstract List<Variable> namespacedVariables();
    public abstract Alignment      alignment();
    public abstract BackgroundColor background();


    // SHARED METHODS
    // -----------------------------------------------------------------------------------------

    protected LinearLayout layout(Column column, Context context)
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


        layout.backgroundColor      = this.background().colorId();
        layout.backgroundResource   = R.drawable.bg_cell;


        return layout.linearLayout(context);
    }


}
