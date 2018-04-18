
package com.kispoko.tome.activity.official.pathfinder_one_srd


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.games.HeroesCharacterSheetSummary



/**
 * Pathfinder 1 SRD Official Characters
 */
//class PathfinderOneSRDCharactersFragment : Fragment()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var theme : Theme? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun newInstance(theme : Theme) : PathfinderOneSRDCharactersFragment
//        {
//            val fragment = PathfinderOneSRDCharactersFragment()
//
//            val args = Bundle()
//            args.putSerializable("theme", theme)
//            fragment.arguments = args
//
//            return fragment
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // FRAGMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onCreate(savedInstanceState : Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//
//        if (arguments != null)
//        {
//            this.theme = arguments.getSerializable("theme") as Theme
//        }
//    }
//
//
//    override fun onCreateView(inflater : LayoutInflater?,
//                              container : ViewGroup?,
//                              savedInstanceState : Bundle?) : View?
//    {
//        val theme = this.theme
//
//        return if (theme != null)
//            this.view(theme, context)
//        else
//            null
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEWS
//    // -----------------------------------------------------------------------------------------
//
//    private fun view(theme : Theme, context : Context) : View
//    {
//        val layout = this.viewLayout(theme, context)
//
//        // Recycler View
////        val characterSheetManifest = OfficialManager.heroesCharacterSheetManifest(context)
////        if (characterSheetManifest != null)
////        {
////            val summaries = characterSheetManifest.summaries
////            layout.addView(this.characterSummaryRecyclerView(summaries, theme, context))
////        }
//
//        return layout
//    }
//
//
//    private fun viewLayout(theme : Theme, context : Context) : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        layout.backgroundColor  = theme.colorOrBlack(colorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun characterSummaryRecyclerView(summaries : List<HeroesCharacterSheetSummary>,
//                                             theme : Theme,
//                                             context : Context) : RecyclerView
//    {
//        val recyclerView                = RecyclerViewBuilder()
//
//        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
//        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT
//
//        recyclerView.layoutManager      = LinearLayoutManager(context)
//
//        recyclerView.adapter            = CharactersRecyclerViewAdapter(summaries,
//                theme,
//                context)
//
//        recyclerView.padding.leftDp     = 6f
//        recyclerView.padding.rightDp    = 6f
//        recyclerView.padding.bottomDp   = 60f
//
//        recyclerView.clipToPadding      = false
//
//        return recyclerView.recyclerView(context)
//    }
//
//}