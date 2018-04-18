
package com.kispoko.tome.util


import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.rts.entity.sheet.SheetUI
import com.kispoko.tome.rts.entity.theme.ThemeManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch



/**
 * Android Class Extensions
 *
 * Extensions give the appearance of adding methods to existing objects. This feature is useful,
 * because it allows us to create methods that can be called on existing Android class instances
 * with the dot syntax. What this really does though (I assume) is create a method which
 * takes 'this' as the first parameter and uses that implicity in our fake method. Of course, it
 * seems to be a solution to a problem that doesn't need to exist, since without OOP, you would
 * just write a function f(extended_class_instance, some_data1, some_data_2) and everything would
 * just be normal.
 */


fun AppCompatActivity.configureToolbar(title : String, fontStyle : TextFontStyle? = null)
{
    // (1) Configure Action Bar
    // -------------------------------------------------------------------------------------

    val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
    this.setSupportActionBar(toolbar)
    val actionBar = this.supportActionBar

    actionBar?.setDisplayShowTitleEnabled(false)

    // (2) Configure Title
    // -------------------------------------------------------------------------------------

    val titleView = this.findViewById<TextView>(R.id.toolbar_title)

    if (fontStyle != null)
        titleView.typeface  = Font.typeface(TextFont.default(), fontStyle, this)
    else
        titleView.typeface  = Font.typeface(TextFont.default(), TextFontStyle.Medium, this)

    val trimmedTitle = if (title.length >= 25) {
        title.substring(0, 25).plus("\u2026")
    }
    else {
        title
    }

    titleView.text      = trimmedTitle

    //titleView.setTextColor(SheetManager)

    // (3) Configure Back Button
    // -------------------------------------------------------------------------------------

    val backButtonView = this.findViewById<ImageView>(R.id.toolbar_back_button)
    backButtonView?.setOnClickListener { this.finish() }

    val closeButtonView = this.findViewById<ImageView>(R.id.toolbar_close_button)
    closeButtonView?.setOnClickListener { this.finish() }

}


//private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()

//val AppCompatActivity.messageListenerDisposable : CompositeDisposable //  = CompositeDisposable()


//private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()

//fun AppCompatActivity.initializeState()
//{
//    val activity = this as SheetUI
//    launch(UI) {
//        ThemeManager.loadOfficialThemes(activity.context())
//        SheetManager.startSession(activity)
//    }
//}
