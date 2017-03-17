
package com.kispoko.tome.template;


import android.content.Context;

import com.kispoko.tome.ApplicationAssets;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

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
                  throws IOException, YamlParseException
    {
        InputStream yamlIS = context.getAssets().open(ApplicationAssets.templateManifest);
        YamlParser yaml = YamlParser.fromFile(yamlIS);

        List<Template> templates = yaml.atKey("templates").forEach(new YamlParser.ForEach<Template>() {
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
