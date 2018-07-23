
package com.taletable.android.activity.sheet.dialog


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

