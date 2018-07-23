
package com.taletable.android.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.sheet.procedure.NumberUpdateRequest
import com.taletable.android.activity.sheet.procedure.NumberUpdater
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack


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
    private var numberUpdater       : NumberUpdater? = null
    private var entityId            : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(title : String,
                        numberUpdateRequest : NumberUpdateRequest,
                        entityId : EntityId) : SimpleAdderDialog
        {
            val dialog = SimpleAdderDialog()

            val args = Bundle()
            args.putString("title", title)
            args.putSerializable("number_update_request", numberUpdateRequest)
            args.putSerializable("entity_id", entityId)

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

        this.title               = arguments?.getString("title")
        this.numberUpdateRequest = arguments?.getSerializable("number_update_request") as NumberUpdateRequest
        this.entityId            = arguments?.getSerializable("entity_id") as EntityId


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val width  = context?.resources?.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        width?.let {
            dialog.window.setLayout(width.toInt(), height)
        }

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val entityId      = this.entityId
        val title         = this.title
        val updateRequest = this.numberUpdateRequest
        val context       = this.context

        return if (entityId != null && title != null && updateRequest != null && context != null)
        {
            val viewBuilder = SimpleAdderViewBuilder(updateRequest,
                                                     title,
                                                     entityId,
                                                     context)
            viewBuilder.view()
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    override fun onAttach(context : Context?)
    {
        super.onAttach(context)
        try {
            this.numberUpdater = context as NumberUpdater
        } catch (e : ClassCastException) {
            throw ClassCastException("Activity does not satisfy Number Updater interface.")
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


class SimpleAdderViewBuilder(val numberUpdateRequest : NumberUpdateRequest,
                             val title : String,
                             val entityId : EntityId,
                             val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var currentValue : Int = numberUpdateRequest.currentValue.toInt()

    var valueTextView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun increment()
    {
        this.currentValue += 1
        this.valueTextView?.text = this.currentValue.toString()
    }


    fun decrement()
    {
        this.currentValue -= 1
        this.valueTextView?.text = this.currentValue.toString()
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.headerView())

        layout.addView(this.row1View())

        layout.addView(this.row2View())

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(context)
    }


    private fun headerView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.padding.topDp      = 6f
        name.padding.bottomDp   = 6f
        name.padding.leftDp     = 8f
        name.padding.rightDp    = 8f

        name.margin.bottomDp    = 1f

        name.backgroundColor    = Color.WHITE

        name.text               = title

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        name.sizeSp             = 16f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.corners            = Corners(3.0, 3.0, 0.0, 0.0)

        return name.textView(context)
    }


    private fun row1View() : LinearLayout
    {
        val layout  = this.rowViewLayout()

        val valueView = this.valueView()
        this.valueTextView = valueView
        layout.addView(valueView)

        layout.addView(this.undoButton())

        return layout
    }


    private fun row2View() : LinearLayout
    {
        val layout  = this.rowViewLayout()

        layout.addView(this.decrementButton())

        layout.addView(this.incrementButton())

        layout.addView(this.doneButton())

        return layout
    }


    private fun rowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.bottomDp  = 1f
        layout.padding.leftDp   = 0.5f
        layout.padding.rightDp  = 0.5f

        return layout.linearLayout(context)
    }


    private fun valueView() : TextView
    {
        val value               = TextViewBuilder()

        value.width             = 0
        value.height            = LinearLayout.LayoutParams.MATCH_PARENT
        value.weight            = 5f

        value.gravity           = Gravity.START or Gravity.CENTER_VERTICAL

        value.padding.leftDp    = 8f

        value.margin.leftDp     = 0.5f

        value.text              = currentValue.toString()

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        value.sizeSp            = 30f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        value.backgroundColor   = colorOrBlack(bgColorTheme, entityId)
//        value.backgroundColor   = Color.WHITE

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color              = colorOrBlack(colorTheme, entityId)
//        value.color              = Color.WHITE

//        value.corners           = Corners(2.0, 2.0, 2.0, 2.0)

        return value.textView(context)
    }


    private fun incrementButton() : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = 0
        button.height               = LinearLayout.LayoutParams.MATCH_PARENT
        button.weight               = 1f

        button.gravity              = Gravity.CENTER

        button.margin.leftDp        = 0.5f
        button.margin.rightDp       = 0.5f

        button.text                 = "+1"

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        button.sizeSp               = 30f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))
        button.color                = colorOrBlack(colorTheme, entityId)

        button.backgroundColor      = Color.WHITE

        button.onClick              = View.OnClickListener {
            this.increment()
        }

        return button.textView(context)
    }


    private fun decrementButton() : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = 0
        button.height               = LinearLayout.LayoutParams.MATCH_PARENT
        button.weight               = 1f

        button.gravity              = Gravity.CENTER

        button.margin.leftDp        = 0.5f
        button.margin.rightDp       = 0.5f

        button.text                 = "-1"

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        button.sizeSp               = 30f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))
        button.color                = colorOrBlack(colorTheme, entityId)

        button.backgroundColor      = Color.WHITE

        button.onClick              = View.OnClickListener {
            this.decrement()
        }

        return button.textView(context)
    }


    private fun undoButton() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 2f

        layout.gravity              = Gravity.CENTER

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
        layout.backgroundColor      = Color.WHITE

        layout.margin.leftDp       = 1f
        layout.margin.rightDp      = 0.5f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 22
        icon.heightDp               = 22

        icon.image                  = R.drawable.icon_undo

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color                  = colorOrBlack(iconColorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun doneButton() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 1f

        layout.gravity              = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_80"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)
        //layout.backgroundColor      = Color.WHITE

        layout.margin.leftDp       = 0.5f
        layout.margin.rightDp      = 0.5f

        layout.corners              = Corners(0.0, 0.0, 3.0, 0.0)

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 23
        icon.heightDp               = 23

        icon.image                  = R.drawable.icon_check_bold

//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("green_90"))))
//        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
        icon.color                  = Color.WHITE

        return layout.linearLayout(context)
    }

}
