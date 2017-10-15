
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.TextStyle
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Boolean Widget Format
 */
data class BooleanWidgetFormat(override val id : UUID,
                               val widgetFormat : Comp<WidgetFormat>,
                               val textStyle : Comp<TextStyle>,
                               val trueText : Prim<TrueText>,
                               val falseText : Prim<FalseText>)
                                : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                textStyle : TextStyle,
                trueText : TrueText,
                falseText : FalseText)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(textStyle),
               Prim(trueText),
               Prim(falseText))


    companion object : Factory<BooleanWidgetFormat>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::BooleanWidgetFormat,
                         // Widget Format
                         doc.at("widget_format") ap { WidgetFormat.fromDocument(it) },
                         // Text Style
                         doc.at("text_style") ap { TextStyle.fromDocument(it) },
                         // True Text
                         doc.at("true_text") ap { TrueText.fromDocument(it) },
                         // False Text
                         doc.at("false_text") ap { FalseText.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "text_style" to this.textStyle().toDocument(),
        "true_text" to this.trueText.value.toDocument(),
        "false_text" to this.falseText.value.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun textStyle() : TextStyle = this.textStyle.value



    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "boolean_widget_format"

    override val modelObject = this

}


/**
 * True Text
 */
data class TrueText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TrueText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TrueText> = when (doc)
        {
            is DocText -> effValue(TrueText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * False Text
 */
data class FalseText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FalseText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<FalseText> = when (doc)
        {
            is DocText -> effValue(FalseText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


//
///**
// * Boolean WidgetData
// */
//public class BooleanWidget extends Widget
//                           implements ToYaml, Serializable
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
//    private ModelFunctor<BooleanWidgetFormat>   format;
//    private ModelFunctor<BooleanVariable>       valueVariable;
//
//    private PrimitiveFunctor<String>            onText;
//    private PrimitiveFunctor<String>            offText;
//
//
//    // > Internal
//    // ------------------------------------------------------------------------------------------
//
//    private Integer                             valueViewId;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public BooleanWidget()
//    {
//        this.id             = null;
//
//        this.widgetData     = ModelFunctor.empty(WidgetData.class);
//        this.format         = ModelFunctor.empty(BooleanWidgetFormat.class);
//        this.valueVariable  = ModelFunctor.empty(BooleanVariable.class);
//
//        this.onText         = new PrimitiveFunctor<>(null, String.class);
//        this.offText        = new PrimitiveFunctor<>(null, String.class);
//
//        this.valueViewId    = null;
//    }
//
//
//    public BooleanWidget(UUID id,
//                         WidgetData widgetData,
//                         BooleanWidgetFormat format,
//                         BooleanVariable valueVariable,
//                         String onText,
//                         String offText)
//    {
//        this.id             = id;
//
//        this.widgetData     = ModelFunctor.full(widgetData, WidgetData.class);
//        this.format         = ModelFunctor.full(format, BooleanWidgetFormat.class);
//        this.valueVariable  = ModelFunctor.full(valueVariable, BooleanVariable.class);
//
//        this.onText         = new PrimitiveFunctor<>(onText, String.class);
//        this.offText        = new PrimitiveFunctor<>(offText, String.class);
//
//        this.valueViewId    = null;
//
//        this.initializeBooleanWidget();
//    }
//
//
//    public static BooleanWidget fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        UUID                id         = UUID.randomUUID();
//
//        WidgetData          widgetData = WidgetData.fromYaml(yaml.atKey("data"));
//        BooleanWidgetFormat format     = BooleanWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
//        BooleanVariable     value      = BooleanVariable.fromYaml(yaml.atKey("value"));
//
//        String            onText     = yaml.atMaybeKey("on_text").getString();
//        String            offText    = yaml.atMaybeKey("off_text").getString();
//
//        return new BooleanWidget(id, widgetData, format, value, onText, offText);
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
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
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
//     * This method is called when the Boolean Widget is completely loaded for the first time.
//     */
//    public void onLoad()
//    {
//        this.initializeBooleanWidget();
//    }
//
//
//    // > Widget
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the text widget state.
//     */
//    @Override
//    public void initialize(GroupParent groupParent, Context context)
//    {
//        // > If the variable is non-null
//        if (!this.valueVariable.isNull()) {
//            this.valueVariable().initialize();
//        }
//
//        // > If the variable has a non-null value
//        if (!this.valueVariable.isNull())
//        {
//            this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
//                @Override
//                public void onUpdate() {
//                    onValueUpdate();
//                }
//            });
//
//
//            State.addVariable(this.valueVariable());
//        }
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
//    @Override
//    public View view(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = viewLayout(rowHasLabel, context);
//
//        // > Label View
////        if (this.data().format().label() != null) {
////            layout.addView(this.labelView(context));
////        }
//
//        // > Value
//        final TextView valueView = valueView(context);
//
//        valueView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (value()) {
//                    setValue(false);
//                    valueView.setText(offText());
//                }
//                else {
//                    setValue(true);
//                    valueView.setText(onText());
//                }
//            }
//        });
//
//        layout.addView(valueView);
//
//        return layout;
//    }
//
//
//    // > To Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Boolean Widget's yaml representation.
//     * @return The Yaml Builder.
//     */
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putYaml("data", this.data())
//                .putYaml("value", this.valueVariable());
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the BooleanWidget's value variable (of type boolean).
//     * @return The Variable for the BoolenWidget value.
//     */
//    public BooleanVariable valueVariable()
//    {
//        return this.valueVariable.getValue();
//    }
//
//
//    public Boolean value()
//    {
//        if (!this.valueVariable.isNull())
//            return this.valueVariable().value();
//        return null;
//    }
//
//
//    public void setValue(Boolean value)
//    {
//        this.valueVariable().setValue(value);
//    }
//
//
//    /**
//     * The text displayed when the widget's value is true.
//     * @return The on text.
//     */
//    public String onText()
//    {
//        return this.onText.getValue();
//    }
//
//
//    /**
//     * The text displayed when the widget's value is false.
//     * @return The off text.
//     */
//    public String offText()
//    {
//        return this.offText.getValue();
//    }
//
//
//    /**
//     * The Boolean Widget format settings.
//     * @return The boolean widget format.
//     */
//    public BooleanWidgetFormat format()
//    {
//        return this.format.getValue();
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeBooleanWidget()
//    {
//        // [1] Apply default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Content Alignment
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        // ** Label Style
//        if (this.data().format().labelStyle() == null) {
//            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
//                                                        TextColor.THEME_DARK,
//                                                        TextSize.SMALL,
//                                                        Alignment.CENTER);
//            this.data().format().setLabelStyle(defaultLabelStyle);
//        }
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.DARK);
//
//        // ** Corners
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.SMALL);
//    }
//
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private LinearLayout viewLayout(boolean rowHasLabel, Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.width                = 0;
//        layout.weight               = this.data().format().width().floatValue();
//        layout.gravity              = Gravity.CENTER;
//
//        layout.margin.left          = R.dimen.widget_margin_horz;
//        layout.margin.right         = R.dimen.widget_margin_horz;
//
//        if (this.data().format().label() == null && rowHasLabel) {
//            layout.padding.top      = R.dimen.widget_label_fill_padding;
//        }
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private TextView valueView(Context context)
//    {
//        TextViewBuilder value = new TextViewBuilder();
//
//        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        value.font                  = Font.serifFontRegular(context);
//        value.size                  = this.format().size().resourceId();
//
//        value.backgroundResource    = this.data().format().background()
//                                          .resourceId(this.data().format().corners());
//
//        value.gravity               = Gravity.CENTER;
//        value.layoutGravity         = Gravity.CENTER;
//
//        if (this.value())
//        {
//            value.text  = this.onText();
//            value.color =  R.color.dark_blue_hl_1;
//        }
//        else
//        {
//            value.text  = this.offText();
//            value.color =  R.color.dark_blue_hl_6;
//        }
//
//        return value.textView(context);
//    }
//
//
//    private TextView labelView(Context context)
//    {
//        TextViewBuilder label = new TextViewBuilder();
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.text              = this.data().format().label();
//        label.font              = Font.serifFontRegular(context);
//        label.color             = R.color.dark_blue_1;
//        label.size              = R.dimen.widget_label_text_size;
//
//        label.margin.bottom     = R.dimen.widget_label_margin_bottom;
//
//        return label.textView(context);
//    }
//
//
//    // > Value Updates
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * When the text widget's value is updated.
//     */
//    private void onValueUpdate()
//    {
//        if (this.valueViewId != null)
//        {
//            Activity activity = (Activity) SheetManagerOld.currentSheetContext();
//            TextView textView = (TextView) activity.findViewById(this.valueViewId);
//
//            Boolean value = this.value();
//
//            // TODO can value be null
//            if (value != null)
//                textView.setText(Boolean.toString(value));
//        }
//    }
//
//
//}
