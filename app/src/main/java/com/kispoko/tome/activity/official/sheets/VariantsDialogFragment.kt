
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
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.theme.ThemeManager
import java.io.Serializable



/**
 * Variants Dialog Fragment
 */
class VariantsDialogFragment : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var sheetName    : String? = null
    private var variants     : List<SheetVariant> = listOf()
    //private var sheetContext : SheetContext? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(sheetName : String,
                        variants : List<SheetVariant>) : VariantsDialogFragment
        {
            val dialog = VariantsDialogFragment()

            val args = Bundle()
            args.putString("sheet_name", sheetName)
            args.putSerializable("variants", variants as Serializable)
//            args.putSerializable("sheet_context", sheetContext)
            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    @Suppress("UNCHECKED_CAST")
    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.sheetName    = arguments.getString("sheet_name")
        this.variants     = arguments.getSerializable("variants") as List<SheetVariant>
//        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val window = dialog.window
        val wlp = window.attributes

        wlp.gravity = Gravity.CENTER
        window.attributes = wlp

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
        if (sheetName != null)
        {
            return VariantsView.view(sheetName,
                                     this.variants,
                                     this.appSettings.themeId(),
                                     context)
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout(context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}


// ---------------------------------------------------------------------------------------------
// SHEET VARIANT
// ---------------------------------------------------------------------------------------------

data class SheetVariant(val name : String, val id : String)


// ---------------------------------------------------------------------------------------------
// VARIANTS VIEW
// ---------------------------------------------------------------------------------------------

object VariantsView
{

    fun view(sheetName : String,
             variants : List<SheetVariant>,
             themeId : ThemeId,
             context : Context) : View
    {
        val layout = this.viewLayout(themeId, context)

        // Header
        layout.addView(this.headerView(themeId, context))

        // Sheet Name
        layout.addView(this.sheetNameView(sheetName, themeId, context))

        // Buttons
        layout.addView(this.buttonsView(variants, themeId, context))

        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(2f),
                                           TopRightCornerRadius(2f),
                                           BottomRightCornerRadius(2f),
                                           BottomLeftCornerRadius(2f))

        return layout.linearLayout(context)
    }


    private fun headerView(themeId : ThemeId, context : Context) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = context.getString(R.string.choose_variant_for).toUpperCase()

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color            = ThemeManager.color(themeId, colorTheme)

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        header.sizeSp           = 13f

        header.margin.leftDp    = 8f
        header.margin.rightDp   = 8f
        header.margin.topDp     = 8f

        return header.textView(context)
    }


    private fun sheetNameView(sheetName : String,
                              themeId : ThemeId,
                              context : Context) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = sheetName

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color              = ThemeManager.color(themeId, colorTheme)

        name.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Light,
                                                context)

        name.sizeSp             = 17f

        name.margin.leftDp      = 8f
        name.margin.rightDp     = 8f
        name.margin.topDp       = 8f
        name.margin.bottomDp    = 8f

        return name.textView(context)
    }


    private fun buttonsView(variants : List<SheetVariant>,
                            themeId : ThemeId,
                            context : Context) : LinearLayout
    {
        val layout = this.buttonsViewLayout(themeId, context)

        variants.forEach {
            layout.addView(this.buttonView(it, themeId, context))
        }

        return layout
    }


    private fun buttonsViewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun buttonView(variant : SheetVariant,
                           themeId : ThemeId,
                           context : Context) : TextView
    {
        val button              = TextViewBuilder()

        button.width            = LinearLayout.LayoutParams.MATCH_PARENT
        button.height           = 0
        button.weight           = 1f

        button.text             = variant.name

        val textColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        button.color            = ThemeManager.color(themeId, textColorTheme)

        button.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        button.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        button.sizeSp           = 17f

        button.padding.topDp    = 12f
        button.padding.bottomDp = 12f

        button.padding.leftDp   = 6f
        button.padding.rightDp  = 6f

        button.margin.leftDp    = 8f
        button.margin.rightDp   = 8f
        button.margin.bottomDp  = 8f

        button.corners          = Corners(TopLeftCornerRadius(2f),
                                          TopRightCornerRadius(2f),
                                          BottomRightCornerRadius(2f),
                                          BottomLeftCornerRadius(2f))

        return button.textView(context)
    }


}