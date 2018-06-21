
package com.kispoko.tome.activity.home


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight



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
            playUI.noSessionsView()
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
    // VIEWS
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

        titleView.font              = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        titleView.gravity           = Gravity.CENTER_HORIZONTAL

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        titleView.color              = theme.colorOrBlack(nameColorTheme)

        titleView.sizeSp             = 23f

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

        layout.margin.leftDp        = 8f
        layout.margin.rightDp       = 8f

        layout.elevation            = 7f

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_85"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.child(labelView)
              .child(iconView)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = "Start a New Session"

        labelView.font              = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        labelView.color             = Color.WHITE

        labelView.sizeSp            = 22f

        labelView.margin.rightDp    = 4f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 29
        iconView.heightDp           = 29

        iconView.image              = R.drawable.icon_arrow_right

        iconView.color              = Color.WHITE

        iconView.margin.topDp       = 2f

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
                                       "1st Level Human Rogue")
        layout.addView(casmeySessionButtonView)

        val coreRulesSessionButtonView =
                this.sessionButtonView("BOOK",
                                       "Book",
                                       R.drawable.icon_book,
                                       "5th SRD Core Rulebook",
                                       "5th Ed Open Rules and Data")
        layout.addView(coreRulesSessionButtonView)

        val npcSessionButtonView =
                this.sessionButtonView("NPC",
                                       "Sheet",
                                       R.drawable.icon_document,
                                       "Mirana O'dara",
                                       "CR 1/2 Human Chef")
        layout.addView(npcSessionButtonView)

        val bearSessionButtonView =
                this.sessionButtonView("CREATURE",
                                       "Sheet",
                                       R.drawable.icon_document,
                                       "Brown Bear",
                                       "CR 1 Large Beast")
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

        view.font               = Font.typeface(TextFont.default(),
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

        layout.addView(this.sessionButtonLeftView(sessionType, entityType, entityTypeIconId))

        layout.addView(this.sessionButtonDividerView())

        layout.addView(this.sessionButtonRightView(sessionName, sessionDescription))

        return layout
    }


    private fun sessionButtonViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = 70

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


    private fun sessionButtonLeftView(sessionType : String,
                                      entityType : String,
                                      entityIconId : Int) : RelativeLayout
    {
        val layout = this.sessionButtonLeftViewLayout()

        layout.addView(this.sessionButtonTypeView(sessionType))

        layout.addView(this.sessionButtonEntityView(entityType, entityIconId))

        return layout
    }



    private fun sessionButtonLeftViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 1f

        layout.margin.rightDp      = 8f
        layout.margin.leftDp       = 8f

        return layout.relativeLayout(context)
    }


    private fun sessionButtonTypeView(typeString : String) : TextView
    {
        val view                = TextViewBuilder()

        view.layoutType         = LayoutType.RELATIVE
        view.width              = RelativeLayout.LayoutParams.WRAP_CONTENT
        view.height             = RelativeLayout.LayoutParams.WRAP_CONTENT

        view.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        view.addRule(RelativeLayout.CENTER_HORIZONTAL)

        view.text               = typeString.toUpperCase()

        view.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        view.gravity            = Gravity.CENTER_HORIZONTAL

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        if (typeString.length <= 4)
            view.sizeSp             = 18f
        else
            view.sizeSp             = 16f

        view.margin.topDp       = 4f

        return view.textView(context)
    }


    private fun sessionButtonEntityView(name : String, iconId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val labelView               = TextViewBuilder()
        val iconView                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------


        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER

        layout.padding.topDp        = 3f
        layout.padding.bottomDp     = 3f
        layout.padding.leftDp       = 5f
        layout.padding.rightDp      = 5f

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layout.addRule(RelativeLayout.CENTER_HORIZONTAL)

        layout.margin.bottomDp      = 4f

        layout.child(iconView)
              .child(labelView)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = name

        labelView.font              = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        labelView.color             = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 14.5f

        labelView.margin.leftDp     = 3f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 14
        iconView.heightDp           = 14

        iconView.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

        iconView.margin.topDp       = 1f

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

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 3.2f

        layout.orientation          = LinearLayout.VERTICAL

        layout.margin.topDp         = 3f

        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        return layout.linearLayout(context)
    }


    private fun sessionButtonNameView(name : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.text               = name

        view.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 19f

        return view.textView(context)
    }


    private fun sessionButtonDescriptionView(description : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.text               = description

        view.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        view.color              = theme.colorOrBlack(nameColorTheme)

        view.sizeSp             = 17f

        return view.textView(context)
    }

}