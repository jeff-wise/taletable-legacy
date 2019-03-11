
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.EntityId



/**
 * Bookmark List Fragment
 */
class BookmarkListFragment : Fragment()
{

    // | PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var bookId : EntityId? = null


    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(bookId : EntityId) : BookSettingsFragment
        {
            val fragment = BookSettingsFragment()

            val args = Bundle()
            args.putSerializable("book_id", bookId)
            fragment.arguments = args

            return fragment
        }
    }


    // | FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        this.bookId = arguments?.getSerializable("book_id") as EntityId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val bookId  = this.bookId
        val context = getContext()

        var view : View? =null

        if (bookId != null && context != null)
        {
//            val bookActivity = context as SessionActivity
//            book(bookId).doMaybe {
//                view = BookUI(it, bookActivity, officialThemeLight).view()
//            }

            view = bookmarkListView(officialThemeLight, context)
        }

        return view
    }


}



private fun bookmarkListView(theme : Theme, context : Context) : LinearLayout
{
    val layout = bookmarkListViewLayout(theme, context)

    layout.addView(bookmarkListHeaderView(theme, context))

    layout.addView(bookmarkListContentView(theme, context))

    return layout
}


private fun bookmarkListViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.MATCH_PARENT

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}


private fun bookmarkListHeaderView(theme : Theme, context : Context) : ViewGroup
{
    val layout = bookmarkListHeaderViewLayout(theme, context)

    val closeIconView = bookmarkListHeaderCloseIconView(theme, context)

    val titleView     = bookmarkListHeaderTitleView(theme, context)
    val titleViewLP = titleView.layoutParams as RelativeLayout.LayoutParams
    titleViewLP.addRule(RelativeLayout.END_OF, R.id.icon)
    titleViewLP.addRule(RelativeLayout.CENTER_VERTICAL)
    titleView.layoutParams = titleViewLP

    layout.addView(closeIconView)
    layout.addView(titleView)

    return layout
}


private fun bookmarkListHeaderViewLayout(theme : Theme, context : Context) : ViewGroup
{
    val layout                  = RelativeLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.padding.topDp        = 16f
    layout.padding.bottomDp     = 16f
    layout.padding.leftDp       = 16f
    layout.padding.rightDp      = 16f

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    return layout.relativeLayout(context)
}


private fun bookmarkListHeaderCloseIconView(theme : Theme, context : Context) : ImageView
{
    val iconViewBuilder             = ImageViewBuilder()

    iconViewBuilder.id              = R.id.icon

    iconViewBuilder.layoutType      = LayoutType.RELATIVE
    iconViewBuilder.widthDp         = 20
    iconViewBuilder.heightDp        = 20

    iconViewBuilder.margin.leftDp   = 4f
    iconViewBuilder.margin.rightDp  = 18f

    iconViewBuilder.image           = R.drawable.icon_delete

    iconViewBuilder.color           = Color.WHITE

    iconViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)
    iconViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)

    return iconViewBuilder.imageView(context)
}


private fun bookmarkListHeaderTitleView(theme : Theme, context : Context) : TextView
{
    val titleViewBuilder                = TextViewBuilder()

    titleViewBuilder.layoutType         = LayoutType.RELATIVE
    titleViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    titleViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    titleViewBuilder.textId             = R.string.settings

    titleViewBuilder.font               = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Medium,
                                                        context)

//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    titleViewBuilder.color              = Color.WHITE

    titleViewBuilder.sizeSp             = 19f

    return titleViewBuilder.textView(context)
}


private fun bookmarkListContentView(theme : Theme, context : Context) : LinearLayout
{
    val layout = bookmarkListContentViewLayout(theme, context)

    return layout
}


private fun bookmarkListContentViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.MATCH_PARENT

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    layoutBuilder.backgroundColor   = Color.WHITE

    return layoutBuilder.linearLayout(context)
}

