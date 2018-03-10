
package com.kispoko.tome.activity.entity.engine.valueset


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.engine.value.ValueSet
import com.kispoko.tome.model.game.engine.value.ValueSetBase
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.rts.entity.valueSets
import com.kispoko.tome.util.SimpleDividerItemDecoration
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val
import effect.effValue



/**
 * Value Sets Activity
 */
class ValueSetsActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var entityId : EntityId? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_value_sets)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("entity_id"))
            this.entityId = this.intent.getSerializableExtra("entity_id") as EntityId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        this.configureToolbar(getString(R.string.engine_value_sets))

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // > Views
        val entityId = this.entityId
        if (entityId != null)
        {
            val valueSets = valueSets(entityId)
            when (valueSets) {
                is Val ->
                {
                    val valueSetsSorted = valueSets.value.sortedBy { it.labelString() }
                    this.initializeValueSetListView(entityId, valueSetsSorted)
                }
                is Err -> ApplicationLog.error(valueSets.error)
            }
        }
        else
        {
            Log.d("***VALUESETSACT", "game id is null")
        }

        this.initializeFABView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeValueSetListView(entityId : EntityId,
                                           valueSets : List<ValueSet>)
    {
        val recyclerView = this.findViewById(R.id.value_set_list_view) as RecyclerView

        recyclerView.adapter =
                ValueSetRecyclerViewAdapter(valueSets, entityId, this.appSettings.themeId(), this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val dividerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val dividerColor = ThemeManager.color(this.appSettings.themeId(), dividerColorTheme)
        if (dividerColor != null)
            recyclerView.addItemDecoration(SimpleDividerItemDecoration(this, dividerColor))

    }


    private fun initializeFABView()
    {
//        val fabView = this.findViewById(R.id.button_new_value_set)
//        fabView.setOnClickListener {
//            val intent = Intent(this, OpenSheetActivity::class.java)
//            this.startActivity(intent)
//        }
    }


    private fun applyTheme(uiColors : UIColors)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = this.appSettings.color(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_back_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val searchButton = this.findViewById(R.id.toolbar_search_button) as ImageButton
        searchButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

    }

}


// -----------------------------------------------------------------------------------------
// VALUE SET RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class ValueSetRecyclerViewAdapter(val valueSets : List<ValueSet>,
                                  val entityId : EntityId,
                                  val themeId : ThemeId,
                                  val activity : AppCompatActivity)
                                   : RecyclerView.Adapter<ValueSetSummaryViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup,
                                    viewType : Int) : ValueSetSummaryViewHolder
    {
        return ValueSetSummaryViewHolder(ValueSetSummaryView.view(themeId, parent.context))
    }


    override fun onBindViewHolder(viewHolder : ValueSetSummaryViewHolder, position : Int)
    {
        val valueSet = this.valueSets[position]

        viewHolder.setHeaderText(valueSet.labelString())
        viewHolder.setDescriptionText(valueSet.descriptionString())

        val valueSetSize = valueSet.values(entityId) ap { effValue<AppError,Int>(it.size) }
        when (valueSetSize) {
            is Val -> viewHolder.setItemCount(valueSetSize.value)
            is Err -> ApplicationLog.error(valueSetSize.error)
        }

        viewHolder.setOnClick(View.OnClickListener {
            when (valueSet)
            {
                is ValueSetBase ->
                {
                    val intent = Intent(activity, BaseValueSetActivity::class.java)
                    intent.putExtra("entity_id", entityId)
                    intent.putExtra("value_set_id", valueSet.valueSetId())
                    activity.startActivity(intent)
                }
            }
        })

    }


    override fun getItemCount() = this.valueSets.size

}


// ---------------------------------------------------------------------------------------------
// VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class ValueSetSummaryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout : LinearLayout? = null
    var headerView : TextView?  = null
    var descView   : TextView?  = null
    var countView  : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout     = itemView.findViewById(R.id.value_set_list_item_layout) as LinearLayout
        this.headerView = itemView.findViewById(R.id.value_set_list_item_header) as TextView
        this.descView   = itemView.findViewById(R.id.value_set_list_item_description) as TextView
        this.countView  = itemView.findViewById(R.id.value_set_list_item_count) as TextView
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setHeaderText(headerString : String)
    {
        this.headerView?.text = headerString
    }


    fun setDescriptionText(descriptionString : String)
    {
        this.descView?.text = descriptionString
    }


    fun setItemCount(count : Int)
    {
        this.countView?.text = count.toString()
    }


    fun setOnClick(onClick : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClick)
    }

}


object ValueSetSummaryView
{

    fun view(themeId : ThemeId, context : Context) : View
    {
        val layout = this.viewLayout(context)

        // Items Count
        layout.addView(this.itemCountView(themeId, context))

        // Summary
        layout.addView(this.summaryView(themeId, context))

        return layout
    }


    private fun viewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.id                   = R.id.value_set_list_item_layout

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.gravity              = Gravity.CENTER_VERTICAL
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        layout.color                = ThemeManager.color(themeId, headerColorTheme)

        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f
        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        return layout.linearLayout(context)
    }


    private fun summaryView(themeId : ThemeId, context : Context) : LinearLayout
    {
        // (1) Declarations
        // --------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val header      = TextViewBuilder()
        val description = TextViewBuilder()

        // (2) Layout
        // --------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.child(header)
              .child(description)

        // (3 A) Header
        // --------------------------------------------------------------------------------------

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.id                   = R.id.value_set_list_item_header

        val headerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color                = ThemeManager.color(themeId, headerColorTheme)

        header.font                 = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        header.sizeSp               = 16f

//        header.margin.bottomDp      = 3f

        // (3 B) Description
        // --------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        description.id              = R.id.value_set_list_item_description

        val descriptionColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color           = ThemeManager.color(themeId, descriptionColorTheme)

        description.font            = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        description.sizeSp          = 14f

        return layout.linearLayout(context)
    }


    private fun itemCountView(themeId : ThemeId, context : Context) : TextView
    {
        val count                   = TextViewBuilder()

        count.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        count.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        count.id                    = R.id.value_set_list_item_count

        count.gravity               = Gravity.CENTER

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        count.color                 = ThemeManager.color(themeId, colorTheme)

        count.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        count.backgroundResource    = R.drawable.bg_value_set_size

        count.sizeSp                = 15f

        count.margin.rightDp        = 15f

        return count.textView(context)
    }


}



