
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
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.Err
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
    private var sheetContext  : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueSet : ValueSet,
                        selectedValue : Value,
                        sheetContext: SheetContext) : ValueChooserDialogFragment
        {
            val dialog = ValueChooserDialogFragment()

            val args = Bundle()
            args.putSerializable("value_set", valueSet)
            args.putSerializable("selected_value", selectedValue)
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
        this.selectedValue = arguments.getSerializable("selected_value") as Value
        this.sheetContext  = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val dialogLayout = this.dialogLayout(sheetUIContext)

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

            val valueSet       = this.valueSet
            val selectedValue  = this.selectedValue

            if (valueSet != null && selectedValue != null)
                return ValueChooserView.view(valueSet, selectedValue, sheetUIContext)
            else
                return super.onCreateView(inflater, container, savedInstanceState)
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

}


object ValueChooserView
{


    fun view(valueSet : ValueSet,
             selectedValue : Value,
             sheetUIContext: SheetUIContext) : View
    {
        val layout = viewLayout(sheetUIContext)

        // (1) Views
        // -------------------------------------------------------------------
        val chooserView     = chooserView(valueSet, selectedValue, sheetUIContext)
        val optionsMenuView = optionsMenuView(sheetUIContext.context)
        val headerView      = headerView(valueSet.label(), chooserView,
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
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(TopLeftCornerRadius(0f),
                                              TopRightCornerRadius(0f),
                                              BottomRightCornerRadius(2f),
                                              BottomLeftCornerRadius(2f))

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
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_11")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f
        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        layout.margin.bottomDp      = 3f


        layout.corners              = Corners(TopLeftCornerRadius(2f),
                                                TopRightCornerRadius(2f),
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
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
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
                    selectedValue : Value,
                    sheetUIContext: SheetUIContext) : RecyclerView
    {
        val recyclerView            = RecyclerViewBuilder()

        recyclerView.width          = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height         = R.dimen.dialog_choose_value_list_height

        recyclerView.layoutManager  = LinearLayoutManager(sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_11")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        recyclerView.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        when (valueSet)
        {
            is ValueSetBase ->
            {
                recyclerView.adapter = BaseValueSetRecyclerViewAdapter(valueSet.sortedValues(),
                        selectedValue,
                        sheetUIContext)
            }
            is ValueSetCompound ->
            {
                val valueSets = valueSet.valueSets(sheetUIContext)
                when (valueSets)
                {
                    is Val ->
                    {
                        val items = valueSetIndexList(valueSets.value, sheetUIContext)
                        recyclerView.adapter =
                                CompoundValueSetRecyclerViewAdapter(items,
                                        selectedValue,
                                        sheetUIContext)
                    }
                    is Err -> ApplicationLog.error(valueSets.error)
                }
            }
        }

        return recyclerView.recyclerView(sheetUIContext.context)
    }


    private fun valueSetIndexList(valueSets : Set<ValueSet>,
                                  sheetUIContext: SheetUIContext) : List<Any> =
        valueSets.sortedBy { it.label() }
                .flatMap { listOf(it.label()).plus(it.values(SheetContext(sheetUIContext))) }


    // -----------------------------------------------------------------------------------------
    // VALUE VIEW
    // -----------------------------------------------------------------------------------------

    fun valueView(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = valueViewLayout(sheetUIContext)

        layout.addView(valueHeaderView(sheetUIContext))
        layout.addView(valueSummaryView(sheetUIContext))
        // layout.addView(this.valueDividerView(context))

        return layout
    }


    private fun valueViewLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp       = 9f
        layout.padding.rightDp      = 9f

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.margin.leftDp        = 3f
        layout.margin.rightDp       = 3f

        layout.margin.topDp         = 3f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_9")),
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

        icon.id                     = R.id.choose_value_dialog_item_icon

        icon.widthDp                = 17
        icon.heightDp               = 17

        icon.image                  = R.drawable.icon_check

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("green_1")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp         = 5f

        icon.visibility             = View.GONE

        // (3 B) Name
        // -------------------------------------------------------------------------------------

        name.id                     = R.id.choose_value_dialog_item_value

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font                   = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color                  = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.sizeSp                 = 15f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun valueSummaryView(sheetUIContext: SheetUIContext) : TextView
    {
        val summary             = TextViewBuilder()

        summary.width           = LinearLayout.LayoutParams.MATCH_PARENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.id              = R.id.choose_value_dialog_item_summary

        summary.font            = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_30")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        summary.color           = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        summary.sizeSp          = 13f

        summary.margin.topDp    = 3f
        summary.margin.leftDp   = 0.5f

        return summary.textView(sheetUIContext.context)
    }


    fun valueSetNameView(context : Context) : TextView
    {
        val name            = TextViewBuilder()

        name.id             = R.id.value_list_header_text

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font           = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            context)

//        name.color          = R.color.gold_light
        name.sizeSp         = 18f

        name.margin.leftDp  = 10f
        name.margin.rightDp = 10f

        name.margin.topDp   = 20f

        return name.textView(context)
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
                                      val selectedValue : Value,
                                      val sheetUIContext: SheetUIContext)
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
            }
            is ValueNumber ->
            {
                viewHolder.setValueText(value.value().toString())
            }
        }
    }


    override fun getItemCount() : Int = this.values.size

}


// -----------------------------------------------------------------------------------------
// COMPOUND VALUE SET ADPATER
// -----------------------------------------------------------------------------------------

class CompoundValueSetRecyclerViewAdapter(val items : List<Any>,
                                          val selectedValue : Value,
                                          val sheetUIContext: SheetUIContext)
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

        if (itemAtPosition is String)
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
                val headerView = ValueChooserView.valueSetNameView(parent.context)
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
                }
                is ValueNumber ->
                {
                    valueViewHolder.setValueText(item.value().toString())
                }
            }
        }
        else if (item is String)
        {
            val headerViewHolder = viewHolder as HeaderViewHolder
            headerViewHolder.setHeaderText(item)
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

    var valueView   : TextView?  = null
    var summaryView : TextView?  = null
    var iconView    : ImageView? = null


    val normalColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    val greyColor = SheetManager.color(sheetUIContext.sheetId, normalColorTheme)


    val hlColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("green_1")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
    val greenColor = SheetManager.color(sheetUIContext.sheetId, hlColorTheme)

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.valueView   = itemView.findViewById(R.id.choose_value_dialog_item_value)
                                as TextView

        this.summaryView = itemView.findViewById(R.id.choose_value_dialog_item_summary)
                                as TextView

        this.iconView    = itemView.findViewById(R.id.choose_value_dialog_item_icon)
                                as ImageView
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
        this.headerTextView = headerView.findViewById(R.id.value_list_header_text) as TextView
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
