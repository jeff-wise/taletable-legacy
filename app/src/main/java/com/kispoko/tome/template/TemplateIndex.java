
package com.kispoko.tome.template;


import android.content.Context;

import com.kispoko.tome.ApplicationAssets;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



/**
 * Template Index
 */
public class TemplateIndex
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------


    private List<Template> templates;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TemplateIndex(List<Template> templates)
    {
        this.templates = templates;
    }


    /**
     * Load the games from the manifest file which contains all of the officially support games.
     */
    public static TemplateIndex fromManifest(Context context)
                  throws IOException, YamlException
    {
        InputStream yamlIS = context.getAssets().open(ApplicationAssets.templateManifest);
        Yaml yaml = Yaml.fromFile(yamlIS);

        List<Template> templates = yaml.atKey("templates").forEach(new Yaml.ForEach<Template>() {
            @Override
            public Template forEach(Yaml yaml, int index) throws YamlException {
                return Template.fromYaml(yaml);
            }
        });

        return new TemplateIndex(templates);
    }


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * All of the templates in the index.
     * @param gameName Return templates for this game only.
     * @return An immutable list of Games.
     */
    public List<Template> templates(String gameName)
    {
        List<Template> gameTemplates = new ArrayList<>();

        for (Template template : this.templates) {
            if (template.game().equals(gameName))
                gameTemplates.add(template);
        }

        return gameTemplates;
    }


}
