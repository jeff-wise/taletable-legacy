
package com.taletable.android.rts.entity.sheet


import com.taletable.android.app.ApplicationEvent
import com.taletable.android.app.EventTypeSheetUpdate
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.model.entity.EntityUpdateSheet
import com.taletable.android.model.sheet.widget.TableWidget
import com.taletable.android.model.sheet.widget.WidgetId
import com.taletable.android.rts.entity.EntityId
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

