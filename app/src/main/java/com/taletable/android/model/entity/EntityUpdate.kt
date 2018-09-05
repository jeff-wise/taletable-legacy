
package com.taletable.android.model.entity


import com.taletable.android.model.engine.value.ValueId
import com.taletable.android.model.sheet.group.GroupReference
import com.taletable.android.model.sheet.widget.WidgetId
import java.io.Serializable
import java.util.*


// ---------------------------------------------------------------------------------------------
// | Entity Update
// ---------------------------------------------------------------------------------------------

sealed class EntityUpdate : Serializable


// ---------------------------------------------------------------------------------------------
// | Sheet Update
// ---------------------------------------------------------------------------------------------

sealed class EntityUpdateSheet : EntityUpdate()


// | Sheet Update > Widget Update
// ---------------------------------------------------------------------------------------------

sealed class SheetUpdateWidget(open val widgetId : WidgetId) : EntityUpdateSheet()

// | Sheet Update > Widget Update > Action Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateActionWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class ActionWidgetUpdate(override val widgetId : WidgetId) : WidgetUpdateActionWidget(widgetId)


// | Sheet Update > Widget Update > Boolean Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateBooleanWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class BooleanWidgetUpdateToggle(override val widgetId : WidgetId) : WidgetUpdateBooleanWidget(widgetId)

data class BooleanWidgetUpdateSetValue(override val widgetId : WidgetId,
                                       val newValue : Boolean) : WidgetUpdateBooleanWidget(widgetId)


// | Sheet Update > Widget Update > Group Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateGroupWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class GroupWidgetUpdateSetReferences(
        override val widgetId : WidgetId,
        val newReferenceList : List<GroupReference>) : WidgetUpdateGroupWidget(widgetId)


// | Sheet Update > Widget Update > Expander Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateExpanderWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class ExpanderWidgetUpdateToggle(override val widgetId : WidgetId) : WidgetUpdateExpanderWidget(widgetId)


// | Sheet Update > Widget Update > List Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateListWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class ListWidgetUpdateSetCurrentValue(
        override val widgetId : WidgetId,
        val newCurrentValue : List<String>) : WidgetUpdateListWidget(widgetId)


data class ListWidgetUpdateAddValue(
        override val widgetId : WidgetId,
        val newValue : String) : WidgetUpdateListWidget(widgetId)

// | Sheet Update > Widget Update > Number Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateNumberWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class NumberWidgetUpdateValue(override val widgetId : WidgetId,
                                   val newValue : Double) : WidgetUpdateNumberWidget(widgetId)

// | Sheet Update > Widget Update > Points Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdatePointsWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)


data class PointsWidgetUpdateSetCurrentValue(
                    override val widgetId : WidgetId,
                    val newCurrentValue : Double) : WidgetUpdatePointsWidget(widgetId)

// | Sheet Update > Widget Update > Story Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateStoryWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)


data class StoryWidgetUpdateNumberPart(override val widgetId : WidgetId,
                                       val partIndex : Int,
                                       val newNumber : Double) : WidgetUpdateStoryWidget(widgetId)

data class StoryWidgetUpdateTextPart(override val widgetId : WidgetId,
                                     val partIndex : Int,
                                     val newValue : String) : WidgetUpdateStoryWidget(widgetId)

data class StoryWidgetUpdateTextValuePart(override val widgetId : WidgetId,
                                          val partIndex : Int,
                                          val newValueId : ValueId) : WidgetUpdateStoryWidget(widgetId)

// | Sheet Update > Widget Update > Table Widget
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

// | Sheet Update > Widget Update > Text Widget
// ---------------------------------------------------------------------------------------------

sealed class WidgetUpdateTextWidget(override val widgetId : WidgetId) : SheetUpdateWidget(widgetId)

data class TextWidgetUpdateSetText(override val widgetId : WidgetId,
                                   val newText : String) : WidgetUpdateTextWidget(widgetId)
{
    override fun toString() = "Text Widget {$widgetId} Text Updated to $newText"
}

