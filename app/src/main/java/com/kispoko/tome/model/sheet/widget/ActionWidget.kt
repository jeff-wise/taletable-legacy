
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.TextStyle
import effect.Err
import effect.effApply
import effect.effApply5
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Action Description
 */
data class ActionDescription(val value : String)
{

    companion object : Factory<ActionDescription>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ActionDescription> = when (doc)
        {
            is DocText -> valueResult(ActionDescription(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Action Description Highlight
 */
data class ActionDescriptionHighlight(val value : String)
{

    companion object : Factory<ActionDescriptionHighlight>
    {
        override fun fromDocument(doc: SpecDoc)
                      : ValueParser<ActionDescriptionHighlight> = when (doc)
        {
            is DocText -> valueResult(ActionDescriptionHighlight(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Action Name
 */
data class ActionName(val value : String)
{

    companion object : Factory<ActionName>
    {
        override fun fromDocument(doc: SpecDoc)
                      : ValueParser<ActionName> = when (doc)
        {
            is DocText -> valueResult(ActionName(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Action Result
 */
data class ActionResult(val value : String)
{

    companion object : Factory<ActionResult>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ActionResult> = when (doc)
        {
            is DocText -> valueResult(ActionResult(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Action Widget Format
 */
data class ActionWidgetFormat(override val id : UUID,
                              val widgetFormat : Func<WidgetFormat>,
                              val descriptionStyle : Func<TextStyle>,
                              val actionStyle : Func<TextStyle>,
                              val height : Func<Height>) : Model
{
    companion object : Factory<ActionWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ActionWidgetFormat> = when (doc)
        {
            is DocDict -> effApply5(::ActionWidgetFormat,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Widget Format
                                    doc.at("widget_format") ap {
                                        effApply(::Comp, WidgetFormat.fromDocument(it))
                                    },
                                    // Description Style
                                    doc.at("description_style") ap {
                                        effApply(::Comp, TextStyle.fromDocument(it))
                                    },
                                    // Action Style
                                    doc.at("action_style") ap {
                                        effApply(::Comp, TextStyle.fromDocument(it))
                                    },
                                    effApply(::Prim, doc.enum<Height>("height")))
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



//
//public class ActionWidget extends Widget
//                          implements Serializable
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    private UUID                                id;
//
//
//    // > Functors
//    // ------------------------------------------------------------------------------------------
//
//    private ModelFunctor<WidgetData>            widgetData;
//    private ModelFunctor<ActionWidgetFormat>    format;
//
//    private PrimitiveFunctor<String>            description;
//    private PrimitiveFunctor<String>            actionHighlight;
//
//    private PrimitiveFunctor<String>            actionName;
//    private PrimitiveFunctor<String>            actionResult;
//
//    private ModelFunctor<NumberVariable>        modifier;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public ActionWidget()
//    {
//        this.id                 = null;
//
//        this.widgetData         = ModelFunctor.empty(WidgetData.class);
//        this.format             = ModelFunctor.empty(ActionWidgetFormat.class);
//        this.description        = new PrimitiveFunctor<>(null, String.class);
//        this.actionHighlight    = new PrimitiveFunctor<>(null, String.class);
//        this.actionName         = new PrimitiveFunctor<>(null, String.class);
//        this.actionResult       = new PrimitiveFunctor<>(null, String.class);
//        this.modifier           = ModelFunctor.empty(NumberVariable.class);
//
//        this.initializeFunctors();
//    }
//
//
//    public ActionWidget(UUID id,
//                        WidgetData widgetData,
//                        ActionWidgetFormat format,
//                        String description,
//                        String actionHighlight,
//                        String actionName,
//                        String actionResult,
//                        NumberVariable modifier)
//    {
//        this.id                 = id;
//
//        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
//        this.format             = ModelFunctor.full(format, ActionWidgetFormat.class);
//        this.description        = new PrimitiveFunctor<>(description, String.class);
//        this.actionHighlight    = new PrimitiveFunctor<>(actionHighlight, String.class);
//        this.actionName         = new PrimitiveFunctor<>(actionName, String.class);
//        this.actionResult       = new PrimitiveFunctor<>(actionResult, String.class);
//        this.modifier           = ModelFunctor.full(modifier, NumberVariable.class);
//
//        this.initializeActionWidget();
//        this.initializeFunctors();
//    }
//
//
//    /**
//     * Create a Roll Widget from its Yaml representation.
//     * @param yaml The yaml parser.
//     * @return The parsed Roll Widget.
//     * @throws YamlParseException
//     */
//    public static ActionWidget fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        UUID               id              = UUID.randomUUID();
//
//        String             description     = yaml.atKey("description").getString();
//        String             actionHighlight = yaml.atKey("action_highlight").getTrimmedString();
//        String             actionName      = yaml.atKey("action_name").getTrimmedString();
//        String             actionResult    = yaml.atMaybeKey("action_result").getTrimmedString();
//        NumberVariable     modifier        = NumberVariable.fromYaml(yaml.atKey("modifier"));
//        WidgetData         widgetData      = WidgetData.fromYaml(yaml.atKey("data"));
//        ActionWidgetFormat format          = ActionWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
//
//        return new ActionWidget(id, widgetData, format, description, actionHighlight,
//                                actionName, actionResult, modifier);
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    // ** Id
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the model identifier.
//     * @return The model UUID.
//     */
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    /**
//     * Set the model identifier.
//     * @param id The new model UUID.
//     */
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the RulesEngine is completely loaded for the first time.
//     */
//    public void onLoad()
//    {
//        this.initializeActionWidget();
//    }
//
//
//    // > Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Action Widget's yaml representation.
//     * @return
//     */
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putString("description", this.description())
//                .putString("action_highlight", this.actionHighlight())
//                .putString("action_name", this.actionName())
//                .putString("action_result", this.actionResult())
//                .putYaml("modifier", this.modifierVariable())
//                .putYaml("data", this.data())
//                .putYaml("format", this.format());
//    }
//
//
//    // > Widget
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public void initialize(GroupParent groupParent, Context context)
//    {
//        // [1] Add variable to state
//        // --------------------------------------------------------------------------------------
//
//        if (!this.modifier.isNull()) {
//            State.addVariable(this.modifierVariable());
//        }
//
//    }
//
//
//    @Override
//    public WidgetData data()
//    {
//        return this.widgetData.getValue();
//    }
//
//
//    /**
//     * The text widget's tile view.
//     * @return The tile view.
//     */
//    @Override
//    public View view(boolean rowHasLabel, Context context)
//    {
//        return this.widgetView(rowHasLabel, context);
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the roll description.
//     * @return The roll description.
//     */
//    public String description()
//    {
//        return this.description.getValue();
//    }
//
//
//    /**
//     * The action highlight.
//     * @return The action highlight.
//     */
//    public String actionHighlight()
//    {
//        return this.actionHighlight.getValue();
//    }
//
//
//    /**
//     * The action name.
//     * @return The action name.
//     */
//    public String actionName()
//    {
//        return this.actionName.getValue();
//    }
//
//
//    /**
//     * The action result. For example, "fire damage" or "strength check".
//     * @return The action result description string..
//     */
//    public String actionResult()
//    {
//        return this.actionResult.getValue();
//    }
//
//
//    /**
//     * Get the modifier variable.
//     * @return The modifier variable.
//     */
//    public NumberVariable modifierVariable()
//    {
//        return this.modifier.getValue();
//    }
//
//
//    /**
//     * The action widget format.
//     * @return The Action Widget Format.
//     */
//    public ActionWidgetFormat format()
//    {
//        return this.format.getValue();
//    }
//
//
//    /**
//     * Get the roll modifier value (the current value of the modifier number variable).
//     * @return The roll modifier value integer.
//     */
//    public Integer modifer()
//    {
//        if (!this.modifier.isNull())
//        {
//            try {
//                return this.modifierVariable().value();
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//                return 0;
//            }
//        }
//
//        return 0;
//    }
//
//
//    /**
//     * Get the roll widget's modifier value as a string.
//     * @return The modifier string.
//     */
//    public String modifierString()
//    {
//        try {
//            return this.modifierVariable().valueString();
//        }
//        catch (NullVariableException exception) {
//            ApplicationFailure.nullVariable(exception);
//            return "";
//        }
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeActionWidget()
//    {
//        // [1] Apply default formats
//        // -------------------------------------------------------------------------------------
//
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.NONE);
//    }
//
//
//    private void initializeFunctors()
//    {
//        // Action Name
//        this.actionName.setName("action_name");
//        this.actionName.setLabelId(R.string.widget_action_field_action_name_label);
//        this.actionName.setDescriptionId(R.string.widget_action_field_action_name_description);
//
//        // Action Result
//        this.actionResult.setName("action_result");
//        this.actionResult.setLabelId(R.string.widget_action_field_action_result_label);
//        this.actionResult.setDescriptionId(R.string.widget_action_field_action_result_description);
//
//        // Modifier
//        this.modifier.setName("modifier");
//        this.modifier.setLabelId(R.string.widget_action_field_modifier_label);
//        this.modifier.setDescriptionId(R.string.widget_action_field_modifier_description);
//
//        // Description
//        this.description.setName("description");
//        this.description.setLabelId(R.string.widget_action_field_description_label);
//        this.description.setDescriptionId(R.string.widget_action_field_modifier_description);
//
//        // Action Highlight
//        this.actionHighlight.setName("action_highlight");
//        this.actionHighlight.setLabelId(R.string.widget_action_field_action_highlight_label);
//        this.actionHighlight
//                    .setDescriptionId(R.string.widget_action_field_action_highlight_description);
//
//        // Format
//        this.format.setName("format");
//        this.format.setLabelId(R.string.widget_action_field_format_label);
//        this.format.setDescriptionId(R.string.widget_action_field_format_description);
//
//        // Widget Data
////        this.widgetData.setName("widget_data");
////        this.widgetData.setLabelId(R.string.value_set_field_values_label);
////        this.widgetData.setDescriptionId(R.string.value_set_field_values_description);
////
//
//    }
//
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private View widgetView(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = this.layout(rowHasLabel, context);
//
//        layout.addView(mainView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout mainView(Context context)
//    {
//        LinearLayout layout = mainViewLayout(context);
//
//        layout.addView(descriptionTextView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout mainViewLayout(final Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//
//        // > Width
//        if (this.format().paddingHorizontal() > 0)
//            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//        else
//            layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//        layout.layoutGravity        = this.data().format().alignment().gravityConstant()
//                                        | Gravity.CENTER_VERTICAL;
//
//
//        if (this.data().format().background() != BackgroundColor.EMPTY)
//        {
//            layout.backgroundColor      = this.data().format().background().colorId();
//
//            if (this.format().height() != Height.WRAP)
//            {
//                layout.backgroundResource = this.format().height()
//                                                  .resourceId(this.data().format().corners());
//            }
//            else
//            {
//                layout.backgroundResource = this.data().format().corners().widgetResourceId();
//            }
//        }
//
//        layout.marginSpacing        = this.data().format().margins();
//
//        // > Horizontal Padding
//        layout.padding.leftDp   = this.format().paddingHorizontal().floatValue();
//        layout.padding.rightDp  = this.format().paddingHorizontal().floatValue();
//
//        // > Vertical Padding
//        if (this.format().height() == Height.WRAP) {
//            layout.padding.topDp    = this.format().paddingVertical().floatValue();
//            layout.padding.bottomDp = this.format().paddingVertical().floatValue();
//        }
//
//        layout.onClick              = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onActionWidgetShortClick(context);
//            }
//        };
//
//        return layout.linearLayout(context);
//    }
//
//
//
//    private TextView descriptionTextView(Context context)
//    {
//        TextViewBuilder description = new TextViewBuilder();
//
//        description.width       = LinearLayout.LayoutParams.MATCH_PARENT;
//        description.height      = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        description.textSpan    = descriptionSpannable(context);
//
//        this.format().descriptionStyle().styleTextViewBuilder(description, context);
//
//        switch (this.data().format().alignment())
//        {
//            case LEFT:
//                description.layoutGravity = Gravity.LEFT;
//                description.gravity       = Gravity.LEFT;
//                break;
//            case CENTER:
//                description.layoutGravity = Gravity.CENTER_HORIZONTAL;
//                description.gravity       = Gravity.CENTER_HORIZONTAL;
//                break;
//            case RIGHT:
//                description.layoutGravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
//                description.gravity       = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
//                break;
//        }
//
//        return description.textView(context);
//    }
//
//
//    private SpannableStringBuilder descriptionSpannable(Context context)
//    {
//        SpannableStringBuilder builder = new SpannableStringBuilder(this.description());
//
//        int actionHighlightIndex = this.description().indexOf(this.actionHighlight());
//
//        if (actionHighlightIndex >= 0)
//        {
//            ImageSpan diceRollIcon = this.actionImageSpan(context);
//
//            String imageSpace = "i" + "\u2006";
//            builder.insert(actionHighlightIndex, imageSpace);
//            builder.setSpan(diceRollIcon, actionHighlightIndex, actionHighlightIndex + 1, 0);
//
//            int actionNameEnd = actionHighlightIndex + 2 + this.actionHighlight().length();
//            builder.setSpan(this.actionHighlightSpan(context), actionHighlightIndex + 1, actionNameEnd, 0);
//
//            int textSizeResourceId = this.format().actionStyle().size().resourceId();
//            int textSizePx = context.getResources().getDimensionPixelSize(textSizeResourceId);
//            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(textSizePx, true);
//            builder.setSpan(sizeSpan, actionHighlightIndex + 1, actionNameEnd, 0);
//
//            // > Typeface
//            // -------------------------------------------------------------------------------------
//            switch (this.format().actionStyle().font())
//            {
//                case BOLD:
//                    StyleSpan valueBoldSpan = new StyleSpan(Typeface.BOLD);
//                    builder.setSpan(valueBoldSpan, actionHighlightIndex + 1, actionNameEnd, 0);
//                    break;
//                case ITALIC:
//                    StyleSpan valueItalicSpan = new StyleSpan(Typeface.ITALIC);
//                    builder.setSpan(valueItalicSpan, actionHighlightIndex + 1, actionNameEnd, 0);
//                    break;
//                case BOLD_ITALIC:
//                    StyleSpan valueBoldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
//                    builder.setSpan(valueBoldItalicSpan, actionHighlightIndex + 1, actionNameEnd, 0);
//                    break;
//            }
//        }
//
//        return builder;
//    }
//
//
//    private ForegroundColorSpan actionHighlightSpan(Context context)
//    {
//        int colorId = this.format().actionStyle().color().resourceId();
//        return new ForegroundColorSpan(ContextCompat.getColor(context, colorId));
//    }
//
//
//    private ImageSpan actionImageSpan(Context context)
//    {
//        Drawable diceDrawable = ContextCompat.getDrawable(context, R.drawable.ic_roll_blue_m);
//
//        int diceColorResourceId = this.format().actionStyle().color().resourceId();
//        int diceColor = ContextCompat.getColor(context, diceColorResourceId);
//
//        diceDrawable.setColorFilter(new PorterDuffColorFilter(diceColor, PorterDuff.Mode.SRC_IN));
//
//
//        int diceSizeId = 0;
//        switch (this.format().actionStyle().size())
//        {
//            case SMALL:
//                diceSizeId = R.dimen.widget_action_dice_size_small;
//                break;
//            case MEDIUM_SMALL:
//                diceSizeId = R.dimen.widget_action_dice_size_medium_small;
//                break;
//            case MEDIUM:
//                diceSizeId = R.dimen.widget_action_dice_size_medium;
//                break;
//            case MEDIUM_LARGE:
//                diceSizeId = R.dimen.widget_action_dice_size_medium_large;
//                break;
//            case LARGE:
//                diceSizeId = R.dimen.widget_action_dice_size_large;
//                break;
//            case VERY_LARGE:
//                diceSizeId = R.dimen.widget_action_dice_size_very_large;
//                break;
//        }
//
//        Float width = context.getResources().getDimension(diceSizeId);
//        diceDrawable.setBounds(0, 0, width.intValue(), width.intValue());
//
//        return new ImageSpan(diceDrawable);
//    }
//
//
//    // > Clicks
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * On a short click, open the value editor.
//     */
//    private void onActionWidgetShortClick(Context context)
//    {
//        if (this.modifierVariable().kind() == NumberVariable.Kind.SUMMATION)
//        {
//            Summation summation = this.modifierVariable().summation();
//
//            Intent intent = new Intent(context, DiceRollerActivity.class);
//            intent.putExtra("summation", summation);
//
//            if (!this.actionName.isNull())
//                intent.putExtra("roll_name", this.actionName());
//
//            if (!this.actionResult.isNull())
//                intent.putExtra("roll_description", this.actionResult());
//
//            intent.putExtra("action_widget_id", this.getId());
//
//            context.startActivity(intent);
//        }
//    }
//
//
//}
