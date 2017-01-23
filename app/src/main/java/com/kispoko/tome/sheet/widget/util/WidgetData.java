
package com.kispoko.tome.sheet.widget.util;


import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.List;
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
    private PrimitiveFunctor<Action[]>  actions;
    private PrimitiveFunctor<Action>    primaryAction;


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


    public static WidgetData fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID         id            = UUID.randomUUID();

        WidgetFormat format        = WidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        List<Action> actions       = yaml.atKey("actions").forEach(new YamlParser.ForEach<Action>() {
            @Override
            public Action forEach(YamlParser yaml, int index) throws YamlParseException {
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


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("format", this.format());
        yaml.putArray("actions", this.actions());
        yaml.putYaml("primary_action", this.primaryAction());

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


    // ** Actions
    // ------------------------------------------------------------------------------------------

    public Action[] actions()
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
