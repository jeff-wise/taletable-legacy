
package com.kispoko.tome.sheet.widget.list;


import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;


/**
 * List Widget Format
 */
public class ListWidgetFormat extends Model
                              implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>         itemStyle;
    private ModelFunctor<TextStyle>         annotationStyle;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ListWidgetFormat()
    {
        this.id                 = null;

        this.itemStyle          = ModelFunctor.empty(TextStyle.class);
        this.annotationStyle    = ModelFunctor.empty(TextStyle.class);
    }


    public ListWidgetFormat(UUID id,
                            TextStyle itemStyle,
                            TextStyle annotationStyle)
    {
        this.id                 = id;

        this.itemStyle          = ModelFunctor.full(itemStyle, TextStyle.class);
        this.annotationStyle    = ModelFunctor.full(annotationStyle, TextStyle.class);

        this.setItemStyle(itemStyle);
        this.setAnnotationStyle(annotationStyle);
    }


    /**
     * Create a List Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The List Widget Format.
     * @throws YamlParseException
     */
    public static ListWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return ListWidgetFormat.asDefault();

        UUID      id              = UUID.randomUUID();

        TextStyle itemStyle       = TextStyle.fromYaml(yaml.atMaybeKey("item_style"));
        TextStyle annotationStyle = TextStyle.fromYaml(yaml.atMaybeKey("annotation_style"));

        return new ListWidgetFormat(id, itemStyle, annotationStyle);
    }


    private static ListWidgetFormat asDefault()
    {
        ListWidgetFormat listWidgetFormat = new ListWidgetFormat();

        listWidgetFormat.setId(UUID.randomUUID());

        listWidgetFormat.setItemStyle(null);
        listWidgetFormat.setAnnotationStyle(null);

        return listWidgetFormat;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // --------------------------------------------------------------------------------------

    // ** Id
    // --------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // --------------------------------------------------------------------------------------

    /**
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // --------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("item_style", this.itemStyle())
                .putYaml("annotation_style", this.annotationStyle());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Item Style
    // --------------------------------------------------------------------------------------

    /**
     * The item style.
     * @return The item style.
     */
    public TextStyle itemStyle()
    {
        return this.itemStyle.getValue();
    }


    /**
     * Set the item style. If null, sets a default style.
     * @param style The style.
     */
    public void setItemStyle(TextStyle style)
    {
        if (style != null) {
            this.itemStyle.setValue(style);
        }
        else {
            TextStyle defaultItemStyle = new TextStyle(UUID.randomUUID(),
                                                       TextColor.THEME_LIGHT,
                                                       TextSize.MEDIUM_SMALL);
            this.itemStyle.setValue(defaultItemStyle);
        }
    }


    // ** Annotation Style
    // --------------------------------------------------------------------------------------

    /**
     * The annotation style.
     * @return The annotation style.
     */
    public TextStyle annotationStyle()
    {
        return this.annotationStyle.getValue();
    }


    public void setAnnotationStyle(TextStyle annotationStyle)
    {
        if (annotationStyle != null) {
            this.annotationStyle.setValue(annotationStyle);
        }
        else {
            TextStyle defaultAnnotationStyle = new TextStyle(UUID.randomUUID(),
                                                             TextColor.THEME_MEDIUM_DARK,
                                                             TextSize.MEDIUM_SMALL);
            this.annotationStyle.setValue(defaultAnnotationStyle);
        }
    }


}
