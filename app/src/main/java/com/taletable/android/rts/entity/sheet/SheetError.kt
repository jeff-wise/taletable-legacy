
package com.taletable.android.rts.entity.sheet


import com.taletable.android.app.ApplicationError
import com.taletable.android.model.sheet.widget.WidgetId
import com.taletable.android.model.sheet.widget.table.TableWidgetCellType
import com.taletable.android.model.sheet.widget.table.TableWidgetColumnType
import java.util.*



/**
 * Sheet Error
 */
sealed class SheetError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


/**
 * The theme does not have a color with the given id.
 */
class CellTypeDoesNotMatchColumnType(val cellType : TableWidgetCellType,
                                     val columnType : TableWidgetColumnType) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Cell Type Does Not Match Its Column Type
                Cell Type: $cellType
                Column Type: $columnType
            """

    override fun logMessage(): String = userMessage()
}


/**
 * The theme does not have a color with the given id.
 */
class CellVariableUndefined(val cellId : UUID) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Cell Variable Is Undefined
                Cell Id: $cellId
            """

    override fun logMessage(): String = userMessage()
}

/**
 * The theme does not have a color with the given id.
 */
class CellDoesNotHaveBookReference(val cellId : UUID) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Cell Variable Is Undefined
                Cell Id: $cellId
            """

    override fun logMessage(): String = userMessage()
}


/**
 * The theme does not have a color with the given id.
 */
class TableWidgetDoesNotHaveColumnVariableId(val widgetId : WidgetId) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Table Widget Does Not Have Column VariableId
                Widget Id: $widgetId
            """

    override fun logMessage(): String = userMessage()
}


/**
 * The theme does not have a color with the given id.
 */
class SheetDoesNotHaveWidget(val widgetId : WidgetId) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Sheet Does Not Have Widget
                Widget Id: $widgetId
            """

    override fun logMessage(): String = userMessage()
}


/**
 * The expander widget doesn't have a checkbox variable
 */
class ExpanderWidgetDoesNotHaveCheckboxVariable(val widgetId : WidgetId) : SheetError()
{
    override fun debugMessage(): String =
            """
            Sheet Error: Expander Widget Does Not Have Checkbox Variable
                Widget Id: $widgetId
            """

    override fun logMessage(): String = userMessage()
}
