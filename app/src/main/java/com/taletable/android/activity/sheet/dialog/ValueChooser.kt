
package com.taletable.android.activity.sheet.dialog


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
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.activity.sheet.SheetActivity
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.value.*
import com.taletable.android.model.entity.ListWidgetUpdateAddValue
import com.taletable.android.model.entity.StoryWidgetUpdateTextValuePart
import com.taletable.android.model.entity.TableWidgetUpdateSetTextCellValue
import com.taletable.android.model.entity.TextWidgetUpdateSetText
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.entity.sheet.*
import com.taletable.android.util.SimpleDividerItemDecoration
import effect.Err
import maybe.Just
import effect.Val
import java.io.Serializable


/**
 * Value Choose Dialog Fragment
 */
class ValueChooserDialogFragment : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueSetId      : ValueSetId? = null
    private var valueIds        : List<ValueId> = listOf()
    private var selectedValueId : ValueId? = null
    private var updateTarget    : UpdateTarget? = null
    private var entityId        : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueSetId : ValueSetId,
                        valueIds : List<ValueId>,
                        selectedValueId : ValueId?,
                        updateTarget : UpdateTarget,
                        entityId : EntityId) : ValueChooserDialogFragment
        {
            val dialog = ValueChooserDialogFragment()

            val args = Bundle()
            args.putSerializable("value_set_id", valueSetId)
            args.putSerializable("value_ids", valueIds as Serializable)
            args.putSerializable("selected_value_id", selectedValueId)
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

        this.valueSetId      = arguments?.getSerializable("value_set_id") as ValueSetId
        this.valueIds        = arguments?.getSerializable("value_ids") as List<ValueId>
        this.selectedValueId = arguments?.getSerializable("selected_value_id") as ValueId?
        this.updateTarget    = arguments?.getSerializable("update_target") as UpdateTarget
        this.entityId        = arguments?.getSerializable("entity_id") as EntityId


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
        val entityId     = this.entityId
        val valueSetId   = this.valueSetId
        val updateTarget = this.updateTarget
        val context      = this.context

        if (entityId != null && valueSetId != null && updateTarget != null && context != null)
        {
            val valueSet = valueSet(valueSetId, entityId)
            return when (valueSet) {
                is Val -> {
                    val selectedValueId = this.selectedValueId
                    if (selectedValueId != null) {
                        val selectedValue = value(ValueReference(valueSetId, selectedValueId), entityId)
                        when (selectedValue) {
                            is Val -> {
                                ValueChooserView.view(valueSet.value,
                                              valueIds,
                                              selectedValue.value,
                                              updateTarget,
                                              this,
                                              entityId,
                                              context)
                            }
                            is Err -> {
                                ApplicationLog.error(selectedValue.error)
                                super.onCreateView(inflater, container, savedInstanceState)
                            }
                        }
                    }
                    else {
                        return ValueChooserView.view(valueSet.value,
                                              valueIds,
                                              null,
                                              updateTarget,
                                              this,
                                              entityId,
                                              context)

                    }
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


object ValueChooserView
{


    fun view(valueSet : ValueSet,
             valueIds : List<ValueId>,
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
                                          valueIds,
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
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

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(1.0, 1.0, 0.0, 0.0)

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

        layout.padding.leftDp       = 14f
        layout.padding.rightDp      = 14f
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

        title.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        title.color           = colorOrBlack(colorTheme, entityId)

        title.sizeSp          = 21f

        title.margin.leftDp   = 0.5f

        return title.textView(context)
    }

    // List View
    // -----------------------------------------------------------------------------------------

    fun chooserView(valueSet : ValueSet,
                    valueIds : List<ValueId>,
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
        recyclerView.backgroundColor      = colorOrBlack(colorTheme, entityId)

        val dividerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        val dividerColor = SheetManager.color(sheetUIContext.sheetId, dividerColorTheme)
         //recyclerView.divider = ValueDividerItemDecoration(entityId, context)

        when (valueSet)
        {
            is ValueSetBase ->
            {
                if (valueIds.isEmpty())
                {
                    recyclerView.adapter = BaseValueSetRecyclerViewAdapter(valueSet.sortedValues(),
                                                                           selectedValue,
                                                                           updateTarget,
                                                                           dialog,
                                                                           entityId,
                                                                           context)
                }
                else
                {
                    recyclerView.adapter = BaseValueSetRecyclerViewAdapter(valueSet.values(valueIds, entityId),
                                                                           selectedValue,
                                                                           updateTarget,
                                                                           dialog,
                                                                           entityId,
                                                                           context)
                }
            }
            is ValueSetCompound ->
            {
                if (valueIds.isEmpty())
                {
                    recyclerView.adapter =
                            CompoundValueSetRecyclerViewAdapter(valueSet.indexList(entityId),
                                                                selectedValue,
                                                                updateTarget,
                                                                dialog,
                                                                entityId,
                                                                context)
                }
                else
                {
                    recyclerView.adapter =
                            CompoundValueSetRecyclerViewAdapter(valueSet.indexList(valueIds,entityId),
                                                                selectedValue,
                                                                updateTarget,
                                                                dialog,
                                                                entityId,
                                                                context)
                }
            }
        }

        return recyclerView.recyclerView(context)
    }


//    private fun valueSetIndexList(valueSets : Set<ValueSet>,
//                                  entityId : EntityId,
//                                  context : Context) : List<Any>
//    {
//        fun valueSetItems(valueSet : ValueSet) : List<Any>
//        {
//            val values = valueSet.values(entityId)
//            return when (values) {
//                is Val -> listOf(valueSet.label()).plus(values.value)
//                is Err -> listOf(valueSet.label())
//            }
//        }
//
//        return valueSets.sortedBy { it.labelString() }
//                        .flatMap(::valueSetItems)
//    }


    // -----------------------------------------------------------------------------------------
    // VALUE VIEW
    // -----------------------------------------------------------------------------------------

    fun valueView(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = this.valueViewLayout(entityId, context)

        // Header
        layout.addView(this.valueHeaderView(entityId, context))

        layout.addView(this.valueDividerView(entityId, context))

        // Summary
        //layout.addView(this.valueSummaryView(entityId, context))

        // Reference Link
//        layout.addView(this.referenceView(entityId, context))

        return layout
    }


    private fun valueDividerView(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
        layout.backgroundColor      = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun valueViewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.id                   = R.id.choose_value_item_layout

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp        = 14f
        layout.padding.rightDp       = 14f


//         layout.margin.topDp         = 1f

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

        layout.padding.topDp        = 14f
        layout.padding.bottomDp     = 14f

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.child(icon)
              .child(name)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.id                     = R.id.choose_value_item_icon

        icon.widthDp                = 21
        icon.heightDp               = 21

        icon.image                  = R.drawable.icon_check

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        icon.color                  = colorOrBlack(iconColorTheme, entityId)

        icon.margin.rightDp         = 8f
        icon.margin.topDp         = 1f

        icon.visibility             = View.GONE

        // (3 B) Name
        // -------------------------------------------------------------------------------------

        name.id                     = R.id.choose_value_item_value

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font                   = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        name.sizeSp                 = 21f

        return layout.linearLayout(context)
    }


    private fun valueSummaryView(entityId : EntityId, context : Context) : TextView
    {
        val summary             = TextViewBuilder()

        summary.width           = LinearLayout.LayoutParams.MATCH_PARENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.id              = R.id.choose_value_item_summary

        summary.font            = Font.typeface(TextFont.RobotoCondensed,
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

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        name.color              = colorOrBlack(colorTheme, entityId)



//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        name.backgroundColor      = colorOrBlack(bgColorTheme, entityId)
        name.backgroundColor      = Color.WHITE

        name.sizeSp             = 15f

        name.margin.leftDp      = 2f
        name.margin.rightDp     = 2f


        name.margin.topDp       = 2f

        name.padding.topDp      = 6f
        name.padding.bottomDp   = 6f
        name.padding.leftDp     = 6f
        name.padding.rightDp    = 6f

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
                        is UpdateTargetTextWidget ->
                        {
                            val textWidgetUpdate = TextWidgetUpdateSetText(updateTarget.textWidgetId,
                                                                           value.valueId().value)
                            Router.send(MessageSheetUpdate(textWidgetUpdate))
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
                                Router.send(MessageSheetUpdate(textCellUpdate))
                            }
                            is UpdateTargetListWidget ->
                            {
                                val listWidgetUpdate = ListWidgetUpdateAddValue(
                                                            updateTarget.listWidgetId,
                                                            item.valueId().value)
                                Router.send(MessageSheetUpdate(listWidgetUpdate))
                            }
                        }
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
//            headerViewHolder.setHeaderText(item.value.toUpperCase())
    headerViewHolder.setHeaderText(item.value)
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
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
    val greyColor = colorOrBlack(normalColorTheme, entityId)


    val hlColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_8")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("green_tint_1"))))
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

    val dividerDrawable : Drawable? =
            ContextCompat.getDrawable(context, R.drawable.divider_choose_value)

    init {

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        val color = colorOrBlack(colorTheme, entityId)
        dividerDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    override fun onDrawOver(c : Canvas, parent : RecyclerView, state : RecyclerView.State)
    {
        val left  = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        (0..(parent.childCount - 1)).forEach { i ->

            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top    = child.bottom + params.bottomMargin
            val bottom = top + (dividerDrawable?.intrinsicHeight ?: 0)

            dividerDrawable?.setBounds(left, top, right, bottom)
            dividerDrawable?.draw(c)
        }
    }

}
