
package com.kispoko.tome.rts.entity.sheet


import com.kispoko.tome.app.ApplicationEvent
import com.kispoko.tome.app.EventTypeSheetUpdate
import com.kispoko.tome.model.engine.value.ValueId
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.WidgetId
import java.io.Serializable
import java.util.*



/**
 * Sheet Update
 *
 * Defines all types of udpates that can happen to a sheet.
 */


data class SheetUpdateEvent(val sheetUpdate : SheetUpdate,
                            val sheetId : SheetId) : ApplicationEvent
{
    override fun debugMessage() : String
    {
        return """Sheet Updated:
               |    Sheet Id: $sheetId
               |    Update: $sheetUpdate
               """.trimMargin()
    }

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeSheetUpdate
}



data class WidgetReference(val widgetId : UUID, val widgetViewId : Int)



// ---------------------------------------------------------------------------------------------
// SHEET UPDATE
// ---------------------------------------------------------------------------------------------

sealed class SheetUpdate


// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update
// ---------------------------------------------------------------------------------------------

sealed class SheetUpdateWidget(open val widgetId : WidgetId) : SheetUpdate()


// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update > Action Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateActionWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class ActionWidgetUpdate(override val widgetId : WidgetId) : WidgetUpdateActionWidget(widgetId)


// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update > Boolean Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateBooleanWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class BooleanWidgetUpdateToggle(override val widgetId : WidgetId) : WidgetUpdateBooleanWidget(widgetId)

data class BooleanWidgetUpdateSetValue(override val widgetId : WidgetId,
                                       val newValue : Boolean) : WidgetUpdateBooleanWidget(widgetId)

// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update > List Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateListWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class ListWidgetUpdateSetCurrentValue(
        override val widgetId : WidgetId,
        val newCurrentValue : List<String>) : WidgetUpdateListWidget(widgetId)


data class ListWidgetUpdateAddValue(
        override val widgetId : WidgetId,
        val newValue : String) : WidgetUpdateListWidget(widgetId)


// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update > Number Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateNumberWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class NumberWidgetUpdateValue(override val widgetId : WidgetId,
                                   val newValue : Double) : WidgetUpdateNumberWidget(widgetId)


// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update > Points Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdatePointsWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)


data class PointsWidgetUpdateSetCurrentValue(
                    override val widgetId : WidgetId,
                    val newCurrentValue : Double) : WidgetUpdatePointsWidget(widgetId)


// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update > Story Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateStoryWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)


data class StoryWidgetUpdateNumberPart(override val widgetId : WidgetId,
                                       val partIndex : Int,
                                       val newNumber : Double) : WidgetUpdateStoryWidget(widgetId)

data class StoryWidgetUpdateTextValuePart(override val widgetId : WidgetId,
                                          val partIndex : Int,
                                          val newValueId : ValueId) : WidgetUpdateStoryWidget(widgetId)


// ---------------------------------------------------------------------------------------------
// SHEET UPDATE > Widget Update > Table Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateTableWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class TableWidgetUpdateSetNumberCell(override val widgetId : WidgetId,
                                          val cellId : UUID,
                                          val newNumber : Double) : WidgetUpdateTableWidget(widgetId)

data class TableWidgetUpdateSetTextCellValue(override val widgetId : WidgetId,
                                             val cellId : UUID,
                                             val newValueId : ValueId) : WidgetUpdateTableWidget(widgetId)

data class TableWidgetUpdateInsertRowBefore(
                            override val widgetId : WidgetId,
                            val selectedRow : Int) : WidgetUpdateTableWidget(widgetId)

data class TableWidgetUpdateInsertRowAfter(
                            override val widgetId : WidgetId,
                            val selectedRow : Int) : WidgetUpdateTableWidget(widgetId)

data class TableWidgetUpdateSubset(override val widgetId : WidgetId,
                                   val values : List<String>) : WidgetUpdateTableWidget(widgetId)


// ---------------------------------------------------------------------------------------------
// WIDGET UPDATE > Text Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateTextWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class TextWidgetUpdateSetText(override val widgetId : WidgetId,
                                   val newText : String) : WidgetUpdateTextWidget(widgetId)
{
    override fun toString() = "Text Widget {$widgetId} Text Updated to $newText"
}


// ---------------------------------------------------------------------------------------------
// TARGET
// ---------------------------------------------------------------------------------------------

sealed class UpdateTarget : Serializable

data class UpdateTargetActionWidget(val actionWidgetId : WidgetId) : UpdateTarget()

data class UpdateTargetBooleanWidget(val booleanWidgetId : WidgetId) : UpdateTarget()

data class UpdateTargetListWidget(val listWidgetId : WidgetId) : UpdateTarget()

data class UpdateTargetNumberWidget(val numberWidgetId : WidgetId) : UpdateTarget()

data class UpdateTargetNumberCell(val tableWidgetId : WidgetId, val cellId : UUID) : UpdateTarget()

data class UpdateTargetPointsWidget(val pointsWidgetId : WidgetId) : UpdateTarget()

data class UpdateTargetTextCell(val tableWidgetId : WidgetId, val cellId : UUID) : UpdateTarget()

data class UpdateTargetTextWidget(val textWidgetId : WidgetId) : UpdateTarget()

data class UpdateTargetInsertTableRow(val tableWidget : TableWidget) : UpdateTarget()

data class UpdateTargetTableWidget(val tableWidgetId : WidgetId) : UpdateTarget()

data class UpdateTargetStoryWidgetPart(val storyWidgetId : WidgetId,
                                       val partIndex : Int) : UpdateTarget()

//data class UpdateTargetSummationNumberTerm(val termId : UUID) : UpdateTarget()

data class UpdateTargetVariable(val variableId : VariableId) : UpdateTarget()





// -----------------------------------------------------------------------------------------
// SHEET ACTION
// -----------------------------------------------------------------------------------------
//
//sealed class SheetAction : Serializable
//{
//
//    data class TableRow(val tableWidgetId : UUID,
//                        val rowClickedIndex : Int,
//                        val tableNameString : String,
//                        val tableColumns : List<TableWidgetColumn>) : SheetAction()
//
//}

