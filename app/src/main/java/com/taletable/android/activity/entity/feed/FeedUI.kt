
package com.taletable.android.activity.entity.feed


import android.content.Context
import android.graphics.Color
import android.sax.RootElement
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.sheet.group.SwipeAndDragHelper
import com.taletable.android.lib.ui.*
import com.taletable.android.model.AppAction
import com.taletable.android.model.AppActionOpenNewsArticle
import com.taletable.android.model.AppActionOpenSession
import com.taletable.android.model.feed.Feed
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
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

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
//        recyclerView.backgroundColor    = theme.colorOrBlack(colorTheme)

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

        layout.addView(this.cardHeaderView())

        layout.addView(this.cardGroupsLayout())

        return layout
    }


    private fun cardViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.elevation        = 2f

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 8f

        layout.padding.topDp    = 10f

        return layout.linearLayout(context)
    }


    private fun cardHeaderView() : RelativeLayout
    {
        val layout = this.cardHeaderViewLayout()

        layout.addView(this.cardIconView())

        val infoView = this.cardInfoView()
        layout.addView(infoView)

        val infoViewLP = infoView.layoutParams as RelativeLayout.LayoutParams
        infoViewLP.addRule(RelativeLayout.END_OF, R.id.icon_view_layout)
        infoView.layoutParams = infoViewLP

        layout.addView(this.cardOptionsButtonView())

        return layout
    }


    private fun cardHeaderViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.bottomDp     = 10f

        return layout.relativeLayout(context)
    }


    private fun cardIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.id                   = R.id.icon_view_layout

        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.margin.leftDp        = 12f

        layout.backgroundResource   = R.drawable.bg_card_icon

        layout.gravity              = Gravity.CENTER

        layout.child(iconView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 19
        iconView.heightDp           = 19

        iconView.id                 = R.id.icon_view

        iconView.image              = R.drawable.icon_open_in_window

        iconView.color              = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun cardInfoView() : LinearLayout
    {
        val layout = this.cardInfoViewLayout()

        layout.addView(this.cardTitleView())

        layout.addView(this.cardReasonView())

        return layout
    }



    private fun cardInfoViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.margin.leftDp        = 12f

        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        return layout.linearLayout(context)
    }




    private fun cardTitleView() : TextView
    {
        val titleView               = TextViewBuilder()

        titleView.id                = R.id.title_view

        titleView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        titleView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        titleView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Medium,
                                                    context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        titleView.color              = theme.colorOrBlack(nameColorTheme)

        titleView.sizeSp             = 15f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_2"))))
//        titleView.backgroundColor    = theme.colorOrBlack(bgColorTheme)
        //layout.backgroundColor  = Color.WHITE

        return titleView.textView(context)
    }


    private fun cardReasonView() : TextView
    {
        val titleView               = TextViewBuilder()

        titleView.id                = R.id.reason_view

        titleView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        titleView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        titleView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Medium,
                                                    context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        titleView.color              = theme.colorOrBlack(nameColorTheme)

        titleView.sizeSp             = 15f

        return titleView.textView(context)
    }



    private fun cardOptionsButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.margin.rightDp       = 12f

        layout.child(iconView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 24
        iconView.heightDp           = 24

        iconView.image              = R.drawable.icon_vertical_ellipsis

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        iconView.color              = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    private fun cardGroupsLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.id               = R.id.card_groups_layout

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

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

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_2"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        return layout.relativeLayout(context)
    }


    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        layout.margin.leftDp    = 12f
        layout.margin.rightDp   = 12f

        return layout.linearLayout(context)
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


        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.margin.rightDp       = 8f

        layout.backgroundColor      = Color.WHITE

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.child(labelView)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        labelView.id                = R.id.card_read_more_button

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_3"))))
        labelView.color             = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 15f

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
        viewHolder.setReasonText(card.reason().value)

        card.appAction().doMaybe {
            viewHolder.setAction(it)
        }

        val groupViews = card.groups(feed.entityId())
                             .map { it.view(feed.entityId(), context) }
        viewHolder.addGroupViews(groupViews)
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

    var titleView          : TextView?  = null
    var reasonView         : TextView?  = null
    var iconView           : ImageView?  = null
    var groupsLayout       : LinearLayout?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.titleView          = itemView.findViewById(R.id.title_view)
        this.reasonView         = itemView.findViewById(R.id.reason_view)
        this.iconView           = itemView.findViewById(R.id.icon_view)
        this.groupsLayout       = itemView.findViewById(R.id.card_groups_layout)
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------


    fun setTitleText(titleString : String)
    {
        this.titleView?.text = titleString
    }


    fun setReasonText(reason : String)
    {
        this.reasonView?.text = reason
    }


    fun setAction(action : AppAction)
    {
        when (action) {
            is AppActionOpenNewsArticle -> {
                this.iconView?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_news))
            }
            is AppActionOpenSession -> {
                this.iconView?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_open_in_window))
            }
        }
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


}

//
//    private fun cardPinnedView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout                  = LinearLayoutBuilder()
//        val iconView                = ImageViewBuilder()
//        val labelView               = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.id                   = R.id.card_pinned_layout
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.gravity              = Gravity.CENTER_VERTICAL
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
////        layout.backgroundColor              = theme.colorOrBlack(bgColorTheme)
//        layout.backgroundColor  = Color.WHITE
//
//        layout.visibility           = View.GONE
//
//        layout.padding.leftDp        = 10f
//        layout.padding.rightDp       = 12f
//
//        layout.child(iconView)
//              .child(labelView)
//
//        // (3 A) Icon
//        // -------------------------------------------------------------------------------------
//
//        iconView.widthDp            = 15
//        iconView.heightDp           = 15
//
//        iconView.image              = R.drawable.icon_pin_filled
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//        iconView.color              = theme.colorOrBlack(colorTheme)
//
//        iconView.margin.rightDp     = 4f
//        iconView.padding.topDp      = 1f
//
//        // (3 B) Label
//        // -------------------------------------------------------------------------------------
//
//        labelView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        labelView.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        labelView.textId             = R.string.pinned
//
//        labelView.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                     TextFontStyle.Regular,
//                                                     context)
//
//        val nameColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//        labelView.color              = theme.colorOrBlack(nameColorTheme)
//
//        labelView.sizeSp             = 14f
//
//        labelView.padding.bottomDp   = 2f
//
//        return layout.linearLayout(context)
//    }

