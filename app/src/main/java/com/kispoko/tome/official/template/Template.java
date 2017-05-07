
package com.kispoko.tome.official.template;


import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;



/**
 * Sheet Template
 *
 * This class represents a sheet template. A sheet template is simply a sheet that is provided
 * as a starting point for creating a character. This class does not contain the sheet itself,
 * but only the template metadata.
 */
public class Template implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String          name;
    private String          label;

    private String          shortDescription;
    private String          fullDescription;

    private Game            game;
    private SkillLevel      skillLevel;

    private List<Variant>   variants;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Template(String name,
                    String label,
                    String shortDescription,
                    String fullDescription,
                    Game game,
                    SkillLevel skillLevel,
                    List<Variant> variants)
    {
        this.name = name;
        this.label = label;

        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;

        this.game = game;

        this.skillLevel = skillLevel;
        this.variants = variants;
    }


    public static Template fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        String        name             = yaml.atKey("name").getString();
        String        label            = yaml.atKey("label").getString();

        String        shortDescription = yaml.atKey("short_description").getTrimmedString();
        String        fullDescription  = yaml.atKey("full_description").getTrimmedString();

        Game          game             = Game.fromYaml(yaml.atKey("game"));
        SkillLevel    skillLevel       = SkillLevel.fromYaml(yaml.atKey("skill_level"));

        List<Variant> variants          = yaml.atKey("variants")
                .forEach(new YamlParser.ForEach<Variant>() {
                    @Override
                    public Variant forEach(YamlParser yaml, int index) throws YamlParseException {
                        return Variant.fromYaml(yaml);
                    }
                }, true);

        return new Template(name, label, shortDescription, fullDescription,
                game, skillLevel, variants);
    }


    // API
    // -----------------------------------------------------------------------------------------

    /**
     * Get the template's official id. The template file names are named with this id.
     *
     * @return The official template id.
     */
    public String id(String variantName)
    {
        return this.game.name().toLowerCase() + "_" +
                this.skillLevel.name().toLowerCase() + "_" +
                this.name + "_" +
                variantName;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the template name. The name is unique identifier for the template.
     *
     * @return The template name.
     */
    public String name()
    {
        return this.name;
    }


    /**
     * Get the template label. The label is a short description of the template.
     *
     * @return The template label.
     */
    public String label()
    {
        return this.label;
    }


    /**
     * Get the template description, which is a long summary of the template.
     *
     * @return The template description.
     */
    public String shortDescription()
    {
        return this.shortDescription;
    }


    /**
     * The full description.
     * @return The full description.
     */
    public String fullDescription()
    {
        return this.fullDescription;
    }


    /**
     * Get the roleplaying game name that the template is designed for.
     * @return The template game.
     */
    public Game game()
    {
        return this.game;
    }


    /**
     * The skill level.
     * @return The Skill Level.
     */
    public SkillLevel skillLevel()
    {
        return this.skillLevel;
    }


    /**
     * The template's variants.
     * @return The variant list.
     */
    public List<Variant> variants()
    {
        return this.variants;
    }


    // EQUALITY
    // -----------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        return new EqualsBuilder()
                .append(name, template.name)
                .isEquals();
    }


    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }
}
