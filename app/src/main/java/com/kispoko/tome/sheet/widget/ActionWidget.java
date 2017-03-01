
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
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
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.action.ActionWidgetFormat;
import com.kispoko.tome.activity.sheet.dialog.RollDialogFragment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;



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
    private PrimitiveFunctor<String>            action;
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
        this.action             = new PrimitiveFunctor<>(null, String.class);
        this.actionHighlight    = new PrimitiveFunctor<>(null, String.class);
        this.actionName         = new PrimitiveFunctor<>(null, String.class);
        this.modifier           = ModelFunctor.empty(NumberVariable.class);
    }


    public ActionWidget(UUID id,
                        WidgetData widgetData,
                        ActionWidgetFormat format,
                        String action,
                        String actionHighlight,
                        String actionName,
                        NumberVariable modifier)
    {
        this.id                 = id;

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
        this.format             = ModelFunctor.full(format, ActionWidgetFormat.class);
        this.action             = new PrimitiveFunctor<>(action, String.class);
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

        String             description     = yaml.atKey("action").getString().trim();
        String             actionHighlight = yaml.atKey("action_highlight").getString().trim();
        String             actionName      = yaml.atKey("action_name").getString().trim();
        NumberVariable     modifier        = NumberVariable.fromYaml(yaml.atKey("modifier"));
        WidgetData         widgetData      = WidgetData.fromYaml(yaml.atKey("data"), false);
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
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putString("action", this.action());
        yaml.putString("action_name", this.actionName());
        yaml.putYaml("modifier", this.modifierVariable());
        yaml.putYaml("data", this.data());
        yaml.putYaml("format", this.format());

        return yaml;
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
        return this.widgetView(context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the roll description.
     * @return The roll description.
     */
    public String action()
    {
        return this.action.getValue();
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

        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        if (this.data().format().alignment() == null)
            this.data().format().setAlignment(Alignment.CENTER);

        if (this.data().format().background() == null)
            this.data().format().setBackground(BackgroundColor.NONE);
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(Context context)
    {
        LinearLayout layout = this.widgetViewLayout(context);

        layout.addView(actionView(context));

        return layout;
    }


    private LinearLayout widgetViewLayout(final Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.onClick              = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActionWidgetShortClick(context);
            }
        };

        return layout.linearLayout(context);
    }


    private TextView actionView(Context context)
    {
        TextView action = actionTextView(context);

        SpannableStringBuilder builder = new SpannableStringBuilder(this.action());

        int actionNameIndex = this.action().indexOf(this.actionHighlight());

        ImageSpan diceRollIcon = this.actionImageSpan(context);

        String imageSpace = "i" + "\u2006";
        builder.insert(actionNameIndex, imageSpace);
        builder.setSpan(diceRollIcon, actionNameIndex, actionNameIndex + 1, 0);

        int actionNameEnd = actionNameIndex + 2 + this.actionHighlight().length();
        builder.setSpan(this.actionHighlightSpan(context), actionNameIndex + 1, actionNameEnd, 0);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        builder.setSpan(boldSpan, actionNameIndex + 1, actionNameEnd, 0 );

        action.setText(builder);

        return action;
    }


    private TextView actionTextView(Context context)
    {
        TextViewBuilder action = new TextViewBuilder();

        action.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        action.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        action.font            = Font.serifFontRegular(context);
        action.color           = R.color.dark_blue_hl_6;
        action.size            = this.format().size().resourceId();

        switch (this.data().format().alignment())
        {
            case LEFT:
                break;
            case CENTER:
                action.layoutGravity = Gravity.CENTER_HORIZONTAL;
                action.gravity       = Gravity.CENTER_HORIZONTAL;
                break;
            case RIGHT:
                break;
        }

        return action.textView(context);
    }


    private ForegroundColorSpan actionHighlightSpan(Context context)
    {
        int colorId = this.format().actionColor().resourceId();
        return new ForegroundColorSpan(ContextCompat.getColor(context, colorId));
    }


    private ImageSpan actionImageSpan(Context context)
    {
        switch (this.format().actionColor())
        {
            case BLUE:
                switch (this.format().size())
                {
                    case SMALL:
                        return new ImageSpan(context, R.drawable.ic_roll_blue_l);
                    case MEDIUM:
                        return new ImageSpan(context, R.drawable.ic_roll_blue_m);
                    case LARGE:
                        return new ImageSpan(context, R.drawable.ic_roll_blue_l);
                }
            case GREEN:
                switch (this.format().size())
                {
                    case SMALL:
                        return new ImageSpan(context, R.drawable.ic_roll_green_m);
                    case MEDIUM:
                        return new ImageSpan(context, R.drawable.ic_roll_green_m);
                    case LARGE:
                        return new ImageSpan(context, R.drawable.ic_roll_green_m);
                }
            default:
                return new ImageSpan(context, R.drawable.ic_roll_blue_l);
        }
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
