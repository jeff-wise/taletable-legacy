
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.sheet.dialog.ListWidgetDialogFragment;
import com.kispoko.tome.activity.sheet.dialog.TextWidgetDialogFragment;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.Value;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.list.ListWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

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

    private CollectionFunctor<VariableUnion>    values;

    /**
     * If this value is not null, it indicates that all values in the list are to be taken from
     * the value set with the given name.
     */
    private PrimitiveFunctor<String>            valueSetName;

    private ModelFunctor<ListWidgetFormat>      format;
    private ModelFunctor<WidgetData>            widgetData;



    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ListWidget()
    {
        this.id             = null;

        this.values         = CollectionFunctor.empty(VariableUnion.class);

        this.valueSetName   = new PrimitiveFunctor<>(null, String.class);

        this.format         = ModelFunctor.empty(ListWidgetFormat.class);
        this.widgetData     = ModelFunctor.empty(WidgetData.class);
    }


    public ListWidget(UUID id,
                      List<VariableUnion> values,
                      String valueSetName,
                      ListWidgetFormat format,
                      WidgetData widgetData)
    {
        this.id             = id;

        this.values         = CollectionFunctor.full(values, VariableUnion.class);

        this.valueSetName   = new PrimitiveFunctor<>(valueSetName, String.class);

        this.format         = ModelFunctor.full(format, ListWidgetFormat.class);
        this.widgetData     = ModelFunctor.full(widgetData, WidgetData.class);

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
        UUID                id           = UUID.randomUUID();

        List<VariableUnion> values       = yaml.atMaybeKey("values").forEach(
                                                new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        String              valueSetName = yaml.atMaybeKey("value_set").getString();

        ListWidgetFormat    format       = ListWidgetFormat.fromYaml(yaml.atMaybeKey("format"));
        WidgetData          widgetData   = WidgetData.fromYaml(yaml.atMaybeKey("data"));

        return new ListWidget(id, values, valueSetName, format, widgetData);
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
                .putList("values", this.values())
                .putString("value_set_name", this.valueSetName())
                .putYaml("format", this.format())
                .putYaml("data", this.data());
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


    // ** Value Set Name
    // -----------------------------------------------------------------------------------------

    /**
     * The value set name. May be null.
     * @return The value set name.
     */
    public String valueSetName()
    {
        return this.valueSetName.getValue();
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

        int itemIndex = 0;
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
            {
                itemIndex += 1;
                layout.addView(listItemView(itemValue, itemLabel, itemIndex, context));
            }
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


    private LinearLayout listItemView(String itemValue,
                                      String itemLabel,
                                      final int itemIndex,
                                      final Context context)
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

        layout.onClick              = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onListWidgetShortClick(itemIndex, context);
            }
        };

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


    // > Clicks
    // -----------------------------------------------------------------------------------------

    /**
     * On a short click, open the value editor.
     */
    private void onListWidgetShortClick(Integer itemClicked, Context context)
    {
        SheetActivity sheetActivity = (SheetActivity) context;

        ListWidgetDialogFragment dialog = ListWidgetDialogFragment.newInstance(this, itemClicked);
        dialog.show(sheetActivity.getSupportFragmentManager(), "");
    }


}
