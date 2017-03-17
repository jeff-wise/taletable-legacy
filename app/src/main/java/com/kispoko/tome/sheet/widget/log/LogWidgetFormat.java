
package com.kispoko.tome.sheet.widget.log;


import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Log Widget Format
 */
public class LogWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The type of divider (may be none) between log entries.
     */
    private PrimitiveFunctor<DividerType>   dividerType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public LogWidgetFormat()
    {
        this.id             = null;

        this.dividerType    = new PrimitiveFunctor<>(null, DividerType.class);
    }


    public LogWidgetFormat(UUID id, DividerType dividerType)
    {
        this.id             = id;

        this.dividerType    = new PrimitiveFunctor<>(dividerType, DividerType.class);
    }


    /**
     * Create a Log Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Log Widget Format.
     * @throws YamlParseException
     */
    public static LogWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return LogWidgetFormat.asDefault();

        UUID        id          = UUID.randomUUID();

        DividerType dividerType = DividerType.fromYaml(yaml.atMaybeKey("divider"));

        return new LogWidgetFormat(id, dividerType);
    }


    private static LogWidgetFormat asDefault()
    {
        LogWidgetFormat format = new LogWidgetFormat();

        format.setId(UUID.randomUUID());
        format.setDividerType(null);

        return format;
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
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // --------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("divider", this.dividerType());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Divider Type
    // --------------------------------------------------------------------------------------

    /**
     * The type of divider between log entries.
     * @return The divider type.
     */
    public DividerType dividerType()
    {
        return this.dividerType.getValue();
    }


    /**
     * Set the divider type. If null, defaults to DARK.
     * @param dividerType The divider type.
     */
    public void setDividerType(DividerType dividerType)
    {
        if (dividerType != null)
            this.dividerType.setValue(dividerType);
        else
            this.dividerType.setValue(DividerType.DARK);
    }


}
