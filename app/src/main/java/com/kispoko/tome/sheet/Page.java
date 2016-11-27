
package com.kispoko.tome.sheet;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.kispoko.tome.R.id.textView;


/**
 * Sheet Page
 *
 * A page corresponds to a real-life piece of paper, where each page has fields that are related
 * to a specific theme. The fields are cotained in a list of groups, which group related
 * character content.
 */
public class Page implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                    id;

    private PrimitiveValue<String>  label;
    private PrimitiveValue<Integer> index;
    private CollectionValue<Group>  groups;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private int                     pageViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Page()
    {
        this.id = null;
        initialize();
    }


    public Page(UUID id, String label, Integer index, List<Group> groups)
    {
        this.id = id;

        initialize();

        this.label.setValue(label);
        this.index.setValue(index);
        this.groups.setValue(groups);

        // Make sure groups are sorted
        Collections.sort(groups, new Comparator<Group>() {
            @Override
            public int compare(Group group1, Group group2) {
                if (group1.getIndex() > group2.getIndex())
                    return 1;
                if (group1.getIndex() < group2.getIndex())
                    return -1;
                return 0;
            }
        });
    }


    public static Page fromYaml(Yaml yaml, int pageIndex)
                  throws YamlException
    {
        UUID       id     = UUID.randomUUID();
        String     label  = yaml.atKey("label").getString();
        Integer    index  = pageIndex;

        List<Group> groups = yaml.atKey("groups").forEach(new Yaml.ForEach<Group>() {
            @Override
            public Group forEach(Yaml yaml, int index) throws YamlException {
                return Group.fromYaml(yaml, index);
            }
        }, true);

        return new Page(id, label, index, groups);
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


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Label
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the label of this page.
     * @return The page label.
     */
    public String getLabel()
    {
        return this.label.getValue();
    }


    // ** Index
    // ------------------------------------------------------------------------------------------

    public Integer getIndex()
    {
        return this.index.getValue();
    }


    // ** Groups
    // ------------------------------------------------------------------------------------------

    public List<Group> getGroups()
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

        LinearLayout pageLayout = new LinearLayout(context);
        this.pageViewId = Util.generateViewId();
        pageLayout.setId(this.pageViewId);

        LinearLayout.LayoutParams profileLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);

        int paddingTop = (int) Util.getDim(context, R.dimen.page_padding_top);
        pageLayout.setPadding(0, paddingTop, 0, 0);

        pageLayout.setOrientation(LinearLayout.VERTICAL);
        pageLayout.setLayoutParams(profileLayoutParams);

        updateView(pageLayout);

        return pageLayout;
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

    private void initialize()
    {
        this.label  = new PrimitiveValue<>(null, String.class);
        this.index  = new PrimitiveValue<>(null, Integer.class);

        List<Class<? extends Group>> groupClasses = new ArrayList<>();
        groupClasses.add(Group.class);

//        CollectionValue.OnUpdateListener<Group> onUpdateListener =
//                new CollectionValue.OnUpdateListener<Group>() {
//            @Override
//            public void onUpdate(List<Group> values) {
//                updateView(null);
//            }
//        };

        this.groups = new CollectionValue<>(null, groupClasses, null, null);
    }
}



