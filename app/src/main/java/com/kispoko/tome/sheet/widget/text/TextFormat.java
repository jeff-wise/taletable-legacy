
package com.kispoko.tome.sheet.widget.text;


import com.kispoko.tome.sheet.widget.format.Format;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;



/**
 * TextWidget Format
 */
public class TextFormat extends Format
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Size size;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public TextFormat(String label,
                      Boolean showLabel,
                      Integer row,
                      Integer column,
                      Integer width,
                      Alignment alignment,
                      Size size)
    {
        super(label, showLabel, row, column, width, alignment);
        this.size = size;
    }


    public static TextFormat fromYaml(Yaml yaml)
                  throws YamlException
    {
        String    label     = yaml.atKey("label").getString();
        Boolean   showLabel = yaml.atKey("show_label").getBoolean();
        Integer   row       = yaml.atKey("row").getInteger();
        Integer   column    = yaml.atKey("column").getInteger();
        Integer   width     = yaml.atKey("width").getInteger();
        Alignment alignment = Alignment.fromString(yaml.atKey("alignment").getString());
        Size      size      = Size.fromString(yaml.atKey("size").getString());

        return new TextFormat(label, showLabel, row, column, width, alignment, size);
    }


    // API
    // --------------------------------------------------------------------------------------

    /**
     * Get the text size.
     * @return The text size.
     */
    public Size getSize()
    {
        return this.size;
    }


    /**
     * Set the text size.
     * @param size The text size.
     */
    public void setSize(Size size)
    {
        this.size = size;
    }


    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------

    public enum Size
    {
        SMALL,
        MEDIUM,
        LARGE;

        public static Size fromString(String size)
        {
            if (size != null)
                return Size.valueOf(size.toUpperCase());
            return null;
        }

    }

}
