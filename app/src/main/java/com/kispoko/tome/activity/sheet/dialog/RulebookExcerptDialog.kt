
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import br.tiagohm.markdownview.MarkdownView
import com.kispoko.tome.R
import com.kispoko.tome.activity.game.rulebook.StyleSheet
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.book.RulebookExcerpt
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.book.RulebookReferencePath
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.util.Util



/**
 * Rulebook Excerpt Dialog
 */
//class RulebookExcerptDialog : DialogFragment()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var rulebookReference : BookReference? = null
//    private var sheetContext : SheetContext? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun newInstance(rulebookReference : BookReference,
//                        sheetContext : SheetContext) : RulebookExcerptDialog
//        {
//            val dialog = RulebookExcerptDialog()
//
//            val args = Bundle()
//            args.putSerializable("rulebook_reference", rulebookReference)
//            args.putSerializable("sheet_context", sheetContext)
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
//        this.rulebookReference = arguments.getSerializable("rulebook_reference") as BookReference
//        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext
//
//
//        // (2) Initialize UI
//        // -------------------------------------------------------------------------------------
//
//        val dialog = Dialog(context)
//
//        val sheetContext = this.sheetContext
//        if (sheetContext != null)
//        {
//            val sheetUIContext = SheetUIContext(sheetContext, context)
//
//            val dialogLayout = this.dialogLayout(sheetUIContext)
//
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            dialog.setContentView(dialogLayout)
//
//            val widthDp  = 300f
//            val height = LinearLayout.LayoutParams.WRAP_CONTENT
//
//            dialog.window.setLayout(Util.dpToPixel(widthDp), height)
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
//
//
//        val sheetContext = this.sheetContext
//        val rulebookReference = this.rulebookReference
//        if (sheetContext != null && rulebookReference != null)
//        {
//            val sheetUIContext  = SheetUIContext(sheetContext, context)
//            val rulebookId = rulebookReference.rulebookId()
//
//            var view : View? = null
//
////            GameManager.rulebook(sheetUIContext.gameId, rulebookId) apDo { rulebook ->
////
////                val excerpt = rulebook.excerpt(rulebookReference)
////                val refPath = rulebook.referencePath(rulebookReference)
////
////                if (excerpt != null && refPath != null) {
////                    val viewBuilder = ExcerptViewBuilder(excerpt, refPath, sheetUIContext)
////                    view = viewBuilder.view()
////                }
////            }
//
//            if (view != null)
//                return view
//
//        }
//
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//
//    override fun onResume()
//    {
//        super.onResume()
//
//        val sheetContext = this.sheetContext
//        val rulebookReference = this.rulebookReference
//        if (sheetContext != null && rulebookReference != null)
//        {
//            val sheetUIContext  = SheetUIContext(sheetContext, context)
//            val rulebookId = rulebookReference.rulebookId()
//
////            GameManager.rulebook(sheetUIContext.gameId, rulebookId) apDo { rulebook ->
////                val mdView = view?.findViewById(R.id.markdown_view) as MarkdownView
////                rulebook.excerpt(rulebookReference)?.let {
////                    mdView.loadMarkdown(it.body)
////                }
////            }
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // DIALOG LAYOUT
//    // -----------------------------------------------------------------------------------------
//
//    fun dialogLayout(sheetUIContext: SheetUIContext) : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
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
//class ExcerptViewBuilder(val excerpt : RulebookExcerpt,
//                         val referencePath : RulebookReferencePath,
//                         val sheetUIContext : SheetUIContext)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // VIEWS
//    // -----------------------------------------------------------------------------------------
//
//    fun view() : View
//    {
//        val layout = this.viewLayout()
//
//        // Header
//        layout.addView(this.headerView(excerpt.title))
//
//        // Body
//        layout.addView(this.bodyView(excerpt.body))
//
//        // Footer
//        layout.addView(this.footerView())
//
//        return layout
//    }
//
//
//    private fun viewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
////        val colorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
////        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//        layout.backgroundColor  = Color.WHITE
//
//        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)
//
////        layout.corners              = Corners(0.0, 0.0, 3.0, 3.0)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    // Header
//    // -----------------------------------------------------------------------------------------
//
//    private fun headerView(headerString : String) : LinearLayout
//    {
//        val layout          = this.headerViewLayout()
//
//        layout.addView(this.headerLabelView())
//
//        layout.addView(this.pathView())
//
//        layout.addView(this.dividerView())
//
//        return layout
//    }
//
//
//    private fun headerViewLayout() : LinearLayout
//    {
//        val layout          = LinearLayoutBuilder()
//
//        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation  = LinearLayout.VERTICAL
//
//        layout.backgroundColor  = Color.WHITE
//
//        layout.corners      = Corners(3.0, 3.0, 0.0, 0.0)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun headerLabelView() : LinearLayout
//    {
//
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout          = LinearLayoutBuilder()
//
//        val iconLayout      = LinearLayoutBuilder()
//        val icon            = ImageViewBuilder()
//
//        val label           = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.padding.leftDp   = 7f
//        layout.padding.rightDp  = 7f
//        layout.padding.topDp    = 8f
//        layout.padding.bottomDp = 2f
//
//        layout.child(iconLayout)
//                .child(label)
//
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        iconLayout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        iconLayout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        iconLayout.gravity          = Gravity.CENTER
//
//        iconLayout.child(icon)
//
//
//        icon.widthDp            = 20
//        icon.heightDp           = 20
//
//        icon.image              = R.drawable.icon_book_filled
//
//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        icon.color             = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
//
//        icon.margin.rightDp    = 6f
//
//        // (3 B) Title
//        // -------------------------------------------------------------------------------------
//
//        label.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.textId           = R.string.rules_excerpt
//
//        label.font             = Font.typeface(TextFont.default(),
//                                               TextFontStyle.Medium,
//                                               sheetUIContext.context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        label.color            = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//
//        label.sizeSp           = 19f
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun pathView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val path                = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.padding.leftDp   = 8f
//        layout.padding.rightDp  = 7f
//        layout.padding.bottomDp = 4f
//
//        layout.child(path)
//
//        // (3) Path
//        // -------------------------------------------------------------------------------------
//
//        path.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        path.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        path.text               = referencePath.toString()
//
//        path.font               = Font.typeface(TextFont.default(),
//                                               TextFontStyle.Regular,
//                                               sheetUIContext.context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
//        path.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//
//        path.sizeSp             = 16f
//
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    // Body
//    // -----------------------------------------------------------------------------------------
//
//    private fun bodyView(bodyString : String) : ScrollView
//    {
//        val scrollView = this.bodyScrollView()
//
//        val bodyLayout = this.bodyLayout()
//
//        bodyLayout.addView(this.bodyTextView(bodyString))
//
//        scrollView.addView(bodyLayout)
//
//        return scrollView
//    }
//
//
//    private fun bodyScrollView() : ScrollView
//    {
//        val scrollView          = ScrollViewBuilder()
//
//        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
//        scrollView.heightDp     = 300
//
//        scrollView.fadingEnabled    = false
//
//        return scrollView.scrollView(sheetUIContext.context)
//    }
//
//
//    private fun bodyLayout() : LinearLayout
//    {
//        val layout          = LinearLayoutBuilder()
//
//        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.padding.leftDp     = 8f
//        layout.padding.rightDp    = 8f
//        layout.padding.topDp      = 4f
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
//        layout.backgroundColor       = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun bodyTextView(bodyString : String) : MarkdownView
//    {
//        val body                = MarkdownViewBuilder()
//
//        body.id                 = R.id.markdown_view
//
//        body.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        body.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
////        body.markdownText       = bodyString
//        body.stylesheet         = StyleSheet(sheetUIContext.context).lightTheme
////
//
//        return body.textView(sheetUIContext.context)
//    }
//
//
//    // Footer
//    // -----------------------------------------------------------------------------------------
//
//    private fun footerView() : LinearLayout
//    {
//        val layout          = this.footerMainView()
//
//        // layout.addView(this.dividerView())
//
//        //layout.addView(this.footerMainView())
//
//        return layout
//    }
//
//
//    private fun footerViewLayout() : LinearLayout
//    {
//        val layout          = LinearLayoutBuilder()
//
//        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation  = LinearLayout.VERTICAL
//
//        layout.corners          = Corners(0.0, 0.0, 3.0, 3.0)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun footerMainView() : LinearLayout
//    {
//
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout                  = LinearLayoutBuilder()
//
//        val label                   = TextViewBuilder()
//
//        val iconLayout              = LinearLayoutBuilder()
//        val icon                    = ImageViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp             = 48
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.gravity              = Gravity.CENTER
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        layout.backgroundColor       = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//        //layout.backgroundColor       = Color.WHITE
//
//        layout.corners          = Corners(0.0, 0.0, 3.0, 3.0)
//
//        //layout.padding.leftDp       = 8f
//        layout.padding.topDp        = 8f
//        layout.padding.bottomDp     = 8f
//
//        layout.child(label)
//              .child(iconLayout)
//
//        // (3) Label
//        // -------------------------------------------------------------------------------------
//
//        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.textId                = R.string.read_in_rulebook
//
////        val textColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("gold_11")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
////        label.color                 = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
//        label.color                 = Color.WHITE
//
//
//        label.font                  = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Regular,
//                                                    sheetUIContext.context)
//
//        label.sizeSp                = 18f
//
//        label.layoutGravity          = Gravity.CENTER
//        label.gravity          = Gravity.CENTER
//
//        // (4) Icon
//        // -------------------------------------------------------------------------------------
//
//        iconLayout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        iconLayout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        iconLayout.gravity          = Gravity.CENTER
//        iconLayout.layoutGravity          = Gravity.CENTER
//
//        iconLayout.child(icon)
//
//        icon.widthDp                = 23
//        icon.heightDp               = 23
//
//        icon.image                  = R.drawable.icon_arrow_right
//
////        val iconColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("gold_15")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        //icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
//        icon.color                  = Color.WHITE
//
//        icon.margin.leftDp         = 7f
//
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun dividerView() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp         = 1
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//
//}

