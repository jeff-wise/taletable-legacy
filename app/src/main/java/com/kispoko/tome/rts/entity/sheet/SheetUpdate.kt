
package com.kispoko.tome.rts.entity.sheet


import com.kispoko.tome.app.ApplicationEvent
import com.kispoko.tome.app.EventTypeSheetUpdate
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.entity.EntityUpdateSheet
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.WidgetId
import com.kispoko.tome.rts.entity.EntityId
import java.io.Serializable
import java.util.*



/**
 * Sheet Update
 *
 * Defines all types of udpates that can happen to a sheet.
 */


data class SheetUpdateEvent(val sheetUpdate : EntityUpdateSheet,
                            val sheetId : EntityId) : ApplicationEvent
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

