
package com.kispoko.tome.rts.entity.sheet


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.sheet.widget.WidgetId
import com.kispoko.tome.model.sheet.widget.table.TableWidgetCellType
import com.kispoko.tome.model.sheet.widget.table.TableWidgetColumnType
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
