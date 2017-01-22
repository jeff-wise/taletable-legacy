
package com.kispoko.tome.campaign;


import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.util.UUID;



/**
 * Campaign
 */
public class Campaign implements Model
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Campaign()
    {
        this.name = new PrimitiveFunctor<>(null, String.class);
    }


    public Campaign(UUID id, String name)
    {
        this.id     = id;
        this.name   = new PrimitiveFunctor<>(name, String.class);
    }


    /**
     * Create a campaign from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Campaign.
     * @throws YamlParseException
     */
    public static Campaign fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        UUID        id          = UUID.randomUUID();

        String      name        = yaml.atKey("name").getString();

        return new Campaign(id, name);
    }


    // API
    // -----------------------------------------------------------------------------------------


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
     * This method is called when the Sheet is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The campaign name.
     * @return The campaign name.
     */
    public String name()
    {
        return this.name.getValue();
    }

}
