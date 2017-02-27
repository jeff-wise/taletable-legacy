
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.Alignment;
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

import static android.R.attr.width;


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

    private ModelFunctor<TextColumnFormat>  format;

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
        this.format             = ModelFunctor.empty(TextColumnFormat.class);
        this.definesNamespace   = new PrimitiveFunctor<>(null, Boolean.class);
        this.isNamespaced       = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public TextColumn(UUID id,
                      String name,
                      String defaultValue,
                      String defaultLabel,
                      TextColumnFormat format,
                      Boolean definesNamespace,
                      Boolean isNamespaced)
    {
        this.id                 = id;

        this.name               = new PrimitiveFunctor<>(name, String.class);
        this.defaultValue       = new PrimitiveFunctor<>(defaultValue, String.class);
        this.defaultLabel       = new PrimitiveFunctor<>(defaultLabel, String.class);
        this.format             = ModelFunctor.empty(TextColumnFormat.class);
        this.definesNamespace   = new PrimitiveFunctor<>(definesNamespace, Boolean.class);
        this.isNamespaced       = new PrimitiveFunctor<>(isNamespaced, Boolean.class);

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
        UUID             id                = UUID.randomUUID();

        String           name              = yaml.atKey("name").getString();
        String           defaultValue      = yaml.atKey("default_value").getString();
        String           defaultLabel      = yaml.atMaybeKey("default_label").getString();
        TextColumnFormat format            = TextColumnFormat.fromYaml(yaml.atMaybeKey("format"));
        Boolean          definesNamespace  = yaml.atMaybeKey("defines_namespace").getBoolean();
        Boolean          isNamespaced      = yaml.atMaybeKey("namespaced").getBoolean();

        return new TextColumn(id, name, defaultValue, defaultLabel, format,
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

    @Override
    public String name()
    {
        return this.name.getValue();
    }


    @Override
    public Alignment alignment()
    {
        return this.format().alignment();
    }


    @Override
    public Integer width()
    {
        return this.format().width();
    }


    @Override
    public TextStyle style()
    {
        return this.format().style();
    }


    // > State
    // ------------------------------------------------------------------------------------------

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


    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The text column format.
     * @return The format.
     */
    public TextColumnFormat format()
    {
        return this.format.getValue();
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
