
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
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
    private ModelFunctor<WidgetFormat> format;
    private PrimitiveFunctor<Action[]> actions;
    private PrimitiveFunctor<Action> primaryAction;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public WidgetData()
    {
        this.id            = null;

        this.format        = ModelFunctor.empty(WidgetFormat.class);
        this.actions       = new PrimitiveFunctor<>(null, Action[].class);
        this.primaryAction = new PrimitiveFunctor<>(null, Action.class);
    }


    public WidgetData(UUID id,
                      WidgetFormat widgetFormat,
                      List<Action> actions,
                      Action primaryAction)
    {
        this.id            = id;

        this.format        = ModelFunctor.full(widgetFormat, WidgetFormat.class);

        Action[] actionArray = new Action[actions.size()];
        actions.toArray(actionArray);
        this.actions       = new PrimitiveFunctor<>(actionArray, Action[].class);

        this.primaryAction = new PrimitiveFunctor<>(null, Action.class);

        // If the primary action is null, then the default primary action is the first action in
        // the action list.
        if (primaryAction != null)
            this.primaryAction.setValue(primaryAction);
        else if (actions.size() > 0)
            this.primaryAction.setValue(actions.get(0));
    }


    public static WidgetData fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID         id            = UUID.randomUUID();

        WidgetFormat format        = WidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        List<Action> actions       = yaml.atKey("actions").forEach(new Yaml.ForEach<Action>() {
            @Override
            public Action forEach(Yaml yaml, int index) throws YamlException {
                return Action.fromYaml(yaml);
            }
        }, true);

        Action       primaryAction = Action.fromYaml(yaml.atMaybeKey("primary_action"));

        return new WidgetData(id, format, actions, primaryAction);
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

    public Action[] getActions()
    {
        return this.actions.getValue();
    }


    /**
     * Get the widget's primary action.
     * @return The primary actino.
     */
    public Action primaryAction()
    {
        return this.primaryAction.getValue();
    }



}
