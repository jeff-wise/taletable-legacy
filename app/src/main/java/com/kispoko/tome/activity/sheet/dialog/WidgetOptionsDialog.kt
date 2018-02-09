
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.VariableHistoryActivity
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.Widget
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext



/**
 * Widget Options Dialog
 */
class WidgetOptionsDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var widget : Widget? = null
    private var sheetContext : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(widget : Widget, sheetContext : SheetContext) : WidgetOptionsDialog
        {
            val dialog = WidgetOptionsDialog()

            val args = Bundle()
            args.putSerializable("sheet_context", sheetContext)
            args.putSerializable("widget", widget)
            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext
        this.widget = arguments.getSerializable("widget") as Widget


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val dialogLayout = this.dialogLayout(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val width  = context.resources.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width.toInt(), height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {


        val sheetContext = this.sheetContext
        val widget = this.widget
        if (sheetContext != null && widget != null)
        {
            val sheetUIContext  = SheetUIContext(sheetContext, context)

            val viewBuilder = OptionsViewBuidler(widget, this, sheetUIContext)
            return viewBuilder.view()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout(context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}



// ---------------------------------------------------------------------------------------------
// GETSTURE DETECTOR
// ---------------------------------------------------------------------------------------------


fun openWidgetOptionsDialogOnDoubleTap(sheetActivity : SheetActivity,
                                       widget : Widget,
                                       sheetContext : SheetContext) : GestureDetectorCompat
{
    val gd = GestureDetectorCompat(sheetActivity,
        object: GestureDetector.SimpleOnGestureListener() {

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                val dialog = WidgetOptionsDialog.newInstance(widget, sheetContext)
                dialog.show(sheetActivity.supportFragmentManager, "")
                return true
            }

            override fun onDown(e: MotionEvent?): Boolean {
                return super.onDown(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                return super.onSingleTapConfirmed(e)
            }
        })

    return gd
}


// ---------------------------------------------------------------------------------------------
// OPTIONS VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class OptionsViewBuidler(val widget : Widget,
                         val dialog : WidgetOptionsDialog,
                         val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val sheetActivity : SheetActivity = sheetUIContext.context as SheetActivity
    val sheetContext : SheetContext = SheetContext(sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // History Button
        val historyButtonOnClick = View.OnClickListener {
//            val variables = widget.variables(sheetContext)
//            if (variables.isNotEmpty())
//            {
//                val intent = Intent(sheetActivity, VariableHistoryActivity::class.java)
//                intent.putExtra("variable", variables.first())
//                intent.putExtra("sheet_context", sheetContext)
//                sheetActivity.startActivity(intent)
//                dialog.dismiss()
//            }
        }
        layout.addView(this.buttonView(R.drawable.icon_history,
                                       R.string.value_history,
                                       23,
                                       historyButtonOnClick))

        // --------------------------------------------------------
        layout.addView(this.dividerView())

        // Widget Editor Button
        val widgetButtonOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.drawable.icon_widget,
                                       R.string.edit_widget,
                                       20,
                                       widgetButtonOnClick))

        // --------------------------------------------------------
        layout.addView(this.dividerView())

        // ---  Sharing -------------------------------------------
        layout.addView(this.sectionHeaderView(R.string.sharing))

        // Shareable Mode
        layout.addView(this.shareableModeView())

        // Visibility Button
        val visibilityButtonOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.drawable.icon_lock,
                                       R.string.visibility,
                                       21,
                                       visibilityButtonOnClick))


        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.topDp    = 8f

        return layout.linearLayout(sheetUIContext.context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView() : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId           = R.string.widget_options

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Bold,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color            = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        header.sizeSp           = 16f

        header.margin.bottomDp  = 4f

        return header.textView(sheetUIContext.context)
    }


    // Section Header
    // -----------------------------------------------------------------------------------------

    private fun sectionHeaderView(stringId : Int) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId           = stringId

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color            = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        header.sizeSp           = 14f

        header.margin.topDp     = 10f
        header.margin.bottomDp  = 4f

        return header.textView(sheetUIContext.context)
    }


    // Divider
    // -----------------------------------------------------------------------------------------

    private fun dividerView() : LinearLayout
    {
        val divider                 = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return divider.linearLayout(sheetUIContext.context)
    }


    // Button
    // -----------------------------------------------------------------------------------------

    private fun buttonView(iconId : Int,
                           labelId : Int,
                           iconSize : Int,
                           onClick : View.OnClickListener) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val icon   = ImageViewBuilder()
        val label  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.margin.topDp         = 14f
        layout.margin.bottomDp      = 14f

        layout.onClick              = onClick

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = iconSize
        icon.heightDp               = iconSize

        icon.image                  = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_24")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp         = 7f

        // (3 B) Name
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        label.textId                = labelId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color                 = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.sizeSp                = 16f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun shareableModeView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val label                   = TextViewBuilder()
        val switch                  = SwitchBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.margin.leftDp        = 2f
        layout.margin.rightDp       = 4f

        layout.padding.topDp        = 5f
        layout.padding.bottomDp     = 5f

        layout.child(label)
              .child(switch)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT
        label.weight                = 1f

        label.textId                = R.string.is_shareable

        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color                 = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        label.sizeSp                = 16f

        // (3 B) Switcher
        // -------------------------------------------------------------------------------------

        switch.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        switch.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        switch.checked              = false

        switch.scaleX               = 0.9f
        switch.scaleY               = 0.9f

        return layout.linearLayout(sheetUIContext.context)
    }


}

