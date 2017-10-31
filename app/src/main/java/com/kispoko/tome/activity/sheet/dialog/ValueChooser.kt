
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.SimpleDividerItemDecoration
import effect.Err
import effect.Just
import effect.Val



/**
 * Value Choose Dialog Fragment
 */
class ValueChooserDialogFragment : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueSet      : ValueSet? = null
    private var selectedValue : Value? = null
    private var updateTarget  : UpdateTarget? = null
    private var sheetContext  : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueSet : ValueSet,
                        selectedValue : Value?,
                        updateTarget : UpdateTarget,
                        sheetContext: SheetContext) : ValueChooserDialogFragment
        {
            val dialog = ValueChooserDialogFragment()

            val args = Bundle()
            args.putSerializable("value_set", valueSet)
            args.putSerializable("selected_value", selectedValue)
            args.putSerializable("update_target", updateTarget)
            args.putSerializable("sheet_context", sheetContext)
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

        this.valueSet      = arguments.getSerializable("value_set") as ValueSet
        this.selectedValue = arguments.getSerializable("selected_value") as Value?
        this.updateTarget  = arguments.getSerializable("update_target") as UpdateTarget
        this.sheetContext  = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val dialogLayout = this.dialogLayout()

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setContentView(dialogLayout)

            val width  = context.resources.getDimension(R.dimen.action_dialog_width)
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            dialog.window.setLayout(width.toInt(), height)
        }

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val valueSet      = this.valueSet
            val selectedValue = this.selectedValue
            val updateTarget  = this.updateTarget

            if (valueSet != null && updateTarget != null) {
                return ValueChooserView.view(valueSet,
                                             selectedValue,
                                             updateTarget,
                                             sheetUIContext,
                                             this)
            }
            else {
                return super.onCreateView(inflater, container, savedInstanceState)
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


object ValueChooserView
{


    fun view(valueSet : ValueSet,
             selectedValue : Value?,
             updateTarget : UpdateTarget,
             sheetUIContext : SheetUIContext,
             dialog : DialogFragment) : View
    {
        val layout = viewLayout(sheetUIContext)

        // (1) Views
        // -------------------------------------------------------------------
        val chooserView     = chooserView(valueSet,
                                          selectedValue,
                                          updateTarget,
                                          sheetUIContext,
                                          dialog)
        val optionsMenuView = optionsMenuView(sheetUIContext.context)
        val headerView      = headerView(valueSet.labelString(), chooserView,
                optionsMenuView, sheetUIContext)

        // (2) Initialize
        // -------------------------------------------------------------------

        // > Hide menu by default
        optionsMenuView.visibility = View.GONE

        // (3) Add Views
        // -------------------------------------------------------------------

        // > Header
        layout.addView(headerView)

        // > Chooser
        layout.addView(chooserView)

        // > Options Menu
        layout.addView(optionsMenuView)

        return layout
    }


    private fun viewLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    //    layout.backgroundResource   = R.drawable.bg_dialog

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(TopLeftCornerRadius(3f),
                                              TopRightCornerRadius(3f),
                                              BottomRightCornerRadius(3f),
                                              BottomLeftCornerRadius(3f))

        return layout.linearLayout(sheetUIContext.context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    fun headerView(title : String,
                   chooserView : View,
                   menuView : View,
                   sheetUIContext: SheetUIContext) : RelativeLayout
    {
        val layout = headerViewLayout(sheetUIContext)

        val titleView = headerTitleView(title, sheetUIContext)
        val iconView  = headerIconView(sheetUIContext)

        layout.addView(titleView)
        layout.addView(iconView)

        // > Toggle Menu Functionality
        // ----------------------------------------------------------------------------

        val closeIcon = ContextCompat.getDrawable(sheetUIContext.context,
                                                  R.drawable.ic_dialog_chooser_close_menu)

        val menuIcon = ContextCompat.getDrawable(sheetUIContext.context,
                                                 R.drawable.ic_dialog_chooser_menu)

        iconView.setOnClickListener {
            // Show MENU
            if (chooserView.visibility == View.VISIBLE)
            {
                chooserView.visibility = View.GONE
                menuView.visibility = View.VISIBLE

                iconView.setImageDrawable(closeIcon)

                titleView.setText(R.string.options)
            }
            // Show VALUES
            else
            {
                chooserView.visibility = View.VISIBLE
                menuView.visibility = View.GONE

                iconView.setImageDrawable(menuIcon)

            //    titleView.setText(title)
            }
        }

//        iconView.setOnClickListener(View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//
//            }
//        })

        return layout
    }


    private fun headerViewLayout(sheetUIContext: SheetUIContext) : RelativeLayout
    {
        val layout = RelativeLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f
        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        layout.margin.bottomDp      = 1f

        layout.corners              = Corners(TopLeftCornerRadius(3f),
                                                TopRightCornerRadius(3f),
                                                BottomRightCornerRadius(0f),
                                                BottomLeftCornerRadius(0f))

        return layout.relativeLayout(sheetUIContext.context)
    }


    private fun headerTitleView(titleString : String, sheetUIContext: SheetUIContext) : TextView
    {
        val title               = TextViewBuilder()

        title.layoutType        = LayoutType.RELATIVE
        title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        title.text              = titleString

        title.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        title.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        title.sizeSp            = 15f

        title.addRule(RelativeLayout.ALIGN_PARENT_START)
        title.addRule(RelativeLayout.CENTER_VERTICAL)

        return title.textView(sheetUIContext.context)
    }


    private fun headerIconView(sheetUIContext: SheetUIContext) : ImageView
    {
        val icon = ImageViewBuilder()

        icon.layoutType     = LayoutType.RELATIVE
        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = R.drawable.ic_dialog_chooser_menu

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        icon.addRule(RelativeLayout.ALIGN_PARENT_END)
        icon.addRule(RelativeLayout.CENTER_VERTICAL)

        return icon.imageView(sheetUIContext.context)
    }


    // List View
    // -----------------------------------------------------------------------------------------

    fun chooserView(valueSet : ValueSet,
                    selectedValue : Value?,
                    updateTarget : UpdateTarget,
                    sheetUIContext : SheetUIContext,
                    dialog : DialogFragment) : RecyclerView
    {
        val recyclerView            = RecyclerViewBuilder()

        recyclerView.width          = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height         = R.dimen.dialog_choose_value_list_height

        recyclerView.layoutManager  = LinearLayoutManager(sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        recyclerView.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        val dividerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val dividerColor = SheetManager.color(sheetUIContext.sheetId, dividerColorTheme)
        recyclerView.divider = SimpleDividerItemDecoration(sheetUIContext.context, dividerColor)

        when (valueSet)
        {
            is ValueSetBase ->
            {
                recyclerView.adapter = BaseValueSetRecyclerViewAdapter(valueSet.sortedValues(),
                                                                       selectedValue,
                                                                       updateTarget,
                                                                       sheetUIContext,
                                                                       dialog)
            }
            is ValueSetCompound ->
            {
                val valueSets = valueSet.valueSets(sheetUIContext)
                when (valueSets)
                {
                    is Val ->
                    {
                        val items = valueSetIndexList(valueSets.value, sheetUIContext)
                        //Log.d("***VALUECHOOSER", items.toString())
                        recyclerView.adapter =
                                CompoundValueSetRecyclerViewAdapter(items,
                                        selectedValue,
                                        updateTarget,
                                        sheetUIContext,
                                        dialog)
                    }
                    is Err -> ApplicationLog.error(valueSets.error)
                }
            }
        }

        return recyclerView.recyclerView(sheetUIContext.context)
    }


    private fun valueSetIndexList(valueSets : Set<ValueSet>,
                                  sheetUIContext : SheetUIContext) : List<Any>
    {
        fun valueSetItems(valueSet : ValueSet) : List<Any>
        {
            val values = valueSet.values(sheetUIContext.gameId)
            when (values) {
                is Val -> return listOf(valueSet.label()).plus(values.value)
                is Err -> return listOf(valueSet.label())
            }
        }

        return valueSets.sortedBy { it.labelString() }
                        .flatMap(::valueSetItems)
    }


    // -----------------------------------------------------------------------------------------
    // VALUE VIEW
    // -----------------------------------------------------------------------------------------

    fun valueView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.valueViewLayout(sheetUIContext)

        // Header
        layout.addView(this.valueHeaderView(sheetUIContext))

        // Summary
        layout.addView(this.valueSummaryView(sheetUIContext))

        // Reference Link
        layout.addView(this.referenceView(sheetUIContext))

        return layout
    }


    private fun valueViewLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.id                   = R.id.choose_value_item_layout

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp       = 6f
        layout.padding.rightDp      = 7f

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.margin.leftDp        = 4f
        layout.margin.rightDp       = 4f

        layout.margin.topDp         = 3f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun valueHeaderView(sheetUIContext: SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val icon   = ImageViewBuilder()
        val name   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.child(icon)
              .child(name)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.id                     = R.id.choose_value_item_icon

        icon.widthDp                = 16
        icon.heightDp               = 16

        icon.image                  = R.drawable.icon_check

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp         = 4f

        icon.visibility             = View.GONE

        // (3 B) Name
        // -------------------------------------------------------------------------------------

        name.id                     = R.id.choose_value_item_value

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font                   = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        name.sizeSp                 = 15f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun valueSummaryView(sheetUIContext: SheetUIContext) : TextView
    {
        val summary             = TextViewBuilder()

        summary.width           = LinearLayout.LayoutParams.MATCH_PARENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.id              = R.id.choose_value_item_summary

        summary.font            = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        summary.color           = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        summary.sizeSp          = 13f

        summary.margin.leftDp   = 0.5f

        return summary.textView(sheetUIContext.context)
    }


    private fun referenceView(sheetUIContext : SheetUIContext) : LinearLayout

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
        icon.color           = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp     = 10f
        icon.padding.topDp      = 1f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.read_about_in_rulebook

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.sizeSp            = 16f

        return layout.linearLayout(sheetUIContext.context)
    }


    fun valueSetNameView(sheetUIContext : SheetUIContext) : TextView
    {
        val name            = TextViewBuilder()

        name.id             = R.id.choose_value_header

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font           = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color          = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.sizeSp         = 12f

        name.margin.leftDp  = 10f
        name.margin.rightDp = 10f

        name.margin.topDp   = 10f
        name.margin.bottomDp = 10f

        return name.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // MENU VIEW
    // -----------------------------------------------------------------------------------------


    private fun optionsMenuView(context : Context) : LinearLayout
    {
        val layout = optionsMenuViewLayout(context)

        // Sort Asc Button
        val sortAscButtonView =
                optionsMenuButtonView(R.string.sort_values_ascending,
                        R.drawable.ic_dialog_chooser_sort_asc,
                        context)
        layout.addView(sortAscButtonView)

        // Sort Desc Button
        val sortDescButtonView =
                optionsMenuButtonView(R.string.sort_values_descending,
                        R.drawable.ic_dialog_chooser_sort_desc,
                        context)
        layout.addView(sortDescButtonView)

        // --- Divider
        layout.addView(optionsMenuDividerView(context))

        // Edit Values
        val editValuesButton =
                optionsMenuButtonView(R.string.edit_values,
                        R.drawable.ic_dialog_chooser_edit_values,
                        context)
        layout.addView(editValuesButton)

        // --- Divider
        layout.addView(optionsMenuDividerView(context))

        // Edit Values
        val styleWidgetButton =
                optionsMenuButtonView(R.string.style_widget,
                        R.drawable.ic_dialog_chooser_style_widget,
                        context)
        layout.addView(styleWidgetButton)

        // Edit Widget
        val editWidgetButton =
                optionsMenuButtonView(R.string.edit_widget,
                        R.drawable.ic_dialog_chooser_widget,
                        context)
        layout.addView(editWidgetButton)

        return layout
    }


    private fun optionsMenuViewLayout(context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = R.dimen.dialog_choose_value_list_height

        layout.backgroundColor      = R.color.dark_blue_7
        layout.backgroundResource   = R.drawable.bg_dialog_list_widget_chooser

        layout.padding.leftDp       = 13f
        layout.padding.rightDp      = 13f
        layout.padding.topDp        = 10f

        return layout.linearLayout(context)
    }


    private fun optionsMenuButtonView(labelId : Int,
                                      iconId : Int,
                                      context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val icon   = ImageViewBuilder()
        val label  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.topDp     = 14f
        layout.margin.bottomDp  = 14f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        icon.image              = iconId

        icon.color              = R.color.dark_blue_hl_2

        icon.margin.rightDp     = 10f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)
        label.color             = R.color.dark_blue_hlx_10
        label.sizeSp            = 17f


        return layout.linearLayout(context)
    }


    private fun optionsMenuDividerView(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        layout.backgroundColor  = R.color.dark_blue_4

        return layout.linearLayout(context)
    }


}


class BaseValueSetRecyclerViewAdapter(val values : List<Value>,
                                      val selectedValue : Value?,
                                      val updateTarget : UpdateTarget,
                                      val sheetUIContext: SheetUIContext,
                                      val dialog : DialogFragment)
                : RecyclerView.Adapter<ValueViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ValueViewHolder
    {
        val itemView = ValueChooserView.valueView(sheetUIContext)
        return ValueViewHolder(itemView, sheetUIContext)
    }


    override fun onBindViewHolder(viewHolder : ValueViewHolder, position : Int)
    {
        val value = this.values[position]

        when (value)
        {
            is ValueText ->
            {
                if (value.equals(selectedValue))
                    viewHolder.setValueTextSelected(value.value())
                else
                    viewHolder.setValueText(value.value())

                val description = value.description()
                if (description != null)
                    viewHolder.setSummaryText(description)

                viewHolder.setOnClick(View.OnClickListener {
                    when (updateTarget) {
                        is UpdateTargetStoryWidgetPart -> {
                            val textValuePartUpdate =
                                StoryWidgetUpdateTextValuePart(
                                        updateTarget.storyWidgetId,
                                        updateTarget.partIndex,
                                        value.valueId()
                                        )
                            SheetManager.updateSheet(sheetUIContext.sheetId, textValuePartUpdate)
                            dialog.dismiss()
                        }
                        is UpdateTargetTextCell -> {
                            val update = TableWidgetUpdateSetTextCellValue(updateTarget.tableWidgetId,
                                                                           updateTarget.cellId,
                                                                           value.valueId())
                            SheetManager.updateSheet(sheetUIContext.sheetId, update)
                            dialog.dismiss()
                        }
                    }
                })
            }
            is ValueNumber ->
            {
                viewHolder.setValueText(value.value().toString())
            }
        }

        val rulebookRef = value.rulebookReference()
        when (rulebookRef) {
            is Just ->
            {
                viewHolder.setReferenceView(View.OnClickListener {
                    val sheetActivity = sheetUIContext.context as SheetActivity
                    val dialog = RulebookExcerptDialog.newInstance(rulebookRef.value,
                                                                   SheetContext(sheetUIContext))
                    dialog.show(sheetActivity.supportFragmentManager, "")
                })
            }
        }
    }


    override fun getItemCount() : Int = this.values.size

}


// -----------------------------------------------------------------------------------------
// COMPOUND VALUE SET ADPATER
// -----------------------------------------------------------------------------------------

class CompoundValueSetRecyclerViewAdapter(val items : List<Any>,
                                          val selectedValue : Value?,
                                          val updateTarget : UpdateTarget,
                                          val sheetUIContext : SheetUIContext,
                                          val dialog : DialogFragment)
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

        if (itemAtPosition is ValueSetLabel)
            return HEADER
        else if (itemAtPosition is Value)
            return VALUE
        else
            return -1
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder =
        when (viewType)
        {
            HEADER ->
            {
                val headerView = ValueChooserView.valueSetNameView(sheetUIContext)
                HeaderViewHolder(headerView)
            }
            // VALUE
            else ->
            {
                val valueView = ValueChooserView.valueView(sheetUIContext)
                ValueViewHolder(valueView, sheetUIContext)
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
                    if (item.equals(this.selectedValue))
                        valueViewHolder.setValueTextSelected(item.value())
                    else
                        valueViewHolder.setValueText(item.value())

                    val description = item.description()
                    if (description != null)
                        valueViewHolder.setSummaryText(description)

                    viewHolder.setOnClick(View.OnClickListener {
                        when (updateTarget) {
                            is UpdateTargetStoryWidgetPart -> {
                                val textValuePartUpdate =
                                        StoryWidgetUpdateTextValuePart(
                                                updateTarget.storyWidgetId,
                                                updateTarget.partIndex,
                                                item.valueId()
                                        )
                                SheetManager.updateSheet(sheetUIContext.sheetId, textValuePartUpdate)
                                dialog.dismiss()
                            }
                            is UpdateTargetTextCell -> {
                                val update = TableWidgetUpdateSetTextCellValue(updateTarget.tableWidgetId,
                                        updateTarget.cellId,
                                        item.valueId())
                                SheetManager.updateSheet(sheetUIContext.sheetId, update)
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
            val headerViewHolder = viewHolder as HeaderViewHolder
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
class ValueViewHolder(itemView : View, val sheetUIContext: SheetUIContext)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout? = null
    var valueView   : TextView?  = null
    var summaryView : TextView?  = null
    var iconView    : ImageView? = null
    var refView     : LinearLayout? = null


    val normalColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    val greyColor = SheetManager.color(sheetUIContext.sheetId, normalColorTheme)


    val hlColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
    val greenColor = SheetManager.color(sheetUIContext.sheetId, hlColorTheme)

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.choose_value_item_layout) as LinearLayout
        this.valueView   = itemView.findViewById(R.id.choose_value_item_value) as TextView
        this.summaryView = itemView.findViewById(R.id.choose_value_item_summary) as TextView
        this.iconView    = itemView.findViewById(R.id.choose_value_item_icon) as ImageView
        this.refView     = itemView.findViewById(R.id.choose_value_item_reference) as LinearLayout
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setValueText(valueString : String)
    {
        this.valueView?.text = valueString

        this.valueView?.setTextColor(greyColor)

        this.iconView?.visibility = View.GONE
    }


    fun setValueTextSelected(valueString : String)
    {
        this.valueView?.text = valueString

        this.valueView?.setTextColor(greenColor)

        this.iconView?.visibility = View.VISIBLE
    }


    fun setSummaryText(summaryString : String)
    {
        this.summaryView?.text = summaryString
    }


    fun setReferenceView(onClickListener : View.OnClickListener)
    {
        this.refView?.visibility = View.VISIBLE
        this.refView?.setOnClickListener(onClickListener)
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}




// ---------------------------------------------------------------------------------------------
// HEADER VIEW HOLDER
// ---------------------------------------------------------------------------------------------

class HeaderViewHolder(val headerView : View) : RecyclerView.ViewHolder(headerView)
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
        this.headerTextView = headerView.findViewById(R.id.choose_value_header) as TextView
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun setHeaderText(headerString : String)
    {
        this.headerTextView?.text = headerString
    }

}




class ValueDividerItemDecoration(sheetUIContext: SheetUIContext) : RecyclerView.ItemDecoration()
{

    val dividerDrawable : Drawable =
            ContextCompat.getDrawable(sheetUIContext.context, R.drawable.divider_choose_value)

    init {

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        val color = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        dividerDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    override fun onDrawOver(c : Canvas, parent : RecyclerView, state : RecyclerView.State)
    {
        val left  = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        (0..(parent.childCount - 1)).forEach { i ->

            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top    = child.bottom + params.bottomMargin
            val bottom = top + dividerDrawable.intrinsicHeight

            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(c)
        }
    }

}
