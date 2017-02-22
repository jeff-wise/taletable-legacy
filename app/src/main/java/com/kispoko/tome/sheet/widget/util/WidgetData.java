
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
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

    private ModelFunctor<WidgetFormat>  format;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public WidgetData()
    {
        this.id            = null;

        this.format        = ModelFunctor.empty(WidgetFormat.class);
    }


    public WidgetData(UUID id,
                      WidgetFormat widgetFormat)
    {
        this.id            = id;

        this.format        = ModelFunctor.full(widgetFormat, WidgetFormat.class);
    }


    public static WidgetData fromYaml(YamlParser yaml, boolean useDefaultFormat)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return WidgetData.asDefault();

        UUID         id            = UUID.randomUUID();

        WidgetFormat format        = WidgetFormat.fromYaml(yaml.atMaybeKey("format"),
                                                           useDefaultFormat);

        return new WidgetData(id, format);
    }


    public static WidgetData asDefault()
    {
        WidgetData widgetData = new WidgetData();

        widgetData.setId(UUID.randomUUID());
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
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("format", this.format());

        return yaml;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** WidgetFormat
    // ------------------------------------------------------------------------------------------

    public WidgetFormat format()
    {
        return this.format.getValue();
    }


    public void setWidgetFormat(WidgetFormat format)
    {
        this.format.setValue(format);
    }

}
