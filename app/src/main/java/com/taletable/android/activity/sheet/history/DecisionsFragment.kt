
package com.taletable.android.activity.sheet.history


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.app.AppError
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.task.Task
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.entityEngineState
import effect.Err
import effect.Val



class DecisionsFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // Must be used from a sheet activity
    var sheetId : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(sheetId : EntityId) : DecisionsFragment
        {
            val decisionsFragment = DecisionsFragment()

            val args = Bundle()
            args.putSerializable("sheet_id", sheetId)

            decisionsFragment.arguments = args

            return decisionsFragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        this.sheetId = arguments?.getSerializable("sheet_id") as EntityId
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val sheetId = this.sheetId
        val context = this.context

        return if (sheetId != null && context != null)
        {

            val entityState = entityEngineState(sheetId)
            val completedTasks = when (entityState)
            {
                is Val -> entityState.value.completedTasks()
                is Err -> listOf()
            }

            completedTaskRecyclerView(completedTasks,
                                      officialAppThemeLight,
                                      sheetId,
                                      context)
        }
        else
        {
            null
        }
    }


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

}




fun completedTaskRecyclerView(completedTasks : List<Task>,
                              theme : Theme,
                              entityId : EntityId,
                              context : Context) : RecyclerView
{
    val recyclerView                = RecyclerViewBuilder()

    recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
    recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

    recyclerView.layoutManager      = LinearLayoutManager(context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
    recyclerView.backgroundColor    = colorOrBlack(colorTheme, entityId)

    recyclerView.adapter            = CompletedTaskRecyclerViewAdapter(completedTasks, theme, entityId, context)

    recyclerView.padding.leftDp     = 6f
    recyclerView.padding.rightDp    = 6f

    return recyclerView.recyclerView(context)
}



class CompletedTaskRecyclerViewAdapter(val completedTasks : List<Task>,
                                       val theme : Theme,
                                       val entityId : EntityId,
                                       val context : Context)
                                        : RecyclerView.Adapter<CompletedTaskViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : CompletedTaskViewHolder
    {
        val itemView = completedTaskView(theme, context)
        return CompletedTaskViewHolder(itemView, entityId, context)
    }


    override fun onBindViewHolder(viewHolder : CompletedTaskViewHolder, position : Int)
    {
        val task = this.completedTasks[position]

        viewHolder.setNameText(task.title.value)
    }


    override fun getItemCount() : Int = this.completedTasks.size

}



fun completedTaskView(theme : Theme, context : Context) : LinearLayout
{
    val layout = completedTaskViewLayout(context)

    layout.addView(taskNameView(theme, context))

    return layout
}


fun completedTaskViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    layout.backgroundColor  = Color.WHITE

    layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

    layout.padding.topDp    = 8f
    layout.padding.bottomDp = 8f
    layout.padding.leftDp   = 8f
    layout.padding.rightDp  = 8f

    layout.margin.topDp     = 4f

    layout.elevation        = 2f

    return layout.linearLayout(context)
}


private fun taskNameView(theme: Theme, context : Context) : TextView
{
    val nameView                = TextViewBuilder()

    nameView.id                 = R.id.name_view

    nameView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    nameView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    nameView.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    nameView.color              = theme.colorOrBlack(colorTheme)

    nameView.sizeSp             = 18f

    return nameView.textView(context)
}




/**
 * Group List Item View Holder
 */
class CompletedTaskViewHolder(itemView : View,
                              val entityId: EntityId,
                              val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout? = null
    var nameView    : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.nameView    = itemView.findViewById(R.id.name_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setNameText(nameString : String)
    {
        this.nameView?.text = nameString
    }

}


