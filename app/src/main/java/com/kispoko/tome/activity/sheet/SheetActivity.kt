
package com.kispoko.tome.activity.sheet


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu

import com.kispoko.tome.R
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetManager



class SheetActivity : AppCompatActivity()
{

    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_sheet)

        // [2] Load Sheet
        // -------------------------------------------------------------------------------------

        SheetManager.setContext(this)
        GameManager.setContext(this)

        SheetManager.loadSheetTemplate("test", this)
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


}
