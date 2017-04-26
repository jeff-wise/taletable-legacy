
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
import com.kispoko.tome.sheet.widget.button.ButtonViewType;
import com.kispoko.tome.sheet.widget.button.ButtonWidgetFormat;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.lib.ui.FormattedString;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

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

    private PrimitiveFunctor<ButtonViewType>    viewType;

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

        this.viewType       = new PrimitiveFunctor<>(null, ButtonViewType.class);

        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);
        this.icon           = new PrimitiveFunctor<>(null, ButtonIcon.class);

        this.format         = ModelFunctor.empty(ButtonWidgetFormat.class);
        this.widgetData     = ModelFunctor.empty(WidgetData.class);
    }


    public ButtonWidget(UUID id,
                        ButtonViewType viewType,
                        String label,
                        String description,
                        ButtonIcon icon,
                        ButtonWidgetFormat format,
                        WidgetData widgetData)
    {
        this.id             = id;

        this.viewType       = new PrimitiveFunctor<>(viewType, ButtonViewType.class);

        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);
        this.icon           = new PrimitiveFunctor<>(icon, ButtonIcon.class);

        this.format         = ModelFunctor.full(format, ButtonWidgetFormat.class);
        this.widgetData     = ModelFunctor.full(widgetData, WidgetData.class);

        // > Set defaults
        this.setViewType(viewType);

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

        ButtonViewType      viewType    = ButtonViewType.fromYaml(yaml.atMaybeKey("view_type"));

        String              label       = yaml.atMaybeKey("label").getString();
        String              description = yaml.atMaybeKey("description").getString();
        ButtonIcon          icon        = ButtonIcon.fromYaml(yaml.atMaybeKey("icon"));

        ButtonWidgetFormat  format      = ButtonWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        WidgetData          widgetData  = WidgetData.fromYaml(yaml.atMaybeKey("data"));

        return new ButtonWidget(id, viewType, label, description, icon, format, widgetData);
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
                .putYaml("view_type", this.viewType())
                .putString("label", this.label())
                .putString("description", this.description())
                .putYaml("icon", this.icon())
                .putYaml("format", this.format())
                .putYaml("data", this.data());
    }


    // > Widget
    // -----------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent, Context context) { }


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

    // ** View Type
    // -----------------------------------------------------------------------------------------

    /**
     * The button view type.
     * @return The view type.
     */
    public ButtonViewType viewType()
    {
        return this.viewType.getValue();
    }


    /**
     * Set the button view type. If null, defaults to TEXT view.
     * @param viewType The view type.
     */
    public void setViewType(ButtonViewType viewType)
    {
        if (viewType != null)
            this.viewType.setValue(viewType);
        else
            this.viewType.setValue(ButtonViewType.TEXT);
    }


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

        // ** Alignment
        if (this.data().format().alignmentIsDefault())
            this.data().format().setAlignment(Alignment.CENTER);

        // ** Background
        if (this.data().format().backgroundIsDefault())
            this.data().format().setBackground(BackgroundColor.DARK);

        // ** Corners
        if (this.data().format().cornersIsDefault())
            this.data().format().setCorners(Corners.SMALL);

        // ** Elevation
        if (this.viewType() == ButtonViewType.TEXT) {
            if (this.data().format().elevation() == null)
                this.data().format().setElevation(7);
        }

    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = this.layout(rowHasLabel, context);

        // > Main View
        switch (this.viewType())
        {
            case TEXT:
                layout.addView(buttonTextView(context));
                break;
            case CIRCLE_ICON:
                layout.addView(buttonCircleIconView(context));
                break;
        }

        return layout;
    }


    // ** Text View
    // -----------------------------------------------------------------------------------------

    private LinearLayout buttonTextView(Context context)
    {
        LinearLayout layout = this.buttonTextViewLayout(context);

        // > Icon
        if (this.icon() != null)
            layout.addView(buttonTextIconView(context));

        // > Label
        layout.addView(buttonTextLabelView(context));

        return layout;
    }


    private LinearLayout buttonTextViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;

        // > Width
        if (this.format().paddingHorizontal() != null)
            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        else
            layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = this.data().format().background().colorId();

        if (this.format().height() != Height.WRAP) {
            layout.backgroundResource = this.format().height()
                                              .resourceId(this.data().format().corners());
        }
        else {
            layout.backgroundResource = this.data().format().corners().resourceId();
        }

        layout.gravity              = this.data().format().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;

        layout.layoutGravity        = this.data().format().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;


        layout.elevation            = this.data().format().elevation().floatValue();

        if (this.data().format().elevation() != null)
        {
            if (this.data().format().margins().left() < 3)
                this.data().format().margins().setLeft(3);

            if (this.data().format().margins().right() < 3)
                this.data().format().margins().setRight(3);

            if (this.data().format().margins().bottom() < 3)
                this.data().format().margins().setBottom(4);

        }

        layout.marginSpacing        = this.data().format().margins();

        if (this.format().paddingHorizontal() != null) {
            layout.padding.leftDp   = this.format().paddingHorizontal().floatValue();
            layout.padding.rightDp  = this.format().paddingHorizontal().floatValue();
        }

        if (this.format().height() == Height.WRAP) {
            layout.padding.topDp    = this.format().paddingVertical().floatValue();
            layout.padding.bottomDp = this.format().paddingVertical().floatValue();
        }

        return layout.linearLayout(context);
    }


    private ImageView buttonTextIconView(Context context)
    {
        ImageViewBuilder icon = new ImageViewBuilder();

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = this.icon().resouceId();

        icon.color          = this.format().iconColor().resourceId();

        return icon.imageView(context);
    }


    private TextView buttonTextLabelView(Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = this.label();

        this.format().labelStyle().styleTextViewBuilder(label, context);

        return label.textView(context);
    }


    // ** Circle Icon View
    // -----------------------------------------------------------------------------------------

    private LinearLayout buttonCircleIconView(Context context)
    {
        LinearLayout layout = buttonCircleIconViewLayout(context);

        // Description (left)
        if (this.description() != null &&
            this.format().descriptionPosition() == Position.LEFT) {
            layout.addView(buttonCircleIconDescriptionView(context));
        }

        // > Button
        layout.addView(buttonCircleIconButtonView(context));

        // Description (right)
        if (this.description() != null &&
            this.format().descriptionPosition() == Position.RIGHT) {
            layout.addView(buttonCircleIconDescriptionView(context));
        }

        return layout;
    }


    private LinearLayout buttonCircleIconViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = this.data().format().alignment().gravityConstant()
                                    | Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private LinearLayout buttonCircleIconButtonView(Context context)
    {
        LinearLayout layout = buttonCircleIconButtonViewLayout(context);

        layout.addView(buttonCircleIconIconView(context));

        return layout;
    }


    private LinearLayout buttonCircleIconButtonViewLayout(Context context)
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


    private ImageView buttonCircleIconIconView(Context context)
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


    private TextView buttonCircleIconDescriptionView(Context context)
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
                    new FormattedString.Span(this.label(),
                                             labelPlaceholder,
                                             this.format().labelStyle().color().color(context),
                                             this.format().descriptionStyle().size().size(context),
                                             this.format().labelStyle().font());
            spans.add(labelSpan);

            description.textSpan = FormattedString.spannableStringBuilder(this.description(),
                                                                          spans);
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
