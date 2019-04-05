
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.model.user.User
import com.taletable.android.model.user.UserId
import com.taletable.android.model.user.UserName
import com.taletable.android.model.user.catalog.BookmarkCollection
import com.taletable.android.model.user.catalog.BookmarkCollectionName
import com.taletable.android.model.user.catalog.Catalog
import com.taletable.android.util.SimpleDividerItemDecoration



/**
 * Bookmark List Fragment
 */
class BookmarkCollectionListFragment : Fragment()
{

    // | PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var userId : UserId? = null


    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(userId : UserId) : BookmarkCollectionListFragment
        {
            val fragment = BookmarkCollectionListFragment()

            val args = Bundle()
            args.putSerializable("user_id", userId)
            fragment.arguments = args

            return fragment
        }
    }


    // | FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        this.userId = arguments?.getSerializable("user_id") as UserId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        //val bookId  = this.bookId
        val context = context

        var view : View? =null

        if (context != null)
        {
//            val bookActivity = context as SessionActivity
//            book(bookId).doMaybe {
//                view = BookUI(it, bookActivity, officialThemeLight).view()
//            }

            val collection1 = BookmarkCollection(BookmarkCollectionName("Combat Rules"), listOf())
            val collection2 = BookmarkCollection(BookmarkCollectionName("Garak the Crusher"), listOf())
            val collection3 = BookmarkCollection(BookmarkCollectionName("Custom Weapons"), listOf())
            val testCatalog = Catalog(listOf(collection1, collection2, collection3))

            val user = User(UserName("Bob"), testCatalog)

            view = bookmarkCollectionListView(user.catalog.collections,
                                              officialThemeLight,
                                              this,
                                              context)
        }

        return view
    }


}



private fun bookmarkCollectionListView(collections : List<BookmarkCollection>,
                               theme : Theme,
                               fragment : BookmarkCollectionListFragment,
                               context : Context) : ViewGroup
{
    val layout = bookmarkCollectionListViewLayout(theme, context)

    val headerView = bookmarkCollectionListHeaderView(theme, fragment, context)

    val contentView = bookmarkCollectionListContentView(theme, context)
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
                                             fragment : BookmarkCollectionListFragment,
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


private fun bookmarkCollectionListContentView(theme : Theme, context : Context) : ViewGroup
{
    val layout = bookmarkCollectionListContentViewLayout(theme, context)

    val recyclerView = bookmarkCollectionRecyclerView(theme, context)

    layout.addView(recyclerView)

    return layout
}


private fun bookmarkCollectionListContentViewLayout(theme : Theme, context : Context) : RelativeLayout
{
    val layoutBuilder               = RelativeLayoutBuilder()

    layoutBuilder.layoutType        = LayoutType.RELATIVE
    layoutBuilder.width             = RelativeLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp          = 0

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    layoutBuilder.backgroundColor   = Color.WHITE

    return layoutBuilder.relativeLayout(context)
}


// | Recycler View
// -----------------------------------------------------------------------------

private fun bookmarkCollectionRecyclerView(theme : Theme, context : Context) : RecyclerView
{
    val recyclerView                = RecyclerViewBuilder()

    recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
    recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

    recyclerView.layoutManager      = LinearLayoutManager(context)

    val dividerColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
    val dividerColor              = theme.colorOrBlack(dividerColorTheme)
    recyclerView.divider            = SimpleDividerItemDecoration(context, dividerColor)

    recyclerView.clipToPadding      = false

    return recyclerView.recyclerView(context)
}



// -----------------------------------------------------------------------------
// | Recycler View Adapter
// -----------------------------------------------------------------------------

class BookmarkCollectionRecyclerViewAdapter(val theme : Theme,
                                            val context : Context)
                                            : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // | PROPERTIES
    // -------------------------------------------------------------------------------------

    private val COLLECTION = 1


    // | ITEMS
    // -------------------------------------------------------------------------------------

    var items : List<Any> = listOf()
        set(newItems) {
            field = newItems
            this.notifyDataSetChanged()
        }


    // | RECYCLER VIEW ADAPTER
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
//        val itemAtPosition = this.items[position]
//        return when (itemAtPosition) {
//            is SessionListHeader -> HEADER
//            else                 -> ENTITY
//        }

        return COLLECTION
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder
    {
        val collectionView = bookmarkCollectionEntryView(theme, context)
        return CollectionViewHolder(collectionView, theme)
    }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val collection = this.items[position] as BookmarkCollection
        val collectionViewHolder = viewHolder as CollectionViewHolder
        collectionViewHolder.setName(collection.name.value)
        collectionViewHolder.setCount(collection.bookmarks.size)
    }


    override fun getItemCount() = this.items.size

}


class CollectionViewHolder(itemView : View,
                           val theme : Theme)
                           : RecyclerView.ViewHolder(itemView)
{

    // | PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout    : LinearLayout? = null
    var nameView  : TextView? = null
    var countView : TextView? = null

    // | INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout     = itemView.findViewById(R.id.layout)
        this.nameView   = itemView.findViewById(R.id.name_view)
        this.countView  = itemView.findViewById(R.id.count_view)
    }


    fun setName(name : String)
    {
        this.nameView?.text = name
    }


    fun setCount(count : Int)
    {
        this.countView?.text = count.toString()
    }
}


private fun bookmarkCollectionEntryView(theme : Theme, context : Context) : LinearLayout
{
    val layout = bookmarkCollectionEntryViewLayout(theme, context)

    layout.addView(bookmarkCollectionEntryNameView(theme, context))

    return layout
}


private fun bookmarkCollectionEntryViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.backgroundColor      = Color.WHITE

    layout.gravity              = Gravity.CENTER_VERTICAL

    layout.padding.topDp        = 16f
    layout.padding.bottomDp     = 16f

    layout.padding.leftDp       = 16f
    layout.padding.rightDp      = 16f

    return layout.linearLayout(context)
}


private fun bookmarkCollectionEntryNameView(theme : Theme,
                                            context : Context) : TextView
{
    val nameViewBuilder             = TextViewBuilder()

    nameViewBuilder.id              = R.id.name_view

    nameViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    nameViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT


    nameViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Bold,
                                                    context)


    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    nameViewBuilder.color              = theme.colorOrBlack(colorTheme)

    nameViewBuilder.sizeSp             = 19f

    return nameViewBuilder.textView(context)
}

