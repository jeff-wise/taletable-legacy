
package com.taletable.android.activity.nav


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.RecyclerViewBuilder
import com.taletable.android.model.game.Game
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.game.GameManager
import com.taletable.android.rts.entity.theme.ThemeManager



/**
 * Game Navigation Fragment
 */
//class GameNavigationFragment : Fragment()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var themeId : ThemeId? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun newInstance(themeId : ThemeId) : GameNavigationFragment
//        {
//            val fragment = GameNavigationFragment()
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
//        if (themeId != null && context != null)
//            return this.view(themeId, context)
//        else
//        return null
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
//        val games = GameManager.openGames()
//        layout.addView(this.campaignSummaryRecyclerView(games, themeId, context))
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
//    private fun campaignSummaryRecyclerView(games : List<Game>,
//                                            themeId : ThemeId,
//                                            context : Context) : RecyclerView
//    {
//        val recyclerView                = RecyclerViewBuilder()
//
//        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
//        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT
//
//        recyclerView.layoutManager      = LinearLayoutManager(context)
//
//        val activity = context as AppCompatActivity
//        recyclerView.adapter            = GameRecyclerViewAdapter(games, activity, themeId)
//
//        recyclerView.padding.leftDp     = 6f
//        recyclerView.padding.rightDp    = 6f
//
//        return recyclerView.recyclerView(context)
//    }
//
//}


//// -----------------------------------------------------------------------------------------
//// GAME RECYCLER VIEW ADPATER
//// -----------------------------------------------------------------------------------------
//
//class GameRecyclerViewAdapter(val games : List<Game>,
//                              val activity : AppCompatActivity,
//                              val themeId : ThemeId)
//                                : RecyclerView.Adapter<OpenGameSummaryViewHolder>()
//{
//
//    // -------------------------------------------------------------------------------------
//    // RECYCLER VIEW ADAPTER API
//    // -------------------------------------------------------------------------------------
//
//    override fun onCreateViewHolder(parent : ViewGroup,
//                                    viewType : Int) : OpenGameSummaryViewHolder
//    {
//        return OpenGameSummaryViewHolder(OpenGameSummaryView.view(themeId, parent.context))
//    }
//
//
//    override fun onBindViewHolder(viewHolder : OpenGameSummaryViewHolder, position : Int)
//    {
//        val game = this.games[position]
//
//        viewHolder.setOnClick(View.OnClickListener {
//            val intent = Intent(activity, GameActivity::class.java)
//            intent.putExtra("game", game)
//            activity.startActivity(intent)
//        })
//
//        viewHolder.setNameText(game.gameName().value)
//        viewHolder.setSummaryText(game.gameName().value)
//    }
//
//
//    override fun getItemCount() = this.games.size
//
//}
//
//
//// ---------------------------------------------------------------------------------------------
//// VIEW HOLDER
//// ---------------------------------------------------------------------------------------------
//
///**
// * The View Holder caches a view for each item.
// */
//class OpenGameSummaryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    var layoutView  : LinearLayout? = null
//    var nameView    : TextView?  = null
//    var summaryView : TextView?  = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.layoutView  = itemView.findViewById(R.id.game_nav_item_layout) as LinearLayout
//        this.nameView    = itemView.findViewById(R.id.game_nav_item_header) as TextView
//        this.summaryView = itemView.findViewById(R.id.game_nav_item_summary) as TextView
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEW HOLDER
//    // -----------------------------------------------------------------------------------------
//
//    fun setNameText(nameString : String)
//    {
//        this.nameView?.text = nameString
//    }
//
//
//    fun setSummaryText(summaryString : String)
//    {
//        this.summaryView?.text = summaryString
//    }
//
//
//    fun setOnClick(onClick : View.OnClickListener)
//    {
//        this.layoutView?.setOnClickListener(onClick)
//    }
//
//}
//
//
//object OpenGameSummaryView
//{
//
//    fun view(themeId : ThemeId, context : Context) : View
//    {
//        val layout = this.viewLayout(themeId, context)
//
//        // Header
//        layout.addView(this.headerView(themeId, context))
//
//        // Summary
//        layout.addView(this.summaryView(themeId, context))
//
//        return layout
//    }
//
//
//    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.id               = R.id.game_nav_item_layout
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)
//
//        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)
//
//        layout.margin.topDp     = 6f
//
//        layout.padding.topDp    = 6f
//        layout.padding.leftDp   = 6f
//        layout.padding.rightDp  = 6f
//        layout.padding.bottomDp = 6f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun headerView(themeId : ThemeId, context : Context) : TextView
//    {
//        val header              = TextViewBuilder()
//
//        header.id               = R.id.game_nav_item_header
//
//        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        header.font             = Font.typeface(TextFont.FiraSans,
//                                                TextFontStyle.Regular,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        header.color            = ThemeManager.color(themeId, colorTheme)
//
//
//        header.sizeSp           = 17f
//
//        return header.textView(context)
//    }
//
//
//    private fun summaryView(themeId : ThemeId, context : Context) : TextView
//    {
//        val summary             = TextViewBuilder()
//
//        summary.id              = R.id.game_nav_item_summary
//
//        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
//        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        summary.font            = Font.typeface(TextFont.FiraSans,
//                                                TextFontStyle.Regular,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        summary.color           = ThemeManager.color(themeId, colorTheme)
//
//        summary.sizeSp          = 14f
//
//        return summary.textView(context)
//    }
//
//
//}
