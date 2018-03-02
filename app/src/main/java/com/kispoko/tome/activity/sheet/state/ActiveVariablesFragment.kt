
package com.kispoko.tome.activity.sheet.state


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.sheet.SheetManager
import com.kispoko.tome.rts.entity.theme.ThemeManager
import effect.Val
import effect.effValue



/**
 * Active Variables Fragment
 */
class ActiveVariablesFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var themeId : ThemeId? = null
    private var sheetId : SheetId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(sheetId : SheetId, themeId : ThemeId) : ActiveVariablesFragment
        {
            val fragment = ActiveVariablesFragment()

            val args = Bundle()
            args.putSerializable("sheet_id", sheetId)
            args.putSerializable("theme_id", themeId)
            fragment.arguments = args

            return fragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (arguments != null)
        {
            this.sheetId = arguments.getSerializable("sheet_id") as SheetId
            this.themeId = arguments.getSerializable("theme_id") as ThemeId
        }
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {

        val themeId = this.themeId
        val sheetId = this.sheetId

        if (themeId != null && sheetId != null)
            return this.view(themeId, sheetId, context)
        else
            return null
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    private fun view(themeId : ThemeId, sheetId : SheetId, context : Context) : View
    {
        val layout = this.viewLayout(themeId, context)

        // Recycler View
        val activeVariables = SheetManager.sheetState(sheetId)
                                .apply { effValue<AppError,Collection<Variable>>(it.variables()) }

        when (activeVariables)
        {
            is Val ->  {
                //layout.addView(this.activeVariablesRecyclerView(activeVariables.value, themeId, context))
            }
        }

        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        return layout.linearLayout(context)
    }


    private fun activeVariablesRecyclerView(context : Context) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        //recyclerView.adapter            = ActiveVariableRecyclerViewAdapter(sheetItems, themeId)

        recyclerView.padding.leftDp     = 6f
        recyclerView.padding.rightDp    = 6f

        return recyclerView.recyclerView(context)
    }

}


// ---------------------------------------------------------------------------------------------
// ACTIVE VARIABLE
// ---------------------------------------------------------------------------------------------

data class ActiveVariable(val label : String, val valueString : String)


// ---------------------------------------------------------------------------------------------
// RECYCLER VIEW ADAPTER
// ---------------------------------------------------------------------------------------------

/**
 * Active Variable RecyclerView Adapter
 */
class ActiveVariableRecyclerViewAdapter(val activeVariables : List<ActiveVariable>,
                                        val themeId : ThemeId,
                                        val context : Context)
                                         : RecyclerView.Adapter<ActiveVariableViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -----------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup,
                                    viewType : Int) : ActiveVariableViewHolder
    {
        val viewBuilder = ActiveVariableViewBuilder(themeId, parent.context)
        return ActiveVariableViewHolder(viewBuilder.view())
    }


    override fun onBindViewHolder(viewHolder : ActiveVariableViewHolder, position : Int)
    {
        val activeVariable = this.activeVariables[position]

        // Name
        viewHolder.setName(activeVariable.label)

        // Value
        viewHolder.setValue(activeVariable.valueString)
    }


    override fun getItemCount() = this.activeVariables.size

}


// ---------------------------------------------------------------------------------------------
// VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * Active Variable View Holder
 */
class ActiveVariableViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout    : LinearLayout? = null
    var nameView  : TextView? = null
    var valueView : TextView? = null

    init
    {
        this.layout    = itemView.findViewById(R.id.active_variable_layout) as LinearLayout
        this.nameView  = itemView.findViewById(R.id.active_variable_name) as TextView
        this.valueView = itemView.findViewById(R.id.active_variable_value) as TextView
    }


    fun setName(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setValue(valueString : String)
    {
        this.valueView?.text = valueString
    }

}


// ---------------------------------------------------------------------------------------------
// ACTIVE VARIABLE VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class ActiveVariableViewBuilder(val themeId : ThemeId, val context : Context)
{


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Name
        layout.addView(this.nameView())

        // Value
        layout.addView(this.valueView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun nameView() : TextView
    {
        val name                = TextViewBuilder()

        name.id                 = R.id.function_list_item_header

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = ThemeManager.color(themeId, colorTheme)

        name.font               = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        name.sizeSp             = 17f

        name.margin.bottomDp    = 4f

        return name.textView(context)
    }


    private fun valueView() : TextView
    {
        val value               = TextViewBuilder()

        value.id                = R.id.function_list_item_description

        value.width             = LinearLayout.LayoutParams.MATCH_PARENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color             = ThemeManager.color(themeId, colorTheme)

        value.font              = Font.typeface(TextFont.FiraSans,
                                                        TextFontStyle.Regular,
                                                        context)

        value.sizeSp            = 14f

        value.margin.bottomDp   = 8f

        return value.textView(context)
    }

}


//
//    // SEARCH MODE
//    // -----------------------------------------------------------------------------------------
//
//    private void enableSearchMode()
//    {
//        // [1] Get Views
//        // -------------------------------------------------------------------------------------
//
//        RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
//
//        RelativeLayout searchView = SearchView.searchBarView(this);
//        final EditText searchFieldView = (EditText) searchView.findViewById(R.id.search_field);
//        ImageView searchExitButtonView = (ImageView) searchView.findViewById(R.id.search_exit);
//        ImageView searchClearButtonView = (ImageView) searchView.findViewById(R.id.search_clear);
//
//        RecyclerView searchResultsView = (RecyclerView) findViewById(R.id.search_results);
//
//        // [2] Hide / Show Views
//        // -------------------------------------------------------------------------------------
//
//        tabLayout.setVisibility(View.GONE);
//        toolbarLayout.setVisibility(View.GONE);
//        viewPager.setVisibility(View.GONE);
//        searchResultsView.setVisibility(View.VISIBLE);
//
//        toolbar.addView(searchView);
//
//        // [3] Configure Search Bar
//        // -------------------------------------------------------------------------------------
//
//        searchExitButtonView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableSearchMode();
//            }
//        });
//
//        searchClearButtonView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchFieldView.setText("");
//            }
//        });
//
//
//        searchFieldView.addTextChangedListener(new TextWatcher()
//        {
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
//            {
//                // DO NOTHING
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
//            {
//                // DO NOTHING
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable)
//            {
////                Engine engine = SheetManagerOld.currentSheet().engine();
////                if (engine != null)
////                {
////                    String query = searchFieldView.getText().toString();
////                    Set<EngineActiveSearchResult> searchResults = engine.searchActive(query);
////                    searchResultsAdapter.updateSearchResults(searchResults, query);
////                }
//            }
//
//        });
//
//        searchFieldView.requestFocus();
//        InputMethodManager imm =
//                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//
//        // [4] Configure Search Results View
//        // -------------------------------------------------------------------------------------
//
//        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
//
//        this.searchResultsAdapter = new ActiveSearchResultsRecyclerViewAdapter(this);
//        searchResultsView.setAdapter(this.searchResultsAdapter);
//
//        SimpleDividerItemDecoration dividerItemDecoration =
//                            new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86);
//        searchResultsView.addItemDecoration(dividerItemDecoration);
//    }
//
//
//    private void disableSearchMode()
//    {
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
//        RelativeLayout searchView = (RelativeLayout) findViewById(R.id.search_view);
//        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
//        RecyclerView searchResultsView = (RecyclerView) findViewById(R.id.search_results);
//
//        searchView.setVisibility(View.GONE);
//        searchResultsView.setVisibility(View.GONE);
//        tabLayout.setVisibility(View.VISIBLE);
//        toolbarLayout.setVisibility(View.VISIBLE);
//        viewPager.setVisibility(View.VISIBLE);
//    }

