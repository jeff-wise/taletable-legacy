
package com.taletable.android.activity.session


import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.session.Session
import com.taletable.android.rts.session.openSession
import com.taletable.android.util.Util



/**
 * Session Record Dialog
 */
class SessionRecordDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var session : Session? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(session : Session) : SessionRecordDialog
        {
            val dialog = SessionRecordDialog()

            val args = Bundle()
            args.putSerializable("session", session)
            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.session = arguments?.getSerializable("session") as Session

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val widthDp  = 300f
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(Util.dpToPixel(widthDp), height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val context = this.context
        val session = this.session

        if (session != null && context != null)
        {
            val savedSessionUI = SavedSessionRecordUI(session, officialAppThemeLight, context)
            return savedSessionUI.view()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}




class SavedSessionRecordUI(val session : Session,
                           val theme : Theme,
                           val context : Context)
{

    // | Properties
    // -----------------------------------------------------------------------------------------

//    val context = activity


    // | Views
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = savedSessionCardViewLayout()

        layout.addView(savedSessionCardHeaderView())

        layout.addView(savedSessionCardDescriptionView())

        layout.addView(savedSessionCardComponentsButtonView())

        layout.addView(openSessionButtonView())

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


    private fun savedSessionCardHeaderView() : LinearLayout
    {
        val layout = this.savedSessionCardHeaderViewLayout()

        layout.addView(this.savedSessionCardAddImageButtonView())

        layout.addView(this.savedSessionCardInfoView())

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


    private fun savedSessionCardInfoView() : LinearLayout
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


    private fun savedSessionCardDescriptionView() : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.margin.topDp       = 6f

        view.margin.leftDp        = 8f
        view.margin.rightDp       = 8f

        view.text               = session.sessionInfo.sessionSummary.value

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



    private fun openSessionButtonView() : LinearLayout
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

            //activity.selectedSession = session

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




}


