
package com.kispoko.tome.activity.test


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.session.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import maybe.Nothing
import java.util.*



/**
 * Load Activity
 */
class TestSessionActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val appSettings : AppSettings = AppSettings(ThemeId.Light)


    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()


//    private val sheetId = SheetId("creature_brown_bear")
    private val sheetId = SheetId("character_sanson_level_1")


    // -----------------------------------------------------------------------------------------
    // ACTIVITY
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_load)

        // (2) Listen to messages
        // -------------------------------------------------------------------------------------

        val disposable = Router.listen(MessageSessionLoaded::class.java)
                               .subscribe(this::onSheetLoad)
        this.messageListenerDisposable.add(disposable)

        // (3) Load Test Sheet
        // -------------------------------------------------------------------------------------

        val context = this
        launch(UI) {
            val sheetLoader = OfficialSheetLoader("Sheet Name",
                                                  sheetId,
                                                  GameId("magic_of_heroes"))

            val campaignLoader = OfficialCampaignLoader("Campaign Name",
                                                        CampaignId("isara"),
                                                        GameId("magic_of_heroes"))

            val gameLoader = OfficialGameLoader("Game Name", GameId("magic_of_heroes"))

            val coreRulebookLoader = OfficialBookLoader("Book Name",
                                                        BookId("core_rules"),
                                                        GameId("magic_of_heroes"))

            val loaders = listOf(sheetLoader, campaignLoader, gameLoader, coreRulebookLoader)

            val sessionInfo = SessionInfo(SessionSummary(""),
                                          SessionDescription(""),
                                          "1st Level Human Fighter",
                                          SessionTag("Level 1"),
                                          listOf(SessionTag("Human"), SessionTag("Fighter")))
            val sessionLoader = SessionLoader(SessionId(UUID.randomUUID()),
                                              SessionName("Casmey Dalseya"),
                                              sessionInfo,
                                              GameId("magic_of_heroes"),
                                              EntityKindId("player_character"),
                                              Nothing(),
                                              loaders,
                                              EntitySheetId(SheetId("character_casmey_level_1")))

            newSession(sessionLoader, context)
        }

    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }


    private fun onSheetLoad(message : MessageSessionLoaded)
    {
        val intent = Intent(this, SheetActivity::class.java)
        intent.putExtra("sheet_id", sheetId)
        finish()
        startActivity(intent)
    }


}

