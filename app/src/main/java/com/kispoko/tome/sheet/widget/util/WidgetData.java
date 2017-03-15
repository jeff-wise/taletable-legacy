
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.util.functor.PrimitiveFunctor;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Widget Data
 */
public class WidgetData implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;
    private ModelFunctor<WidgetFormat>  format;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public WidgetData()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.format         = ModelFunctor.empty(WidgetFormat.class);
    }


    public WidgetData(UUID id,
                      String name,
                      WidgetFormat widgetFormat)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.format         = ModelFunctor.full(widgetFormat, WidgetFormat.class);
    }


    /**
     * Create a WidgetData from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Widget Data.
     * @throws YamlParseException
     */
    public static WidgetData fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return WidgetData.asDefault();

        UUID         id            = UUID.randomUUID();

        String       name        = yaml.atMaybeKey("name").getString();
        WidgetFormat format        = WidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        return new WidgetData(id, name, format);
    }


    public static WidgetData asDefault()
    {
        WidgetData widgetData = new WidgetData();

        widgetData.setId(UUID.randomUUID());

        widgetData.setName(null);
        widgetData.format.setValue(WidgetFormat.asDefault());

        return widgetData;
    }


    // API
    // ------------------------------------------------------------------------------------------

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
     * This method is called when the Widget Data is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putYaml("format", this.format());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * The widget name.
     * @return The name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * Set the widget name. If the name is null, sets an empty string to prevent errosr.
     * @param name The widget name.
     */
    public void setName(String name)
    {
        if (name != null)
            this.name.setValue(name);
        else
            this.name.setValue("");
    }


    // ** WidgetFormat
    // ------------------------------------------------------------------------------------------

    /**
     * The standard widget formatting options.
     * @return The widget format.
     */
    public WidgetFormat format()
    {
        return this.format.getValue();
    }


    /**
     * Set the widget format. If null, sets the default widget format.
     * @param widgetFormat The widget format.
     */
    public void setFormat(WidgetFormat widgetFormat)
    {
        if (widgetFormat != null)
            this.format.setValue(widgetFormat);
        else
            this.format.setValue(WidgetFormat.asDefault());
    }


}
