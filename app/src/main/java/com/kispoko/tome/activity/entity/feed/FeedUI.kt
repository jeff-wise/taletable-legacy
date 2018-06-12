
package com.kispoko.tome.activity.entity.feed


import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.group.SwipeAndDragHelper
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.feed.Feed
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import maybe.Just


class FeedUI(val feed : Feed,
             val theme : Theme,
             val activity : AppCompatActivity)
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View = recyclerView()


    private fun recyclerView() : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
        recyclerView.backgroundColor    = theme.colorOrBlack(colorTheme)

        recyclerView.adapter            = FeedRecyclerViewAdapater(feed, this, theme, context)

        return recyclerView.recyclerView(context)
    }



    // VIEWS > Card View
    // -----------------------------------------------------------------------------------------

    fun cardView() : LinearLayout
    {
        val layout = this.cardViewLayout()

        layout.addView(this.cardPinnedView())

        layout.addView(this.cardTitleView())

        layout.addView(this.cardGroupsLayout())

        layout.addView(this.cardFooterView())

        return layout
    }


    private fun cardViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.margin.leftDp    = 4f
        layout.margin.rightDp   = 4f
        layout.margin.topDp     = 4f

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 8f

        return layout.linearLayout(context)
    }


    private fun cardPinnedView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()
        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.id                   = R.id.card_pinned_layout

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.visibility           = View.GONE

        layout.margin.leftDp        = 6f
        layout.margin.rightDp       = 8f

        layout.child(iconView)
              .child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 15
        iconView.heightDp           = 15

        iconView.image              = R.drawable.icon_pin_filled

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        iconView.color              = theme.colorOrBlack(colorTheme)

        iconView.margin.rightDp     = 4f
        iconView.padding.topDp      = 1f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        labelView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.textId             = R.string.pinned

        labelView.font               = Font.typeface(TextFont.default(),
                                                     TextFontStyle.Regular,
                                                     context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        labelView.color              = theme.colorOrBlack(nameColorTheme)

        labelView.sizeSp             = 14f

        labelView.padding.bottomDp   = 2f

        return layout.linearLayout(context)
    }


    private fun cardTitleView() : TextView
    {
        val titleView               = TextViewBuilder()

        titleView.id                = R.id.card_title

        titleView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        titleView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        titleView.margin.leftDp     = 8f
        titleView.margin.rightDp    = 8f

        titleView.font              = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        titleView.color              = theme.colorOrBlack(nameColorTheme)

        titleView.sizeSp             = 16f

        return titleView.textView(context)
    }


    private fun cardGroupsLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.id               = R.id.card_groups_layout

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun cardFooterView() : RelativeLayout
    {
        val layout = this.cardFooterViewLayout()

        layout.addView(this.cardReadMoreButtonView())

        return layout
    }


    private fun cardFooterViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.rightDp   = 8f
        layout.margin.leftDp    = 8f

        return layout.relativeLayout(context)
    }


    private fun cardReadMoreButtonView() : TextView
    {
        val view                = TextViewBuilder()

        view.id                 = R.id.card_read_more_button

        view.layoutType         = LayoutType.RELATIVE
        view.width              = RelativeLayout.LayoutParams.WRAP_CONTENT
        view.height             = RelativeLayout.LayoutParams.WRAP_CONTENT

        view.addRule(RelativeLayout.ALIGN_PARENT_END)

        view.visibility         = View.GONE

        view.margin.topDp       = 10f
        view.margin.bottomDp    = 4f
        view.margin.rightDp     = 8f

        view.text               = context.getString(R.string.read_more).toUpperCase()

        view.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 15f

        return view.textView(context)
    }


}


class FeedRecyclerViewAdapater(private val feed : Feed,
                               private val feedUI : FeedUI,
                               private val theme : Theme,
                               private val context : Context)
                                : RecyclerView.Adapter<CardViewHolder>(),
                                  SwipeAndDragHelper.ActionCompletionContract
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var touchHelper : ItemTouchHelper? = null


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : CardViewHolder
    {
        val cardView = feedUI.cardView()
        return CardViewHolder(cardView, context)
    }


    override fun onBindViewHolder(viewHolder : CardViewHolder, position : Int)
    {
        val card = this.feed.cards()[position]

        viewHolder.setTitleText(card.title().value)

        if (card.isPinned().value)
            viewHolder.showPinnedLayout()

        val groupViews = card.groups(feed.entityId())
                             .map { it.view(feed.entityId(), context) }
        viewHolder.addGroupViews(groupViews)

        when (card.appAction()) {
            is Just -> viewHolder.showReadMoreButton()
        }
    }


    override fun getItemCount() : Int = this.feed.cards().size


    // Swipe and Drag
    // -----------------------------------------------------------------------------------------

    override fun onViewMoved(oldPosition : Int, newPosition : Int)
    {
//        val otherGroup = groups.get(oldPosition)
//        groups.removeAt(oldPosition)
//        groups.add(newPosition, otherGroup)
//        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(position : Int)
    {
//        groups.removeAt(position)
//        notifyItemRemoved(position)
    }

}




/**
 * Card View Holder
 */
class CardViewHolder(itemView : View,
                     val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var isPinnedLayout     : LinearLayout?  = null
    var titleView          : TextView?  = null
    var groupsLayout       : LinearLayout?  = null
    var readMoreButtonView : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.isPinnedLayout     = itemView.findViewById(R.id.card_pinned_layout)
        this.titleView          = itemView.findViewById(R.id.card_title)
        this.groupsLayout       = itemView.findViewById(R.id.card_groups_layout)
        this.readMoreButtonView = itemView.findViewById(R.id.card_read_more_button)
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun showPinnedLayout()
    {
        this.isPinnedLayout?.visibility = View.VISIBLE
    }


    fun setTitleText(titleString : String)
    {
        this.titleView?.text = titleString
    }


    fun addGroupViews(groupViews : List<View>)
    {
        this.groupsLayout?.removeAllViews()
        this.groupsLayout?.let { layout ->
            groupViews.forEach { view ->
                layout.addView(view)
            }
        }
    }


    fun showReadMoreButton()
    {
        this.readMoreButtonView?.visibility = View.VISIBLE
    }

}
