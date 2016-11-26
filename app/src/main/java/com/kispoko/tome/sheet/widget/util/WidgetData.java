
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Widget Data
 */
public class WidgetData implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                      id;
    private ModelValue<WidgetFormat>  format;
    private PrimitiveValue<String[]>  actions;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public WidgetData()
    {
        this.id      = null;

        this.format  = new ModelValue<>(null, this, WidgetFormat.class);
        this.actions = new PrimitiveValue<>(null, this, String[].class);
    }


    public WidgetData(UUID id,
                      WidgetFormat widgetFormat,
                      String[] actions)
    {
        this.id      = id;

        this.format  = new ModelValue<>(widgetFormat, this, WidgetFormat.class);
        this.actions = new PrimitiveValue<>(actions, this, String[].class);
    }


    public static WidgetData fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id                   = UUID.randomUUID();
        WidgetFormat widgetFormat = WidgetFormat.fromYaml(yaml.atKey("format"));

        List<String> actionList   = yaml.atKey("actions").getStringList();
        String[]     actions      = actionList.toArray(new String[actionList.size()]);

        return new WidgetData(id, widgetFormat, actions);
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

    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onValueUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** WidgetFormat
    // ------------------------------------------------------------------------------------------

    public WidgetFormat getFormat()
    {
        return this.format.getValue();
    }


    // ** Actions
    // ------------------------------------------------------------------------------------------

    public String[] getActions() {
        return this.actions.getValue();
    }


}
