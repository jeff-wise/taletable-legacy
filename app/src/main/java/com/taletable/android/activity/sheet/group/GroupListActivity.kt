
package com.taletable.android.activity.sheet.group


import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.*
import android.widget.*
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.tag.TagQuery
import com.taletable.android.model.sheet.group.Group
import com.taletable.android.model.sheet.group.GroupReference
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.groups
import androidx.recyclerview.widget.ItemTouchHelper
import com.taletable.android.model.sheet.group.GroupId
import com.taletable.android.rts.entity.sheet.UpdateTarget
import com.taletable.android.util.configureToolbar



/**
 * Group List Activity
 */
class GroupListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var groupRefs    : List<GroupReference> = listOf()
    private var title        : String?              = null
    private var tagQuery     : TagQuery?            = null
    private var entityId     : EntityId?         = null
    private var updateTarget : UpdateTarget?     = null

    private var newGroupReferenceList : List<GroupReference>? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_group_list)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("title"))
            this.title = this.intent.getStringExtra("title")

        if (this.intent.hasExtra("group_references"))
            this.groupRefs = this.intent.getSerializableExtra("group_references") as List<GroupReference>

        if (this.intent.hasExtra("tag_query"))
            this.tagQuery = this.intent.getSerializableExtra("tag_query") as TagQuery

        if (this.intent.hasExtra("entity_id"))
            this.entityId = this.intent.getSerializableExtra("entity_id") as EntityId

        if (this.intent.hasExtra("update_target"))
            this.updateTarget = this.intent.getSerializableExtra("update_target") as UpdateTarget

        // (3) Configure View
        // -------------------------------------------------------------------------------------

//        val title = this.title
//        if (title != null)
//        else
//            this.configureToolbar(getString(R.string.groups))
        this.configureToolbar(getString(R.string.edit_groups), TextFont.RobotoCondensed, TextFontStyle.Bold)

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

//        val doneButtonLayout = this.findViewById<LinearLayout>(R.id.toolbar_done_button)
//        doneButtonLayout?.let {
//            it.addView(this.doneButtonView(officialAppThemeLight))
//        }

        this.entityId?.let { entityId ->
            val groupList = groups(groupRefs, entityId)

            val theme = officialAppThemeLight

            val adapter = GroupListItemRecyclerViewAdapter(groupList.toMutableList(), title ?: "", theme, entityId, this)

            val swipeAndDragHelper = SwipeAndDragHelper(adapter)
            val touchHelper = ItemTouchHelper(swipeAndDragHelper)
//
//            val recyclerView = groupListRecyclerView(this.groupRefs,
//                                                     adapter,
//                                                     theme,
//                                                     entityId,
//                                                     this)

            val recyclerView = findViewById<RecyclerView>(R.id.group_list)
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter

//            val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
//            recyclerView.addItemDecoration(dividerItemDecoration)

            recyclerView.invalidate()

            touchHelper.attachToRecyclerView(recyclerView)

            contentView?.addView(recyclerView)
        }
    }


    private fun initializeFAB()
    {
        val fab = this.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, GroupSearchActivity::class.java)

            this.title?.let { intent.putExtra("title", it) }
            this.tagQuery?.let { intent.putExtra("tag_query", it) }
            this.entityId?.let { intent.putExtra("entity_id", it) }

            startActivity(intent)
        }
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
//            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.setSystemUiVisibility(flags)
            this.getWindow().setStatusBarColor(Color.WHITE);
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_close_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageView>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }


    fun updateGroupReferenceList(updatedGroupIds : List<GroupId>)
    {
        val originalGroupIdList = this.groupRefs.map { it.groupId() }

        val newGroupIdList = this.newGroupReferenceList?.let { it.map { it.groupId() } }

        val isUnique = if (newGroupIdList != null)
                           newGroupIdList != updatedGroupIds
                       else
                           originalGroupIdList != updatedGroupIds

        if (isUnique)
        {
            val groupRefById  : MutableMap<GroupId,GroupReference> =
                                    this.groupRefs.associateBy { it.groupId() }
                                            as MutableMap<GroupId,GroupReference>

            val newGroupRefList : MutableList<GroupReference> = mutableListOf()

            updatedGroupIds.forEach {
                groupRefById.get(it)?.let { newGroupRefList.add(it) }
            }

            this.newGroupReferenceList = newGroupRefList
        }

    }

}




fun groupItemView(theme : Theme, context : Context) : RelativeLayout
{
    val layout = groupItemViewLayout(context)

    // Drag Icon
    layout.addView(dragHandleIconView(theme, context))

    // Info
    val contentView = groupItemContentView(theme, context)
    val contentViewLP = contentView.layoutParams as RelativeLayout.LayoutParams
    contentViewLP.addRule(RelativeLayout.END_OF, R.id.drag_icon_view)
    contentView.layoutParams = contentViewLP

    layout.addView(contentView)

    // Options
    layout.addView(groupItemOptionIconView(theme, context))

    return layout
}


private fun groupItemViewLayout(context : Context) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.backgroundColor  = Color.WHITE

    layout.gravity          = Gravity.CENTER_VERTICAL

    layout.padding.topDp    = 18f
    layout.padding.bottomDp = 18f
    layout.padding.leftDp   = 17f
    layout.padding.rightDp  = 15.5f

//    layout.margin.leftDp    = 2f
//    layout.margin.rightDp   = 2f
    layout.margin.topDp     = 1f

    return layout.relativeLayout(context)
}


private fun groupItemContentView(theme : Theme, context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val nameView                = TextViewBuilder()
    val summaryView             = TextViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.addRule(RelativeLayout.CENTER_VERTICAL)

    layout.orientation          = LinearLayout.VERTICAL

    layout.padding.leftDp       = 19f

    layout.child(nameView)
//          .child(summaryView)

    // (3 A) Name
    // -------------------------------------------------------------------------------------

    nameView.id                 = R.id.group_list_item_name

    nameView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    nameView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    nameView.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    nameView.color              = theme.colorOrBlack(nameColorTheme)

    nameView.sizeSp             = 22f

    // (3 B) Summary
    // -------------------------------------------------------------------------------------

    summaryView.id              = R.id.group_list_item_summary

    summaryView.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    summaryView.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    summaryView.font            = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

    val summaryColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
    summaryView.color           = theme.colorOrBlack(summaryColorTheme)

    summaryView.sizeSp          = 16f

    return layout.linearLayout(context)
}


private fun dragHandleIconView(theme : Theme, context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------------------

    layout.id                   = R.id.drag_icon_view

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

    layout.addRule(RelativeLayout.CENTER_VERTICAL)

    layout.child(iconView)

    // (3) Icon
    // -------------------------------------------------------------------------------------

    iconView.widthDp            = 23
    iconView.heightDp           = 23

    iconView.image              = R.drawable.icon_drag_reorder

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


private fun groupItemOptionIconView(theme : Theme, context : Context) : LinearLayout
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

    layout.orientation          = LinearLayout.VERTICAL

    layout.addRule(RelativeLayout.CENTER_VERTICAL)
    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.child(iconView)

    // (3) Icon
    // -------------------------------------------------------------------------------------

    iconView.widthDp            = 24
    iconView.heightDp           = 24

    iconView.image              = R.drawable.icon_vertical_ellipsis

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_24"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


private fun listHeaderView(count : Int,
                           type : String,
                           theme : Theme,
                           context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val countView               = TextViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.padding.leftDp       = 17f
    layout.padding.topDp        = 12f
    layout.padding.bottomDp     = 12f

    layout.child(countView)

    // (3 A) Name
    // -------------------------------------------------------------------------------------

    countView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    countView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    countView.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

    val typeString = if (count == 1 && type.endsWith("s")) {
        type.dropLast(1)
    } else {
        type
    }

    countView.text               = "$count $typeString".toUpperCase()

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))
    countView.color              = theme.colorOrBlack(nameColorTheme)

    countView.sizeSp             = 14f

    return layout.linearLayout(context)
}





class GroupListItemRecyclerViewAdapter(val groups : MutableList<Group>,
                                       val groupType : String,
                                       val theme : Theme,
                                       val entityId : EntityId,
                                       val groupListActivity : GroupListActivity)
                     : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SwipeAndDragHelper.ActionCompletionContract
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var touchHelper : ItemTouchHelper? = null

    val context = groupListActivity


    private val HEADER = 0
    private val GROUP  = 1

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int = when (position)
    {
        0    -> HEADER
        else -> GROUP
    }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        if (position != 0)
        {
            val group = this.groups[position - 1]

            val itemViewHolder = viewHolder as GroupListItemViewHolder

            itemViewHolder.setNameText(group.name().value)
            itemViewHolder.setSummaryText(group.summary().value)
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder =
        when (viewType)
        {
            HEADER ->
            {
                val headerView = listHeaderView(groups.size, groupType, theme, context)
                HeaderViewHolder(headerView)
            }
            // VALUE
            else ->
            {
                val itemView = groupItemView(theme, context)
                GroupListItemViewHolder(itemView, entityId, context)
            }
        }



    override fun getItemCount() : Int = this.groups.size + 1


    // Swipe and Drag
    // -----------------------------------------------------------------------------------------

    override fun onViewMoved(oldPosition : Int, newPosition : Int)
    {
        val otherGroup = groups.get(oldPosition)
        groups.removeAt(oldPosition)
        groups.add(newPosition, otherGroup)
        notifyItemMoved(oldPosition, newPosition)

        groupListActivity.updateGroupReferenceList(groups.map { it.id })
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


class SwipeAndDragHelper(private val contract : ActionCompletionContract) : ItemTouchHelper.Callback()
{

    override fun getMovementFlags(recyclerView : RecyclerView,
                                  viewHolder : RecyclerView.ViewHolder) : Int
    {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView : RecyclerView,
                        viewHolder : RecyclerView.ViewHolder,
                        target : RecyclerView.ViewHolder) : Boolean
    {
        contract.onViewMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder : RecyclerView.ViewHolder, direction : Int)
    {
        contract.onViewSwiped(viewHolder.adapterPosition)
    }

    override fun isLongPressDragEnabled() : Boolean {
        return true
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val alpha = 1 - Math.abs(dX) / recyclerView.width
            viewHolder.itemView.alpha = alpha
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface ActionCompletionContract
    {
        fun onViewMoved(oldPosition: Int, newPosition: Int)

        fun onViewSwiped(position: Int)
    }

}



class HeaderViewHolder(val headerView : LinearLayout) : RecyclerView.ViewHolder(headerView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

}

