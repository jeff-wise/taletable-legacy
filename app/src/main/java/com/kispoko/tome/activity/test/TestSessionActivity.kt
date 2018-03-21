
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
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.rts.session.MessageSessionLoaded
import com.kispoko.tome.rts.session.SessionId
import com.kispoko.tome.rts.session.newSession
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch



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


    private val sheetId = SheetId("creature_brown_bear")


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
            val sheetLoader = OfficialSheetLoader("Casmey",
                                                  sheetId,
                                                  CampaignId("isara"),
                                                  GameId("magic_of_heroes"))

            val campaignLoader = OfficialCampaignLoader("Isara",
                                                        CampaignId("isara"),
                                                        GameId("magic_of_heroes"))

            val gameLoader = OfficialGameLoader("Magic of Heroes", GameId("magic_of_heroes"))

            val coreRulebookLoader = OfficialBookLoader("Core Rules",
                                                        BookId("core_rules"),
                                                        GameId("magic_of_heroes"))

            val loaders = listOf(sheetLoader, campaignLoader, gameLoader, coreRulebookLoader)

            newSession(loaders, SessionId("test"), context)
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

