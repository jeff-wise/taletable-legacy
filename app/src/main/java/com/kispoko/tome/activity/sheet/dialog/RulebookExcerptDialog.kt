
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
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.RulebookReference
import com.kispoko.tome.model.game.RulebookSubsection
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.Err
import effect.Val



/**
 * Rulebook Excerpt Dialog
 */
class RulebookExcerptDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var rulebookReference : RulebookReference? = null
    private var sheetContext : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(rulebookReference : RulebookReference,
                        sheetContext : SheetContext) : RulebookExcerptDialog
        {
            val dialog = RulebookExcerptDialog()

            val args = Bundle()
            args.putSerializable("rulebook_reference", rulebookReference)
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

        this.rulebookReference = arguments.getSerializable("rulebook_reference") as RulebookReference
        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val dialogLayout = this.dialogLayout(sheetUIContext)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setContentView(dialogLayout)

            val width  = context.resources.getDimension(R.dimen.action_dialog_width)
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            dialog.window.setLayout(width.toInt(), height)
        }

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {


        val sheetContext = this.sheetContext
        val rulebookReference = this.rulebookReference
        if (sheetContext != null && rulebookReference != null)
        {
            val sheetUIContext  = SheetUIContext(sheetContext, context)

            val subsectionEff = GameManager.rulebookSubsection(sheetUIContext.gameId,
                                                            rulebookReference)

            when (subsectionEff)
            {
                is Val ->
                {
                    val subsection = subsectionEff.value
                    if (subsection != null) {
                        val viewBuilder = ExcerptViewBuilder(subsection, sheetUIContext)
                        return viewBuilder.view()
                    }
                }
                is Err -> ApplicationLog.error(subsectionEff.error)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}



class ExcerptViewBuilder(val subsection : RulebookSubsection,
                         val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView(subsection.title()))

        // Body
        layout.addView(this.bodyView(subsection.body()))

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

        layout.corners          = Corners(TopLeftCornerRadius(3f),
                                          TopRightCornerRadius(3f),
                                          BottomRightCornerRadius(3f),
                                          BottomLeftCornerRadius(3f))

        return layout.linearLayout(sheetUIContext.context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView(headerString : String) : TextView
    {
        val header               = TextViewBuilder()

        header.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text              = headerString

        header.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        header.sizeSp            = 16f

        return header.textView(sheetUIContext.context)
    }


    // Body
    // -----------------------------------------------------------------------------------------

    private fun bodyView(bodyString : String) : TextView
    {
        val body                = TextViewBuilder()

        body.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        body.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        body.text               = bodyString

        body.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        body.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        body.sizeSp             = 16f

        return body.textView(sheetUIContext.context)
    }

}

