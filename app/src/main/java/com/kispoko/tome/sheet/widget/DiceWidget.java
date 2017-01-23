
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;

import java.io.Serializable;
import java.util.UUID;



/**
 * Dice Widget
 */
public class DiceWidget extends Widget
                        implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<DiceRoll> diceRoll;
    private ModelFunctor<WidgetData> widgetData;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceWidget()
    {
        this.id         = null;

        this.diceRoll   = ModelFunctor.empty(DiceRoll.class);
        this.widgetData = ModelFunctor.empty(WidgetData.class);
    }


    public DiceWidget(UUID id, DiceRoll diceRoll, WidgetData widgetData)
    {
        this.id         = id;

        this.diceRoll   = ModelFunctor.full(diceRoll, DiceRoll.class);
        this.widgetData = ModelFunctor.full(widgetData, WidgetData.class);
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
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Dice Widget's yaml representation.
     * @return
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map();
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize() { }


    /**
     * The general widget data.
     * @return The widget data.
     */
    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    /**
     * Run an action on the Dice Widget.
     * @param action The widget action.
     */
    @Override
    public void runAction(Action action) { }


    /**
     * The text widget's tile view.
     * @return The tile view.
     */
    public View tileView()
    {
        // [1] Setup / Declarations
        // --------------------------------------------------------------------------------------

        Context context = SheetManager.currentSheetContext();

        // [2 A] Layouts
        // --------------------------------------------------------------------------------------

        return new LinearLayout(context);
    }


    /**
     * The text widget's editor view.
     * @return The editor view.
     */
    public View editorView(Context context)
    {
        return new LinearLayout(context);
    }


}
