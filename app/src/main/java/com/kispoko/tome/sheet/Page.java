
package com.kispoko.tome.sheet;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.value.Functor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;



/**
 * Sheet Page
 *
 * A page corresponds to a real-life piece of paper, where each page has fields that are related
 * to a specific theme. The fields are cotained in a list of groups, which group related
 * character content.
 */
public class Page implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                                id;

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<BackgroundColor>   background;
    private PrimitiveFunctor<Integer>           index;
    private CollectionFunctor<Group>            groups;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private int                                 pageViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Page()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.background     = new PrimitiveFunctor<>(null, BackgroundColor.class);
        this.index          = new PrimitiveFunctor<>(null, Integer.class);

        this.groups         = CollectionFunctor.empty(Group.class);
    }


    public Page(UUID id,
                String name,
                BackgroundColor background,
                Integer index,
                List<Group> groups)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.background     = new PrimitiveFunctor<>(background, BackgroundColor.class);
        this.index          = new PrimitiveFunctor<>(index, Integer.class);

        this.groups         = CollectionFunctor.full(groups, Group.class);

        this.setBackground(background);

        this.initializePage();
    }


    public static Page fromYaml(YamlParser yaml, int pageIndex)
                  throws YamlParseException
    {
        UUID            id          = UUID.randomUUID();

        String          name        = yaml.atKey("name").getString();
        BackgroundColor background  = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));
        Integer             index   = pageIndex;

        List<Group>     groups      = yaml.atKey("groups").forEach(new YamlParser.ForEach<Group>() {
            @Override
            public Group forEach(YamlParser yaml, int index) throws YamlParseException {
                return Group.fromYaml(yaml, index);
            }
        }, true);

        return new Page(id, name, background, index, groups);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Page is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializePage();
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the page.
     */
    public void initialize()
    {
        // Initialize each group
        for (Group group : this.groups()) {
            group.initialize();
        }
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The page's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putYaml("background", this.background())
                .putList("groups", this.groups());
    }

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Label
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the label of this page.
     * @return The page label.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Background
    // ------------------------------------------------------------------------------------------

    /**
     * The page background.
     * @return The background.
     */
    public BackgroundColor background()
    {
        return this.background.getValue();
    }


    public void setBackground(BackgroundColor background)
    {
        if (background != null)
            this.background.setValue(background);
        else
            this.background.setValue(BackgroundColor.MEDIUM);
    }


    // ** Index
    // ------------------------------------------------------------------------------------------

    public Integer index()
    {
        return this.index.getValue();
    }


    // ** Groups
    // ------------------------------------------------------------------------------------------

    public List<Group> groups()
    {
        return this.groups.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    /**
     * Returns an android view that represents this page.
     * @return A View of this page.
     */
    public View view()
    {
        Context context = SheetManager.currentSheetContext();

        LinearLayout layout = viewLayout(context);

        updateView(layout);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        this.pageViewId         = Util.generateViewId();
        layout.id               = this.pageViewId;

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundColor  = this.background().colorId();

        return layout.linearLayout(context);
    }


    private void updateView(LinearLayout pageLayout)
    {
        // > Ensure page layout is available
        // --------------------------------------------------------------------------------------
        Context context = SheetManager.currentSheetContext();
        if (pageLayout == null)
            pageLayout = (LinearLayout) ((Activity) context).findViewById(this.pageViewId);

        // > Views
        // --------------------------------------------------------------------------------------

        // ** Groups
        // --------------------------------------------------------------------------------------
        if (!this.groups.isNull()) {
            for (Group group : this.groups.getValue()) {
                pageLayout.addView(group.view(context));
            }
        }
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the page state.
     */
    private void initializePage()
    {
        // [1] Sort the groups
        // --------------------------------------------------------------------------------------

        this.sortGroups();

        // [2] Add group update listener to ensure they are always sorted
        // --------------------------------------------------------------------------------------

        this.groups.setOnUpdateListener(new Functor.OnUpdateListener() {
            @Override
            public void onUpdate() {
                sortGroups();
            }
        });
    }


    /**
     * Sort the pages by their index value, so they are displayed in the intended order.
     */
    private void sortGroups()
    {
        if (this.groups.isNull())
            return;

        // Make sure groups are sorted
        Collections.sort(this.groups.getValue(), new Comparator<Group>() {
            @Override
            public int compare(Group group1, Group group2) {
                if (group1.index() > group2.index())
                    return 1;
                if (group1.index() < group2.index())
                    return -1;
                return 0;
            }
        });
    }


}



