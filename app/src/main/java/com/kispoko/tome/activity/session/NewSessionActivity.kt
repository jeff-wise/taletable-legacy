
package com.kispoko.tome.activity.session


import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.official.GameSummary
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.session.*
import com.kispoko.tome.util.configureToolbar
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.maybe
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.util.Log
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.model.entity.entityManifest
import com.kispoko.tome.model.session.sessionManifest
import com.kispoko.tome.official.gameManifest
import com.kispoko.tome.rts.entity.EntityId
import io.reactivex.disposables.CompositeDisposable
import java.util.*



/**
 * New Session Activity
 * Session here refers to a saved session, not necessarily creating a new Session object.
 * It is creating a new session from the user's perspective.
 */
class NewSessionActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var step : Int = 1

    private val defaultGameId = EntityId(UUID.fromString("85f6da8e-e497-4aa2-a3c3-c2eab32d2839"))


    private var newSessionUI : NewSessionUI? = null

    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_open_session)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("step"))
            this.step = this.intent.getIntExtra("step", 1)

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.new_session), TextFont.RobotoCondensed, TextFontStyle.Regular)

        this.applyTheme(com.kispoko.tome.model.theme.official.officialAppThemeLight)

        this.initializeListeners()

        this.setStep(1, this.defaultGameId, Nothing())
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }



    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    fun setStep(step : Int, gameId : EntityId, maybeSessionId : Maybe<SessionId>)
    {
        this.step = step

        if (this.newSessionUI == null) {
            this.newSessionUI = NewSessionUI(step,
                                             gameId,
                                             maybeSessionId,
                                             officialAppThemeLight,
                                             this)
        }

        newSessionUI?.setStep(step)
        newSessionUI?.render()
    }


    private fun applyTheme(theme : Theme)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        val toolbarBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))

        // Toolbar > Background
//        toolbar.setBackgroundColor(theme.colorOrBlack(toolbarBgColorTheme))
        toolbar.setBackgroundColor(Color.WHITE)

        // Toolbar > Icons
        val toolbarIconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_close_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(theme.colorOrBlack(toolbarIconColorTheme), PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(theme.colorOrBlack(toolbarIconColorTheme), PorterDuff.Mode.SRC_IN)

    }


    private fun initializeListeners()
    {
        val newSessionMessageDisposable = Router.listen(NewSessionMessage::class.java)
                                                .subscribe(this::onMessage)
        this.messageListenerDisposable.add(newSessionMessageDisposable)

        val sessionMessageDisposable = Router.listen(MessageSessionLoad::class.java)
                                             .subscribe(this::onSessionLoadMessage)
        this.messageListenerDisposable.add(sessionMessageDisposable)
    }


    private fun onMessage(message : NewSessionMessage)
    {
        when (message)
        {
            is NewSessionMessageGame    ->
            {
                this.newSessionUI?.updateGame(message.gameId)
            }
            is NewSessionMessageSession ->
            {
                this.newSessionUI?.updateSession(message.sessionId)
            }
        }
    }


    private fun onSessionLoadMessage(message : MessageSessionLoad)
    {
        when (message)
        {
            is MessageSessionEntityLoaded ->
            {
                this.newSessionUI?.updateLoadProgress(message.update)
            }
            is MessageSessionLoaded ->
            {
                this.newSessionUI?.startSession(message.sessionId)

            }
        }
    }

}


// ---------------------------------------------------------------------------------------------
// MESSAGE
// ---------------------------------------------------------------------------------------------

sealed class NewSessionMessage

data class NewSessionMessageGame(val gameId : EntityId) : NewSessionMessage()

data class NewSessionMessageSession(val sessionId : SessionId) : NewSessionMessage()


// ---------------------------------------------------------------------------------------------
// UI
// ---------------------------------------------------------------------------------------------

class NewSessionUI(private var step : Int,
                   private var gameId : EntityId,
                   private var sessionId : Maybe<SessionId>,
                   private val theme : Theme,
                   private val newSessionActivity : NewSessionActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = newSessionActivity

    fun gameSummary() : Maybe<GameSummary> =
        maybe(gameManifest(context)?.game(this.gameId))


    private fun sessionLoader() : Maybe<Session>
    {
        val sessionId = this.sessionId
        Log.d("***NEW SESSION ACTIVITY", "session loader: $sessionId")
        return when (sessionId) {
            is Just    -> {
                sessionManifest(context).apply { it.session(sessionId.value) }
            }
            is Nothing -> {
                gameSummary().apply { gameSummary ->
                    sessionManifest(context).apply { it.session(gameSummary.defaultSessionId) }
                }
            }
        }
    }


//    private fun entityKindName() : String
//    {
//        val maybeName = this.gameSummary().apply { gameSummary ->
//                        this.sessionLoader().apply { sessionLoader ->
//                        gameSummary.entityKind(sessionLoader.entityKindId).apply {
//                            Just(it.name)
//                         } } }
//        return when (maybeName) {
//            is Just    -> maybeName.value
//            is Nothing -> ""
//        }
//    }


    // PROPERTIES > Views
    // -----------------------------------------------------------------------------------------

    private var progressBar : ProgressBar? = null
    private var openButtonLabelView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun setStep(step : Int)
    {
        this.step = step
    }


    fun render()
    {
        val contentView = newSessionActivity.findViewById<LinearLayout>(R.id.content)
//        val footerView = newSessionActivity.findViewById<LinearLayout>(R.id.footer)

        contentView?.removeAllViews()
        contentView?.let {
            it.addView(this.view())
        }

//        footerView?.removeAllViews()
//        footerView?.let {
//            it.addView(this.footerView())
//        }
    }


    fun updateGame(gameId : EntityId)
    {
        this.gameId = gameId

        this.render()
    }


    fun updateSession(sessionId : SessionId)
    {
        this.sessionId = Just(sessionId)

        Log.d("***NEW SESSION ACTIVITY", "update session id: ${this.sessionId}")

        this.render()
    }


    fun updateLoadProgress(sessionLoadUpdate : SessionLoadUpdate)
    {
        this.progressBar?.let { bar ->
            val updateAmount = bar.progress + (72 / sessionLoadUpdate.totalEntities)
            bar.progress = updateAmount
        }
    }


    fun startSession(sessionId : SessionId)
    {
        this.sessionLoader().doMaybe {
            if (it.sessionId == sessionId)
            {
                val mainEntityId = it.mainEntityId
                val activity = context as AppCompatActivity
                val intent = Intent(activity, SheetActivity::class.java)
                intent.putExtra("sheet_id", mainEntityId)
                activity.startActivity(intent)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.openSessionViewLayout()

        layout.addView(this.stepView(1, R.string.game))

        layout.addView(this.stepView(2, R.string.session))

        layout.addView(this.stepView(3, R.string.review))

//        layout.addView(this.openSessionButtonView())

        return layout
    }


    private fun openSessionViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.bottomDp = 20f

        return layout.linearLayout(context)
    }


    private fun stepView(step : Int, labelId : Int) : LinearLayout
    {
        val layout = this.stepViewLayout()

        layout.addView(this.titleView(step, context.getString(labelId)))

        when (step)
        {
            1 -> {
                val gameName = this.gameSummary().toNullable()?.name ?: ""
                layout.addView(this.descriptionView(R.string.choose_game_description))
                layout.addView(this.selectionView(gameName))
            }
            2 -> {
                val sessionName = this.sessionLoader().toNullable()?.sessionName?.value ?: ""
                val sessionLoader = this.sessionLoader()
                when (sessionLoader) {
                    is Nothing -> {
                        Log.d("***NEW SESSION ACTIVITY", "session loader is null")
                    }
                }
                layout.addView(this.descriptionView(R.string.choose_session_description))
                layout.addView(this.selectionView(sessionName))
//                layout.addView(this.chooseButtonView(2, R.string.select))
            }
            3 -> {
                layout.addView(this.descriptionView(R.string.this_session_contains))
                layout.addView(this.entityLoadersView())
                layout.addView(this.loadView())
            }
        }


//        layout.addView(this.selectionView(step))

        return layout
    }


    private fun stepViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        layout.margin.leftDp    = 4f
//        layout.margin.rightDp   = 4f
        layout.margin.topDp     = 1f

        layout.padding.leftDp   = 9f
        layout.padding.rightDp  = 8f
//        layout.padding.topDp    = 14f
//        layout.padding.bottomDp = 16f

//        layout.backgroundColor  = Color.WHITE

        val bglColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_9"))))
        layout.backgroundColor  = theme.colorOrBlack(bglColorTheme)

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        return layout.linearLayout(context)
    }



    // VIEWS > Title
    // -----------------------------------------------------------------------------------------

    private fun titleView(step : Int, title : String) : RelativeLayout
    {
        val layout = this.titleViewLayout()

        // Label
        layout.addView(this.titleTextView(step, title))

        // Choose Button
//        layout.addView(this.chooseButtonView(step, R.string.choose))

        return layout
    }


    private fun titleViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.relativeLayout(context)
    }


    private fun titleTextView(step : Int, titleString : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val stepView            = TextViewBuilder()
        val labelView           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.leftDp    = 3f

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.child(stepView)
              .child(labelView)

        // (3 A) Index
        // -------------------------------------------------------------------------------------

        stepView.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        stepView.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        stepView.backgroundResource    = R.drawable.bg_session_step

        stepView.color                 = Color.WHITE

        stepView.font                  = Font.typeface(TextFont.RobotoCondensed,
                                                       TextFontStyle.SemiBold,
                                                       context)

        stepView.text                  = step.toString()

        stepView.gravity               = Gravity.CENTER

        stepView.sizeSp                = 16f

        stepView.margin.rightDp        = 10f

        stepView.padding.bottomDp       = 1f
        stepView.margin.topDp          = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.MATCH_PARENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = titleString

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))

        labelView.color             = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 19f

        labelView.padding.bottomDp  = 2f
        labelView.padding.topDp     = 12f
        labelView.padding.leftDp    = 10f

        labelView.backgroundColor   = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun descriptionView(descriptionStringId : Int) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.MATCH_PARENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        view.backgroundColor   = Color.WHITE

        view.margin.leftDp      = 37f
        view.padding.leftDp     = 10f

        view.textId             = descriptionStringId

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        view.color              = theme.colorOrBlack(colorTheme)

        view.sizeSp             = 17f

        view.padding.bottomDp   = 12f

        return view.textView(context)
    }


//    private fun chooseButtonView(step : Int, labelId : Int) : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val iconView            = ImageViewBuilder()
//        val labelView           = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.layoutType       = LayoutType.RELATIVE
//        layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT
//        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.margin.rightDp   = 8f
//        layout.margin.topDp     = 8f
//        layout.margin.leftDp    = 42f
////
////        layout.padding.topDp    = 4f
////        layout.padding.bottomDp    = 4f
////        layout.padding.leftDp    = 6f
////        layout.padding.rightDp    = 6f
//
////        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)
//
//        layout.addRule(RelativeLayout.CENTER_VERTICAL)
//
//        layout.onClick          = View.OnClickListener {
//            when (step)
//            {
//                // Choose the game
//                1 ->
//                {
//                    val intent = Intent(newSessionActivity, GamesListActivity::class.java)
//                    intent.putExtra("game_action", GameActionLoadSession)
//                    newSessionActivity.startActivity(intent)
//                }
//            }
//        }
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
////        layout.backgroundColor   = theme.colorOrBlack(bgColorTheme)
//
//        layout.child(labelView)
////              .child(iconView)
//
//        // (3) Icon
//        // -------------------------------------------------------------------------------------
//
//        iconView.widthDp            = 24
//        iconView.heightDp           = 24
//
//        iconView.image              = R.drawable.icon_arrow_right
//
//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_green"))))
//        iconView.color              = theme.colorOrBlack(iconColorTheme)
//
//        iconView.margin.leftDp      = 4f
//        iconView.padding.topDp      = 2f
//
//        // (4) Label
//        // -------------------------------------------------------------------------------------
//
//        labelView.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
//        labelView.height                = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        labelView.text                  = context.getString(labelId).toUpperCase()
//
//        labelView.font                  = Font.typeface(TextFont.RobotoCondensed,
//                                                        TextFontStyle.Bold,
//                                                        context)
//
//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
//        labelView.color                 = theme.colorOrBlack(labelColorTheme)
//
//        labelView.sizeSp                = 15f
//
//        labelView.padding.bottomDp      = 1f
//
//        return layout.linearLayout(context)
//    }


    // VIEWS > Choose Game View
    // -----------------------------------------------------------------------------------------

//    private fun selectionView(step : Int) : LinearLayout
//    {
//        val layout = this.selectionViewLayout(step)
//
//        when (step)
//        {
//            1 -> {
//                layout.addView(this.gameSelectionView())
//            }
//            2 -> {
//                layout.addView(this.sessionNameView())
//            }
//        }
//
//        return layout
//    }
//
//
//    private fun selectionViewLayout(step : Int) : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.margin.topDp     = 6f
//
//        layout.onClick          = View.OnClickListener {
//            when (step)
//            {
//                // Choose the game
//                1 ->
//                {
//                    val intent = Intent(newSessionActivity, GamesListActivity::class.java)
//                    intent.putExtra("game_action", GameActionLoadSession)
//                    newSessionActivity.startActivity(intent)
//                }
//                2 ->
//                {
//                    val intent = Intent(newSessionActivity, EntityTypeListActivity::class.java)
//                    intent.putExtra("game_id", this.gameId)
//                    newSessionActivity.startActivity(intent)
//                }
//            }
//        }
//
//        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_3"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        return layout.linearLayout(context)
//    }


    private fun selectionView(value : String) : LinearLayout
    {
        val layout = this.selectionViewLayout()

        layout.addView(this.selectionIconView())

        layout.addView(this.selectionButtonView(value))

        return layout
    }


    private fun selectionViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    private fun selectionIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.leftDp    = 1f
        layout.margin.topDp     = 1f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 27
        icon.heightDp           = 27

        icon.image              = R.drawable.icon_question_filled

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }


    private fun selectionButtonView(value : String) : LinearLayout
    {
        val layout = this.selectionButtonViewLayout()

        layout.addView(this.selectionButtonMainView(value))

        return layout
    }


    private fun selectionButtonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = Color.WHITE

        layout.margin.leftDp    = 9f

        layout.padding.bottomDp = 14f
        layout.padding.topDp    = 14f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 8f

        return layout.linearLayout(context)
    }



    private fun selectionButtonMainView(value : String) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
        name.backgroundColor    = theme.colorOrBlack(bgColorTheme)

        name.padding.leftDp     = 8f
        name.padding.rightDp    = 10f
        name.padding.topDp      = 6f
        name.padding.bottomDp   = 6f

        name.corners            = Corners(1.0, 1.0, 1.0, 1.0)

        name.elevation          = 6

        name.text               = value

        name.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
//        name.color              = theme.colorOrBlack(textColorTheme)
        name.color              = Color.WHITE

        name.sizeSp             = 17f

        return name.textView(context)
    }


    private fun loadView() : LinearLayout
    {
        val layout = this.loadViewLayout()

        layout.addView(this.loadIconView())

        layout.addView(this.openSessionButtonView())

        return layout
    }


    private fun loadViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.topDp     = 1f

        return layout.linearLayout(context)
    }


    private fun loadIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.leftDp    = 2f
        layout.margin.topDp     = 1f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 27
        icon.heightDp           = 27

        icon.image              = R.drawable.icon_publish

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_90"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }


    private fun openSessionButtonView() : LinearLayout
    {
        val layout = this.openSessionButtonViewLayout()

        val contentLayout = this.openSessionButtonContentViewLayout()

        layout.addView(contentLayout)

        val progressBar = this.openSessionProgressBar()
        this.progressBar = progressBar
        contentLayout.addView(progressBar)

        progressBar.progress = 0

        val labelView = this.openSessionButtonLabelView()
        this.openButtonLabelView = labelView

        contentLayout.addView(labelView)

        layout.setOnClickListener {
            this.sessionLoader().doMaybe {

                val animation = ObjectAnimator.ofInt(progressBar, "progress", 30)
                animation.duration = 1000
                animation.interpolator = DecelerateInterpolator()
                animation.start()

                labelView.text = "Loading\u2026"

                openSession(it, context)
            }
        }


        return layout
    }


    private fun openSessionButtonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.leftDp    = 8f

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

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
        bar.widthDp             = RelativeLayout.LayoutParams.MATCH_PARENT
        bar.heightDp            = 40

        bar.progressDrawableId  = R.drawable.progress_bar_load_session

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

        label.addRule(RelativeLayout.ALIGN_PARENT_START)
        label.addRule(RelativeLayout.CENTER_VERTICAL)

        label.margin.leftDp     = 10f

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


    fun entityLoadersView() : LinearLayout
    {
        val layout = this.entityLoadersViewLayout()

        Log.d("***NEW SESSION ACTVITY", "entity loaders session is: ${this.sessionId}")
        this.sessionLoader().doMaybe { sessionLoader ->
        entityManifest(context).doMaybe { manifest ->
            sessionLoader.entityIds.forEach { entityId ->
                manifest.persistedEntity(entityId).doMaybe { persistedEntity ->
                    layout.addView(this.entityLoaderView(persistedEntity.category, persistedEntity.name))
                }
            }
        } }

        return layout
    }


    fun entityLoadersViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 37f

        layout.backgroundColor  = Color.WHITE

        layout.padding.topDp    = 12f
        layout.padding.bottomDp  = 12f
        layout.padding.leftDp  = 10f
        layout.padding.rightDp  = 8f

        return layout.linearLayout(context)
    }


    private fun entityLoaderView(category : String, name : String) : LinearLayout
    {
        val layout = this.entityLoaderViewLayout()

        // Category
        layout.addView(this.entityLoaderCategoryView(category))

        // Name
        layout.addView(this.entityLoaderNameView(name))

        return layout
    }


    private fun entityLoaderViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.bottomDp  = 1f

        return layout.linearLayout(context)
    }


    private fun entityLoaderCategoryView(category : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        view.backgroundColor    = theme.colorOrBlack(bgColorTheme)

        view.padding.leftDp     = 8f
        view.padding.rightDp    = 8f
        view.padding.topDp      = 7f
        view.padding.bottomDp   = 7f

        view.corners            = Corners(1.0, 0.0, 0.0, 1.0)

        view.text               = category

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        view.color                = Color.WHITE

        view.sizeSp               = 17f

        return view.textView(context)
    }


    private fun entityLoaderNameView(label : String) : TextView
    {
        val view                = TextViewBuilder()

        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        view.backgroundColor    = theme.colorOrBlack(bgColorTheme)

        view.corners            = Corners(0.0, 1.0, 1.0, 0.0)

        view.padding.leftDp     = 8f
        view.padding.rightDp    = 8f
        view.padding.topDp      = 7f
        view.padding.bottomDp   = 7f

        view.text               = label

        view.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        view.color                = theme.colorOrBlack(colorTheme)

        view.sizeSp               = 17f

        return view.textView(context)
    }

}
