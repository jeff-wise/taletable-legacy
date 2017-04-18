
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.BaseValueSet;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.ValueReference;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.engine.value.ValueSetUnion;
import com.kispoko.tome.engine.value.ValueType;
import com.kispoko.tome.engine.value.ValueUnion;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.option.OptionWidgetFormat;
import com.kispoko.tome.sheet.widget.option.ViewType;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.widget.util.Height;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Option Widget
 */
public class OptionWidget extends Widget implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            description;

    /**
     * The view type determines which interface is used to display the value.
     */
    private PrimitiveFunctor<ViewType>          viewType;

    private ModelFunctor<OptionWidgetFormat>    format;
    private ModelFunctor<WidgetData>            widgetData;

    private PrimitiveFunctor<ValueType>         valueType;
    private ModelFunctor<TextVariable>          textVariable;
    private ModelFunctor<NumberVariable>        numberVariable;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private Map<String,Integer>                 valueStringListIndexMap;

    private ValueSet                            valueSet;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public OptionWidget()
    {
        this.id                 = null;

        this.description        = new PrimitiveFunctor<>(null, String.class);
        this.viewType           = new PrimitiveFunctor<>(null, ViewType.class);
        this.format             = ModelFunctor.empty(OptionWidgetFormat.class);
        this.widgetData         = ModelFunctor.empty(WidgetData.class);

        this.valueType          = new PrimitiveFunctor<>(null, ValueType.class);
        this.textVariable       = ModelFunctor.empty(TextVariable.class);
        this.numberVariable     = ModelFunctor.empty(NumberVariable.class);
    }


    public OptionWidget(UUID id,
                        String description,
                        ViewType viewType,
                        OptionWidgetFormat format,
                        WidgetData widgetData,
                        ValueType valueType,
                        Variable variable)
    {
        this.id                 = id;

        this.description        = new PrimitiveFunctor<>(description, String.class);
        this.viewType           = new PrimitiveFunctor<>(viewType, ViewType.class);
        this.format             = ModelFunctor.full(format, OptionWidgetFormat.class);
        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);

        this.valueType          = new PrimitiveFunctor<>(valueType, ValueType.class);
        this.textVariable       = ModelFunctor.full(null, TextVariable.class);
        this.numberVariable     = ModelFunctor.full(null, NumberVariable.class);

        // > Set variable
        switch (valueType)
        {
            case TEXT:
                this.textVariable.setValue((TextVariable) variable);
                break;
            case NUMBER:
                this.numberVariable.setValue((NumberVariable) variable);
                break;
        }

        // > Set nulls to default values
        this.setViewType(viewType);

        this.initializeOptionWidget();
    }


    /**
     * Create an Option Widget from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Option Widget.
     * @throws YamlParseException
     */
    public static OptionWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID               id          = UUID.randomUUID();

        String             description = yaml.atMaybeKey("description").getTrimmedString();
        ViewType           viewType    = ViewType.fromYaml(yaml.atMaybeKey("view_type"));
        OptionWidgetFormat format      = OptionWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        WidgetData         widgetData  = WidgetData.fromYaml(yaml.atMaybeKey("data"));

        ValueType          valueType   = ValueType.fromYaml(yaml.atKey("value_type"));

        Variable variable = null;
        switch (valueType)
        {
            case TEXT:
                variable               = TextVariable.fromYaml(yaml.atKey("value"));
                break;
            case NUMBER:
                variable               = NumberVariable.fromYaml(yaml.atKey("value"));
                break;
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ValueType.class.getName())));
        }

        return new OptionWidget(id, description, viewType, format, widgetData, valueType, variable);
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
        this.initializeOptionWidget();
    }


    // > Yaml
    // -----------------------------------------------------------------------------------------

    /**
     * The Mechanic Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder yaml =  YamlBuilder.map();

        yaml.putString("description", this.description());
        yaml.putYaml("view_type", this.viewType());
        yaml.putYaml("format", this.format());
        yaml.putYaml("data", this.data());
        yaml.putYaml("value_type", this.valueType());

        switch (this.valueType())
        {
            case TEXT:
                yaml.putYaml("value", this.textVariable());
                break;
            case NUMBER:
                yaml.putYaml("value", this.numberVariable());
                break;
        }

        return yaml;
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

    // ** Description
    // -----------------------------------------------------------------------------------------

    /**
     * The option widget's description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    // ** View Type
    // -----------------------------------------------------------------------------------------

    /**
     * The option widget view type. Determines which interface is used to display the value.
     * @return The view type.
     */
    public ViewType viewType()
    {
        return this.viewType.getValue();
    }


    /**
     * Set the view type. If null, defaults to a plain box with no arrows.
     * @param viewType The view type.
     */
    public void setViewType(ViewType viewType)
    {
        if (viewType != null)
            this.viewType.setValue(viewType);
        else
            this.viewType.setValue(ViewType.NO_ARROWS);
    }


    // ** Format
    // -----------------------------------------------------------------------------------------

    /**
     * The option widget's format options.
     * @return The format.
     */
    public OptionWidgetFormat format()
    {
        return this.format.getValue();
    }


    // ** Value Type
    // -----------------------------------------------------------------------------------------

    /**
     * The type of value (number/text) that the option widget holds.
     * @return The value type.
     */
    public ValueType valueType()
    {
        return this.valueType.getValue();
    }


    // ** Text Variable
    // -----------------------------------------------------------------------------------------

    /**
     * The text variable that holds the widget value.
     * @return The text variable.
     */
    public TextVariable textVariable()
    {
        if (this.valueType() != ValueType.TEXT) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("text", this.valueType().toString())));
        }
        return this.textVariable.getValue();
    }


    // ** Number Variable
    // -----------------------------------------------------------------------------------------

    /**
     * The number variable that holds the widget value.
     * @return The number variable.
     */
    public NumberVariable numberVariable()
    {
        if (this.valueType() != ValueType.NUMBER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("number", this.valueType().toString())));
        }
        return this.numberVariable.getValue();
    }


    // > Value String
    // -----------------------------------------------------------------------------------------

    private String valueString()
    {
        switch (this.valueType())
        {
            case TEXT:
                try {
                    return this.textVariable().value();
                }
                catch (NullVariableException exception) {
                    ApplicationFailure.nullVariable(exception);
                }
            case NUMBER:
                try {
                    return this.numberVariable().valueString();
                }
                catch (NullVariableException exception) {
                    ApplicationFailure.nullVariable(exception);
                }
            default:
                return "N/A";
        }
    }


    // > Value Reference
    // -----------------------------------------------------------------------------------------

    /**
     * The value reference of the option widget.
     * @return The value reference.
     */
    private ValueReference valueReference()
    {
        switch (this.valueType())
        {
            case TEXT:
                return this.textVariable().valueReference();
            case NUMBER:
                return this.numberVariable().valueReference();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ValueType.class.getName())));
                return null;
        }
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Value Set
    // -----------------------------------------------------------------------------------------

    /**
     * Get the value set that the option widget represents.
     * @return The Value Set.
     */
    public ValueSet valueSet()
    {
        if (this.valueSet != null)
            return this.valueSet;

        Dictionary dictionary = SheetManager.dictionary();

        if (this.valueReference() != null && dictionary != null)
        {
            ValueSetUnion valueSetUnion = dictionary.lookup(this.valueReference().valueSetName());
            if (valueSetUnion != null)
            {
                this.valueSet = valueSetUnion.valueSet();
                return this.valueSet;
            }
        }

        return null;
    }


    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeOptionWidget()
    {
        // [1] Configure default format values
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
    }


    // > Values
    // -----------------------------------------------------------------------------------------

    // TODO add null value exception
    private List<String> valueStrings()
    {
        List<String> valueStrings = new ArrayList<>();

        ValueSet valueSet = this.valueSet();

        if (valueSet != null)
        {
            for (ValueUnion valueUnion : valueSet().values()) {
                valueStrings.add(valueUnion.value().valueString());
            }
        }

        return valueStrings;
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = this.layout(rowHasLabel, context);

        switch (this.viewType())
        {
            case ARROWS_VERTICAL:
                layout.addView(verticalArrowsView(context));
                break;
            case EXPANDED_SLASHES:
                layout.addView(expandedSlashesView(context));
                break;
        }

        return layout;
    }


    private LinearLayout verticalArrowsView(Context context)
    {
        LinearLayout layout = verticalArrowsViewLayout(context);

        // > Description
        layout.addView(verticalArrowsDescriptionView(context));

        // > Value
        layout.addView(verticalArrowsValueView(context));

        return layout;
    }


    private LinearLayout verticalArrowsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = this.data().format().alignment().gravityConstant()
                                        | Gravity.CENTER_VERTICAL;

        if (this.data().format().background() != BackgroundColor.EMPTY) {
            layout.backgroundResource   = this.data().format().corners().resourceId();
            layout.backgroundColor      = this.data().format().background().colorId();
        }

        return layout.linearLayout(context);
    }


    private TextView verticalArrowsDescriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.text            = this.description();
        description.color           = this.format().descriptionStyle().color().resourceId();
        description.size            = this.format().descriptionStyle().size().resourceId();
        description.font            = this.format().descriptionStyle().typeface(context);

        return description.textView(context);
    }


    private LinearLayout verticalArrowsValueView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
        ImageViewBuilder    chevronUp   = new ImageViewBuilder();
        ImageViewBuilder    chevronDown = new ImageViewBuilder();
        TextViewBuilder     value       = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.VERTICAL;
        layout.width                    = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity                  = Gravity.CENTER_HORIZONTAL;

        layout.margin.top               = R.dimen.half_dp;

        layout.child(chevronUp)
              .child(value)
              .child(chevronDown);

        // [3 A] Chevron Up
        // -------------------------------------------------------------------------------------

        chevronUp.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        chevronUp.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        chevronUp.image         = R.drawable.ic_option_chevron_up;

        chevronUp.margin.bottom = R.dimen.negative_four_dp;

        // [3 A] Chevron Down
        // -------------------------------------------------------------------------------------

        chevronDown.width       = LinearLayout.LayoutParams.WRAP_CONTENT;
        chevronDown.height      = LinearLayout.LayoutParams.WRAP_CONTENT;

        chevronDown.image       = R.drawable.ic_option_chevron_down;

        chevronDown.margin.top    = R.dimen.negative_four_dp;

        // [4] Value
        // -------------------------------------------------------------------------------------

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = this.valueString();
        value.font                  = this.format().valueStyle().typeface(context);
        value.color                 = this.format().valueStyle().color().resourceId();
        value.size                  = this.format().valueStyle().size().resourceId();

        value.gravity               = Gravity.CENTER_HORIZONTAL;

        value.backgroundColor       = this.format().valueStyle().backgroundColor().colorId();
        value.backgroundResource    = R.drawable.bg_option_value;

        value.margin.left           = R.dimen.six_dp;

        // > Set value width by longest value string
        value.maxEms                = this.valueSet().lengthOfLongestValueString() - 2;
        value.minEms                = this.valueSet().lengthOfLongestValueString() - 2;


        return layout.linearLayout(context);
    }


    private TextView expandedSlashesView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.gravity       = this.data().format().alignment().gravityConstant()
                                | Gravity.CENTER_VERTICAL;

        value.layoutGravity = this.data().format().alignment().gravityConstant()
                                | Gravity.CENTER_VERTICAL;


        value.textSpan      = expandedSlashesSpannable(context);

        this.format().descriptionStyle().styleTextViewBuilder(value, context);

        if (this.data().format().background() != BackgroundColor.EMPTY) {
            value.backgroundResource   = this.data().format().corners().resourceId();
            value.backgroundColor      = this.data().format().background().colorId();
        }

        if (this.format().height() == Height.WRAP) {
            value.padding.topDp    = this.format().verticalPadding().floatValue();
            value.padding.bottomDp = this.format().verticalPadding().floatValue();
        }

        return value.textView(context);
    }


    private SpannableStringBuilder expandedSlashesSpannable(Context context)
    {
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(this.description());

        StringBuilder stringBuilder = new StringBuilder(this.description());

        String placeholderString = context.getString(R.string.placeholder_value);
        int placeholderIndex = this.description().indexOf(placeholderString);
        int placeholderLength = placeholderString.length();

        // IF the placeholder EXISTS in the text...
        if (placeholderIndex >= 0)
        {
            String valueListString = valueListBySlashes();

            // (1 A) Remove the placeholder string
            spanBuilder.delete(placeholderIndex, placeholderIndex + placeholderLength);
            stringBuilder.replace(placeholderIndex, placeholderIndex + placeholderLength, "");

            // (1 B) Insert the text
            spanBuilder.insert(placeholderIndex, valueListString);
            stringBuilder.insert(placeholderIndex, valueListString);

            // > Format value list item strings
            for (String valueItemString : this.valueStrings())
            {
                int valueItemIndex = placeholderIndex +
                                     this.valueStringListIndexMap.get(valueItemString);

                if (valueItemIndex >= 0) {
                    FormattedString.formatSpan(spanBuilder,
                                               valueItemIndex,
                                               valueItemString.length(),
                                               this.format().valueItemStyle(),
                                               context);
                }

            }

            // > Format value
            String valueString = this.valueString();
            int valueIndex = placeholderIndex + this.valueStringListIndexMap.get(valueString);

            if (valueIndex >= 0)
            {
                FormattedString.formatSpan(spanBuilder,
                                           valueIndex,
                                           valueIndex + valueString.length() - 4,
                                           this.format().valueStyle(),
                                           context);
            }

        }

        return spanBuilder;
    }


    private String valueListBySlashes()
    {
        StringBuilder valuesSB = new StringBuilder();

        if (this.valueSet() == null)
            return "";

        Collection<ValueUnion> values = valueSet().values();

        this.valueStringListIndexMap = new HashMap<>();

        int currentIndex = 0;
        String sep = "";
        for (ValueUnion valueUnion : values)
        {
            String valueString = valueUnion.value().valueString();

            valuesSB.append(sep);

            valuesSB.append(valueString);

            this.valueStringListIndexMap.put(valueString, currentIndex);

            sep = " / ";

            currentIndex += valueString.length();
            currentIndex += 3;
        }

        return valuesSB.toString().trim();
    }




}
