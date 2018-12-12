
package com.taletable.android.activity.home


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    // | PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity

    // -----------------------------------------------------------------------------------------
    // | METHODS
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // | VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val scrollView = this.scrollView()

        val playViewLayout = this.playViewLayout()

        scrollView.addView(playViewLayout)

        // Sessions...
        val sessions = readSessionList(context)
        if (sessions.isEmpty())
        {
            playViewLayout.addView(newSessionButtonView())

            playViewLayout.addView(dividerView())

            playViewLayout.addView(this.headerView())
            playViewLayout.addView(this.featuredSessionsView())
        }
        else {
            //this.sessionRecords = sessions
//            activity.hasSavedSessions = true

//            val tabDivider = activity.findViewById<LinearLayout>(R.id.tab_divider)
//            tabDivider?.setBackgroundColor(Color.parseColor("#E8E8E8"))

            //val _savedSessionView = savedSessionView(sessions)
//            this.savedSessionView = _savedSessionView
//            _savedSessionView
            // noSessionsView()
        }

        return scrollView
    }


    fun scrollView() : ScrollView
    {
        val scrollView = ScrollView(context)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.MATCH_PARENT)

        scrollView.layoutParams = layoutParams

        return scrollView
    }


    private fun playViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun headerView() : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.textId             = R.string.featured_sessions_header

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 18f

        view.margin.topDp       = 12f
        view.margin.bottomDp    = 4f
        view.margin.leftDp      = 12f

        return view.textView(context)
    }


    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 2

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_10"))))
        layout.backgroundColor  = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    // | VIEWS > No Saved Sessions
    // -----------------------------------------------------------------------------------------

    private fun newSessionButtonView() : LinearLayout
    {
        val layout = this.newSessionButtonViewLayout()

        layout.addView(this.newSessionMessageView())

        layout.addView(this.newSessionButtonLabelView())

        return layout
    }


    private fun newSessionButtonViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL


        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        layout.margin.leftDp        = 12f
        layout.margin.rightDp       = 12f

        layout.margin.topDp         = 24f
        layout.margin.bottomDp      = 24f

        layout.elevation            = 7f

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        layout.backgroundColor      = Color.WHITE

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
//        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)


        layout.onClick              = View.OnClickListener {
            val intent = Intent(activity, NewSessionActivity::class.java)
            activity.startActivity(intent)
        }

        return layout.linearLayout(context)
    }



    private fun newSessionMessageView() : TextView
    {
        val titleView               = TextViewBuilder()

        titleView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        titleView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        titleView.textId            = R.string.no_sessions_message

        titleView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        titleView.gravity           = Gravity.CENTER_HORIZONTAL

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        titleView.color              = theme.colorOrBlack(nameColorTheme)

        titleView.sizeSp             = 18f

        return titleView.textView(context)
    }


    private fun newSessionButtonLabelView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val labelView               = TextViewBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.topDp         = 12f

        layout.backgroundColor      = Color.WHITE

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

        labelView.text              = "Start a New Session"

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        labelView.color         = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 19f

        labelView.margin.rightDp    = 4f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 24
        iconView.heightDp           = 24

        iconView.image              = R.drawable.icon_arrow_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

        iconView.margin.topDp       = 1f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Session Button
    // -----------------------------------------------------------------------------------------

    private fun featuredSessionsView() : HorizontalScrollView
    {
        val scrollView = this.featuredSessionsHorizontalScrollview()

        val layout = this.featuredSessionsViewLayout()

        scrollView.addView(layout)

        //layout.addView(this.headerView())

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


        return scrollView
    }


    private fun featuredSessionsHorizontalScrollview() : HorizontalScrollView
    {
        val scrollView = HorizontalScrollView(context)

        scrollView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                           FrameLayout.LayoutParams.WRAP_CONTENT)

        scrollView.isHorizontalScrollBarEnabled = false

        return scrollView
    }


    private fun featuredSessionsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.topDp     = 4f

        return layout.linearLayout(context)
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

        layout.widthDp              = 300
        layout.heightDp             = 210

        layout.margin.leftDp        = 12f
        layout.margin.bottomDp      = 6f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

        layout.elevation            = 7f

        return layout.linearLayout(context)
    }


    private fun sessionButtonRightView(name : String,
                                       description : String) : LinearLayout
    {
        val layout = this.sessionButtonRightViewLayout()

        layout.addView(this.sessionButtonNameView(name))

        layout.addView(this.sessionButtonDescriptionView(description))

        return layout
    }


    private fun sessionButtonRightViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun sessionButtonNameView(name : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.MATCH_PARENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.padding.leftDp     = 10f
        view.padding.rightDp    = 10f
        view.padding.topDp      = 20f
        view.padding.bottomDp   = 10f

        view.corners            = Corners(4.0, 4.0, 0.0, 0.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
        view.backgroundColor    = theme.colorOrBlack(bgColorTheme)

        view.text               = name

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        view.color              = theme.colorOrBlack(nameColorTheme)
        view.color              = Color.WHITE

        view.sizeSp             = 20f

        return view.textView(context)
    }


    private fun sessionButtonDescriptionView(description : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.padding.leftDp     = 8f
        view.padding.rightDp    = 8f
        view.padding.topDp      = 8f

        view.text               = description

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 17f

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

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_VERTICAL
        layout.layoutGravity        = Gravity.END

        layout.margin.rightDp       = 4f
        layout.margin.topDp         = 6f
        layout.margin.bottomDp      = 6f

//        layout.padding.topDp        = 4f
//        layout.padding.bottomDp     = 4f
//        layout.padding.leftDp       = 8f
//        layout.padding.rightDp      = 8f

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_2"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)
        layout.backgroundColor      = Color.WHITE

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_2"))))
        labelView.color             = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 15f

        return layout.linearLayout(context)
    }


    // VIEWS > Saved Sessions
    // -----------------------------------------------------------------------------------------
//
//    private fun savedSessionView(sessionRecords : List<Session>) : LinearLayout
//    {
//        val layout = this.savedSessionListViewLayout()
//
////        layout.addView(this.savedSessionListHeaderView())
//
//        layout.addView(this.savedSessionListRecyclerView(sessionRecords))
//
//        return layout
//    }
//
//
//    private fun savedSessionListViewLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation          = LinearLayout.VERTICAL
//
//        return layout.linearLayout(context)
//    }
//
//
//    fun savedSessionListHeaderView() : RelativeLayout
//    {
//        val layout = this.savedSessionListHeaderViewLayout()
//
//        layout.addView(this.savedSessionListHeaderNavView())
//
//        return layout
//    }
//
//
//    private fun savedSessionListHeaderViewLayout() : RelativeLayout
//    {
//        val layout                  = RelativeLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp             = 46
//
//        layout.padding.leftDp       = 10f
//        layout.padding.rightDp      = 10f
//
//        return layout.relativeLayout(context)
//    }
//
//
//    private fun savedSessionListHeaderNavView() : LinearLayout
//    {
//        val layout = this.savedSessionListHeaderNavViewLayout()
//
//        val titleView = this.savedSessionListHeaderTitleView()
//        layout.addView(titleView)
//
//        return layout
//    }
//
//
//    private fun savedSessionListHeaderNavViewLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.layoutType           = LayoutType.RELATIVE
//        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
//        layout.addRule(RelativeLayout.CENTER_VERTICAL)
//
//        layout.margin.bottomDp      = 4f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun savedSessionListHeaderTitleView() : TextView
//    {
//        val view                = TextViewBuilder()
//
//        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        view.textId             = R.string.saved_sessions
//
//        view.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Regular,
//                                                context)
//
//        val nameColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        view.color              = theme.colorOrBlack(nameColorTheme)
//
//        view.sizeSp             = 16f
//
//        return view.textView(context)
//    }
//
//
//    private fun savedSessionBackButtonView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout                  = LinearLayoutBuilder()
//        val iconView                = ImageViewBuilder()
//        val labelView               = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.heightDp             = 46
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.gravity              = Gravity.CENTER_VERTICAL
//
//        layout.onClick              = View.OnClickListener {
//            this.showSessionList()
//        }
//
//        layout.child(iconView)
//              .child(labelView)
//
//        // (3 A) Icon
//        // -------------------------------------------------------------------------------------
//
//        iconView.widthDp            = 22
//        iconView.heightDp           = 22
//
//        iconView.image              = R.drawable.icon_chevron_left
//
//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        iconView.color              = theme.colorOrBlack(iconColorTheme)
//
////        iconView.margin.rightDp     = 2f
//
//        // (3 B) Label
//        // -------------------------------------------------------------------------------------
//
//        labelView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        labelView.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        labelView.textId             = R.string.back_to_session_list
//
//        labelView.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                     TextFontStyle.Regular,
//                                                     context)
//
//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        labelView.color              = theme.colorOrBlack(labelColorTheme)
//
//        labelView.sizeSp             = 16f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun loadSessionView(session : Session) : LinearLayout
//    {
//        val layout = this.loadSessionViewLayout()
//
//        layout.addView(this.savedSessionCardView(session))
//
//        return layout
//    }
//
//
//    private fun loadSessionViewLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation          = LinearLayout.VERTICAL
//
//        layout.margin.topDp         = 6f
//
//        return layout.linearLayout(context)
//    }
//



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




}


