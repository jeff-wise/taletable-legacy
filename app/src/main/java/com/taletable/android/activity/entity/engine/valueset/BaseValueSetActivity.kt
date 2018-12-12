
package com.taletable.android.activity.entity.engine.valueset


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.entity.engine.value.ValueActivity
import com.taletable.android.app.AppSettings
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.engine.value.Value
import com.taletable.android.model.engine.value.ValueSetBase
import com.taletable.android.model.engine.value.ValueSetId
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.game.GameManager
import com.taletable.android.rts.entity.theme.ThemeManager
import com.taletable.android.util.SimpleDividerItemDecoration
import com.taletable.android.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Base Value Set Activity
 */
class BaseValueSetActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueSetId   : ValueSetId? = null
    private var gameId : EntityId? = null

    private var valueSet : ValueSetBase? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)

    private var recyclerViewAdapter : ValueRecyclerViewAdapter? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_base_value_set)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("value_set_id"))
            this.valueSetId = this.intent.getSerializableExtra("value_set_id") as ValueSetId

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as EntityId

        // (3) Lookup Value Set
        // -------------------------------------------------------------------------------------

        val gameId = this.gameId
        val valueSetId = this.valueSetId
        if (gameId != null && valueSetId != null)
        {
            val baseValueSet = GameManager.engine(gameId)
                                          .apply { it.baseValueSet(valueSetId) }

            when (baseValueSet) {
                is Val -> this.valueSet = baseValueSet.value
                is Err -> ApplicationLog.error(baseValueSet.error)
            }
        }

        // (4) Initialize Views
        // -------------------------------------------------------------------------------------

        val valueSet = this.valueSet

        // > Toolbar
        if (valueSet != null)
            this.configureToolbar(valueSet.labelString())

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // > Value List
        if (valueSet != null && gameId != null)
        {
            val values = valueSet.sortedValues()
            this.initializeValueListView(valueSet, gameId)
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

    private fun initializeValueListView(valueSet : ValueSetBase,
                                        gameId : EntityId)
    {
        val recyclerView = this.findViewById<RecyclerView>(R.id.value_list_view)

        val adapter = ValueRecyclerViewAdapter(valueSet.sortedValues(),
                                               this.appSettings.themeId(),
                                               gameId,
                                               this)
        recyclerView.adapter = adapter
        this.recyclerViewAdapter = adapter

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
        val fabView = this.findViewById<View>(R.id.button_new_value)
        fabView.setOnClickListener {
            val valueSet = this.valueSet
            val gameId = this.gameId
            if (valueSet != null && gameId != null)
            {
                val intent = Intent(this, ValueActivity::class.java)
                intent.putExtra("is_new_value", true)
                intent.putExtra("game_id", gameId)
                intent.putExtra("value_set_id", valueSet)
                startActivity(intent)
            }
//            if (valueSet != null && gameId != null)
//            {
//                val newValue = valueSet.newDefaultTextValue()
//                valueSet.addValue(newValue)
//                this.recyclerViewAdapter?.updateValues(valueSet.sortedValues())
//                this.recyclerViewAdapter?.notifyDataSetChanged()
//
////                GameManager.engine(gameId)
////                        .apDo { it.updateValueSet(valueSet) }
//            }
        }
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
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_back_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val searchButton = this.findViewById<ImageButton>(R.id.toolbar_search_button)
        searchButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TOOLBAR TITLE
        // -------------------------------------------------------------------------------------
        val toolbarTitleView = this.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

        // TITLE
        // -------------------------------------------------------------------------------------

        val titleView = this.findViewById<TextView>(R.id.title)
        titleView.typeface = Font.typeface(TextFont.default(), TextFontStyle.Regular, this)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val color = ThemeManager.color(this.appSettings.themeId(), colorTheme)
        if (color != null)
            titleView.setTextColor(color)

    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun onUpdate()
    {
        val valueSet = this.valueSet
        if (valueSet != null)
        {
            this.recyclerViewAdapter?.updateValues(valueSet.sortedValues())
            this.recyclerViewAdapter?.notifyDataSetChanged()
        }
    }

}


// -----------------------------------------------------------------------------------------
// VALUE RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class ValueRecyclerViewAdapter(var values : List<Value>,
                               val themeId : ThemeId,
                               val gameId : EntityId,
                               val activity : AppCompatActivity)
                               : RecyclerView.Adapter<ValueSummaryViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup,
                                    viewType : Int) : ValueSummaryViewHolder
    {
        return ValueSummaryViewHolder(ValueSummaryView.view(themeId, parent.context))
    }


    override fun onBindViewHolder(viewHolder : ValueSummaryViewHolder, position : Int)
    {
        val value = this.values[position]

        viewHolder.setValueText(value.valueString())

        viewHolder.setDescriptionText(value.description().value)

        viewHolder.setOnClick(View.OnClickListener {
            val intent = Intent(activity, ValueActivity::class.java)
            intent.putExtra("game_id", gameId)
            intent.putExtra("value_reference", value.valueReference())
            activity.startActivity(intent)
        })

        viewHolder.setOnLongClick(View.OnLongClickListener {
            val dialog = ValueActionDialog.newInstance(value.valueReference(), gameId)
            dialog.show(activity.supportFragmentManager, "")
            true
        })

    }


    override fun getItemCount() = this.values.size


    // -------------------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------------------

    fun updateValues(values : List<Value>) {
        this.values = values
    }

}



// ---------------------------------------------------------------------------------------------
// VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class ValueSummaryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout    : LinearLayout? = null
    var valueView : TextView?  = null
    var descView  : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout    = itemView.findViewById(R.id.value_list_item_layout)
        this.valueView = itemView.findViewById(R.id.value_list_item_value)
        this.descView  = itemView.findViewById(R.id.value_list_item_description)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setValueText(valueString : String)
    {
        this.valueView?.text = valueString
    }


    fun setDescriptionText(descriptionString : String)
    {
        this.descView?.text = descriptionString
    }


    fun setOnClick(onClick : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClick)
    }


    fun setOnLongClick(onLongClick : View.OnLongClickListener)
    {
        this.layout?.setOnLongClickListener(onLongClick)
    }

}


// ---------------------------------------------------------------------------------------------
// VALUE SUMMARY VIEW
// ---------------------------------------------------------------------------------------------

object ValueSummaryView
{

    fun view(themeId : ThemeId, context : Context) : View
    {
        val layout = this.viewLayout(context)

        // Value
        layout.addView(this.valueView(themeId, context))

        // Description
        layout.addView(this.descriptionView(themeId, context))

        return layout
    }


    private fun viewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.id                   = R.id.value_list_item_layout

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f
        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        return layout.linearLayout(context)
    }


    private fun valueView(themeId : ThemeId, context : Context) : TextView
    {
        val value                   = TextViewBuilder()

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.id                    = R.id.value_list_item_value

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color                 = ThemeManager.color(themeId, colorTheme)

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        value.sizeSp                = 17f

        return value.textView(context)
    }


    private fun descriptionView(themeId : ThemeId, context : Context) : TextView
    {
        val desc                    = TextViewBuilder()

        desc.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        desc.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        desc.id                     = R.id.value_list_item_description

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        desc.color                  = ThemeManager.color(themeId, colorTheme)

        desc.font                   = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        desc.sizeSp                 = 13f

        return desc.textView(context)
    }


}


