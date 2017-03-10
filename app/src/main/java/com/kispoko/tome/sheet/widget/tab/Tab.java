
package com.kispoko.tome.sheet.widget.tab;


import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Tab
 */
public class Tab implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;
    private CollectionFunctor<Group>    groups;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Tab()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.groups     = CollectionFunctor.empty(Group.class);
    }


    public Tab(UUID id, String name, List<Group> groups)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.groups     = CollectionFunctor.full(groups, Group.class);
    }


    /**
     * Create a tab from its yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Tab.
     * @throws YamlParseException
     */
    public static Tab fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID        id     = UUID.randomUUID();

        String      name   = yaml.atKey("name").getString();

        List<Group> groups = yaml.atMaybeKey("groups").forEach(
                                                            new YamlParser.ForEach<Group>() {
            @Override
            public Group forEach(YamlParser yaml, int index) throws YamlParseException {
                return Group.fromYaml(yaml, index);
            }
        }, true);

        return new Tab(id, name, groups);
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
    public void onLoad() { }


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
                .putList("groups", this.groups());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * The tab name.
     * @return The tab name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Groups
    // ------------------------------------------------------------------------------------------

    /**
     * The groups in the tab view.
     * @return The group list.
     */
    public List<Group> groups()
    {
        return this.groups.getValue();
    }


}
