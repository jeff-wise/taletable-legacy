
package com.kispoko.tome.engine.refinement;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Refinement Index
 */
public class RefinementIndex implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<RefinementUnion> refinements;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,MemberOf>             memberOfIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RefinementIndex()
    {
        this.id        = null;

        List<Class<? extends RefinementUnion>> refinementUnionClasses = new ArrayList<>();
        refinementUnionClasses.add(RefinementUnion.class);
        this.refinements = CollectionFunctor.empty(refinementUnionClasses);

        // > Initialize indexes
        this.memberOfIndex = new HashMap<>();
    }


    public RefinementIndex(UUID id, List<RefinementUnion> refinementUnions)
    {
        this.id        = id;

        List<Class<? extends RefinementUnion>> refinementUnionClasses = new ArrayList<>();
        refinementUnionClasses.add(RefinementUnion.class);
        this.refinements = CollectionFunctor.full(refinementUnions, refinementUnionClasses);

        // > Initialize indexes
        memberOfIndex = new HashMap<>();

        this.indexRefinements();
    }


    public static RefinementIndex fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID                  id          = UUID.randomUUID();

        List<RefinementUnion> refinements = yaml.forEach(new Yaml.ForEach<RefinementUnion>()
        {
            @Override
            public RefinementUnion forEach(Yaml yaml, int index) throws YamlException
            {
                return RefinementUnion.fromYaml(yaml);
            }
        });

        return new RefinementIndex(id, refinements);
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
     * This method is called when the Refinement Index is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.indexRefinements();
    }


    /**
     * Add a new refinement to the index. If a refinement of the same type and name already exists,
     * then the old refinement is replaced with the new one being added.
     * @param refinementUnion The new refinement to add.
     */
    public void addRefinement(RefinementUnion refinementUnion)
    {
        switch (refinementUnion.type())
        {
            case MEMBER_OF:
                this.refinements.getValue().add(refinementUnion);
                MemberOf memberOf = refinementUnion.memberOf();
                this.memberOfIndex.put(memberOf.getName(), memberOf);
                break;
        }
    }


    /**
     * Get the MemberOf refinement with the provided name.
     * @param memberOfName The MemberOf refinement name.
     * @return A MemberOf Refinement or null if the refinement with the given name does not exist.
     */
    public MemberOf memberOfWithName(String memberOfName)
    {
        return this.memberOfIndex.get(memberOfName);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void indexRefinements()
    {
        for (RefinementUnion refinementUnion : this.refinements.getValue())
        {
            switch (refinementUnion.type())
            {
                case MEMBER_OF:
                    MemberOf memberOf = refinementUnion.memberOf();
                    this.memberOfIndex.put(memberOf.getName(), memberOf);
                    break;
            }
        }
    }

}
