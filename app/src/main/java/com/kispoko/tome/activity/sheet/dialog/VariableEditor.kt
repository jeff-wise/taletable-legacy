
package com.kispoko.tome.activity.sheet.dialog


import android.util.Log
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.UpdateTarget
import effect.Err
import effect.Val



/**
 * Variable Editor Dialog
 */
fun openVariableEditorDialog(variable : Variable,
                             numericEditorType : NumericEditorType?,
                             updateTarget : UpdateTarget,
                             sheetUIContext : SheetUIContext)
{

    when (variable)
    {
        is TextVariable   -> openTextVariableEditorDialog(variable,
                                                          updateTarget,
                                                          sheetUIContext)
        is NumberVariable ->
        {
            if (numericEditorType != null) {
                openNumberVariableEditorDialog(variable,
                                               numericEditorType,
                                               updateTarget,
                                               sheetUIContext)
            } else {
                openNumberVariableEditorDialog(variable,
                                               updateTarget,
                                               sheetUIContext)
            }
        }
    }

}


/**
 * Variable Editor Dialog
 */
fun openVariableEditorDialog(variable : Variable,
                             updateTarget : UpdateTarget,
                             sheetUIContext : SheetUIContext)
{

    when (variable)
    {
        is TextVariable   -> openTextVariableEditorDialog(variable,
                                                          updateTarget,
                                                          sheetUIContext)
        is NumberVariable -> openNumberVariableEditorDialog(variable, updateTarget, sheetUIContext)
    }

}


fun openNumberVariableEditorDialog(numberVariable : NumberVariable,
                                   updateTarget : UpdateTarget,
                                   sheetUIContext : SheetUIContext) =
    openNumberVariableEditorDialog(numberVariable,
                                   NumericEditorType.Adder,
                                   updateTarget,
                                   sheetUIContext)


fun openNumberVariableEditorDialog(numberVariable : NumberVariable,
                                   editorType : NumericEditorType,
                                   updateTarget : UpdateTarget,
                                   sheetUIContext : SheetUIContext)
{
    val variableValue = numberVariable.variableValue()

    val sheetActivity = sheetUIContext.context as SheetActivity

    when (variableValue)
    {
        is NumberVariableLiteralValue ->
        {
            Log.d("***VAREDITOR", editorType.toString())
            when (editorType)
            {
                is NumericEditorType.Adder ->
                {
                    val adderState = AdderState(variableValue.value,
                                                0.0,
                                                setOf(),
                                                numberVariable.label(),
                                                updateTarget)
                    val adderDialog = AdderDialogFragment.newInstance(adderState,
                                                                      SheetContext(sheetUIContext))
                    adderDialog.show(sheetActivity.supportFragmentManager, "")
                }
                is NumericEditorType.Simple ->

                {
                    val simpleDialog = NumberEditorDialog.newInstance(variableValue.value,
                                                                      updateTarget,
                                                                      SheetContext(sheetUIContext))
                    simpleDialog.show(sheetActivity.supportFragmentManager, "")
                }
            }
        }
        is NumberVariableSummationValue ->
        {
            val summation = GameManager.engine(sheetUIContext.gameId)
                                       .apply{ it.summation(variableValue.summationId) }
            when (summation)
            {
                is Val ->
                {
                    val dialog = SummationDialogFragment.newInstance(
                                            summation.value,
                                            numberVariable.label(),
                                            SheetContext(sheetUIContext))
                    dialog.show(sheetActivity.supportFragmentManager, "")
                }
                is Err -> ApplicationLog.error(summation.error)
            }
        }
    }

}


fun openTextVariableEditorDialog(textVariable : TextVariable,
                                 updateTarget : UpdateTarget,
                                 sheetUIContext : SheetUIContext)
{
    val variableValue = textVariable.variableValue()

    when (variableValue)
    {
        is TextVariableLiteralValue ->
        {
            val title = textVariable.label()
            val text  = variableValue.value

            val sheetActivity = sheetUIContext.context as SheetActivity
            val dialog = TextEditorDialogFragment.newInstance(title,
                                                              text,
                                                              updateTarget,
                                                              SheetContext(sheetUIContext))
            dialog.show(sheetActivity.supportFragmentManager, "")
        }
        is TextVariableValueValue ->
        {
            val valueReference = variableValue.valueReference
            val valueSetId     = valueReference.valueSetId

            val valueSet = GameManager.engine(sheetUIContext.gameId)
                                      .apply { it.valueSet(valueSetId) }
            val value    = GameManager.engine(sheetUIContext.gameId)
                                      .apply { it.value(valueReference, SheetContext(sheetUIContext)) }

            when (valueSet)
            {
                is Val ->
                {
                    when (value)
                    {
                        is Val ->
                        {
                            val sheetActivity = sheetUIContext.context as SheetActivity
                            val chooseDialog =
                                    ValueChooserDialogFragment.newInstance(
                                                    valueSet.value,
                                                    value.value,
                                                    SheetContext(sheetUIContext))
                            chooseDialog.show(sheetActivity.supportFragmentManager, "")
                        }
                        is Err -> ApplicationLog.error(value.error)
                    }
                }
                is Err -> ApplicationLog.error(valueSet.error)
            }
        }
    }
}



