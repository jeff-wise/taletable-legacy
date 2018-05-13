
package com.kispoko.tome.activity.sheet.dialog


import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.kispoko.tome.activity.entity.engine.summation.SummationDialog
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.engine.value.ValueId
import com.kispoko.tome.model.engine.variable.*
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.sheet.UpdateTarget
import com.kispoko.tome.rts.entity.summation
import com.kispoko.tome.rts.entity.value
import com.kispoko.tome.rts.entity.valueSet
import effect.Err
import effect.Val
import maybe.Just



/**
 * Variable Editor Dialog
 */
fun openVariableEditorDialog(variable : Variable,
                             numericEditorType : NumericEditorType?,
                             updateTarget : UpdateTarget,
                             entityId : EntityId,
                             context : Context)
{

    when (variable)
    {
        is TextVariable   -> openTextVariableEditorDialog(variable,
                                                          updateTarget,
                                                          entityId,
                                                          context)
        is NumberVariable ->
        {
            if (numericEditorType != null) {
                openNumberVariableEditorDialog(variable,
                                               numericEditorType,
                                               updateTarget,
                                               entityId,
                                               context)
            } else {
                openNumberVariableEditorDialog(variable,
                                               updateTarget,
                                               entityId,
                                               context)
            }
        }
        is TextListVariable   -> openTextListVariableEditorDialog(variable,
                                                                  updateTarget,
                                                                  entityId,
                                                                  context)
    }

}


/**
 * Variable Editor Dialog
 */
fun openVariableEditorDialog(variable : Variable,
                             updateTarget : UpdateTarget,
                             entityId : EntityId,
                             context : Context)
{

    when (variable)
    {
        is TextVariable   -> openTextVariableEditorDialog(variable,
                                                          updateTarget,
                                                          entityId,
                                                          context)
        is NumberVariable -> openNumberVariableEditorDialog(variable,
                                                            updateTarget,
                                                            entityId,
                                                            context)
    }

}


fun openNumberVariableEditorDialog(numberVariable : NumberVariable,
                                   updateTarget : UpdateTarget,
                                   entityId : EntityId,
                                   context : Context) =
    openNumberVariableEditorDialog(numberVariable,
                                   NumericEditorType.Adder,
                                   updateTarget,
                                   entityId,
                                   context)


fun openNumberVariableEditorDialog(numberVariable : NumberVariable,
                                   editorType : NumericEditorType,
                                   updateTarget : UpdateTarget,
                                   entityId : EntityId,
                                   context : Context)
{
    val variableValue = numberVariable.variableValue()

    val sheetActivity = context as SheetActivity

    when (variableValue)
    {
        is NumberVariableLiteralValue ->
        {
            when (editorType)
            {
                is NumericEditorType.Adder ->
                {
                    val adderState = AdderState(variableValue.value,
                                                0.0,
                                                setOf(),
                                                numberVariable.label().value,
                                                updateTarget,
                                                numberVariable.variableId())
                    val adderDialog = AdderDialog.newInstance(adderState, entityId)

                    adderDialog.show(sheetActivity.supportFragmentManager, "")
                }
                is NumericEditorType.Simple ->

                {
                    val simpleDialog = NumberEditorDialog.newInstance(variableValue.value,
                                                                      numberVariable.label().value,
                                                                      updateTarget,
                                                                      entityId)
                    simpleDialog.show(sheetActivity.supportFragmentManager, "")
                }
            }
        }
        is NumberVariableSummationValue ->
        {
            val summation = summation(variableValue.summationId, entityId)
            when (summation)
            {
                is Val ->
                {
                    val dialog = SummationDialog.newInstance(
                                            summation.value,
                                            numberVariable.label().value,
                                            entityId)
                    dialog.show(sheetActivity.supportFragmentManager, "")
                }
                is Err -> ApplicationLog.error(summation.error)
            }
        }
    }

}


fun openTextVariableEditorDialog(textVariable : TextVariable,
                                 updateTarget : UpdateTarget,
                                 entityId : EntityId,
                                 context : Context)
{
    val variableValue = textVariable.variableValue()

    when (variableValue)
    {
        is TextVariableLiteralValue ->
        {
            val title = textVariable.label().value
            val text  = variableValue.value

            val activity = context as AppCompatActivity
            val dialog = TextEditorDialog.newInstance(title,
                                                      text,
                                                      updateTarget,
                                                      entityId)
            dialog.show(activity.supportFragmentManager, "")
        }
        is TextVariableValueValue ->
        {
            val valueReference = variableValue.valueReference
            val valueSetId     = valueReference.valueSetId

            val valueSet = valueSet(valueSetId, entityId)
            val value    = value(valueReference, entityId)

            when (valueSet)
            {
                is Val ->
                {
                    when (value)
                    {
                        is Val ->
                        {
                            val sheetActivity = context as SheetActivity
                            val chooseDialog =
                                    ValueChooserDialogFragment.newInstance(
                                                    valueSet.value,
                                                    value.value,
                                                    updateTarget,
                                                    entityId)
                            chooseDialog.show(sheetActivity.supportFragmentManager, "")
                        }
                        is Err -> ApplicationLog.error(value.error)
                    }
                }
                is Err -> ApplicationLog.error(valueSet.error)
            }
        }
        is TextVariableValueUnknownValue -> {
            val valueSet = valueSet(variableValue.valueSetId, entityId)

            when (valueSet)
            {
                is Val ->
                {
                    val sheetActivity = context as SheetActivity
                    val chooseDialog =
                            ValueChooserDialogFragment.newInstance(
                                            valueSet.value,
                                            null,
                                            updateTarget,
                                            entityId)
                    chooseDialog.show(sheetActivity.supportFragmentManager, "")
                }
                is Err -> ApplicationLog.error(valueSet.error)
            }
        }
    }
}




fun openTextListVariableEditorDialog(textListVariable : TextListVariable,
                                     updateTarget : UpdateTarget,
                                     entityId : EntityId,
                                     context : Context)
{
    val variableValue = textListVariable.variableValue()

    when (variableValue)
    {
        is TextListVariableLiteralValue ->
        {
            val sheetActivity = context as SheetActivity

            val valueSetId = textListVariable.valueSetId
            val values = textListVariable.value(entityId)

            when (valueSetId) {
                is Just -> {
                    when (values) {
                        is Val -> {
                            val dialog = ListEditorDialog.newInstance(valueSetId.value,
                                                                      values.value.map { ValueId(it) },
                                                                      updateTarget,
                                                                      entityId)
                            dialog.show(sheetActivity.supportFragmentManager, "")
                        }
                    }
                }
            }

        }
    }
}
