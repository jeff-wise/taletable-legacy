
package com.taletable.android.activity.entity.book


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.*
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.rts.entity.book
import maybe.Just



fun navView(bookReference : BookReference,
            theme : Theme,
            sessionActivity : SessionActivity) : View
{
    val layout = navViewLayout(sessionActivity)

    when (bookReference)
    {
        is BookReferenceChapter ->
        {
            layout.addView(navLinkView("Cover", false, bookReference.bookReference(), theme, sessionActivity))
            layout.addView(navSeparatorView(theme, sessionActivity) )
            val chapter = book(bookReference.bookId()).apply { it.chapter(bookReference.chapterId()) }

            when (chapter) {
                is Just -> layout.addView(navLinkView(chapter.value.title.value,
                                                      true,
                                                      bookReference.bookReference(),
                                                      theme,
                                                      sessionActivity))
                is Nothing -> layout.addView(navLinkView("",
                                                         true,
                                                         bookReference.bookReference(),
                                                         theme,
                                                         sessionActivity))
            }
        }
        is BookReferenceSection ->
        {
            layout.addView(navLinkView("Cover", false, bookReference.bookReference(), theme, sessionActivity))
            layout.addView(navSeparatorView(theme, sessionActivity) )

            val chapter    = book(bookReference.bookId()).apply { it.chapter(bookReference.chapterId()) }
            val section    = book(bookReference.bookId()).apply { it.section(bookReference.chapterId(), bookReference.sectionId) }

            when (chapter) {
                is Just -> layout.addView(navLinkView(chapter.value.title.value,
                                                      false,
                                                      bookReference.chapterReference(),
                                                      theme,
                                                      sessionActivity))
                is Nothing -> layout.addView(navLinkView("",
                                                         true,
                                                         bookReference.chapterReference(),
                                                         theme,
                                                         sessionActivity))
            }

            layout.addView(navSeparatorView(theme, sessionActivity) )

            when (section) {
                is Just -> layout.addView(navLinkView(section.value.title.value,
                                                      true,
                                                      bookReference.sectionReference(),
                                                      theme,
                                                      sessionActivity))
                is Nothing -> layout.addView(navLinkView("",
                                                         true,
                                                         bookReference.sectionReference(),
                                                         theme,
                                                         sessionActivity))
            }

        }
        is BookReferenceSubsection ->
        {
            layout.addView(navLinkView("Cover", false, bookReference.bookReference(), theme, sessionActivity))
            layout.addView(navSeparatorView(theme, sessionActivity) )

            val chapter    = book(bookReference.bookId()).apply { it.chapter(bookReference.chapterId()) }
            val section    = book(bookReference.bookId()).apply { it.section(bookReference.chapterId(), bookReference.sectionId) }
            val subsection = book(bookReference.bookId()).apply {
                                it.subsection(bookReference.chapterId(), bookReference.sectionId, bookReference.subsectionId) }

            when (chapter) {
                is Just -> layout.addView(navLinkView(chapter.value.title.value,
                                                      false,
                                                      bookReference.chapterReference(),
                                                      theme,
                                                      sessionActivity))
                is Nothing -> layout.addView(navLinkView("",
                                                         true,
                                                         bookReference.chapterReference(),
                                                         theme,
                                                         sessionActivity))
            }

            layout.addView(navSeparatorView(theme, sessionActivity) )

            when (section) {
                is Just -> layout.addView(navLinkView(section.value.title.value,
                                                      false,
                                                      bookReference.sectionReference(),
                                                      theme,
                                                      sessionActivity))
                is Nothing -> layout.addView(navLinkView("",
                                                         true,
                                                         bookReference.sectionReference(),
                                                         theme,
                                                         sessionActivity))
            }

            layout.addView(navSeparatorView(theme, sessionActivity) )

            when (subsection) {
                is Just -> layout.addView(navLinkView(subsection.value.title.value,
                                                      true,
                                                      bookReference.subsectionReference(),
                                                      theme,
                                                      sessionActivity))
                is Nothing -> layout.addView(navLinkView("",
                                                         true,
                                                         bookReference.subsectionReference(),
                                                         theme,
                                                         sessionActivity))
            }
        }
        is BookReferenceCard ->
        {
            layout.addView(cardNavView(theme, sessionActivity))
            //layout.setPadding(Util.dpToPixel(8f), layout.paddingTop, layout.paddingRight, layout.paddingBottom)
        }
    }


    return layout
}


private fun navViewLayout(context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.gravity              = Gravity.CENTER_VERTICAL

    layout.padding.leftDp       = 15f
    layout.padding.topDp        = 10f
    layout.padding.bottomDp     = 15f
    layout.padding.rightDp      = 15f

    return layout.linearLayout(context)
}


private fun navLinkView(label : String,
                        isEnd : Boolean,
                        bookReference : BookReference,
                        theme : Theme,
                        sessionActivity : SessionActivity) : TextView
{
    val link                = TextViewBuilder()

    link.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    link.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    link.text               = label

    link.font               = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            sessionActivity)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_12"))))
    link.color              = theme.colorOrBlack(colorTheme)

    link.sizeSp             = 18f

    link.onClick            = if (!isEnd) {
                                  View.OnClickListener {
                                      sessionActivity.setCurrentBookReference(bookReference)
                                  }
                              } else {
                                  View.OnClickListener {  }
                              }

    return link.textView(sessionActivity)
}


private fun navSeparatorView(theme : Theme, context : Context) : ImageView
{
    val iconBuilder             = ImageViewBuilder()

    iconBuilder.widthDp         = 21
    iconBuilder.heightDp        = 21

    iconBuilder.image           = R.drawable.icon_chevron_right

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    iconBuilder.color           = theme.colorOrBlack(colorTheme)

    iconBuilder.margin.leftDp   = 2f
    iconBuilder.margin.rightDp  = 2f
    iconBuilder.margin.topDp    = 2f

    return iconBuilder.imageView(context)
}


private fun cardNavView(theme : Theme, sessionActivity : SessionActivity) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder                   = LinearLayoutBuilder()
    val iconViewBuilder                 = ImageViewBuilder()
    val labelViewBuilder                = TextViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height                = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

    layoutBuilder.gravity               = Gravity.CENTER_VERTICAL

    //layoutBuilder.padding.topDp         = 8f

    layoutBuilder.margin.leftDp         = -6f

    layoutBuilder.onClick               = View.OnClickListener {
        sessionActivity.previousBookReference()
    }

    layoutBuilder.child(iconViewBuilder)
                 .child(labelViewBuilder)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 26
    iconViewBuilder.heightDp            = 26

    iconViewBuilder.image               = R.drawable.icon_chevron_left

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    iconViewBuilder.color               = theme.colorOrBlack(iconColorTheme)

    iconViewBuilder.margin.rightDp      = 4f

    // 4 | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.textId             = R.string.back

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    labelViewBuilder.color              = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.font               = Font.typeface(TextFont.RobotoCondensed,
                                                        TextFontStyle.Regular,
                                                        sessionActivity)

    labelViewBuilder.sizeSp             = 18f

    labelViewBuilder.margin.bottomDp    = 1f

    return layoutBuilder.linearLayout(sessionActivity)
}
