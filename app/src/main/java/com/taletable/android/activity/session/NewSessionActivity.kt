
package com.taletable.android.activity.session


import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.WindowManager
import android.widget.*
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.official.GameSummary
import com.taletable.android.router.Router
import com.taletable.android.rts.session.*
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.maybe
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import com.taletable.android.model.entity.entityManifest
import com.taletable.android.model.session.sessionManifest
import com.taletable.android.official.gameManifest
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.util.configureToolbar
import io.reactivex.disposables.CompositeDisposable
import java.util.*



/**
 * New Session Activity
 * Session here refers to a saved session, not necessarily creating a new Session object.
 * It is creating a new session from the user's perspective.
 */
//class NewSessionActivity : AppCompatActivity()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var step : Int = 1
//
//    private val defaultGameId = EntityId(UUID.fromString("ae94f23c-1ff5-48cc-a977-4dd2f7e81341"))
//
//    private var newSessionUI : NewSessionUI? = null
//
//    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()
//
//
//    // -----------------------------------------------------------------------------------------
//    // ACTIVITY API
//    // -----------------------------------------------------------------------------------------
//
//    override fun onCreate(savedInstanceState : Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//
//        // (1) Set Content View
//        // -------------------------------------------------------------------------------------
//
//        setContentView(R.layout.activity_open_session)
//
//        // (2) Read Parameters
//        // -------------------------------------------------------------------------------------
//
//        if (this.intent.hasExtra("step"))
//            this.step = this.intent.getIntExtra("step", 1)
//
//        // (3) Configure View
//        // -------------------------------------------------------------------------------------
//
//        this.configureToolbar(getString(R.string.new_session), TextFont.RobotoCondensed, TextFontStyle.Regular)
//
//        this.applyTheme(com.taletable.android.model.theme.official.officialAppThemeLight)
//
//        this.initializeListeners()
//
//        this.setStep(1, this.defaultGameId, Nothing())
//    }
//
//
//    override fun onCreateOptionsMenu(menu : Menu) : Boolean
//    {
//        menuInflater.inflate(R.menu.empty, menu)
//        return true
//    }
//
//
//    override fun onDestroy()
//    {
//        super.onDestroy()
//        this.messageListenerDisposable.clear()
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // UI
//    // -----------------------------------------------------------------------------------------
//
//    fun setStep(step : Int, gameId : EntityId, maybeSessionId : Maybe<SessionId>)
//    {
//        this.step = step
//
//        if (this.newSessionUI == null) {
//            this.newSessionUI = NewSessionUI(step,
//                                             gameId,
//                                             maybeSessionId,
//                                             officialAppThemeLight,
//                                             this)
//        }
//
//        newSessionUI?.setStep(step)
//        newSessionUI?.render()
//    }
//
//
//    private fun applyTheme(theme : Theme)
//    {
//        // STATUS BAR
//        // -------------------------------------------------------------------------------------
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            val window = this.window
//
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//
//            val statusBarColorTheme = ColorTheme(setOf(
//                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
//                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
//            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
//        }
//
//        // TOOLBAR
//        // -------------------------------------------------------------------------------------
//        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
//
//        val toolbarBgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
//
//        // Toolbar > Background
////        toolbar.setBackgroundColor(theme.colorOrBlack(toolbarBgColorTheme))
//        toolbar.setBackgroundColor(Color.WHITE)
//
//        // Toolbar > Icons
//        val toolbarIconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
//
//        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_close_button)
//        menuLeftButton.colorFilter = PorterDuffColorFilter(theme.colorOrBlack(toolbarIconColorTheme), PorterDuff.Mode.SRC_IN)
//
//        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
//        menuRightButton.colorFilter = PorterDuffColorFilter(theme.colorOrBlack(toolbarIconColorTheme), PorterDuff.Mode.SRC_IN)
//
//    }
//
//
//    private fun initializeListeners()
//    {
//        val newSessionMessageDisposable = Router.listen(NewSessionMessage::class.java)
//                                                .subscribe(this::onMessage)
//        this.messageListenerDisposable.add(newSessionMessageDisposable)
//
//        val sessionMessageDisposable = Router.listen(MessageSessionLoad::class.java)
//                                             .subscribe(this::onSessionLoadMessage)
//        this.messageListenerDisposable.add(sessionMessageDisposable)
//    }
//
//
//    private fun onMessage(message : NewSessionMessage)
//    {
//        when (message)
//        {
//            is NewSessionMessageGame    ->
//            {
//                this.newSessionUI?.updateGame(message.gameId)
//            }
//            is NewSessionMessageSession ->
//            {
//                this.newSessionUI?.updateSession(message.sessionId)
//            }
//        }
//    }
//
//
//    private fun onSessionLoadMessage(message : MessageSessionLoad)
//    {
//        when (message)
//        {
//            is MessageSessionEntityLoaded ->
//            {
//                this.newSessionUI?.updateLoadProgress(message.update)
//            }
//            is MessageSessionLoaded ->
//            {
//                this.newSessionUI?.startSession(message.sessionId)
//
//            }
//        }
//    }
//
//}
//
//
//// ---------------------------------------------------------------------------------------------
//// MESSAGE
//// ---------------------------------------------------------------------------------------------
//
//sealed class NewSessionMessage
//
//data class NewSessionMessageGame(val gameId : EntityId) : NewSessionMessage()
//
//data class NewSessionMessageSession(val sessionId : SessionId) : NewSessionMessage()
//
//
//// ---------------------------------------------------------------------------------------------
//// UI
//// ---------------------------------------------------------------------------------------------
//
//class NewSessionUI(private var step : Int,
//                   private var gameId : EntityId,
//                   private var sessionId : Maybe<SessionId>,
//                   private val theme : Theme,
//                   private val newSessionActivity : NewSessionActivity)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    val context = newSessionActivity
//
//    fun gameSummary() : Maybe<GameSummary> =
//        maybe(gameManifest(context)?.game(this.gameId))
//
//
//    private fun sessionLoader() : Maybe<Session>
//    {
//        val sessionId = this.sessionId
//        Log.d("***NEW SESSION ACTIVITY", "session loader: $sessionId")
//        return when (sessionId) {
//            is Just    -> {
//                sessionManifest(context).apply { it.session(sessionId.value) }
//            }
//            is Nothing -> {
//                gameSummary().apply { gameSummary ->
//                    sessionManifest(context).apply { it.session(gameSummary.defaultSessionId) }
//                }
//            }
//        }
//    }
//
//
////    private fun entityKindName() : String
////    {
////        val maybeName = this.gameSummary().apply { gameSummary ->
////                        this.sessionLoader().apply { sessionLoader ->
////                        gameSummary.entityKind(sessionLoader.entityKindId).apply {
////                            Just(it.name)
////                         } } }
////        return when (maybeName) {
////            is Just    -> maybeName.value
////            is Nothing -> ""
////        }
////    }
//
//
//    // PROPERTIES > Views
//    // -----------------------------------------------------------------------------------------
//
//    private var progressBar : ProgressBar? = null
//    private var openButtonLabelView : TextView? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // METHODS
//    // -----------------------------------------------------------------------------------------
//
//    fun setStep(step : Int)
//    {
//        this.step = step
//    }
//
//
//    fun render()
//    {
//        val contentView = newSessionActivity.findViewById<LinearLayout>(R.id.content)
////        val footerView = newSessionActivity.findViewById<LinearLayout>(R.id.footer)
//
//        contentView?.removeAllViews()
//        contentView?.let {
//            it.addView(this.view())
//        }
//
////        footerView?.removeAllViews()
////        footerView?.let {
////            it.addView(this.footerView())
////        }
//    }
//
//
//    fun updateGame(gameId : EntityId)
//    {
//        this.gameId = gameId
//
//        this.render()
//    }
//
//
//    fun updateSession(sessionId : SessionId)
//    {
//        this.sessionId = Just(sessionId)
//
//        Log.d("***NEW SESSION ACTIVITY", "update session id: ${this.sessionId}")
//
//        this.render()
//    }
//
//
//    fun updateLoadProgress(sessionLoadUpdate : SessionLoadUpdate)
//    {
//        this.progressBar?.let { bar ->
//            val updateAmount = bar.progress + (72 / sessionLoadUpdate.totalEntities)
//            bar.progress = updateAmount
//        }
//    }
//
//
//    fun startSession(sessionId : SessionId)
//    {
//        this.sessionLoader().doMaybe {
//            if (it.sessionId == sessionId)
//            {
//                val mainEntityId = it.mainEntityId
//                val activity = context as AppCompatActivity
//                val intent = Intent(activity, SessionActivity::class.java)
//                intent.putExtra("sheet_id", mainEntityId)
//                activity.startActivity(intent)
//            }
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEWS
//    // -----------------------------------------------------------------------------------------
//
//    fun view() : LinearLayout
//    {
//        val layout = this.openSessionViewLayout()
//
//        layout.addView(this.stepView(1, R.string.what_game_do_you_play))
//
//        //layout.addView(this.stepViewBottomBorderView())
//
//        layout.addView(this.stepView(2, R.string.what_are_you_looking_for))
//
//        //layout.addView(this.stepViewBottomBorderView())
//
//        layout.addView(this.stepView(3, R.string.this_session_contains))
//
//        //layout.addView(this.stepViewBottomBorderView())
//
//
//        val loadLayout = newSessionActivity.findViewById<LinearLayout>(R.id.footer)
//        loadLayout.addView(this.loadView())
//
//        return layout
//    }
//
//
//    private fun openSessionViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
////        layout.padding.topDp    = 1f
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun stepView(step : Int, labelId : Int) : LinearLayout
//    {
//        val layout = this.stepViewLayout()
//
//
//        when (step)
//        {
//            1 -> {
//                layout.addView(this.titleView(step, context.getString(labelId)))
//                val gameName = this.gameSummary().toNullable()?.name ?: ""
////                layout.addView(this.descriptionView(R.string.choose_game_description))
//
//                val onGameClick = View.OnClickListener {
//                    val intent = Intent(newSessionActivity, GamesListActivity::class.java)
//                    intent.putExtra("game_action", GameActionLoadSession)
//                    newSessionActivity.startActivity(intent)
//                }
//                layout.addView(this.selectionView(gameName, "5th SRD fantasy RPG", R.drawable.icon_die, onGameClick))
//            }
//            2 -> {
//                layout.addView(this.titleView(step, context.getString(labelId)))
//                val sessionName = this.sessionLoader().toNullable()?.sessionName?.value ?: ""
//                val sessionLoader = this.sessionLoader()
//                when (sessionLoader) {
//                    is Nothing -> {
//                        Log.d("***NEW SESSION ACTIVITY", "session loader is null")
//                    }
//                }
////                layout.addView(this.descriptionView(R.string.choose_session_description))
//                val onSessionClick = View.OnClickListener {
//                    val intent = Intent(newSessionActivity, EntityTypeListActivity::class.java)
//                    intent.putExtra("game_id", this.gameId)
//                    newSessionActivity.startActivity(intent)
//                }
//                layout.addView(this.selectionView(sessionName, "1st level human rogue", R.drawable.icon_apps, onSessionClick))
////                layout.addView(this.chooseButtonView(2, R.string.select))
//            }
//            3 -> {
////                layout.addView(this.descriptionView(R.string.this_session_contains))
//                layout.addView(this.entityLoadersView())
////                layout.addView(this.loadView())
//            }
//        }
//
//
////        layout.addView(this.selectionView(step))
//
//        return layout
//    }
//
//
//    private fun stepViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)
//
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun stepViewBottomBorderView() : LinearLayout
//    {
//        val layout          = LinearLayoutBuilder()
//
//        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp     = 1
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//
//    // VIEWS > Title
//    // -----------------------------------------------------------------------------------------
//
//    private fun titleView(step : Int, title : String) : RelativeLayout
//    {
//        val layout = this.titleViewLayout()
//
//        // Label
//        layout.addView(this.titleTextView(step, title))
//
//        // Choose Button
////        layout.addView(this.chooseButtonView(step, R.string.choose))
//
//        return layout
//    }
//
//
//    private fun titleViewLayout() : RelativeLayout
//    {
//        val layout              = RelativeLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        return layout.relativeLayout(context)
//    }
//
//
//    private fun titleTextView(step : Int, titleString : String) : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val stepView            = TextViewBuilder()
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
//        layout.margin.leftDp    = 16f
//        layout.padding.topDp    = 14f
//        layout.padding.bottomDp = 6f
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
//        layout.addRule(RelativeLayout.CENTER_VERTICAL)
//
//        layout.child(stepView)
//              .child(labelView)
//
//        // (3 A) Index
//        // -------------------------------------------------------------------------------------
//
//        stepView.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
//        stepView.height                = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        stepView.backgroundResource    = R.drawable.bg_session_step
//
//        stepView.color                 = Color.WHITE
//
//        stepView.font                  = Font.typeface(TextFont.RobotoCondensed,
//                                                       TextFontStyle.Bold,
//                                                       context)
//
//        stepView.text                  = step.toString()
//
//        stepView.gravity               = Gravity.CENTER
//
//        stepView.sizeSp                = 14f
//
//        stepView.margin.rightDp        = 20f
//
//        stepView.padding.bottomDp       = 1f
//
//        // (3 B) Label
//        // -------------------------------------------------------------------------------------
//
//        labelView.width             = LinearLayout.LayoutParams.MATCH_PARENT
//        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        labelView.text              = titleString
//
//        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
//                                                    TextFontStyle.Regular,
//                                                    context)
//
//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//
//        labelView.color             = theme.colorOrBlack(labelColorTheme)
//
//        labelView.sizeSp            = 17f
//
//        labelView.padding.bottomDp  = 2f
//
//        return layout.linearLayout(context)
//    }
//
//
//
//    private fun selectionView(value : String,
//                              description : String,
//                              iconId : Int,
//                              onClick : View.OnClickListener) : LinearLayout
//    {
//        val layout = this.selectionViewLayout()
//
//        layout.addView(this.selectionIconView(iconId))
//
//        layout.addView(this.selectionInfoView(value, description))
//
//        layout.setOnClickListener(onClick)
//
//        return layout
//    }
//
//
//    private fun selectionViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.backgroundColor  = Color.WHITE
//
//        layout.padding.rightDp  = 10f
//        layout.padding.topDp    = 12f
//        layout.padding.bottomDp = 12f
//
//        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)
//
//        layout.elevation        = 2f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun selectionIconView(iconId : Int) : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val icon                = ImageViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.padding.leftDp   = 16f
//        layout.padding.topDp    = 4f
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
////        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        layout.child(icon)
//
//        // (3 A) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.widthDp            = 22
//        icon.heightDp           = 22
//
//        icon.image              = iconId
//
//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
//        icon.color              = theme.colorOrBlack(iconColorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun selectionInfoView(value : String, description : String) : LinearLayout
//    {
//        val layout = this.selectionInfoViewLayout()
//
//        layout.addView(this.selectionValueView(value))
//
//        layout.addView(this.selectionDescriptionView(description))
//
//        return layout
//    }
//
//
//
//    private fun selectionInfoViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.margin.leftDp    = 20f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun selectionValueView(value : String) : TextView
//    {
//        val name                = TextViewBuilder()
//
//        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        name.text               = value
//
//        name.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Bold,
//                                                context)
//
//        val textColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
//        name.color              = theme.colorOrBlack(textColorTheme)
//
//        name.sizeSp             = 20.5f
//
//        return name.textView(context)
//    }
//
//
//
//    private fun selectionDescriptionView(description : String) : TextView
//    {
//        val view                = TextViewBuilder()
//
//        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        view.margin.topDp       = 2f
//
//        view.text               = description
//
//        view.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Regular,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
//        view.color              = theme.colorOrBlack(colorTheme)
//
//        view.sizeSp             = 18f
//
//        return view.textView(context)
//    }
//
//
//
//    private fun loadView() : LinearLayout
//    {
//        val layout = this.loadViewLayout()
//
////        layout.addView(this.loadIconView())
//
//        layout.addView(this.openSessionButtonView())
//
//        return layout
//    }
//
//
//    private fun loadViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun loadIconView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val icon                = ImageViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.id               = R.id.baseline
//
//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.gravity          = Gravity.CENTER
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        layout.padding.leftDp   = 13f
//
//        layout.child(icon)
//
//        // (3) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.widthDp            = 23
//        icon.heightDp           = 23
//
//        icon.image              = R.drawable.icon_sync
//
//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
//        icon.color              = theme.colorOrBlack(iconColorTheme)
//        icon.color              = Color.WHITE
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun openSessionButtonView() : LinearLayout
//    {
//        val layout = this.openSessionButtonViewLayout()
//
//        val contentLayout = this.openSessionButtonContentViewLayout()
//
//        layout.addView(contentLayout)
//
//        val progressBar = this.openSessionProgressBar()
//        this.progressBar = progressBar
//        contentLayout.addView(progressBar)
//
//        progressBar.progress = 0
//
//        val labelView = this.openSessionButtonLabelView()
//        this.openButtonLabelView = labelView
//
//        // contentLayout.addView(loadIconView())
//
//        contentLayout.addView(labelView)
//
////        val labelLayoutParams = labelView.layoutParams as RelativeLayout.LayoutParams
////        labelLayoutParams.addRule(RelativeLayout.ALIGN_END, R.id.baseline)
////        labelView.layoutParams = labelLayoutParams
//
//        layout.setOnClickListener {
//            this.sessionLoader().doMaybe {
//
//                val animation = ObjectAnimator.ofInt(progressBar, "progress", 30)
//                animation.duration = 1000
//                animation.interpolator = DecelerateInterpolator()
//                animation.start()
//
//                labelView.text = "Loading\u2026"
//
//                openSession(it, context)
//            }
//        }
//
//
//        return layout
//    }
//
//
//    private fun openSessionButtonViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        // layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)
//
//        return layout.linearLayout(context)
//    }
//
//    private fun openSessionButtonContentViewLayout() : RelativeLayout
//    {
//        val layout              = RelativeLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        return layout.relativeLayout(context)
//    }
//
//
//    private fun openSessionProgressBar() : ProgressBar
//    {
//        val bar                 = ProgressBarBuilder()
//
//        bar.id                  = R.id.progress_bar
//
//        bar.layoutType          = LayoutType.RELATIVE
//        bar.width               = RelativeLayout.LayoutParams.MATCH_PARENT
//        bar.height              = RelativeLayout.LayoutParams.MATCH_PARENT
//
//        bar.progressDrawableId  = R.drawable.progress_bar_load_session
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        bar.backgroundColor = theme.colorOrBlack(bgColorTheme)
//
//        return bar.progressBar(context)
//    }
//
//
//    private fun openSessionButtonLabelView() : TextView
//    {
//        val label               = TextViewBuilder()
//
//        label.layoutType        = LayoutType.RELATIVE
//        label.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
//        label.height            = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        label.addRule(RelativeLayout.ALIGN_PARENT_START)
//        label.addRule(RelativeLayout.CENTER_VERTICAL)
//
//        label.margin.leftDp     = 55f
//
//        label.textId            = R.string.start_new_session
//
//        label.font              = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Bold,
//                                                context)
//
//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
//        label.color             = Color.WHITE
//
//        label.sizeSp            = 17f
//
//        return label.textView(context)
//    }
//
//
//    fun entityLoadersView() : LinearLayout
//    {
//        val layout = this.entityLoadersViewLayout()
//
//        Log.d("***NEW SESSION ACTVITY", "entity loaders session is: ${this.sessionId}")
//        this.sessionLoader().doMaybe { sessionLoader ->
//        entityManifest(context).doMaybe { manifest ->
//            sessionLoader.entityIds.forEach { entityId ->
//                manifest.persistedEntity(entityId).doMaybe { persistedEntity ->
//                    layout.addView(this.entityLoaderView(persistedEntity.category, persistedEntity.name))
//                    layout.addView(this.entityBottomBorderView())
//                }
//            }
//        } }
//
//        return layout
//    }
//
//
//    fun entityLoadersViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.padding.leftDp    = 58f
//
//        layout.backgroundColor  = Color.WHITE
//
//        layout.padding.topDp    = 8f
//        layout.padding.bottomDp  = 12f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun entityLoaderView(category : String, name : String) : LinearLayout
//    {
//        val layout = this.entityLoaderViewLayout()
//
//        // Name
//        layout.addView(this.entityLoaderNameView(name))
//
//        // Category
//        layout.addView(this.entityLoaderCategoryView(category))
//
//        return layout
//    }
//
//
//    private fun entityLoaderViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.margin.bottomDp  = 1f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun entityLoaderCategoryView(category : String) : TextView
//    {
//        val view                = TextViewBuilder()
//
//        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
////        view.backgroundColor    = theme.colorOrBlack(bgColorTheme)
//
//        view.padding.leftDp     = 8f
//        view.padding.rightDp    = 8f
//        view.padding.topDp      = 4f
//        view.padding.bottomDp   = 4f
//
//        view.corners            = Corners(2.0, 2.0, 2.0, 2.0)
//
//        view.text               = category
//
//        view.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Regular,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
//        view.color                = Color.WHITE
//        view.color                  = theme.colorOrBlack(colorTheme)
//
//        view.sizeSp               = 17f
//
//        return view.textView(context)
//    }
//
//
//    private fun entityLoaderNameView(label : String) : TextView
//    {
//        val view                = TextViewBuilder()
//
//        view.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        view.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
////        view.backgroundColor    = theme.colorOrBlack(bgColorTheme)
//        view.backgroundColor    = Color.WHITE
//
//        view.corners            = Corners(3.0, 0.0, 0.0, 3.0)
//
//        //view.padding.leftDp     = 8f
//        view.padding.rightDp    = 8f
//        view.padding.topDp      = 8f
//        view.padding.bottomDp   = 8f
//
//        view.text               = label
//
//        view.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Bold,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
//        view.color                = theme.colorOrBlack(colorTheme)
//
//        view.sizeSp               = 18f
//
//        return view.textView(context)
//    }
//
//
//    private fun entityBottomBorderView() : LinearLayout
//    {
//        val layout          = LinearLayoutBuilder()
//
//        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp     = 1
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//
//}
