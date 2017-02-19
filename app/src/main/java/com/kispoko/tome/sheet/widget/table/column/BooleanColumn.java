
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Boolean Column
 *
 * Contains metadata about the cells in a table's boolean column.
 */
public class BooleanColumn implements Model, Column, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functor
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;

    private PrimitiveFunctor<Boolean>       defaultValue;

    private PrimitiveFunctor<CellAlignment> alignment;
    private PrimitiveFunctor<Integer>       width;

    private PrimitiveFunctor<String>        trueText;
    private PrimitiveFunctor<String>        falseText;

    /**
     * True if the cells in this column are namespaced.
     */
    private PrimitiveFunctor<Boolean>       isNamespaced;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanColumn()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);

        this.defaultValue   = new PrimitiveFunctor<>(null, Boolean.class);

        this.alignment      = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.width          = new PrimitiveFunctor<>(null, Integer.class);

        this.trueText       = new PrimitiveFunctor<>(null, String.class);
        this.falseText      = new PrimitiveFunctor<>(null, String.class);

        this.isNamespaced   = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public BooleanColumn(UUID id,
                         String name,
                         Boolean defaultValue,
                         CellAlignment alignment,
                         Integer width,
                         String trueText,
                         String falseText,
                         Boolean isNamespaced)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);

        this.defaultValue   = new PrimitiveFunctor<>(defaultValue, Boolean.class);

        this.alignment      = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.width          = new PrimitiveFunctor<>(width, Integer.class);

        this.trueText       = new PrimitiveFunctor<>(trueText, String.class);
        this.falseText      = new PrimitiveFunctor<>(falseText, String.class);

        this.isNamespaced   = new PrimitiveFunctor<>(isNamespaced, Boolean.class);

        this.setTrueText(trueText);
        this.setFalseText(falseText);
        this.setIsNamespaced(isNamespaced);
    }


    /**
     * Create a boolean column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Boolean ColumnUnion.
     * @throws YamlParseException
     */
    public static BooleanColumn fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID          id           = UUID.randomUUID();

        String        name         = yaml.atKey("name").getString();

        Boolean       defaultValue = yaml.atKey("default_value").getBoolean();

        CellAlignment alignment    = CellAlignment.fromYaml(yaml.atKey("alignment"));
        Integer       width        = yaml.atKey("width").getInteger();

        String        trueText     = yaml.atMaybeKey("true").getString();
        String        falseText    = yaml.atMaybeKey("false").getString();

        Boolean       isNamespaced = yaml.atMaybeKey("namespaced").getBoolean();

        return new BooleanColumn(id, name, defaultValue, alignment, width,
                                 trueText, falseText, isNamespaced);
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
     * This method is called when the Boolean Column is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Boolean Column's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putBoolean("default_value", this.defaultValue())
                .putYaml("alignment", this.alignment())
                .putInteger("width", this.width())
                .putString("true", this.trueText())
                .putString("false", this.falseText())
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
     * Get the alignment of this cell.
     * @return The cell Alignment.
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

    // ** True Text
    // ------------------------------------------------------------------------------------------

    /**
     * The text to display in the column's cells when the value is true.
     * @return The true text.
     */
    public String trueText()
    {
        return this.trueText.getValue();
    }


    /**
     * Set the true text value.
     * @param trueText The true text. If null, defaults to 'Yes'
     */
    public void setTrueText(String trueText)
    {
        if (trueText != null)
            this.trueText.setValue(trueText);
        else
            this.trueText.setValue("Yes");
    }


    // ** False Text
    // ------------------------------------------------------------------------------------------

    /**
     * The text to display in the column's cells when the value is false.
     * @return The false text.
     */
    public String falseText()
    {
        return this.falseText.getValue();
    }


    /**
     * Set the false text value.
     * @param falseText The false text. If null, defaults to 'No'
     */
    public void setFalseText(String falseText)
    {
        if (falseText != null)
            this.falseText.setValue(falseText);
        else
            this.falseText.setValue("No");
    }


    // ** Default Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the default column value. Cells with null values are given this value (if this value
     * is not null).
     * @return The default value.
     */
    public Boolean defaultValue()
    {
        return this.defaultValue.getValue();
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
