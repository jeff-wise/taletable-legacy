
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.RulebookReference
import com.kispoko.tome.model.game.RulebookReferencePath
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
import com.kispoko.tome.util.Util



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

            val widthDp  = 300f
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            dialog.window.setLayout(Util.dpToPixel(widthDp), height)
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

            var view : View? = null

            GameManager.rulebook(sheetUIContext.gameId) apDo { rulebook ->

                val subsection = rulebook.subsection(rulebookReference)
                val refPath    = rulebook.referencePath(rulebookReference)

                if (subsection != null && refPath != null) {
                    val viewBuilder = ExcerptViewBuilder(subsection, refPath, sheetUIContext)
                    view = viewBuilder.view()
                }
            }

            if (view != null)
                return view

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
                         val referencePath : RulebookReferencePath,
                         val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView(subsection.titleString()))

        // Body
        layout.addView(this.bodyView(subsection.bodyString()))

        // Footer
        layout.addView(this.footerView())


        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
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
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.MATCH_PARENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Bold,
                                                sheetUIContext.context)

        val bgcolorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgcolorTheme)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color            = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        header.corners          = Corners(TopLeftCornerRadius(3f),
                                          TopRightCornerRadius(3f),
                                          BottomRightCornerRadius(0f),
                                          BottomLeftCornerRadius(0f))

        header.sizeSp           = 16f

        header.padding.leftDp   = 9f
        header.padding.rightDp  = 8f
        header.padding.topDp    = 10f
        header.padding.bottomDp = 10f

        header.margin.bottomDp  = 2f

        return header.textView(sheetUIContext.context)
    }


    // Body
    // -----------------------------------------------------------------------------------------

    private fun bodyView(bodyString : String) : ScrollView
    {
        val scrollView = this.bodyScrollView()

        scrollView.addView(this.bodyTextView(bodyString))

        return scrollView
    }


    private fun bodyScrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.heightDp     = 300

        return scrollView.scrollView(sheetUIContext.context)
    }


    private fun bodyTextView(bodyString : String) : TextView
    {
        val body                = TextViewBuilder()

        body.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        body.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        body.text               = bodyString

        body.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val bgcolorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        body.backgroundColor    = SheetManager.color(sheetUIContext.sheetId, bgcolorTheme)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        body.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        body.sizeSp             = 14f

        body.padding.leftDp     = 8f
        body.padding.rightDp    = 8f
        body.padding.topDp      = 4f

        return body.textView(sheetUIContext.context)
    }


    // Footer
    // -----------------------------------------------------------------------------------------

    private fun footerView() : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()
        val label                   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor       = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners               = Corners(TopLeftCornerRadius(0f),
                                               TopRightCornerRadius(0f),
                                               BottomRightCornerRadius(3f),
                                               BottomLeftCornerRadius(3f))

        layout.padding.leftDp       = 8f
        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f

        layout.margin.topDp         = 2f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 19
        icon.heightDp               = 19

        icon.image                  = R.drawable.icon_book

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("gold_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp         = 7f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId                = R.string.read_in_rulebook

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("gold_11")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color                 = SheetManager.color(sheetUIContext.sheetId, textColorTheme)


        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        label.sizeSp                = 16f

        return layout.linearLayout(sheetUIContext.context)
    }




}

