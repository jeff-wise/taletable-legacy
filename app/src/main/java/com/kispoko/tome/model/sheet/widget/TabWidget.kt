
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Tab
 */
data class Tab(override val id : UUID,
               val tabName : Func<TabName>,
               val groups : Coll<Group>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.tabName.name       = "tab_name"
        this.groups.name        = "groups"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Tab>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Tab> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Tab,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Tab Name
                         doc.at("name") ap {
                             effApply(::Prim, TabName.fromDocument(it))
                         },
                         // Groups
                         doc.list("groups") ap { docList ->
                             effApply(::Coll, docList.mapIndexed {
                                 d,index -> Group.fromDocument(d,index) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "tab"

    override val modelObject = this

}


/**
 * Tab Name
 */
data class TabName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TabName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TabName> = when (doc)
        {
            is DocText -> effValue(TabName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Default Selected
 */
data class DefaultSelected(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefaultSelected>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DefaultSelected> = when (doc)
        {
            is DocNumber -> effValue(DefaultSelected(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Tab Widget Row Format
 */
data class TabWidgetFormat(override val id : UUID,
                           val widgetFormat : Comp<WidgetFormat>,
                           val tabDefaultStyle : Comp<TextStyle>,
                           val tabSelectedStyle : Comp<TextStyle>,
                           val underlineSelected : Prim<TabUnderlineSelected>,
                           val underlineThickness : Prim<TabUnderlineThickness>,
                           val tabMargins : Comp<Spacing>,
                           val tabPaddingVertical : Prim<TabVerticalPadding>,
                           val tabHeight : Prim<Height>,
                           val backgroundColorTheme : Prim<ColorTheme>,
                           val tabCorners : Comp<Corners>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name              = "widget_format"
        this.tabDefaultStyle.name           = "tab_default_style"
        this.tabSelectedStyle.name          = "tab_selected_style"
        this.underlineSelected.name         = "underline_selected"
        this.underlineThickness.name        = "underline_thickness"
        this.tabMargins.name                = "tab_margins"
        this.tabPaddingVertical.name        = "tab_padding_vertical"
        this.tabHeight.name                 = "tab_height"
        this.backgroundColorTheme.name      = "background_color_theme"
        this.tabCorners.name                = "tab_corners"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                tabDefaultStyle : TextStyle,
                tabSelectedstyle : TextStyle,
                underlineSelected : TabUnderlineSelected,
                underlineThickness : TabUnderlineThickness,
                tabMargins : Spacing,
                tabPaddingVertical: TabVerticalPadding,
                tabHeight : Height,
                backgroundColorTheme : ColorTheme,
                tabCorners : Corners)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(tabDefaultStyle),
               Comp(tabSelectedstyle),
               Prim(underlineSelected),
               Prim(underlineThickness),
               Comp(tabMargins),
               Prim(tabPaddingVertical),
               Prim(tabHeight),
               Prim(backgroundColorTheme),
               Comp(tabCorners))


    companion object : Factory<TabWidgetFormat>
    {

        private val defaultWidgetFormat         = WidgetFormat.default()
        private val defaultTabDefaultStyle      = TextStyle.default()
        private val defaultTabSelectedStyle     = TextStyle.default()
        private val defaultUnderlineSelected    = TabUnderlineSelected(true)
        private val defaultUnderlineThickness   = TabUnderlineThickness(2)
        private val defaultTabMargins           = Spacing.default()
        private val defaultTabPaddingVertical   = TabVerticalPadding(5)
        private val defaultTabHeight            = Height.Wrap
        private val defaultBackgroundColorTheme = ColorTheme.transparent
        private val defaultTabCorners           = Corners.default()


        override fun fromDocument(doc : SpecDoc) : ValueParser<TabWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TabWidgetFormat,
                         // Widget Format
                         split(doc.maybeAt("widget_format"),
                               effValue(defaultWidgetFormat),
                               { WidgetFormat.fromDocument(it) }),
                         // Tab Default Style
                         split(doc.maybeAt("tab_default_style"),
                               effValue(defaultTabDefaultStyle),
                               { TextStyle.fromDocument(it) }),
                         // Tab Selected Style
                         split(doc.maybeAt("tab_selected_style"),
                               effValue(defaultTabSelectedStyle),
                               { TextStyle.fromDocument(it) }),
                         // Underline Selected?
                         split(doc.maybeAt("underline_selected"),
                               effValue(defaultUnderlineSelected),
                               { TabUnderlineSelected.fromDocument(it) }),
                         // Underline Thickness
                         split(doc.maybeAt("underline_thickness"),
                               effValue(defaultUnderlineThickness),
                               { TabUnderlineThickness.fromDocument(it) }),
                         // Margins
                         split(doc.maybeAt("tab_margins"),
                               effValue(defaultTabMargins),
                               { Spacing.fromDocument(it) }),
                         // Tab Padding Vertical
                         split(doc.maybeAt("tab_padding_vertical"),
                               effValue(defaultTabPaddingVertical),
                               { TabVerticalPadding.fromDocument(it) }),
                         // Tab Height
                         split(doc.maybeAt("tab_height"),
                               effValue<ValueError,Height>(defaultTabHeight),
                               { Height.fromDocument(it) }),
                         // Background Color Theme
                         split(doc.maybeAt("background_color_theme"),
                               effValue(defaultBackgroundColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Tab Corners
                         split(doc.maybeAt("tab_corners"),
                               effValue<ValueError,Corners>(defaultTabCorners),
                               { Corners.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : TabWidgetFormat =
                TabWidgetFormat(defaultWidgetFormat,
                                defaultTabDefaultStyle,
                                defaultTabSelectedStyle,
                                defaultUnderlineSelected,
                                defaultUnderlineThickness,
                                defaultTabMargins,
                                defaultTabPaddingVertical,
                                defaultTabHeight,
                                defaultBackgroundColorTheme,
                                defaultTabCorners)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "tab_widget_format"

    override val modelObject = this

}



/**
 * Tab Underline Selected?
 */
data class TabUnderlineSelected(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TabUnderlineSelected>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TabUnderlineSelected> = when (doc)
        {
            is DocBoolean -> effValue(TabUnderlineSelected(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Tab Underline Thickness
 */
data class TabUnderlineThickness(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TabUnderlineThickness>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TabUnderlineThickness> = when (doc)
        {
            is DocNumber -> effValue(TabUnderlineThickness(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Tab Vertical Padding
 */
data class TabVerticalPadding(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TabVerticalPadding>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TabVerticalPadding> = when (doc)
        {
            is DocNumber -> effValue(TabVerticalPadding(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

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
