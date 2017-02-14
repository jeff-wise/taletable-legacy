
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.widget.adder.AdderWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.UUID;



/**
 * Adder Widget
 */
public class AdderWidget extends Widget
                         implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<Type>          type;
    private ModelFunctor<NumberVariable>    valueVariable;
    private ModelFunctor<AdderWidgetFormat> format;
    private ModelFunctor<WidgetData>        widgetData;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public AdderWidget()
    {
        this.id             = null;

        this.type           = new PrimitiveFunctor<>(null, Type.class);
        this.valueVariable  = ModelFunctor.empty(NumberVariable.class);
        this.format         = ModelFunctor.empty(AdderWidgetFormat.class);
        this.widgetData     = ModelFunctor.empty(WidgetData.class);
    }


    public AdderWidget(UUID id,
                       Type type,
                       NumberVariable valueVariable,
                       AdderWidgetFormat format,
                       WidgetData widgetData)
    {
        this.id             = id;

        this.type           = new PrimitiveFunctor<>(type, Type.class);
        this.valueVariable  = ModelFunctor.full(valueVariable, NumberVariable.class);
        this.format         = ModelFunctor.full(format, AdderWidgetFormat.class);
        this.widgetData     = ModelFunctor.full(widgetData, WidgetData.class);
    }


    /**
     * Create an Adder Widget from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Adder Widget.
     * @throws YamlParseException
     */
    public static AdderWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id      = UUID.randomUUID();

        Type              type    = Type.fromYaml(yaml.atKey("type"));
        NumberVariable    value   = NumberVariable.fromYaml(yaml.atKey("value"));
        AdderWidgetFormat format  = AdderWidgetFormat.fromYaml(yaml.atMaybeKey("format"), type);
        WidgetData        data    = WidgetData.fromYaml(yaml.atMaybeKey("data"));

        return new AdderWidget(id, type, value, format, data);
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
     * This method is called when the Text Widget is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("type", this.type())
                .putYaml("value", this.valueVariable())
                .putYaml("format", this.format())
                .putYaml("data", this.data());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize() { }


    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    @Override
    public View view(boolean rowHasLabel, Context context)
    {
        return this.widgetView(context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * The Adder Widget type.
     * @return The type.
     */
    public Type type()
    {
        return this.type.getValue();
    }


    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * The value number variable.
     * @return The number variable.
     */
    public NumberVariable valueVariable()
    {
        return this.valueVariable.getValue();
    }


    public String valueString()
    {
        try {
            return this.valueVariable().valueString();
        }
        catch (NullVariableException exception) {
            ApplicationFailure.nullVariable(exception);
        }

        return "";
    }


    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The Adder Widget Format.
     * @return The format.
     */
    public AdderWidgetFormat format()
    {
        return this.format.getValue();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > View
    // ------------------------------------------------------------------------------------------

    public View widgetView(Context context)
    {
        LinearLayout layout = widgetViewLayout(context);

        layout.addView(valueView(context));

        layout.addView(resetButtonView(context));

        return layout;
    }


    private LinearLayout widgetViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        return layout.linearLayout(context);
    }


    private LinearLayout valueView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout    = new LinearLayoutBuilder();
        TextViewBuilder     subButton = new TextViewBuilder();
        TextViewBuilder     addButton = new TextViewBuilder();
        TextViewBuilder     value     = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER;

        layout.child(subButton)
              .child(value)
              .child(addButton);

        // [3 A] Sub Button
        // -------------------------------------------------------------------------------------

        subButton.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        subButton.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        subButton.text              = this.format().subtractLabel();
        subButton.font              = Font.serifFontRegular(context);
        subButton.color             = R.color.dark_blue_hl_1;
        subButton.size              = R.dimen.widget_adder_mod_button_text_size;

        //subButton.backgroundResource = R.drawable.bg_widget_adder;

        subButton.margin.right      = R.dimen.widget_adder_mod_button_margin;

        // [3 B] Add Button
        // -------------------------------------------------------------------------------------

        addButton.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        addButton.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        addButton.text              = this.format().addLabel();
        addButton.font              = Font.serifFontRegular(context);
        addButton.color             = R.color.dark_blue_hl_1;
        addButton.size              = R.dimen.widget_adder_mod_button_text_size;

        //addButton.backgroundResource = R.drawable.bg_widget_adder;

        addButton.margin.left       = R.dimen.widget_adder_mod_button_margin;

        // [4] Value
        // -------------------------------------------------------------------------------------

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = this.valueString();
        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_blue_hlx_8;
        value.size                  = R.dimen.widget_adder_value_text_size;


        return layout.linearLayout(context);
    }


    private TextView resetButtonView(Context context)
    {
        TextViewBuilder reset = new TextViewBuilder();

        reset.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        reset.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        reset.layoutGravity     = Gravity.CENTER_HORIZONTAL;

        reset.text              = this.format().resetLabel();
        reset.font              = Font.serifFontRegular(context);
        reset.color             = R.color.dark_blue_hl_5;
        reset.size              = R.dimen.widget_adder_reset_button_text_size;

        reset.margin.top        = R.dimen.widget_adder_footer_margin_top;

        return reset.textView(context);
    }


    // TYPE
    // -----------------------------------------------------------------------------------------

    public enum Type implements ToYaml
    {

        // VALUES
        // -------------------------------------------------------------------------------------

        BY_ONE,
        BY_MANY;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public static Type fromString(String typeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Type.class, typeString);
        }


        public static Type fromYaml(YamlParser yaml)
                      throws YamlParseException
        {
            String typeString = yaml.getString();
            try {
                return Type.fromString(typeString);
            } catch (InvalidDataException e) {
                throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
            }
        }


        public static Type fromSQLValue(SQLValue sqlValue)
                      throws DatabaseException
        {
            String enumString = "";
            try {
                enumString = sqlValue.getText();
                Type type = Type.fromString(enumString);
                return type;
            } catch (InvalidDataException e) {
                throw DatabaseException.invalidEnum(
                        new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
            }
        }


        // TO YAML
        // -------------------------------------------------------------------------------------

        /**
         * The Widget Content Alignment's yaml string representation.
         * @return The Yaml Builder.
         */
        public YamlBuilder toYaml()
        {
            return YamlBuilder.string(this.name().toLowerCase());
        }

    }


}
