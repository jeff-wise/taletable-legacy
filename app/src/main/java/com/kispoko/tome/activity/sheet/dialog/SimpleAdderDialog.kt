
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.R.string.name
import com.kispoko.tome.activity.sheet.procedure.NumberUpdateRequest
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.UpdateTarget
import maybe.Just


/**
 * Simple Adder Dialog
 */
class SimpleAdderDialog : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var title               : String? = null
    private var numberUpdateRequest : NumberUpdateRequest? = null
    private var sheetContext        : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(title : String,
                        numberUpdateRequest : NumberUpdateRequest,
                        sheetContext : SheetContext) : SimpleAdderDialog
        {
            val dialog = SimpleAdderDialog()

            val args = Bundle()
            args.putString("title", title)
            args.putSerializable("number_update_request", numberUpdateRequest)
            args.putSerializable("sheet_context", sheetContext)

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

        this.title               = arguments.getString("title")
        this.numberUpdateRequest = arguments.getSerializable("number_update_request") as NumberUpdateRequest
        this.sheetContext        = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

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
        if (sheetContext != null)
        {
            val sheetUIContext  = SheetUIContext(sheetContext, context)
            val title           = this.title
            val updateRequest   = this.numberUpdateRequest

            return if (title != null && updateRequest != null)
            {
                val viewBuilder = SimpleAdderViewBuilder(sheetUIContext,
                                                         updateRequest,
                                                         title)
                viewBuilder.view()
            }
            else
                super.onCreateView(inflater, container, savedInstanceState)
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }


}


class SimpleAdderViewBuilder(val sheetUIContext : SheetUIContext,
                             val numberUpdateRequest : NumberUpdateRequest,
                             val title : String)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val sheetContext = SheetContext(sheetUIContext)

    var currentValue : Int = numberUpdateRequest.currentValue.toInt()


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.headerView())

        layout.addView(this.valueView())

        layout.addView(this.mainView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun headerView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.padding.topDp      = 10f
        name.padding.leftDp     = 8f
        name.padding.rightDp    = 8f

        name.backgroundColor    = Color.WHITE

        name.text               = title

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                sheetUIContext.context)

        name.sizeSp             = 16f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        name.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.corners            = Corners(3.0, 3.0, 0.0, 0.0)

        return name.textView(sheetUIContext.context)
    }


    private fun mainView() : LinearLayout
    {
        val layout  = this.mainViewLayout()

        layout.addView(this.addButton("-1"))

        layout.addView(this.addButton("-10"))

        layout.addView(this.addButton("+10"))

        layout.addView(this.addButton("+1"))

        return layout
    }


    private fun mainViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun valueView() : TextView
    {
        val value               = TextViewBuilder()

        value.width             = LinearLayout.LayoutParams.MATCH_PARENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.gravity           = Gravity.START

        value.text              = currentValue.toString()

        value.margin.bottomDp   = 2f
        value.padding.leftDp    = 9f

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                sheetUIContext.context)

        value.sizeSp            = 35f

        value.backgroundColor   = Color.WHITE

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return value.textView(sheetUIContext.context)
    }


    private fun addButton(buttonLabel : String) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = 0
        button.height               = LinearLayout.LayoutParams.MATCH_PARENT
        button.weight               = 1f

        button.gravity              = Gravity.CENTER

        button.margin.leftDp        = 1f
        button.margin.rightDp       = 1f

        button.text                 = buttonLabel

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    sheetUIContext.context)

        button.sizeSp               = 24f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))

        button.color                = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        button.backgroundColor      = Color.WHITE

        return button.textView(sheetUIContext.context)
    }

}
