
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.sheet.widget.action.Action;
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

    private UUID                     id;
    private ModelValue<WidgetFormat> format;
    private PrimitiveValue<Action[]> actions;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public WidgetData()
    {
        this.id      = null;

        this.format  = ModelValue.empty(WidgetFormat.class);
        this.actions = new PrimitiveValue<>(null, Action[].class);
    }


    public WidgetData(UUID id,
                      WidgetFormat widgetFormat,
                      List<Action> actions)
    {
        this.id      = id;

        this.format  = ModelValue.full(widgetFormat, WidgetFormat.class);

        Action[] actionArray = new Action[actions.size()];
        actions.toArray(actionArray); // fill the array
        this.actions = new PrimitiveValue<>(actionArray, Action[].class);
    }


    public static WidgetData fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID         id      = UUID.randomUUID();

        WidgetFormat format  = WidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        List<Action> actions = yaml.atKey("actions").forEach(new Yaml.ForEach<Action>() {
            @Override
            public Action forEach(Yaml yaml, int index) throws YamlException {
                return Action.fromYaml(yaml);
            }
        });

        return new WidgetData(id, format, actions);
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

    public Action[] getActions() {
        return this.actions.getValue();
    }


}
