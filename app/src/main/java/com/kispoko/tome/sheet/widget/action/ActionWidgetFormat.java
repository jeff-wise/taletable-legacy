
package com.kispoko.tome.sheet.widget.action;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Action Widget Format
 */
public class ActionWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<ActionSize>    size;
    private PrimitiveFunctor<ActionColor>   actionColor;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ActionWidgetFormat()
    {
        this.id             = null;

        this.size           = new PrimitiveFunctor<>(null, ActionSize.class);
        this.actionColor    = new PrimitiveFunctor<>(null, ActionColor.class);
    }


    public ActionWidgetFormat(UUID id, ActionSize size, ActionColor actionColor)
    {
        this.id             = id;

        this.size           = new PrimitiveFunctor<>(size, ActionSize.class);
        this.actionColor    = new PrimitiveFunctor<>(actionColor, ActionColor.class);

        this.setSize(size);
        this.setActionColor(actionColor);
    }


    /**
     * Create an ActionWidgetFormat from its yaml representation.
     * @param yaml The yaml parrser.
     * @return The parsed Action Widget Format.
     * @throws YamlParseException
     */
    public static ActionWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return ActionWidgetFormat.asDefault();

        UUID id                  = UUID.randomUUID();

        ActionSize   size        = ActionSize.fromYaml(yaml.atMaybeKey("size"));
        ActionColor  actionColor = ActionColor.fromYaml(yaml.atMaybeKey("hl_color"));

        return new ActionWidgetFormat(id, size, actionColor);
    }


    private static ActionWidgetFormat asDefault()
    {
        ActionWidgetFormat format = new ActionWidgetFormat();

        format.setId(UUID.randomUUID());
        format.setSize(null);
        format.setActionColor(null);

        return format;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    // ** Id
    // -----------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // -----------------------------------------------------------------------------------------

    /**
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // -----------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("size", this.size());
        yaml.putYaml("hl_color", this.actionColor());

        return yaml;
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Size
    // --------------------------------------------------------------------------------------

    /**
     * The Action Widget's text size.
     * @return The Widget Content Size.
     */
    public ActionSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the action widget's text size.
     * @param size The text size.
     */
    public void setSize(ActionSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(ActionSize.MEDIUM);
    }


    // ** Tint
    // --------------------------------------------------------------------------------------

    /**
     * The action color.
     * @return The tint.
     */
    public ActionColor actionColor()
    {
        return this.actionColor.getValue();
    }


    public void setActionColor(ActionColor color)
    {
        if (color != null)
            this.actionColor.setValue(color);
        else
            this.actionColor.setValue(ActionColor.BLUE);
    }


}
