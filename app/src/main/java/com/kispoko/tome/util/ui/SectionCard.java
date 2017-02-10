
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kispoko.tome.R;



/**
 * Section Card
 */
public class SectionCard
{

    public static RelativeLayout view(int titleStringId,
                                      int iconId,
                                      int descriptionStringId,
                                      Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout        = new RelativeLayoutBuilder();

        TextViewBuilder       title         = new TextViewBuilder();
        ImageViewBuilder      icon          = new ImageViewBuilder();
        TextViewBuilder       description   = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.LINEAR;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = 0;
        layout.weight               = 1.0f;

        layout.backgroundResource   = R.drawable.bg_section_card;

        layout.margin.top           = R.dimen.section_card_margin_top;

        layout.child(title)
              .child(icon)
              .child(description);

        // [3] Title
        // --------------------------------------------------------------------------------------

        title.layoutType    = LayoutType.RELATIVE;
        title.width         = RelativeLayout.LayoutParams.WRAP_CONTENT;
        title.height        = RelativeLayout.LayoutParams.WRAP_CONTENT;

        title.textId        = titleStringId;
        title.font          = Font.sansSerifFontBold(context);
        title.color         = R.color.gold_light;
        title.size          = R.dimen.section_card_title_text_size;
        //title.margin.bottom = R.dimen.section_card_title_margin_bottom;

        title.addRule(RelativeLayout.CENTER_HORIZONTAL);

        // [4] Icon
        // --------------------------------------------------------------------------------------

        icon.layoutType     = LayoutType.RELATIVE;
        icon.width          = RelativeLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = RelativeLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = iconId;
        //icon.margin.bottom  = R.dimen.section_card_icon_margin_bottom;

        icon.addRule(RelativeLayout.CENTER_IN_PARENT);

        // [5] Description
        // --------------------------------------------------------------------------------------

        description.layoutType      = LayoutType.RELATIVE;
        description.width           = RelativeLayout.LayoutParams.WRAP_CONTENT;
        description.height          = RelativeLayout.LayoutParams.WRAP_CONTENT;

        description.textId          = descriptionStringId;
        description.color           = R.color.dark_blue_hl_8;
        description.size            = R.dimen.section_card_description_text_size;
        description.font            = Font.sansSerifFontRegular(context);

        description.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        return layout.relativeLayout(context);
    }


}
