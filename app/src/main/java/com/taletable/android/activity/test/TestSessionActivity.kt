
package com.taletable.android.activity.test


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.app.AppSettings
import com.taletable.android.model.session.sessionManifest
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.session.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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
    private val sheetId = EntityId(UUID.fromString("7259bcfa-76f9-42bb-b9d3-50a698850e8d"))


    private val testSessionId = SessionId(UUID.fromString("56897634-288a-478e-bb59-eedf09d8aab6"))


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

//            val intent = Intent(homeActivity, SessionActivity::class.java)
//            intent.putExtra("session_id", item.sessionId)
//            homeActivity.startActivity(intent)

            sessionManifest(context).doMaybe {
                it.session(testSessionId).doMaybe {

                    openSession(it, context)
                }
            }

        }

    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }


    private fun onSheetLoad(message : MessageSessionLoaded)
    {
        val intent = Intent(this, SessionActivity::class.java)
        intent.putExtra("sheet_id", EntityId(UUID.fromString("7259bcfa-76f9-42bb-b9d3-50a698850e8d")))
        finish()
        startActivity(intent)
    }


}

