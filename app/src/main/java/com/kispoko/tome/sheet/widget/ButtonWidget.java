
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.button.ButtonWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetBackground;
import com.kispoko.tome.sheet.widget.util.WidgetCorners;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Button Widget
 */
public class ButtonWidget extends Widget implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            label;
    private ModelFunctor<ButtonWidgetFormat>    format;
    private ModelFunctor<WidgetData>            widgetData;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ButtonWidget()
    {
        this.id             = null;

        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.format         = ModelFunctor.empty(ButtonWidgetFormat.class);
        this.widgetData     = ModelFunctor.empty(WidgetData.class);
    }


    public ButtonWidget(UUID id, String label, ButtonWidgetFormat format, WidgetData widgetData)
    {
        this.id             = id;

        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.format         = ModelFunctor.full(format, ButtonWidgetFormat.class);
        this.widgetData     = ModelFunctor.full(widgetData, WidgetData.class);

        this.initializeButtonWidget();
    }


    /**
     * Create a Button Widget from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Button Widget.
     * @throws YamlParseException
     */
    public static ButtonWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                id          = UUID.randomUUID();

        String              label       = yaml.atKey("label").getString();
        ButtonWidgetFormat  format      = ButtonWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        WidgetData          widgetData  = WidgetData.fromYaml(yaml.atMaybeKey("data"), false);

        return new ButtonWidget(id, label, format, widgetData);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    // ** Id
    // -----------------------------------------------------------------------------------------

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
    // -----------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeButtonWidget();
    }


    // > Yaml
    // -----------------------------------------------------------------------------------------

    /**
     * The Mechanic Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putString("label", this.label());
        yaml.putYaml("data", this.data());

        return yaml;
    }


    // > Widget
    // -----------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent) { }


    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    /**
     * The text widget's tile view.
     * @return The tile view.
     */
    @Override
    public View view(boolean rowHasLabel, Context context)
    {
        return this.widgetView(context);
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Label
    // -----------------------------------------------------------------------------------------

    /**
     * The button label.
     * @return The label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    // ** Format
    // -----------------------------------------------------------------------------------------

    /**
     * The button widget format.
     * @return The format.
     */
    public ButtonWidgetFormat format()
    {
        return this.format.getValue();
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeButtonWidget()
    {
        // > Configure default format values
        // -------------------------------------------------------------------------------------

        // ** Width
        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        // ** Alignment
        if (this.data().format().alignment() == null)
            this.data().format().setAlignment(Alignment.CENTER);

        // ** Background
        if (this.data().format().background() == null)
            this.data().format().setBackground(WidgetBackground.DARK);

        // ** Corners
        if (this.data().format().corners() == null)
            this.data().format().setCorners(WidgetCorners.SMALL);

    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(Context context)
    {
        LinearLayout layout = widgetViewLayout(context);

        return layout;
    }


    private LinearLayout widgetViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }

}
