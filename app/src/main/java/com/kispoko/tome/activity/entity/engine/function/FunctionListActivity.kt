
package com.kispoko.tome.activity.entity.engine.function


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
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.engine.function.Function
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.game.GameManager
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.SimpleDividerItemDecoration
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val
import effect.effValue



/**
 * Function List Activity
 */
class FunctionListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameId : EntityId? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_function_list)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as EntityId


        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        this.configureToolbar(this.getString(R.string.functions))

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // > Function List
        val gameId = this.gameId
        if (gameId != null)
        {
            val functions = GameManager.engine(gameId).apply {
                                effValue<AppError,List<Function>>(it.functions()) }

            when (functions) {
                is Val -> {
                    val functionSet = functions.value
                    val functionList = functionSet.sortedBy { it.labelString() }
                    this.initializeFunctionListView(functionList, gameId)
                }
                is Err -> ApplicationLog.error(functions.error)
            }
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

    private fun initializeFunctionListView(functions : List<Function>, gameId : EntityId)
    {
        val recyclerView = this.findViewById<RecyclerView>(R.id.function_list_view)

        recyclerView.adapter = FunctionRecyclerViewAdapter(functions,
                                                           gameId,
                                                           this.appSettings.themeId(),
                                                           this)

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
//        val fabView = this.findViewById(R.id.button_new_value)
//        fabView.setOnClickListener {
//            val valueSet = this.valueSet
//            if (valueSet != null)
//            {
//                val dialog = NewValueDialog.newInstance(valueSet)
//                dialog.show(supportFragmentManager, "")
//            }
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
    }

}


// ---------------------------------------------------------------------------------------------
// RECYCLER VIEW ADAPTER
// ---------------------------------------------------------------------------------------------

/**
 * Function List RecyclerView Adapter
 */
class FunctionRecyclerViewAdapter(val functions : List<Function>,
                                  val gameId : EntityId,
                                  val themeId : ThemeId,
                                  val activity : AppCompatActivity)
                                   : RecyclerView.Adapter<FunctionViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -----------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup,
                                    viewType : Int) : FunctionViewHolder
    {
        val viewBuilder = FunctionItemViewBuilder(themeId, parent.context)
        return FunctionViewHolder(viewBuilder.view())
    }


    override fun onBindViewHolder(viewHolder : FunctionViewHolder, position : Int)
    {
        val function = this.functions[position]

        // Header
        viewHolder.setHeaderText(function.labelString())

        // Description
        viewHolder.setDescriptionText(function.descriptionString())

        // Parameter Types
        val typeSignature = function.typeSignature()

        viewHolder.setParameterType1(typeSignature.parameter1Type().toString().toUpperCase())

        val paramter2Type = typeSignature.parameter2Type()
        if (paramter2Type != null)
            viewHolder.setParameterType2(paramter2Type.toString().toUpperCase())

        val paramter3Type = typeSignature.parameter3Type()
        if (paramter3Type != null)
            viewHolder.setParameterType3(paramter3Type.toString().toUpperCase())

        val paramter4Type = typeSignature.parameter4Type()
        if (paramter4Type != null)
            viewHolder.setParameterType4(paramter4Type.toString().toUpperCase())

        val paramter5Type = typeSignature.parameter5Type()
        if (paramter5Type != null)
            viewHolder.setParameterType5(paramter5Type.toString().toUpperCase())

        // Result Types
        viewHolder.setResultType(typeSignature.resultType().toString().toUpperCase())

        // On Click Listener
        viewHolder.setOnClick(View.OnClickListener {
            val intent = Intent(activity, FunctionActivity::class.java)
            intent.putExtra("function_id", function.functionId())
            intent.putExtra("game_id", gameId)
            activity.startActivity(intent)
        })

    }

    override fun getItemCount() = this.functions.size

}


// ---------------------------------------------------------------------------------------------
// VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * Function View Holder
 */
class FunctionViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout              : LinearLayout? = null
    var headerView          : TextView? = null
    var descriptionView     : TextView? = null

    var parameterType1View  : TextView? = null
    var parameterType2View  : TextView? = null
    var parameterType3View  : TextView? = null
    var parameterType4View  : TextView? = null
    var parameterType5View  : TextView? = null

    var resultTypeView      : TextView? = null


    init
    {
        this.layout = itemView.findViewById(R.id.function_list_item_layout)
        this.headerView = itemView.findViewById(R.id.function_list_item_header)
        this.descriptionView = itemView.findViewById(R.id.function_list_item_description)

        this.parameterType1View = itemView.findViewById(R.id.function_list_item_parameter_type_1)
        this.parameterType2View = itemView.findViewById(R.id.function_list_item_parameter_type_2)
        this.parameterType3View = itemView.findViewById(R.id.function_list_item_parameter_type_3)
        this.parameterType4View = itemView.findViewById(R.id.function_list_item_parameter_type_4)
        this.parameterType5View = itemView.findViewById(R.id.function_list_item_parameter_type_5)

        this.resultTypeView = itemView.findViewById(R.id.function_list_item_result_type)
    }


    fun setHeaderText(headerString : String)
    {
        this.headerView?.text = headerString
    }


    fun setParameterType1(typeString : String)
    {
        this.parameterType1View?.text = typeString
        this.parameterType1View?.visibility = View.VISIBLE
    }


    fun setParameterType2(typeString : String)
    {
        this.parameterType2View?.text = typeString
        this.parameterType2View?.visibility = View.VISIBLE
    }


    fun setParameterType3(typeString : String)
    {
        this.parameterType3View?.text = typeString
        this.parameterType3View?.visibility = View.VISIBLE
    }


    fun setParameterType4(typeString : String)
    {
        this.parameterType4View?.text = typeString
        this.parameterType4View?.visibility = View.VISIBLE
    }


    fun setParameterType5(typeString : String)
    {
        this.parameterType5View?.text = typeString
        this.parameterType5View?.visibility = View.VISIBLE
    }


    fun setResultType(typeString : String)
    {
        this.resultTypeView?.text = typeString
    }


    fun setDescriptionText(descriptionString : String)
    {
        this.descriptionView?.text = descriptionString
    }


    fun setOnClick(onClick : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClick)

    }

}


// ---------------------------------------------------------------------------------------------
// FUNCTION ITEM VIEW BUILDER
// ---------------------------------------------------------------------------------------------

/**
 * Function Item View Builder
 */
class FunctionItemViewBuilder(val themeId : ThemeId, val context : Context)
{


    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // Description
        layout.addView(this.descriptionView())

        // Types
        layout.addView(this.typesView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.id               = R.id.function_list_item_layout

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 12f
        layout.padding.rightDp  = 12f
        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f

        return layout.linearLayout(context)
    }


    private fun headerView() : TextView
    {
        val header                  = TextViewBuilder()

        header.id                   = R.id.function_list_item_header

        header.width                = LinearLayout.LayoutParams.MATCH_PARENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color                = ThemeManager.color(themeId, colorTheme)

        header.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        header.sizeSp               = 17f

        header.margin.bottomDp      = 4f

        return header.textView(context)
    }


    private fun descriptionView() : TextView
    {
        val description                 = TextViewBuilder()

        description.id                  = R.id.function_list_item_description

        description.width               = LinearLayout.LayoutParams.MATCH_PARENT
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color               = ThemeManager.color(themeId, colorTheme)

        description.font                = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Regular,
                                                        context)

        description.sizeSp              = 14f

        description.margin.bottomDp     = 8f

        return description.textView(context)
    }


    private fun typesView() : LinearLayout
    {
        val layout = this.typesLayout()

        // > Parameter Type Views
        layout.addView(this.parameterTypeView(1))
        layout.addView(this.parameterTypeView(2))
        layout.addView(this.parameterTypeView(3))
        layout.addView(this.parameterTypeView(4))
        layout.addView(this.parameterTypeView(5))

        // > Arrow
        layout.addView(this.arrowView())

        // > Result Type View
        layout.addView(this.resultTypeView())

        return layout
    }


    private fun typesLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    private fun arrowView() : ImageView
    {
        val arrow               = ImageViewBuilder()

        arrow.widthDp           = 18
        arrow.heightDp          = 18

        arrow.image             = R.drawable.ic_type_arrow

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        arrow.color             = ThemeManager.color(themeId, colorTheme)

        arrow.margin.rightDp    = 6f

        return arrow.imageView(context)
    }


    private fun parameterTypeView(parameterIndex : Int) : TextView
    {
        val type                = TextViewBuilder()

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        type.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        val colorTheme = ColorTheme(setOf(
                            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        type.color              = ThemeManager.color(themeId, colorTheme)

        type.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        type.sizeSp             = 11f


        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        type.backgroundColor    = ThemeManager.color(themeId, bgColorTheme)

        type.visibility         = View.GONE

        type.margin.rightDp     = 6f

        type.padding.topDp      = 6f
        type.padding.bottomDp   = 6f
        type.padding.leftDp     = 9f
        type.padding.rightDp    = 9f

        // > Set the id
        when (parameterIndex)
        {
            1 -> type.id = R.id.function_list_item_parameter_type_1
            2 -> type.id = R.id.function_list_item_parameter_type_2
            3 -> type.id = R.id.function_list_item_parameter_type_3
            4 -> type.id = R.id.function_list_item_parameter_type_4
            5 -> type.id = R.id.function_list_item_parameter_type_5
        }

        return type.textView(context)
    }


    private fun resultTypeView() : TextView
    {
        val type                = TextViewBuilder()

        type.id                 = R.id.function_list_item_result_type

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        type.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        val textColorTheme = ColorTheme(setOf(
                            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        type.color              = ThemeManager.color(themeId, textColorTheme)

        type.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        type.sizeSp             = 11.5f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        type.backgroundColor    = ThemeManager.color(themeId, bgColorTheme)

        type.padding.topDp      = 6f
        type.padding.bottomDp   = 6f
        type.padding.leftDp     = 9f
        type.padding.rightDp    = 9f

        return type.textView(context)
    }


}
