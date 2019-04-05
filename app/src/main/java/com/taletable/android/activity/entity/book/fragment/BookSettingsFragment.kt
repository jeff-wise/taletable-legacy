
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
 * Book Settings Fragment
 */
class BookSettingsFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var bookId : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
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


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
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
        val context = context

        var view : View? =null

        if (bookId != null && context != null)
        {
//            val bookActivity = context as SessionActivity
//            book(bookId).doMaybe {
//                view = BookUI(it, bookActivity, officialThemeLight).view()
//            }

            view = bookmarkCollectionListView(officialThemeLight, this, context)
        }

        return view
    }


}



private fun bookmarkCollectionListView(theme : Theme,
                                       fragment : BookSettingsFragment,
                                       context : Context) : ViewGroup
{
    val layout = bookmarkCollectionListViewLayout(theme, context)

    val headerView = bookmarkCollectionListHeaderView(theme, fragment, context)

    val contentView = bookmarkListContentView(theme, context)
    val contentViewLP = contentView.layoutParams as RelativeLayout.LayoutParams
    contentViewLP.addRule(RelativeLayout.BELOW, R.id.header)
    contentViewLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
    contentView.layoutParams = contentViewLP

    layout.addView(headerView)
    layout.addView(contentView)

    return layout
}


private fun bookmarkCollectionListViewLayout(theme : Theme, context : Context) : RelativeLayout
{
    val layoutBuilder               = RelativeLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.MATCH_PARENT

//    layoutBuilder.orientation       = LinearLayout.VERTICAL

    layoutBuilder.backgroundColor   = Color.WHITE

    return layoutBuilder.relativeLayout(context)
}


private fun bookmarkCollectionListHeaderView(theme : Theme,
                                             fragment : BookSettingsFragment,
                                             context : Context) : ViewGroup
{
    val layout = bookmarkCollectionListHeaderViewLayout(theme, context)

    val closeIconView = bookmarkCollectionListHeaderCloseIconView(theme, context)

    val titleView     = bookmarkCollectionListHeaderTitleView(theme, context)
    val titleViewLP = titleView.layoutParams as RelativeLayout.LayoutParams
    titleViewLP.addRule(RelativeLayout.END_OF, R.id.icon)
    titleViewLP.addRule(RelativeLayout.CENTER_VERTICAL)
    titleView.layoutParams = titleViewLP

    layout.addView(closeIconView)
    layout.addView(titleView)

    closeIconView.setOnClickListener {
        fragment.fragmentManager?.popBackStack()
    }

    return layout
}


private fun bookmarkCollectionListHeaderViewLayout(theme : Theme, context : Context) : ViewGroup
{
    val layout                  = RelativeLayoutBuilder()

    layout.id                   = R.id.header

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.MATCH_PARENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.addRule(RelativeLayout.ALIGN_PARENT_TOP)

    layout.padding.topDp        = 16f
    layout.padding.bottomDp     = 16f
    layout.padding.leftDp       = 16f
    layout.padding.rightDp      = 16f

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    return layout.relativeLayout(context)
}


private fun bookmarkCollectionListHeaderCloseIconView(theme : Theme, context : Context) : ImageView
{
    val iconViewBuilder             = ImageViewBuilder()

    iconViewBuilder.id              = R.id.icon

    iconViewBuilder.layoutType      = LayoutType.RELATIVE
    iconViewBuilder.widthDp         = 20
    iconViewBuilder.heightDp        = 20

    iconViewBuilder.margin.leftDp   = 2f
    iconViewBuilder.margin.rightDp  = 20f

    iconViewBuilder.image           = R.drawable.icon_delete

    iconViewBuilder.color           = Color.WHITE

    iconViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)
    iconViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)

    return iconViewBuilder.imageView(context)
}


private fun bookmarkCollectionListHeaderTitleView(theme : Theme, context : Context) : TextView
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


private fun bookmarkListContentView(theme : Theme, context : Context) : ViewGroup
{
    val layout = bookmarkListContentViewLayout(theme, context)

    return layout
}


private fun bookmarkListContentViewLayout(theme : Theme, context : Context) : RelativeLayout
{
    val layoutBuilder               = RelativeLayoutBuilder()

    layoutBuilder.layoutType        = LayoutType.RELATIVE
    layoutBuilder.width             = RelativeLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp          = 0

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    layoutBuilder.backgroundColor   = Color.WHITE

    return layoutBuilder.relativeLayout(context)
}

