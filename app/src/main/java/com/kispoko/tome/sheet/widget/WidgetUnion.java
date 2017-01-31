
package com.kispoko.tome.sheet.widget;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Widget Union
 */
public class WidgetUnion implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<ActionWidget>      actionWidget;
    private ModelFunctor<BooleanWidget>     booleanWidget;
    private ModelFunctor<ImageWidget>       imageWidget;
    private ModelFunctor<ListWidget>        listWidget;
    private ModelFunctor<NumberWidget>      numberWidget;
    private ModelFunctor<TableWidget>       tableWidget;
    private ModelFunctor<TextWidget>        textWidget;

    private PrimitiveFunctor<WidgetType>    type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public WidgetUnion()
    {
        this.id = null;

        this.actionWidget   = ModelFunctor.empty(ActionWidget.class);
        this.booleanWidget  = ModelFunctor.empty(BooleanWidget.class);
        this.imageWidget    = ModelFunctor.empty(ImageWidget.class);
        this.listWidget     = ModelFunctor.empty(ListWidget.class);
        this.numberWidget   = ModelFunctor.empty(NumberWidget.class);
        this.tableWidget    = ModelFunctor.empty(TableWidget.class);
        this.textWidget     = ModelFunctor.empty(TextWidget.class);

        this.type           = new PrimitiveFunctor<>(null, WidgetType.class);
    }


    private WidgetUnion(UUID id, Object widget, WidgetType type)
    {
        this.id              = id;

        this.actionWidget   = ModelFunctor.full(null, ActionWidget.class);
        this.booleanWidget  = ModelFunctor.full(null, BooleanWidget.class);
        this.imageWidget    = ModelFunctor.full(null, ImageWidget.class);
        this.listWidget     = ModelFunctor.full(null, ListWidget.class);
        this.numberWidget   = ModelFunctor.full(null, NumberWidget.class);
        this.tableWidget    = ModelFunctor.full(null, TableWidget.class);
        this.textWidget     = ModelFunctor.full(null, TextWidget.class);

        this.type           = new PrimitiveFunctor<>(type, WidgetType.class);

        switch (type)
        {
            case ACTION:
                this.actionWidget.setValue((ActionWidget) widget);
                break;
            case BOOLEAN:
                this.booleanWidget.setValue((BooleanWidget) widget);
                break;
            case IMAGE:
                this.imageWidget.setValue((ImageWidget) widget);
                break;
            case LIST:
                this.listWidget.setValue((ListWidget) widget);
                break;
            case NUMBER:
                this.numberWidget.setValue((NumberWidget) widget);
                break;
            case TABLE:
                this.tableWidget.setValue((TableWidget) widget);
                break;
            case TEXT:
                this.textWidget.setValue((TextWidget) widget);
                break;
        }
    }


    // > Variants
    // ------------------------------------------------------------------------------------------

    /**
     * Create the "action" variant.
     * @param actionWidget The action widget.
     * @return The "action" Widget Union.
     */
    public static WidgetUnion asAction(UUID id, ActionWidget actionWidget)
    {
        return new WidgetUnion(id, actionWidget, WidgetType.ACTION);
    }


    /**
     * Create the "boolean" variant.
     * @param booleanWidget The boolean widget.
     * @return The "boolean" Widget Union.
     */
    public static WidgetUnion asBoolean(UUID id, BooleanWidget booleanWidget)
    {
        return new WidgetUnion(id, booleanWidget, WidgetType.BOOLEAN);
    }


    /**
     * Create the "image" variant.
     * @param imageWidget The image widget.
     * @return The "image" Widget Union.
     */
    public static WidgetUnion asImage(UUID id, ImageWidget imageWidget)
    {
        return new WidgetUnion(id, imageWidget, WidgetType.IMAGE);
    }


    /**
     * Create the "list" variant.
     * @param listWidget The list widget.
     * @return The "list" Widget Union.
     */
    public static WidgetUnion asList(UUID id, ListWidget listWidget)
    {
        return new WidgetUnion(id, listWidget, WidgetType.LIST);
    }


    /**
     * Create the "number" variant.
     * @param numberWidget The number widget.
     * @return The "number" Widget Union.
     */
    public static WidgetUnion asNumber(UUID id, NumberWidget numberWidget)
    {
        return new WidgetUnion(id, numberWidget, WidgetType.NUMBER);
    }


    /**
     * Create the "table" variant.
     * @param tableWidget The table widget.
     * @return The "table" Widget Union.
     */
    public static WidgetUnion asTable(UUID id, TableWidget tableWidget)
    {
        return new WidgetUnion(id, tableWidget, WidgetType.TABLE);
    }


    /**
     * Create the "text" variant.
     * @param textWidget The text widget.
     * @return The "text" Widget Union.
     */
    public static WidgetUnion asText(UUID id, TextWidget textWidget)
    {
        return new WidgetUnion(id, textWidget, WidgetType.TEXT);
    }


    // > From Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * Create a Widget Union from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Widget Union
     * @throws YamlParseException
     */
    public static WidgetUnion fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID       id   = UUID.randomUUID();

        WidgetType type = WidgetType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case ACTION:
                ActionWidget actionWidget = ActionWidget.fromYaml(yaml);
                return WidgetUnion.asAction(id, actionWidget);
            case BOOLEAN:
                BooleanWidget booleanWidget = BooleanWidget.fromYaml(yaml);
                return WidgetUnion.asBoolean(id, booleanWidget);
            case IMAGE:
                ImageWidget imageWidget = ImageWidget.fromYaml(yaml);
                return WidgetUnion.asImage(id, imageWidget);
            case LIST:
                ListWidget listWidget = ListWidget.fromYaml(yaml);
                return WidgetUnion.asList(id, listWidget);
            case NUMBER:
                NumberWidget numberWidget = NumberWidget.fromYaml(yaml);
                return WidgetUnion.asNumber(id, numberWidget);
            case TABLE:
                TableWidget tableWidget = TableWidget.fromYaml(yaml);
                return WidgetUnion.asTable(id, tableWidget);
            case TEXT:
                TextWidget textWidget = TextWidget.fromYaml(yaml);
                return WidgetUnion.asText(id, textWidget);
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(WidgetType.class.getName())));
        }

        return null;
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
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Variable Union's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder widgetYaml = null;

        switch (this.type())
        {
            case ACTION:
                widgetYaml = this.actionWidget().toYaml();
                break;
            case BOOLEAN:
                widgetYaml = this.booleanWidget().toYaml();
                break;
            case IMAGE:
                widgetYaml = this.imageWidget().toYaml();
                break;
            case LIST:
                widgetYaml = this.listWidget().toYaml();
                break;
            case NUMBER:
                widgetYaml = this.numberWidget().toYaml();
                break;
            case TABLE:
                widgetYaml = this.tableWidget().toYaml();
                break;
            case TEXT:
                widgetYaml = this.textWidget().toYaml();
                break;
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(WidgetType.class.getName())));
        }

        widgetYaml.putYaml("type", this.type());

        return widgetYaml;
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    public Widget widget()
    {
        switch (this.type())
        {
            case ACTION:
                return this.actionWidget();
            case BOOLEAN:
                return this.booleanWidget();
            case IMAGE:
                return this.imageWidget();
            case LIST:
                return this.listWidget();
            case NUMBER:
                return this.numberWidget();
            case TABLE:
                return this.tableWidget();
            case TEXT:
                return this.textWidget();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(WidgetType.class.getName())));
        }

        return null;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * The Widget Type.
     * @return The Widget Type.
     */
    public WidgetType type()
    {
        return this.type.getValue();
    }


    // ** Variants
    // ------------------------------------------------------------------------------------------

    /**
     * The "action" case.
     * @return The Action Widget.
     */
    public ActionWidget actionWidget()
    {
        return this.actionWidget.getValue();
    }


    /**
     * The "boolean" case.
     * @return The Boolean Widget.
     */
    public BooleanWidget booleanWidget()
    {
        return this.booleanWidget.getValue();
    }


    /**
     * The "image" case.
     * @return The Image Widget.
     */
    public ImageWidget imageWidget()
    {
        return this.imageWidget.getValue();
    }


    /**
     * The "list" case.
     * @return The List Widget.
     */
    public ListWidget listWidget()
    {
        return this.listWidget.getValue();
    }


    /**
     * The "number" case.
     * @return The Number Widget.
     */
    public NumberWidget numberWidget()
    {
        return this.numberWidget.getValue();
    }


    /**
     * The "table" case.
     * @return The Table Widget.
     */
    public TableWidget tableWidget()
    {
        return this.tableWidget.getValue();
    }


    /**
     * The "text" case.
     * @return The Text Widget.
     */
    public TextWidget textWidget()
    {
        return this.textWidget.getValue();
    }
}
