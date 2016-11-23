
package com.kispoko.tome.rules.refinement;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
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

    private UUID id;

    private CollectionValue<MemberOf> memberOfs;

    // > Internal
    private Map<String,MemberOf> memberOfIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RefinementIndex() { }


    public RefinementIndex(UUID id)
    {
        this.id        = id;

        List<Class<? extends MemberOf>> memberOfClasses = new ArrayList<>();
        memberOfClasses.add(MemberOf.class);
        this.memberOfs = new CollectionValue<>(new ArrayList<MemberOf>(), this, memberOfClasses);

        // > Initialize indexes
        memberOfIndex = new HashMap<>();
    }


    public static RefinementIndex fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();
        final RefinementIndex refinementIndex = new RefinementIndex(id);

        yaml.forEach(new Yaml.ForEach<Void>() {
            @Override
            public Void forEach(Yaml yaml, int index) throws YamlException {
                String type = yaml.atKey("type").getString();
                switch (type)
                {
                    case "member_of":
                        refinementIndex.addMemberOf(MemberOf.fromYaml(yaml));
                        break;
                }
                return null;
            }
        });

        return refinementIndex;
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


    /**
     * Add a new MemberOf refinement to the refinement index.
     * @param memberOf The MemberOf refinement to add to the index.
     */
    public void addMemberOf(MemberOf memberOf)
    {
        if (!this.memberOfIndex.containsKey(memberOf.getName()))
        {
            this.memberOfIndex.put(memberOf.getName(), memberOf);
            this.memberOfs.getValue().add(memberOf);
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

}
