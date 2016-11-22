
package com.kispoko.tome.rules.refinement;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
* Created by jeff on 11/19/16.
*/
public class RefinementId implements Model, Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private UUID id;
    private PrimitiveValue<String> name;
    private PrimitiveValue<RefinementType>   type;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public RefinementId(UUID id, String name, RefinementType type)
    {
        this.id   = id;
        this.name = new PrimitiveValue<>(name, this, String.class);
        this.type = new PrimitiveValue<>(type, this, RefinementType.class);
    }


    public static RefinementId fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID           id   = UUID.randomUUID();
        String         name = yaml.atKey("name").getString();
        RefinementType type = RefinementType.fromYaml(yaml.atKey("type"));

        return new RefinementId(id, name, type);
    }


    // API
    // --------------------------------------------------------------------------------------

    // > Model
    // --------------------------------------------------------------------------------------

    // ** Id
    // --------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Update
    // --------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


    // > State
    // --------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name.getValue();
    }


    public RefinementType getType()
    {
        return this.type.getValue();
    }

}

