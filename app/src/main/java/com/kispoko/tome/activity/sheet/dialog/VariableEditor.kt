
package com.kispoko.tome.activity.sheet.dialog


import android.util.Log
import com.kispoko.tome.R.string.value
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
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
    val sheetContext = SheetContext(sheetUIContext)

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
                                                numberVariable.label().value,
                                                updateTarget)
                    val adderDialog = AdderDialogFragment.newInstance(adderState,
                                                                      SheetContext(sheetUIContext))
                    adderDialog.show(sheetActivity.supportFragmentManager, "")
                }
                is NumericEditorType.Simple ->

                {
                    val simpleDialog = NumberEditorDialog.newInstance(variableValue.value,
                                                                      numberVariable.label().value,
                                                                      updateTarget,
                                                                      SheetContext(sheetUIContext))
                    simpleDialog.show(sheetActivity.supportFragmentManager, "")
                }
            }
        }
        is NumberVariableSummationValue ->
        {
//            val summation = GameManager.engine(sheetUIContext.gameId)
//                                       .apply{ it.summation(variableValue.summationId) }
            val summation = SheetManager.summation(variableValue.summationId, sheetContext)
            when (summation)
            {
                is Val ->
                {
                    val dialog = SummationDialogFragment.newInstance(
                                            summation.value,
                                            numberVariable.label().value,
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
            val title = textVariable.label().value
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
                                      .apply { it.value(valueReference, sheetUIContext.gameId) }

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
                                                    updateTarget,
                                                    SheetContext(sheetUIContext))
                            chooseDialog.show(sheetActivity.supportFragmentManager, "")
                        }
                        is Err -> ApplicationLog.error(value.error)
                    }
                }
                is Err -> ApplicationLog.error(valueSet.error)
            }
        }
        is TextVariableValueUnknownValue -> {
            val valueSet = GameManager.engine(sheetUIContext.gameId)
                                      .apply { it.valueSet(variableValue.valueSetId) }

            when (valueSet)
            {
                is Val ->
                {
                    val sheetActivity = sheetUIContext.context as SheetActivity
                    val chooseDialog =
                            ValueChooserDialogFragment.newInstance(
                                            valueSet.value,
                                            null,
                                            updateTarget,
                                            SheetContext(sheetUIContext))
                    chooseDialog.show(sheetActivity.supportFragmentManager, "")
                }
                is Err -> ApplicationLog.error(valueSet.error)
            }
        }
    }
}



