
package com.kispoko.tome.theme;


import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Theme
 */
public class Theme extends Model
                   implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private CollectionFunctor<ThemeColor>   colors;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Theme()
    {
        this.id     = null;

        this.name   = new PrimitiveFunctor<>(null, String.class);
        this.colors = CollectionFunctor.empty(ThemeColor.class);
    }


    public Theme(UUID id, String name, List<ThemeColor> colors)
    {
        this.id     = id;

        this.name   = new PrimitiveFunctor<>(name, String.class);
        this.colors = CollectionFunctor.full(colors, ThemeColor.class);
    }


    /**
     * Create a Theme from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Theme.
     * @throws YamlParseException
     */
    public static Theme fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID             id     = UUID.randomUUID();

        String           name   = yaml.atKey("name").getString();

        List<ThemeColor> colors = yaml.atKey("colors").forEach(new YamlParser.ForEach<ThemeColor>()
        {
            @Override
            public ThemeColor forEach(YamlParser yaml, int index) throws YamlParseException
            {
                return ThemeColor.fromYaml(yaml);
            }
        }, true);

        return new Theme(id, name, colors);
    }


    // API
    // -----------------------------------------------------------------------------------------

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


    // ** On Load
    // --------------------------------------------------------------------------------------

    /**
     * Called when the Spacing is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // --------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putList("colors", this.colors());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Name
    // --------------------------------------------------------------------------------------

    /**
     * The theme name.
     * @return The name
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Colors
    // --------------------------------------------------------------------------------------

    /**
     * The theme color palette.
     * @return The color list.
     */
    public List<ThemeColor> colors()
    {
        if (this.colors.isNull())
            return new ArrayList<>();

        return this.colors.getValue();
    }


}
