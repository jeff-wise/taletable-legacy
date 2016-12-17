
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.UUID;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.engine.programming.variable.NumberVariable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;



/**
 * Roll Widget
 */
public class ActionWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<String>       name;
    private PrimitiveValue<String>       description;
    private ModelValue<NumberVariable>   modifier;
    private ModelValue<WidgetData>       widgetData;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ActionWidget()
    {
        this.id = null;

        this.name         = new PrimitiveValue<>(null, String.class);
        this.description  = new PrimitiveValue<>(null, String.class);
        this.modifier     = ModelValue.empty(NumberVariable.class);
        this.widgetData   = ModelValue.empty(WidgetData.class);
    }


    public ActionWidget(UUID id,
                        String name,
                        String description,
                        NumberVariable modifier,
                        WidgetData widgetData)
    {
        this.id           = id;

        this.name         = new PrimitiveValue<>(name, String.class);
        this.description  = new PrimitiveValue<>(description, String.class);
        this.modifier     = ModelValue.full(modifier, NumberVariable.class);
        this.widgetData   = ModelValue.full(widgetData, WidgetData.class);
    }


    /**
     * Create a Roll Widget from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Roll Widget.
     * @throws YamlException
     */
    public static ActionWidget fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID           id           = UUID.randomUUID();

        String         name         = yaml.atKey("name").getString();
        String         description  = yaml.atMaybeKey("description").getString();
        NumberVariable modifier     = NumberVariable.fromYaml(yaml.atKey("modifier"));
        WidgetData     widgetData   = WidgetData.fromYaml(yaml.atKey("data"));

        return new ActionWidget(id, name, description, modifier, widgetData);
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

    public String name()
    {
        return "roll";
    }


    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


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

        LinearLayout layout            = this.widgetLayout(false);
        LinearLayout contentLayout =
                (LinearLayout) layout.findViewById(R.id.widget_content_layout);
        // ((LinearLayout.LayoutParams) contentLayout.getLayoutParams()).gravity = Gravity.TOP;

        LinearLayout rollWidgetLayout  = this.rollWidgetLayout(context);

        LinearLayout headerLayout      = this.headerLayout(context);
        LinearLayout descriptionLayout = this.descriptionLayout(context);
        LinearLayout rollBonusLayout   = this.modifierLayout(context);

        rollWidgetLayout.addView(headerLayout);

        if (this.description() != null)
            rollWidgetLayout.addView(descriptionLayout);

        rollWidgetLayout.addView(rollBonusLayout);

        contentLayout.addView(rollWidgetLayout);

        // [2 A] Layouts
        // --------------------------------------------------------------------------------------

        return layout;
    }


    /**
     * The text widget's editor view.
     * @return The editor view.
     */
    public View editorView(Context context)
    {
        return new LinearLayout(context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the roll widget name.
     * @return The roll name.
     */
    public String rollName()
    {
        return this.name.getValue();
    }


    /**
     * Get the roll description.
     * @return The roll description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    /**
     * Get the modifier variable.
     * @return The modifier variable.
     */
    public NumberVariable modifierVariable()
    {
        return this.modifier.getValue();
    }


    /**
     * Get the roll modifier value (the current value of the modifier number variable).
     * @return The roll modifier value integer.
     */
    public Integer modifer()
    {
        if (!this.modifier.isNull())
        {
            try {
                return this.modifierVariable().value();
            }
            catch (SummationException exception) {
                ApplicationFailure.summation(exception);
            }
        }

        return 0;
    }


    /**
     * Get the roll widget's modifier value as a string.
     * @return The modifier string.
     */
    public String modifierString()
    {
        return this.modifer().toString();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout rollWidgetLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.padding.left     = R.dimen.widget_roll_padding_horz;
        layout.padding.right    = R.dimen.widget_roll_padding_horz;

        return layout.linearLayout(context);
    }


    private LinearLayout headerLayout(Context context)
    {
        // > Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout    = new LinearLayoutBuilder();
        ImageViewBuilder    iconView  = new ImageViewBuilder();
        TextViewBuilder     titleView = new TextViewBuilder();

        // > Layout
        // --------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity          = Gravity.CENTER_VERTICAL;
        layout.margin.left      = R.dimen.widget_roll_header_layout_margin_left;

        layout.child(iconView)
              .child(titleView);

        // > Icon View
        // --------------------------------------------------------------------------------------

        iconView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        iconView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
        iconView.image          = R.drawable.ic_roll_widget;
        iconView.padding.right  = R.dimen.widget_roll_icon_padding_right;

        // > Title View
        // --------------------------------------------------------------------------------------

        titleView.width     = LinearLayout.LayoutParams.WRAP_CONTENT;
        titleView.height    = LinearLayout.LayoutParams.WRAP_CONTENT;
        titleView.text      = this.rollName();
        titleView.size      = R.dimen.widget_roll_name_text_size;
        titleView.color     = R.color.grey_5;
        titleView.font      = Font.serifFontBold(context);


        return layout.linearLayout(context);
    }


    private LinearLayout descriptionLayout(Context context)
    {
        // > Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
        TextViewBuilder description = new TextViewBuilder();

        // > Layout
        // --------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.child(description);

        // > Description
        // --------------------------------------------------------------------------------------

        description.width  = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height = LinearLayout.LayoutParams.MATCH_PARENT;
        description.text   = this.description();

        return layout.linearLayout(context);
    }


    private LinearLayout modifierLayout(Context context)
    {
        // > Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout       = new LinearLayoutBuilder();
        ImageViewBuilder    iconView     = new ImageViewBuilder();
        TextViewBuilder     modifierView = new TextViewBuilder();

        // > Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.backgroundResource   = R.drawable.bg_roll_widget_modifier;
        layout.margin.top           = R.dimen.widget_roll_modifier_layout_margin_top;
        layout.padding.left         = R.dimen.widget_roll_modifier_layout_padding;
        layout.padding.right        = R.dimen.widget_roll_modifier_layout_padding;
        layout.padding.top          = R.dimen.widget_roll_modifier_layout_padding;
        layout.padding.bottom       = R.dimen.widget_roll_modifier_layout_padding;
        layout.gravity              = Gravity.CENTER;

//        layout.child(iconView)
        layout.child(modifierView);

        // > Icon View
        // --------------------------------------------------------------------------------------

        iconView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        iconView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
        iconView.image          = R.drawable.ic_roll_widget;
        iconView.padding.right  = R.dimen.widget_roll_icon_padding_right;

        // > Modifier Text
        // --------------------------------------------------------------------------------------

        modifierView.width  = LinearLayout.LayoutParams.WRAP_CONTENT;
        modifierView.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        modifierView.text   = "+" + this.modifierString();
        modifierView.font   = Font.serifFontBold(context);
        modifierView.size   = R.dimen.widget_roll_modifier_text_size;
        modifierView.color  = R.color.light_grey_8;

        return layout.linearLayout(context);

    }


}
