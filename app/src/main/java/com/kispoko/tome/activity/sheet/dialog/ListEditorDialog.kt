
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.game.GameManager
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.rts.entity.valueSet
import effect.Err
import maybe.Just
import effect.Val
import java.io.Serializable



/**
 * List Editor Dialog
 */
class ListEditorDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueSetId    : ValueSetId? = null
    private var currentValues : List<ValueId> = listOf()
    private var updateTarget  : UpdateTarget? = null
    private var entityId      : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueSetId : ValueSetId,
                        currentValues : List<ValueId>,
                        updateTarget : UpdateTarget,
                        entityId : EntityId) : ListEditorDialog
        {
            val dialog = ListEditorDialog()

            val args = Bundle()
            args.putSerializable("value_set_id", valueSetId)
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

        this.valueSetId    = arguments.getSerializable("value_set_id") as ValueSetId
        this.currentValues = arguments.getSerializable("current_values") as List<ValueId>
        this.updateTarget  = arguments.getSerializable("update_target") as UpdateTarget
        this.entityId      = arguments.getSerializable("entity_id") as EntityId

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
        val entityId = this.entityId
        val valueSetId = this.valueSetId
        val updateTarget = this.updateTarget

        if (entityId != null && valueSetId != null && updateTarget != null)
        {
            val valueSet = valueSet(valueSetId, entityId)

            return when (valueSet) {
                is Val -> {
                    val viewBuilder = ListEditorViewBuilder(valueSet.value,
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



class ListEditorViewBuilder(val valueSet : ValueSet,
                            val currentValues : List<ValueId>,
                            val updateTarget : UpdateTarget,
                            val dialog : ListEditorDialog,
                            val entityId : EntityId,
                            val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    var adapter : ListEditorBaseValueSetRecyclerViewAdapter? = null


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


        // > Toggle Menu Functionality
        // ----------------------------------------------------------------------------

        val closeIcon = ContextCompat.getDrawable(context,
                                                  R.drawable.ic_dialog_chooser_close_menu)

        val menuIcon = ContextCompat.getDrawable(context,
                                                 R.drawable.ic_dialog_chooser_menu)

//        iconView.setOnClickListener {
//            // Show MENU
//            if (chooserView.visibility == View.VISIBLE)
//            {
//                chooserView.visibility = View.GONE
//                menuView.visibility = View.VISIBLE
//
//                iconView.setImageDrawable(closeIcon)
//
//                titleView.setText(R.string.options)
//            }
//            // Show VALUES
//            else
//            {
//                chooserView.visibility = View.VISIBLE
//                menuView.visibility = View.GONE
//
//                iconView.setImageDrawable(menuIcon)
//            }
//        }

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

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

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f
        layout.padding.topDp        = 4f
        layout.padding.bottomDp     = 4f

//        layout.corners              = Corners(3.0, 3.0, 0.0, 0.0)

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
                                                TextFontStyle.Bold,
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
                    val valueStrings = this.adapter?.selectedValues?.map { it.value }
                    val update = ListWidgetUpdateSetCurrentValue(
                                    updateTarget.listWidgetId,
                                    valueStrings ?: listOf())
//                    SheetManager.updateSheet(sheetUIContext.sheetId,
//                                            update,
//                                            sheetUIContext.sheetUI())
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

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        //icon.color          = SheetManager.color(sheetUIContext.sheetId, colorTheme)
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

//        val dividerColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        val dividerColor = SheetManager.color(sheetUIContext.sheetId, dividerColorTheme)
//        recyclerView.divider = SimpleDividerItemDecoration(sheetUIContext.context, dividerColor)

        when (valueSet)
        {
            is ValueSetBase ->
            {
                val adapter = ListEditorBaseValueSetRecyclerViewAdapter(
                                                valueSet.sortedValues(),
                                                currentValues,
                                                updateTarget,
                                                dialog,
                                                entityId,
                                                context)
                this.adapter = adapter
                recyclerView.adapter = adapter
            }
            is ValueSetCompound ->
            {
                val valueSets = valueSet.valueSets(entityId)
                when (valueSets)
                {
                    is Val ->
                    {
                        val items = valueSetIndexList(valueSets.value, entityId, context)
                        //Log.d("***VALUECHOOSER", items.toString())
                        recyclerView.adapter =
                                ListEditorCompoundValueSetRecyclerViewAdapter(
                                        items,
                                        currentValues,
                                        updateTarget,
                                        dialog,
                                        entityId,
                                        context)
                    }
                    is Err -> ApplicationLog.error(valueSets.error)
                }
            }
        }

        return recyclerView.recyclerView(context)
    }


    private fun valueSetIndexList(valueSets : Set<ValueSet>,
                                  entityId : EntityId,
                                  context : Context) : List<Any>
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
        name.color          = colorOrBlack(colorTheme, entityId)

        name.sizeSp         = 12f

        name.margin.leftDp  = 10f
        name.margin.rightDp = 10f

        name.margin.topDp   = 10f
        name.margin.bottomDp = 10f

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

                viewHolder.setSummaryText(value.description().value, isSelected)

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

        val rulebookRef = value.rulebookReference()
        when (rulebookRef) {
            is Just<*> ->
            {
//                viewHolder.setReferenceView(View.OnClickListener {
//                    val sheetActivity = sheetUIContext.context as SheetActivity
//                    val dialog = RulebookExcerptDialog.newInstance(rulebookRef.value,
//                                                                   SheetContext(sheetUIContext))
//                    dialog.show(sheetActivity.supportFragmentManager, "")
//                })
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
                HeaderViewHolder(headerView)
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
            val valueViewHolder = viewHolder as ValueViewHolder

            when (item)
            {
                is ValueText ->
                {
//                    if (item.equals(this.selectedValue))
//                        valueViewHolder.setValueTextSelected(item.value())
//                    else
//                        valueViewHolder.setValueText(item.value())

                    valueViewHolder.setSummaryText(item.description().value)

                    viewHolder.setOnClick(View.OnClickListener {
                        when (updateTarget) {
                            is UpdateTargetStoryWidgetPart -> {
                                val textValuePartUpdate =
                                        StoryWidgetUpdateTextValuePart(
                                                updateTarget.storyWidgetId,
                                                updateTarget.partIndex,
                                                item.valueId()
                                        )
//                                SheetManager.updateSheet(sheetUIContext.sheetId,
//                                        textValuePartUpdate,
//                                        sheetUIContext.sheetUI())
                                dialog.dismiss()
                            }
                            is UpdateTargetTextCell -> {
                                val update = TableWidgetUpdateSetTextCellValue(updateTarget.tableWidgetId,
                                        updateTarget.cellId,
                                        item.valueId())
//                                SheetManager.updateSheet(sheetUIContext.sheetId,
//                                                         update,
//                                                         sheetUIContext.sheetUI())
                                dialog.dismiss()
                            }
                        }
                    })
                }
                is ValueNumber ->
                {
                    valueViewHolder.setValueText(item.value().toString())
                }
            }
        }
        else if (item is ValueSetLabel)
        {
            val headerViewHolder = viewHolder as ListEditorHeaderViewHolder
            headerViewHolder.setHeaderText(item.value.toUpperCase())
        }
    }


    override fun getItemCount() = this.items.size

}


// ---------------------------------------------------------------------------------------------
// VALUE VIEW HOLDER
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
        this.layout      = itemView.findViewById(R.id.dialog_list_editor_item_layout) as LinearLayout
        this.valueView   = itemView.findViewById(R.id.dialog_list_editor_item_value) as TextView
        this.summaryView = itemView.findViewById(R.id.dialog_list_editor_item_summary) as TextView
        this.checkIconView  = itemView.findViewById(R.id.dialog_list_editor_checkbox_icon) as ImageView
//        this.refView     = itemView.findViewById(R.id.choose_value_item_reference) as LinearLayout
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setValueText(valueString : String, isSelected : Boolean)
    {
        this.valueView?.text = valueString

        if (isSelected) {
            //this.layout?.setBackgroundColor(selectedBgColor)
            this.valueView?.setTextColor(selectedNameColor)
        }
        else {
            this.valueView?.setTextColor(unselectedNameColor)
        }
    }


    fun setSummaryText(summaryString : String, isSelected : Boolean)
    {
        this.summaryView?.text = summaryString

//        if (isSelected) {
//            this.summaryView?.setTextColor(Color.WHITE)
//        }
//        else {
//            this.summaryView?.setTextColor(unselectedSummaryColor)
//        }
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


//    fun setReferenceView(onClickListener : View.OnClickListener)
//    {
//        this.refView?.visibility = View.VISIBLE
//        this.refView?.setOnClickListener(onClickListener)
//    }
//
//

    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }


    fun setSelected() {
     //   this.layout?.setBackgroundColor(selectedBgColor)
        this.valueView?.setTextColor(selectedNameColor)
        this.checkIconView?.setColorFilter(PorterDuffColorFilter(checkSelectedColor, PorterDuff.Mode.SRC_IN))
        //this.summaryView?.setTextColor(Color.WHITE)
    }

    fun setUnselected() {
    //    this.layout?.setBackgroundColor(unselectedBgColor)
        this.valueView?.setTextColor(unselectedNameColor)
        this.checkIconView?.setColorFilter(PorterDuffColorFilter(checkUnselectedColor, PorterDuff.Mode.SRC_IN))
        //this.summaryView?.setTextColor(unselectedSummaryColor)
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
        this.headerTextView = headerView.findViewById(R.id.dialog_list_editor_header) as TextView
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun setHeaderText(headerString : String)
    {
        this.headerTextView?.text = headerString
    }

}

