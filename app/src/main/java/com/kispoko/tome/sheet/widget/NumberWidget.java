
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.rules.programming.variable.NumberVariable;
import com.kispoko.tome.rules.programming.variable.TextVariable;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetUI;
import com.kispoko.tome.util.Util;
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

    private UUID                              id;

    private ModelValue<WidgetData>            widgetData;
    private PrimitiveValue<WidgetFormat.Size> size;
    private ModelValue<NumberVariable>        value;
    private ModelValue<TextVariable>          prefix;
    private ModelValue<TextVariable>          postfix;


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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onValueUpdate(String valueName) { }


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

    public void runAction(String actionName, Context context, Rules rules) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Value
    // ------------------------------------------------------------------------------------------

    public NumberVariable getValue()
    {
        return this.value.getValue();
    }

    // ** Prefix
    // ------------------------------------------------------------------------------------------

    public TextVariable getPrefix() {
        return this.prefix.getValue();
    }


    // ** Postfix
    // ------------------------------------------------------------------------------------------

    public TextVariable getPostfix() {
        return this.prefix.getValue();
    }



    // > Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context, Rules rules)
    {
        LinearLayout integerLayout = WidgetUI.linearLayout(this, context, rules);

        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setLayoutParams(Util.linearLayoutParamsMatch());
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        contentLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add text view
        TextView textView = new TextView(context);

        textView.setTextSize(this.size.getValue().toSP(context));

        textView.setTypeface(Util.serifFontBold(context));
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        Integer integerValue = this.getValue().getInteger();
        if (integerValue != null)
            textView.setText(Integer.toString(integerValue));
        else
            textView.setText("");

        contentLayout.addView(textView);

        integerLayout.addView(contentLayout);

        return integerLayout;
    }


    public View getEditorView(Context context, Rules rules)
    {
        return new LinearLayout(context);
    }

}
