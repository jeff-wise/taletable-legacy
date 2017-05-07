

package com.kispoko.tome.official.template;


import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;



/**
 * Template Variant
 */
public class Variant implements Serializable
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private String name;
    private String label;
    private String description;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------

    public Variant(String name, String label, String description)
    {
        this.name        = name;
        this.label       = label;
        this.description = description;
    }


    /**
     * Create a Variant from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Variant.
     * @throws YamlParseException
     */
    public static Variant fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String name         = yaml.atKey("name").getTrimmedString();
        String label        = yaml.atKey("label").getTrimmedString();
        String description  = yaml.atKey("description").getTrimmedString();

        return new Variant(name, label, description);
    }


    // API
    // -------------------------------------------------------------------------------------

    public String name()
    {
        return this.name;
    }


    public String label()
    {
        return this.label;
    }


    public String description()
    {
        return this.description;
    }

}
