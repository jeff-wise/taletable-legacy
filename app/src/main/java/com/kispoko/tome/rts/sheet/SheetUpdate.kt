
package com.kispoko.tome.rts.sheet


import com.kispoko.tome.app.ApplicationEvent
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.widget.Widget
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
}



data class WidgetReference(val widgetId : UUID, val widgetViewId : Int)



sealed class SheetUpdate


// Widget Update

data class WidgetUpdateEvent(val widget : Widget, val sheetId : SheetId)


sealed class WidgetUpdate(open val widgetId : UUID) : SheetUpdate()


// Story Widget Update

sealed class WidgetUpdateStoryWidget(override val widgetId : UUID) : WidgetUpdate(widgetId)


data class StoryWidgetUpdateNumberPart(override val widgetId : UUID,
                                        val partIndex : Int,
                                        val newNumber : Double) : WidgetUpdateStoryWidget(widgetId)


// Table Widget Update

sealed class WidgetUpdateTableWidget(override val widgetId : UUID) : WidgetUpdate(widgetId)

data class TableWidgetUpdateSetNumberCell(override val widgetId : UUID,
                                          val cellId : UUID,
                                          val newNumber : Double) : WidgetUpdateTableWidget(widgetId)


// Text Widget Update

sealed class WidgetUpdateTextWidget(override val widgetId : UUID) : WidgetUpdate(widgetId)

data class TextWidgetUpdateSetText(override val widgetId : UUID,
                                   val newText : String) : WidgetUpdateTextWidget(widgetId)
{
    override fun toString() = "Text Widget {$widgetId} Text Updated to $newText"
}


// ---------------------------------------------------------------------------------------------
// TARGET
// ---------------------------------------------------------------------------------------------

sealed class UpdateTarget : Serializable

data class UpdateTargetNumberCell(val tableWidgetId : UUID, val cellId : UUID) : UpdateTarget()

data class UpdateTargetTextCell(val tableWidgetId : UUID, val cellId : UUID) : UpdateTarget()

data class UpdateTargetTextWidget(val textWidgetId : UUID) : UpdateTarget()

data class UpdateTargetStoryWidgetPart(val storyWidgetId : UUID,
                                       val partIndex : Int) : UpdateTarget()


