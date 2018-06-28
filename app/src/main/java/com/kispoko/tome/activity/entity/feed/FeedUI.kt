
package com.kispoko.tome.activity.entity.feed


import android.content.Context
import android.graphics.Color
import android.support.v4.app.FragmentActivity
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
             val activity : FragmentActivity)
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

        recyclerView.padding.bottomDp   = 80f
        recyclerView.clipToPadding      = false

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

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f
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

        labelView.font               = Font.typeface(TextFont.RobotoCondensed,
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

        titleView.font              = Font.typeface(TextFont.RobotoCondensed,
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


    private fun cardReadMoreButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val labelView               = TextViewBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------


        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.margin.topDp         = 10f
        layout.margin.bottomDp      = 4f
        layout.margin.rightDp       = 2f

        layout.padding.topDp        = 4f
        layout.padding.bottomDp     = 4f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_85"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.child(labelView)
        //        .child(iconView)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        labelView.id                = R.id.card_read_more_button

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.SemiBold,
                                                    context)

        labelView.color             = Color.WHITE

        labelView.sizeSp            = 14f

        labelView.margin.rightDp    = 3f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 16
        iconView.heightDp           = 16

        iconView.image              = R.drawable.icon_arrow_right

        iconView.color              = Color.WHITE

        return layout.linearLayout(context)
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
            is Just -> {
                val actionLabel = card.actionLabel()
                when (actionLabel) {
                    is Just    -> viewHolder.showReadMoreButton(actionLabel.value.value.toUpperCase())
                    is Nothing -> viewHolder.showReadMoreButton("READ MORE")
                }
            }
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


    fun showReadMoreButton(label : String)
    {
        this.readMoreButtonView?.visibility = View.VISIBLE
        this.readMoreButtonView?.text = label
    }

}
