
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.tab.Tab;
import com.kispoko.tome.sheet.widget.tab.TabWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Tab Widget
 */
public class TabWidget extends Widget implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private CollectionFunctor<Tab>          tabs;
    private PrimitiveFunctor<Integer>       defaultSelected;

    private ModelFunctor<TabWidgetFormat>   format;
    private ModelFunctor<WidgetData>        widgetData;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private Integer                         currentTabIndex;

    private GroupParent                     groupParent;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TabWidget()
    {
        this.id                 = null;

        this.tabs               = CollectionFunctor.empty(Tab.class);
        this.defaultSelected    = new PrimitiveFunctor<>(null, Integer.class);

        this.format             = ModelFunctor.empty(TabWidgetFormat.class);
        this.widgetData         = ModelFunctor.empty(WidgetData.class);
    }


    public TabWidget(UUID id,
                     List<Tab> tabs,
                     Integer defaultSelected,
                     TabWidgetFormat format,
                     WidgetData widgetData)
    {
        this.id                 = id;

        this.tabs               = CollectionFunctor.full(tabs, Tab.class);
        this.defaultSelected    = new PrimitiveFunctor<>(defaultSelected, Integer.class);

        this.format             = ModelFunctor.full(format, TabWidgetFormat.class);
        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);

        this.setDefaultSelected(defaultSelected);

        this.initializeTabWidget();
    }


    /**
     * Create a Tab Widget from its yaml representation.
     * @param yaml The yaml parser.
     * @return The pasred Tab Widget.
     * @throws YamlParseException
     */
    public static TabWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID            id              = UUID.randomUUID();

        List<Tab>       tabs            = yaml.atMaybeKey("tabs").forEach(
                                                                new YamlParser.ForEach<Tab>() {
            @Override
            public Tab forEach(YamlParser yaml, int index) throws YamlParseException {
                return Tab.fromYaml(yaml);
            }
        }, true);

        Integer         defaultSelected = yaml.atMaybeKey("default_selected").getInteger();

        TabWidgetFormat format          = TabWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        WidgetData      widgetData      = WidgetData.fromYaml(yaml.atMaybeKey("data"), false);

        return new TabWidget(id, tabs, defaultSelected, format, widgetData);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeTabWidget();
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Expander Widget's yaml representation.
     * @return The yaml builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putList("tabs", this.tabs())
                .putInteger("default_selected", this.defaultSelected())
                .putYaml("format", this.format())
                .putYaml("data", this.data());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent)
    {
        this.groupParent = groupParent;
    }


    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    /**
     * The text widget's tile view.
     * @return The tile view.
     */
    @Override
    public View view(boolean rowHasLabel, Context context)
    {
        return this.widgetView(context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Tabs
    // ------------------------------------------------------------------------------------------

    /**
     * The tabs.
     * @return The tab list.
     */
    public List<Tab> tabs()
    {
        return this.tabs.getValue();
    }


    // ** Default Selected
    // ------------------------------------------------------------------------------------------

    /**
     * The index of the tab that is selected by default.
     * @return The tab index.
     */
    public Integer defaultSelected()
    {
        return this.defaultSelected.getValue();
    }


    /**
     * Set the tab index of the default selected tab.
     * @param tabIndex The tab index.
     */
    public void setDefaultSelected(Integer tabIndex)
    {
        if (tabIndex == null ||
            tabIndex < 0 ||
            tabIndex > this.tabs().size())
        {
            this.defaultSelected.setValue(1);
        }
        else
        {
            this.defaultSelected.setValue(tabIndex);
        }
    }


    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The tab widget formatting options.
     * @return The format.
     */
    public TabWidgetFormat format()
    {
        return this.format.getValue();
    }

    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Initialize
    // ------------------------------------------------------------------------------------------

    private void initializeTabWidget()
    {
        // [1] Apply default formats
        // -------------------------------------------------------------------------------------

        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        if (this.data().format().alignment() == null)
            this.data().format().setAlignment(Alignment.CENTER);

        if (this.data().format().background() == null)
            this.data().format().setBackground(BackgroundColor.NONE);

        if (this.data().format().corners() == null)
            this.data().format().setCorners(Corners.NONE);

        // [2] Current Tab Index
        // -------------------------------------------------------------------------------------

        this.currentTabIndex = this.defaultSelected();
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private View widgetView(Context context)
    {
        LinearLayout layout = widgetViewLayout(context);

        layout.addView(this.tabBarView(context));

        layout.addView(this.groupsView(context));

        return layout;
    }


    private LinearLayout widgetViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private View tabBarView(Context context)
    {
        LinearLayout layout = this.tabBarLayout(context);

        // > Create tab text views
        List<TextView> tabTextViews = new ArrayList<>();
        List<LinearLayout> underlineViews = new ArrayList<>();

        for (Tab tab : this.tabs())
        {
            TextView tabTextView = this.tabTextView(tab.name(), context);
            tabTextViews.add(tabTextView);

            LinearLayout underlineView = this.underlineView(context);
            underlineViews.add(underlineView);

            layout.addView(tabView(tabTextView, underlineView, context));
        }

        // Style default selected tab
        this.styleTabTextViewSelected(tabTextViews.get(this.currentTabIndex - 1),
                                      underlineViews.get(this.currentTabIndex - 1),
                                      context);


        return layout;
    }


    private LinearLayout tabBarLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.marginSpacing    = this.data().format().margins();

        return layout.linearLayout(context);
    }


    private LinearLayout tabView(TextView tabTextView,
                                 LinearLayout underlineView,
                                 Context context)
    {
        LinearLayout layout = tabViewLayout(context);

        layout.addView(tabTextView);

        layout.addView(underlineView);

        return layout;
    }


    private LinearLayout tabViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation  = LinearLayout.VERTICAL;

        layout.width        = 0;
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight       = 1.0f;

        return layout.linearLayout(context);
    }


    private LinearLayout underlineView(Context context)
    {
        LinearLayoutBuilder line = new LinearLayoutBuilder();

        line.orientation        = LinearLayout.VERTICAL;

        line.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        line.heightDp           = this.format().underlineThickness();

        line.backgroundColor    = this.resolveTabBackgroundColor().colorId();

        line.margin.top         = R.dimen.four_dp;

        return line.linearLayout(context);
    }


    private TextView tabTextView(String tabLabel, Context context)
    {
        TextViewBuilder tab = new TextViewBuilder();

        tab.width       = LinearLayout.LayoutParams.WRAP_CONTENT;
        tab.height      = LinearLayout.LayoutParams.WRAP_CONTENT;

        tab.text        = tabLabel;

        tab.gravity      = Gravity.CENTER;
        tab.layoutGravity = Gravity.CENTER;

        this.format().tabDefaultTextStyle().styleTextViewBuilder(tab, context);

        // > Background Color
        // -------------------------------------------------------------------------------------
        tab.backgroundColor  = this.resolveTabBackgroundColor().colorId();

        // > Background Resource
        if (this.format().tabPaddingVertical() == null)
        {
            if (this.format().tabHeight() != null)
            {
                switch (this.format().tabHeight())
                {
                    case MEDIUM_SMALL:
                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
                        break;
                    case MEDIUM:
                        tab.backgroundResource = R.drawable.bg_tab_medium;
                        break;
                    default:
                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
                }
            }
            else
            {
                switch (this.format().tabDefaultTextStyle().size())
                {
                    case MEDIUM_SMALL:
                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
                        break;
                    case MEDIUM:
                        tab.backgroundResource = R.drawable.bg_tab_medium;
                        break;
                    default:
                        tab.backgroundResource = R.drawable.bg_tab_medium_small;
                }
            }
        }
        else
        {
            tab.padding.topDp    = this.format().tabPaddingVertical();
            tab.padding.bottomDp = this.format().tabPaddingVertical();
        }

        return tab.textView(context);
    }


    private void styleTabTextViewSelected(TextView tabView,
                                          LinearLayout underlineView,
                                          Context context)
    {
        int colorResId = this.format().tabSelectedTextStyle().color().resourceId();

        // > Set Text Color
        tabView.setTextColor(ContextCompat.getColor(context, colorResId));

        // > Set Text Size
        int sizeResourceId = this.format().tabSelectedTextStyle().size().resourceId();
        tabView.setTextSize(context.getResources().getDimensionPixelSize(sizeResourceId));

        // > Underline Color
        underlineView.setBackgroundColor(ContextCompat.getColor(context, colorResId));
    }


    private void styleTabTextViewDefault(TextView tabView, Context context)
    {
        // > Set Text Color
        int colorResourceId = this.format().tabDefaultTextStyle().color().resourceId();
        tabView.setTextColor(ContextCompat.getColor(context, colorResourceId));

        // > Set Text Size
        int sizeResourceId = this.format().tabDefaultTextStyle().size().resourceId();
        tabView.setTextSize(context.getResources().getDimensionPixelSize(sizeResourceId));
    }


    private BackgroundColor resolveTabBackgroundColor()
    {
        BackgroundColor thisTabBgColor = this.format().tabBackgroundColor();
        BackgroundColor thisBgColor = this.data().format().background();
        BackgroundColor parentBgColor = this.groupParent.background();

        if (thisTabBgColor != null && thisTabBgColor != BackgroundColor.NONE)
            return thisTabBgColor;

        if (thisBgColor != null && thisBgColor != BackgroundColor.NONE)
            return thisBgColor;

        return parentBgColor;
    }


    private LinearLayout groupsView(Context context)
    {
        LinearLayout layout = groupsViewLayout(context);

        Tab currentTab = this.tabs().get(this.currentTabIndex - 1);

        for (Group group : currentTab.groups())
        {
            layout.addView(group.view(context));
        }

        return layout;
    }


    private LinearLayout groupsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }
}
