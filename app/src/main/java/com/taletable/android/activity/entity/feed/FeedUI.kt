
package com.taletable.android.activity.entity.feed


import android.content.Context
import android.graphics.Color
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*



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

        layout.addView(this.cardInfoView())

        layout.addView(this.cardHeaderView())

        // layout.addView(this.dividerView())

        layout.addView(this.cardGroupsLayout())

        return layout
    }


    private fun cardViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.elevation        = 1f

        layout.backgroundColor  = Color.WHITE


        //layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout.margin.topDp     = 16f

//        layout.margin.leftDp    = 6f
//        layout.margin.rightDp   = 6f

        return layout.linearLayout(context)
    }


    private fun cardHeaderView() : LinearLayout
    {
        val layout = this.cardHeaderViewLayout()

        layout.addView(this.cardIconView())

        val infoView = this.cardOptionsButtonView()

//        val infoViewLP = infoView.layoutParams as RelativeLayout.LayoutParams
//        infoViewLP.addRule(RelativeLayout.END_OF, R.id.icon_view_layout)
//        infoView.layoutParams = infoViewLP

        layout.addView(infoView)

        return layout
    }


    private fun cardHeaderViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.bottomDp      = 14f

        layout.padding.leftDp       = 14f
        layout.padding.rightDp      = 14f

        layout.padding.topDp        = 14f
        layout.padding.bottomDp        = 14f
//        layout.margin.topDp        = 10f

//        layout.corners          = Corners(3.0, 3.0, 0.0, 0.0)

        layout.gravity              = Gravity.TOP

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


    private fun cardIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.id                   = R.id.icon_view_layout


        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_23"))))
        layout.backgroundColor              = theme.colorOrBlack(bgColorTheme)
        layout.backgroundColor              = Color.TRANSPARENT

        layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

        layout.margin.topDp         = 4f

//        layout.padding.topDp        = 4f
        layout.padding.bottomDp        = 4f
//        layout.padding.leftDp        = 4f
        layout.padding.rightDp        = 8f

        layout.gravity              = Gravity.CENTER

        layout.margin.rightDp       = 12f

        layout.child(iconView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 25
        iconView.heightDp           = 25

        iconView.id                 = R.id.icon_view

        iconView.image              = R.drawable.icon_open_in_window

        iconView.color              = Color.WHITE

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_9"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)
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

        //layout.layoutType           = LayoutType.RELATIVE
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
        layout.backgroundColor              = theme.colorOrBlack(bgColorTheme)

        layout.padding.leftDp        = 16f
        layout.padding.rightDp        = 14f

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 20f

//        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        return layout.linearLayout(context)
    }




    private fun cardTitleView() : TextView
    {
        val titleView               = TextViewBuilder()

        titleView.id                = R.id.title_view

        titleView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        titleView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        titleView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
        titleView.color              = theme.colorOrBlack(nameColorTheme)
        //titleView.color              = Color.WHITE

        titleView.sizeSp             = 16f

        titleView.margin.rightDp        = 10f

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
                                                    TextFontStyle.Italic,
                                                    context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_11"))))
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

        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        //layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.ALIGN_PARENT_TOP)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
        //layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)
        layout.backgroundColor      = Color.TRANSPARENT

//        layout.padding.topDp        = 10f
//        layout.padding.bottomDp      = 10f
//        layout.padding.leftDp        = 12f
//        layout.padding.rightDp        = 12f

        //layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        layout.child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 24
        iconView.heightDp           = 24

        iconView.image              = R.drawable.icon_vertical_ellipsis

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        iconView.color              = theme.colorOrBlack(colorTheme)


        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.id                = R.id.button
//        labelView.text              = "PLAY"

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        labelView.color              = theme.colorOrBlack(labelColorTheme)
        labelView.color             = Color.WHITE

        labelView.sizeSp            = 22f

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

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

        layout.corners          = Corners(0.0, 0.0, 3.0, 3.0)

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

        layout.margin.bottomDp   = 8f

        layout.margin.leftDp        = 12f
        layout.margin.rightDp        = 12f

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

        card.actionLabel().doMaybe { actionLabel ->
        card.appAction().doMaybe { action ->
            viewHolder.setAction(action, actionLabel.value)
        } }

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
    var buttonView         : TextView?  = null
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
        this.buttonView        = itemView.findViewById(R.id.button)
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


    fun setAction(action : AppAction, actionLabel : String)
    {
        when (action) {
            is AppActionOpenNewsArticle -> {
                this.iconView?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_news))
            }
            is AppActionOpenSession -> {
                this.iconView?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_open_in_window))
            }
        }

        this.buttonView?.text = actionLabel
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

