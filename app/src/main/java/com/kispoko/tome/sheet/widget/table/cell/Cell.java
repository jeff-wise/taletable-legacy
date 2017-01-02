
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;

import java.util.List;



/**
 * Cell Interface
 */
public interface Cell
{
    void setWidgetContainer(WidgetContainer widgetContainer);
    List<Variable> namespacedVariables();
}
