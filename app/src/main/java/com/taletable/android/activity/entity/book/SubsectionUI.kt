
package com.taletable.android.activity.entity.book


import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.Book
import com.taletable.android.model.book.BookSubsection
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.groups


/**
 * Subsection UI
 */
class SubsectionUI(val subsection : BookSubsection,
                   val book : Book,
                   val bookActivity : BookActivity,
                   val theme : Theme)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = book.entityId()
    val context = bookActivity

        // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.headerView())

        layout.addView(this.sectionView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Header
    // -----------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.headerMainView())

        layout.addView(this.headerBorderView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun headerMainView() : LinearLayout
    {
        val layout = this.headerMainViewLayout()

        if (bookActivity.referenceHistory.isNotEmpty())
            layout.addView(this.headerBackButtonView())

        layout.addView(this.headerTextView())

        return layout
    }


    private fun headerMainViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    private fun headerBackButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER

        layout.onClick              = View.OnClickListener {
            bookActivity.setToPreviousReference()
        }

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 32
        icon.heightDp               = 32

        icon.image                  = R.drawable.icon_chevron_left

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        icon.color                  = theme.colorOrBlack(colorTheme)

        icon.padding.topDp          = 1f

        return layout.linearLayout(context)
    }


    private fun headerTextView() : TextView
    {
        val header                = TextViewBuilder()

        header.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text               = subsection.title().value

        header.font               = Font.typeface(TextFont.default(),
                                                  TextFontStyle.Medium,
                                                  context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color                = colorOrBlack(colorTheme, entityId)

        header.sizeSp               = 22f

        header.padding.leftDp       = 2f
        header.padding.topDp        = 10f
        header.padding.bottomDp     = 10f

        return header.textView(context)

    }


    private fun headerBorderView() : LinearLayout
    {
        val border              = LinearLayoutBuilder()

        border.width            = LinearLayout.LayoutParams.MATCH_PARENT
        border.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_10"))))
        border.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return border.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Section
    // -----------------------------------------------------------------------------------------

    fun sectionView() : View
    {
        val scrollView = this.sectionScrollView()
        val cardLayout = this.sectionViewLayout()

        cardLayout.addView(this.bodyView())

        scrollView.addView(cardLayout)

        return scrollView
    }


    private fun sectionScrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        scrollView.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        return scrollView.scrollView(context)
    }


    private fun sectionViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.bottomDp = 70f

//        layout.padding.topDp    = 8f

//        layout.margin.leftDp    = 6f
//        layout.margin.rightDp   = 6f

        return layout.linearLayout(context)
    }


    // VIEWS > Body
    // --------------------------------------------------------------------------------------------

    private fun bodyView() : LinearLayout
    {
        val layout = this.bodyViewLayout()

        subsection.bodyContent(book).forEach { content ->

            // TODO is this right??
            groups(content.groupReferences(), book.entityId()).forEach {
                it.onSheetComponentActive(book.entityId(), context)
            }

            groups(content.groupReferences(), book.entityId()).forEach {
                layout.addView(it.view(book.entityId(), context))
            }
        }

        return layout
    }


    private fun bodyViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

//        layout.backgroundColor      = Color.WHITE
//
//        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

//        layout.padding.topDp        = 8f
//        layout.padding.bottomDp     = 8f
//        layout.padding.leftDp       = 8f
//        layout.padding.rightDp      = 8f

        return layout.linearLayout(context)
    }

}


