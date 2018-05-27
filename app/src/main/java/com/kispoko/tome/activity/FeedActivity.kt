
package com.kispoko.tome.activity


import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.util.Util
import com.kispoko.tome.util.configureToolbar
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout



/**
 * Feed Activity
 */
class FeedActivity : AppCompatActivity(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Any>
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_feed)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.tome))

        this.findViewById<TextView>(R.id.toolbar_title)?.let { titleTextView ->
            titleTextView.textSize = Util.spToPx(7.5f, this).toFloat()

        }

        this.applyTheme(officialAppThemeLight)

        this.initializeView()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeView()
    {
        val contentView = this.findViewById<LinearLayout>(R.id.content)

    }


    private fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.decorView.setSystemUiVisibility(flags)
//            this.getWindow().setStatusBarColor(Color.WHITE);
//        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_main_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }


    // -----------------------------------------------------------------------------------------
    // FLOATING ACTION BUTTON
    // -----------------------------------------------------------------------------------------

    private fun initializeFAB()
    {
        val rfaContent = RapidFloatingActionContentLabelList(this)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)

        val items = mutableListOf<RFACLabelItem<Int>>()
        items.add(RFACLabelItem<Int>()
                .setLabel("Github: wangjiegulu")
//                .setResId(R.mipmap.ico_test_d)
                .setIconNormalColor(-0x27bceb)
                .setIconPressedColor(-0x40c9f4)
                .setWrapper(0)
        )
        items.add(RFACLabelItem<Int>()
                .setLabel("tiantian.china.2@gmail.com")
//                .setResId(R.mipmap.ico_test_c)
                .setIconNormalColor(-0xb1cbd2)
                .setIconPressedColor(-0xc1d8dd)
                .setLabelColor(Color.WHITE)
                .setLabelSizeSp(14)
//                .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(-0x56000000, ABTextUtil.dip2px(context, 4)))
                .setWrapper(1)
        )
        items.add(RFACLabelItem<Int>()
                .setLabel("WangJie")
//                .setResId(R.mipmap.ico_test_b)
                .setIconNormalColor(-0xfa9100)
                .setIconPressedColor(-0xf2acfe)
                .setLabelColor(-0xfa9100)
                .setWrapper(2)
        )
        items.add(RFACLabelItem<Int>()
                .setLabel("Compose")
//                .setResId(R.mipmap.ico_test_a)
                .setIconNormalColor(-0xd7ca6d)
                .setIconPressedColor(-0xe5dc82)
                .setLabelColor(-0xd7ca6d)
                .setWrapper(3)
        )
        rfaContent
                .setItems(items.toList())
//                .setIconShadowRadius(ABTextUtil.dip2px(context, 5))
                .setIconShadowColor(-0x777778)
//                .setIconShadowDy(ABTextUtil.dip2px(context, 5))

        val fabLayout = this.findViewById<RapidFloatingActionLayout>(R.id.fab_layout)
        val fabView = this.findViewById<RapidFloatingActionButton>(R.id.fab)

        val rfabHelper = RapidFloatingActionHelper(
                            this,
                            fabLayout,
                            fabView,
                            rfaContent
        ).build()

    }


    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<Any>?) {
    }


    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<Any>?) {
    }

}


class FeedUI(val theme : Theme,
             val activity : AppCompatActivity)
{


}
