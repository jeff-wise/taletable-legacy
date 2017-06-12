
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Text Format
 */
data class TextFormat(override val id : UUID,
                      val style : Comp<TextStyle>,
                      val position : Prim<Position>,
                      val height : Prim<Height>,
                      val padding : Comp<Spacing>,
                      val margins : Comp<Spacing>,
                      val alignment: Prim<Alignment>,
                      val verticalAlignment: Prim<VerticalAlignment>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(style : TextStyle,
                position : Position,
                height : Height,
                padding : Spacing,
                margins : Spacing,
                alignment : Alignment,
                verticalAlignment : VerticalAlignment)
        : this(UUID.randomUUID(),
               Comp(style),
               Prim(position),
               Prim(height),
               Comp(padding),
               Comp(margins),
               Prim(alignment),
               Prim(verticalAlignment))


    companion object : Factory<TextFormat>
    {

        private val defaultStyle             = TextStyle.default()
        private val defaultPosition          = Position.Top()
        private val defaultHeight            = Height.Wrap()
        private val defaultPadding           = Spacing.default()
        private val defaultMargins           = Spacing.default()
        private val defaultAlignment         = Alignment.Center()
        private val defaultVerticalAlignment = VerticalAlignment.Middle()

        override fun fromDocument(doc : SpecDoc) : ValueParser<TextFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextFormat,
                         // Style
                         split(doc.maybeAt("style"),
                               effValue(defaultStyle),
                               { TextStyle.fromDocument(it) }),
                         // Position
                         split(doc.maybeAt("position"),
                               effValue<ValueError,Position>(defaultPosition),
                               { Position.fromDocument(it) }),
                         // Height
                         split(doc.maybeAt("height"),
                               effValue<ValueError,Height>(defaultHeight),
                               { Height.fromDocument(it) }),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(defaultPadding),
                               { Spacing.fromDocument(it) }),
                         // Margins
                         split(doc.maybeAt("margins"),
                               effValue(defaultMargins),
                               { Spacing.fromDocument(it) }),
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Alignment>(defaultAlignment),
                               { Alignment.fromDocument(it) }),
                         // Vertical Alignment
                         split(doc.maybeAt("vertical_alignment"),
                               effValue<ValueError,VerticalAlignment>(defaultVerticalAlignment),
                               { VerticalAlignment.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : TextFormat =
                TextFormat(defaultStyle,
                           defaultPosition,
                           defaultHeight,
                           defaultPadding,
                           defaultMargins,
                           defaultAlignment,
                           defaultVerticalAlignment)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}


/**
 * Text Style
 */
data class TextStyle(override val id : UUID,
                     val colorTheme : Prim<ColorTheme>,
                     val size : Prim<TextSize>,
                     val font : Prim<TextFont>,
                     val isUnderlined : Prim<IsUnderlined>,
                     val alignment: Prim<Alignment>,
                     val backgroundColorTheme : Prim<ColorTheme>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme,
                size : TextSize,
                font : TextFont,
                isUnderlined: IsUnderlined,
                alignment : Alignment,
                backgroundColorTheme: ColorTheme)
        : this(UUID.randomUUID(),
               Prim(colorTheme),
               Prim(size),
               Prim(font),
               Prim(isUnderlined),
               Prim(alignment),
               Prim(backgroundColorTheme))


    companion object : Factory<TextStyle>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextStyle> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextStyle,
                         // Color Theme
                         split(doc.maybeAt("color_theme"),
                               effValue(ColorTheme.black),
                               { ColorTheme.fromDocument(it) }),
                         // Size
                         split(doc.maybeAt("size"),
                               effValue(TextSize(5.0)),
                               { TextSize.fromDocument(it) }),
                         // Font
                         split(doc.maybeAt("font"),
                                effValue<ValueError,TextFont>(TextFont.Regular()),
                                { TextFont.fromDocument(it) }),
                         // Is Underlined?
                         split(doc.maybeAt("is_underlined"),
                               effValue(IsUnderlined(false)),
                               { IsUnderlined.fromDocument(it) }),
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Alignment>(Alignment.Center()),
                               { Alignment.fromDocument(it) }),
                         // Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(ColorTheme.transparent),
                               { ColorTheme.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : TextStyle =
                TextStyle(ColorTheme.black,
                          TextSize(5.0),
                          TextFont.Regular(),
                          IsUnderlined(false),
                          Alignment.Center(),
                          ColorTheme.transparent)

    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}


/**
 * Text Size
 */
data class TextSize(val dp : Double)
{

    companion object : Factory<TextSize>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<TextSize> = when (doc)
        {
            is DocNumber -> effValue(TextSize(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }
}


/**
 * Is Underlined
 */
data class IsUnderlined(val value : Boolean)
{

    companion object : Factory<IsUnderlined>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<IsUnderlined> = when (doc)
        {
            is DocBoolean -> effValue(IsUnderlined(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }
}


/**
 * Text Font
 */
sealed class TextFont
{

    class Regular : TextFont()
    class Bold : TextFont()
    class Italic : TextFont()
    class BoldItalic : TextFont()


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<TextFont> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "regular"     -> effValue<ValueError,TextFont>(TextFont.Regular())
                "bold"        -> effValue<ValueError,TextFont>(TextFont.Bold())
                "italic"      -> effValue<ValueError,TextFont>(TextFont.Italic())
                "bold_italic" -> effValue<ValueError,TextFont>(TextFont.BoldItalic())
                else          -> effError<ValueError,TextFont>(
                                    UnexpectedValue("TextFont", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}




//
//    public float size()
//    {
//        switch (this)
//        {
//            case SUPER_SMALL:
//                return 3f;
//            case VERY_SMALL:
//                return 3.3f;
//            case SMALL:
//                return 3.7f;
//            case MEDIUM_SMALL:
//                return 4.2f;
//            case MEDIUM:
//                return 4.6f;
//            case MEDIUM_LARGE:
//                return 5f;
//            case LARGE:
//                return 6.2f;
//            case VERY_LARGE:
//                return 7.5f;
//            case HUGE:
//                return 9f;
//            case GARGANTUAN:
//                return 11;
//            case COLOSSAL:
//                return 13f;
//            default:
//                return 4.2f;
//        }
//    }

//
//    public Integer resourceId()
//    {
//        switch (this)
//        {
//            case THEME_VERY_LIGHT:
//                return R.color.dark_blue_hlx_5;
//            case THEME_LIGHT:
//                return R.color.dark_blue_hlx_7;
//            case THEME_MEDIUM_LIGHT:
//                return R.color.dark_blue_hlx_9;
//            case THEME_MEDIUM:
//                return R.color.dark_blue_hl_2;
//            case THEME_MEDIUM_DARK:
//                return R.color.dark_blue_hl_4;
//            case THEME_DARK:
//                return R.color.dark_blue_hl_6;
//            case THEME_VERY_DARK:
//                return R.color.dark_blue_hl_8;
//            case THEME_SUPER_DARK:
//                return R.color.dark_blue_1;
//            case THEME_BACKGROUND_LIGHT:
//                return R.color.dark_blue_5;
//            case THEME_BACKGROUND_MEDIUM_LIGHT:
//                return R.color.dark_blue_6;
//            case THEME_BACKGROUND_MEDIUM:
//                return R.color.dark_blue_7;
//            case THEME_BACKGROUND_MEDIUM_DARK:
//                return R.color.dark_blue_8;
//            case THEME_BACKGROUND_DARK:
//                return R.color.dark_blue_9;
//            case GOLD_VERY_LIGHT:
//                return R.color.gold_very_light;
//            case GOLD_LIGHT:
//                return R.color.gold_light;
//            case GOLD_MEDIUM_LIGHT:
//                return R.color.gold_medium_light;
//            case GOLD_MEDIUM:
//                return R.color.gold_medium;
//            case GOLD_MEDIUM_DARK:
//                return R.color.gold_medium_dark;
//            case GOLD_DARK:
//                return R.color.gold_dark;
//            case GOLD_VERY_DARK:
//                return R.color.gold_very_dark;
//            case PURPLE:
//                return R.color.purple_light;
//            case PURPLE_VERY_LIGHT:
//                return R.color.purple_very_light;
//            case PURPLE_LIGHT:
//                return R.color.purple_light;
//            case PURPLE_MEDIUM_LIGHT:
//                return R.color.purple_medium_light;
//            case PURPLE_MEDIUM:
//                return R.color.purple_medium;
//            case PURPLE_MEDIUM_DARK:
//                return R.color.purple_medium_dark;
//            case RED_LIGHT:
//                return R.color.red_light;
//            case RED_ORANGE_LIGHT:
//                return R.color.red_orange_light;
//            case ORANGE_LIGHT:
//                return R.color.orange_light;
//            case BLUE_LIGHT:
//                return R.color.blue_light;
//            case GREEN_VERY_LIGHT:
//                return R.color.green_very_light;
//            case GREEN_LIGHT:
//                return R.color.green_light;
//            case GREEN_MEDIUM_LIGHT:
//                return R.color.green_medium_light;
//            default:
//                return R.color.dark_blue_hl_5;
//        }
//
//    }

//
//public class TextStyle extends Model
//                       implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                                id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<TextColor>         color;
//    private PrimitiveFunctor<TextSize>          size;
//    private PrimitiveFunctor<TextFont>          font;
//    private primitivefunctor<boolean>           isunderlined;
//    private primitivefunctor<alignment>         alignment;
//    private primitivefunctor<backgroundcolor>   backgroundcolor;
//
//
//    // constructors
//    // -----------------------------------------------------------------------------------------
//
//    public textstyle()
//    {
//        this.id                 = null;
//
//        this.color              = new primitivefunctor<>(null, textcolor.class);
//        this.size               = new primitivefunctor<>(null, textsize.class);
//        this.font               = new primitivefunctor<>(null, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(null, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     textfont font,
//                     boolean isunderlined,
//                     alignment alignment,
//                     backgroundcolor backgroundcolor)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(font, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(isunderlined, boolean.class);
//        this.alignment          = new primitivefunctor<>(alignment, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(backgroundcolor, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(font);
//        this.setisunderlined(isunderlined);
//        this.setalignment(alignment);
//        this.setbackgroundcolor(backgroundcolor);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(null, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(null, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(null);
//        this.setisunderlined(null);
//        this.setalignment(null);
//        this.setbackgroundcolor(null);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     alignment alignment)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(null, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(alignment, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(null);
//        this.setisunderlined(null);
//        this.setalignment(alignment);
//        this.setbackgroundcolor(null);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     textfont font)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(font, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(null, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(font);
//        this.setisunderlined(null);
//        this.setalignment(null);
//        this.setbackgroundcolor(null);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     textfont font,
//                     alignment alignment)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(font, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(alignment, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(font);
//        this.setisunderlined(null);
//        this.setalignment(alignment);
//        this.setbackgroundcolor(null);
//    }
//
//
//    /**
//     * create a widget text style from its yaml representation. if the yaml is null, return a
//     * default text style.
//     * @param yaml the yaml parser.
//     * @return the parsed text style.
//     * @throws yamlparseexception
//     */
//    public static textstyle fromyaml(yamlparser yaml)
//                  throws yamlparseexception
//    {
//        return textstyle.fromyaml(yaml, false);
//    }
//
//
//    /**
//     * create a text style from its yaml representation. if the yaml is null, and usedefault is
//     * null, returns null.
//     * @param yaml the yaml parser.
//     * @param usedefault if true, return a default when the yaml is null.
//     * @return the parsed text style.
//     * @throws yamlparseexception
//     */
//    public static textstyle fromyaml(yamlparser yaml, boolean usedefault)
//                  throws yamlparseexception
//    {
//        if (yaml.isnull() && usedefault)
//            return textstyle.asdefault();
//        else if (yaml.isnull())
//            return null;
//
//        uuid            id           = uuid.randomuuid();
//
//        textcolor       color        = textcolor.fromyaml(yaml.atmaybekey("color"));
//        textsize        size         = textsize.fromyaml(yaml.atmaybekey("size"));
//        textfont        font         = textfont.fromyaml(yaml.atmaybekey("font"));
//        boolean         isunderlined = yaml.atmaybekey("is_underlined").getboolean();
//        alignment       alignment    = alignment.fromyaml(yaml.atmaybekey("alignment"));
//        backgroundcolor bgcolor      = backgroundcolor.fromyaml(
//                                                yaml.atmaybekey("background_color"));
//
//        return new textstyle(id, color, size, font, isunderlined, alignment, bgcolor);
//    }
//
//
//    public static textstyle asdefault()
//    {
//        textstyle style = new textstyle();
//
//        style.setid(uuid.randomuuid());
//        style.setcolor(null);
//        style.setsize(null);
//        style.setfont(null);
//        style.setisunderlined(null);
//        style.setalignment(null);
//        style.setbackgroundcolor(null);
//
//        return style;
//    }
//
//
//    // api
//    // -----------------------------------------------------------------------------------------
//
//    // > model
//    // --------------------------------------------------------------------------------------
//
//    // ** id
//    // --------------------------------------------------------------------------------------
//
//    public uuid getid()
//    {
//        return this.id;
//    }
//
//
//    public void setid(uuid id)
//    {
//        this.id = id;
//    }
//
//
//    // ** on load
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * called when the text widget format is completely loaded.
//     */
//    public void onload() { }
//
//
//    // > to yaml
//    // -----------------------------------------------------------------------------------------
//
//    public yamlbuilder toyaml()
//    {
//        return yamlbuilder.map()
//                .putyaml("color", this.color())
//                .putyaml("size", this.size())
//                .putyaml("font", this.font())
//                .putboolean("is_underlined", this.isunderlined())
//                .putyaml("alignment", this.alignment())
//                .putyaml("background_color", this.backgroundcolor());
//    }
//
//
//    // > state
//    // -----------------------------------------------------------------------------------------
//
//    // ** color
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the text color.
//     * @return the text color.
//     */
//    public textcolor color()
//    {
//        return this.color.getvalue();
//    }
//
//
//    public void setcolor(textcolor color)
//    {
//        if (color != null)
//            this.color.setvalue(color);
//        else
//            this.color.setvalue(textcolor.theme_medium);
//    }
//
//
//    // ** size
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the text size.
//     * @return the size.
//     */
//    public textsize size()
//    {
//        return this.size.getvalue();
//    }
//
//
//    /**
//     * set the text size. if null, defaults to medium.
//     * @param size the text size.
//     */
//    public void setsize(textsize size)
//    {
//        if (size != null)
//            this.size.setvalue(size);
//        else
//            this.size.setvalue(textsize.medium);
//    }
//
//
//    // ** is underlined
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * true if the text should be underlined.
//     * @return is underlined?
//     */
//    public boolean isunderlined()
//    {
//        return this.isunderlined.getvalue();
//    }
//
//
//    /**
//     * set the text to be underlined or not underlined. defaults to non underlined if null.
//     * @param isunderlined is underlined?
//     */
//    public void setisunderlined(boolean isunderlined)
//    {
//        if (isunderlined != null)
//            this.isunderlined.setvalue(isunderlined);
//        else
//            this.isunderlined.setvalue(false);
//    }
//
//
//    // ** font
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the font.
//     * @return the font.
//     */
//    public textfont font()
//    {
//        return this.font.getvalue();
//    }
//
//
//    /**
//     * set the text font
//     * @param font the font.
//     */
//    public void setfont(textfont font)
//    {
//        if (font != null)
//            this.font.setvalue(font);
//        else
//            this.font.setvalue(textfont.regular);
//    }
//
//
//    // ** alignment
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the alignment.
//     * @return the alignment.
//     */
//    public alignment alignment()
//    {
//        return this.alignment.getvalue();
//    }
//
//
//    /**
//     * set the alignment. if null, defaults to left.
//     * @param alignment the alignment.
//     */
//    public void setalignment(alignment alignment)
//    {
//        if (alignment != null)
//            this.alignment.setvalue(alignment);
//        else
//            this.alignment.setvalue(alignment.left);
//    }
//
//
//    // ** background color
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the background color.
//     * @return the background color.
//     */
//    public backgroundcolor backgroundcolor()
//    {
//        return this.backgroundcolor.getvalue();
//    }
//
//
//    /**
//     * set the background color. if null, defaults to medium.
//     * @param backgroundcolor the background color.
//     */
//    public void setbackgroundcolor(backgroundcolor backgroundcolor)
//    {
//        if (backgroundcolor != null)
//            this.backgroundcolor.setvalue(backgroundcolor);
//        else
//            this.backgroundcolor.setvalue(backgroundcolor.medium);
//    }
//
//
//    // > typeface
//    // -----------------------------------------------------------------------------------------
//
//    public typeface typeface(context context)
//    {
//        switch (this.font())
//        {
//            case regular:
//                return font.seriffontregular(context);
//            case bold:
//                return font.seriffontbold(context);
//            case italic:
//                return font.seriffontitalic(context);
//            case bold_italic:
//                return font.seriffontbolditalic(context);
//            default:
//                return font.seriffontregular(context);
//        }
//    }
//
//
//    // > style text view
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * format a text view with this style.
//     * @param textview the text view.
//     */
//    public void styletextview(textview textview, context context)
//    {
//        // ** color
//        textview.settextcolor(contextcompat.getcolor(context, this.color().resourceid()));
//
//        // ** size
//        textview.settextsize(context.getresources().getdimension(this.size().resourceid()));
//
//        // ** font
//        textview.settypeface(this.typeface(context));
//    }
//
//
//    // > style text view builder
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * set the text view builder style options to match this style.
//     * @param viewbuilder the text view builder.
//     */
//    public void styletextviewbuilder(textviewbuilder viewbuilder, context context)
//    {
//        // ** color
//        viewbuilder.color   = this.color().resourceid();
//
//        // ** size
//        viewbuilder.size    = this.size().resourceid();
//
//        // ** fton
//        viewbuilder.font    = this.typeface(context);
//    }
//
//}
