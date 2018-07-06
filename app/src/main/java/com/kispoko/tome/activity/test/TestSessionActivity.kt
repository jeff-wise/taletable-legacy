
package com.kispoko.tome.activity.test


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.model.campaign.CampaignId
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
    private val sheetId = EntityId(UUID.fromString("ff30a874-976b-4fce-b1c9-618c0d839330"))


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

            val entityIds = listOf(
                    EntityId(UUID.fromString("ff30a874-976b-4fce-b1c9-618c0d839330")),
                    EntityId(UUID.fromString("cccb2da0-f6e5-4ea1-acee-6469942cfb3b")),
                    EntityId(UUID.fromString("85f6da8e-e497-4aa2-a3c3-c2eab32d2839")),
                    EntityId(UUID.fromString("7140ca5a-a5ce-4a37-95c8-519c7de3c040"))
                    )

            val sessionInfo = SessionInfo(SessionSummary(""),
                                          SessionDescription(""),
                                          "1st Level Human Fighter",
                                          SessionTag("Level 1"),
                                          listOf(SessionTag("Human"), SessionTag("Fighter")))
            val sessionLoader = Session(SessionId(UUID.randomUUID()),
                                              SessionName("Casmey Dalseya"),
                                              sessionInfo,
                                              EntityId(UUID.fromString("85f6da8e-e497-4aa2-a3c3-c2eab32d2839")),
                                              //EntityKindId("player_character"),
                                              Nothing(),
                                              entityIds,
                                              EntityId(UUID.fromString("ff30a874-976b-4fce-b1c9-618c0d839330")))

            openSession(sessionLoader, context)
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

