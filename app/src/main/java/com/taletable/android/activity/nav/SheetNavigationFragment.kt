
package com.taletable.android.activity.nav


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.RecyclerViewBuilder
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.theme.ThemeManager


/**
 * Sheet Navigation Fragment
 */
//class SheetNavigationFragment : Fragment()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var themeId : ThemeId? = null
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun newInstance(themeId : ThemeId) : SheetNavigationFragment
//        {
//            val fragment = SheetNavigationFragment()
//
//            val args = Bundle()
//            args.putSerializable("theme_id", themeId)
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
//        this.themeId = arguments?.getSerializable("theme_id") as ThemeId
//    }
//
//
//    override fun onCreateView(inflater : LayoutInflater,
//                              container : ViewGroup?,
//                              savedInstanceState : Bundle?) : View?
//    {
//        val themeId = this.themeId
//        val context = this.context
//
//        return if (themeId != null && context != null)
//            this.view(themeId, context)
//        else
//            null
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEWS
//    // -----------------------------------------------------------------------------------------
//
//    private fun view(themeId : ThemeId, context : Context) : View
//    {
//        val layout = this.viewLayout(themeId, context)
//
//        // Recycler View
////        val sheetItems = SheetManager.openSheets().map {
////            val sheetContext = SheetManager.sheetContext(it)
////
////            val campaign = sheetContext ap { CampaignManager.campaignWithId(it.campaignId) }
////            val game = sheetContext ap { GameManager.gameWithId(it.gameId) }
//////
////            var campaignName : String? = null
////            var gameName : String? = null
////
////            when (campaign) {
////                is Val -> campaignName = campaign.value.campaignName()
////                is Err -> ApplicationLog.error(campaign.error)
////            }
////
////            when (game) {
////                is Val -> gameName = game.value.gameName().value
////                is Err -> ApplicationLog.error(game.error)
////            }
////
////            SheetItem(it.settings().sheetName(),
////                      it.settings().sheetSummary(),
////                      campaignName,
////                      gameName)
////        }
//
////        layout.addView(this.sheetRecyclerView(sheetItems, themeId, context))
//
//        return layout
//    }
//
//
//    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun sheetRecyclerView(sheetItems : List<SheetItem>,
//                                  themeId : ThemeId,
//                                  context : Context) : RecyclerView
//    {
//        val recyclerView                = RecyclerViewBuilder()
//
//        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
//        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT
//
//        recyclerView.layoutManager      = LinearLayoutManager(context)
//
//        recyclerView.adapter            = SheetRecyclerViewAdapter(sheetItems, themeId)
//
//        recyclerView.padding.leftDp     = 6f
//        recyclerView.padding.rightDp    = 6f
//
//        return recyclerView.recyclerView(context)
//    }
//
//}
//

