
package com.kispoko.tome.activity.sheet.dialog


import android.util.Log
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.widget.dialog.ValueChooserDialogFragment
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.game.engine.variable.*
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
    val variableValue = numberVariable.variableValue()

    when (variableValue)
    {
        is NumberVariableSummationValue ->
        {
            val sheetActivity = sheetUIContext.context as SheetActivity
            val dialog = SummationDialogFragment.newInstance(variableValue.summation,
                                                             numberVariable.label(),
                                                             SheetContext(sheetUIContext))
            dialog.show(sheetActivity.supportFragmentManager, "")
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
