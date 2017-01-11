
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;



/**
 * Column Interface
 */
public interface Column
{
    String name();
    CellAlignment alignment();
    Integer width();
}
