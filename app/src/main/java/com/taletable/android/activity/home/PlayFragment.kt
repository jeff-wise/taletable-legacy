
package com.taletable.android.activity.home


import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import com.taletable.android.R
import com.taletable.android.activity.session.NewSessionActivity
import com.taletable.android.db.readSessionList
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.session.Session
import com.taletable.android.rts.session.openSession
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
        return if (homeActivity is HomeActivity)
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
             val activity : HomeActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity

    var savedSessionView : ViewGroup? = null

    var sessionRecords : List<Session> = listOf()


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun showSessionCard(sessionRecord : Session)
    {
        this.savedSessionView?.removeAllViews()
        this.savedSessionView?.addView(this.loadSessionView(sessionRecord))

        val navView = activity.findViewById<LinearLayout>(R.id.sub_toolbar)
        navView.removeAllViews()

        navView.addView(this.savedSessionBackButtonView())
    }


    fun showSessionList()
    {
        this.savedSessionView?.removeAllViews()
        this.savedSessionView?.addView(this.savedSessionListRecyclerView(this.sessionRecords))

        val navView = activity.findViewById<LinearLayout>(R.id.sub_toolbar)
        navView.removeAllViews()
        navView.addView(this.savedSessionListHeaderView())
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val sessions = readSessionList(context)

        return if (sessions.isEmpty()) {
            noSessionsView()
        }
        else {
            this.sessionRecords = sessions
            activity.hasSavedSessions = true

//            val tabDivider = activity.findViewById<LinearLayout>(R.id.tab_divider)
//            tabDivider?.setBackgroundColor(Color.parseColor("#E8E8E8"))

            val _savedSessionView = savedSessionView(sessions)
            this.savedSessionView = _savedSessionView
            _savedSessionView
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

        titleView.sizeSp             = 26f

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

        layout.margin.topDp         = 20f

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

        view.sizeSp             = 17.5f

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

    private fun savedSessionView(sessionRecords : List<Session>) : LinearLayout
    {
        val layout = this.savedSessionListViewLayout()

//        layout.addView(this.savedSessionListHeaderView())

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


    fun savedSessionListHeaderView() : RelativeLayout
    {
        val layout = this.savedSessionListHeaderViewLayout()

        layout.addView(this.savedSessionListHeaderNavView())

        return layout
    }


    private fun savedSessionListHeaderViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = 46

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 16f

        return view.textView(context)
    }


    private fun savedSessionBackButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()
        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.heightDp             = 46

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.onClick              = View.OnClickListener {
            this.showSessionList()
        }

        layout.child(iconView)
              .child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 22
        iconView.heightDp           = 22

        iconView.image              = R.drawable.icon_chevron_left

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

//        iconView.margin.rightDp     = 2f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        labelView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.textId             = R.string.back_to_session_list

        labelView.font               = Font.typeface(TextFont.RobotoCondensed,
                                                     TextFontStyle.Regular,
                                                     context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        labelView.color              = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp             = 16f

        return layout.linearLayout(context)
    }


    private fun loadSessionView(session : Session) : LinearLayout
    {
        val layout = this.loadSessionViewLayout()

        layout.addView(this.savedSessionCardView(session))


        return layout
    }


    private fun loadSessionViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.margin.topDp         = 6f

        return layout.linearLayout(context)
    }



    private fun savedSessionCardView(sessionRecord : Session) : LinearLayout
    {
        val layout = this.savedSessionCardViewLayout()

        layout.addView(this.savedSessionCardHeaderView(sessionRecord))

        layout.addView(this.savedSessionCardDescriptionView(sessionRecord.sessionInfo.sessionDescription.value))

        layout.addView(this.savedSessionCardComponentsButtonView())

        layout.addView(this.openSessionButtonView(sessionRecord))

        return layout
    }


    private fun savedSessionCardViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        layout.margin.leftDp        = 4f
        layout.margin.rightDp       = 4f

        return layout.linearLayout(context)
    }


    private fun savedSessionCardHeaderView(session : Session) : LinearLayout
    {
        val layout = this.savedSessionCardHeaderViewLayout()

        layout.addView(this.savedSessionCardAddImageButtonView())

        layout.addView(this.savedSessionCardInfoView(session))

        return layout
    }


    private fun savedSessionCardHeaderViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.margin.leftDp        = 8f
        layout.margin.rightDp       = 8f

        return layout.linearLayout(context)
    }


    private fun savedSessionCardAddImageButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()
        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.widthDp              = 70
        layout.heightDp             = 70

        layout.orientation          = LinearLayout.VERTICAL

        layout.gravity              = Gravity.CENTER

        layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        layout.child(iconView)
//              .child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 22
        iconView.heightDp           = 22

        iconView.image              = R.drawable.icon_add_photo

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

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

        labelView.sizeSp             = 16f

        labelView.padding.bottomDp   = 2f

        return layout.linearLayout(context)
    }


    private fun savedSessionCardInfoView(session : Session) : LinearLayout
    {
        val layout = this.savedSessionCardInfoViewLayout()

        layout.addView(this.savedSessionCardNameView(session.sessionName.value))

        layout.addView(this.savedSessionCardSummaryView(session.sessionInfo.tagline))

        return layout
    }


    private fun savedSessionCardInfoViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.margin.leftDp        = 8f

        return layout.linearLayout(context)
    }


    private fun savedSessionCardNameView(name : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.text               = name

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 20f

        return view.textView(context)
    }


    private fun savedSessionCardSummaryView(summary : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.text               = summary

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 17f

        return view.textView(context)
    }


    private fun savedSessionCardDescriptionView(description : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.margin.topDp       = 6f

        view.margin.leftDp        = 8f
        view.margin.rightDp       = 8f

        view.text               = description

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 16f

        return view.textView(context)
    }


    private fun savedSessionCardComponentsButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()
        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.margin.topDp         = 12f
        layout.margin.bottomDp      = 12f
        layout.margin.leftDp        = 1f

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.child(iconView)
              .child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 22
        iconView.heightDp           = 22

        iconView.image              = R.drawable.icon_chevron_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

        iconView.margin.rightDp     = 2f

        iconView.padding.topDp      = 2f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        labelView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.textId             = R.string.view_other_components

        labelView.font               = Font.typeface(TextFont.RobotoCondensed,
                                                     TextFontStyle.Regular,
                                                     context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        labelView.color              = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp             = 17f

        return layout.linearLayout(context)
    }


    // VIEWS > Open Button
    // -----------------------------------------------------------------------------------------
//
//    private fun openSessionButtonView(session : Session) : LinearLayout
//    {
//        val layout = this.openSessionButtonViewLayout()
//
//        val labelView = this.openSessionButtonLabelView()
//        layout.addView(labelView)
//
//        val progressBar = this.openSessionProgressBar()
//        layout.addView(progressBar)
//        progressBar.progress = 0
//
//        layout.setOnClickListener {
//            val animation = ObjectAnimator.ofInt(progressBar, "progress", 30)
//            animation.duration = 1000
//            animation.interpolator = DecelerateInterpolator()
//            animation.start()
//
//            labelView.text = "Loading\u2026"
//
//            activity.selectedSession = session
//
//            openSession(session, context)
//        }
//
//
//        return layout
//    }



    private fun openSessionButtonView(session : Session) : LinearLayout
    {
        val layout = this.openSessionButtonViewLayout()

        val contentLayout = this.openSessionButtonContentViewLayout()

        layout.addView(contentLayout)

        val progressBar = this.openSessionProgressBar()
        contentLayout.addView(progressBar)

        progressBar.progress = 0

        val labelView = this.openSessionButtonLabelView()

        contentLayout.addView(labelView)

        layout.setOnClickListener {
            val animation = ObjectAnimator.ofInt(progressBar, "progress", 30)
            animation.duration = 1000
            animation.interpolator = DecelerateInterpolator()
            animation.start()

            labelView.text = "Loading\u2026"

            activity.selectedSession = session

            openSession(session, context)
        }


        return layout
    }


    private fun openSessionButtonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.widthDp          = 180
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.layoutGravity    = Gravity.END

        return layout.linearLayout(context)
    }

    private fun openSessionButtonContentViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.relativeLayout(context)
    }


    private fun openSessionProgressBar() : ProgressBar
    {
        val bar                 = ProgressBarBuilder()

        bar.id                  = R.id.progress_bar

        bar.layoutType          = LayoutType.RELATIVE
        bar.width               = RelativeLayout.LayoutParams.MATCH_PARENT
        bar.heightDp            = 40

        bar.addRule(RelativeLayout.ALIGN_PARENT_END)

        bar.margin.rightDp      = 10f

        bar.progressDrawableId  = R.drawable.progress_bar_load_session_2

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        bar.backgroundColor = theme.colorOrBlack(bgColorTheme)

        return bar.progressBar(context)
    }


    private fun openSessionButtonLabelView() : TextView
    {
        val label               = TextViewBuilder()

        label.layoutType        = LayoutType.RELATIVE
        label.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        label.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

//        label.addRule(RelativeLayout.CENTER_VERTICAL)
        label.addRule(RelativeLayout.CENTER_IN_PARENT)

//        label.margin.leftDp     = 10f

        label.textId            = R.string.start_new_session

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        label.color             = Color.WHITE

        label.sizeSp            = 17f

        return label.textView(context)
    }


    private fun savedSessionListRecyclerView(sessionRecords : List<Session>) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
        recyclerView.backgroundColor    = theme.colorOrBlack(colorTheme)

        recyclerView.adapter            = SavedSessionsRecyclerViewAdapater(sessionRecords, this, theme, context)

        recyclerView.padding.bottomDp   = 80f
        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }


}


class SavedSessionsRecyclerViewAdapater(private val records : List<Session>,
                                        private val playUI : PlayUI,
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

        record.timeLastUsed.doMaybe {
            viewHolder.setDate(it)
        }
        viewHolder.setNameText(record.sessionName.value)

        viewHolder.setOnClick(View.OnClickListener {
            playUI.showSessionCard(record)
        })
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

    var layout      : ViewGroup?  = null
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

    fun setOnClick(onClick : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClick)
    }


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

        layout.id               = R.id.session_record_layout

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
