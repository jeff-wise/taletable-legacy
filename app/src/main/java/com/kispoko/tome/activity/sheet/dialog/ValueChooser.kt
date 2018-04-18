
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.kispoko.tome.activity.entity.book.BookActivity
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.entity.sheet.*
import effect.Err
import maybe.Just
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
    private var entityId      : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueSet : ValueSet,
                        selectedValue : Value?,
                        updateTarget : UpdateTarget,
                        entityId : EntityId) : ValueChooserDialogFragment
        {
            val dialog = ValueChooserDialogFragment()

            val args = Bundle()
            args.putSerializable("value_set", valueSet)
            args.putSerializable("selected_value", selectedValue)
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

        this.valueSet      = arguments.getSerializable("value_set") as ValueSet
        this.selectedValue = arguments.getSerializable("selected_value") as Value?
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
        return if (entityId != null)
        {
            val valueSet      = this.valueSet
            val selectedValue = this.selectedValue
            val updateTarget  = this.updateTarget

            if (valueSet != null && updateTarget != null) {
                ValueChooserView.view(valueSet,
                                      selectedValue,
                                      updateTarget,
                                      this,
                                      entityId,
                                      context)
            }
            else {
                super.onCreateView(inflater, container, savedInstanceState)
            }
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


object ValueChooserView
{


    fun view(valueSet : ValueSet,
             selectedValue : Value?,
             updateTarget : UpdateTarget,
             dialog : DialogFragment,
             entityId : EntityId,
             context : Context) : View
    {
        val layout = viewLayout(entityId, context)

        // (1) Views
        // -------------------------------------------------------------------
        val chooserView     = chooserView(valueSet,
                                          selectedValue,
                                          updateTarget,
                                          dialog,
                                          entityId,
                                          context)
        val title = "${context.getString(R.string.choose)} ${valueSet.labelSingular().value}"

        val headerView      = headerView(title,
                                         chooserView,
                                         entityId,
                                         context)

        // (2) Add Views
        // -------------------------------------------------------------------

        // > Header
        layout.addView(headerView)

        // > Chooser
        layout.addView(chooserView)

        return layout
    }


    private fun viewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_6"))))
        layout.backgroundColor      = colorOrBlack(colorTheme, entityId)

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    fun headerView(title : String,
                   chooserView : View,
                   entityId : EntityId,
                   context : Context) : LinearLayout
    {
        val layout = headerViewLayout(entityId, context)

        val mainLayout = this.headerMainViewLayout(entityId, context)
        val titleView = headerTitleTextView(title, entityId, context)
        mainLayout.addView(titleView)

        layout.addView(mainLayout)

        layout.addView(this.headerBottomBorderView(entityId, context))

        return layout
    }


    private fun headerViewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_4")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(3.0, 3.0, 0.0, 0.0)

        return layout.linearLayout(context)
    }


    private fun headerBottomBorderView(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun headerMainViewLayout(entityId : EntityId, context : Context) : RelativeLayout
    {
        val layout = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f
        layout.padding.topDp        = 14f
        layout.padding.bottomDp     = 14f

        return layout.relativeLayout(context)
    }


    private fun headerTitleTextView(titleString : String,
                                    entityId : EntityId,
                                    context : Context) : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text              = titleString

        title.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
        title.color           = colorOrBlack(colorTheme, entityId)

        title.sizeSp          = 17f

        title.margin.leftDp   = 0.5f

        return title.textView(context)
    }

    // List View
    // -----------------------------------------------------------------------------------------

    fun chooserView(valueSet : ValueSet,
                    selectedValue : Value?,
                    updateTarget : UpdateTarget,
                    dialog : DialogFragment,
                    entityId : EntityId,
                    context : Context) : RecyclerView
    {
        val recyclerView            = RecyclerViewBuilder()

        recyclerView.width          = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height         = R.dimen.dialog_choose_value_list_height

        recyclerView.layoutManager  = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        recyclerView.backgroundColor      = colorOrBlack(colorTheme, entityId)

        val dividerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        val dividerColor = SheetManager.color(sheetUIContext.sheetId, dividerColorTheme)
        // recyclerView.divider = SimpleDividerItemDecoration(sheetUIContext.context, dividerColor)

        when (valueSet)
        {
            is ValueSetBase ->
            {
                recyclerView.adapter = BaseValueSetRecyclerViewAdapter(valueSet.sortedValues(),
                                                                       selectedValue,
                                                                       updateTarget,
                                                                       dialog,
                                                                       entityId,
                                                                       context)
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
                                CompoundValueSetRecyclerViewAdapter(items,
                                        selectedValue,
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


    // -----------------------------------------------------------------------------------------
    // VALUE VIEW
    // -----------------------------------------------------------------------------------------

    fun valueView(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = this.valueViewLayout(entityId, context)

        // Header
        layout.addView(this.valueHeaderView(entityId, context))

        // Summary
        layout.addView(this.valueSummaryView(entityId, context))

        // Reference Link
//        layout.addView(this.referenceView(entityId, context))

        return layout
    }


    private fun valueViewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.id                   = R.id.choose_value_item_layout

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.margin.leftDp        = 2f
        layout.margin.rightDp       = 2f

        layout.margin.topDp         = 1f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        layout.backgroundColor      = Color.WHITE

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

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.child(icon)
              .child(name)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.id                     = R.id.choose_value_item_icon

        icon.widthDp                = 17
        icon.heightDp               = 17

        icon.image                  = R.drawable.icon_check_bold

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        icon.color                  = colorOrBlack(iconColorTheme, entityId)

        icon.margin.rightDp         = 4f
        icon.margin.topDp         = 1f

        icon.visibility             = View.GONE

        // (3 B) Name
        // -------------------------------------------------------------------------------------

        name.id                     = R.id.choose_value_item_value

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font                   = Font.typeface(TextFont.Cabin,
                                                    TextFontStyle.Medium,
                                                    context)

        name.sizeSp                 = 18f

        return layout.linearLayout(context)
    }


    private fun valueSummaryView(entityId : EntityId, context : Context) : TextView
    {
        val summary             = TextViewBuilder()

        summary.width           = LinearLayout.LayoutParams.MATCH_PARENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.id              = R.id.choose_value_item_summary

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


    fun valueSetNameView(entityId : EntityId, context : Context) : TextView
    {
        val name                = TextViewBuilder()

        name.id                 = R.id.choose_value_header

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.sizeSp             = 13f

        name.margin.leftDp      = 10f
        name.margin.rightDp     = 10f

        name.margin.topDp       = 8f
        name.margin.bottomDp    = 8f

        return name.textView(context)
    }


}


class BaseValueSetRecyclerViewAdapter(val values : List<Value>,
                                      val selectedValue : Value?,
                                      val updateTarget : UpdateTarget,
                                      val dialog : DialogFragment,
                                      val entityId : EntityId,
                                      val context : Context)
                : RecyclerView.Adapter<ValueViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ValueViewHolder
    {
        val itemView = ValueChooserView.valueView(entityId, context)
        return ValueViewHolder(itemView, entityId, context)
    }


    override fun onBindViewHolder(viewHolder : ValueViewHolder, position : Int)
    {
        val value = this.values[position]

        when (value)
        {
            is ValueText ->
            {
                if (value.equals(selectedValue)) {
                    viewHolder.setValueTextSelected(value.value())
                    viewHolder.setSummaryTextSelected(value.description().value)
                }
                else {
                    viewHolder.setValueText(value.value())
                    viewHolder.setSummaryText(value.description().value)
                }

//                viewHolder.setSummaryText(value.description().value)

                val bookReference = value.bookReference()
                when (bookReference) {
                    is Just -> {
                        Log.d("***VALUE CHOOSER", "setting on long click")
                        viewHolder.setOnLongClick(View.OnLongClickListener {
                            val sheetActivity = context as SheetActivity
                            val intent = Intent(sheetActivity, BookActivity::class.java)
                            intent.putExtra("book_reference", bookReference.value)
                            sheetActivity.startActivity(intent)
                            true
                        })
                    }
                }

                viewHolder.setOnClick(View.OnClickListener {
                    when (updateTarget)
                    {
                        is UpdateTargetStoryWidgetPart ->
                        {
                            val storyPartUpdate = StoryWidgetUpdateTextValuePart(
                                                        updateTarget.storyWidgetId,
                                                        updateTarget.partIndex,
                                                        value.valueId())
                            Router.send(MessageSheetUpdate(storyPartUpdate))
                        }
                        is UpdateTargetTextCell ->
                        {
                            val textCellUpdate = TableWidgetUpdateSetTextCellValue(
                                                            updateTarget.tableWidgetId,
                                                            updateTarget.cellId,
                                                            value.valueId())
                            Router.send(MessageSheetUpdate(textCellUpdate))
                        }
                    }
                    dialog.dismiss()
//                    updateVariable(variableId, EngineValueText(value.valueId().value), entityId)
                })
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
                                          val selectedValue : Value?,
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
                val headerView = ValueChooserView.valueSetNameView(entityId, context)
                HeaderViewHolder(headerView)
            }
            // VALUE
            else ->
            {
                val valueView = ValueChooserView.valueView(entityId, context)
                ValueViewHolder(valueView, entityId, context)
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

                    valueViewHolder.setSummaryText(item.description().value)

                    viewHolder.setOnClick(View.OnClickListener {

                        Log.d("***VALUE CHOOSER", "update target: $updateTarget")

                        when (updateTarget)
                        {
                            is UpdateTargetTextCell ->
                            {
                                val textCellUpdate = TableWidgetUpdateSetTextCellValue(
                                                                updateTarget.tableWidgetId,
                                                                updateTarget.cellId,
                                                                item.valueId())
                                Log.d("***VALUE CHOOSER", "send update: $textCellUpdate")
                                Router.send(MessageSheetUpdate(textCellUpdate))
                            }
                        }
//                        updateVariable(variableId, EngineValueText(item.valueId().value), entityId)
                        dialog.dismiss()
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
class ValueViewHolder(itemView : View, val entityId : EntityId, val context : Context)
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
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_11"))))
    val greyColor = colorOrBlack(normalColorTheme, entityId)


    val hlColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
    val greenColor = colorOrBlack(hlColorTheme, entityId)

    val hlSummaryColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("green_70"))))
    val greenSummaryColor = colorOrBlack(hlSummaryColorTheme, entityId)

    val selectedBgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("green_30"))))
    val selectedBgColor      = colorOrBlack(selectedBgColorTheme, entityId)

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.choose_value_item_layout)
        this.valueView   = itemView.findViewById(R.id.choose_value_item_value)
        this.summaryView = itemView.findViewById(R.id.choose_value_item_summary)
        this.iconView    = itemView.findViewById(R.id.choose_value_item_icon)
//        this.refView     = itemView.findViewById(R.id.choose_value_item_reference) as LinearLayout
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

//    fun setLayout(isSelected : Boolean)
//    {
//        if (isSelected)
//        else
//    }

    fun setValueText(valueString : String)
    {
        this.valueView?.text = valueString

        this.valueView?.setTextColor(greyColor)

        this.iconView?.visibility = View.GONE

//        this.layout?.setBackgroundColor(Color.WHITE)
    }


    fun setValueTextSelected(valueString : String)
    {
        this.valueView?.text = valueString

        this.valueView?.setTextColor(greenColor)

        this.iconView?.visibility = View.VISIBLE

//        this.layout?.setBackgroundColor(selectedBgColor)
    }


    fun setSummaryText(summaryString : String)
    {
        this.summaryView?.text = summaryString
//        this.summaryView?.setTextColor(summaryColor)
    }


    fun setSummaryTextSelected(summaryString : String)
    {
        this.summaryView?.text = summaryString
//        this.summaryView?.setTextColor(greenSummaryColor)
    }

//
//    fun setReferenceView(onClickListener : View.OnClickListener)
//    {
//        this.refView?.visibility = View.VISIBLE
//        this.refView?.setOnClickListener(onClickListener)
//    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }


    fun setOnLongClick(listener : View.OnLongClickListener)
    {
        this.layout?.setOnLongClickListener(listener)
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
        this.headerTextView = headerView.findViewById(R.id.choose_value_header)
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun setHeaderText(headerString : String)
    {
        this.headerTextView?.text = headerString
    }

}




data class ValueDividerItemDecoration(val entityId : EntityId,
                                      val context : Context) : RecyclerView.ItemDecoration()
{

    val dividerDrawable : Drawable =
            ContextCompat.getDrawable(context, R.drawable.divider_choose_value)

    init {

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        val color = colorOrBlack(colorTheme, entityId)
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
