
package com.kispoko.tome.template;


import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.List;


/**
 * Sheet Template
 *
 * This class represents a sheet template. A sheet template is simply a sheet that is provided
 * as a starting point for creating a character. This class does not contain the sheet itself,
 * but only the template metadata.
 */
public class Template
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String          name;
    private String          label;
    private String          description;
    private String          game;
    private List<String>    variants;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Template(String name,
                    String label,
                    String description,
                    String game,
                    List<String> variants)
    {
        this.name        = name;
        this.label       = label;
        this.description = description;
        this.game        = game;
        this.variants    = variants;
    }


    public static Template fromYaml(Yaml yaml)
                  throws YamlException
    {
        String       name        = yaml.atKey("name").getString();
        String       label       = yaml.atKey("label").getString();
        String       description = yaml.atKey("description").getString();
        String       game        = yaml.atKey("game").getString();
        List<String> variants    = yaml.atMaybeKey("variants").getStringList();

        return new Template(name, label, description, game, variants);
    }


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * Get the template's official id. The template file names are named with this id.
     * @return The official template id.
     */
    public String officialId()
    {
        return "official_" + this.game + "_" + this.name;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the template name. The name is unique identifier for the template.
     * @return The template name.
     */
    public String name()
    {
        return this.name;
    }


    /**
     * Get the template label. The label is a short description of the template.
     * @return The template label.
     */
    public String label()
    {
        return this.label;
    }


    /**
     * Get the template description, which is a long summary of the template.
     * @return The template description.
     */
    public String description()
    {
        return this.description;
    }


    /**
     * Get the roleplaying game name that the template is designed for.
     * @return The template game.
     */
    public String game()
    {
        return this.game;
    }


    /**
     * The template's variants.
     * @return The variant list.
     */
    public List<String> variants()
    {
        return this.variants;
    }


}
