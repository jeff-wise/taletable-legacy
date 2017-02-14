
package com.kispoko.tome.sheet.widget.adder;


import com.kispoko.tome.R;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.AdderWidget;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Adder Widget Format
 */
public class AdderWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    subtractLabel;
    private PrimitiveFunctor<String>    addLabel;
    private PrimitiveFunctor<String>    resetLabel;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public AdderWidgetFormat()
    {
        this.id             = null;

        this.subtractLabel  = new PrimitiveFunctor<>(null, String.class);
        this.addLabel       = new PrimitiveFunctor<>(null, String.class);
        this.resetLabel     = new PrimitiveFunctor<>(null, String.class);
    }


    public AdderWidgetFormat(UUID id,
                             String subtractLabel,
                             String addLabel,
                             String resetLabel)
    {
        this.id             = id;


        this.subtractLabel  = new PrimitiveFunctor<>(subtractLabel, String.class);
        this.addLabel       = new PrimitiveFunctor<>(addLabel, String.class);
        this.resetLabel     = new PrimitiveFunctor<>(resetLabel, String.class);
    }


    /**
     * Create an Adder Widget Format from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Adder Widget Format.
     * @throws YamlParseException
     */
    public static AdderWidgetFormat fromYaml(YamlParser yaml,
                                             AdderWidget.Type adderType)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return AdderWidgetFormat.asDefault(adderType);

        UUID  id                = UUID.randomUUID();

        String subtractLabel    = yaml.atMaybeKey("subtract_label").getTrimmedString();
        String addLabel         = yaml.atMaybeKey("add_label").getTrimmedString();
        String resetLabel       = yaml.atMaybeKey("reset_label").getTrimmedString();

        return new AdderWidgetFormat(id, subtractLabel, addLabel, resetLabel);
    }


    /**
     * Create an Adder Widget Format with just default values.
     * @return The default Adder Widget Format.
     */
    public static AdderWidgetFormat asDefault(AdderWidget.Type adderType)
    {
        AdderWidgetFormat adderWidgetFormat = new AdderWidgetFormat();

        adderWidgetFormat.setId(UUID.randomUUID());
        adderWidgetFormat.setSubtractLabel(adderType);
        adderWidgetFormat.setAddLabel(adderType);
        adderWidgetFormat.setResetLabel(null);

        return adderWidgetFormat;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Column Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Adder Widget Format yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putString("subtract_label", this.subtractLabel());
        yaml.putString("add_label", this.addLabel());
        yaml.putString("reset_label", this.resetLabel());

        return yaml;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Subtract Label
    // ------------------------------------------------------------------------------------------

    /**
     * The subtract label.
     * @return The subtract label.
     */
    public String subtractLabel()
    {
        return this.subtractLabel.getValue();
    }


    public void setSubtractLabel(String subtractLabel)
    {
        this.subtractLabel.setValue(subtractLabel);
    }


    /**
     * Set a default subtract label based on the adder type.
     * @param adderType The Adder Widget type.
     */
    public void setSubtractLabel(AdderWidget.Type adderType)
    {
        switch (adderType)
        {
            case BY_ONE:
                this.subtractLabel.setValue("-1");
                break;
            case BY_MANY:
                this.subtractLabel.setValue("-XXX");
                break;
        }
    }


    // ** Add Label
    // ------------------------------------------------------------------------------------------

    /**
     * The add label.
     * @return The add label.
     */
    public String addLabel()
    {
        return this.addLabel.getValue();
    }


    /**
     * Set a default add label based on the adder type.
     * @param adderType The Adder Widget type.
     */
    public void setAddLabel(AdderWidget.Type adderType)
    {
        switch (adderType)
        {
            case BY_ONE:
                this.subtractLabel.setValue("+1");
                break;
            case BY_MANY:
                this.subtractLabel.setValue("+XXX");
                break;
        }
    }


    // ** Reset Label
    // ------------------------------------------------------------------------------------------

    /**
     * The reset label.
     * @return The reset label.
     */
    public String resetLabel()
    {
        return this.resetLabel.getValue();
    }


    public void setResetLabel(String resetLabel)
    {
        if (resetLabel != null) {
            this.resetLabel.setValue(resetLabel);

        }
        else {
            String resetString = SheetManager.currentSheetContext().getString(R.string.reset);
            this.resetLabel.setValue(resetString);
        }
    }

}
