
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.util.TextStyle;



/**
 * Column Interface
 */
public interface Column
{
    String    name();
    TextStyle style();
    Alignment alignment();
    Integer   width();
}
