
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.TextStyle;
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
 * Boolean Column Format
 */
public class BooleanColumnFormat extends Model
                                 implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The default alignment for all cells in the column.
     */
    private PrimitiveFunctor<Alignment>     alignment;

    /**
     * The default width for all cells in the column.
     */
    private PrimitiveFunctor<Integer>       width;

    /**
     * The default background for cells in this column.
     */
    private PrimitiveFunctor<BackgroundColor>    background;

    /**
     * The default column text style.
     */
    private ModelFunctor<TextStyle>         style;

    /**
     * The text style of any true values in the column.
     */
    private ModelFunctor<TextStyle>         trueStyle;

    /**
     * The text style of any false values in the column.
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

    public BooleanColumnFormat()
    {
        this.id             = null;

        this.alignment      = new PrimitiveFunctor<>(null, Alignment.class);
        this.width          = new PrimitiveFunctor<>(null, Integer.class);
        this.background     = new PrimitiveFunctor<>(null, BackgroundColor.class);

        this.style          = ModelFunctor.empty(TextStyle.class);
        this.trueStyle      = ModelFunctor.empty(TextStyle.class);
        this.falseStyle     = ModelFunctor.empty(TextStyle.class);

        this.showTrueIcon   = new PrimitiveFunctor<>(null, Boolean.class);
        this.showFalseIcon  = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public BooleanColumnFormat(UUID id,
                               Alignment alignment,
                               Integer width,
                               BackgroundColor background,
                               TextStyle style,
                               TextStyle trueStyle,
                               TextStyle falseStyle,
                               Boolean showTrueIcon,
                               Boolean showFalseIcon)
    {
        this.id             = id;


        this.alignment      = new PrimitiveFunctor<>(alignment, Alignment.class);
        this.width          = new PrimitiveFunctor<>(width, Integer.class);
        this.background     = new PrimitiveFunctor<>(background, BackgroundColor.class);

        this.style          = ModelFunctor.full(style, TextStyle.class);
        this.trueStyle      = ModelFunctor.full(trueStyle, TextStyle.class);
        this.falseStyle     = ModelFunctor.full(falseStyle, TextStyle.class);

        this.showTrueIcon   = new PrimitiveFunctor<>(showTrueIcon, Boolean.class);
        this.showFalseIcon  = new PrimitiveFunctor<>(showFalseIcon, Boolean.class);
    }


    /**
     * Create a Boolean Column Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Boolean Column Format.
     * @throws YamlParseException
     */
    public static BooleanColumnFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return BooleanColumnFormat.asDefault();

        UUID       id            = UUID.randomUUID();

        Alignment  alignment     = Alignment.fromYaml(yaml.atMaybeKey("alignment"));
        Integer    width         = yaml.atMaybeKey("width").getInteger();
        BackgroundColor background    = BackgroundColor.fromYaml(yaml.atMaybeKey("background"));

        TextStyle  style         = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        TextStyle  trueStyle     = TextStyle.fromYaml(yaml.atMaybeKey("true_style"), false);
        TextStyle  falseStyle    = TextStyle.fromYaml(yaml.atMaybeKey("false_style"), false);

        Boolean    showTrueIcon  = yaml.atMaybeKey("show_true_icon").getBoolean();
        Boolean    showFalseIcon = yaml.atMaybeKey("show_false_icon").getBoolean();

        return new BooleanColumnFormat(id, alignment, width, background, style,
                                       trueStyle, falseStyle, showTrueIcon, showFalseIcon);
    }


    /**
     * Create a default Boolaen Column Format. All of the values are null
     * @return
     */
    private static BooleanColumnFormat asDefault()
    {
        BooleanColumnFormat format = new BooleanColumnFormat();

        format.setId(UUID.randomUUID());

        format.setAlignment(null);
        format.setWidth(null);
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
                .putInteger("width", this.width())
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
        this.alignment.setValue(alignment);
    }


    // ** Width
    // --------------------------------------------------------------------------------------

    /**
     * The default column width.
     * @return The width.
     */
    public Integer width()
    {
        return this.width.getValue();
    }


    /**
     * Set the default column width.
     * @param width The width.
     */
    public void setWidth(Integer width)
    {
        this.width.setValue(width);
    }


    // ** Background
    // --------------------------------------------------------------------------------------

    /**
     * The boolean column default cell background.
     * @return The Background.
     */
    public BackgroundColor background()
    {
        return this.background.getValue();
    }


    /**
     * Set the default cell background.
     * @param background The background.
     */
    public void setBackground(BackgroundColor background)
    {
        this.background.setValue(background);
    }


    // ** Style
    // --------------------------------------------------------------------------------------

    /**
     * The default column style.
     * @return The style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the default column style.
     * @param style The style.
     */
    public void setStyle(TextStyle style)
    {
        this.style.setValue(style);
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


    // ** False Style
    // --------------------------------------------------------------------------------------

    /**
     * The default column false valuestyle.
     * @return The style.
     */
    public TextStyle falseStyle()
    {
        return this.falseStyle.getValue();
    }


    /**
     * Set the default column false valuestyle.
     * @param style The style.
     */
    public void setFalseStyle(TextStyle style)
    {
        this.falseStyle.setValue(style);
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
        this.showTrueIcon.setValue(showTrueIcon);
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
        this.showFalseIcon.setValue(showFalseIcon);
    }

}
