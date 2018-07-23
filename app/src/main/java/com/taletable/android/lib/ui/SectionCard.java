
package com.taletable.android.lib.ui;


import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.taletable.android.R;



/**
 * Section Card
 */
public class SectionCard
{


    public enum Color
    {
        GOLD,
        RED_ORANGE,
        RED;
    }


    public static RelativeLayout view(int titleStringId,
                                      int iconId,
                                      int descriptionStringId,
                                      Color color,
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

        layout.margin.bottom        = R.dimen.section_card_margin_top;

        layout.child(title)
              .child(icon)
              .child(description);

        // [3] Title
        // --------------------------------------------------------------------------------------

        title.layoutType    = LayoutType.RELATIVE;
        title.width         = RelativeLayout.LayoutParams.WRAP_CONTENT;
        title.height        = RelativeLayout.LayoutParams.WRAP_CONTENT;

        title.textId        = titleStringId;
//        title.font          = Font.serifFontItalic(context);
        title.size          = R.dimen.section_card_title_text_size;
        title.color         = R.color.gold_light;

//        // > Title Color
//        switch (color)
//        {
//            case GOLD:
//                title.color = R.color.gold_medium_light;
//                break;
//            case RED_ORANGE:
//                title.color = R.color.red_orange_light;
//                break;
//            case RED:
//                title.color = R.color.red_light;
//                break;
//            default:
//                title.color = R.color.gold_medium_light;
//        }

        title.addRule(RelativeLayout.CENTER_HORIZONTAL);

        // [4] Icon
        // --------------------------------------------------------------------------------------

        icon.layoutType     = LayoutType.RELATIVE;
        icon.width          = RelativeLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = RelativeLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = iconId;

//        switch (color)
//        {
//            case GOLD:
//                icon.color = R.color.gold_medium_dark;
//                break;
//            case RED_ORANGE:
//                icon.color = R.color.red_orange_light;
//                break;
//            case RED:
//                icon.color = R.color.red_light;
//                break;
//            default:
//                icon.color = R.color.gold_medium_light;
//        }

        icon.addRule(RelativeLayout.CENTER_IN_PARENT);

        // [5] Description
        // --------------------------------------------------------------------------------------

        description.layoutType      = LayoutType.RELATIVE;
        description.width           = RelativeLayout.LayoutParams.WRAP_CONTENT;
        description.height          = RelativeLayout.LayoutParams.WRAP_CONTENT;

        description.textId          = descriptionStringId;
        description.size            = R.dimen.section_card_description_text_size;
//        description.font            = Font.serifFontRegular(context);

        description.gravity         = Gravity.CENTER_HORIZONTAL;
        description.color           = R.color.dark_blue_hl_8;

//        switch (color)
//        {
//            case GOLD:
//                description.color   = R.color.gold_medium_dark;
//                break;
//            case RED_ORANGE:
//                description.color   = R.color.red_orange_light;
//                break;
//            case RED:
//                description.color   = R.color.red_light;
//                break;
//            default:
//                description.color   = R.color.gold_medium_light;
//        }

        description.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        return layout.relativeLayout(context);
    }


}
