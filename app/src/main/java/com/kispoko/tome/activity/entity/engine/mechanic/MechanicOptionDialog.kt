
package com.kispoko.tome.activity.entity.engine.mechanic


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.EngineValueBoolean
import com.kispoko.tome.model.game.engine.mechanic.Mechanic
import com.kispoko.tome.model.game.engine.mechanic.MechanicId
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.entityEngineState
import com.kispoko.tome.rts.entity.mechanic
import effect.Err
import effect.Val



/**
 * Mechanic Option Dialog
 */
class MechanicOptionDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var mechanicId : MechanicId? = null
    private var entityId   : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(mechanicId : MechanicId,
                        entityId : EntityId) : MechanicOptionDialog
        {
            val dialog = MechanicOptionDialog()

            val args = Bundle()
            args.putSerializable("mechanic_id", mechanicId)
            args.putSerializable("entity_id", entityId)
            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.mechanicId = arguments.getSerializable("mechanic_id") as MechanicId
        this.entityId   = arguments.getSerializable("entity_id") as EntityId

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val width  = context.resources.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width.toInt(), height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val mechanicId = this.mechanicId
        val entityId = this.entityId

        if (entityId != null && mechanicId != null)
        {
            val mechanic = mechanic(mechanicId, entityId)

            return when (mechanic) {
                is Val -> {
                    val viewBuilder = MechanicOptionsViewBuilder(mechanic.value,
                            this,
                            entityId,
                            context)
                    viewBuilder.view()
                }
                is Err -> {
                    ApplicationLog.error(mechanic.error)
                    super.onCreateView(inflater, container, savedInstanceState)
                }
            }
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}



class MechanicOptionsViewBuilder(val mechanic : Mechanic,
                                 val dialog : DialogFragment,
                                 val entityId : EntityId,
                                 val context : Context)
{



    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // List
        layout.addView(this.listView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor      = colorOrBlack(colorTheme, entityId)

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    fun headerView() : LinearLayout
    {
        val layout  = this.headerViewLayout()

        val mainLayout = headerMainViewLayout()

        val titleView = headerTitleTextView()
        mainLayout.addView(titleView)

        layout.addView(mainLayout)
        layout.addView(this.dividerView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.corners              = Corners(3.0, 3.0, 0.0, 0.0)

        return layout.linearLayout(context)
    }



    private fun headerMainViewLayout() : RelativeLayout
    {
        val layout = RelativeLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_4")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        layout.backgroundColor      = Color.WHITE

        layout.padding.leftDp       = 6f
        layout.padding.rightDp      = 6f
        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.corners              = Corners(2.0, 2.0, 0.0, 0.0)

//        layout.corners              = Corners(3.0, 3.0, 0.0, 0.0)

        return layout.relativeLayout(context)
    }


    private fun headerTitleTextView() : TextView
    {
        val title           = TextViewBuilder()

        title.layoutType    = LayoutType.RELATIVE
        title.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        title.addRule(RelativeLayout.CENTER_VERTICAL)
        title.addRule(RelativeLayout.ALIGN_PARENT_START)

        title.text          = this.mechanic.summaryString()

        title.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        title.color           = colorOrBlack(colorTheme, entityId)

        title.sizeSp          = 18f

        title.margin.leftDp   = 0.5f


        return title.textView(context)
    }


    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    fun listView() : RecyclerView
    {
        val recyclerView            = RecyclerViewBuilder()

        recyclerView.width          = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height         = R.dimen.dialog_choose_value_list_height

        recyclerView.layoutManager  = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        recyclerView.backgroundColor      = colorOrBlack(colorTheme, entityId)


        val adapter = MechanicOptionsRecyclerViewAdapter(mechanic,
                dialog,
                entityId,
                context)
        recyclerView.adapter = adapter


        return recyclerView.recyclerView(context)
    }

}



private fun mechanicOptionView(entityId : EntityId, context : Context) : View
{
    val layout  = mechanicOptionViewLayout(entityId, context)

    layout.addView(mechanicOptionTitleView(entityId, context))

    layout.addView(mechanicOptionSummaryView(entityId, context))

    return layout
}


private fun mechanicOptionViewLayout(entityId : EntityId, context : Context) : LinearLayout
{
    val layout          = LinearLayoutBuilder()

    layout.id           = R.id.dialog_mechanic_options_layout

    layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation  = LinearLayout.VERTICAL

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
    layout.backgroundColor  = Color.WHITE

    layout.margin.bottomDp  = 1f
    layout.margin.leftDp    = 2f
    layout.margin.rightDp   = 2f

    layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

    layout.padding.topDp    = 6f
    layout.padding.bottomDp = 6f
    layout.padding.leftDp   = 6f
    layout.padding.rightDp  = 6f

    return layout.linearLayout(context)
}


private fun mechanicOptionTitleView(entityId : EntityId, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.dialog_mechanic_options_title

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.default(),
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    title.color             = colorOrBlack(colorTheme, entityId)

    title.sizeSp            = 18f

    return title.textView(context)
}


private fun mechanicOptionSummaryView(entityId : EntityId, context : Context) : TextView
{
    val summary             = TextViewBuilder()

    summary.id              = R.id.dialog_mechanic_options_summary

    summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    summary.font            = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
    summary.color           = colorOrBlack(colorTheme, entityId)

    summary.sizeSp          = 16f

    return summary.textView(context)
}


class MechanicOptionsRecyclerViewAdapter(val mechanic : Mechanic,
                                         val dialog : DialogFragment,
                                         val entityId : EntityId,
                                         val context : Context)
                                          : RecyclerView.Adapter<MechanicOptionViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val variables : MutableList<Variable> = mutableListOf()

    init {
        mechanic.variables().forEach {
            variables.add(it)
        }
    }


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MechanicOptionViewHolder
    {
        val itemView = mechanicOptionView(entityId, context)
        return MechanicOptionViewHolder(itemView)
    }


    override fun onBindViewHolder(viewHolder : MechanicOptionViewHolder, position : Int)
    {
        val variable = this.variables[position]

        viewHolder.setTitleText(variable.label().value)

        viewHolder.setSummaryText(variable.description().value)

        viewHolder.setOnClick(View.OnClickListener {
            entityEngineState(entityId) apDo { sheetState ->
                sheetState.removeMechanic(mechanic)
                sheetState.updateVariable(variable.variableId(),
                                          EngineValueBoolean(true),
                                          entityId)
            }
            dialog.dismiss()
        })
    }


    override fun getItemCount() : Int = this.variables.size

}



// ---------------------------------------------------------------------------------------------
// VALUE VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class MechanicOptionViewHolder(itemView : View)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout        : LinearLayout? = null
    var titleView     : TextView?     = null
    var summaryView   : TextView?     = null

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.dialog_mechanic_options_layout)
        this.titleView   = itemView.findViewById(R.id.dialog_mechanic_options_title)
        this.summaryView = itemView.findViewById(R.id.dialog_mechanic_options_summary)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setTitleText(valueString : String)
    {
        this.titleView?.text = valueString
    }


    fun setSummaryText(summaryString : String)
    {
        this.summaryView?.text = summaryString
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}

