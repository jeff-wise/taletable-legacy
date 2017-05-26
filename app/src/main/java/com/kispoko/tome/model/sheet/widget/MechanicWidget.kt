
package com.kispoko.tome.model.sheet.widget



/**
 * Mechanic Widget
 */



//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeMechanicWidget()
//    {
//        // [1] Apply default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//    }
//
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private View widgetView(Context context)
//    {
//        LinearLayout layout = widgetViewLayout(context);
//
//        Set<Mechanic> mechanics = SheetManagerOld.currentSheet().engine().mechanicIndex()
//                                              .mechanicsInCategory(this.mechanicCategory(), true);
//
//        for (Mechanic mechanic : mechanics) {
//            layout.addView(mechanicView(mechanic.label(), mechanic.summary(), context));
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout widgetViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout mechanicView(String nameText, String descriptionText, Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
//        TextViewBuilder     name        = new TextViewBuilder();
//        TextViewBuilder     description = new TextViewBuilder();
//        LinearLayoutBuilder divider     = new LinearLayoutBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.margin.bottom        = R.dimen.widget_mechanic_item_margin_bottom;
//
//        layout.child(name)
//              .child(description)
//              .child(divider);
//
//        // [3 A] Name
//        // -------------------------------------------------------------------------------------
//
//        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        name.text                   = nameText;
//        name.font                   = Font.serifFontRegular(context);
//        name.color                  = R.color.dark_blue_hl_1;
//        name.size                   = R.dimen.widget_mechanic_name_text_size;
//
//        name.padding.left           = R.dimen.widget_mechanic_item_padding_horz;
//        name.padding.right          = R.dimen.widget_mechanic_item_padding_horz;
//
//        name.margin.bottom          = R.dimen.widget_mechanic_item_name_margin_bottom;
//
//        // [3 B] Description
//        // -------------------------------------------------------------------------------------
//
//        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
//        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        description.text            = descriptionText;
//        description.font            = Font.serifFontRegular(context);
//        description.size            = R.dimen.widget_mechanic_description_text_size;
//        description.color           = R.color.dark_blue_hl_8;
//
//        description.padding.left    = R.dimen.widget_mechanic_item_padding_horz;
//        description.padding.right   = R.dimen.widget_mechanic_item_padding_horz;
//
//        description.margin.bottom   = R.dimen.widget_mechanic_item_description_margin_bottom;
//
//        // [3 C] Divider
//        // -------------------------------------------------------------------------------------
//
//        divider.orientation          = LinearLayout.HORIZONTAL;
//        divider.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        divider.height               = R.dimen.one_dp;
//
//        divider.backgroundColor      = R.color.dark_blue_4;
//
//
//        return layout.linearLayout(context);
//    }
//}