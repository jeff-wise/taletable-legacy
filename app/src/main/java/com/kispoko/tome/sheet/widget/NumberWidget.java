
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Widget: Number
 */
public class NumberWidget extends Widget
                          implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<WidgetData>            widgetData;
    private PrimitiveFunctor<WidgetContentSize> size;
    private ModelFunctor<NumberVariable>        valueVariable;
    private PrimitiveFunctor<String>            valuePrefix;
    private PrimitiveFunctor<String>            valuePostfix;
    private ModelFunctor<TextVariable>          prefixVariable;
    private ModelFunctor<TextVariable>          postfixVariable;
    private CollectionFunctor<VariableUnion>    variables;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                             valueViewId;
    private Integer                             prefixViewId;
    private Integer                             postfixViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberWidget()
    {
        this.id                 = null;

        this.widgetData         = ModelFunctor.empty(WidgetData.class);
        this.size               = new PrimitiveFunctor<>(null, WidgetContentSize.class);
        this.valueVariable      = ModelFunctor.empty(NumberVariable.class);
        this.valuePrefix        = new PrimitiveFunctor<>(null, String.class);
        this.valuePostfix       = new PrimitiveFunctor<>(null, String.class);
        this.prefixVariable     = ModelFunctor.empty(TextVariable.class);
        this.postfixVariable    = ModelFunctor.empty(TextVariable.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.empty(variableClasses);

        this.valueViewId    = null;
        this.prefixViewId   = null;
        this.postfixViewId  = null;
    }


    public NumberWidget(UUID id,
                        WidgetData widgetData,
                        WidgetContentSize size,
                        NumberVariable valueVariable,
                        String valuePrefix,
                        String valuePostfix,
                        TextVariable prefixVariable,
                        TextVariable postfixVariable,
                        List<VariableUnion> variables)
    {
        this.id                 = id;

        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);
        this.size               = new PrimitiveFunctor<>(size, WidgetContentSize.class);
        this.valueVariable      = ModelFunctor.full(valueVariable, NumberVariable.class);
        this.valuePrefix        = new PrimitiveFunctor<>(valuePrefix, String.class);
        this.valuePostfix       = new PrimitiveFunctor<>(valuePostfix, String.class);
        this.prefixVariable     = ModelFunctor.full(prefixVariable, TextVariable.class);
        this.postfixVariable    = ModelFunctor.full(postfixVariable, TextVariable.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.full(variables, variableClasses);

        this.valueViewId    = null;
        this.prefixViewId   = null;
        this.postfixViewId  = null;
    }


    public static NumberWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id            = UUID.randomUUID();

        WidgetData        widgetData    = WidgetData.fromYaml(yaml.atKey("data"));
        WidgetContentSize size          = WidgetContentSize.fromYaml(yaml.atKey("size"));
        NumberVariable    value         = NumberVariable.fromYaml(yaml.atKey("value"));
        String            valuePrefix   = yaml.atMaybeKey("value_prefix").getString();
        String            valuePostfix  = yaml.atMaybeKey("value_postfix").getString();
        TextVariable      prefix        = TextVariable.fromYaml(yaml.atMaybeKey("prefix"));
        TextVariable      postfix       = TextVariable.fromYaml(yaml.atMaybeKey("postfix"));

        List<VariableUnion> variables   = yaml.atMaybeKey("variables").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new NumberWidget(id, widgetData, size, value, valuePrefix, valuePostfix,
                                prefix, postfix, variables);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Number Widget is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data())
                .putYaml("size", this.size())
                .putYaml("value", this.valueVariable())
                .putString("value_prefix", this.valuePrefix())
                .putString("value_postfix", this.valuePostfix())
                .putYaml("prefix", this.prefixVariable())
                .putYaml("postfix", this.postfixVariable())
                .putList("variables", this.variables());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    @Override
    public void initialize()
    {
        // [1] Initialize variables with listeners to update the number widget views when the
        //     values of the variables change
        // --------------------------------------------------------------------------------------

        // ** Value
        // --------------------------------------------------------------------------------------

        if (!this.valueVariable.isNull()) {
            this.valueVariable().initialize();
        }

        if (!this.valueVariable.isNull())
        {
            this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onValueUpdate();
                }
            });

            State.addVariable(this.valueVariable());
        }

        // ** Prefix
        // --------------------------------------------------------------------------------------

        if (!this.prefixVariable.isNull()) {
            this.prefixVariable().initialize();
        }

        if (!this.prefixVariable.isNull())
        {
            this.prefixVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onPrefixUpdate();
                }
            });

            State.addVariable(this.prefixVariable());
        }

        // ** Postfix
        // --------------------------------------------------------------------------------------

        if (!this.postfixVariable.isNull()) {
            this.postfixVariable().initialize();
        }

        if (!this.postfixVariable.isNull())
        {
            this.postfixVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onPostfixUpdate();
                }
            });

            State.addVariable(this.postfixVariable());
        }

        // [2] Initialize the helper variables
        // --------------------------------------------------------------------------------------

        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }

    }


    /**
     * Get the widget's common data values.
     * @return The widget's WidgetData.
     */
    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    // ** Run Action
    // ------------------------------------------------------------------------------------------

    @Override
    public void runAction(Action action)
    {
        switch (action)
        {
            case EDIT:
                editAction();
                break;
        }
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The number widget's content size.
     * @return The WidgetContentSize.
     */
    public WidgetContentSize size()
    {
        return this.size.getValue();
    }


    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the number widget's value variable.
     * @return The number variable.
     */
    public NumberVariable valueVariable()
    {
        return this.valueVariable.getValue();
    }


    /**
     * Get the number widget's value (from its value variable).
     * @return The integer value.
     */
    public Integer value()
    {
        Integer integerValue = null;

        try {
            integerValue = this.valueVariable().value();
        }
        catch (VariableException exception) {
            ApplicationFailure.variable(exception);
        }

        return integerValue;
    }


    /**
     * Get the number widget's value as a string.
     * @return The value string.
     */
    public String valueString()
    {
        Integer value = this.value();
        if (value != null)
        {
            StringBuilder valueString = new StringBuilder();

            if (!this.valuePrefix.isNull())
                valueString.append(this.valuePrefix());

            valueString.append(Integer.toString(value));

            if (!this.valuePostfix.isNull())
                valueString.append(this.valuePostfix());

            return valueString.toString();
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the value prefix (may be null).
     * @return The value prefix string.
     */
    public String valuePrefix()
    {
        return this.valuePrefix.getValue();
    }


    /**
     * Get the value postfix (may be null).
     * @return The value postfix string.
     */
    public String valuePostfix()
    {
        return this.valuePostfix.getValue();
    }


    // ** Variables
    // ------------------------------------------------------------------------------------------

    /**
     * Get the text widget's helper variables.
     * @return The list of variables.
     */
    public List<VariableUnion> variables()
    {
        return this.variables.getValue();
    }


    // ** Prefix
    // ------------------------------------------------------------------------------------------

    /**
     * Get the number widget's prefix variable.
     * @return The text variable.
     */
    public TextVariable prefixVariable()
    {
        return this.prefixVariable.getValue();
    }


    /**
     * Get the number widget's prefix value (from its prefix variable).
     * @return The prefix (may be null).
     */
    public String prefix()
    {
        if (this.prefixVariable() != null)
            return this.prefixVariable().value();
        else
            return null;
    }


    // ** Postfix
    // ------------------------------------------------------------------------------------------

    /**
     * Get the postfixVariable Text Variable. The postfixVariable contains any text that comes after
     * the main value.
     * @return The postfixVariable Text Variable.
     */
    public TextVariable postfixVariable()
    {
        return this.postfixVariable.getValue();
    }


    /**
     * Get the number widget's postfixVariable value (from its postfixVariable variablbe).
     * @return The postfixVariable (may be null).
     */
    public String postfix()
    {
        if (this.postfixVariable() != null)
            return this.postfixVariable().value();
        else
            return null;
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View tileView()
    {
        // [1] Setup / Declarations
        // --------------------------------------------------------------------------------------

        Context context            = SheetManager.currentSheetContext();
        LinearLayout integerLayout = this.widgetLayout(true);
        LinearLayout contentLayout = (LinearLayout) integerLayout.findViewById(
                                                                    R.id.widget_content_layout);

        // [2] Value View
        // --------------------------------------------------------------------------------------

        LinearLayout valueView = this.valueView(context);

        contentLayout.addView(valueView);

        return integerLayout;
    }


    public View editorView(Context context)
    {
        return new LinearLayout(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------


    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        if (this.valueViewId != null && !this.valueVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            if (textView == null)
                return;

            Integer value = this.value();

            // TODO can value be null
            if (value != null)
                textView.setText(this.valueString());
        }
    }


    /**
     * When the text widget's prefix is updated.
     */
    private void onPrefixUpdate()
    {
        if (this.prefixViewId != null && !this.prefixVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.prefixViewId);

            String prefixValue = this.prefix();

            if (prefixValue != null)
                textView.setText(prefixValue);
        }
    }


    /**
     * When the text widget's postfixVariable is updated.
     */
    private void onPostfixUpdate()
    {
        if (this.postfixViewId != null && !this.postfixVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.postfixViewId);

            String postfixValue = this.postfix();

            if (postfixValue != null)
                textView.setText(postfixValue);
        }
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout valueView(Context context)
    {
        // [1] Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
        TextViewBuilder     valueView   = new TextViewBuilder();
        TextViewBuilder     postfixView = new TextViewBuilder();

        this.valueViewId   = Util.generateViewId();
        this.postfixViewId = Util.generateViewId();

        // [2 A] Layout
        // --------------------------------------------------------------------------------------

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.child(valueView);

        if (!this.postfixVariable.isNull())
            layout.child(postfixView);

        // [2 B] Value
        // --------------------------------------------------------------------------------------

        valueView.id            = this.valueViewId;
        valueView.size          = this.size.getValue().resourceId();
        valueView.font          = Font.serifFontBold(context);
        valueView.color         = R.color.dark_blue_hlx_6;
        //valueView.color         = R.color.light_grey_6;
        valueView.text          = this.valueString();
        valueView.margin.right  = R.dimen.widget_number_value_margin_right;

        // [2 C] Postfix
        // --------------------------------------------------------------------------------------

        postfixView.id    = this.postfixViewId;
        postfixView.size  = this.size.getValue().resourceId();
        postfixView.font  = Font.serifFontBold(context);
        postfixView.color = R.color.dark_blue_hl_5;
        postfixView.text  = this.postfix();


        return layout.linearLayout(context);
    }


    // > Actions
    // ------------------------------------------------------------------------------------------

    /**
     * Respond to the edit action. Opens another activity depending on the type of number widget
     * value.
     */
    private void editAction()
    {
        if (this.valueVariable.isNull())
            return;

        this.valueVariable().openEditActivity(this.data().format().label());
    }

}
