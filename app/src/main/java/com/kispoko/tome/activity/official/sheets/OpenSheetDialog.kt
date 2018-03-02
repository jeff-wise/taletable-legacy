
package com.kispoko.tome.activity.official.sheets


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.theme.ThemeManager


/**
 * Sheet Variants Dialog
 */
class OpenSheetDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var sheetName : String? = null
    private var sheetId : SheetId? = null


    private val appSettings : AppSettings = AppSettings(ThemeId.Light)


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(sheetName : String, sheetId : SheetId) : OpenSheetDialog
        {
            val dialog = OpenSheetDialog()

            val args = Bundle()
            args.putString("sheet_name", sheetName)
            args.putSerializable("sheet_id", sheetId)
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

        this.sheetName = arguments.getString("sheet_name")
        this.sheetId   = arguments.getSerializable("sheet_id") as SheetId

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
        val sheetName = this.sheetName
        val sheetId = this.sheetId

        return if (sheetName != null && sheetId != null)
        {
            val viewBuilder = OpenSheetViewBuilder(sheetName,
                                                   sheetId,
                                                   this.appSettings.themeId,
                                                   context)
            viewBuilder.view()
        }
        else
        {
            super.onCreateView(inflater, container, savedInstanceState)
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



class OpenSheetViewBuilder(val sheetName : String,
                           val sheetId : SheetId,
                           val themeId : ThemeId,
                           val context : Context)
{


    fun view() : View
    {
        val layout          = this.viewLayout()

        layout.addView(this.mainView())

        layout.addView(this.footerView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }


    private fun mainView() : LinearLayout
    {
        val layout      = this.mainViewLayout()

        layout.addView(this.mainIconView())

        layout.addView(this.sheetNameView())

        return layout
    }


    private fun mainViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.elevation        = 3f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.margin.topDp     = 12f
        layout.margin.bottomDp  = 12f
        layout.margin.leftDp    = 8f
        layout.margin.rightDp   = 8f

        return layout.linearLayout(context)
    }


    private fun mainIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.layoutGravity    = Gravity.CENTER_VERTICAL

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_document

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color    = ThemeManager.color(themeId, iconColorTheme)

        icon.margin.rightDp     = 8f

        return layout.linearLayout(context)
    }


    private fun sheetNameView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = sheetName

        name.layoutGravity    = Gravity.CENTER_VERTICAL

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        name.color              = ThemeManager.color(themeId, colorTheme)

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        name.sizeSp             = 20f

        return name.textView(context)
    }




    private fun footerView() : LinearLayout
    {
        val layout      = this.footerViewLayout()

        layout.addView(this.openButtonView())

        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        layout.gravity      = Gravity.CENTER_VERTICAL or Gravity.END

        layout.padding.topDp    = 6f
        layout.padding.bottomDp    = 6f
        layout.padding.rightDp    = 8f
        layout.padding.leftDp    = 8f

        return layout.linearLayout(context)
    }


    private fun openButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val label           = TextViewBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.gravity          = Gravity.CENTER_VERTICAL

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)


        layout.padding.topDp    = 6f
        layout.padding.bottomDp    = 6f
        layout.padding.rightDp    = 10f
        layout.padding.leftDp    = 10f

        layout.child(label)
              .child(icon)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.open_sheet

        label.color             = Color.WHITE

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        label.sizeSp            = 18f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_arrow_right

        icon.color              = Color.WHITE

        icon.margin.leftDp      = 8f

        return layout.linearLayout(context)
    }

}

