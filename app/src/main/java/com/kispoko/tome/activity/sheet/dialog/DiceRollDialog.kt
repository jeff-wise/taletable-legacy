
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.dice.DiceRollGroup
import com.kispoko.tome.model.game.engine.dice.RollPartSummary
import com.kispoko.tome.model.game.engine.dice.RollSummary
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId



/**
 * Dice Roller Dialog
 */
//class DiceRollDialog : DialogFragment()
//{
//
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var diceRollGroup : DiceRollGroup? = null
//
//    private var autoRolls     : Int             = 0
//
//    private var sheetContext  : SheetContext? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun newInstance(diceRollGroup : DiceRollGroup,
//                        sheetContext : SheetContext,
//                        autoRolls : Int = 0) : DiceRollDialog
//        {
//            val dialog = DiceRollDialog()
//
//            val args = Bundle()
//            args.putSerializable("dice_roll_group", diceRollGroup)
//            args.putInt("auto_rolls", autoRolls)
//            args.putSerializable("sheet_context", sheetContext)
//
//            dialog.arguments = args
//
//            return dialog
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // DIALOG FRAGMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
//    {
//        // (1) Read State
//        // -------------------------------------------------------------------------------------
//
//        this.diceRollGroup  = arguments.getSerializable("dice_roll_group") as DiceRollGroup
//
//        this.autoRolls      = arguments.getInt("auto_rolls")
//
//        this.sheetContext   = arguments.getSerializable("sheet_context") as SheetContext
//
//        // (2) Initialize UI
//        // -------------------------------------------------------------------------------------
//
//        val dialog = Dialog(activity)
//
//        val sheetContext = this.sheetContext
//        if (sheetContext != null)
//        {
//            val dialogLayout = this.dialogLayout(context)
//
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            dialog.setContentView(dialogLayout)
//
//            val width  = context.resources.getDimension(R.dimen.action_dialog_width)
//            val height = LinearLayout.LayoutParams.WRAP_CONTENT
//
//            dialog.window.setLayout(width.toInt(), height)
//        }
//
//        return dialog
//    }
//
//
//    override fun onCreateView(inflater : LayoutInflater?,
//                              container : ViewGroup?,
//                              savedInstanceState : Bundle?) : View?
//    {
//        val sheetContext = this.sheetContext
//        if (sheetContext != null)
//        {
//            val sheetUIContext  = SheetUIContext(sheetContext, context)
//
//            val diceRollGroup = this.diceRollGroup
//
//            return if (diceRollGroup != null) {
//                val viewBuilder = DiceRollerViewBuilder(diceRollGroup, autoRolls, sheetUIContext)
//                viewBuilder.view()
//            }
//            else {
//                super.onCreateView(inflater, container, savedInstanceState)
//            }
//        }
//        else
//        {
//            return super.onCreateView(inflater, container, savedInstanceState)
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // DIALOG LAYOUT
//    // -----------------------------------------------------------------------------------------
//
//    fun dialogLayout(context : Context) : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.orientation          = LinearLayout.VERTICAL
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
//
//        return layout.linearLayout(context)
//    }
//
//}
//
//
//

