
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.engine.value.*
import com.kispoko.tome.model.engine.variable.TextListVariable
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.rts.entity.textListVariable
import com.kispoko.tome.rts.entity.valueSet
import effect.Err
import maybe.Just
import effect.Val
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Subset Editor Dialog
 */
class SubsetEditorDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueSetId    : ValueSetId?       = null
    private var setVariableId : Maybe<VariableId> = Nothing()
    private var currentValues : List<ValueId>     =  listOf()
    private var updateTarget  : UpdateTarget?     = null
    private var entityId      : EntityId?         = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueSetId : ValueSetId,
                        setVariableId : Maybe<VariableId>,
                        currentValues : List<ValueId>,
                        updateTarget : UpdateTarget,
                        entityId : EntityId) : SubsetEditorDialog
        {
            val dialog = SubsetEditorDialog()

            val args = Bundle()
            args.putSerializable("value_set_id", valueSetId)
            args.putSerializable("set_variable_id", setVariableId)
            args.putSerializable("current_values", currentValues as Serializable)
            args.putSerializable("update_target", updateTarget)
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

        this.valueSetId    = arguments?.getSerializable("value_set_id") as ValueSetId
        this.setVariableId = arguments?.getSerializable("set_variable_id") as Maybe<VariableId>
        this.currentValues = arguments?.getSerializable("current_values") as List<ValueId>
        this.updateTarget  = arguments?.getSerializable("update_target") as UpdateTarget
        this.entityId      = arguments?.getSerializable("entity_id") as EntityId

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val width  = context?.resources?.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        width?.let {
            dialog.window.setLayout(width.toInt(), height)
        }

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val entityId = this.entityId
        val valueSetId = this.valueSetId
        val updateTarget = this.updateTarget
        val context = this.context

        if (entityId != null && valueSetId != null && updateTarget != null && context != null)
        {
            val valueSet = valueSet(valueSetId, entityId)

            return when (valueSet) {
                is Val -> {
                    val viewBuilder = ListEditorUI(valueSet.value,
                                                   this.setVariableId,
                                                   this.currentValues,
                                                   updateTarget,
                                                   this,
                                                   entityId,
                                                   context)
                    viewBuilder.view()
                }
                is Err -> {
                    ApplicationLog.error(valueSet.error)
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



class ListEditorUI(val valueSet : ValueSet,
                   val setVariableId : Maybe<VariableId>,
                   val currentValues : List<ValueId>,
                   val updateTarget : UpdateTarget,
                   val dialog : SubsetEditorDialog,
                   val entityId : EntityId,
                   val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var baseAdapter : ListEditorBaseValueSetRecyclerViewAdapter? = null
    var compoundAdapter : ListEditorCompoundValueSetRecyclerViewAdapter? = null


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun valueStrings() : List<String>
    {
        val strings = if (this.baseAdapter != null)
            this.baseAdapter?.selectedValues?.map { it.value }
        else
            this.compoundAdapter?.selectedValues?.map { it.value }

        return strings ?: listOf()
    }


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
        val iconView  = headerIconView()

        mainLayout.addView(titleView)
        mainLayout.addView(iconView)

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

        title.text              = this.valueSet.labelString()

        title.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        title.color             = colorOrBlack(colorTheme, entityId)

        title.sizeSp            = 17f

        title.margin.leftDp     = 0.5f


        return title.textView(context)
    }


    private fun headerIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout  = LinearLayoutBuilder()
        val icon    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType   = LayoutType.RELATIVE
        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.corners      = Corners(3.0, 3.0, 3.0, 3.0)

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
        layout.padding.leftDp   = 20f
        layout.padding.rightDp  = 20f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.onClick = View.OnClickListener {
            when (updateTarget) {
                is UpdateTargetListWidget -> {
                    val valueStrings = if (this.baseAdapter != null)
                                            this.baseAdapter?.selectedValues?.map { it.value }
                                       else
                                            this.compoundAdapter?.selectedValues?.map { it.value }
                    val update = ListWidgetUpdateSetCurrentValue(
                                    updateTarget.listWidgetId,
                                    valueStrings ?: listOf())
                    Router.send(MessageSheetUpdate(update))
                    dialog.dismiss()
                }
                is UpdateTargetTableWidget ->
                {
                    val update = TableWidgetUpdateSubset(
                                        updateTarget.tableWidgetId,
                                        valueStrings())
                    Router.send(MessageSheetUpdate(update))
                    dialog.dismiss()
                }
            }
        }


        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 22
        icon.heightDp       = 22

        icon.image          = R.drawable.icon_check_bold

        icon.color          = Color.WHITE


        return layout.linearLayout(context)
    }


    // List View
    // -----------------------------------------------------------------------------------------

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

//        recyclerView.divider = SimpleDividerItemDecoration(sheetUIContext.context, dividerColor)

        when (valueSet)
        {
            is ValueSetBase ->
            {
                val baseAdapter = ListEditorBaseValueSetRecyclerViewAdapter(
                                                valueSet.sortedValues(),
                                                currentValues,
                                                updateTarget,
                                                dialog,
                                                entityId,
                                                context)
                this.baseAdapter = baseAdapter
                recyclerView.adapter = baseAdapter
            }
            is ValueSetCompound ->
            {
                val valueSets = valueSet.valueSets(entityId)
                when (valueSets)
                {
                    is Val ->
                    {
                        var items : List<Any> = listOf()

                        when (setVariableId)
                        {
                            is Just -> {
                                val setVariable = textListVariable(setVariableId.value, entityId)
                                items = when (setVariable) {
                                    is Val -> {
                                        variableValuesInSet(setVariable.value, valueSet)
                                    }
                                    is Err -> {
                                        ApplicationLog.error(setVariable.error)
                                        valueSetIndexList(valueSets.value, entityId)
                                    }
                                }

                            }
                            is Nothing -> {
                                items = valueSetIndexList(valueSets.value, entityId)
                            }
                        }
                        val compoundAdapter =
                                ListEditorCompoundValueSetRecyclerViewAdapter(
                                                items,
                                                currentValues,
                                                updateTarget,
                                                dialog,
                                                entityId,
                                                context)

                        this.compoundAdapter = compoundAdapter
                        recyclerView.adapter = compoundAdapter
                    }
                    is Err -> ApplicationLog.error(valueSets.error)
                }
            }
        }

        return recyclerView.recyclerView(context)
    }


    private fun valueSetIndexList(valueSets : Set<ValueSet>,
                                  entityId : EntityId) : List<Any>
    {
        fun valueSetItems(valueSet : ValueSet) : List<Any>
        {
            val values = valueSet.values(entityId)
            return when (values) {
                is Val -> listOf(valueSet.label()).plus(values.value)
                is Err -> listOf(valueSet.label())
            }
        }

        return valueSets.sortedBy { it.labelString() }
                        .flatMap(::valueSetItems)
    }


    private fun variableValuesInSet(textListVariable : TextListVariable,
                                    valueSet : ValueSetCompound) : List<Any>
    {
        val valuesBySetId : MutableMap<ValueSetLabel,MutableSet<Value>> = mutableMapOf()

        textListVariable.value(entityId).apDo {
            it.forEach {
                valueSet.valueAndValueSet(ValueId(it), entityId).apDo { (value,valueSet) ->
                    val valueSetLabel = valueSet.label()
                    if (!valuesBySetId.containsKey(valueSetLabel))
                        valuesBySetId[valueSetLabel] = mutableSetOf()
                    valuesBySetId[valueSetLabel]?.add(value)
                }
            }
        }

        val valuesList : MutableList<Any> = mutableListOf()

        valuesBySetId.toSortedMap().entries.forEach {
            valuesList.add(it.key)
            it.value.sortedBy { it.valueString() }.forEach {
                valuesList.add(it)
            }
        }

        return valuesList
    }


    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

//        layout.margin.topDp     = 5f
//        layout.margin.bottomDp  = 5f

        return layout.linearLayout(context)
    }


}


object ListEditor
{

    // -----------------------------------------------------------------------------------------
    // VALUE VIEW
    // -----------------------------------------------------------------------------------------

    fun valueView(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = this.valueViewLayout(entityId, context)

        layout.addView(this.checkboxView(entityId, context))

        val mainLayout = this.valueMainViewLayout(entityId, context)

        // > Header
        mainLayout.addView(this.valueHeaderView(entityId, context))

        // Summary
        mainLayout.addView(this.valueSummaryView(entityId, context))

        // Reference Link
        mainLayout.addView(this.referenceView(entityId, context))

        layout.addView(mainLayout)

        return layout
    }


    private fun valueViewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.id                   = R.id.dialog_list_editor_item_layout

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.margin.leftDp        = 2f
        layout.margin.rightDp       = 2f

        layout.margin.topDp         = 1f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        layout.backgroundColor      = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun checkboxView(entityId : EntityId, context : Context) : LinearLayout
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
        layout.gravity               = Gravity.CENTER

        layout.backgroundResource   = R.drawable.bg_checkbox_unselected

        layout.margin.leftDp       = 5f
        layout.margin.rightDp       = 15f

        layout.child(icon)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.id                     = R.id.dialog_list_editor_checkbox_icon

        icon.widthDp                = 18
        icon.heightDp               = 18

        icon.image                  = R.drawable.icon_check

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        icon.color                  = colorOrBlack(iconColorTheme, entityId)

        //icon.visibility             = View.GONE

        return layout.linearLayout(context)
    }


    private fun valueMainViewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

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


    private fun valueHeaderView(entityId : EntityId, context : Context) : LinearLayout
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        icon.color                  = colorOrBlack(iconColorTheme, entityId)

        icon.margin.rightDp         = 4f

        icon.visibility             = View.GONE

        // (3 B) Name
        // -------------------------------------------------------------------------------------

        name.id                     = R.id.dialog_list_editor_item_value

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font                   = Font.typeface(TextFont.Cabin,
                                                    TextFontStyle.Medium,
                                                    context)

        name.sizeSp                 = 17f

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


    private fun referenceView(entityId : EntityId, context : Context) : LinearLayout

    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val icon   = ImageViewBuilder()
        val label  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.id               = R.id.choose_value_item_reference

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.topDp     = 8f
        layout.margin.leftDp    = 2f

        layout.visibility       = View.GONE

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 20
        icon.heightDp           = 20

        icon.image              = R.drawable.icon_open_book

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color           = colorOrBlack(iconColorTheme, entityId)

        icon.margin.rightDp     = 10f
        icon.padding.topDp      = 1f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.read_about_in_rulebook

        label.font              = Font.typeface(TextFont.Cabin,
                                                TextFontStyle.Regular,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color             = colorOrBlack(labelColorTheme, entityId)

        label.sizeSp            = 16f

        return layout.linearLayout(context)
    }


    fun valueSetNameView(entityId : EntityId, context : Context) : TextView
    {
        val name            = TextViewBuilder()

        name.id             = R.id.dialog_list_editor_header

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font           = Font.typeface(TextFont.Cabin,
                                            TextFontStyle.Regular,
                                            context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.sizeSp             = 16f

        name.margin.leftDp      = 10f
        name.margin.rightDp     = 10f

        name.margin.topDp       = 6f
        name.margin.bottomDp    = 6f

        return name.textView(context)
    }


}



class ListEditorBaseValueSetRecyclerViewAdapter(
                    val values : List<Value>,
                    val currentValues : List<ValueId>,
                    val updateTarget : UpdateTarget,
                    val dialog : DialogFragment,
                    val entityId : EntityId,
                    val context : Context)
                     : RecyclerView.Adapter<ListEditorValueViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val selectedValues : MutableList<ValueId> = mutableListOf()

    init {
        currentValues.forEach { selectedValues.add(it) }
    }


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ListEditorValueViewHolder
    {
        val itemView = ListEditor.valueView(entityId, context)
        return ListEditorValueViewHolder(itemView, entityId, context)
    }


    override fun onBindViewHolder(viewHolder : ListEditorValueViewHolder, position : Int)
    {
        val value = this.values[position]

        when (value)
        {
            is ValueText ->
            {
                val isSelected = selectedValues.contains(value.valueId)
                viewHolder.setValueText(value.value(), isSelected)

                viewHolder.setSummaryText(value.description().value)

                viewHolder.setIcon(isSelected)

                viewHolder.setOnClick(View.OnClickListener {
                    if (selectedValues.contains(value.valueId)) {
                        // DESELECT
                        viewHolder.setUnselected()
                        this.selectedValues.remove(value.valueId)
                    } else {
                        // SELECT
                        viewHolder.setSelected()
                        this.selectedValues.add(value.valueId)
                    }
                })
            }
            is ValueNumber ->
            {
                //viewHolder.setValueText(value.value().toString())
            }
        }
    }


    override fun getItemCount() : Int = this.values.size

}


// -----------------------------------------------------------------------------------------
// COMPOUND VALUE SET ADPATER
// -----------------------------------------------------------------------------------------

class ListEditorCompoundValueSetRecyclerViewAdapter(
                    val items : List<Any>,
                    val currentValues : List<ValueId>,
                    val updateTarget : UpdateTarget,
                    val dialog : DialogFragment,
                    val entityId : EntityId,
                    val context : Context)
                     : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private val HEADER = 0
    private val VALUE  = 1


    val selectedValues : MutableList<ValueId> = mutableListOf()

    init {
        currentValues.forEach { selectedValues.add(it) }
    }


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
        val itemAtPosition = this.items[position]

        return when (itemAtPosition) {
            is ValueSetLabel -> HEADER
            is Value -> VALUE
            else -> -1
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder =
        when (viewType)
        {
            HEADER ->
            {
                val headerView = ListEditor.valueSetNameView(entityId, context)
                ListEditorHeaderViewHolder(headerView)
            }
            // VALUE
            else ->
            {
                val valueView = ListEditor.valueView(entityId, context)
                ListEditorValueViewHolder(valueView, entityId, context)
            }
        }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val item = this.items[position]

        if (item is Value)
        {
            val valueViewHolder = viewHolder as ListEditorValueViewHolder

            when (item)
            {
                is ValueText ->
                {
                    val isSelected = selectedValues.contains(item.valueId)
                    valueViewHolder.setValueText(item.value(), isSelected)

                    valueViewHolder.setSummaryText(item.description().value)

                    valueViewHolder.setIcon(isSelected)


                    valueViewHolder.setOnClick(View.OnClickListener {
                        if (selectedValues.contains(item.valueId)) {
                            // DESELECT
                            viewHolder.setUnselected()
                            this.selectedValues.remove(item.valueId)
                        } else {
                            // SELECT
                            viewHolder.setSelected()
                            this.selectedValues.add(item.valueId)
                        }
                    })
                }
                is ValueNumber ->
                {
//                    valueViewHolder.setValueText(item.value().toString())
                }
            }
        }
        else if (item is ValueSetLabel)
        {
            val headerViewHolder = viewHolder as ListEditorHeaderViewHolder
            headerViewHolder.setHeaderText(item.value)
        }
    }


    override fun getItemCount() = this.items.size

}


// ---------------------------------------------------------------------------------------------
// =============================================================================================
// List Editor Value View Holder
// =============================================================================================
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class ListEditorValueViewHolder(itemView : View,
                                val entityId: EntityId,
                                val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout? = null
    var valueView   : TextView?  = null
    var summaryView : TextView?  = null
    var checkIconView : ImageView? = null
//    var refView     : LinearLayout? = null


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

//    val unselectedBgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//    val unselectedBgColor = SheetManager.color(sheetUIContext.sheetId, selectedBgColorTheme)

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
        this.layout      = itemView.findViewById(R.id.dialog_list_editor_item_layout)
        this.valueView   = itemView.findViewById(R.id.dialog_list_editor_item_value)
        this.summaryView = itemView.findViewById(R.id.dialog_list_editor_item_summary)
        this.checkIconView  = itemView.findViewById(R.id.dialog_list_editor_checkbox_icon)
//        this.refView     = itemView.findViewById(R.id.choose_value_item_reference) as LinearLayout
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setValueText(valueString : String, isSelected : Boolean)
    {
        this.valueView?.text = valueString

        if (isSelected) {
            this.valueView?.setTextColor(selectedNameColor)
        }
        else {
            this.valueView?.setTextColor(unselectedNameColor)
        }
    }


    fun setSummaryText(summaryString : String)
    {
        if (summaryString.isNotBlank())
        {
            this.summaryView?.visibility = View.VISIBLE
            this.summaryView?.text = summaryString
        }
    }


    fun setIcon(isSelected : Boolean)
    {
        if (isSelected) {
            this.checkIconView?.setColorFilter(PorterDuffColorFilter(checkSelectedColor, PorterDuff.Mode.SRC_IN))
        }
        else {
            this.checkIconView?.setColorFilter(PorterDuffColorFilter(checkUnselectedColor, PorterDuff.Mode.SRC_IN))
        }

    }

    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }


    fun setSelected() {
        this.valueView?.setTextColor(selectedNameColor)
        this.checkIconView?.setColorFilter(PorterDuffColorFilter(checkSelectedColor, PorterDuff.Mode.SRC_IN))
    }

    fun setUnselected() {
        this.valueView?.setTextColor(unselectedNameColor)
        this.checkIconView?.setColorFilter(PorterDuffColorFilter(checkUnselectedColor, PorterDuff.Mode.SRC_IN))
    }

}




// ---------------------------------------------------------------------------------------------
// HEADER VIEW HOLDER
// ---------------------------------------------------------------------------------------------

class ListEditorHeaderViewHolder(val headerView : View) : RecyclerView.ViewHolder(headerView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var headerTextView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.headerTextView = headerView.findViewById(R.id.dialog_list_editor_header)
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun setHeaderText(headerString : String)
    {
        this.headerTextView?.text = headerString
    }

}
