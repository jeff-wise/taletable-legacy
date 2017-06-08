
package com.kispoko.tome.util


import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font


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


fun AppCompatActivity.configureToolbar(title : String)
{
    // (1) Configure Action Bar
    // -------------------------------------------------------------------------------------

    val toolbar = this.findViewById(R.id.toolbar) as Toolbar
    this.setSupportActionBar(toolbar)
    val actionBar = this.supportActionBar

    actionBar?.setDisplayShowTitleEnabled(false)

    // (2) Configure Title
    // -------------------------------------------------------------------------------------

    val titleView = this.findViewById(R.id.toolbar_title) as TextView

    titleView.typeface = Font.serifFontRegular(this)
    titleView.text = title

    // (3) Configure Back Button
    // -------------------------------------------------------------------------------------

    val backButtonView = this.findViewById(R.id.toolbar_back_button) as? ImageView
    backButtonView?.setOnClickListener { this.finish() }

}
