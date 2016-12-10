
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.engine.programming.variable.NumberVariable;
import com.kispoko.tome.engine.programming.variable.TextVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Widget: Number
 */
public class NumberWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                              id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<WidgetData>            widgetData;
    private PrimitiveValue<WidgetFormat.Size> size;
    private ModelValue<NumberVariable>        value;
    private ModelValue<TextVariable>          prefix;
    private ModelValue<TextVariable>          postfix;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                           valueViewId;
    private Integer                           prefixViewId;
    private Integer                           postfixViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberWidget()
    {
        this.id         = null;

        this.widgetData = ModelValue.empty(WidgetData.class);
        this.size       = new PrimitiveValue<>(null, WidgetFormat.Size.class);
        this.value      = ModelValue.empty(NumberVariable.class);
        this.prefix     = ModelValue.empty(TextVariable.class);
        this.postfix    = ModelValue.empty(TextVariable.class);
    }


    public NumberWidget(UUID id,
                        WidgetData widgetData,
                        WidgetFormat.Size size,
                        NumberVariable value,
                        TextVariable prefix,
                        TextVariable postfix)
    {
        this.id   = id;

        this.widgetData = ModelValue.full(widgetData, WidgetData.class);
        this.size       = new PrimitiveValue<>(size, WidgetFormat.Size.class);
        this.value      = ModelValue.full(value, NumberVariable.class);
        this.prefix     = ModelValue.full(prefix, TextVariable.class);
        this.postfix    = ModelValue.full(postfix, TextVariable.class);

        initialize();
    }


    public static NumberWidget fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID              id         = UUID.randomUUID();
        WidgetData        widgetData = WidgetData.fromYaml(yaml.atKey("data"));
        WidgetFormat.Size size       = WidgetFormat.Size.fromYaml(yaml.atKey("size"));
        NumberVariable    value      = NumberVariable.fromYaml(yaml.atKey("value"));
        TextVariable      prefix     = TextVariable.fromYaml(yaml.atMaybeKey("prefix"));
        TextVariable      postfix    = TextVariable.fromYaml(yaml.atMaybeKey("postfix"));

        return new NumberWidget(id, widgetData, size, value, prefix, postfix);
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
    public void onLoad()
    {
        initialize();
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the Widget.
     * @return The widget's name as a String.
     */
    public String name()
    {
        return "number";
    }

    // ** Data
    // ------------------------------------------------------------------------------------------

    /**
     * Get the widget's common data values.
     * @return The widget's WidgetData.
     */
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    // ** Run Action
    // ------------------------------------------------------------------------------------------

    public void runAction(String actionName, Context context, RulesEngine rulesEngine) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the number widget's value variable.
     * @return The number variable.
     */
    public NumberVariable valueVariable()
    {
        return this.value.getValue();
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
        } catch (SummationException exception) {
            ApplicationFailure.summation(exception);
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
            return Integer.toString(value);
        else
            return null;
    }


    // ** Prefix
    // ------------------------------------------------------------------------------------------

    /**
     * Get the number widget's prefix variable.
     * @return The text variable.
     */
    public TextVariable prefixVariable()
    {
        return this.prefix.getValue();
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
     * Get the postfix Text Variable. The postfix contains any text that comes after
     * the main value.
     * @return The postfix Text Variable.
     */
    public TextVariable postfixVariable()
    {
        return this.postfix.getValue();
    }


    /**
     * Get the number widget's postfix value (from its postfix variablbe).
     * @return The postfix (may be null).
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

    public View view()
    {
        // [1] Setup / Declarations
        // --------------------------------------------------------------------------------------

        Context context            = SheetManager.currentSheetContext();
        LinearLayout integerLayout = this.linearLayout();
        LinearLayout contentLayout = (LinearLayout) integerLayout.findViewById(
                                                                    R.id.widget_content_layout);

        this.valueViewId = Util.generateViewId();

        // [2] Number View
        // --------------------------------------------------------------------------------------

        TextViewBuilder numberView = new TextViewBuilder();

        numberView.id    = this.valueViewId;
        numberView.size  = this.size.getValue().resourceId();
        numberView.font  = Font.serifFontBold(context);
        numberView.color = R.color.light_grey_5;
        numberView.text  = this.valueString();

        contentLayout.addView(numberView.textView(context));

        // [3] Postfix View
        // --------------------------------------------------------------------------------------

        this.addPostfixView(contentLayout, context);


        return integerLayout;
    }


    private void addPostfixView(LinearLayout contentLayout, Context context)
    {
        // [0] Postfix variable may be null. Display nothing.
        // --------------------------------------------------------------------------------------

        if (this.postfix.isNull())
            return;

        // [1] Setup / Declarations
        // --------------------------------------------------------------------------------------

        this.postfixViewId = Util.generateViewId();

        // [2] Postfix View
        // --------------------------------------------------------------------------------------

        TextViewBuilder postfixView = new TextViewBuilder();

        postfixView.id    = this.postfixViewId;
        postfixView.size  = this.size.getValue().resourceId();
        postfixView.font  = Font.serifFontBold(context);
        postfixView.color = R.color.text_medium;
        postfixView.text  = this.postfix();

        contentLayout.addView(postfixView.textView(context));
    }


    public View getEditorView(Context context, RulesEngine rulesEngine)
    {
        return new LinearLayout(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    private void initialize()
    {
        // [1] Initialize variables with listeners to update the number widget views when the
        //     values of the variables change
        // --------------------------------------------------------------------------------------

        this.valueViewId   = null;
        this.prefixViewId  = null;
        this.postfixViewId = null;

        if (!this.value.isNull())
        {
            this.valueVariable().addOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onValueUpdate();
                }
            });
        }

        if (!this.prefix.isNull())
        {
            this.prefixVariable().addOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onPrefixUpdate();
                }
            });
        }


        if (!this.postfix.isNull())
        {
            this.postfixVariable().addOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onPostfixUpdate();
                }
            });
        }
    }


    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        if (this.valueViewId != null && !this.value.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

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
        if (this.prefixViewId != null && !this.prefix.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.prefixViewId);

            String prefixValue = this.prefix();

            if (prefixValue != null)
                textView.setText(prefixValue);
        }
    }


    /**
     * When the text widget's postfix is updated.
     */
    private void onPostfixUpdate()
    {
        if (this.postfixViewId != null && !this.postfix.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.postfixViewId);

            String postfixValue = this.postfix();

            if (postfixValue != null)
                textView.setText(postfixValue);
        }
    }


}
