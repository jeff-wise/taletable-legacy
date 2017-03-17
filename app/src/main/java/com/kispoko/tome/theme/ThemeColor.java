
package com.kispoko.tome.theme;


import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Theme Color
 */
public class ThemeColor implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The color's name. Used to identify the color in the sheet definition.
     */
    private PrimitiveFunctor<String>    name;

    /**
     * The color's hex value e.g. #456789
     */
    private PrimitiveFunctor<String>    colorHex;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ThemeColor()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.colorHex   = new PrimitiveFunctor<>(null, String.class);
    }


    public ThemeColor(UUID id, String name, String colorHex)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.colorHex   = new PrimitiveFunctor<>(colorHex, String.class);
    }


    /**
     * Create a Theme Color from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Theme Color.
     * @throws YamlParseException
     */
    public static ThemeColor fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID   id   = UUID.randomUUID();

        String name = yaml.atKey("name").getString();

        // TODO yaml parsing validation, better yaml parsing errors
        String hex  = yaml.atKey("color_hex").getString();

        return new ThemeColor(id, name, hex);
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
                .putString("color_hex", this.colorHex());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Name
    // --------------------------------------------------------------------------------------

    /**
     * The color name.
     * @return The color name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Color Hex
    // --------------------------------------------------------------------------------------

    public String colorHex()
    {
        return this.colorHex.getValue();
    }

}
