
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.UUID;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.action.ActionWidgetFormat;
import com.kispoko.tome.activity.sheet.dialog.RollDialogFragment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;



/**
 * Action Widget
 */
public class ActionWidget extends Widget
                          implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<WidgetData>            widgetData;
    private ModelFunctor<ActionWidgetFormat>    format;
    private PrimitiveFunctor<String>            description;
    private PrimitiveFunctor<String>            actionHighlight;
    private PrimitiveFunctor<String>            actionName;
    private ModelFunctor<NumberVariable>        modifier;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ActionWidget()
    {
        this.id                 = null;

        this.widgetData         = ModelFunctor.empty(WidgetData.class);
        this.format             = ModelFunctor.empty(ActionWidgetFormat.class);
        this.description        = new PrimitiveFunctor<>(null, String.class);
        this.actionHighlight    = new PrimitiveFunctor<>(null, String.class);
        this.actionName         = new PrimitiveFunctor<>(null, String.class);
        this.modifier           = ModelFunctor.empty(NumberVariable.class);
    }


    public ActionWidget(UUID id,
                        WidgetData widgetData,
                        ActionWidgetFormat format,
                        String description,
                        String actionHighlight,
                        String actionName,
                        NumberVariable modifier)
    {
        this.id                 = id;

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
        this.format             = ModelFunctor.full(format, ActionWidgetFormat.class);
        this.description        = new PrimitiveFunctor<>(description, String.class);
        this.actionHighlight    = new PrimitiveFunctor<>(actionHighlight, String.class);
        this.actionName         = new PrimitiveFunctor<>(actionName, String.class);
        this.modifier           = ModelFunctor.full(modifier, NumberVariable.class);

        this.initializeActionWidget();
    }


    /**
     * Create a Roll Widget from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Roll Widget.
     * @throws YamlParseException
     */
    public static ActionWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID               id              = UUID.randomUUID();

        String             description     = yaml.atKey("description").getString();
        String             actionHighlight = yaml.atKey("action_highlight").getTrimmedString();
        String             actionName      = yaml.atKey("action_name").getTrimmedString();
        NumberVariable     modifier        = NumberVariable.fromYaml(yaml.atKey("modifier"));
        WidgetData         widgetData      = WidgetData.fromYaml(yaml.atKey("data"));
        ActionWidgetFormat format          = ActionWidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        return new ActionWidget(id, widgetData, format, description,
                                actionHighlight, actionName, modifier);
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
    public void onLoad()
    {
        this.initializeActionWidget();
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Action Widget's yaml representation.
     * @return
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("description", this.description())
                .putString("action_highlight", this.actionHighlight())
                .putString("action_name", this.actionName())
                .putYaml("modifier", this.modifierVariable())
                .putYaml("data", this.data())
                .putYaml("format", this.format());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent)
    {
        // [1] Add variable to state
        // --------------------------------------------------------------------------------------

        if (!this.modifier.isNull()) {
            State.addVariable(this.modifierVariable());
        }

    }


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
    // ------------------------------------------------------------------------------------------

    /**
     * Get the roll description.
     * @return The roll description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    /**
     * The action highlight.
     * @return The action highlight.
     */
    public String actionHighlight()
    {
        return this.actionHighlight.getValue();
    }


    /**
     * The action name.
     * @return The action name.
     */
    public String actionName()
    {
        return this.actionName.getValue();
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
     * The action widget format.
     * @return The Action Widget Format.
     */
    public ActionWidgetFormat format()
    {
        return this.format.getValue();
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
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
                return 0;
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
        try {
            return this.modifierVariable().valueString();
        }
        catch (NullVariableException exception) {
            ApplicationFailure.nullVariable(exception);
            return "";
        }
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeActionWidget()
    {
        // [1] Apply default formats
        // -------------------------------------------------------------------------------------

        if (this.data().format().alignmentIsDefault())
            this.data().format().setAlignment(Alignment.CENTER);

        if (this.data().format().backgroundIsDefault())
            this.data().format().setBackground(BackgroundColor.NONE);

        if (this.data().format().cornersIsDefault())
            this.data().format().setCorners(Corners.NONE);
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = this.layout(rowHasLabel, context);

        layout.addView(mainView(context));

        return layout;
    }


    private LinearLayout mainView(Context context)
    {
        LinearLayout layout = mainViewLayout(context);

        layout.addView(descriptionTextView(context));

        return layout;
    }


    private LinearLayout mainViewLayout(final Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        // > Width
        if (this.format().paddingHorizontal() > 0)
            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        else
            layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;
        layout.layoutGravity        = this.data().format().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;


        if (this.data().format().background() != BackgroundColor.EMPTY)
        {
            layout.backgroundColor      = this.data().format().background().colorId();

            if (this.format().height() != Height.WRAP)
            {
                layout.backgroundResource = this.format().height()
                                                  .resourceId(this.data().format().corners());
            }
            else
            {
                layout.backgroundResource = this.data().format().corners().widgetResourceId();
            }
        }

        layout.marginSpacing        = this.data().format().margins();

        // > Horizontal Padding
        layout.padding.leftDp   = this.format().paddingHorizontal();
        layout.padding.rightDp  = this.format().paddingHorizontal();

        // > Vertical Padding
        if (this.format().height() == Height.WRAP) {
            layout.padding.topDp    = this.format().paddingVertical();
            layout.padding.bottomDp = this.format().paddingVertical();
        }

        layout.onClick              = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActionWidgetShortClick(context);
            }
        };

        return layout.linearLayout(context);
    }



    private TextView descriptionTextView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width       = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height      = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.textSpan    = descriptionSpannable(context);

        this.format().descriptionStyle().styleTextViewBuilder(description, context);

        switch (this.data().format().alignment())
        {
            case LEFT:
                description.layoutGravity = Gravity.LEFT;
                description.gravity       = Gravity.LEFT;
                break;
            case CENTER:
                description.layoutGravity = Gravity.CENTER_HORIZONTAL;
                description.gravity       = Gravity.CENTER_HORIZONTAL;
                break;
            case RIGHT:
                description.layoutGravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                description.gravity       = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }

        return description.textView(context);
    }


    private SpannableStringBuilder descriptionSpannable(Context context)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder(this.description());

        int actionHighlightIndex = this.description().indexOf(this.actionHighlight());

        if (actionHighlightIndex >= 0)
        {
            ImageSpan diceRollIcon = this.actionImageSpan(context);

            String imageSpace = "i" + "\u2006";
            builder.insert(actionHighlightIndex, imageSpace);
            builder.setSpan(diceRollIcon, actionHighlightIndex, actionHighlightIndex + 1, 0);

            int actionNameEnd = actionHighlightIndex + 2 + this.actionHighlight().length();
            builder.setSpan(this.actionHighlightSpan(context), actionHighlightIndex + 1, actionNameEnd, 0);

            int textSizeResourceId = this.format().actionStyle().size().resourceId();
            int textSizePx = context.getResources().getDimensionPixelSize(textSizeResourceId);
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(textSizePx, true);
            builder.setSpan(sizeSpan, actionHighlightIndex + 1, actionNameEnd, 0);

            // > Typeface
            // -------------------------------------------------------------------------------------
            switch (this.format().actionStyle().font())
            {
                case BOLD:
                    StyleSpan valueBoldSpan = new StyleSpan(Typeface.BOLD);
                    builder.setSpan(valueBoldSpan, actionHighlightIndex + 1, actionNameEnd, 0);
                    break;
                case ITALIC:
                    StyleSpan valueItalicSpan = new StyleSpan(Typeface.ITALIC);
                    builder.setSpan(valueItalicSpan, actionHighlightIndex + 1, actionNameEnd, 0);
                    break;
                case BOLD_ITALIC:
                    StyleSpan valueBoldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                    builder.setSpan(valueBoldItalicSpan, actionHighlightIndex + 1, actionNameEnd, 0);
                    break;
            }
        }

        return builder;
    }


    private ForegroundColorSpan actionHighlightSpan(Context context)
    {
        int colorId = this.format().actionStyle().color().resourceId();
        return new ForegroundColorSpan(ContextCompat.getColor(context, colorId));
    }


    private ImageSpan actionImageSpan(Context context)
    {
        Drawable diceDrawable = ContextCompat.getDrawable(context, R.drawable.ic_roll_blue_m);

        int diceColorResourceId = this.format().actionStyle().color().resourceId();
        int diceColor = ContextCompat.getColor(context, diceColorResourceId);

        diceDrawable.setColorFilter(new PorterDuffColorFilter(diceColor, PorterDuff.Mode.SRC_IN));


        int diceSizeId = 0;
        switch (this.format().actionStyle().size())
        {
            case SMALL:
                diceSizeId = R.dimen.widget_action_dice_size_small;
                break;
            case MEDIUM_SMALL:
                diceSizeId = R.dimen.widget_action_dice_size_medium_small;
                break;
            case MEDIUM:
                diceSizeId = R.dimen.widget_action_dice_size_medium;
                break;
            case MEDIUM_LARGE:
                diceSizeId = R.dimen.widget_action_dice_size_medium_large;
                break;
            case LARGE:
                diceSizeId = R.dimen.widget_action_dice_size_large;
                break;
            case VERY_LARGE:
                diceSizeId = R.dimen.widget_action_dice_size_very_large;
                break;
        }

        Float width = context.getResources().getDimension(diceSizeId);
        diceDrawable.setBounds(0, 0, width.intValue(), width.intValue());


        return new ImageSpan(diceDrawable);

    }


    // > Clicks
    // -----------------------------------------------------------------------------------------

    /**
     * On a short click, open the value editor.
     */
    private void onActionWidgetShortClick(Context context)
    {
        SheetActivity sheetActivity = (SheetActivity) context;

        RollDialogFragment dialog =
                RollDialogFragment.newInstance(this.actionName(), this.modifierVariable());
        dialog.show(sheetActivity.getSupportFragmentManager(), "");
    }


}
