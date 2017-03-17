
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Number ColumnUnion
 *
 * Contains metadata about the cells in a table's number column.
 */
public class NumberColumn implements Model, Column, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;

    private PrimitiveFunctor<Integer>           defaultValue;
    private PrimitiveFunctor<String>            defaultLabel;

    private ModelFunctor<NumberColumnFormat>    format;

    private PrimitiveFunctor<Boolean>           isNamespaced;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberColumn()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);

        this.defaultValue   = new PrimitiveFunctor<>(null, Integer.class);
        this.defaultLabel   = new PrimitiveFunctor<>(null, String.class);

        this.format         = ModelFunctor.empty(NumberColumnFormat.class);

        this.isNamespaced   = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public NumberColumn(UUID id,
                        String name,
                        Integer defaultValue,
                        String defaultLabel,
                        NumberColumnFormat format,
                        Boolean isNamespaced)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);

        this.defaultValue   = new PrimitiveFunctor<>(defaultValue, Integer.class);
        this.defaultLabel   = new PrimitiveFunctor<>(defaultLabel, String.class);

        this.format         = ModelFunctor.full(format, NumberColumnFormat.class);

        this.isNamespaced   = new PrimitiveFunctor<>(isNamespaced, Boolean.class);

        this.setIsNamespaced(isNamespaced);
    }


    /**
     * Create a number column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Number ColumnUnion.
     * @throws YamlParseException
     */
    public static NumberColumn fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                id           = UUID.randomUUID();

        String              name         = yaml.atKey("name").getString();

        Integer             defaultValue = yaml.atKey("default_value").getInteger();
        String              defaultLabel = yaml.atMaybeKey("default_label").getString();

        NumberColumnFormat  format       = NumberColumnFormat.fromYaml(yaml.atMaybeKey("format"));

        Boolean             isNamespaced = yaml.atMaybeKey("namespaced").getBoolean();

        return new NumberColumn(id, name, defaultValue, defaultLabel, format, isNamespaced);
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
     * This method is called when the Number Column is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Number Column's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putInteger("default_value", this.defaultValue())
                .putString("default_label", this.defaultLabel())
                .putYaml("format", this.format())
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
    public Integer defaultValue()
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
     * The number column format options.
     * @return The format.
     */
    public NumberColumnFormat format()
    {
        return this.format.getValue();
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
