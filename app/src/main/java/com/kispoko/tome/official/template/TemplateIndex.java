
package com.kispoko.tome.official.template;


import android.content.Context;
import android.util.Log;

import com.kispoko.tome.ApplicationAssets;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Template Index
 */
public class TemplateIndex implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private List<Template>                  templates;


    // > Indices
    // ------------------------------------------------------------------------------------------

    private Map<SkillLevel,Set<Template>>   templatesBySkillLevel;
    private Map<Game,Set<Template>>         templatesByGame;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TemplateIndex(List<Template> templates)
    {
        this.templates = templates;

        // Index templates by skill level
        this.templatesBySkillLevel = new HashMap<>();
        for (Template template : this.templates)
        {
            SkillLevel skillLevel = template.skillLevel();

            if (!this.templatesBySkillLevel.containsKey(skillLevel))
                this.templatesBySkillLevel.put(skillLevel, new HashSet<Template>());

            this.templatesBySkillLevel.get(skillLevel).add(template);
        }

        // Index templates by skill level
        this.templatesByGame = new HashMap<>();
        for (Template template : this.templates)
        {
            Game game = template.game();

            if (!this.templatesByGame.containsKey(game))
                this.templatesByGame.put(game, new HashSet<Template>());

            this.templatesByGame.get(game).add(template);
        }

    }


    /**
     * Load the games from the manifest file which contains all of the officially support games.
     */
    public static TemplateIndex fromManifest(Context context)
                  throws IOException, YamlParseException
    {
        InputStream yamlIS = context.getAssets().open(ApplicationAssets.templateManifest);
        YamlParser yaml = YamlParser.fromFile(yamlIS);

        List<Template> templates = yaml.atKey("templates")
                                        .forEach(new YamlParser.ForEach<Template>() {
            @Override
            public Template forEach(YamlParser yaml, int index) throws YamlParseException {
                return Template.fromYaml(yaml);
            }
        });

        return new TemplateIndex(templates);
    }


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * Get all the templates for the given skill level.
     * @param skillLevel The skill level.
     * @return The template set.
     */
    public List<Template> templatesWithSkillLevel(Game game, SkillLevel skillLevel)
    {
        Set<Template> templatesOfGame = new HashSet<>();
        Set<Template> templatesOfSkillLevel = new HashSet<>();

        if (this.templatesByGame.containsKey(game))
            templatesOfGame = this.templatesByGame.get(game);

        if (this.templatesBySkillLevel.containsKey(skillLevel))
            templatesOfSkillLevel = this.templatesBySkillLevel.get(skillLevel);

        templatesOfGame.retainAll(templatesOfSkillLevel);

        List<Template> templates = new ArrayList<>(templatesOfGame);

        Collections.sort(templates, new Comparator<Template>()
        {
            public int compare(Template template1, Template template2)
            {
                return template1.name().compareTo(template2.name());
            }
        });

        return templates;
    }

}
