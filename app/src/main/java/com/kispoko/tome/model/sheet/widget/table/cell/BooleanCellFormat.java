
package com.kispoko.tome.model.sheet.widget.table.cell;


import com.kispoko.tome.model.sheet.BackgroundColor;
import com.kispoko.tome.model.sheet.widget.util.TextStyle;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Boolean Cell Format
 */
public class BooleanCellFormat extends Model
                               implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The alignment of the content in the cell.
     */
    private PrimitiveFunctor<Alignment>     alignment;


    /**
     * The cell background color.
     */
    private PrimitiveFunctor<BackgroundColor>    background;

    /**
     * The cell value text style.
     */
    private ModelFunctor<TextStyle>         style;

    /**
     * The text style of the cell's value if the value is true.
     */
    private ModelFunctor<TextStyle>         trueStyle;

    /**
     * The text style of the cell's value if the value is false.
     */
    private ModelFunctor<TextStyle>         falseStyle;

    /**
     * If true, shows an icon beside the true text.
     */
    private PrimitiveFunctor<Boolean>       showTrueIcon;

    /**
     * If true, shows an icon beside the false text.
     */
    private PrimitiveFunctor<Boolean>       showFalseIcon;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public BooleanCellFormat()
    {
        this.id             = null;

        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
        this.background     = new PrimitiveFunctor<>(null, BackgroundColor.class);

        this.style          = ModelFunctor.empty(TextStyle.class);
        this.trueStyle      = ModelFunctor.empty(TextStyle.class);
        this.falseStyle     = ModelFunctor.empty(TextStyle.class);

        this.showTrueIcon   = new PrimitiveFunctor<>(null, Boolean.class);
        this.showFalseIcon  = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public BooleanCellFormat(UUID id,
                             Alignment alignment,
                             BackgroundColor background,
                             TextStyle style,
                             TextStyle trueStyle,
                             TextStyle falseStyle,
                             Boolean showTrueIcon,
                             Boolean showFalseIcon)
    {
        this.id             = id;


        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.background     = new PrimitiveFunctor<>(background, BackgroundColor.class);

        this.style          = ModelFunctor.full(style, TextStyle.class);
        this.trueStyle      = ModelFunctor.full(trueStyle, TextStyle.class);
        this.falseStyle     = ModelFunctor.full(falseStyle, TextStyle.class);

        this.showTrueIcon   = new PrimitiveFunctor<>(showTrueIcon, Boolean.class);
        this.showFalseIcon  = new PrimitiveFunctor<>(showFalseIcon, Boolean.class);

        this.setAlignment(alignment);
        this.setBackground(background);
        this.setStyle(style);
        this.setShowTrueIcon(showTrueIcon);
        this.setShowFalseIcon(showFalseIcon);
    }


    /**
     * Create a Boolean Cell Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Boolean Cell Format.
     * @throws YamlParseException
     */
    public static BooleanCellFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return BooleanCellFormat.asDefault();

        UUID       id            = UUID.randomUUID();

        Alignment  alignment     = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        BackgroundColor background    = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));

        TextStyle  style         = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        TextStyle  trueStyle     = TextStyle.fromYaml(yaml.atMaybeKey("true_style"), false);
        TextStyle  falseStyle    = TextStyle.fromYaml(yaml.atMaybeKey("false_style"), false);

        Boolean    showTrueIcon  = yaml.atMaybeKey("show_true_icon").getBoolean();
        Boolean    showFalseIcon = yaml.atMaybeKey("show_false_icon").getBoolean();

        return new BooleanCellFormat(id, alignment, background, style, trueStyle, falseStyle,
                                     showTrueIcon, showFalseIcon);
    }


    /**
     * Create a Boolean Cell Format with default formatting options.
     * @return The default Boolean Cell Format.
     */
    private static BooleanCellFormat asDefault()
    {
        BooleanCellFormat format = new BooleanCellFormat();

        format.setId(UUID.randomUUID());

        format.setAlignment(null);
        format.setBackground(null);

        format.setStyle(null);
        format.setTrueStyle(null);
        format.setFalseStyle(null);

        format.setShowTrueIcon(null);
        format.setShowFalseIcon(null);

        return format;
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
                .putYaml("alignment", this.alignment())
                .putYaml("background", this.background())
                .putYaml("style", this.style())
                .putYaml("true_style", this.trueStyle())
                .putYaml("false_style", this.falseStyle())
                .putBoolean("show_true_icon", this.showTrueIcon())
                .putBoolean("show_false_icon", this.showFalseIcon());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Alignment
    // --------------------------------------------------------------------------------------

    /**
     * The default column alignment.
     * @return The alignment
     */
    public Alignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Set the default column alignment.
     * @param alignment The alignment
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment != null) {
            this.alignment.setValue(alignment);
            this.alignment.setIsDefault(false);
        }
        else {
            this.alignment.setValue(Alignment.CENTER);
            this.alignment.setIsDefault(true);
        }
    }


    /**
     * Resolve the alignment setting between the column and the cell.
     * @param columnAlignment The column alignment (could be null).
     * @return The appropriate alignment.
     */
    public Alignment resolveAlignment(Alignment columnAlignment)
    {
        if (columnAlignment == null)
            return this.alignment();

        if (this.alignment.isDefault())
            return columnAlignment;

        return this.alignment();
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The cell background color.
     * @return The background.
     */
    public BackgroundColor background()
    {
        return this.background.getValue();
    }


    /**
     * Set the cell background color.
     * @param background The background.
     */
    public void setBackground(BackgroundColor background)
    {
        if (background != null) {
            this.background.setValue(background);
            this.background.setIsDefault(false);
        }
        else {
            this.background.setValue(BackgroundColor.MEDIUM);
            this.background.setIsDefault(true);
        }
    }


    /**
     * Resolve the background value between the column and the cell.
     * @param columnBackground The column background (could be null).
     * @return The appropriate background.
     */
    public BackgroundColor resolveBackground(BackgroundColor columnBackground)
    {
        if (columnBackground == null)
            return this.background();

        if (this.background.isDefault())
            return columnBackground;

        return this.background();
    }


    // ** Style
    // --------------------------------------------------------------------------------------

    /**
     * The default cell style.
     * @return The style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the default cell style. If null, a default style is set.
     * @param style The style.
     */
    public void setStyle(TextStyle style)
    {
        if (style != null) {
            this.style.setValue(style);
            this.style.setIsDefault(false);
        }
        else {
            TextStyle defaultBooleanCellStyle = new TextStyle(UUID.randomUUID(),
                                                              TextColor.THEME_MEDIUM,
                                                              TextSize.MEDIUM_SMALL);
            this.style.setValue(defaultBooleanCellStyle);
            this.style.setIsDefault(true);
        }
    }


    /**
     * Resolve the style setting between the column and the cell.
     * @param columnStyle The column style (could be null).
     * @return The appropriate style.
     */
    public TextStyle resolveStyle(TextStyle columnStyle)
    {
        if (columnStyle == null)
            return this.style();

        if (this.style.isDefault())
            return columnStyle;

        return this.style();
    }


    // ** True Style
    // --------------------------------------------------------------------------------------

    /**
     * The default column true valuestyle.
     * @return The style.
     */
    public TextStyle trueStyle()
    {
        return this.trueStyle.getValue();
    }


    /**
     * Set the default column true valuestyle.
     * @param style The style.
     */
    public void setTrueStyle(TextStyle style)
    {
        this.trueStyle.setValue(style);
    }


    /**
     * Resolve the true style between the boolean column and the boolean cell.
     * @param columnTrueStyle The column true style.
     * @return The appropriate true style.
     */
    public TextStyle resolveTrueStyle(TextStyle columnTrueStyle)
    {
        if (columnTrueStyle == null)
            return this.trueStyle();

        if (this.trueStyle() == null)
            return columnTrueStyle;

        return this.trueStyle();
    }


    // ** False Style
    // --------------------------------------------------------------------------------------

    /**
     * The default column false style.
     * @return The style.
     */
    public TextStyle falseStyle()
    {
        return this.falseStyle.getValue();
    }


    /**
     * Set the default column false style.
     * @param style The style.
     */
    public void setFalseStyle(TextStyle style)
    {
        this.falseStyle.setValue(style);
    }


    /**
     * Resolve the false style between the boolean column and the boolean cell.
     * @param columnFalseStyle The column false style.
     * @return The appropriate false style (could be null).
     */
    public TextStyle resolveFalseStyle(TextStyle columnFalseStyle)
    {
        if (columnFalseStyle == null)
            return this.falseStyle();

        if (this.falseStyle() == null)
            return columnFalseStyle;

        return this.falseStyle();
    }


    // ** Show True Icon
    // --------------------------------------------------------------------------------------

    /**
     * True if the column displays an icon in all true value cells.
     * @return Show true icon?
     */
    public Boolean showTrueIcon()
    {
        return this.showTrueIcon.getValue();
    }


    /**
     * Set true if an icon is displayed in all true value cells.
     * @param showTrueIcon Show true icon?
     */
    public void setShowTrueIcon(Boolean showTrueIcon)
    {
        if (showTrueIcon != null) {
            this.showTrueIcon.setValue(showTrueIcon);
            this.showTrueIcon.setIsDefault(false);
        }
        else {
            this.showTrueIcon.setValue(false);
            this.showTrueIcon.setIsDefault(true);
        }
    }


    /**
     * Resolve the show true icon value between the column and the cell.
     * @param columnShowTrueIcon The column's show true icon value.
     * @return The appropriate show true icon value.
     */
    public Boolean resolveShowTrueIcon(Boolean columnShowTrueIcon)
    {
        if (columnShowTrueIcon == null)
            return this.showTrueIcon();

        if (this.showTrueIcon.isDefault())
            return columnShowTrueIcon;

        return this.showTrueIcon();
    }


    // ** Show False Icon
    // --------------------------------------------------------------------------------------

    /**
     * True if the column displays an icon in all false value cells.
     * @return Show false icon?
     */
    public Boolean showFalseIcon()
    {
        return this.showFalseIcon.getValue();
    }


    /**
     * Set true if an icon is displayed in all false value cells.
     * @param showFalseIcon Show false icon?
     */
    public void setShowFalseIcon(Boolean showFalseIcon)
    {
        if (showFalseIcon != null) {
            this.showFalseIcon.setValue(showFalseIcon);
            this.showFalseIcon.setIsDefault(false);
        }
        else {
            this.showFalseIcon.setValue(false);
            this.showFalseIcon.setIsDefault(true);
        }
    }


    /**
     * Resolve the show false icon value between the column and the cell.
     * @param columnShowFalseIcon The column's show false icon value.
     * @return The appropriate show false icon value.
     */
    public Boolean resolveShowFalseIcon(Boolean columnShowFalseIcon)
    {
        if (columnShowFalseIcon == null)
            return this.showFalseIcon();

        if (this.showFalseIcon.isDefault())
            return columnShowFalseIcon;

        return this.showFalseIcon();
    }


}
