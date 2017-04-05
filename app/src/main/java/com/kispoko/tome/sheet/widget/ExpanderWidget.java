
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.expander.ExpanderWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Expander Widget
 */
public class ExpanderWidget extends Widget
                            implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private CollectionFunctor<Group>            groups;
    private ModelFunctor<ExpanderWidgetFormat>  format;
    private ModelFunctor<WidgetData>            widgetData;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    public ExpanderWidget()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.groups     = CollectionFunctor.empty(Group.class);
        this.format     = ModelFunctor.empty(ExpanderWidgetFormat.class);
        this.widgetData = ModelFunctor.empty(WidgetData.class);
    }


    public ExpanderWidget(UUID id,
                          String name,
                          List<Group> groups,
                          ExpanderWidgetFormat format,
                          WidgetData widgetData)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.groups     = CollectionFunctor.full(groups, Group.class);
        this.format     = ModelFunctor.full(format, ExpanderWidgetFormat.class);
        this.widgetData = ModelFunctor.full(widgetData, WidgetData.class);
    }


    /**
     * Create a new Expander Widget from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Exapnder Widget.
     * @throws YamlParseException
     */
    public static ExpanderWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                 id         = UUID.randomUUID();

        String               name       = yaml.atKey("name").getString();

        List<Group>          groups     = yaml.atMaybeKey("groups").forEach(
                                                            new YamlParser.ForEach<Group>() {
            @Override
            public Group forEach(YamlParser yaml, int index) throws YamlParseException {
                return Group.fromYaml(yaml, index);
            }
        }, true);

        ExpanderWidgetFormat format     = ExpanderWidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        WidgetData           widgetData = WidgetData.fromYaml(yaml.atMaybeKey("data"));

        return new ExpanderWidget(id, name, groups, format, widgetData);
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
        this.initializeExpanderWidget();
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
                .putString("name", this.name())
                .putList("groups", this.groups())
                .putYaml("format", this.format())
                .putYaml("data", this.data());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent, Context context) { }


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
        return this.widgetView(rowHasLabel, context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The expander name.
     * @return The name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The groups that are expanded/hidden.
     * @return The groups.
     */
    public List<Group> groups()
    {
        return this.groups.getValue();
    }


    /**
     * The expander widget formatting options.
     * @return The format.
     */
    public ExpanderWidgetFormat format()
    {
        return this.format.getValue();
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeExpanderWidget()
    {
        // [1] Apply default formats
        // -------------------------------------------------------------------------------------

        if (this.data().format().alignmentIsDefault())
            this.data().format().setAlignment(Alignment.CENTER);

        if (this.data().format().backgroundIsDefault())
            this.data().format().setBackground(BackgroundColor.NONE);

        if (this.data().format().cornersIsDefault())
            this.data().format().setCorners(Corners.NONE);
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = this.layout(rowHasLabel, context);

        layout.addView(mainView(context));

        return layout;
    }


    private LinearLayout mainView(Context context)
    {
        LinearLayout layout = mainViewLayout(context);

        // > Header View
        // -----------------------------------------------------------
        layout.addView(headerView(context));

        return layout;
    }


    private LinearLayout mainViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = this.data().format().background().colorId();
        layout.backgroundResource   = this.data().format().corners().resourceId();


        Spacing margins = this.data().format().margins();

        if (this.data().format().elevation() != null)
        {
            layout.elevation        = this.data().format().elevation().floatValue();

            if (margins.bottom() < 3)
                margins.setBottom(3);

            if (margins.left() < 3)
                margins.setLeft(3);

            if (margins.right() < 3)
                margins.setRight(3);
        }

        layout.marginSpacing        = margins;

        return layout.linearLayout(context);
    }


    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = headerViewLayout(context);

        this.onExpanderClick(layout);

        // > Name View
        layout.addView(nameView(context));


        return layout;
    }


    private LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.paddingSpacing   = this.format().headerPadding();

        return layout.linearLayout(context);
    }

    private RelativeLayout nameView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout  = new RelativeLayoutBuilder();

        TextViewBuilder       name    = new TextViewBuilder();
        ImageViewBuilder      icon    = new ImageViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.child(name)
              .child(icon);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.layoutType         = LayoutType.RELATIVE;
        name.width              = RelativeLayout.LayoutParams.WRAP_CONTENT;
        name.height             = RelativeLayout.LayoutParams.WRAP_CONTENT;

        name.text               = this.name();

        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        name.addRule(RelativeLayout.CENTER_VERTICAL);

        this.format().nameStyleClosed().styleTextViewBuilder(name, context);

        // [3 B] Icon
        // -------------------------------------------------------------------------------------

        icon.layoutType         = LayoutType.RELATIVE;
        icon.width              = RelativeLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = RelativeLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_expander_more;

        //icon.color              = this.format().nameStyleClosed().color().resourceId();
        icon.color              = R.color.dark_blue_hl_8;

        icon.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        icon.addRule(RelativeLayout.CENTER_VERTICAL);


        return layout.relativeLayout(context);
    }


    private void onExpanderClick(LinearLayout layout)
    {
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }


}
