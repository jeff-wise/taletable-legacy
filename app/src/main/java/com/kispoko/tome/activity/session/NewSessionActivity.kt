
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
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.official.GameSummary
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.EntityLoader
import com.kispoko.tome.rts.entity.EntityType
import com.kispoko.tome.rts.official.OfficialManager
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
import com.kispoko.tome.rts.entity.EntitySheetId
import io.reactivex.disposables.CompositeDisposable



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

    private val defaultGameId = GameId("magic_of_heroes")


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

        this.configureToolbar(getString(R.string.new_session))

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

    fun setStep(step : Int, gameId : GameId, maybeSessionId : Maybe<SessionId>)
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))

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

data class NewSessionMessageGame(val gameId : GameId) : NewSessionMessage()

data class NewSessionMessageSession(val sessionId : SessionId) : NewSessionMessage()


// ---------------------------------------------------------------------------------------------
// UI
// ---------------------------------------------------------------------------------------------

class NewSessionUI(private var step : Int,
                   private var gameId : GameId,
                   private var sessionId : Maybe<SessionId>,
                   private val theme : Theme,
                   private val newSessionActivity : NewSessionActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    val context = newSessionActivity

    fun gameSummary() : Maybe<GameSummary> =
        maybe(OfficialManager.gameManifest(context)?.game(this.gameId))




    private fun sessionLoader() : Maybe<SessionLoader>
    {
        val sessionId = this.sessionId
        Log.d("***NEW SESSION ACTIVITY", "session loader: $sessionId")
        return when (sessionId) {
            is Just    -> {
                officialSession(this.gameId, sessionId.value, context)
            }
            is Nothing -> {
                gameSummary().apply { officialSession(this.gameId, it.defaultSessionId, context) }
            }
        }
    }


    private fun entityKindName() : String
    {
        val maybeName = this.gameSummary().apply { gameSummary ->
                        this.sessionLoader().apply { sessionLoader ->
                        gameSummary.entityKind(sessionLoader.entityKindId).apply {
                            Just(it.name)
                         } } }
        return when (maybeName) {
            is Just    -> maybeName.value
            is Nothing -> ""
        }
    }


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
        val footerView = newSessionActivity.findViewById<LinearLayout>(R.id.footer)

        contentView?.removeAllViews()
        contentView?.let {
            it.addView(this.view())
        }

        footerView?.removeAllViews()
        footerView?.let {
            it.addView(this.footerView())
        }
    }


    fun updateGame(gameId : GameId)
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
                when (mainEntityId)
                {
                    is EntitySheetId -> {
                        val activity = context as AppCompatActivity
                        val intent = Intent(activity, SheetActivity::class.java)
                        intent.putExtra("sheet_id", mainEntityId.sheetId)
                        activity.startActivity(intent)
                    }
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.openSessionViewLayout()

        when (step)
        {
            1 ->
            {
                layout.addView(this.titleView(1, context.getString(R.string.what_game_do_you_play)))

                layout.addView(this.chooseButtonView(R.string.select_game))
                layout.addView(this.selectionView())

                layout.addView(this.miscGameButtonsView())
            }
            2 ->
            {
                layout.addView(this.titleView(2, context.getString(R.string.what_are_you_playing_with)))

                layout.addView(this.chooseButtonView(R.string.select_session))
                layout.addView(this.selectionView())
            }
            3 ->
            {
                layout.addView(this.titleView(3, context.getString(R.string.review_new_session)))

                layout.addView(this.entityLoadersView())
            }
        }



        return layout
    }


    private fun openSessionViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // VIEWS > Title
    // -----------------------------------------------------------------------------------------

    private fun titleView(step : Int, titleString : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val stepView            = TextViewBuilder()
        val labelView           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f

        layout.margin.topDp     = 1f
//        layout.margin.rightDp   = 6f
//        layout.margin.leftDp    = 6f

        layout.child(stepView)
              .child(labelView)

        // (3 A) Index
        // -------------------------------------------------------------------------------------

        stepView.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        stepView.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        stepView.backgroundResource    = R.drawable.bg_session_step

//        val indexColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))

        stepView.color                 = Color.WHITE

        stepView.font                  = Font.typeface(TextFont.default(),
                                                       TextFontStyle.SemiBold,
                                                       context)

        stepView.text                  = step.toString()

        stepView.gravity               = Gravity.CENTER

        stepView.sizeSp                = 17.5f

        stepView.margin.rightDp        = 8f
        stepView.padding.bottomDp      = 1f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = titleString

        labelView.font              = Font.typeface(TextFont.default(),
                                                    TextFontStyle.SemiBold,
                                                    context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_11"))))

        labelView.color             = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 20f

        labelView.padding.bottomDp  = 1f

        return layout.linearLayout(context)
    }


    // VIEWS > Choose Game View
    // -----------------------------------------------------------------------------------------

    private fun selectionView() : LinearLayout
    {
        val layout = this.selectionViewLayout()

        when (this.step)
        {
            1 -> {
                layout.addView(this.gameNameView())
                layout.addView(this.gameDescriptionView())
            }
            2 -> {
//                layout.addView(this.entityKindView())
                layout.addView(this.sessionNameView())
                layout.addView(this.sessionDescriptionView())
            }
        }

        return layout
    }


    private fun selectionViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.topDp     = 1f
        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f

        when (this.step)
        {
            1 -> {
                layout.padding.topDp    = 10f
                layout.padding.bottomDp = 10f
            }
            2 -> {
                layout.padding.topDp    = 8f
                layout.padding.bottomDp = 8f
            }
        }


        layout.onClick          = View.OnClickListener {
            when (this.step)
            {
                // Choose the game
                1 ->
                {
                    val intent = Intent(newSessionActivity, GamesListActivity::class.java)
                    intent.putExtra("game_action", GameActionLoadSession)
                    newSessionActivity.startActivity(intent)
                }
                2 ->
                {
                    val intent = Intent(newSessionActivity, EntityTypeListActivity::class.java)
                    intent.putExtra("game_id", this.gameId)
                    newSessionActivity.startActivity(intent)
                }
            }
        }

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


    private fun gameNameView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val name                = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.child(name)

        // (4) Label
        // -------------------------------------------------------------------------------------

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = this.gameSummary().toNullable()?.name

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        name.color              = Color.WHITE

        name.sizeSp             = 19f

        return layout.linearLayout(context)
    }


    private fun gameDescriptionView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val descriptionView     = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.child(descriptionView)

        // (4) Label
        // -------------------------------------------------------------------------------------

        descriptionView.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        descriptionView.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        descriptionView.text            = this.gameSummary().toNullable()?.name

        descriptionView.font            = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Regular,
                                                        context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        descriptionView.color           = theme.colorOrBlack(labelColorTheme)

        descriptionView.sizeSp          = 16f

        return layout.linearLayout(context)
    }


    private fun entityKindView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val kindView            = TextViewBuilder()
        val borderView          = LinearLayoutBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.child(kindView)
              .child(borderView)

        // (3) Kind
        // -------------------------------------------------------------------------------------

        kindView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        kindView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        kindView.padding.leftDp   = 8f
        kindView.padding.rightDp  = 8f

        kindView.text               = this.entityKindName()

        kindView.font               = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val kindColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_6"))))
        kindView.color              = theme.colorOrBlack(kindColorTheme)

        kindView.sizeSp             = 14f

        // (4) Border
        // -------------------------------------------------------------------------------------

        borderView.width                = LinearLayout.LayoutParams.MATCH_PARENT
        borderView.heightDp             = 1

        val borderColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("transparent_80"))))
        borderView.backgroundColor      = theme.colorOrBlack(borderColorTheme)

        borderView.margin.topDp         = 6f

        return layout.linearLayout(context)
    }


    private fun sessionNameView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val name                = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.child(name)

        // (4) Label
        // -------------------------------------------------------------------------------------

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        this.sessionLoader().doMaybe {
            name.text           = it.sessionName.value
        }

        name.font               = Font.typeface(TextFont.default(),
                                                 TextFontStyle.SemiBold,
                                                 context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        name.color              = theme.colorOrBlack(labelColorTheme)
        name.color              = Color.WHITE

        name.sizeSp             = 20f

        return layout.linearLayout(context)
    }


    private fun sessionDescriptionView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val descriptionView     = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.child(descriptionView)

        // (4) Label
        // -------------------------------------------------------------------------------------

        descriptionView.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        descriptionView.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        this.sessionLoader().doMaybe {
            descriptionView.text           = it.sessionInfo.tagline
        }

        descriptionView.font               = Font.typeface(TextFont.default(),
                                                           TextFontStyle.Regular,
                                                           context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        descriptionView.color              = theme.colorOrBlack(colorTheme)

        descriptionView.sizeSp             = 17f

        return layout.linearLayout(context)
    }


    private fun chooseButtonView(labelId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val iconView            = ImageViewBuilder()
        val labelView           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.topDp     = 20f
        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f

        layout.padding.leftDp   = 12f
        layout.padding.rightDp  = 12f
        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        layout.onClick          = View.OnClickListener {
            when (this.step)
            {
                // Choose the game
                1 ->
                {
                    val intent = Intent(newSessionActivity, GamesListActivity::class.java)
                    intent.putExtra("game_action", GameActionLoadSession)
                    newSessionActivity.startActivity(intent)
                }
            }
        }

        layout.child(labelView)
              .child(iconView)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = 24
        iconView.heightDp           = 24

        iconView.image              = R.drawable.icon_arrow_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

        iconView.margin.leftDp      = 4f
        iconView.padding.topDp      = 2f

        // (4) Label
        // -------------------------------------------------------------------------------------

        labelView.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.textId                = labelId

        labelView.font                  = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Medium,
                                                        context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        labelView.color                 = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp                = 16.5f

        labelView.padding.bottomDp      = 1f

        return layout.linearLayout(context)
    }


    private fun miscGameButtonsView() : LinearLayout
    {
        val layout = this.miscButtonsViewLayout()

        val randomGameOnClick = View.OnClickListener {  }
        layout.addView(this.miscButtonView(R.drawable.icon_die,
                                           R.string.choose_random_game,
                                           randomGameOnClick))

        val createGameOnClick = View.OnClickListener {  }
        layout.addView(this.miscButtonView(R.drawable.icon_plus_sign,
                                           R.string.create_new_game,
                                           createGameOnClick, 25))

        val aboutGamesOnClick = View.OnClickListener {  }
        layout.addView(this.miscButtonView(R.drawable.icon_info_outline,
                                           R.string.about_games,
                                           aboutGamesOnClick, 22))

        return layout
    }


    private fun miscButtonsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.topDp     = 30f

        return layout.linearLayout(context)
    }


    private fun miscButtonView(iconId : Int,
                               labelId : Int,
                               onClick : View.OnClickListener,
                               iconSize : Int? = 24) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val iconView            = ImageViewBuilder()
        val labelView           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.topDp     = 2f
        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f
        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.onClick          = onClick

        layout.child(iconView)
              .child(labelView)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp            = iconSize
        iconView.heightDp           = iconSize

        iconView.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

        iconView.margin.rightDp     = 6f

        // (4) Label
        // -------------------------------------------------------------------------------------

        labelView.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.textId                = labelId

        labelView.font                  = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Medium,
                                                        context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        labelView.color                 = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp                = 18f

        labelView.padding.bottomDp      = 1f

        return layout.linearLayout(context)
    }


    // VIEWS > Footer
    // -----------------------------------------------------------------------------------------

    fun footerView() : LinearLayout
    {
        val layout = this.footerViewLayout()

        layout.addView(this.footerTopBorderView())

        layout.addView(this.footerMainView())

        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = Color.WHITE

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun footerTopBorderView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        layout.backgroundColor  = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    private fun footerMainView() : LinearLayout
    {
        val layout = this.footerMainViewLayout()

        layout.addView(this.previousButtonView())

        when (this.step) {
            3    -> {
                layout.addView(this.openSessionButtonView())
//                val progressBar = this.openSessionProgressBar()
//                layout.addView(progressBar)
//                progressBar.progress = 30
            }
            else -> layout.addView(this.nextButtonView())
        }

        return layout
    }


    private fun footerMainViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.topDp    = 4f
        layout.padding.bottomDp = 4f

        return layout.linearLayout(context)
    }


    private fun previousButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val buttonLayout        = LinearLayoutBuilder()

        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER

        layout.onClick          = View.OnClickListener {
            when (step)
            {
                1 -> { }
                2 -> newSessionActivity.setStep(1, this.gameId, Nothing())
                3 -> newSessionActivity.setStep(2, this.gameId, Nothing())
            }
        }

        when (this.step) {
            1    -> { }
            else -> layout.child(buttonLayout)
        }

        // (3) Button Layout
        // -------------------------------------------------------------------------------------

        buttonLayout.width              = LinearLayout.LayoutParams.MATCH_PARENT
        buttonLayout.heightDp           = 40

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        buttonLayout.backgroundColor    = Color.WHITE

        buttonLayout.padding.topDp      = 6f
        buttonLayout.padding.bottomDp   = 6f

        buttonLayout.margin.leftDp      = 12f
        buttonLayout.margin.rightDp     = 12f

        buttonLayout.gravity            = Gravity.CENTER

        buttonLayout.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        buttonLayout.child(icon)
                    .child(label)

        // (4 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 28
        icon.heightDp           = 28

        icon.image              = R.drawable.icon_chevron_left

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        icon.padding.topDp      = 2.5f

        // (4 B) Label
        // -------------------------------------------------------------------------------------

        label.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text               = "${context.getString(R.string.previous)}   "

        label.font               = Font.typeface(TextFont.default(),
                                                 TextFontStyle.Medium,
                                                 context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        label.color              = theme.colorOrBlack(labelColorTheme)

        label.sizeSp             = 19f

        return layout.linearLayout(context)
    }


    private fun nextButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val buttonLayout        = LinearLayoutBuilder()

        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER

        layout.onClick          = View.OnClickListener {
            when (step)
            {
                1 -> newSessionActivity.setStep(2, this.gameId, Nothing())
                2 -> newSessionActivity.setStep(3, this.gameId, Nothing())
            }
        }

        layout.child(buttonLayout)

        // (3) Button Layout
        // -------------------------------------------------------------------------------------

        buttonLayout.width              = LinearLayout.LayoutParams.MATCH_PARENT
        buttonLayout.heightDp           = 40

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        buttonLayout.backgroundColor    = theme.colorOrBlack(bgColorTheme)

        buttonLayout.padding.topDp      = 6f
        buttonLayout.padding.bottomDp   = 6f

        buttonLayout.margin.leftDp      = 12f
        buttonLayout.margin.rightDp     = 12f

        buttonLayout.gravity            = Gravity.CENTER

        buttonLayout.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        buttonLayout.child(label)
                    .child(icon)

        // (4 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 28
        icon.heightDp           = 28

        icon.image              = R.drawable.icon_chevron_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = Color.WHITE

        icon.padding.topDp      = 2.5f

        // (4 B) Label
        // -------------------------------------------------------------------------------------

        label.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text               = "   ${context.getString(R.string.next)}"

        label.font               = Font.typeface(TextFont.default(),
                                                 TextFontStyle.Medium,
                                                 context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        label.color              = Color.WHITE

        label.sizeSp             = 19f

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

                newSession(it, context)
            }
        }


        return layout
    }


    private fun openSessionButtonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

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

        bar.margin.leftDp       = 12f
        bar.margin.rightDp      = 12f

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

        label.addRule(RelativeLayout.CENTER_IN_PARENT)

        label.text              = "   ${context.getString(R.string.open).toUpperCase()}"

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        label.color             = Color.WHITE

        label.sizeSp            = 18f

        return label.textView(context)
    }


    fun entityLoadersView() : LinearLayout
    {
        val layout = this.entityLoadersViewLayout()

        Log.d("***NEW SESSION ACTVITY", "entity loaders session is: ${this.sessionId}")
        this.sessionLoader().doMaybe {
            it.entityLoadersByType().entries.forEach { (entityType, loaders) ->
                layout.addView(this.entityLoaderCategoryView(entityType, loaders))
            }
        }

        return layout
    }


    fun entityLoadersViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f

        layout.margin.topDp     = 20f

        return layout.linearLayout(context)
    }


    private fun entityLoaderCategoryView(entityType : EntityType,
                                         loaders : List<EntityLoader>) : LinearLayout
    {
        val layout = this.entityLoaderCategoryViewLayout()

        layout.addView(this.entityLoaderCategoryHeaderView(entityType.toString()))

        loaders.forEach {
            layout.addView(this.entityLoaderView(it))
        }

        return layout
    }


    private fun entityLoaderCategoryViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 2f

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f
        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        return layout.linearLayout(context)
    }


    private fun entityLoaderCategoryHeaderView(headerString : String) : TextView
    {
        val headerView                  = TextViewBuilder()

        headerView.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        headerView.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        headerView.text                 = "${headerString}s"

        headerView.font                 = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Regular,
                                                        context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        headerView.color                = theme.colorOrBlack(colorTheme)

        headerView.sizeSp               = 15f

        headerView.margin.bottomDp      = 6f

        return headerView.textView(context)
    }


    private fun entityLoaderView(entityLoader : EntityLoader) : LinearLayout
    {
        val layout = this.entityLoaderViewLayout()

        // Name
        layout.addView(this.entityLoadNameView(entityLoader.name))

        return layout
    }


    private fun entityLoaderViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

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
        val nameView                  = TextViewBuilder()

        nameView.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        nameView.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        nameView.text                 = label

        nameView.font                 = Font.typeface(TextFont.default(),
                                                      TextFontStyle.Medium,
                                                      context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        nameView.color                = theme.colorOrBlack(colorTheme)

        nameView.sizeSp               = 18f

        return nameView.textView(context)
    }

}
