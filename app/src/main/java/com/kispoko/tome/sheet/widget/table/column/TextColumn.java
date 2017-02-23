
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Text Column
 *
 * Contains metadata about the cells in a table's text column.
 */
public class TextColumn implements Model, Column, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private PrimitiveFunctor<String>        defaultValue;
    private PrimitiveFunctor<String>        defaultLabel;
    private PrimitiveFunctor<CellAlignment> alignment;

    /**
     * The column's text style. Any style elements defined are applied to each cell in the column.
     */
    private ModelFunctor<TextStyle>         style;

    private PrimitiveFunctor<Integer>       width;

    /**
     * True if the cells in this column define a namespace over the column row.
     */
    private PrimitiveFunctor<Boolean>       definesNamespace;

    /**
     * True if the cells in this column are namespaced.
     */
    private PrimitiveFunctor<Boolean>       isNamespaced;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextColumn()
    {
        this.id                 = null;

        this.name               = new PrimitiveFunctor<>(null, String.class);
        this.defaultValue       = new PrimitiveFunctor<>(null, String.class);
        this.defaultLabel       = new PrimitiveFunctor<>(null, String.class);
        this.alignment          = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.style              = ModelFunctor.empty(TextStyle.class);
        this.width              = new PrimitiveFunctor<>(null, Integer.class);
        this.definesNamespace   = new PrimitiveFunctor<>(null, Boolean.class);
        this.isNamespaced       = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public TextColumn(UUID id,
                      String name,
                      String defaultValue,
                      String defaultLabel,
                      CellAlignment alignment,
                      TextStyle style,
                      Integer width,
                      Boolean definesNamespace,
                      Boolean isNamespaced)
    {
        this.id                 = id;

        this.name               = new PrimitiveFunctor<>(name, String.class);
        this.defaultValue       = new PrimitiveFunctor<>(defaultValue, String.class);
        this.defaultLabel       = new PrimitiveFunctor<>(defaultLabel, String.class);
        this.alignment          = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.style              = ModelFunctor.full(style, TextStyle.class);
        this.width              = new PrimitiveFunctor<>(width, Integer.class);
        this.definesNamespace   = new PrimitiveFunctor<>(definesNamespace, Boolean.class);
        this.isNamespaced       = new PrimitiveFunctor<>(isNamespaced, Boolean.class);

        this.setAlignment(alignment);
        this.setStyle(style);
        this.setDefinesNamespace(definesNamespace);
        this.setIsNamespaced(isNamespaced);
    }


    /**
     * Create a text column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Text ColumnUnion.
     * @throws YamlParseException
     */
    public static TextColumn fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID          id                = UUID.randomUUID();

        String        name              = yaml.atKey("name").getString();
        String        defaultValue      = yaml.atKey("default_value").getString();
        String        defaultLabel      = yaml.atMaybeKey("default_label").getString();
        CellAlignment alignment         = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        TextStyle     style             = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        Integer       width             = yaml.atKey("width").getInteger();
        Boolean       definesNamespace  = yaml.atMaybeKey("defines_namespace").getBoolean();
        Boolean       isNamespaced      = yaml.atMaybeKey("namespaced").getBoolean();

        return new TextColumn(id, name, defaultValue, defaultLabel, alignment, style, width,
                              definesNamespace, isNamespaced);
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
     * This method is called when the Text Column is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Column's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("default_value", this.defaultValue())
                .putString("default_label", this.defaultLabel())
                .putYaml("alignment", this.alignment())
                .putYaml("style", this.style())
                .putInteger("width", this.width())
                .putBoolean("defines_namespace", this.definesNamespace())
                .putBoolean("namespaced", this.isNamespaced());
    }


    // > Column
    // ------------------------------------------------------------------------------------------

    /**
     * Get the column name.
     * @return The column name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * Get the alignment of this column. All cells in the column should have the same alignment.
     * @return The column alignment.
     */
    public CellAlignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Get the column width. All cells in the column should have the same width.
     * @return The column width.
     */
    public Integer width()
    {
        return this.width.getValue();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Alignment
    // ------------------------------------------------------------------------------------------

    /**
     * Set the alignment of the cells in the column.
     * @param alignment The cell alignment. If null, defaults to CENTER.
     */
    public void setAlignment(CellAlignment alignment)
    {
        if (alignment != null)
            this.alignment.setValue(alignment);
        else
            this.alignment.setValue(CellAlignment.CENTER);
    }


    // ** Style
    // ------------------------------------------------------------------------------------------

    /**
     * The column's text style. Any style elements defined are applied to each cell in the column.
     * @return Is Bold?
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the column's default text style.
     * @param style The text style.
     */
    public void setStyle(TextStyle style)
    {
        if (style != null) {
            this.style.setValue(style);
        }
        else {
            TextStyle defaultTextColumnStyle = new TextStyle(UUID.randomUUID(),
                                                             TextColor.THEME_MEDIUM,
                                                             TextSize.MEDIUM_SMALL);
            this.style.setValue(defaultTextColumnStyle);
        }
    }


    // ** Default Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the default column value. Cells with null values are given this value (if this value
     * is not null).
     * @return The default value.
     */
    public String defaultValue()
    {
        return this.defaultValue.getValue();
    }


    // ** Default Label
    // ------------------------------------------------------------------------------------------

    /**
     * The default column label. Variables in this column will get this label, if their label is
     * null (and this is not null).
     * @return The default value.
     */
    public String defaultLabel()
    {
        return this.defaultLabel.getValue();
    }


    // ** Defines Namespace
    // ------------------------------------------------------------------------------------------

    /**
     * True if the cells in this column define a namespace over the column row.
     * @return Defines namespace?
     */
    public Boolean definesNamespace()
    {
        return this.definesNamespace.getValue();
    }


    /**
     * Set the defines namespace value. If null, defaults to false.
     * @param definesNamespace True if this column defines a namespace.
     */
    public void setDefinesNamespace(Boolean definesNamespace)
    {
        if (definesNamespace != null)
            this.definesNamespace.setValue(definesNamespace);
        else
            this.definesNamespace.setValue(false);
    }


    // ** Is Namespaced
    // ------------------------------------------------------------------------------------------

    /**
     * True if the cells in this column are namespaced.
     * @return Is namespaced?
     */
    public Boolean isNamespaced()
    {
        return this.isNamespaced.getValue();
    }


    /**
     * Set to true if the column is namespaced.
     * @param isNamespaced True if this column is namespaced.
     */
    public void setIsNamespaced(Boolean isNamespaced)
    {
        if (isNamespaced != null)
            this.isNamespaced.setValue(isNamespaced);
        else
            this.isNamespaced.setValue(false);
    }

}
