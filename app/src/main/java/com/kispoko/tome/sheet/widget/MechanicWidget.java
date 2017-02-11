
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.mechanic.Mechanic;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;



/**
 * Mechanic Widget
 */
public class MechanicWidget extends Widget implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<WidgetData>    widgetData;
    private PrimitiveFunctor<String>    mechanicCategory;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public MechanicWidget()
    {
        this.id                 = null;

        this.mechanicCategory   = new PrimitiveFunctor<>(null, String.class);
        this.widgetData         = ModelFunctor.empty(WidgetData.class);
    }


    public MechanicWidget(UUID id, String mechanicCategory, WidgetData widgetData)
    {
        this.id                 = id;

        this.mechanicCategory   = new PrimitiveFunctor<>(mechanicCategory, String.class);

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
    }


    /**
     * Create a Mechanic Widget from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Mechanic Widget.
     * @throws YamlParseException
     */
    public static MechanicWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID        id               = UUID.randomUUID();

        String      mechanicCategory = yaml.atKey("category").getTrimmedString();

        WidgetData  widgetData       = WidgetData.fromYaml(yaml.atMaybeKey("data"));

        return new MechanicWidget(id, mechanicCategory, widgetData);
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
    public void onLoad() { }


    // > Yaml
    // -----------------------------------------------------------------------------------------

    /**
     * The Mechanic Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putString("category", this.mechanicCategory());
        yaml.putYaml("data", this.data());

        return yaml;
    }



    // > Widget
    // -----------------------------------------------------------------------------------------

    @Override
    public void initialize() { }


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

    /**
     * The mechanic category that the widget displays.
     * @return The mechanic category.
     */
    public String mechanicCategory()
    {
        return this.mechanicCategory.getValue();
    }


    // > Views
    // -----------------------------------------------------------------------------------------


    private View widgetView(Context context)
    {
        LinearLayout layout = widgetViewLayout(context);

        Set<Mechanic> mechanics = SheetManager.currentSheet().engine().mechanicIndex()
                                              .mechanicsInCategory(this.mechanicCategory(), true);

        for (Mechanic mechanic : mechanics) {
            layout.addView(mechanicView(mechanic.label(), mechanic.summary(), context));
        }

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


    private LinearLayout mechanicView(String nameText, String descriptionText, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
        TextViewBuilder     name        = new TextViewBuilder();
        TextViewBuilder     description = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.child(name)
              .child(description);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text                   = nameText;
        name.font                   = Font.serifFontRegular(context);
        name.color                  = R.color.dark_blue_hl_5;
        name.size                   = R.dimen.widget_mechanic_name_text_size;

        // [3 B] Description
        // -------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.text            = descriptionText;
        description.font            = Font.serifFontRegular(context);
        description.size            = R.dimen.widget_mechanic_description_text_size;
        description.color           = R.color.dark_blue_hl_8;


        return layout.linearLayout(context);
    }
}
