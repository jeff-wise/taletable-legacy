
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.button.ButtonIcon;
import com.kispoko.tome.sheet.widget.button.ButtonWidgetFormat;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.Background;
import com.kispoko.tome.sheet.widget.util.WidgetCorners;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.FormattedString;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private PrimitiveFunctor<String>            description;
    private PrimitiveFunctor<ButtonIcon>        icon;
    private ModelFunctor<ButtonWidgetFormat>    format;
    private ModelFunctor<WidgetData>            widgetData;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ButtonWidget()
    {
        this.id             = null;

        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);
        this.icon           = new PrimitiveFunctor<>(null, ButtonIcon.class);
        this.format         = ModelFunctor.empty(ButtonWidgetFormat.class);
        this.widgetData     = ModelFunctor.empty(WidgetData.class);
    }


    public ButtonWidget(UUID id,
                        String label,
                        String description,
                        ButtonIcon icon,
                        ButtonWidgetFormat format,
                        WidgetData widgetData)
    {
        this.id             = id;

        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);
        this.icon           = new PrimitiveFunctor<>(icon, ButtonIcon.class);
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

        String              label       = yaml.atMaybeKey("label").getString();
        String              description = yaml.atMaybeKey("description").getString();
        ButtonIcon          icon        = ButtonIcon.fromYaml(yaml.atMaybeKey("icon"));
        ButtonWidgetFormat  format      = ButtonWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        WidgetData          widgetData  = WidgetData.fromYaml(yaml.atMaybeKey("data"), false);

        return new ButtonWidget(id, label, description, icon, format, widgetData);
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
        return YamlBuilder.map()
                .putString("label", this.label())
                .putString("description", this.description())
                .putYaml("icon", this.icon())
                .putYaml("format", this.format())
                .putYaml("data", this.data());
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
        return this.widgetView(rowHasLabel, context);
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


    // ** Description
    // -----------------------------------------------------------------------------------------

    /**
     * The button description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
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


    // ** Icon
    // --------------------------------------------------------------------------------------

    /**
     * The button icon. May be null.
     * @return The button icon.
     */
    public ButtonIcon icon()
    {
        return this.icon.getValue();
    }


    /**
     * The button icon color.
     * @param icon
     */
    public void setIcon(ButtonIcon icon)
    {
        this.icon.setValue(icon);
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
            this.data().format().setBackground(Background.DARK);

        // ** Corners
        if (this.data().format().corners() == null)
            this.data().format().setCorners(WidgetCorners.SMALL);

    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = this.layout(rowHasLabel, context);

        // > Main View
        layout.addView(mainView(context));

        return layout;
    }


    private LinearLayout mainView(Context context)
    {
        LinearLayout layout = mainLayout(context);

        // Description (left)
        if (this.description() != null &&
            this.format().descriptionPosition() == Position.LEFT) {
            layout.addView(descriptionView(context));
        }

        // > Button
        layout.addView(buttonView(context));

        // Description (right)
        if (this.description() != null &&
            this.format().descriptionPosition() == Position.RIGHT) {
            layout.addView(descriptionView(context));
        }

        return layout;
    }


    private LinearLayout mainLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = this.data().format().alignment().gravityConstant()
                                    | Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private LinearLayout buttonView(Context context)
    {
        LinearLayout layout = buttonViewLayout(context);

        layout.addView(iconView(context));

        return layout;
    }


    private LinearLayout buttonViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_button_green;
        layout.elevation            = 17.0f;

        layout.margin.bottom        = R.dimen.three_dp;
        layout.margin.top           = R.dimen.three_dp;
        layout.margin.left          = R.dimen.three_dp;
        layout.margin.right         = R.dimen.three_dp;

        layout.gravity              = Gravity.CENTER;

        return layout.linearLayout(context);
    }


    private ImageView iconView(Context context)
    {
        ImageViewBuilder icon = new ImageViewBuilder();

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        // icon.layoutGravity  = Gravity.CENTER;

        if (this.icon() != null)
            icon.image      = this.icon().resouceId();

        icon.color          = this.format().iconColor().resourceId();

        return icon.imageView(context);
    }


    private TextView descriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        // > Description Text
        String labelPlaceholder = context.getString(R.string.placeholder_label);
        if (this.label() != null)
        {
            List<FormattedString.Span> spans = new ArrayList<>();
            FormattedString.Span labelSpan =
                        new FormattedString.Span(labelPlaceholder,
                                                 this.label(),
                                                 this.format().labelStyle(),
                                                 this.format().descriptionStyle().size());
            spans.add(labelSpan);

            description.textSpan = FormattedString.spannableStringBuilder(
                                                        this.description(),
                                                        spans,
                                                        context);
        }
        else
        {
            // If label is null, but there is still a <label> in the string, remove the <label>
            description.text = this.description().replace(labelPlaceholder, "");
        }

        description.font    = this.format().descriptionStyle().typeface(context);
        description.color   = this.format().descriptionStyle().color().resourceId();
        description.size    = this.format().descriptionStyle().size().resourceId();

        switch (this.format().descriptionPosition())
        {
            case LEFT:
                description.margin.right = R.dimen.widget_button_description_margin_horz;
                break;
            case RIGHT:
                description.margin.left = R.dimen.widget_button_description_margin_horz;
                break;
        }

        return description.textView(context);
    }


}
