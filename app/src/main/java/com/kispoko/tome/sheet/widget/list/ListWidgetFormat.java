
package com.kispoko.tome.sheet.widget.list;


import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.UUID;



/**
 * List Widget Format
 */
public class ListWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<TextSize>      size;
    private PrimitiveFunctor<TextColor>     tint;
    private ModelFunctor<TextStyle>         annotationStyle;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ListWidgetFormat()
    {
        this.id                 = null;

        this.size               = new PrimitiveFunctor<>(null, TextSize.class);
        this.tint               = new PrimitiveFunctor<>(null, TextColor.class);
        this.annotationStyle    = ModelFunctor.empty(TextStyle.class);
    }


    public ListWidgetFormat(UUID id,
                            TextSize size,
                            TextColor tint,
                            TextStyle annotationStyle)
    {
        this.id                 = id;

        this.size               = new PrimitiveFunctor<>(size, TextSize.class);
        this.tint               = new PrimitiveFunctor<>(tint, TextColor.class);
        this.annotationStyle    = ModelFunctor.full(annotationStyle, TextStyle.class);

        this.setSize(size);
        this.setTint(tint);
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

        TextSize  size            = TextSize.fromYaml(yaml.atMaybeKey("size"));
        TextColor tint            = TextColor.fromYaml(yaml.atMaybeKey("tint"));
        TextStyle annotationStyle = TextStyle.fromYaml(yaml.atMaybeKey("annotation_style"), false);

        return new ListWidgetFormat(id, size, tint, annotationStyle);
    }


    private static ListWidgetFormat asDefault()
    {
        ListWidgetFormat listWidgetFormat = new ListWidgetFormat();

        listWidgetFormat.setId(UUID.randomUUID());
        listWidgetFormat.setSize(null);
        listWidgetFormat.setTint(null);
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
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("size", this.size());
        yaml.putYaml("tint", this.tint());
        yaml.putYaml("annotation_style", this.annotationStyle());

        return yaml;
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Size
    // --------------------------------------------------------------------------------------

    /**
     * The List Widget's item text size.
     * @return The Widget Content Size.
     */
    public TextSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the list widget's item text size.
     * @param size The text size.
     */
    public void setSize(TextSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(TextSize.MEDIUM);
    }


    // ** Tint
    // --------------------------------------------------------------------------------------

    /**
     * The list widget item text tint.
     * @return The tint.
     */
    public TextColor tint()
    {
        return this.tint.getValue();
    }


    public void setTint(TextColor tint)
    {
        if (tint != null)
            this.tint.setValue(tint);
        else
            this.tint.setValue(TextColor.THEME_MEDIUM);
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
