
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.value.ModelValue;

import java.io.Serializable;
import java.util.UUID;



/**
 * Dice Widget
 */
public class DiceWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<DiceRoll>   diceRoll;
    private ModelValue<WidgetData> widgetData;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceWidget()
    {
        this.id         = null;

        this.diceRoll   = ModelValue.empty(DiceRoll.class);
        this.widgetData = ModelValue.empty(WidgetData.class);
    }


    public DiceWidget(UUID id, DiceRoll diceRoll, WidgetData widgetData)
    {
        this.id         = id;

        this.diceRoll   = ModelValue.full(diceRoll, DiceRoll.class);
        this.widgetData = ModelValue.full(widgetData, WidgetData.class);
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


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * The widget name.
     * @return The widget name.
     */
    public String name()
    {
        return "roll";
    }


    /**
     * The general widget data.
     * @return The widget data.
     */
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    /**
     * Run an action on the Dice Widget.
     * @param action The widget action.
     */
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
