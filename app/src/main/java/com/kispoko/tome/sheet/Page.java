
package com.kispoko.tome.sheet;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.Arrays;
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
public class Page implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                    id;

    private PrimitiveValue<String>  label;
    private PrimitiveValue<Integer> index;
    private CollectionValue<Group>  groups;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Page(UUID id, String label, Integer index, List<Group> groups)
    {
        this.id     = id;

        this.label  = new PrimitiveValue<>(label, this, String.class);
        this.index  = new PrimitiveValue<>(index, this, Integer.class);

        List<Class<Group>> groupClasses = Arrays.asList(Group.class);
        this.groups = new CollectionValue<>(groups, this, groupClasses);

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
        });

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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


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


    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Returns an android view that represents this page.
     * @param context The parent activity context.
     * @return A View of this page.
     */
    public View getView(Context context, Rules rules)
    {
        LinearLayout profileLayout = new LinearLayout(context);

        LinearLayout.LayoutParams profileLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);

        int paddingTop = (int) Util.getDim(context, R.dimen.page_padding_top);
        profileLayout.setPadding(0, paddingTop, 0, 0);

        profileLayout.setOrientation(LinearLayout.VERTICAL);
        profileLayout.setLayoutParams(profileLayoutParams);

        for (Group group : this.groups.getValue()) {
            profileLayout.addView(group.getView(context, rules));
        }

        return profileLayout;
    }

}



