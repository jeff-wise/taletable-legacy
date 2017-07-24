
package com.kispoko.tome.activity.sheet.dialog


import android.util.Log
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.Err
import effect.Val



/**
 * Variable Editor Dialog
 */
fun openVariableEditorDialog(variable : Variable, sheetUIContext: SheetUIContext)
{

    when (variable)
    {
        is TextVariable   -> openTextVariableEditorDialog(variable, sheetUIContext)
        is NumberVariable -> openNumberVariableEditorDialog(variable, sheetUIContext)
    }

}


fun openNumberVariableEditorDialog(numberVariable : NumberVariable,
                                   sheetUIContext : SheetUIContext)
{
    openNumberVariableEditorDialog(numberVariable, NumericEditorType.Calculator, sheetUIContext)
}


fun openNumberVariableEditorDialog(numberVariable : NumberVariable,
                                   editorType : NumericEditorType,
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
                     val adderDialog = AdderDialogFragment.newInstance(
                                                variableValue.value,
                                                numberVariable.label(),
                                                SheetContext(sheetUIContext))
                    adderDialog.show(sheetActivity.supportFragmentManager, "")
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



