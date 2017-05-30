
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Tab
 */
data class Tab(override val id : UUID,
               val name : Func<TabName>,
               val groups : Coll<Group>) : Model
{

    companion object : Factory<Tab>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Tab> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Tab,
                         // Model Id
                         valueResult(UUID.randomUUID()),
                         // Tab Name
                         doc.at("name") ap {
                             effApply(::Prim, TabName.fromDocument(it))
                         },
                         // Groups
                         doc.list("groups") ap { docList ->
                             effApply(::Coll,
                                 docList.map { Group.fromDocument(it) })
                         })
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Tab Name
 */
data class TabName(val value : String)
{

    companion object : Factory<TabName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TabName> = when (doc)
        {
            is DocText -> valueResult(TabName(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Tab Widget Row Format
 */
data class TabWidgetFormat(override val id : UUID,
                           val tabDefaultStyle : Func<TextStyle>,
                           val tabSelectedStyle : Func<TextStyle>,
                           val underlineSelected : Func<Boolean>,
                           val underlineThickness : Func<Int>,
                           val tabMargins : Func<Spacing>,
                           val tabPaddingVertical : Func<Int>,
                           val tabHeight : Func<Height>,
                           val backgroundColor : Func<ColorId>,
                           val tabCorners : Func<Corners>) : Model
{

    companion object : Factory<TabWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TabWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TabWidgetFormat,
                         // Model Id
                         valueResult(UUID.randomUUID()),
                         // Tab Default Style
                         split(doc.maybeAt("tab_default_style"),
                               valueResult<Func<TextStyle>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                   effApply(::Comp, TextStyle.fromDocument(d))),
                         // Tab Default Style
                         split(doc.maybeAt("tab_selected_style"),
                               valueResult<Func<TextStyle>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                   effApply(::Comp, TextStyle.fromDocument(d))),
                         // Underline Selected?
                         split(doc.maybeBoolean("underline_selected"),
                               valueResult<Func<Boolean>>(Null()),
                               { valueResult(Prim(it)) }),
                         // Underline Thickness
                         split(doc.maybeInt("underline_thickness"),
                               valueResult<Func<Int>>(Null()),
                               { valueResult(Prim(it)) }),
                         // Margins
                         split(doc.maybeAt("tab_margins"),
                               valueResult<Func<Spacing>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<Spacing>> =
                                   effApply(::Comp, Spacing.fromDocument(d))),
                         // Tab Padding Vertical
                         split(doc.maybeInt("tab_padding_vertical"),
                               valueResult<Func<Int>>(Null()),
                               { valueResult(Prim(it)) }),
                         // Tab Height
                         split(doc.maybeEnum<Height>("tab_height"),
                               valueResult<Func<Height>>(Null()),
                               { valueResult(Prim(it)) }),
                         // Background Color
                         split(doc.maybeAt("background_color"),
                               valueResult<Func<ColorId>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<ColorId>> =
                                   effApply(::Prim, ColorId.fromDocument(d))),
                         // Tab Corners
                         split(doc.maybeEnum<Corners>("tab_corners"),
                               valueResult<Func<Corners>>(Null()),
                               { valueResult(Prim(it)) })
                      )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeTabWidget()
//    {
//        // [1] Apply default formats
//        // -------------------------------------------------------------------------------------
//
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.NONE);
//
//        // [2] Current Tab Index
//        // -------------------------------------------------------------------------------------
//
//        this.currentTabIndex = this.defaultSelected();
//    }
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    private View widgetView(Context context)
//    {
//        LinearLayout layout = widgetViewLayout(context);
//
//        layout.addView(this.tabBarView(context));
//
//        layout.addView(this.groupsView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout widgetViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private View tabBarView(Context context)
//    {
//        LinearLayout layout = this.tabBarLayout(context);
//
//        // > Create tab text views
//        List<TextView> tabTextViews = new ArrayList<>();
//        List<LinearLayout> underlineViews = new ArrayList<>();
//
//        for (Tab tab : this.tabs())
//        {
//            TextView tabTextView = this.tabTextView(tab.name(), context);
//            tabTextViews.add(tabTextView);
//
//            LinearLayout underlineView = this.underlineView(context);
//            underlineViews.add(underlineView);
//
//            layout.addView(tabView(tabTextView, underlineView, context));
//        }
//
//        // Style default selected tab
//        this.styleTabTextViewSelected(tabTextViews.get(this.currentTabIndex - 1),
//                                      underlineViews.get(this.currentTabIndex - 1),
//                                      context);
//
//
//        return layout;
//    }
//
//
//    private LinearLayout tabBarLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.HORIZONTAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.marginSpacing    = this.data().format().margins();
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout tabView(TextView tabTextView,
//                                 LinearLayout underlineView,
//                                 Context context)
//    {
//        LinearLayout layout = tabViewLayout(context);
//
//        layout.addView(tabTextView);
//
//        layout.addView(underlineView);
//
//        return layout;
//    }
//
//
//    private LinearLayout tabViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation  = LinearLayout.VERTICAL;
//
//        layout.width        = 0;
//        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.weight       = 1.0f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout underlineView(Context context)
//    {
//        LinearLayoutBuilder line = new LinearLayoutBuilder();
//
//        line.orientation        = LinearLayout.VERTICAL;
//
//        line.width              = LinearLayout.LayoutParams.MATCH_PARENT;
//        line.heightDp           = this.format().underlineThickness();
//
//        line.backgroundColor    = this.resolveTabBackgroundColor().colorId();
//
//        line.margin.top         = R.dimen.four_dp;
//
//        return line.linearLayout(context);
//    }
//
//
//    private TextView tabTextView(String tabLabel, Context context)
//    {
//        TextViewBuilder tab = new TextViewBuilder();
//
//        tab.width       = LinearLayout.LayoutParams.WRAP_CONTENT;
//        tab.height      = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        tab.text        = tabLabel;
//
//        tab.gravity      = Gravity.CENTER;
//        tab.layoutGravity = Gravity.CENTER;
//
//        this.format().tabDefaultTextStyle().styleTextViewBuilder(tab, context);
//
//        // > Background Color
//        // -------------------------------------------------------------------------------------
//        tab.backgroundColor  = this.resolveTabBackgroundColor().colorId();
//
//        // > Background Resource
//        if (this.format().tabPaddingVertical() == null)
//        {
//            if (this.format().tabHeight() != null)
//            {
//                switch (this.format().tabHeight())
//                {
//                    case MEDIUM_SMALL:
//                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
//                        break;
//                    case MEDIUM:
//                        tab.backgroundResource = R.drawable.bg_tab_medium;
//                        break;
//                    default:
//                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
//                }
//            }
//            else
//            {
//                switch (this.format().tabDefaultTextStyle().size())
//                {
//                    case MEDIUM_SMALL:
//                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
//                        break;
//                    case MEDIUM:
//                        tab.backgroundResource = R.drawable.bg_tab_medium;
//                        break;
//                    default:
//                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
//                }
//            }
//        }
//        else
//        {
//            tab.padding.topDp    = this.format().tabPaddingVertical().floatValue();
//            tab.padding.bottomDp = this.format().tabPaddingVertical().floatValue();
//        }
//
//        return tab.textView(context);
//    }
//
//
//    private void styleTabTextViewSelected(TextView tabView,
//                                          LinearLayout underlineView,
//                                          Context context)
//    {
//        int colorResId = this.format().tabSelectedTextStyle().color().resourceId();
//
//        // > Set Text Color
//        tabView.setTextColor(ContextCompat.getColor(context, colorResId));
//
//        // > Set Text Size
//        int sizeResourceId = this.format().tabSelectedTextStyle().size().resourceId();
//        tabView.setTextSize(context.getResources().getDimensionPixelSize(sizeResourceId));
//
//        // > Underline Color
//        underlineView.setBackgroundColor(ContextCompat.getColor(context, colorResId));
//    }
//
//
//    private void styleTabTextViewDefault(TextView tabView, Context context)
//    {
//        // > Set Text Color
//        int colorResourceId = this.format().tabDefaultTextStyle().color().resourceId();
//        tabView.setTextColor(ContextCompat.getColor(context, colorResourceId));
//
//        // > Set Text Size
//        int sizeResourceId = this.format().tabDefaultTextStyle().size().resourceId();
//        tabView.setTextSize(context.getResources().getDimensionPixelSize(sizeResourceId));
//    }
//
//
//    private BackgroundColor resolveTabBackgroundColor()
//    {
//        BackgroundColor thisTabBgColor = this.format().tabBackgroundColor();
//        BackgroundColor thisBgColor = this.data().format().background();
//        BackgroundColor parentBgColor = this.groupParent.background();
//
//        if (thisTabBgColor != null && thisTabBgColor != BackgroundColor.NONE)
//            return thisTabBgColor;
//
//        if (thisBgColor != null && thisBgColor != BackgroundColor.NONE)
//            return thisBgColor;
//
//        return parentBgColor;
//    }
//
//
//    private LinearLayout groupsView(Context context)
//    {
//        LinearLayout layout = groupsViewLayout(context);
//
//        Tab currentTab = this.tabs().get(this.currentTabIndex - 1);
//
//        for (Group group : currentTab.groups())
//        {
//            layout.addView(group.view(context));
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout groupsViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//}
