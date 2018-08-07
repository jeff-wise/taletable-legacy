
package com.taletable.android.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.task.Task
import com.taletable.android.model.engine.variable.Variable
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.entityEngineState
import com.taletable.android.rts.entity.variable
import java.io.Serializable



/**
 * Variable Toggle Editor Dialog
 */
class VariableToggleEditorDialog : BottomSheetDialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var variableIds   : List<VariableId>  = listOf()
    private var task          : Task?             = null
    private var title         : String?           = null
    private var maxSize       : Int?              = null
    private var entityId      : EntityId?         = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(variableIds : List<VariableId>,
                        task : Task,
                        title : String,
                        maxSize : Int?,
                        entityId : EntityId) : VariableToggleEditorDialog
        {
            val dialog = VariableToggleEditorDialog()

            val args = Bundle()
            args.putSerializable("variable_ids", variableIds as Serializable)
            args.putSerializable("task", task)
            args.putString("title", title)
            args.putSerializable("max_size", maxSize)
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

        this.variableIds = arguments?.getSerializable("variable_ids") as List<VariableId>
        this.task        = arguments?.getSerializable("task") as Task?
        this.title       = arguments?.getString("title")
        this.maxSize     = arguments?.getSerializable("max_size") as Int?
        this.entityId    = arguments?.getSerializable("entity_id") as EntityId


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window.attributes.windowAnimations = R.style.DialogAnimation

        dialog.setContentView(dialogLayout)

        val window = dialog.window
        val wlp = window.attributes

        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp

        val width  = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width, height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val entityId = this.entityId
        val task = this.task
        val context = this.context

        return if (entityId != null && context != null && task != null)
        {
            val viewBuilder = VariableToggleEditorUI(this.variableIds,
                                                     task,
                                                     this.title,
                                                     this.maxSize,
                                                     entityId,
                                                     context,
                                                     this)
            viewBuilder.view()
        }
        else
        {
            super.onCreateView(inflater, container, savedInstanceState)
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



class VariableToggleEditorUI(val variableIds : List<VariableId>,
                             val task : Task,
                             val title : String?,
                             val maxSize : Int?,
                             val entityId : EntityId,
                             val context : Context,
                             val dialog : BottomSheetDialogFragment)
{

    // -----------------------------------------------------------------------------------------
    // | PROPERTIES
    // -----------------------------------------------------------------------------------------

    val selectedVariableIds : MutableSet<VariableId> = mutableSetOf()


    // -----------------------------------------------------------------------------------------
    // | METHODS
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // | VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // List
        layout.addView(this.recyclerView())

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


    // | Views > Header
    // -----------------------------------------------------------------------------------------

    fun headerView() : LinearLayout
    {
        val layout  = this.headerViewLayout()

        val mainLayout = headerMainViewLayout()

        val titleView = headerTitleTextView()
        val iconView  = headerIconView()

        mainLayout.addView(titleView)
        mainLayout.addView(iconView)

        layout.addView(mainLayout)
        layout.addView(this.headerBottomBorderView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }



    private fun headerMainViewLayout() : RelativeLayout
    {
        val layout = RelativeLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor      = Color.WHITE

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f
        layout.padding.topDp        = 4f
        layout.padding.bottomDp     = 4f

        layout.corners              = Corners(2.0, 2.0, 0.0, 0.0)

        return layout.relativeLayout(context)
    }


    private fun headerTitleTextView() : TextView
    {
        val title               = TextViewBuilder()

        title.layoutType        = LayoutType.RELATIVE
        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.addRule(RelativeLayout.CENTER_VERTICAL)
        title.addRule(RelativeLayout.ALIGN_PARENT_START)

        title.text              = this.title

        title.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        title.color             = colorOrBlack(colorTheme, entityId)

        title.sizeSp            = 17f

        title.margin.leftDp     = 6f

        return title.textView(context)
    }


    private fun headerIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout       = LinearLayoutBuilder()
        val labelView    = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType   = LayoutType.RELATIVE
        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.corners      = Corners(3.0, 3.0, 3.0, 3.0)

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("green_tint_1"))))
//        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.backgroundColor  = Color.WHITE

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.onClick = View.OnClickListener {
            entityEngineState(entityId).apDo {
                it.completeTask(task)
            }

            dialog.dismiss()
        }

        layout.child(labelView)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        labelView.color             = colorOrBlack(textColorTheme, entityId)

        labelView.text              = context.getString(R.string.done).toUpperCase()

        labelView.font              = Font.typeface(TextFont.Cabin,
                                                    TextFontStyle.Bold,
                                                    context)

        labelView.sizeSp            = 16f

        return layout.linearLayout(context)
    }


    // List View
    // -----------------------------------------------------------------------------------------

    fun recyclerView() : RecyclerView
    {
        val recyclerView            = RecyclerViewBuilder()

        recyclerView.width          = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.heightDp       = 300

        recyclerView.layoutManager  = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
        recyclerView.backgroundColor      = colorOrBlack(colorTheme, entityId)

        recyclerView.adapter        = VariableToggleRecyclerViewAdapter(this.variables(),
                                                                        this,
                                                                        this.entityId,
                                                                        context)

        return recyclerView.recyclerView(context)
    }



    private fun headerBottomBorderView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

//        layout.margin.topDp     = 5f
//        layout.margin.bottomDp  = 5f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // | Variables
    // -----------------------------------------------------------------------------------------

    private fun variables() : List<Variable>
    {
        val variables : MutableList<Variable> = mutableListOf()

        this.variableIds.forEach {
            variable(it, entityId).apDo {
                variables.add(it)
            }
        }

        return variables
    }

}


// -----------------------------------------------------------------------------------------
// VARIABLE OPTION VIEW
// -----------------------------------------------------------------------------------------

fun variableOptionView(entityId : EntityId, context : Context) : LinearLayout
{
    val layout = variableOptionViewLayout(entityId, context)

    layout.addView(variableOptionCheckboxView(entityId, context))

    val mainLayout = variableOptionMainViewLayout(entityId, context)

    // > Header
    mainLayout.addView(variableOptionNameView(entityId, context))

    layout.addView(mainLayout)

    return layout
}


private fun variableOptionViewLayout(entityId : EntityId, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.id                   = R.id.layout

    layout.padding.leftDp       = 10f
    layout.padding.rightDp      = 10f

    layout.padding.topDp        = 10f
    layout.padding.bottomDp     = 10f

//    layout.margin.leftDp        = 2f
//    layout.margin.rightDp       = 2f

    layout.margin.bottomDp      = 1f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
    layout.backgroundColor      = Color.WHITE

    return layout.linearLayout(context)
}


private fun variableOptionCheckboxView(entityId : EntityId, context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------------------

    val layout = LinearLayoutBuilder()
    val icon   = ImageViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------------------

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.layoutGravity        = Gravity.CENTER
    layout.gravity              = Gravity.CENTER

    layout.backgroundResource   = R.drawable.bg_checkbox_unselected

    layout.margin.leftDp        = 5f

    layout.child(icon)

    // (3 A) Icon
    // -------------------------------------------------------------------------------------

    icon.id                     = R.id.icon_view

    icon.widthDp                = 18
    icon.heightDp               = 18

    icon.image                  = R.drawable.icon_check

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    icon.color                  = colorOrBlack(iconColorTheme, entityId)

    icon.visibility             = View.GONE

    return layout.linearLayout(context)
}


private fun variableOptionMainViewLayout(entityId : EntityId, context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

    layout.margin.leftDp        = 22f

    //layout.id                   = R.id.dialog_list_editor_item_layout

//        layout.padding.leftDp       = 10f
//        layout.padding.rightDp      = 10f
//
//        layout.padding.topDp        = 10f
//        layout.padding.bottomDp     = 10f
//
//        layout.margin.leftDp        = 2f
//        layout.margin.rightDp       = 2f
//
//        layout.margin.topDp         = 2f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

    return layout.linearLayout(context)
}


private fun variableOptionNameView(entityId : EntityId, context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------------------

    val layout = LinearLayoutBuilder()
    val icon   = ImageViewBuilder()
    val name   = TextViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------------------

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.gravity              = Gravity.CENTER_VERTICAL

    layout.child(icon)
          .child(name)

    // (3 A) Icon
    // -------------------------------------------------------------------------------------

    //icon.id                     = R.id.choose_value_item_icon

    icon.widthDp                = 16
    icon.heightDp               = 16

    icon.image                  = R.drawable.icon_check

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    icon.color                  = colorOrBlack(iconColorTheme, entityId)

    icon.margin.rightDp         = 4f

    icon.visibility             = View.GONE

    // (3 B) Name
    // -------------------------------------------------------------------------------------

    name.id                     = R.id.name_view

    name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
    name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

    name.font                   = Font.typeface(TextFont.Cabin,
                                                TextFontStyle.Medium,
                                                context)

    name.sizeSp                 = 17.5f

    return layout.linearLayout(context)
}


private fun valueSummaryView(entityId : EntityId, context : Context) : TextView
{
    val summary             = TextViewBuilder()

    summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    summary.id              = R.id.dialog_list_editor_item_summary

    summary.visibility      = View.GONE

    summary.font            = Font.typeface(TextFont.Cabin,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
    summary.color           = colorOrBlack(colorTheme, entityId)

    summary.sizeSp          = 15f

    summary.margin.leftDp   = 0.5f

    return summary.textView(context)
}



// ---------------------------------------------------------------------------------------------
// VARIABLE TOGGLE RECYCLER VIEW ADPATER
// ---------------------------------------------------------------------------------------------

class VariableToggleRecyclerViewAdapter(
                    val variables : List<Variable>,
                    val variableToggleUI : VariableToggleEditorUI,
                    val entityId : EntityId,
                    val context : Context)
                     : RecyclerView.Adapter<VariableEditorOptionViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : VariableEditorOptionViewHolder
    {
        val itemView = variableOptionView(entityId, context)
        return VariableEditorOptionViewHolder(itemView, entityId, context)
    }


    override fun onBindViewHolder(viewHolder : VariableEditorOptionViewHolder, position : Int)
    {
        this.variables.getOrNull(position)?.let { variable ->

            val isSelected = variableToggleUI.selectedVariableIds.contains(variable.variableId())

            viewHolder.setOnClick(View.OnClickListener {

                if (variableToggleUI.selectedVariableIds.contains(variable.variableId()))
                {
                    variableToggleUI.selectedVariableIds.remove(variable.variableId())
                    viewHolder.setUnselected()
                }
                else
                {
                    variableToggleUI.selectedVariableIds.add(variable.variableId())
                    viewHolder.setSelected()
                }
            })

            viewHolder.setName(variable.label().value, isSelected)
            viewHolder.setIcon(isSelected)
        }

    }


    override fun getItemCount() : Int = this.variables.size

}


// ---------------------------------------------------------------------------------------------
// VARIABLE EDITOR OPTION VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class VariableEditorOptionViewHolder(itemView : View,
                                     val entityId: EntityId,
                                     val context : Context)
                                      : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout        : LinearLayout? = null
    var nameView      : TextView?  = null
    var checkIconView : ImageView? = null


    val unselectedNameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    val unselectedNameColor = colorOrBlack(unselectedNameColorTheme, entityId)

    val unselectedSummaryColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
    val unselectedSummaryColor = colorOrBlack(unselectedSummaryColorTheme, entityId)

    val selectedNameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    val selectedNameColor = colorOrBlack(selectedNameColorTheme, entityId)

    val checkSelectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    val checkSelectedColor = colorOrBlack(checkSelectedColorTheme, entityId)

    val checkUnselectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
    val checkUnselectedColor = colorOrBlack(checkUnselectedColorTheme, entityId)


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout        = itemView.findViewById(R.id.layout)
        this.nameView      = itemView.findViewById(R.id.name_view)
        this.checkIconView = itemView.findViewById(R.id.icon_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setName(name : String, isSelected : Boolean)
    {
        this.nameView?.text = name

        if (isSelected) {
            this.nameView?.setTextColor(selectedNameColor)
        }
        else {
            this.nameView?.setTextColor(unselectedNameColor)
        }
    }


    fun setIcon(isSelected : Boolean)
    {
        if (isSelected) {
            this.checkIconView?.visibility = View.VISIBLE
//            this.checkIconView?.colorFilter = PorterDuffColorFilter(checkSelectedColor, PorterDuff.Mode.SRC_IN)
        }
        else {
            this.checkIconView?.visibility = View.GONE
//            this.checkIconView?.colorFilter = PorterDuffColorFilter(checkUnselectedColor, PorterDuff.Mode.SRC_IN)
        }

    }

    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }


    fun setSelected() {
        this.nameView?.setTextColor(selectedNameColor)
        this.checkIconView?.visibility  = View.VISIBLE
//        this.checkIconView?.colorFilter = PorterDuffColorFilter(checkSelectedColor, PorterDuff.Mode.SRC_IN)
    }

    fun setUnselected() {
        this.nameView?.setTextColor(unselectedNameColor)
        this.checkIconView?.visibility  = View.GONE
//        this.checkIconView?.colorFilter = PorterDuffColorFilter(checkUnselectedColor, PorterDuff.Mode.SRC_IN)
    }

}

