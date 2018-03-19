
package com.kispoko.tome.activity.session


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.EntityLoader
import com.kispoko.tome.rts.session.MessageSessionLoaded
import com.kispoko.tome.rts.session.SessionId
import com.kispoko.tome.rts.session.newSession
import com.kispoko.tome.util.Util
import com.wang.avi.AVLoadingIndicatorView
import com.wang.avi.Indicator
import com.wang.avi.indicators.BallSpinFadeLoaderIndicator
import com.wang.avi.indicators.SemiCircleSpinIndicator
import java.io.Serializable



/**
 * Load Session Dialog
 */
class LoadSessionDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var loaders   : MutableList<EntityLoader> = mutableListOf()
    private var sessionId : SessionId?                = null
    private var sheetId   : SheetId?                  = null
    private var name      : String?                   = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(loaders : MutableList<EntityLoader>,
                        sessionId : SessionId,
                        sheetId : SheetId,
                        name : String) : LoadSessionDialog
        {
            val dialog = LoadSessionDialog()

            val args = Bundle()
            args.putSerializable("loaders", loaders as Serializable)
            args.putSerializable("session_id", sessionId)
            args.putSerializable("sheet_id", sheetId)
            args.putString("name", name)
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

        this.loaders   = arguments.getSerializable("loaders") as MutableList<EntityLoader>
        this.sessionId = arguments.getSerializable("session_id") as SessionId
        this.sheetId   = arguments.getSerializable("sheet_id") as SheetId
        this.name      = arguments.getString("name")

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val width  = context.resources.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width.toInt(), height)

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val sessionId = this.sessionId
        val sheetId = this.sheetId
        val name = this.name

        return if (sessionId != null && name != null && sheetId != null)
        {
            val sessionLoaderUI = SessionLoaderUI(this.loaders, sessionId, sheetId, name, this, officialThemeLight, context)
            sessionLoaderUI.view()
        }
        else
        {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT

        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }

}



class SessionLoaderUI(val loaders : MutableList<EntityLoader>,
                      val sessionId : SessionId,
                      val sheetId : SheetId,
                      val name : String,
                      val dialog : DialogFragment,
                      val theme : Theme,
                      val context : Context)
{


    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.simpleLoaderView(name))

        // Loaders
//        layout.addView(this.entityLoadersView())

        // Button
//        layout.addView(this.loadButtonView())



        Router.listen(MessageSessionLoaded::class.java)
              .subscribe(this::onLoad)


        newSession(loaders, sessionId, context)

        return layout
    }


    private fun onLoad(message : MessageSessionLoaded)
    {
        if (message.sessionId == sessionId)
        {
            val activity = context as AppCompatActivity
            val intent = Intent(activity, SheetActivity::class.java)
            intent.putExtra("sheet_id", sheetId)
            activity.startActivity(intent)
            dialog.dismiss()

        }
    }



    fun viewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor = theme.colorOrBlack(bgColorTheme)
        layout.backgroundColor = Color.WHITE

        layout.corners      = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }


    fun entityLoadersView() : LinearLayout
    {
        val layout = this.entityLoadersViewLayout()

        this.loaders.forEach {
            layout.addView(this.entityLoaderView(it))
        }

        return layout
    }


    fun entityLoadersViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        layout.margin.leftDp    = 1f
        layout.margin.rightDp    = 1f

        return layout.linearLayout(context)
    }


    private fun entityLoaderView(entityLoader : EntityLoader) : LinearLayout
    {
        val layout = this.entityLoaderViewLayout()

        // Status
        val statusLayout = this.entityLoadStatusLayout()
        layout.addView(statusLayout)


        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))

        val progressBar = ProgressBar(context)
        val doubleBounce = DoubleBounce()
        doubleBounce.color = theme.colorOrBlack(colorTheme)
        progressBar.indeterminateDrawable = doubleBounce

        statusLayout.addView(progressBar)

        // Name
        layout.addView(this.entityLoadNameView(entityLoader.label))

        return layout
    }


    private fun entityLoaderViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
//        layout.padding.leftDp    = 12f
//        layout.padding.rightDp = 12f

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.margin.topDp     = 1f


        return layout.linearLayout(context)
    }


    private fun entityLoadStatusLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.widthDp      = 17
        layout.heightDp     = 17

        layout.margin.leftDp = 12f
        layout.margin.rightDp = 12f

        return layout.linearLayout(context)
    }


    private fun entityLoadNameView(label : String) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        button.text                 = label

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        button.color                = theme.colorOrBlack(colorTheme)

        button.sizeSp               = 18f

        return button.textView(context)
    }


    private fun loadButtonView() : TextView
    {
        val button                  = TextViewBuilder()

        button.id                   = R.id.heroes_chars_item_desc

        button.width                = LinearLayout.LayoutParams.MATCH_PARENT
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        button.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        button.layoutGravity              = Gravity.CENTER
        button.gravity              = Gravity.CENTER


        button.textId               = R.string.load_session

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        button.color                = theme.colorOrBlack(colorTheme)
        button.color                = Color.WHITE

        button.sizeSp               = 19f


        button.margin.topDp         = 1f
        button.margin.leftDp        = 1f
        button.margin.rightDp       = 1f
        button.margin.bottomDp      = 1f

        button.padding.topDp        = 8f
        button.padding.bottomDp     = 8f

        return button.textView(context)
    }



    private fun simpleLoaderView(name : String) : LinearLayout
    {
        val layout = this.simpleLoaderViewLayout()

        // Status
//        val statusLayout = this.simpleLoaderStatusLayout()
//        layout.addView(statusLayout)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))

//        val progressBar = ProgressBar(context)
//        val doubleBounce = FadingCircle()
//        doubleBounce.color = theme.colorOrBlack(colorTheme)
//        progressBar.indeterminateDrawable = doubleBounce

        val indicator = AVLoadingIndicatorView(context)
        indicator.indicator = BallSpinFadeLoaderIndicator()
        indicator.setIndicatorColor(theme.colorOrBlack(colorTheme))
        indicator.scaleX = 2.5f
        indicator.scaleY = 2.5f
        indicator.setPadding(Util.dpToPixel(20f), 0, Util.dpToPixel(20f), 0)
        indicator.show()

        layout.addView(indicator)

//        layout.addView(indicator)

        // Name
        layout.addView(this.simpleLoaderNameView(name))

        return layout
    }


    private fun simpleLoaderViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f
//        layout.padding.leftDp   = 15f

        layout.backgroundColor  = Color.WHITE

//        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        return layout.linearLayout(context)
    }


    private fun simpleLoaderHeaderView(label : String) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        button.text                 = label

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        button.color                = theme.colorOrBlack(colorTheme)

        button.sizeSp               = 18f

        return button.textView(context)
    }


    private fun simpleLoaderStatusLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.leftDp = 12f
        layout.margin.rightDp = 12f

        return layout.linearLayout(context)
    }


    private fun simpleLoaderNameView(label : String) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        button.margin.leftDp        = 15f

        button.text                 = label

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        button.color                = theme.colorOrBlack(colorTheme)

        button.sizeSp               = 19f

        return button.textView(context)
    }



}

