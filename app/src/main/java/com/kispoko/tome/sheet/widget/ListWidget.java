
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.Value;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.list.ListWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.functor.CollectionFunctor;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * List Widget
 */
public class ListWidget extends Widget
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
    private ModelFunctor<ListWidgetFormat>      format;
    private CollectionFunctor<VariableUnion>    values;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ListWidget()
    {
        this.id         = null;

        this.widgetData = ModelFunctor.empty(WidgetData.class);
        this.format     = ModelFunctor.empty(ListWidgetFormat.class);
        this.values     = CollectionFunctor.empty(VariableUnion.class);
    }


    public ListWidget(UUID id,
                      WidgetData widgetData,
                      ListWidgetFormat format,
                      List<VariableUnion> values)
    {
        this.id         = id;

        this.widgetData = ModelFunctor.full(widgetData, WidgetData.class);
        this.format     = ModelFunctor.full(format, ListWidgetFormat.class);
        this.values     = CollectionFunctor.full(values, VariableUnion.class);

        this.initializeListWidget();
    }


    /**
     * Create a list widget from its yaml representation.
     * @param yaml The yaml parser.
     * @return The List Widget
     * @throws YamlParseException
     */
    public static ListWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                id         = UUID.randomUUID();

        WidgetData          widgetData = WidgetData.fromYaml(yaml.atMaybeKey("data"));
        ListWidgetFormat    format     = ListWidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        List<VariableUnion> values     = yaml.atMaybeKey("values").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new ListWidget(id, widgetData, format, values);
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
        this.initializeListWidget();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data())
                .putList("values", this.values());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text widget state.
     */
    @Override
    public void initialize(GroupParent groupParent)
    {
        // Initialize variables
        for (VariableUnion variableUnion : this.values())
        {
            variableUnion.variable().initialize();

            // TODO add updatel listeners? in case not just literal values

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


    @Override
    public View view(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > List
        layout.addView(this.listView(context));

        return layout;
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Values
    // -----------------------------------------------------------------------------------------

    /**
     * The list values.
     * @return The variable union list.
     */
    public List<VariableUnion> values()
    {
        return this.values.getValue();
    }


    // ** Format
    // -----------------------------------------------------------------------------------------

    /**
     * The list widget specific format options.
     * @return The List Widget Format.
     */
    public ListWidgetFormat format()
    {
        return this.format.getValue();
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeListWidget()
    {
        // [1] Set default format values
        // -------------------------------------------------------------------------------------

        // ** Background
        if (this.data().format().backgroundIsDefault())
            this.data().format().setBackground(BackgroundColor.NONE);
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private LinearLayout listView(Context context)
    {
        LinearLayout layout = listViewLayout(context);

        for (VariableUnion variableUnion : this.values())
        {
            String itemValue = null;

            try {
                itemValue = variableUnion.variable().valueString();
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
            }

            // > Label
            String itemLabel = null;
            if (variableUnion.type() == VariableType.TEXT)
            {
                TextVariable textVariable = variableUnion.textVariable();
                if (textVariable.kind() == TextVariable.Kind.VALUE) {
                    Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
                    Value value = dictionary.value(textVariable.valueReference());
                    if (value != null)
                        itemLabel = value.summary();
                }
            }

            if (itemValue != null)
                layout.addView(listItemView(itemValue, itemLabel, context));
        }

        return layout;
    }


    private LinearLayout listViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private LinearLayout listItemView(String itemValue, String itemLabel, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();

        LinearLayoutBuilder valueLayout = new LinearLayoutBuilder();

        TextViewBuilder     item        = new TextViewBuilder();
        TextViewBuilder     annotation  = new TextViewBuilder();

        LinearLayoutBuilder divider     = new LinearLayoutBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

//        layout.margin.left          = R.dimen.widget_list_item_margins_horz;
//        layout.margin.right         = R.dimen.widget_list_item_margins_horz;

//        layout.padding.left         = R.dimen.widget_list_item_padding_horz;
//        layout.padding.right        = R.dimen.widget_list_item_padding_horz;

        layout.child(valueLayout)
              .child(divider);


        // [3] Value Layout
        // -------------------------------------------------------------------------------------

        valueLayout.orientation     = LinearLayout.HORIZONTAL;
        valueLayout.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        valueLayout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        valueLayout.child(item)
                   .child(annotation);

        // [4 A] Item
        // -------------------------------------------------------------------------------------

        item.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        item.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        item.text               = itemValue;

        this.format().itemStyle().styleTextViewBuilder(item, context);

        item.padding.left       = R.dimen.widget_list_item_value_padding_left;
        item.padding.top        = R.dimen.widget_list_item_padding_vert;
        item.padding.bottom     = R.dimen.widget_list_item_padding_vert;

        // [4 B] Inline Label
        // -------------------------------------------------------------------------------------

        annotation.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        annotation.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        this.format().annotationStyle().styleTextViewBuilder(annotation, context);

        annotation.text             = itemLabel;

        annotation.margin.left      = R.dimen.widget_list_inline_label_margin_left;

        // [5] Divider
        // -------------------------------------------------------------------------------------

        divider.orientation          = LinearLayout.HORIZONTAL;
        divider.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.height               = R.dimen.one_dp;

        divider.backgroundColor      = R.color.dark_blue_4;


        return layout.linearLayout(context);
    }


}
