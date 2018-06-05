
package com.kispoko.tome.activity


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.R.string.groups
import com.kispoko.tome.activity.session.NewSessionActivity
import com.kispoko.tome.activity.sheet.group.GroupListItemRecyclerViewAdapter
import com.kispoko.tome.activity.sheet.group.GroupListItemViewHolder
import com.kispoko.tome.activity.sheet.group.SwipeAndDragHelper
import com.kispoko.tome.activity.sheet.group.groupItemView
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.model.sheet.group.GroupReference
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.util.Util
import com.kispoko.tome.util.configureToolbar
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout



// pinned
// news
// welcome to tome beta
// > still prototype, schedule
// > content still being added, no licensed content yet
// > about Tome / plan
// > FAQ
//
// support app, donate
//
// core rulebook quick link
//
// random creature
//
// random character sheet
//
// player quote / moment
//
// survey
//
// random spell
//
// random weapon


/**
 * Feed Activity
 */
class FeedActivity : AppCompatActivity(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Any>
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var rfabHelper : RapidFloatingActionHelper? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_feed)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.tome))

        this.findViewById<TextView>(R.id.toolbar_title)?.let { titleTextView ->
            titleTextView.textSize = Util.spToPx(7.2f, this).toFloat()

        }

        this.applyTheme(officialAppThemeLight)

        this.initializeFAB()

        this.initializeView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeView()
    {
        val contentView = this.findViewById<LinearLayout>(R.id.content)

    }


    private fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.decorView.setSystemUiVisibility(flags)
//            this.getWindow().setStatusBarColor(Color.WHITE);
//        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_main_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }


    // -----------------------------------------------------------------------------------------
    // FLOATING ACTION BUTTON
    // -----------------------------------------------------------------------------------------

    private fun initializeFAB()
    {
        val rfaContent = RapidFloatingActionContentLabelList(this)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)

        val items = mutableListOf<RFACLabelItem<Int>>()
        items.add(RFACLabelItem<Int>()
                .setLabel("Open Session")
                .setResId(R.drawable.ic_fab_open)
                .setIconNormalColor(Color.parseColor("#469FCD"))
                .setIconPressedColor(-0xf2acfe)
                .setLabelColor(Color.parseColor("#1887C0"))
                .setWrapper(2)
        )
        items.add(RFACLabelItem<Int>()
                .setLabel("New Session")
                .setResId(R.drawable.ic_fab_add)
                .setIconNormalColor(Color.parseColor("#58C480"))
                .setIconPressedColor(Color.parseColor("#6ACA8E"))
                .setLabelColor(Color.parseColor("#45BD72"))
                .setWrapper(3)
        )
        rfaContent
                .setItems(items.toList())
                .setIconShadowRadius(Util.dpToPixel(5f))
                .setIconShadowColor(-0x777778)
                .setIconShadowRadius(Util.dpToPixel(5f))

        val fabLayout = this.findViewById<RapidFloatingActionLayout>(R.id.fab_layout)
        val fabView = this.findViewById<RapidFloatingActionButton>(R.id.fab)

        this.rfabHelper = RapidFloatingActionHelper(this,
                                                    fabLayout,
                                                    fabView,
                                                    rfaContent
                                                    ).build()

    }



    override fun onRFACItemIconClick(position : Int, item : RFACLabelItem<Any>?)
    {
        this.rfabHelper?.toggleContent()

        when (position) {
            1 -> this.newSession()
        }
    }


    override fun onRFACItemLabelClick(position : Int, item : RFACLabelItem<Any>?)
    {
        this.rfabHelper?.toggleContent()

        when (position) {
            1 -> this.newSession()
        }
    }



    // -----------------------------------------------------------------------------------------
    // SESSIONS
    // -----------------------------------------------------------------------------------------

    private fun newSession()
    {
        val intent = Intent(this, NewSessionActivity::class.java)
        this.startActivity(intent)
    }

}


class FeedUI(val theme : Theme,
             val activity : AppCompatActivity)
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun feedAdapter()
    {

    }


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

        recyclerView.adapter            = this.feedAdapter()

        return recyclerView.recyclerView(context)
    }


}




class FeedRecyclerViewAdapater(val theme : Theme,
                               val context : Context)
                                : RecyclerView.Adapter<GroupListItemViewHolder>(), SwipeAndDragHelper.ActionCompletionContract
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var touchHelper : ItemTouchHelper? = null


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : GroupListItemViewHolder
    {
        val itemView = groupItemView(theme, context)
        return GroupListItemViewHolder(itemView, entityId, context)
    }


    override fun onBindViewHolder(viewHolder : GroupListItemViewHolder, position : Int)
    {
        val group = this.groups[position]

        viewHolder.setNameText(group.name().value)
        viewHolder.setSummaryText(group.summary().value)
    }


    override fun getItemCount() : Int = this.groups.size


    // Swipe and Drag
    // -----------------------------------------------------------------------------------------

    override fun onViewMoved(oldPosition : Int, newPosition : Int)
    {
        Log.d("***GROUP LIST", "on view moved")
        val otherGroup = groups.get(oldPosition)
        groups.removeAt(oldPosition)
        groups.add(newPosition, otherGroup)
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(position : Int)
    {
        groups.removeAt(position)
        notifyItemRemoved(position)
    }

}




/**
 * Group List Item View Holder
 */
class GroupListItemViewHolder(itemView : View,
                              val entityId: EntityId,
                              val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout? = null
    var nameView    : TextView?  = null
    var summaryView : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.group_list_item_layout)
        this.nameView    = itemView.findViewById(R.id.group_list_item_name)
        this.summaryView = itemView.findViewById(R.id.group_list_item_summary)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setNameText(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setSummaryText(summaryString : String)
    {
        this.summaryView?.text = summaryString
    }

}

