
package com.taletable.android.activity.sheet


import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.UIColors
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack




/**
 * Bottom Navigation View Builder
 */
//class BottomNavigationViewBuilder(val uiColors : UIColors,
//                                  val sheetId : EntityId,
//                                  val context : Context)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEWS
//    // -----------------------------------------------------------------------------------------
//
//    fun view() : LinearLayout
//    {
//        val layout = this.viewLayout()
//
//        // Sheets Button
//        val onSheetsClick = View.OnClickListener {
//            val sheetActivity = context as SessionActivity
//            val intent = Intent(sheetActivity, SheetNavigationActivity::class.java)
//            sheetActivity.startActivity(intent)
//        }
//        layout.addView(this.buttonView(R.string.sheets, R.drawable.icon_documents, 26, onSheetsClick))
//
//        // Campaigns Button
//        val onCampaignsClick = View.OnClickListener {  }
//        layout.addView(this.buttonView(R.string.campaigns, R.drawable.icon_adventure, 25, onCampaignsClick))
//
//        // Games Button
//        val onGamesClick = View.OnClickListener {
//            val sheetActivity = context as SessionActivity
//            val intent = Intent(sheetActivity, GameNavigationActivity::class.java)
//            sheetActivity.startActivity(intent)
//        }
//        layout.addView(this.buttonView(R.string.games, R.drawable.icon_book, 25, onGamesClick))
//
//        return layout
//    }
//
//
//    private fun viewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        val bgColorId = this.uiColors.bottomBarBackgroundColorId()
//        layout.backgroundColor  = colorOrBlack(bgColorId, sheetId)
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.padding.topDp    = 6f
//
//        return layout.linearLayout(this.context)
//    }
//
//
//    private fun buttonView(labelStringId : Int,
//                           iconId : Int,
//                           iconSize : Int,
//                           onClick : View.OnClickListener) : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout      = LinearLayoutBuilder()
//        val label       = TextViewBuilder()
//        val icon        = ImageViewBuilder()
//
//        val navColor    = colorOrBlack(this.uiColors.bottomBarNavColorId(), sheetId)
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width        = 0
//        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.weight       = 1f
//
//        layout.gravity      = Gravity.CENTER
//
//        layout.orientation  = LinearLayout.VERTICAL
//
//        layout.onClick      = onClick
//
//        layout.child(icon)
//              .child(label)
//
//        // (3 A) Label
//        // -------------------------------------------------------------------------------------
//
//        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.textId        = labelStringId
//
//        label.color         = navColor
//
//        label.font          = Font.typeface(TextFont.default(),
//                                            TextFontStyle.Light,
//                                            this.context)
//
//        label.sizeSp        = 13f
//
//        // (3) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.widthDp          = iconSize
//        icon.heightDp         = iconSize
//
//        icon.image            = iconId
//
//        icon.color            = navColor
//
//        return layout.linearLayout(this.context)
//    }
//
//
//}
//
//

