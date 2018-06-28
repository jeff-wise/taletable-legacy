
package com.kispoko.tome.activity.home


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.session.NewSessionActivity
import com.kispoko.tome.db.loadSessionList
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.session.SessionRecord
import java.text.SimpleDateFormat
import java.util.*



class PlayFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance() : PlayFragment
        {
            val fragment = PlayFragment()

//            val args = Bundle()
//            args.putSerializable("sheet_id", sheetId)
//            taskFragment.arguments = args

            return fragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val homeActivity = activity
        return if (homeActivity != null)
        {
            val playUI = PlayUI(officialAppThemeLight, homeActivity)
            playUI.view()
        }
        else
        {
            null
        }
    }

}



class PlayUI(val theme : Theme,
             val activity : FragmentActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity

    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val sessions = loadSessionList(context)

        return if (sessions.isEmpty()) {

            noSessionsView()
        }
        else {
            val tabDivider = activity.findViewById<LinearLayout>(R.id.tab_divider)
            tabDivider?.setBackgroundColor(Color.parseColor("#E8E8E8"))

            savedSessionListView(sessions)
        }
    }

    // VIEWS > No Saved Sessions
    // -----------------------------------------------------------------------------------------

    fun noSessionsView() : View
    {
        val scrollView = this.noSessionsScrollView()

        val layout = this.noSessionsViewLayout()
        layout.addView(this.noSessionsMessageView())
        layout.addView(this.newSessionButtonView())

        layout.addView(this.featuredSessionsView())

        scrollView.addView(layout)

        return scrollView
    }


    fun noSessionsScrollView() : ScrollView
    {
        val scrollView = ScrollView(context)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.MATCH_PARENT)

        scrollView.layoutParams = layoutParams

        return scrollView
    }


    private fun noSessionsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER

        layout.padding.topDp    = 30f

        layout.padding.bottomDp = 30f

        return layout.linearLayout(context)
    }


    private fun noSessionsMessageView() : TextView
    {
        val titleView               = TextViewBuilder()

        titleView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        titleView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        titleView.margin.leftDp     = 12f
        titleView.margin.rightDp    = 12f

        titleView.textId            = R.string.no_sessions_message

        titleView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Light,
                                                    context)

        titleView.gravity           = Gravity.CENTER_HORIZONTAL

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        titleView.color              = theme.colorOrBlack(nameColorTheme)

        titleView.sizeSp             = 28f

        return titleView.textView(context)
    }


    private fun newSessionButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val labelView               = TextViewBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER

        layout.margin.topDp         = 15f

        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        layout.margin.leftDp        = 12f
        layout.margin.rightDp       = 12f

        layout.elevation            = 7f

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_85"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.onClick              = View.OnClickListener {
            val intent = Intent(activity, NewSessionActivity::class.java)
            activity.startActivity(intent)
        }

        layout.child(labelView)
              .child(iconView)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = "Start a New Session".toUpperCase()

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        labelView.color             = Color.WHITE

        labelView.sizeSp            = 18f

        labelView.margin.rightDp    = 4f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 29
        iconView.heightDp           = 29

        iconView.image              = R.drawable.icon_arrow_right

        iconView.color              = Color.WHITE

        iconView.margin.topDp       = 1f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Session Button
    // -----------------------------------------------------------------------------------------

    private fun featuredSessionsView() : LinearLayout
    {
        val layout = this.featuredSessionsViewLayout()

        layout.addView(this.featuredSessionsHeaderView())

        val casmeySessionButtonView =
                this.sessionButtonView("PC",
                                       "Sheet",
                                       R.drawable.icon_document,
                                       "Casmey Dalseya",
                                       "1st Level Human Rogue ready-to-play character with a sharp sword and plenty of swashbuckling sass.")
        layout.addView(casmeySessionButtonView)

        val coreRulesSessionButtonView =
                this.sessionButtonView("BOOK",
                                       "Book",
                                       R.drawable.icon_book,
                                       "5th SRD Core Rulebook",
                                       "Rulebook containing the 5th Edition open content for the world's most popular RPG.")
        layout.addView(coreRulesSessionButtonView)

        val npcSessionButtonView =
                this.sessionButtonView("NPC",
                                       "Sheet",
                                       R.drawable.icon_document,
                                       "Mirana O'dara",
                                       "Female human chef known for her hot, savory food as well as her cold, salty demeanor.")
        layout.addView(npcSessionButtonView)

        val bearSessionButtonView =
                this.sessionButtonView("CREATURE",
                                       "Sheet",
                                       R.drawable.icon_document,
                                       "Brown Bear",
                                       "Large animal found in temperate forests.")
        layout.addView(bearSessionButtonView)


        return layout
    }


    private fun featuredSessionsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        val bColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//        view.color              = theme.colorOrBlack(nameColorTheme)

        layout.margin.topDp     = 40f
        layout.margin.leftDp    = 8f
        layout.margin.rightDp   = 8f

        return layout.linearLayout(context)
    }


    private fun featuredSessionsHeaderView() : TextView
    {
        val view                = TextViewBuilder()

        view.layoutType         = LayoutType.RELATIVE
        view.width              = RelativeLayout.LayoutParams.WRAP_CONTENT
        view.height             = RelativeLayout.LayoutParams.WRAP_CONTENT

        view.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        view.addRule(RelativeLayout.CENTER_HORIZONTAL)

        view.textId             = R.string.featured_sessions_header

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        view.gravity            = Gravity.CENTER_HORIZONTAL

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 17f

        view.margin.bottomDp    = 4f

        return view.textView(context)
    }


    private fun sessionButtonView(sessionType : String,
                                  entityType : String,
                                  entityTypeIconId : Int,
                                  sessionName : String,
                                  sessionDescription : String) : LinearLayout
    {
        val layout = this.sessionButtonViewLayout()

//        layout.addView(this.sessionButtonLeftView(sessionType, entityType, entityTypeIconId))
//
//        layout.addView(this.sessionButtonDividerView())

        layout.addView(this.sessionButtonRightView(sessionName, sessionDescription))

        return layout
    }


    private fun sessionButtonViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.margin.topDp         = 2.5f

        return layout.linearLayout(context)
    }


    private fun sessionButtonDividerView() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.widthDp              = 1
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor      = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    private fun sessionButtonRightView(name : String,
                                       description : String) : LinearLayout
    {
        val layout = this.sessionButtonRightViewLayout()

        layout.addView(this.sessionButtonNameView(name))

        layout.addView(this.sessionButtonDescriptionView(description))

        layout.addView(this.featuredSessionOpenButtonView())

        return layout
    }


    private fun sessionButtonRightViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f

        return layout.linearLayout(context)
    }


    private fun sessionButtonNameView(name : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.text               = name

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.SemiBold,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 18.5f

        return view.textView(context)
    }


    private fun sessionButtonDescriptionView(description : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.text               = description

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 18f

        return view.textView(context)
    }


    private fun featuredSessionOpenButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_VERTICAL or Gravity.END

        layout.margin.rightDp       = 4f
        layout.margin.topDp         = 6f
        layout.margin.bottomDp      = 4f

        layout.child(labelView)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = context.getString(R.string.open_session).toUpperCase()

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
        labelView.color             = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 15.5f

        return layout.linearLayout(context)
    }


    // VIEWS > Saved Sessions
    // -----------------------------------------------------------------------------------------

    private fun savedSessionListView(sessionRecords : List<SessionRecord>) : LinearLayout
    {
        val layout = this.savedSessionListViewLayout()

        layout.addView(this.savedSessionListHeaderView())

        layout.addView(this.savedSessionListRecyclerView(sessionRecords))

        return layout
    }


    private fun savedSessionListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun savedSessionListHeaderView() : LinearLayout
    {
        val layout = this.savedSessionListHeaderViewMainLayout()

        layout.addView(this.savedSessionListHeaderContentView())

        layout.addView(this.savedSessionListHeaderBottomBorderView())

        return layout
    }


    private fun savedSessionListHeaderViewMainLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.backgroundColor      = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun savedSessionListHeaderBottomBorderView() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_10"))))
        layout.backgroundColor      = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    private fun savedSessionListHeaderContentView() : ViewGroup
    {
        val layout = this.savedSessionListHeaderContentViewLayout()

        layout.addView(this.savedSessionListHeaderNavView())

        layout.addView(this.savedSessionListNewSessionButtonView())

        return layout
    }


    private fun savedSessionListHeaderContentViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = 46

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
//        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        return layout.relativeLayout(context)
    }


    private fun savedSessionListHeaderNavView() : LinearLayout
    {
        val layout = this.savedSessionListHeaderNavViewLayout()

        val titleView = this.savedSessionListHeaderTitleView()
        layout.addView(titleView)

        return layout
    }


    private fun savedSessionListHeaderNavViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.margin.bottomDp      = 4f

        return layout.linearLayout(context)
    }


    private fun savedSessionListHeaderTitleView() : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.textId             = R.string.saved_sessions

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 18f

        return view.textView(context)
    }


    private fun savedSessionListNewSessionButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()
        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

//        layout.padding.leftDp       = 4f
//        layout.padding.rightDp      = 7f
//        layout.padding.topDp        = 4f
//        layout.padding.bottomDp     = 4f
//
        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        layout.backgroundColor      = Color.WHITE

        layout.margin.bottomDp      = 2f

        layout.child(iconView)
              .child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 18
        iconView.heightDp           = 18

        iconView.image              = R.drawable.icon_plus_sign

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

        iconView.margin.rightDp     = 2f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        labelView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.textId             = R.string.new_session

        labelView.font               = Font.typeface(TextFont.RobotoCondensed,
                                                     TextFontStyle.Regular,
                                                     context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        labelView.color              = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp             = 18f

        labelView.padding.bottomDp   = 2f

        return layout.linearLayout(context)
    }




    private fun savedSessionListRecyclerView(sessionRecords : List<SessionRecord>) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
        recyclerView.backgroundColor    = theme.colorOrBlack(colorTheme)

        recyclerView.adapter            = SavedSessionsRecyclerViewAdapater(sessionRecords, theme, context)

        recyclerView.padding.bottomDp   = 80f
        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }


}


class SavedSessionsRecyclerViewAdapater(private val records : List<SessionRecord>,
                                        private val theme : Theme,
                                        private val context : Context)
                                         : RecyclerView.Adapter<SessionRecordViewHolder>()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SessionRecordViewHolder
    {
        val itemView = SessionRecordView.view(theme, context)
        return SessionRecordViewHolder(itemView, context)
    }


    override fun onBindViewHolder(viewHolder : SessionRecordViewHolder, position : Int)
    {
        val record = this.records[position]

        viewHolder.setDate(record.lastUsed)
        viewHolder.setNameText(record.sessionName.value)
    }


    override fun getItemCount() : Int = this.records.size

}




/**
 * Session Record View Holder
 */
class SessionRecordViewHolder(itemView : View,
                              val context : Context)
                : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout?  = null
    var dateView    : TextView?  = null
    var nameView    : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout     = itemView.findViewById(R.id.session_record_layout)
        this.dateView   = itemView.findViewById(R.id.session_record_last_used)
        this.nameView   = itemView.findViewById(R.id.session_record_name)
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun setNameText(name : String)
    {
        this.nameView?.text = name
    }


    fun setDate(calendar : Calendar)
    {
        val format = SimpleDateFormat("EEEE, MMMM dd")
        this.dateView?.text = format.format(calendar.time)
    }


}



object SessionRecordView
{


    fun view(theme : Theme, context : Context) : View
    {
        val layout = this.viewLayout(context)

        layout.addView(this.infoView(theme, context))

        layout.addView(this.buttonView(theme, context))

        return layout
    }


    private fun viewLayout(context : Context) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 2f
        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        return layout.relativeLayout(context)
    }


    private fun infoView(theme : Theme, context : Context) : LinearLayout
    {
        val layout = this.infoViewLayout(context)

        layout.addView(this.dateView(theme, context))

        layout.addView(this.nameView(theme, context))

        return layout
    }


    private fun infoViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun dateView(theme : Theme, context : Context) : TextView
    {
        val view                = TextViewBuilder()

        view.id                 = R.id.session_record_last_used

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 16.5f

        return view.textView(context)
    }


    private fun nameView(theme : Theme, context : Context) : TextView
    {
        val view                = TextViewBuilder()

        view.id                 = R.id.session_record_name

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 19f

        return view.textView(context)
    }


    private fun buttonView(theme: Theme, context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val iconView            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.child(iconView)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp        = 37
        iconView.heightDp       = 37

        iconView.image          = R.drawable.icon_chevron_right

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_24"))))
        iconView.color          = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


}
